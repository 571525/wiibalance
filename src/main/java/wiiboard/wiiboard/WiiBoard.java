
/*
	Copyright 2008 Nedim Jackman
	
 	This file is part of Wiiboard Simple

    Wiiboard Simple is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Wiiboard Simple is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Wiiboard Simple.  If not, see <http://www.gnu.org/licenses/>.
 */

package wiiboard.wiiboard;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.bluetooth.L2CAPConnection;
import javax.microedition.io.Connector;

import wiiboard.bluetooth.BluetoothDevice;
import wiiboard.wiiboard.event.*;
import wiiboard.wiiboard.event.*;

/**
 * Represents a Wii Balance Board that has been connected to.
 * To use a WiiBoard, initiate a search through the WiiBoardDiscoverer
 * and it will return a connected board if it finds one.
 * Register the returned board with a WiiBoardListener to receive events.
 */
public class WiiBoard implements BluetoothDevice {
	
    /*
     * Note to developers:
     * The Wii Balance Board is very similar in most hardware properties of the wiimote.
     * The force sensing component is treated as an extension to a wiimote.
     *
     * Most of the implementation details in this class are sourced from the wiibrew project
     * Please read http://wiibrew.org/wiki/Wiimote and 
     * http://wiibrew.org/wiki/Wii_Balance_Board for more information.
     * More wiimote info from http://www.wiili.org/index.php/Wiimote
     */
	
	private String address;
	
	private List<WiiBoardListener> listeners;
	
	//Keep track of the discoverer which found us, so that we can remove ourself from its
	//list of found devices once disconnected
	private WiiBoardDiscoverer whoFoundMe;
	
	private L2CAPConnection sendCon;
	private L2CAPConnection receiveCon;
	private boolean connectionOpen;
	private boolean light;
	private WiiBoard wiiboard;
	private WiiBoardButtonEvent lastButtonEvent;
    private boolean calibrationRequested;
    
    final private static byte CONTINUOUS_REPORTING = 0x04;
    
    final private static byte COMMAND_LIGHT = 0x11;
	final private static byte COMMAND_REPORTING = 0x12;
	final private static byte COMMAND_REQUEST_STATUS = 0x15;
	final private static byte COMMAND_REGISTER = 0x16;
	final private static byte COMMAND_READ_REGISTER = 0x17;   
    
    //input is Wii device to host
	private static final byte INPUT_STATUS = 0x20;
	private static final byte INPUT_READ_DATA = 0x21;
 
    final private static byte EXTENSION_8BYTES = 0x32;
    //final private static  byte EXTENSION_19BYTES = 0x34; //Not used, but available

    //order of calibration {top right, bottom right, top left, bottom left}
    final private static int TOP_RIGHT = 0, BOTTOM_RIGHT = 1, TOP_LEFT = 2, BOTTOM_LEFT = 3;
    
    //wiiboard calibration comes with information about 0KG, 17KG and 34KG for every sensor
	private double[] calibration0, calibration17, calibration34;
	private Thread commandListener;
		
	/**
	 * Creates a WiiBoard instance given an address. Forms connections to the wiiboard, and
	 * readies sending and receiving of data. You likely won't call this yourself. Instead,
	 * your WiiBoard instances will be created by WiiBoardDiscoverer
	 */
	public WiiBoard(String a, WiiBoardDiscoverer disc) throws Exception {
		this(a);
		whoFoundMe = disc;
	}
	
	public WiiBoard(String a) throws Exception {
		connectionOpen = false;
		calibration0 = new double[4];
		calibration17 = new double[4];
		calibration34 = new double[4];
		lastButtonEvent = null;
		calibrationRequested = false;
		address = a;
		listeners = new Vector<WiiBoardListener>();
		wiiboard = this;
		String connect_address = "btl2cap://"+a;
		
		try {
			//these sometimes take very long to open, at least for me. This is the primary
			//reason that we need to fail elegantly should they not open.
			receiveCon = (L2CAPConnection)Connector.open(connect_address+":13", Connector.READ, true);
			sendCon = (L2CAPConnection)Connector.open(connect_address+":11", Connector.WRITE, true);
			connectionOpen = true;
		}
		catch (Exception e) {
			if (sendCon!=null)
				sendCon.close();
			if (receiveCon!=null)
				receiveCon.close();
			System.out.println("Could not open. Connections reset.");
			throw e;
		}
		commandListener = new CommandListener();
		commandListener.start();
		connectToExtension();
		readCalibration();
	}

