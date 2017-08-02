//
//  NetworkOutputHandler.java
//  GameServer
//
//  Created by Matthew Hielscher on Wed Sep 29 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.net.*;
import java.io.*;

public class NetworkOutputHandler extends Thread {
	
	private Socket connection; // the TCP connection to the client
	private DataOutputStream stream; // the stream of action byte codes
	private byte[] gameState; // the game state queued to be sent
	private byte[] tempState; // the game state that will go to gameState once gameState is finished writing
	private boolean readyToSend; // ready to send the new gameState
	private boolean doneSending; // ready to do gameState = tempState;
	
	private boolean done;
	
	public NetworkOutputHandler(Socket s) {
		//System.out.println("Creating output.");
		if (s == null)
			return;
		connection = s;
		try {
			stream = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			System.err.println("Output handler for "+s.getInetAddress()+" could not open a stream.");
			System.err.println(e);
		}
		done = false;
		doneSending = true;
		readyToSend = false;
		//System.out.println("Created output.");
	}
	
	public void sendPrimaryID(short id) {
		try {
			stream.writeShort(id);
		} catch (IOException e) {
			System.err.println("Error writing ID.");
			System.err.println(e);
		}
		System.out.println("Sent new primary ID: "+id);
	}
	
	public void queueGameState(byte[] state) {
		//System.out.println("Waiting on sending.");
		while (!doneSending) {
			try {
				Thread.sleep(5);
			} catch (Exception e) {}
		}
		//System.out.println("Done sending, updating.");
		gameState = state;
		readyToSend = true;
	}
	
	public void forceDone() {
		done = true;
	}
	
	public void run() {
		//System.out.println("Started output.");
		while (!done) {
			while (!readyToSend) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
			}
			//System.out.println("Sending data.");
			//now send the whole data
			if (gameState == null || gameState.length < 1) {
				readyToSend = false;
				continue;
			}
			doneSending = false;
			try {
				stream.writeShort(gameState.length+12); // +8 for the timestamp, +4 for the white bytes
				stream.writeLong(System.currentTimeMillis());
				stream.write(gameState, 0, gameState.length);
				stream.write(0xff);
				stream.write(0xff);
				stream.write(0xff);
				stream.write(0xff);
				//System.out.println("Send state data of length "+(gameState.length+4));
			} 
			catch (SocketException e) {
				System.err.println("SocketException. The client has likely disconnected.");
				done = true;
				break;
			}
			catch (IOException e) {
				System.err.println("An I/O error occurred while sending game data to "+connection.getInetAddress());
				System.err.println(e);
			}
			readyToSend = false;
			doneSending = true;
		}
		doneSending = true; // workaround for annoying persistence problem
	}
}
