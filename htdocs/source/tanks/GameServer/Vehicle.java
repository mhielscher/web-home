//
//  Vehicle.java
//  GameMain
//
//  Created by Matthew Hielscher on Fri Sep 24 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.Point;
import java.util.*;
import netbyte.*;

public class Vehicle extends Actor {

	private short throttle; // keys control throttle position (momentary), which controls velocity
	private boolean decelerating; // true when no keys are pressed and the throttle is dropping to 0
	private double angVel; // Angular velocity. Same signs as angle.
	
	private byte blockedX; // Vehicle is blocked on given side. 0 is clear, -1 is left, +1 is right, 2 is both.
	private byte blockedY; // Vehicle is blocked on given side. 0 is clear, -1 is up, +1 is down, 2 is both.
	
	private short armor; // health, basically
	private short shields; // additional protection, possibly won't be used
	private short ammo; // projectiles left; -1 means infinite
	
	private boolean done;
	private boolean actionAvailable;
	
	//network stuff
	private String netName; // the name of the commander of this vehicle
	
	private Vector actions;
	
	public Vehicle(short n, double x, double y, double a, Arena p) {
		super(n, x, y, a, (short)0, p);
		throttle = 0;
		decelerating = false;
		angVel = 0;
		
		blockedX = 0;
		blockedY = 0;
		
		armor = 100;
		shields = 0;
		ammo = -1;
		
		actions = new Vector(0,1);
		actionAvailable = true;
	}
	
	public Vehicle(short n, double x, double y, double a, short ar, short s, short am, Arena p) {
		super(n, x, y, a, (short)0, p);
		throttle = 0;
		decelerating = false;
		angVel = 0;
		
		blockedX = 0;
		blockedY = 0;
		
		armor = ar;
		shields = s;
		ammo = am;
		
		actions = new Vector(0,1);
		actionAvailable = true;
	}
	
	public synchronized void lockActions() {
		while (!actionAvailable) {
			try {
				wait(2);
			} catch (Exception e) {}
		}
		actionAvailable = false;
	}
	
	// be careful with this one, it can unlock from anywhere at anytime
	// use only after a block starting with lockActions()!
	public synchronized void unlockActions() {
		actionAvailable = true;
		notifyAll();
	}
	
	public /*synchronized*/ void setAction(byte code, int direction) {
	/*	while (!actionAvailable) {
			try {
				wait();
			} catch (Exception e) {}
		}*/
		//actionAvailable = false;
		//System.out.println("Setting action "+code+" to "+direction);
		for (int i=0; i<actions.size(); i+=2) {
			if (actions.elementAt(i) instanceof Byte && ((Byte)actions.elementAt(i)).byteValue() == code)
				return;
		}
		actions.add(new Byte(code));
		actions.add(new Integer(direction));
		//actionAvailable = true;
		//notifyAll();
	}
	
	public /*synchronized*/ void removeAction(byte code) {
	/*	while (!actionAvailable) {
			try {
				wait();
			} catch (Exception e) {}
		}*/
		//actionAvailable = false;
		//System.out.println("Removing action "+code);
		for (int i=0; i<actions.size(); i+=2) {
			if (actions.elementAt(i) instanceof Byte && ((Byte)actions.elementAt(i)).byteValue() == code) {
				actions.remove(i+1);
				actions.remove(i);
			}
		}
		//actionAvailable = true;
		//notifyAll();
	}
	
	public /*synchronized*/ void removeAllActions() {
	/*	while (!actionAvailable) {
			try {
				wait();
			} catch (Exception e) {}
		}*/
		//actionAvailable = false;
		actions = new Vector(0,1);
		//actionAvailable = true;
		//notifyAll();
	}
	
	public void forceDone() {
		done = true;
	}
	
	public void run() {
		setPriority(10);
		done = false;
		long prevT = System.currentTimeMillis();
		long currT = prevT;
		long instT = prevT;
		while (!done) {
			currT = System.currentTimeMillis();
			//if (currT-prevT > 35)
			//	System.out.println(currT-prevT);
			prevT = currT;
			incrementPosition(.035);
			incrementAngle(.035);
			//throttle effect
			if (decelerating && throttle > 0)
				throttle -= 5;
			else if (decelerating && throttle < 0)
				throttle += 5;
			if (throttle > -4 && throttle < 4 && decelerating) {
				throttle = 0;
				decelerating = false;
			}
			if (velocity != throttle)
				velocity += (throttle-velocity)/3;
			if (velocity > -4 && velocity < 4)
				velocity = 0;
			
			doActions();
			
			instT = System.currentTimeMillis();
			//System.out.println(instT-prevT);
			try {
				if (instT-prevT >= 31)
					Thread.yield();
				else
					Thread.sleep(31-(instT-prevT));
			} catch (Exception e) {}
		}
	}
	
