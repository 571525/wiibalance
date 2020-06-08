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

package com.balance.wiiboard.wiiboardStack.event;

import com.balance.wiiboard.wiiboardStack.WiiBoard;

/**
 * A button event contains information about the state of the only button
 * on a wii board - the "power" button.
 */
public class WiiBoardButtonEvent extends WiiBoardEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8310554305692835762L;
	/*
	 * Mask information from http://wiibrew.org/wiki/Wiimote
	 */
	private static final int BUTTON_DOWN_MASK = 8;
	private boolean isPressed = false;
	private boolean isReleased = false; 
	
	/**
	 * Constructs a WiiBoardButtonEvent. 
	 * Created automatically when input is read from the board
	 */
	public WiiBoardButtonEvent(WiiBoard wiiboard, int buttonState, WiiBoardButtonEvent lastEvent) {
		super(wiiboard);
		if(buttonState == BUTTON_DOWN_MASK){
			isPressed = true;
		}
		if(lastEvent!= null){
		   if(lastEvent.isPressed && !isPressed){
			  isReleased = true;
		   }
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		WiiBoardButtonEvent other = (WiiBoardButtonEvent)o;
		return (other.isPressed == isPressed && other.isReleased == isReleased);
	}
	
	/**
	 * Returns true if the button is currently pushed down
	 */
	public boolean isPressed(){
		return isPressed;
	}
	
	/**
	 * Determines if the button is released after being pressed down.
	 * Note: If the button is in the pressed down state when the board is connecting,
	 * the first button event that is expected to report a release might not.
	 * 
	 * @return true if the button was pressed down and now is not pressed down
	 */
	public boolean isReleased(){
		return isReleased;
	}

}
