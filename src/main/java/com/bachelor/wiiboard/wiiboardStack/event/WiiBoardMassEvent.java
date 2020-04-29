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

package com.bachelor.wiiboard.wiiboardStack.event;

import com.bachelor.wiiboard.wiiboardStack.WiiBoard;

/**
 * A Mass event contains information about the force sensors on the wii board.
 * To ensure the right sensors are being read, 
 * the orientation of the board should be so that the power button is facing the user:
 * <pre>
 *  __________________________
 * | TopLeft      TopRight    |
 * |                          |
 * | BottomLeft   BottomRight |
 *  ---------POWER------------
 * </pre>
 *
 */
public class WiiBoardMassEvent extends WiiBoardEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4318793163049895672L;
	private double topRight, topLeft, bottomRight, bottomLeft;
	
	public WiiBoardMassEvent(WiiBoard w, double topRight, double topLeft, double bottomRight, double bottomLeft) {
		super(w);
		this.topRight = topRight;
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.bottomLeft = bottomLeft;
	}

	/**
	 * The value in KG of the top right force sensor on the board.
	 */
	public double getTopRight(){
	    return topRight;	
	}
	
	/**
	 * The value in KG of the top left force sensor on the board.
	 */
	public double getTopLeft(){
		return topLeft;
	}
	
	/**
	 * The value in KG of the bottom right force sensor on the board.
	 */
	public double getBottomRight(){
		return bottomRight;
	}
	
	/**
	 * The value in KG of the bottom left force sensor on the board.
	 */
	public double getBottomLeft(){
		return bottomLeft;
	}
	
	/**
	 * The total weight, in KG, on the board
	 */
	public double getTotalWeight(){
		return topRight + topLeft + bottomRight + bottomLeft;
	}
}