	private synchronized void doActions() {
		// run action effects on this vehicle
		while (!actionAvailable) {
			try {
				wait(2);
			} catch (Exception e) {}
		}
		actionAvailable = false;
		for (int i=0; i<actions.size(); i+=2) {
			byte code = ((Byte)actions.elementAt(i)).byteValue();
			int direction = ((Integer)actions.elementAt(i+1)).intValue();
			//System.out.println("Executing action "+code+" with value "+direction);
			if (code == Arena.ACCEL_CODE)
				accelerate(direction);
			else if (code == Arena.DECEL_CODE) {
				decelerate();
				removeAction(Arena.DECEL_CODE);
			}
			else if (code == Arena.TURN_CODE) {
				turn(direction*1.25);
				if (direction == 0)
						removeAction(Arena.TURN_CODE);
			}
			else if (code == Arena.FIRE_CODE) {
				//System.out.println("Loading projectile.");
				parent.fireProjectile(this);
				removeAction(Arena.FIRE_CODE);
			}
		}
		actionAvailable = true;
		notifyAll();
	}
	
	// Increment the position of the vehicle, with a timestep of 1.
	public void incrementPosition() {
		double vy = Math.sin(angle)*velocity;
		double vx = Math.cos(angle)*velocity;
		posX += vx;
		posY -= vy;
	}
	
	// Increment the position of the vehicle, with an arbitrary timestep.
	public void incrementPosition(double timestep) {
		double vy = Math.sin(angle)*velocity;
		double vx = Math.cos(angle)*velocity;
		if (blockedX * vx * timestep <= 0)
			posX += vx*timestep;
		else
			blockedX = 0;
		if (blockedY * vy * timestep >= 0)
			posY -= vy*timestep;
		else
			blockedY = 0;
	}
	
	// Increment the angle of the vehicle, with a timestep of 1.
	public void incrementAngle() {
		angle += angVel;
	}
	
	// Increment the angle of the vehicle, with an arbitrary timestep.
	public void incrementAngle(double timestep) {
		angle += angVel*timestep;
	}
	
	// throttle up
	public void accelerate(int direction) {
		decelerating = false;
		if (throttle == 0)
			throttle += 40*direction;
		else if (throttle >= -60 && throttle <= 100)
			throttle += 20*direction;
		if (throttle < -60)
			throttle = -60;
		if (throttle > 100)
			throttle = 100;
		if (throttle > -10 && throttle < 10)
			throttle = 0;
	}
	
	//throttle down
	public void decelerate() {
		decelerating = true;
	}
	
	//stop dead - kill throttle and velocity
	public void emergencyStop() {
		decelerating = false;
		throttle = 0;
		velocity = 0;
	}
	
	// used externally to tell the vehicle in which directions it's blocked
	// (x, y): positive is left and down, see above; -2 means no change
	public void tellBlocked(int x, int y) {
		if (x > 2 || x < -2 || y > 2 || y < -2)
			return;
		if (x != -2)
			blockedX = (byte)x;
		if (y != -2)
			blockedY = (byte)y;
	}
	
	// set a discrete amount of rotational velocity
	public void turn(double av) {
		angVel = av;
	}
	
	// inflict damage to the vehicle, subtracting from the shields and/or armor where appropriate
	public void inflictDamage(int damage) {
		if (shields > damage)
			shields -= damage;
		else if (shields > 0) {
			damage -= shields;
			shields = 0;
			armor -= damage;
		}
		else
			armor -= damage;
	}
	
	public void setNetName(String name) {
		netName = name;
	}
	
	public void packageData(Vector pack) {
		super.packageData(pack);
		addBytes(pack, NetByte.encodeShort(throttle));
		addBytes(pack, decelerating);
		addBytes(pack, NetByte.encodeDouble(angVel));
		addBytes(pack, blockedX);
		addBytes(pack, blockedY);
		addBytes(pack, NetByte.encodeShort(armor));
		addBytes(pack, NetByte.encodeShort(shields));
		addBytes(pack, NetByte.encodeShort(ammo));
		addBytes(pack, done);
		// netName is not encoded, because it will never change without the user's action
	}
	
	public void unpackageData(byte[] pack) {
		if (pack.length < 36)
			return;
		if (retrieveShort(pack[0], pack[1]) != netID)
			return;
		super.unpackageData(pack);
		throttle = retrieveShort(pack[16], pack[17]);
		decelerating = retrieveBoolean(pack[18]);
		angVel = retrieveDouble(pack[19], pack[20], pack[21], pack[22], pack[23], pack[24], pack[25], pack[26]);
		blockedX = pack[27];
		blockedY = pack[28];
		armor = retrieveShort(pack[29], pack[30]);
		shields = retrieveShort(pack[31], pack[32]);
		ammo = retrieveShort(pack[33], pack[34]);
		done = retrieveBoolean(pack[35]);
	}
}