	/**
	 * Attaches a WiiBoardListener to this wiiboard. The wiiboard will send events to the
	 * given listener whenever something happens (e.g. button is pressed).
	 * Mass events will be sent continuously to the listener.
	 */
	public void addListener(WiiBoardListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}
	
	/**
	 * Detaches the given WiiBoardListener.
	 */
	public void removeListener(WiiBoardListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Dispatches wiiboard events to all the WiiBoardListeners.
	 */
	protected void dispatchEvent(WiiBoardEvent e) {
		
		Iterator<WiiBoardListener> iter = listeners.iterator();
		while(iter.hasNext()){
			WiiBoardListener listener = null;
			try {
				listener = iter.next();
			} catch(Exception ex){
				continue;
			}
			if (e instanceof WiiBoardButtonEvent)
				listener.wiiBoardButtonEvent((WiiBoardButtonEvent)e);
			if (e instanceof WiiBoardMassEvent)
				listener.wiiBoardMassReceived((WiiBoardMassEvent)e);
			if (e instanceof WiiBoardStatusEvent)
				listener.wiiBoardStatusReceived((WiiBoardStatusEvent)e);			
		}

	}
	    	
	/**
	 * Controls the LED power button light.
	 * @param on if true, the light turns on, false and light goes off
	 */
	public void setLight(boolean on) {
		light = on;
		byte[] lightBytes = new byte[] {0}; //Light in fat, not taste!
		if(on) {
			lightBytes[0] = 0x10;
		}
		
		sendCommand(COMMAND_LIGHT, lightBytes);
	}
	
	/**
	 * Sends a request to the wii board to report its status.
	 * The status is then returned as a WiiBoardStatusEvent that is dispatched
	 * to all listeners
	 */
	public void requestStatus(){
		sendCommand(COMMAND_REQUEST_STATUS,new byte[]{0});
	}
	
	/**
	 * Returns whether the power button LED is on.
	 * @return <b>true</b> if the power button LED is lit, <b>false</b otherwise
	 */
	public boolean isLEDon() {
		return light;
	}

	
	/**
	 * Causes the given data to be written to the given register address on the wiiboard
	 */
	private void writeToRegister(int address, byte[] data) {
		byte[] message = new byte[21];
		
		message[0] = 0x04;
		
		message[1] = (byte)(address >> 16 & 0xff);
    	message[2] = (byte)(address >> 8 & 0xff);
    	message[3] = (byte)(address & 0xff);    	
    	
		message[4] = (byte)data.length;
		for (int i=0; i<data.length; i++) {
			message[5+i] = data[i];
		}

		sendCommand(COMMAND_REGISTER, message);
	}
	
	/**
	 * Sends message to the wiiboard asking for mass calibration data.
	 */
	synchronized private void readCalibration() {
		calibrationRequested = true;
		readFromRegister(0xa40024,24);

	}

	/**
	 * Sends a generic command to the wiiboard.
	 */
	synchronized private void sendCommand(byte command, byte[] payload) {
		try {
			byte[] message = new byte[2+payload.length];
			message[0] = 82;  
			message[1] = command;
			System.arraycopy(payload, 0, message, 2, payload.length);
						
			sendCon.send(message);
		}
		catch(IOException ioexception) {
			System.out.println("error sending data " + ioexception);
		}
	}
	
	/**
	 * Causes the wiiboard connections to close.
	 */
	protected void finalize() throws Throwable {
		cleanup();
	}
	
	/**
	 * Closes any open wiiboard connections.
	 */
	public void cleanup() {
		synchronized (receiveCon) { 
			connectionOpen = false;
			for(WiiBoardListener l : listeners){
				l.wiiBoardDisconnected();
			}
			try {
				if (sendCon!=null) {
					sendCon.close();
				}
				if (receiveCon!=null) {
					receiveCon.close();
				}
			} catch (Exception e) {
				System.out.println("cleanup exception " + e);
			}	
			if(whoFoundMe != null) {
			   //remove this board from the list of connected boards in the discoverer
			   whoFoundMe.bluetoothDeviceDisconnected(this);
			   whoFoundMe = null;
			}
		}
	}
	
	/**
	 * Connects to the "balance" extension, so able to read mass data.
	 */
	private void connectToExtension(){
		 //"Magic" addresses obtained from sources listed at the top
		 writeToRegister(0x04a40040,new byte[]{0});
		 
		 //Read 2 bytes from 0xa400fe to get extension type
		 //0x0402 means balance board
		 //Not implemented
		 
		 //Enable continuous reporting - in the case of the board,
		 //there is little distinction between continuous and non continuous
		 setReporting(CONTINUOUS_REPORTING);
	}
	
	/**
	 * Sets the reporting type the wii board will use.
	 * Must be called every time a status event is received.
	 * @param reportingType either continuous or event based reporting
	 */
	private void setReporting(byte reportingType) {
		//Fix the input report to the EXTENSION_8BYTES mode, most suitable for the board
		sendCommand(COMMAND_REPORTING, new byte[]{reportingType, EXTENSION_8BYTES});
	}
	
	/**
	 * Requests register data at location address to address+numBytes.
	 * Data is returned through an INPUT_READ_DATA report
	 */
	private void readFromRegister(int address, int numBytes){
		byte[] byte_address = new byte[4];
		byte_address[0] = 0x04;
		byte_address[1] = (byte)(address >> 16 & 0xff);
		byte_address[2] = (byte)(address >> 8 & 0xff);
		byte_address[3] = (byte)(address & 0xff);    
		
		byte[] message = new byte[6];
		for(int i = 0; i < 4; ++i){
			message[i] = byte_address[i];
		}
		byte[] bytesToRead = intToByte(numBytes);
		message[4] = bytesToRead[2];
		message[5] = bytesToRead[3];
		System.out.println(message[4] +  "  " + message[5]);
		
		sendCommand(COMMAND_READ_REGISTER, message);
	}
	
	/**
	 * Generates a byte array out of the given number.
	 * The resulting array is big-endian
	 */
	private static byte[] intToByte(int num){
		byte[] array = new byte[4];
		array[0] = (byte)(num >> 24 & 0xff);
		array[1] = (byte)(num >> 16 & 0xff);
		array[2] = (byte)(num >> 8 & 0xff);
		array[3] = (byte)(num & 0xff);
		
		return array;
	}
	
	@Override
	public String getBluetoothAddress() {
		return address;
	}

	/**
	 * Returns true if the board is currently connected
	 */
	public boolean isConnected(){
		return connectionOpen;
	}
	
	/**
	 * This loops infinitely, constantly listening for data from the wiiboard. When
	 * data is received it responds accordingly.
	 */
	protected class CommandListener extends Thread {
		
		public void run() {
			long timer = System.currentTimeMillis();
			while (true) {
				if(connectionOpen == false) {
					
					break;
				}
				
				//set a timeout of 5 seconds. if no report is received within 
				//5 seconds, we assume the board has been disconnected
				//Note: enabling socket timeout isn't windows friendly
				if(System.currentTimeMillis() - timer > 5000 ) {
					System.out.println("Connection timed out - closing: " + wiiboard.getBluetoothAddress());
					cleanup();
					break;
				}
				byte[] bytes = new byte[32];
					if (connectionOpen==true) {
						try {
						    receiveCon.receive(bytes); //this blocks until data ready
							timer = System.currentTimeMillis();
							
						}
						catch (IOException e) {
							System.out.println("wiiboard " + receiveCon + " exception: receive(bytes) " + e);
						}
						
						switch (bytes[1]) {
						case INPUT_STATUS: //status event
							//Must set the reporting type after every status report received
							wiiboard.setReporting(CONTINUOUS_REPORTING);
							WiiBoardStatusEvent statusEvent = new WiiBoardStatusEvent(wiiboard, bytes);
							wiiboard.dispatchEvent(statusEvent);		
							break;
							
						case INPUT_READ_DATA: 
							if(calibrationRequested){
							   int packetLength = (bytes[4] & 0xf0)/16 + 1;
							   parseCalibrationResponse(ByteBuffer.allocate(packetLength).put(bytes, 7, packetLength),
									                    packetLength);
							
							   if(packetLength < 16) { 
							      calibrationRequested = false;
							   }
							}
							break;
							
						case EXTENSION_8BYTES: 
						    	createButtonEvent(ByteBuffer.allocate(2).put(bytes, 2, 2));
						    	createMassEvent(ByteBuffer.allocate(8).put(bytes,4,8));	
						    break;
						}
					} else {
						try {
							Thread.sleep(100);
						} catch (Exception e) {e.printStackTrace();}
					}
				}
		}

		protected void parseCalibrationResponse(ByteBuffer b, int length) {

			b.rewind();			
			if(length == 16){
				//first packet of calibration data
				for(int i = 0; i < 4; ++i){
				   calibration0[i] = (double)(((b.get() & 0xff) << 8) + (b.get() & 0xff));
				}
				for(int i = 0; i < 4; ++i){
				   calibration17[i] = (double)(((b.get() & 0xff) << 8) + (b.get() & 0xff));
				}				
			} else {
			    //second packet of calibration data
				for(int i = 0; i < 4; ++i){
			       calibration34[i] = (double)(((b.get() & 0xff) << 8) + (b.get() & 0xff));
				}	
			}
		}
		
		/**
		 * Creates a WiiBoardButtonEvent from raw data
		 */
		protected void createButtonEvent(ByteBuffer b) {			
			int i = (b.get(0) << 8) | b.get(1);
			WiiBoardButtonEvent event = new WiiBoardButtonEvent(wiiboard, i, lastButtonEvent);
			if (!event.equals(lastButtonEvent)) {
				lastButtonEvent = event;
				if(event.isPressed() || event.isReleased()) {		
				   wiiboard.dispatchEvent(event);
				}
			}
		}
				
		/**
		 * Creates a WiiBoardMassEvent from raw data
		 */
		protected void createMassEvent(ByteBuffer b) {
			double rawTR, rawBR, rawTL, rawBL;
			double topRight, bottomRight, topLeft, bottomLeft;
			
			b.rewind();
			rawTR = ((b.get() & 0xff) << 8) + (b.get() & 0xff);
			rawBR = ((b.get() & 0xff) << 8) + (b.get() & 0xff);
			rawTL = ((b.get() & 0xff) << 8) + (b.get() & 0xff);
			rawBL = ((b.get() & 0xff) << 8) + (b.get() & 0xff);
			
			topRight = calcMass(rawTR,TOP_RIGHT);
			topLeft = calcMass(rawTL,TOP_LEFT);
			bottomRight = calcMass(rawBR, BOTTOM_RIGHT);
			bottomLeft = calcMass(rawBL, BOTTOM_LEFT);
			
			WiiBoardMassEvent massEvent = 
				new WiiBoardMassEvent(wiiboard, topRight, topLeft, bottomRight, bottomLeft);
			
			if(massEvent.getTotalWeight() > 30 && 
			   (topRight == 0 || topLeft == 0 || bottomRight == 0 || bottomLeft == 0)){
				//This is a non-clean solution to prevent an occasional bug that occurs
				//when one plate is reportedly 0.
				//Suggestions for a better solution are welcome.
			} else {
				wiiboard.dispatchEvent(massEvent);
			}			
		}
		
		/**
		 * Calculates the Kilogram weight reading from raw data at position pos
		 */
		private double calcMass(double raw, int pos){
			double val = 0.00;
			if(raw < calibration0[pos]){
				//ignore the reading
			}
			else if(raw < calibration17[pos]) {
				val = 17 * ((raw - calibration0[pos]) / (calibration17[pos] - calibration0[pos]));
			} else if( raw > calibration17[pos]){
				val = 17 + 17 * ((raw - calibration17[pos]) / (calibration34[pos] - calibration17[pos]));
			}
			return val;
		}	
	}
}
