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

package wiiboard.bluetooth;

/**
 * In very basic structure, all bluetooth devices have an address that the connection
 * is associated with.
 */
public interface BluetoothDevice {

	/**
	 * The fixed address of the device.
	 * Constant throughout the connection of the device.
	 */
	public String getBluetoothAddress();
		
}
