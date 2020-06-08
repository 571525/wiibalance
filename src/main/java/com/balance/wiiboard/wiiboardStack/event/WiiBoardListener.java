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

/**
 * Implement this and register with a WiiBoard instance in order to receive updates from
 * the wiiboardStack
 */
public interface WiiBoardListener {

	
	/**
	 * Fired when there is a change in the button state on the wii board
	 */
	public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent);
	
	/**
	 * Fired when the mass readings are read from the board.
	 * This will fire continuously.
	 */
	public void wiiBoardMassReceived(WiiBoardMassEvent massEvent);
	
	/**
	 * Fired when a status report is read from the board, usually the result
	 * of calling requestStatus() on a WiiBoard
	 */
	public void wiiBoardStatusReceived(WiiBoardStatusEvent status);
	
	/**
	 * Fired when the board connection is broken, either when it is disconnected
	 * by the user or some other connection troubles (e.g. timeout)
	 */
	public void wiiBoardDisconnected();
	
}
