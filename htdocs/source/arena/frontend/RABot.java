//
//  RAEntity.java
//
//  Created by Matthew Hielscher on 2008-11-20.
//  Written for CS 165A, Fall 2008, UCSB.
//

import java.math.*;
import java.awt.*;
import java.io.*;

public class RABot extends RAEntity
{
	private double aim;
	private int energy;
	private int damage;
	
	public RABot()
	{
		aim = 0;
		energy = 0;
		damage = 0;
	}
	
	public void draw(Graphics2D g2d)
	{
		super.draw(g2d);
		int aimX = (int)(35*Math.cos(aim));
		int aimY = (int)(35*Math.sin(aim));
		Color prev = g2d.getColor();
		g2d.setColor(Color.BLACK);
		g2d.drawLine(position.x, position.y, position.x+aimX, position.y+aimY);
		g2d.setColor(prev);
	}
	
	public void buildFromStream(DataInputStream in) throws IOException
	{
		byte[] id = new byte[5];
		in.read(id, 0, 5);
		try {
			if (!(new String(id, "utf-8")).equals("RABot")) {
				System.out.println("Expected RABot, received: "+(new String(id, "utf-8")));
				return;
			}
		} catch (UnsupportedEncodingException e) {
			return;
		}
		in.skipBytes(1); //skip the \0 (doesn't work in the string)
		super.buildFromStream(in);
		aim = in.readDouble();
		energy = in.readInt();
		damage = in.readInt();
	}
}
