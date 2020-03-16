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

package wiiboard.wiiboard.event;

import wiiboard.wiiboard.WiiBoard;

/**
 * A wiiboard status event contains information about the state of the power button
 * LED and the battery life
 *
 */
public class WiiBoardStatusEvent extends WiiBoardEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6788674411395684603L;
	private double battery;
	private boolean lightState;
	
	private static final byte LED1_MASK = 0x10;
	private static final double BATTERY_MAX = 200;
	
	public WiiBoardStatusEvent(WiiBoard wiiboard, byte[] bytes) {
		super(wiiboard);
	    //(a1) 20 00 00 FF 00 00 BB
		//The board will only have two masks for FF: extension connected and LED1
		//Ignore the extension connected mask as the board is always connected
		
	    battery = (bytes[7] & 0xff) / BATTERY_MAX;
		lightState = (bytes[4] & LED1_MASK) == LED1_MASK ;
	   
	}
	
	/**
	 * Indicates a rough estimate of the battery life in the wii board on a scale of 0 to 1
	 */
	public double batteryLife(){
		return battery;
	}
	
	/**
	 * Indicates if the power button LED is on
	 */
	public boolean isLEDon(){
		return lightState;
	}
}
