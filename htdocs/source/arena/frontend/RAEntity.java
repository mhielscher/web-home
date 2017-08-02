//
//  RAEntity.java
//
//  Created by Matthew Hielscher on 2008-11-20.
//  Written for CS 165A, Fall 2008, UCSB.
//

import java.math.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class RAEntity
{
	protected Point position;
	private int radius;
	protected double angle;
	private double velocity;
	private double angularVelocity;
	protected Color color;
	protected boolean dead;
	
	public RAEntity()
	{
		position = new Point(0,0);
		radius = 0;
		angle = 0;
		velocity = 0;
		angularVelocity = 0;
		color = new Color((new Random()).nextInt());
		dead = false;
	}
	
	public void draw(Graphics2D g2d)
	{
		g2d.setColor(color);
		g2d.fillOval(position.x-radius, position.y-radius, radius*2, radius*2);
	}

	public boolean isDead()
	{
		return dead;
	}
	
	public void buildFromStream(DataInputStream in) throws IOException
	{
		byte[] id = new byte[8];
		in.read(id, 0, 8);
		try {
			if (!(new String(id, "utf-8")).equals("RAEntity")) {
				System.out.println("Expected RAEntity, received: "+(new String(id, "utf-8")));
				return;
			}
		} catch (UnsupportedEncodingException e) {
			return;
		}
		in.skipBytes(1); //skip the \0 (doesn't work in the string)
		position.x = in.readInt();
		position.y = in.readInt();
		System.out.println("Read position: ("+position.x+","+position.y+")");
		angle = in.readDouble();
		velocity = in.readDouble();
		angularVelocity = in.readDouble();
		radius = in.readInt();
	}
	
	public void Print()
	{
		System.out.println("Stats for Bot" + this);
		System.out.println("  position: ("+position.x+","+position.y+")");
		System.out.println("  velocity: "+velocity);
		System.out.println("  radius: "+radius);
	}
}
