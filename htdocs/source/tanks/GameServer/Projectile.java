//
//  Projectile.java
//  GameMain
//
//  Created by Matthew Hielscher on Fri Sep 24 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.Point;
import java.util.*;
import netbyte.*;

public class Projectile extends Actor {
	
	private Vehicle owner;
	
	private boolean done;
	
	public Projectile(short n, double v, double a, short x, short y, Vehicle o, Arena p) {
		super(n, x, y, a, v, p);
		owner = o;
		done = false;
	}
	
	public void run() {
		done = false;
		while (!done) {
			incrementPosition(.025);
			try {
				Thread.sleep(20);
			} catch (Exception e) {}
		}
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void setDone(boolean d) {
		done = d;
	}
	
	public int getDamage() {
		return 20;
	}
	
	public Vehicle getOwner() {
		return owner;
	}
	
	// Increment the position of the projectile, with a timestep of 1.
	public void incrementPosition() {
		double vy = Math.sin(angle)*velocity;
		double vx = Math.cos(angle)*velocity;
		posX += vx;
		posY -= vy;
	}
	
	// Increment the position of the projectile, with an arbitrary timestep.
	public void incrementPosition(double timestep) {
		double vy = Math.sin(angle)*velocity;
		double vx = Math.cos(angle)*velocity;
		posX += vx*timestep;
		posY -= vy*timestep;
	}
	
	public void packageData(Vector pack) {
		super.packageData(pack);
		addBytes(pack, NetByte.encodeShort(owner.getNetID()));
		addBytes(pack, done);
	}
	
	public void unpackageData(byte[] pack) {
		if (pack.length < 19)
			return;
		if (retrieveShort(pack[0], pack[1]) != netID)
			return;
		super.unpackageData(pack);
		owner = (Vehicle)parent.getActorByID(retrieveShort(pack[16], pack[17]));
		done = retrieveBoolean(pack[18]);
	}

}
