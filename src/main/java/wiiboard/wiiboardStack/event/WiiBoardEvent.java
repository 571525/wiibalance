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

package wiiboard.wiiboardStack.event;
import java.util.EventObject;

import wiiboard.wiiboardStack.WiiBoard;

/**
 * Base class for all wiiboardStack event classes.
 */

public class WiiBoardEvent extends EventObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7883303025271326409L;
	protected WiiBoard wiiboard;
		
	protected WiiBoardEvent(WiiBoard w) {
		super(w);
		wiiboard = w;
	}
		
	/**
	 * Returns the wii board that generated this event
	 */
	public WiiBoard getWiiBoard() {
		return wiiboard;
	}

}
