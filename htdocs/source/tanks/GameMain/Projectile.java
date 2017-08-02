//
//  Projectile.java
//  GameMain
//
//  Created by Matthew Hielscher on Fri Sep 24 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import netbyte.*;

public class Projectile extends Actor {
	
	private Vehicle owner;
	private Image image;
	
	private boolean done;
	
	public Projectile(short n, double v, double a, double x, double y, Vehicle o, Arena p) {
		super(n, x, y, a, v, p);
		owner = o;
		done = false;
	}
	
	public void setImage(Image i) {
		image = i;
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
	
	public Point getPosition() {
		return new Point((int)posX, (int)posY);
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
		owner = (Vehicle)parent.getActorByID(retrieveShort(pack[16], pack[17]), Arena.VEHICLE_CODE);
		done = retrieveBoolean(pack[18]);
	}

	// draw this individual projectile in the arena's painting area
	public void paint(Graphics g) {
		BufferedImage buf = new BufferedImage(8, 8, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = buf.createGraphics();
		g2.translate(4, 4); //move the origin to the center of our image for rotation
		g2.rotate(-angle); //don't know why it's backwards, but it needs to be
		g2.translate(-4, -4); //move it back to where it was, now properly rotated
		g2.drawImage(image, 2, 2, null);
		g.drawImage(buf, (int)posX-4, (int)posY-4, null);
	}

}
