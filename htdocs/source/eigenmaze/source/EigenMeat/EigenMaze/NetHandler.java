package EigenMeat.EigenMaze;

import java.util.*;
import java.nio.*;

/**
 * This is the primary network subsystem. Used by both clients and the server,
 * this class interfaces the network code with the engine code.
 */

public class NetHandler {
	
	private static boolean 	isServer = false;
	private static boolean  isConnected = false;
	private Server 		eigenServer;
	private Connection 	eigenConnection;

	private Thread 		serverThread, updateThread;
	private LinkedList 	netList;
	
	private static Map 	netMap = Collections.synchronizedMap(new HashMap());
	private static List 	keyList = Collections.synchronizedList(new LinkedList());
	//private static List     updateList = Collections.synchronizedList(new LinkedList());
	 
	private static double 	lastUpdate = 0;
	public  static short 	this_ID = -1;

	/**
	 * Sets ID used by the network code to manage multiple clients.
	 * Is set when the client first connects to the server.
	 * @param id the value that id gets set to.
	 */
	public void setID(short id) {
		short temp = this_ID;
		this_ID = id;
	//	System.out.println("Entering Synchronized (netmap)");
		synchronized(netMap) {
	//		System.out.println("Entered synchronized (netmap)");
			Iterator it = (netMap.keySet()).iterator();
			while(it.hasNext()) {
				Key key = (Key) it.next();
				if(key.sourceEquals(temp)) 
					key.setSource(this_ID);
			}
			it = (netMap.entrySet()).iterator();
			while(it.hasNext()) {
				NetEntity entity = (NetEntity) ((Map.Entry)it.next()).getValue();
				if(entity.getOwnerID() == temp)
					entity.setOwnerID(id);
			}
		}
	}
		
	/**
	 * Sets up network.
	 * Determines if this will e a server, and which ip and port to use
	 * @param isServer true if this is the server, false if a client.
	 * @param ip ip that client will connect to
	 * @param port port to be used
	 */
	
	public void connect(boolean isServer, String ip, int port) {
		if(isServer) {
			this.isServer = isServer;
			final int thePort = port;
			serverThread = new Thread(
					new Runnable() {
						public void run() {
							eigenServer = new Server(thePort);
						}
					}
			);
			serverThread.start();
		} 
		else {
			eigenConnection = new Connection(this, ip, port);
		}
		isConnected = true;
		//updateThread.start();
	}
	
	/**
	 * Adds a NetEntity to the network.
	 * This will set up a NetEntity, and send it to all participants in the current game.
	 * @param entity the NetEntity to be added
	 */
	public void add(NetEntity entity) {
		short type = entity.getType();
		entity.setOwnerID(this_ID);
		Key key = new Key(this_ID, type);
		netMap.put(key, entity);
		keyList.add(key);
	//	update(entity);
	}

	/*public void remove(NetEntity entity) {
		short type = entity.getType();
		short src = entity.getOwnerID();
		Key key = getKey(src,type);
		netMap.remove(key);
		keyList.remove(key);
	}*/ 
		
		
	/**
	 * Called by server - should be package level access :/
	 */
	 void getAllData(List addList) {
		synchronized(keyList) {
			Iterator it = keyList.iterator();
			while(it.hasNext()) {
				Key key = (Key) it.next();
				if(!(((NetEntity)netMap.get(key)).getType() == 1984))
					addList.add(key.sendData());
			}			
		}
	 }
	
	 synchronized int receive(short src_ID, short type, ByteBuffer bb) {
		//System.out.println("Received ID: "+src_ID+" Type: "+type);
		// System.out.println("<<><><><><><> " + bb.limit());
		 int currentPosition;
		 if(src_ID == this_ID) 
			 return 0;
		 Key key = getKey(src_ID, type);
		 if(key != null) {
			 NetEntity entity = (NetEntity) netMap.get(key);
			 currentPosition = bb.limit()-entity.getDataSize();
			 bb.position(currentPosition);
			 if(key.isUpdated == false)
			 	entity.receiveData(bb);
			 key.isUpdated = true;
		 }
		 else {
			NetEntity entity = buildNewEntity(src_ID, type);
			currentPosition = bb.limit()-entity.getDataSize();
                        bb.position(currentPosition);
			entity.receiveData(bb);
		 }
		 bb.limit(currentPosition);
		 return currentPosition;
	 }

