//
//  ChatLink.java
//  XPReader
//
//  Created by Matthew Hielscher on Fri Jul 2 2004
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatLink {
	
	private class InputBin extends Thread {
		private DataInputStream din;
		private XPLink xp;
		private boolean loggedIn;
		
		public InputBin(DataInputStream d) {
			din = d;
			loggedIn = false;
		}
		
		public void run() {
			while (true) {
				try {
					String cmd = getCommand();
					System.out.println(cmd);
					if (cmd.indexOf("372") != -1)
						loggedIn = true;
					if (cmd.indexOf("MESS") != -1) {
						//System.out.println("Got MESS command.");
						xp.sendMessage("<"+cmd.substring(1, cmd.indexOf("!"))+"> "+cmd.substring(cmd.indexOf("MESS")+5, cmd.length()));
					}
					Thread.sleep(5);
				} catch (Exception e) {}
			}
		}
		
		public void setLink(XPLink x) {
			xp = x;
		}
		
		public boolean isLoggedIn() {
			return loggedIn;
		}
		
		private String getCommand() {
			try {
				StringBuffer message = new StringBuffer();
				byte prev = 0;
				byte curr = din.readByte();
				while (!(prev=='\r' && curr=='\n')) {
					message.append((char)curr);
					prev = curr;
					curr = din.readByte();
				}
				return new String(message.deleteCharAt(message.length()-1));
			} catch (IOException e) {
				System.err.println("ERROR: Could not retrieve client message: ");
			}
			return "";
		}
	}
	
	// Start class ChatLink declaration
	private String channelName;
	private String server;
	private String nick;
	private boolean alive;
	
	private Socket connection;
	private DataOutputStream dout;
	private DataInputStream din;
	private InputBin ib;
	
	public ChatLink(String s, String cn, String n) {
		server = s;
		channelName = cn;
		nick = n;
		
		try {
			connection = new Socket(server, 6667);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			dout = new DataOutputStream(connection.getOutputStream());
			din = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			System.err.println("ERROR: Could not create streams.");
		}
		ib = new InputBin(din);
		ib.start();
	}
	
	public void send(InstrumentData data) {
		//convert InstrumentData fields to text here, and send them in one or two lines
		//sendMessage("---------------------------------");
		sendMessage("alt", "Altitude: "+data.getAltitude()+", Alt. AGL: "+data.getAltitudeAGL());
		sendMessage("speed", "Mach: "+data.getMachRatio()+", True Speed: "+data.getTrueSpeedM()+", Vert. Speed: "+data.getVertSpeed());
		sendMessage("G", "G-normal: "+data.getGnormal()+", G-axial: "+data.getGaxial()+", G-lateral: "+data.getGside());
		sendMessage("angle", "Pitch: "+data.getPitch()+", Roll: "+data.getRoll()+", Heading: "+data.getTrueHeading());
	}
	
	public void startIt() {
		sendCommand("NICK "+nick);
		sendCommand("USER xplanebot exotherm nic xpb");
		while (!ib.isLoggedIn()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				//nothing...
			}
		}
		sendCommand("OPER steward password");
		sendCommand("JOIN "+channelName+"alt");
		sendCommand("JOIN "+channelName+"speed");
		sendCommand("JOIN "+channelName+"G");
		sendCommand("JOIN "+channelName+"angle");
	}
	
	public void sendMessage(String type, String msg) {
		sendCommand("PRIVMSG "+channelName+type+" :"+msg);
	}
	
	public void sendQuit() {
		sendCommand("QUIT");
		ib.stop();
	}
	
	public void setLink(XPLink xp) {
		ib.setLink(xp);
	}
	
	private void sendCommand(String command) {
		try {
			dout.writeBytes(command+"\r\n");
		} catch (IOException e) {
			System.err.println("ERROR: Could not send command to client.");
		}
	}
	
	private String getCommand() {
		try {
			StringBuffer message = new StringBuffer();
			byte prev = 0;
			byte curr = din.readByte();
			while (!(prev=='\r' && curr=='\n')) {
				message.append((char)curr);
				prev = curr;
				curr = din.readByte();
			}
			return new String(message.deleteCharAt(message.length()-1));
		} catch (IOException e) {
			System.err.println("ERROR: Could not retrieve client message: ");
		}
		return "";
	}
	
	public void finalize() {
		sendCommand("QUIT");
		ib.stop();
	}
}
