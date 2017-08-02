//
//  GameServer.java
//  GameServer
//
//  Created by Matthew Hielscher on Mon Sep 27 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
import java.util.*;

public class GameServer {

    public static void main (String args[]) {
       Arena gameSpace = new Arena();
	   (new Thread(gameSpace)).start();
    }
}
