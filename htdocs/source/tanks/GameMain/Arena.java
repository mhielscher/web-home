//
//  Arena.java
//  GameMain
//
//  Created by Matthew Hielscher on Fri Sep 24 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import netbyte.*;

public class Arena extends JComponent implements Runnable {
	
	private Vector vehicles;
	private Vector projectiles;
	private Vector events;
	
	private Vehicle localTank; // the local tank controlled by keypresses
	
	// preload these images so they'll always be ready
	private Image ground;
	private Image tank;
	private Image shell;
	private Image boom;
	
	// player's current executing actions
	private Vector actions;
	
	// network handlers
	private NetworkInputHandler input;
	private NetworkOutputHandler output;
	
	// action byte codes
	public static final byte END_CODE = -1;
	public static final byte ACCEL_CODE = 0;
	public static final byte DECEL_CODE = 1;
	public static final byte TURN_CODE = 2;
	public static final byte FIRE_CODE = 3;
	
	// type byte codes
	public static final byte VEHICLE_CODE = 0;
	public static final byte PROJECTILE_CODE = 1;
	
	public class KeyInputListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (localTank == null)
				return;
			Vehicle v = localTank;
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_UP) {
				v.removeAction(DECEL_CODE);
				v.setAction(ACCEL_CODE, 1);
			}
			else if (code == KeyEvent.VK_DOWN) {
				v.removeAction(DECEL_CODE);
				v.setAction(ACCEL_CODE, -1);
			}
			else if (code == KeyEvent.VK_LEFT) {
				v.removeAction(TURN_CODE);
				v.setAction(TURN_CODE, 1);
			}
			else if (code == KeyEvent.VK_RIGHT) {
				v.removeAction(TURN_CODE);
				v.setAction(TURN_CODE, -1);
			}
			else if (code == KeyEvent.VK_SPACE) {
				v.removeAction(FIRE_CODE);
				v.setAction(FIRE_CODE, 0);
			}
		}
		
		public void keyReleased(KeyEvent e) {
			if (localTank == null)
				return;
			Vehicle v = localTank;
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
				v.removeAction(ACCEL_CODE);
				v.setAction(DECEL_CODE, 0);
			}
			else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
				v.removeAction(TURN_CODE);
				v.setAction(TURN_CODE, 0);
			}
		}
		
		public void keyTyped(KeyEvent e) {}
	}
	
	public Arena(NetworkInputHandler i, NetworkOutputHandler o) {
		super();
		vehicles = new Vector(0,1);
		projectiles = new Vector(0,1);
		events = new Vector(0,1);
		
		localTank = null;
		
		//addKeyListener(new KeyInputListener());
		setFocusable(true);
		requestFocusInWindow();
		
		//load images into memory
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		ground = toolkit.createImage("images/grass.png");
		tank = toolkit.createImage("images/tank.png");
		shell = toolkit.createImage("images/shell.png");
		boom = toolkit.createImage("images/boom.png");
		
		actions = new Vector(0,1);
		
		input = i;
		output = o;
		i.start();
		o.sendName("Restorer");
	}
	
	public KeyInputListener newListener() {
		return new KeyInputListener();
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
		v.setImage(tank);
		v.start();
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
		p.setImage(shell);
		p.start();
	}
	
	public void fireProjectile(Vehicle owner) {
		Point pos = owner.getPosition();
		double angle = owner.getAngle();
		double x = pos.getX() + 5*Math.cos(angle);
		double y = pos.getY() - 5*Math.sin(angle);
		addProjectile(new Projectile(newNetID(), 300, angle, x, y, owner, this)); //BIG PROBLEM! #mark
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
	
	public void createExplosion(int x, int y) {
		Event e = new Event(boom, 100, x, y);
		events.add(e);
		e.start();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		setBackground(Color.white);
		g.setColor(Color.black);
		g.fillRect(0, 0, 699, 499);
	/*	for (int x=0; x<700; x+=16) {
			for (int y=0; y<500; y+=16) {
				g.drawImage(ground, x, y, null);
			}
		}*/
		for (int i=0; i<vehicles.size(); i++)
			((Vehicle)(vehicles.elementAt(i))).paint(g);
		for (int i=0; i<projectiles.size(); i++)
			((Projectile)(projectiles.elementAt(i))).paint(g);
		for (int i=0; i<events.size(); i++)
			((Event)(events.elementAt(i))).paint(g);
	}
	
	public Actor getActorByID(short id, byte type) {
		for (int i=0; i<vehicles.size(); i++) {
			if (((Actor)vehicles.elementAt(i)).getNetID() == id)
				return (Actor)vehicles.elementAt(i);
		}
		for (int i=0; i<projectiles.size(); i++) {
			if (((Actor)projectiles.elementAt(i)).getNetID() == id)
				return (Actor)projectiles.elementAt(i);
		}
		
		// the object referenced doesn't exist; it must be new on the server side
		// create a new object of the correct type and register it with the given ID.
		// specifics of the variables will be added from the net handler
		Actor a = null;
		if (type == VEHICLE_CODE) {
			a = new Vehicle(id, 0, 0, 0, this);
			((Vehicle)a).setImage(tank);
			vehicles.add(a);
			a.start();
		}
		else if (type == PROJECTILE_CODE) {
			a = new Projectile(id, 0, 0, 0, 0, null, this);
			((Projectile)a).setImage(shell);
			projectiles.add(a);
			a.start();
		}
		return a;
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
						createExplosion((int)pos.getX(), (int)pos.getY());
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
	
	public NetworkOutputHandler getOutputHandler() {
		return output;
	}
	
	private void updateState() {
		byte[] newState = input.getGameState();
		//byte[] oldState = serializeGameState();
		boolean finished = false;
		Vector actorsAlive = new Vector((int)(newState.length/36),1); // start it at the smallest possible size, to save time
		int c = 0;
		while (!finished) {
			byte dataType = newState[c++];
			short netID = retrieveShort(newState[c], newState[c+1]);
			byte[] thisActor;
			if (dataType == VEHICLE_CODE)
				thisActor = new byte[36];
			else // projectile
				thisActor = new byte[19];
			for (int i=0; i<thisActor.length; i++)
				thisActor[i] = newState[i+c];
			Actor a = getActorByID(netID, dataType);
			a.unpackageData(thisActor);
			actorsAlive.add(a);
			c += thisActor.length;
			if (newState.length - c < 17) // no more complete data in there
				finished = true;
		}
		// now clean up and delete any actors that the server says don't exist (i.e., they've been destroyed)
		boolean exists;
		for (int i=0; i<vehicles.size(); i++) {
			exists = false;
			for (int j=0; j<actorsAlive.size(); j++) {
				if (actorsAlive.elementAt(j) == vehicles.elementAt(i))
					exists = true;
			}
			if (!exists) {
				vehicles.remove(i);
				i--;
			}
		}
		for (int i=0; i<projectiles.size(); i++) {
			exists = false;
			for (int j=0; j<actorsAlive.size(); j++) {
				if (actorsAlive.elementAt(j) == projectiles.elementAt(i))
					exists = true;
			}
			if (!exists) {
				projectiles.remove(i);
				i--;
			}
		}
	}
	
	private short retrieveShort(byte b1, byte b2) {
		byte[] ba = {b1, b2};
		try {
			return NetByte.decodeShort(ba);
		} catch (Exception e) {return -1;}
	}
	
	public void run() {
		boolean done=false;
		long timex = System.currentTimeMillis();
		while (!done) {
			repaint();
			detectCollisions();
			if (localTank == null) {
				short id = input.getNetID();
				if (id != -1)
					localTank = (Vehicle)getActorByID(id, VEHICLE_CODE);
			}
			if (input.isUpdated()) {
				//System.out.println("Updating state.");
				updateState();
			}
			for (int i=0; i<events.size(); i++)
				if (((Event)events.elementAt(i)).isDone())
					events.remove(i);
		/*	Vector data = new Vector(0,1);
			((Vehicle)vehicles.elementAt(0)).packageData(data);
			System.out.println(System.currentTimeMillis()-timex);
			for (int i=0; i<data.size(); i++)
				System.out.print(((Byte)data.elementAt(i)).byteValue()+", ");
			System.out.println("\n---------");*/
			try {
				Thread.sleep(10);
			} catch (Exception e) {}
		}
	}
	
	public byte[] serializeGameState() {
		//byte[] state = new byte[vehicles.size()*74 + projectiles.size()*22];
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
		return buf;
	}
				
}
