//
//  InputConnectionHandler.java
//  ChatroomClient
//
//  Created by Matthew Hielscher on Thu Apr 15 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Time;

import javax.swing.*;
import java.awt.*;

public class InputConnectionHandler extends Thread {
	private ChatroomClient parent;
	private DataInputStream input;
	private JTextArea chatBox;
	private boolean useTimestamps;
	private Toolkit beepKit;
	private String username;
	private boolean beepOnMessage;
	private int errorCount;
	
	public InputConnectionHandler(ChatroomClient p, Socket connection, JTextArea box, Toolkit tk, String user) {
		parent = p;
		chatBox = box;
		beepKit = tk;
		username = user;
		useTimestamps = true;
		beepOnMessage = false;
		errorCount = 0;
		try {
			input = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			System.err.println("ERROR: Could not open input data stream.");
		}
	}
	
	public void run() {
		while (true) {
			String sMessage = getServerMessage();
			if (sMessage.equals("/\n")) continue;
			String timeStamp = "";
			if (useTimestamps)
				timeStamp = "["+(new Time(System.currentTimeMillis())).toString()+"] ";
			if (sMessage.startsWith("/server")) {
				//chatBox.setForeground(Color.red);
				StringTokenizer st = new StringTokenizer(sMessage.substring(8), "\n");
				while (st.hasMoreTokens())
					chatBox.append(timeStamp+" "+st.nextToken()+"\n");
			}
			else if (sMessage.startsWith("/user")) {
				chatBox.append(timeStamp);
				StringTokenizer st = new StringTokenizer(sMessage, " ");
				st.nextToken();
				//chatBox.setForeground(Color.blue);
				String name = st.nextToken();
				chatBox.append("<"+name+">");
				//chatBox.setForeground(Color.black);
				while (st.hasMoreTokens())
					chatBox.append(" "+st.nextToken());
				if (beepOnMessage && !name.equals(username))
					beepKit.beep();
			}
			else if (sMessage.startsWith("/emote")) {
				chatBox.append(timeStamp);
				StringTokenizer st = new StringTokenizer(sMessage, " ");
				st.nextToken();
				String name = st.nextToken();
				if (beepOnMessage && !name.equals(username))
					beepKit.beep();
				//chatBox.setForeground(Color.blue);
				chatBox.append("* "+name);
				//chatBox.setForeground(Color.black);
				while (st.hasMoreTokens())
					chatBox.append(" "+st.nextToken());
			}
			else if (sMessage.startsWith("/msgto")) {
				chatBox.append(timeStamp);
				StringTokenizer st = new StringTokenizer(sMessage, " ");
				st.nextToken();
				//chatBox.setForeground(Color.blue);
				chatBox.append("<To: "+st.nextToken()+">");
				//chatBox.setForeground(Color.black);
				while (st.hasMoreTokens())
					chatBox.append(" "+st.nextToken());
			}
			else if (sMessage.startsWith("/msgfrom")) {
				if (beepOnMessage)
					beepKit.beep();
				chatBox.append(timeStamp);
				StringTokenizer st = new StringTokenizer(sMessage, " ");
				st.nextToken();
				//chatBox.setForeground(Color.blue);
				chatBox.append("<From: "+st.nextToken()+">");
				//chatBox.setForeground(Color.black);
				while (st.hasMoreTokens())
					chatBox.append(" "+st.nextToken());
			}
			else if (sMessage.startsWith("/awayfrom")) {
				chatBox.append(timeStamp);
				StringTokenizer st = new StringTokenizer(sMessage, " ");
				st.nextToken();
				//chatBox.setForeground(Color.blue);
				chatBox.append("* "+st.nextToken()+" is away: ");
				//chatBox.setForeground(Color.black);
				while (st.hasMoreTokens())
					chatBox.append(" "+st.nextToken());
			}
			else if (sMessage.startsWith("/broadcast")) {
				if (beepOnMessage)
					beepKit.beep();
				chatBox.append(timeStamp);
				chatBox.append(" --- "+sMessage.substring(11));
			}
			else if (!sMessage.equals("")) {
				chatBox.append(timeStamp);
				chatBox.append(sMessage);
			}
			chatBox.setCaretPosition(chatBox.getText().length());
			
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}
	
	public void toggleTimestamps() {
		useTimestamps = !useTimestamps;
	}
	
	public void toggleBeep() {
		beepOnMessage = !beepOnMessage;
	}
	
	private String getServerMessage() {
		try {
			StringBuffer message = new StringBuffer();
			byte prev = 0;
			byte curr = input.readByte();
			while (!(prev=='\n' && curr=='\r')) {
				message.append((char)curr);
				prev = curr;
				curr = input.readByte();
			}
			errorCount = 0;
			return new String(message);
		} catch (IOException e) {
			System.err.println("ERROR: Could not retrieve server message.");
			errorCount++;
			if (errorCount > 10) {
				parent.disconnect();
				stop();
			}
		}
		return "";
	}
}