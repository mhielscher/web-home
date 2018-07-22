//
//  NetworkManager.java
//  GameServer
//
//  Created by Matthew Hielscher on Mon Sep 27 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

/*
NetworkManager keeps a phone book of connected clients for direct access, and spawns input and output
threads for game interaction. It handles accepting new clients with a ServerSocket, and will open

*/

import java.net.*;
import java.util.*;
import java.io.*;
import netbyte.*;

public class NetworkManager extends Thread {
	
	private Arena parent;
	
	private ServerSocket server;
	private Vector clientSockets;
	private Vector inputHandlers;
	private Vector outputHandlers;
	private Vector tankIDs;
	
	private byte[] currentState;
	private byte[] previousState;
	
	private boolean done;
	
	public NetworkManager(Arena p) {
		parent = p;
		
		clientSockets = new Vector(0,1);
		inputHandlers = new Vector(0,1);
		outputHandlers = new Vector(0,1);
		tankIDs = new Vector(0,1);
		
		try {
			server = new ServerSocket(2829);
		} catch (IOException e) {
			System.err.println("Unable to start the server socket.");
			System.err.println(e);
		}
	}
	
	public void sendAction(short id, byte code, byte direction) {
		Vehicle v = (Vehicle)parent.getActorByID(id);
		v.lockActions();
		//System.out.println("Executing action "+code+" with value "+direction);
		if (code == Arena.DECEL_CODE)
			v.removeAction(Arena.ACCEL_CODE);
		else
			v.removeAction(code);
		v.setAction(code, direction);
		v.unlockActions();
	}
	
	public void update() {
		//System.out.println("Updating game state.");
		// this will retrieve all actions from all clients, process them, and send all
		// clients the updated game state
		
		// get most recent actions from each client
		// we use tankIDs for size because that's the confirmed list of registered players
		
		// Action handling is now done directly by the input thread and the vehicle.
		
		/*
		for (int i=0; i<tankIDs.size(); i++) {
			//System.out.println("In action loop.");
			NetworkInputHandler input = (NetworkInputHandler)inputHandlers.elementAt(i);
			Vector actions = input.getMostRecentActions();
			Vehicle v = (Vehicle)parent.getActorByID(((Short)tankIDs.elementAt(i)).shortValue());
			v.lockActions();
			//v.removeAllActions();
			for (int j=0; j<actions.size(); j+=2) {
				byte action = ((Byte)actions.elementAt(j)).byteValue();
				byte dir = ((Byte)actions.elementAt(j+1)).byteValue();
				System.out.println("Executing action "+action+" with value "+dir);
				if (action == Arena.DECEL_CODE)
					v.removeAction(Arena.ACCEL_CODE);
				else
					v.removeAction(action);
				v.setAction(action, dir);
			}
			v.unlockActions();
		}
		*/
		
		// grab the game state and serialize it
		previousState = currentState;
		currentState = parent.serializeGameState();
		
		// compare it with the previous serialized game state
		//to be done later :)
		
		// send each client the [stripped, all-]new game data
		for (int i=0; i<outputHandlers.size(); i++) {
			NetworkOutputHandler output = (NetworkOutputHandler)outputHandlers.elementAt(i);
			output.queueGameState(currentState);
		}
		//System.out.println("Done updating.");
	}
	
	public void removePlayer(Socket s) {
		int player = 0;
		for (int i=0; i<clientSockets.size(); i++) {
			if ((Socket)clientSockets.elementAt(i) == s) {
				player = i;
				break;
			}
		}
		((NetworkInputHandler)inputHandlers.elementAt(player)).forceDone();
		((NetworkOutputHandler)outputHandlers.elementAt(player)).forceDone();
		inputHandlers.remove(player);
		outputHandlers.remove(player);
		clientSockets.remove(player);
		Vehicle v = (Vehicle)parent.getActorByID(((Short)tankIDs.elementAt(player)).shortValue());
		parent.removeVehicle(v);
		tankIDs.remove(player);
		System.gc();
		//System.out.println("Input size: "+inputHandlers.size()+" Output size: "+outputHandlers.size());
	}
	
	public void run() {
		done = false;
		while (!done) {
			try {
				Socket newClient = server.accept();
				System.out.println("Got connection from "+newClient.getInetAddress());
				// maybe do some checking on newClient before adding him?
				clientSockets.add(newClient);
				NetworkInputHandler input = new NetworkInputHandler(newClient, this);
				inputHandlers.add(input);
				NetworkOutputHandler output = new NetworkOutputHandler(newClient);
				outputHandlers.add(output);
				
				input.start();
				String name = input.getPlayerName();
				short netID = parent.placeNewVehicle(name);
				output.sendPrimaryID(netID);
				input.setNetID(netID);
				tankIDs.add(new Short(netID));
				output.start();
			} 
			catch (SocketException e) {
				System.err.println("SocketException. The client has likely disconnected.");
				done = true;
				break;
			}
			catch (IOException e) {
				System.err.println("An I/O error occurred while setting up a new connection.");
				System.err.println(e);
			}
			try {
				Thread.yield(); // just for good measure I guess
			} catch (Exception e) {}
		}
	}
	
}
