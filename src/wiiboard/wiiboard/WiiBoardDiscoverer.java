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

import java.util.LinkedList;
import java.util.ListIterator;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import wiiboard.bluetooth.BluetoothDevice;

/**
 * This class is used to search for WiiBoards.
 * When it finds a board, it attempts to connect to the board and notifies any listeners
 *
 */
public class WiiBoardDiscoverer implements DiscoveryListener {
	//stores previously found wiiboards so that we don't try to connect to the same one twice
	private static LinkedList<String> discoveredWiiBoardAddresses = new LinkedList<String>();
	private static WiiBoardDiscoverer discoverer;

	//stores the address of a newly found wiiboard
	private String discoveredAddress;
	private DiscoveryAgent agent;
	
	private Object lock;
	
	//stores the live connections so we can close them later
	private LinkedList<WiiBoardDiscoveryListener> listeners;
	private boolean isSearching;
	
	protected WiiBoardDiscoverer() {
		isSearching = false;
		listeners = new LinkedList<WiiBoardDiscoveryListener>();
		try {
			LocalDevice device = LocalDevice.getLocalDevice();
			agent = device.getDiscoveryAgent();
			lock = new Object();
		}
		catch (BluetoothStateException e) {
			System.out.println("BluetoothStateException. Exiting. " + e);
			System.exit(0);
		}
		catch (Exception e) {System.out.println("exception " + e);}
	}
	
	/**
	 * Call before attempting to use the discoverer at all to determine
	 * if there is any underlying bluetooth problems
	 * @return true if bluetooth is ready, false otherwise
	 */
	public static boolean isBluetoothReady(){
		try {
			LocalDevice.getLocalDevice();
		} catch (BluetoothStateException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds a WiiBoardDiscoveryListener to the list of listeners. This listener
	 * will be notified whenver a WiiBoard is discovered and connected to.
	 */
	public void addWiiBoardDiscoveryListener(WiiBoardDiscoveryListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}
	
	/**
	 * Removes the given WiiBoardDiscoveryListener from the list of listeners.
	 */
	public void removeWiiBoardDiscoveryListener(WiiBoardDiscoveryListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Returns an instance of WiiBoardDiscoverer. This is how you should get one for use.
	 */
	synchronized static public WiiBoardDiscoverer getWiiBoardDiscoverer() {
		if (discoverer==null) {
			discoverer = new WiiBoardDiscoverer();
		}
		
		return discoverer;
	}
	
	/**
	 * Notifies listeners that a wiiBoard has been discovered and connected to.
	 */
	protected void notifyListeners(WiiBoard wiiboard) {
		for (ListIterator<WiiBoardDiscoveryListener> it = listeners.listIterator(); it.hasNext(); ) {
			it.next().wiiBoardDiscovered(wiiboard);
		}
	}
	
	/**
	 * Advises the discoverer that a connected bluetooth device was disconnected
	 * and it may now connect to that device again
	 */
	public void bluetoothDeviceDisconnected(BluetoothDevice b){
		discoveredWiiBoardAddresses.remove(b.getBluetoothAddress());
	}
	
	/**
	 * Starts a search for WiiBoards. The search will continue until
	 * stopWiiBoardSearch is called.
	 */
	public void startWiiBoardSearch() {
		if (!isSearching) {
			isSearching = true;
			
			new Thread(){
				
				public void run(){
					System.out.println("WiiBoard Discovery Started");
					WiiBoard wiiboard = null;
					try {
						while (isSearching) {
							do {
								System.out.println("Press the red sync button on your board now");
								agent.startInquiry(DiscoveryAgent.GIAC, discoverer);
								synchronized (lock) {
									lock.wait();
								}
							} while (discoveredAddress==null && isSearching);
							try {
								if (discoveredAddress!=null) {
									wiiboard = new WiiBoard(discoveredAddress,discoverer);
									System.out.println("Connected.");
									notifyListeners(wiiboard);
									wiiboard = null;
									discoveredWiiBoardAddresses.add(discoveredAddress);
									discoveredAddress = null;
								}
							} catch (Exception e) {
					    		discoveredAddress = null;
					    		discoveredWiiBoardAddresses.remove(discoveredAddress);
					    		System.out.println("Connection failed. Try again. ");
					    		e.printStackTrace();
							}
						}
					}
					catch (InterruptedException e) {}
					catch (BluetoothStateException bse) {
						System.err.println("Error Starting WiiBoard Discovery");
						bse.printStackTrace();
					}
					finally {
						if (wiiboard!=null) {
							wiiboard.cleanup();
						}
					}
					System.out.println("WiiBoard Discovery Stopped");
				}
				
			}.start();
		}
	}
	
	/**
	 * Returns true if the discoverer is currently searching for a wii board.
	 * Only one search may be running at any one time.
	 * If isSearching returns true, any calls to StartWiiBoardSearch are ignored
	 */
	public boolean isSearching(){
		return isSearching;
	}
	
	/**
	 * Stops a search for WiiBoards. Call this if you are done connecting with wiiBoards.
	 */
	public void stopWiiBoardSearch() {
		isSearching = false;
	}
	
	/**
	 * Is called by a JSR-82 implementation when a Bluetooth inquiry is complete. This occurs either after a WiiBoard
	 * has been found, or after the Bluetooth implementation has given up. It should
	 * not be necessary to call this.
	 */
	public void inquiryCompleted(int i) {
		//findWiiBoard function is waiting for us. Let's let it go
		synchronized(lock) {
			lock.notify();
		}
	}
	
	/**
	 * Is called by a JSR-82 implementation when a device has been discovered. It should
	 * not be necessary to call this.
	 */
	public void deviceDiscovered(RemoteDevice remotedevice, DeviceClass deviceclass) {
		String name = null;
		try {
			name = remotedevice.getFriendlyName(true);
		
			System.out.print("Discovered " + name);
		}
		catch (Exception e) {System.out.println(e);}
		
		//if this isn't named correctly then it isn't a wiiboard.
		//we will return and wait until we find a wiiboard
		if (!name.equals("Nintendo RVL-WBC-01")) {
			System.out.println();
			return;
		}
		
		//it is a wiiboard, so we will get it's address
		String address = remotedevice.getBluetoothAddress();
		System.out.print(" " + address+". ");
		
		//check to see if we found this wiiboard previously. return if we did. we want a new one.
		if (discoveredWiiBoardAddresses.contains(address)) {
			System.out.println("Already connected.");
			return;
		}
		
		isSearching = false;
		
		discoveredAddress = address;
		//cancel inquiry so we can go to the next step of creating the connections
		agent.cancelInquiry(this);
	}
	
	/**
	 * Not implemented
	 */
	public void servicesDiscovered(int i, ServiceRecord a[]) {}
	
	/**
	 * Not implemented
	 */
	public void serviceSearchCompleted(int i, int j) {}	
	
}
