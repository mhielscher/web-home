//
//  XPLink.java
//  XPReader
//
//  Created by Matthew Hielscher on Sat Apr 17 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

import java.io.*;
import java.net.*;
import java.util.*;

public class XPLink extends Thread {

	private SocketAddress ip; //only using this because you can't really convert from SocketAddress to String
	private boolean alive;
	private ChatLink chat;
	
	private DatagramSocket socket;
	private DatagramSocket outgoing;
	
	public XPLink(ChatLink ch) {
		chat = ch;
		try {
			socket = new DatagramSocket(48000);
			outgoing = new DatagramSocket(49000);
		} catch (Exception e) {}
	}
	
	public void run() {
		ip = null;
		alive = true;
		byte[] buffer = new byte[2100];
		for (int i=0; i<2100; i++)
			buffer[i] = 0;
		long time = System.currentTimeMillis();
		while (alive) {
			try {
				DatagramPacket data = new DatagramPacket(buffer, 2100);
				socket.receive(data);
				if (ip == null)
					ip = data.getSocketAddress();
				buffer = data.getData();
				if (System.currentTimeMillis()-time > 4000) {
					//System.out.println("Got packet.");
					InstrumentData info = new InstrumentData(buffer, data.getLength(), data.getOffset());
					chat.send(info);
					time = System.currentTimeMillis();
				}
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void sendMessage(String content) {
		//System.out.println("Sending... "+content);
	
		byte[] buffer = new byte[content.length()+22];
		buffer[0] = 'M';
		buffer[1] = 'E';
		buffer[2] = 'S';
		buffer[3] = 'S';
		buffer[4] = 0;
		buffer[5] = 0;
		byte[] str = content.getBytes();
		int len = str.length;
		for (int i=0; i<len; i++)
			buffer[i+5] = str[i];
		buffer[len+5] = 0;
		
		try {
			DatagramPacket data = new DatagramPacket(buffer, buffer.length, ip);
			outgoing.send(data);
		} catch (Exception e) {
			System.out.println("Could not send cockpit message.");
			e.printStackTrace();
		}
	}
}
