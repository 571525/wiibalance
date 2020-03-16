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


package wiiboard.wiiboardStack;

/**
 * Implement this interface to be notified of WiiBoards that are connected to. Register your
 * listener with an instance of WiiBoardDiscoverer.
 */
public interface WiiBoardDiscoveryListener {

	/**
	 * Is called by a WiiBoardDiscoverer when a WiiBoard has been found and successfully connected to.
	 */
	public void wiiBoardDiscovered(WiiBoard wiiboard);
}
