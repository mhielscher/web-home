//
//  NetworkOutputHandler.java
//  GameMain
//
//  Created by Matthew Hielscher on Wed Sep 29 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.net.*;
import java.io.*;

public class NetworkOutputHandler {

	private Socket connection; // the TCP connection to the client
	private DataOutputStream stream; // the stream of action byte codes
	
	public NetworkOutputHandler(Socket s) {
		if (s == null)
			return;
		connection = s;
		try {
			stream = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			System.err.println("Output handler for "+s.getInetAddress()+" could not open a stream.");
			System.err.println(e);
		}
	}
	
	public void sendName(String name) {
		try {
			byte[] n = name.getBytes();
			stream.write(n, 0, n.length);
			stream.writeByte((byte)('\0'));
		} catch (IOException e) {
			System.err.println(e);
		}
		System.out.println("Sent name.");
	}
	
	public void sendCode(byte code, int direction) {
		try {
			stream.writeByte(code);
			stream.writeByte((byte)direction);
			//System.out.println("Sent action "+code+", "+direction);
		} catch (IOException e) {
			System.err.println("An I/O error occured while writing actions to "+connection.getInetAddress());
			System.err.println(e);
		}
	}
	
}
