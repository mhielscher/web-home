//
//  Arena.java
//  GameMain
//
//  Created by Matthew Hielscher on Fri Sep 24 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.Point;
import java.util.*;

public class Arena extends Thread {
	
	private Vector vehicles;
	private Vector projectiles;
	private NetworkManager networkManager;
	
	// action byte codes
	public static final byte END_CODE = -1;
	public static final byte ACCEL_CODE = 0;
	public static final byte DECEL_CODE = 1;
	public static final byte TURN_CODE = 2;
	public static final byte FIRE_CODE = 3;
	
	// type byte codes
	public static final byte VEHICLE_CODE = 0;
	public static final byte PROJECTILE_CODE = 1;
	
	public Arena() {
		super();
		vehicles = new Vector(0,1);
		projectiles = new Vector(0,1);
		networkManager = new NetworkManager(this);
		networkManager.start();
	}
	
	public short placeNewVehicle(String name) {
		boolean clear;
		short x;
		short y;
		do {
			clear = true;
			x = (short)(Math.random()*680+10);
			y = (short)(Math.random()*480+10);
			for (int i=0; i<vehicles.size(); i++) {
				Point p = ((Actor)vehicles.elementAt(i)).getPosition();
				if (x+7 > p.getX()-7 && x-7 < p.getX()+7 && y+9 > p.getY()-9 && y-9 < p.getY()+9)
					clear = false;
			}
		} while (!clear);
		short id = newNetID();
		Vehicle v = new Vehicle(id, x, y, 0, this);
		v.setNetName(name);
		addVehicle(v);
		return id;
	}
	
	public void addVehicle(Vehicle v) {
		vehicles.add(v);
		//System.out.println("Added vehicle.");
		v.start();
	}
	
	public void removeVehicle(Vehicle v) {
		v.forceDone();
		vehicles.remove(v);
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
		p.start();
	}
	
	public void fireProjectile(Vehicle owner) {
		//System.out.println("Firing projectile.");
		Point pos = owner.getPosition();
		double angle = owner.getAngle();
		short x = (short)(pos.getX() + 5*Math.cos(angle));
		short y = (short)(pos.getY() - 5*Math.sin(angle));
		addProjectile(new Projectile(newNetID(), (short)300, angle, x, y, owner, this));
	}
	
	public short newNetID() {
		boolean taken = false;
		short n;
		for (n=0; n<Short.MAX_VALUE; n++) {
			taken = false;
			for (int i=0; i<vehicles.size(); i++) {
				if (((Actor)vehicles.elementAt(i)).getNetID() == n)
					taken = true;
			}
			for (int i=0; i<projectiles.size(); i++) {
				if (((Actor)projectiles.elementAt(i)).getNetID() == n)
					taken = true;
			}
			if (!taken)
				break;
		}
		return n;
	}
	
	public Actor getActorByID(short id) {
		for (int i=0; i<vehicles.size(); i++) {
			if (((Actor)vehicles.elementAt(i)).getNetID() == id)
				return (Actor)vehicles.elementAt(i);
		}
		for (int i=0; i<projectiles.size(); i++) {
			if (((Actor)projectiles.elementAt(i)).getNetID() == id)
				return (Actor)projectiles.elementAt(i);
		}
		return null;
	}
	
	public void detectCollisions() {
		//check for projectile collisions
		for (int i=0; i<projectiles.size(); i++) {
			Projectile proj = (Projectile)projectiles.elementAt(i);
			Point pos = proj.getPosition();
			
			//remove all projectiles outside the borders (no explosions)
			if (pos.getX() < 0 || pos.getX() > 700 || pos.getY() < 0 || pos.getY()> 500)
				projectiles.remove(i);
			
			//check for projectile-vehicle collisions
			for (int j=0; j<vehicles.size(); j++) {
				Vehicle temp = (Vehicle)vehicles.elementAt(j);
				Point posTank = temp.getPosition();
				//if (proj center is within 10x10 rect around temp && temp != owner)
				if (pos.getX()+2 > posTank.getX()-8 && pos.getY()+2 > posTank.getY()-8
					&& pos.getX()-2 < posTank.getX()+8 && pos.getY()-2 < posTank.getY()+8
					&& temp != proj.getOwner()) {
						temp.inflictDamage(proj.getDamage());
						projectiles.remove(i);
				}
			}
		}
		
		//check for vehicle collisions
		for (int i=0; i<vehicles.size(); i++) {
			Vehicle temp = (Vehicle)vehicles.elementAt(i);
			Point pos = temp.getPosition();
			double velocity = temp.getVelocity();
			
			//stop the motion of any vehicle at the border
			if (pos.getX() < 12)
				temp.tellBlocked(-1, -2);
			if (pos.getX() > 688)
				temp.tellBlocked(1, -2);
			if (pos.getY() < 12)
				temp.tellBlocked(-2, -1);
			if (pos.getY()> 488)
				temp.tellBlocked(-2, 1);
			
			//stop the motion of both vehicles involved in a collision
			//inelastic collisions will be coded later
			for (int j=i; j<vehicles.size(); j++) {
				Vehicle other = (Vehicle)vehicles.elementAt(j);
				Point posOther = other.getPosition();
				double velocityOther = other.getVelocity();
				//if (temp center is within 16x16 rect around other)
				if (pos.getX()+8 > posOther.getX()-8 && pos.getY()+8 > posOther.getY()-8
					&& pos.getX()-8 < posOther.getX()+8 && pos.getY()-8 < posOther.getY()+8) {
						// temp is on left side
						if (pos.getX()-posOther.getX() < 0) {
							temp.tellBlocked(1, -2);
							other.tellBlocked(-1, -2);
						}
						// temp is on right side
						if (pos.getX()-posOther.getX() > 0) {
							temp.tellBlocked(-1, -2);
							other.tellBlocked(1, -2);
						}
						// temp is on top side
						if (pos.getY()-posOther.getY() < 0) {
							temp.tellBlocked(-2, 1);
							other.tellBlocked(-2, -1);
						}
						// temp is on bottom side
						if (pos.getY()-posOther.getY() > 0) {
							temp.tellBlocked(-2, -1);
							other.tellBlocked(-2, 1);
						}
				}
			}
		}
	}
	
	public void run() {
		boolean done=false;
		while (!done) {
			detectCollisions();
			networkManager.update();
			
			try {
				Thread.sleep(60);
			} catch (Exception e) {}
		}
	}
	
	public byte[] serializeGameState() {
		Vector state = new Vector(0,1);
		// vehicles first, then projectiles
		for (int i=0; i<vehicles.size(); i++) {
			Vehicle v = (Vehicle)vehicles.elementAt(i);
			state.add(new Byte(VEHICLE_CODE));
			v.packageData(state);
		}
		for (int i=0; i<projectiles.size(); i++) {
			Projectile p = (Projectile)projectiles.elementAt(i);
			state.add(new Byte(PROJECTILE_CODE));
			p.packageData(state);
		}
		byte[] buf = new byte[state.size()];
		for (int i=0; i<state.size(); i++)
			buf[i] = ((Byte)state.elementAt(i)).byteValue();
		//System.out.println("Constructed state of size "+buf.length);
		return buf;
	}
}
