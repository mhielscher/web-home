//
//  XPReader.java
//  XPReader
//
//  Created by Matthew Hielscher on Fri Jul 2 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.util.*;

public class XPReader {

    public static void main (String args[]) {
    	String server = "nic";
		String channel = "#missioncontrol";
		String nick = "steward";
		System.out.println(args[0]);
		if (args.length > 0)
			server = args[0];
		if (args.length > 1)
			channel = args[1];
		if (args.length > 2)
			nick = args[2];
			ChatLink mainLink = new ChatLink(server, channel, nick);
		mainLink.startIt();
		/*
		byte[] buffer = new byte[42];
		for (int i=0; i<42; i++)
			buffer[i] = 0;
		buffer[13] = 30;
		buffer[17] = 112;
		buffer[24] = (5432 & 0xff00) >> 8;
		buffer[25] = (5432 & 0xff);
		buffer[32] = (6732 & 0xff00) >> 8;
		buffer[33] = (6732 & 0xff);
		InstrumentData data = new InstrumentData(buffer, 42);
		mainLink.send(data);*/
		
		XPLink rec = new XPLink(mainLink);
		rec.start();
		mainLink.setLink(rec);
    }
}
