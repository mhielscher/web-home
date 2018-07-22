//
//  OutputConnectionHandler.java
//  ChatroomClient
//
//  Created by Matthew Hielscher on Wed Apr 14 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.net.*;
import java.io.*;

public class OutputConnectionHandler{
	private DataOutputStream output;
	
	public OutputConnectionHandler(Socket connection) {
		try {
			output = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			System.out.println("ERROR: Could not open output data stream.");
		}
	}
	
	public void login(String username, String password, String channel) {
		try {
			output.writeBytes("/login "+username+" "+password+" "+channel+"\n\r");
		} catch (IOException e) {
			System.err.println("ERROR: Could not login to the server.");
		}
	}
	
	public void say(String message) {
		try {
			output.writeBytes("/say "+message+"\n\r");
		} catch (IOException e) {
			System.err.println("ERROR: Could not write /say to output stream.");
		}
	}
	
	public void send(String command) {
		try {
			output.writeBytes(command+"\n\r");
		} catch (IOException e) {
			System.err.println("ERROR: Could not write command to output stream.");
		}
	}
}
