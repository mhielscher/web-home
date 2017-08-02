//
//  NetworkInputHandler.java
//  GameMain
//
//  Created by Matthew Hielscher on Mon Sep 27 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

/*
The client will send a list of variables and values it wants to change.
The server will review this list and make sure everything is legal, then
update its authoritative copy of the game state. It will not acknowledge
its receipt of the client's data. The server will then send to the client
a list of variables and values that have changed since the last update.
The client will update its own game world using this list.
*/

// Obsolete?
// Data field form: "ObjectType:netID:Variable:value

import java.net.*;
import java.io.*;
import java.util.*;

public class NetworkInputHandler extends Thread {
	
	private Socket connection; // the TCP connection to the server
	private DataInputStream stream; // the stream of game state data
	private byte[] gameState; // the last complete game state frame received
	private boolean updated; // whether gameState has been updated since the last read
	private short netID; // our netID; which tank we will control
	
	private boolean done;
	private boolean temp;
	
	public NetworkInputHandler(Socket s) {
		if (s == null)
			return;
		connection = s;
		try {
			stream = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			System.err.println("Input handler for "+s.getInetAddress()+" could not open a stream.");
			System.err.println(e);
		}
		gameState = null;
		updated = false;
		done = false;
		temp = true;
		netID = -1;
	}
	
	public boolean isUpdated() {
		return /*temp && */updated;
	}
	
	public byte[] getGameState() {
		updated = false;
		temp = false;
		byte[] copyState = new byte[gameState.length];
		for (int i=0; i<gameState.length; i++)
			copyState[i] = gameState[i];
		return copyState;
	}
	
	public short getNetID() {
		return netID;
	}
	
	// First thing to receive will be our tank's netID.
	// Then game data will come in continuously.
	// State frames are separated by four white bytes (0xff).
	// On second thought, the game state data will be preceded by a short telling the length of
	// the upcoming byte array (the white padding will still be there, and included in the length).
	public void run() {
		setPriority(10);
		done = false;
		try {
			netID = stream.readShort();
		} catch (IOException e) {
			System.err.println("I/O error while reading netID from server "+connection.getInetAddress());
			System.err.println(e);
		}
		System.out.println("Got netID "+netID);
		
		short length;
		long serverTimeStamp=0, myTimeStamp=0;
		long serverPrevStamp=0, myPrevStamp=0;
		long myVariance=0;
		boolean rested = true;
		while (!done) {
			length = 0;
			try {
				length = stream.readShort();
				//System.out.println("length = "+length);
				serverTimeStamp = stream.readLong();
				myTimeStamp = System.currentTimeMillis();
				if (rested) {
					myVariance = myTimeStamp-myPrevStamp;
					rested = false;
				}
				//System.out.println("Client: "+(myVariance)+" Server: "+(serverTimeStamp-serverPrevStamp));
				length -= 8;
				gameState = new byte[length];
				//stream.read(gameState, 0, length);
				for (int i=0; i<length; i++)
					gameState[i] = stream.readByte();
			//	while (!done)
			//		System.out.println(stream.readByte());
			} catch (IOException e) {
				System.err.println("I/O error while reading game state from server "+connection.getInetAddress());
				System.err.println("length = "+length);
				e.printStackTrace();
				
			}
			if (gameState[length-1] != -1 || gameState[length-2] != -1 || gameState[length-3] != -1
				|| gameState[length-4] != -1) {
					System.err.println("Got bad game state frame.");
					System.err.print("-1: "+gameState[length-1]+" -2: "+gameState[length-2]);
					System.err.print(" -3: "+gameState[length-3]+" -4: "+gameState[length-4]+"\n");
			}
			else {
				if ((myVariance-20) < (serverTimeStamp-serverPrevStamp) || serverPrevStamp == 0) {
					//System.out.println("Got good game state data!");
					updated = true;
				}
			}
			
			if ((myVariance-5) < (serverTimeStamp-serverPrevStamp) || serverPrevStamp == 0) {
				myPrevStamp = myTimeStamp;
				serverPrevStamp = serverTimeStamp;
				rested = true;
				try {
					Thread.sleep(10);
				} catch (Exception e) {}
			}
		//	for (int i=0; i<gameState.length; i++) {
		//		System.out.println(gameState[i]);
		//	}
		}
	}
	
}
