//
//  Actor.java
//  GameServer
//
//  Created by Matthew Hielscher on Tue Sep 28 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.Point;
import java.util.*;
import netbyte.*;

public class Actor extends Thread {

	protected short netID; // unique ID for identification

	protected double posX; // X position of this actor within its arena. 0 is left wall, positive is right.
	protected double posY; // Y position of this actor within its arena. 0 is top wall, positive is down.
	protected double angle; // facing angle in radians of this actor. 0 is right, CCW increasing.
	protected double velocity; // velocity of this actor in the direction of its travel
	
	protected Arena parent;
	
	public Actor(short id, double x, double y, double a, double v, Arena p) {
		netID = id;
		posX = x;
		posY = y;
		angle = a;
		velocity = v;
		parent = p;
	}
	
	public Point getPosition() {
		return new Point((int)posX, (int)posY);
	}
	
	public double getAngle() {
		return angle;
	}
	
	public double getVelocity() {
		return velocity;
	}
	
	// adds all the variables in this Actor into an existing, initialized Vector
	public void packageData(Vector pack) {
		addBytes(pack, NetByte.encodeShort(netID));
		addBytes(pack, NetByte.encodeShort((short)posX));
		addBytes(pack, NetByte.encodeShort((short)posY));
		addBytes(pack, NetByte.encodeDouble(angle));
		addBytes(pack, NetByte.encodeShort((short)velocity));
	}
	
	public void unpackageData(byte[] pack) {
		posX = (double)retrieveShort(pack[2], pack[3]);
		posY = (double)retrieveShort(pack[4], pack[5]);
		angle = retrieveDouble(pack[6], pack[7], pack[8], pack[9], pack[10], pack[11], pack[12], pack[13]);
		velocity = (double)retrieveShort(pack[14], pack[15]);
	}
	
	protected void addBytes(Vector pack, byte[] bytes) {
		for (int i=0; i<bytes.length; i++)
			pack.add(new Byte(bytes[i]));
	}
	
	protected void addBytes(Vector pack, byte b) {
		pack.add(new Byte(b));
	}
	
	protected void addBytes(Vector pack, boolean b) {
		if (b)
			pack.add(new Byte((byte)1));
		else
			pack.add(new Byte((byte)0));
	}
	
	protected short retrieveShort(byte b1, byte b2) {
		byte[] ba = {b1, b2};
		try {
			return NetByte.decodeShort(ba);
		} catch (Exception e) {return -1;}
	}
	
	protected double retrieveDouble(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
		byte[] ba = {b1, b2, b3, b4, b5, b6, b7, b8};
		try {
			return NetByte.decodeDouble(ba);
		} catch (Exception e) {return -1;}
	}
	
	protected boolean retrieveBoolean(byte b) {
		return b != 0;
	}
	
	public short getNetID() {
		return netID;
	}
}
