//
//  NetworkInputHandler.java
//  GameMain
//
//  Created by Matthew Hielscher on Mon Sep 27 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

/*
POSSIBLY OBSOLETE
The client will send a list of variables and values it wants to change.
The server will review this list and make sure everything is legal, then
update its authoritative copy of the game state. It will not acknowledge
its receipt of the client's data. The server will then send to the client
a list of variables and values that have changed since the last update.
The client will update its own game world using this list.
*/

/*
Client will send list of actions to take that correspond to functions,
e.g. accelerate() or fireProjectile()
Server will do as above.
*/

// Data field form: "ObjectType:netID:Variable:value

import java.net.*;
import java.io.*;
import java.util.*;

//NOTE! This is the server's input handler. It takes input from the clients.
// This is very different from the client's input handler. Don't get them confused.
public class NetworkInputHandler extends Thread {
	
	private Socket connection; // the TCP connection to the client
	private DataInputStream stream; // the stream of action byte codes
	private NetworkManager parent; // used to send actions
	private String netName; // name of the player this connection belongs to
	private short netID; // ID of the vehicle controlled by this handler
	
	boolean breakActions;
	boolean done;
	
	public NetworkInputHandler(Socket s, NetworkManager p) {
		if (s == null)
			return;
		connection = s;
		try {
			stream = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			System.err.println("Input handler for "+s.getInetAddress()+" could not open a stream.");
			System.err.println(e);
		}
		parent = p;
		netName = null; // just to make sure...
		netID = -1;
		done = false;
		breakActions = false;
	}
	
	public void setNetID(short id) {
		netID = id;
	}
	
/*	public Vector getMostRecentActions() {
		Vector a = actions;
		actions = new Vector(0,1);
		return a;
	}*/
	
	public String getPlayerName() {
		while (netName == null) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
		}
		return netName;
	}
	
	public void forceDone() {
		done = true;
	}
	
	public void run() {
		//System.out.println("Listening...");
		//first, get the player's name
		char curr;
		StringBuffer name = new StringBuffer();
		while (name.length() == 0 || name.charAt(name.length()-1) != '\0') {
			try {
				char c = (char)stream.readByte();
				name.append(c);
				System.out.println("Got: "+c);
			}
			catch (SocketException e) {
				System.err.println("SocketException. The client has likely disconnected.");
				done = true;
				break;
			}
			catch (IOException e) {
				if (!connection.isConnected() || e instanceof EOFException) {
					done = true; // end the thread because there's nowhere to write to
					break; // get out of this immediate loop
				}
				else {
					System.err.println("An I/O error occured while reading netName from "+connection.getInetAddress());
					System.err.println(e);
				}
			}
		}
		name.deleteCharAt(name.length()-1);
		netName = name.toString();
		
		while (netID == -1) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {}
		}
		
		byte buf = 0;
		byte val = 0;
		while (!done) {
			try {
				buf = stream.readByte();
				val = stream.readByte();
				//if (buf != -1)
					//System.out.println("Got code "+buf+" and value "+val);
			}
			catch (SocketException e) {
				System.err.println("SocketException. The client has likely disconnected.");
				done = true;
				break;
			}
			catch (IOException e) {
				if (!connection.isConnected() || e instanceof EOFException) {
					done = true; // end the thread because there's nowhere to write to
					break; // get out of this immediate loop
				}
				else {
					System.err.println("Error while reading action code from client "+connection.getInetAddress());
					System.err.println(e);
				}
			}
			parent.sendAction(netID, buf, val);
			
			try {
				Thread.sleep(8);
			} catch (Exception e) {}
		}
		System.out.println("Terminating input thread.");
		parent.removePlayer(connection);
	}
}
