//
//  RAFrontEnd.java
//
//  Created by Matthew Hielscher on 2008-11-20.
//  Written for CS 165A, Fall 2008, UCSB.
//

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.net.*;

import java.util.*;
import java.math.*;

import java.io.*;

public class RAFrontEnd extends JFrame
{
	private class InputConnection implements Runnable
	{
		private Socket connection;
		public InputConnection()
		{
			
		}
		public void run()
		{
			try {
				connection = new Socket("localhost", 3232);
				receive(connection);
			} catch (IOException e) {
				System.out.println("Error: Can't connect.");
			}
		}
	}
	
	//private Socket connection;
	private InputConnection in;
	//private OutputConnection out;
	
	//GUI components
	JPanel arena;
	
	//Entity data
	Vector entities;
	
	public RAFrontEnd()
	{
		super("");
		setSize(1024, 700);
		setResizable(false);
		setVisible(true);
		getRootPane().setDoubleBuffered(true);
		
		entities = new Vector();
		
		//arena = new JPanel();
		
		for (int i=0; i<10; i++)
			entities.add(new RASmartBot());
			
		(new Thread(new InputConnection())).start();
	}
	
	public void paint(Graphics g)
	{
		//manual double-buffering
		Image backbuffer = createImage(1024, 700);
		Graphics2D g2d = (Graphics2D)(backbuffer.getGraphics());
		super.paint(g2d);
		//Graphics2D g2d = (Graphics2D)g;
		//g2d.setColor(Color.WHITE);
		//g2d.fillRect(0,0,1024,700);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0,0,800,600);
		for (Object ent : entities)
		{
			((RAEntity)ent).draw(g2d);
		}
		g.drawImage(backbuffer, 5, 30, this);
	}
	
	public void receive(Socket conn)
	{
		boolean done = false;
		while (done != true)
		{
			try {
				DataInputStream in = new DataInputStream(conn.getInputStream());
				byte[] id = new byte[5];
				in.read(id, 0, 5);
				try {
					if (!((new String(id, "utf-8")).equals("Count"))) {
						System.out.println("Expected Count, received: "+(new String(id, "utf-8")));
						return;
					}
				} catch (UnsupportedEncodingException e) {
					return;
				}
				in.skipBytes(1); //skip the \0 (doesn't work in the string)
				int count = 0;
				count = in.readInt();
				System.out.println("Count is " + count + "; size is " + entities.size());
				Vector toBeDeleted = new Vector();
				for (Object ent : entities)
				{
					((RAEntity)ent).buildFromStream(in);
					((RAEntity)ent).Print();
					count--;
					if (((RAEntity)ent).isDead())
						toBeDeleted.add(ent);
				}
				//System.out.println("Deleting stuff");
				for (Object tbd : toBeDeleted)
				{
					System.out.println("Deleting "+tbd);
					entities.remove(tbd);
				}
						
				while (count > 0)
				{
					RAProjectile p = new RAProjectile();
					entities.add(p);
					p.buildFromStream(in);
					count--;
				}

			} catch (IOException e) {
				System.out.println("Error: Problem reading data.");
			}
			try {
				Thread.sleep(32);
			} catch (InterruptedException e) {
			}
			repaint();
		}
	}
	
	public static void main(String args[])
	{
		new RAFrontEnd();
	}
}
