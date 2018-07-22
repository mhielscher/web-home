//
//  Event.java
//  GameMain
//
//  Created by Matthew Hielscher on Mon Sep 27 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Event extends Thread {

	private int posX; // x location
	private int posY; // y location

	private Image image; // the current image to be displayed at the location of this event
	private Vector animationBank; // a vector of images through which to cycle
	private int delay; // the delay in ms between frames
	private int lifetime; // the total lifetime of the event
	
	private int timer;
	
	public Event(Image i, int l, int x, int y) {
		animationBank = null;
		delay = 10;
		image = i;
		lifetime = l;
		timer = 0;
		posX = x;
		posY = y;
	}
	
	public Event(Vector a, int d, int l, int x, int y) {
		animationBank = a;
		delay = d;
		lifetime = l;
		timer = 0;
		image = (Image)animationBank.elementAt(0);
		posX = x;
		posY = y;
	}
	
	public boolean isDone() {
		return timer >= lifetime;
	}
	
	public void run() {
		int rot = 0;
		while (timer < lifetime) {
			if (animationBank != null) {
				image = (Image)animationBank.elementAt(rot);
				rot++;
				if (rot >= animationBank.size())
					rot = 0;
			}
			timer += delay;
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		}
	}
	
	public void paint(Graphics g) {
		int h = image.getHeight(null);
		int w = image.getWidth(null);
		g.drawImage(image, posX-w/2, posY-h/2, null);
	}
}
