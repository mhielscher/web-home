//
//  RAEntity.java
//
//  Created by Matthew Hielscher on 2008-11-20.
//  Written for CS 165A, Fall 2008, UCSB.
//

import java.math.*;
import java.awt.*;
import java.io.*;

public class RASmartBot extends RABot
{
	private Point goalPoint;
	
	public RASmartBot()
	{
		goalPoint = new Point(0,0);
	}
	
	public void draw(Graphics2D g2d)
	{
		super.draw(g2d);
		g2d.drawLine(goalPoint.x-5, goalPoint.y, goalPoint.x+5, goalPoint.y);
		g2d.drawLine(goalPoint.x, goalPoint.y-5, goalPoint.x, goalPoint.y+5);
	}
	
	public void buildFromStream(DataInputStream in) throws IOException
	{
		byte[] id = new byte[10];
		in.read(id, 0, 10);
		try {
			if (!((new String(id, "utf-8")).equals("RASmartBot"))) {
				System.out.println("Expected RASmartBot, received: "+(new String(id, "utf-8")));
				return;
			}
		} catch (UnsupportedEncodingException e) {
			return;
		}
		in.skipBytes(1); //skip the \0 (doesn't work in the string)
		super.buildFromStream(in);
		goalPoint.x = in.readInt();
		goalPoint.y = in.readInt();
	}
}