	 void finishedUpdateing() {
		 Iterator it = keyList.iterator();
		 while(it.hasNext()) {
			Key key = (Key) it.next();
			key.isUpdated = false;
		 }
	 }
						 
	private NetEntity buildNewEntity(short src_ID, short type) {
		Key newKey;
		NetEntity other;
		
		switch(type) {
			case 5:
			//	System.out.println("Updateing Flag");
				other = Game.flag;
				newKey = new Key(src_ID, type);
				netMap.put(newKey,other);
				newKey.isUpdated = true;
				keyList.add(newKey);
				return other;
			case 25:
				System.out.println("Updating Maze");
				other = Game.getMaze();
				return other;
			case 42:
				System.out.println("Creating a ship!");
				other = (Ship)EigenEngine.instance().getFactory().create("DefaultShip");
				((Ship)other).activate();
				other.setOwnerID(src_ID);
				newKey = new Key(src_ID, type);
				netMap.put(newKey, other);
				newKey.isUpdated = true;
				keyList.add(newKey);
				return other;
			case 43:
				System.out.println("Creating a Drone!");
				other = new Drone();
				other.setOwnerID(src_ID);
				newKey = new Key(src_ID, type);
				netMap.put(newKey,other);
				newKey.isUpdated = true;
				keyList.add(newKey);
				return other;
			case 1984:
			//	System.out.println("Its the end of the world, you know");
				return NetProjectileManager.instance();
			default:
			//	System.out.println("MalFormed Packet: " +src_ID+", "+type);
		}

		return null;
	}
	
	/**
	 * THIS CODE ISNT WORKING PARTICULARLY WELL.
 * Anyway, this updates the called entity, at least it should.
	 * The idea here, is like add, it updates the called entity across
	 * the entire network, however I am still working on this code, so
	 * use at your own risk..
	 * @param entity the NetEntity to be updated
	 */
	public static void update(NetEntity entity) {
		Key key = getKey(entity.getOwnerID(), entity.getType());
		if(this_ID != -1) {
			if(isServer) {
				Server.send(key.sendData());
			}
			else if (this_ID == entity.getOwnerID()) {
				Connection.send(key.sendData());
			}
		}
	}
	/**This is a ugly hack. This is the fastest simplest way to get 
	 * this code working. The entire framework needs to be reworked
	 * to make this not necessary, but I dont have the time or the 
	 * will to do this in the next four days.
	 */

	static Ship getShip(short source) {
		Key key = getKey(source, (short)42);
		NetEntity entity = (NetEntity) netMap.get(key);
		return (Ship) entity;
	}

	private static Key getKey(short src, short typ) {
		//synchronized(keyList) {
			Iterator i = keyList.iterator();
			while(i.hasNext()) {
				Key key = (Key) i.next();
				if(key.equals(src,typ))
					return key;
			}
		//}
		return null;
	}

	private class Key {

		private short source;
		private short type;
		public boolean isUpdated;
		
		public Key(short src, short typ) {
			source = src;
			type = typ;
			isUpdated = false;
		}
		
		public boolean equals(short src, short typ) {
			return source == src && type == typ;
		}

		public boolean sourceEquals(short src) {
			return source == src;
		}

		public void setSource(short src) {
			source = src;
		}

		public ByteBuffer append(ByteBuffer bb) {
			bb.putShort(type);
			bb.putShort(source);
			return bb;
		}
		public ByteBuffer sendData() {
			NetEntity entity = (NetEntity) netMap.get(this);
			ByteBuffer bb = entity.getData();
			bb.putShort(type);
			bb.putShort(source);
			return bb;
		}
	}
}
