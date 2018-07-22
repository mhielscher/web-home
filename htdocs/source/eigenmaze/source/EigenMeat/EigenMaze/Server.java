package EigenMeat.EigenMaze;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;

/**
 * The server used to host a game.
 */

public class Server  {

	private Selector 		selector;
	private ServerSocket		ss;
	private ServerSocketChannel 	ssc;
	
	private final int	DATA_SIZE = 4096;
	
	private short		clientCount = 0;
	
	private Iterator	it;
	private boolean 	lock = false;
	
	private LinkedList	keyList;
	private ListIterator 	keyIt;
	private NetHandler 	netHandler;

	private Thread 		updateThread;
	
	private static LinkedList serverQue = new LinkedList();
	
	private static Logger log = Logger.getLogger("EigenMeat.EigenMaze.Server.log");
	private static ConsoleHandler h = new ConsoleHandler();
	static {
		h.setLevel(Level.INFO);
		log.addHandler(h);
		log.setLevel(Level.ALL);
		log.setUseParentHandlers(false);
	}
	
	/**
	 * Sends the given ByteBuffer to all connected clients.
	 * @param bb the ByteBuffer to send.
	 * @see NetHandler
	 */
	public static void send(ByteBuffer bb) {
		synchronized(serverQue) {
			Iterator it = serverQue.iterator();
			while(it.hasNext()) {
				if(bb == (ByteBuffer) it.next()) {
					return;
				}
			}
			serverQue.add(bb);
		}
	}
	
	public Server(int port) {
		keyList = new LinkedList();
		netHandler = new NetHandler();
		netHandler.setID((short)0);

		updateThread = new Thread( 
				new Runnable() {
					public void run() {
						while(true) {
							sendUpdatesToClients();
							try{Thread.sleep(2);}catch(Exception e){}
						}
					}
				}
		);
		updateThread.start();			
		init(port);
	}
	
	private void init(int port) {
		log.info("Starting server...");
		
		InetSocketAddress address = new InetSocketAddress(port);
		try {

			ssc  = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ss = ssc.socket();
			ss.bind(address);
			ssc.socket().bind(address);
			selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);

		}
		catch(IOException e) { log.severe("Fatal Error in init"); e.printStackTrace(); }
		while(true) {
			try { selector.select();}
			catch(IOException e) { e.printStackTrace();}
			
			Set selectedKeys = selector.selectedKeys();
			it = selectedKeys.iterator();
			
			while(it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();
				it.remove();
				
				int kro = key.readyOps();
				if((kro & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
					acceptKey(key);
					break;
				}

				if((kro & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					try { read(key); }
					catch(Exception e) { e.printStackTrace(); }
				}

				if((kro & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
					try { write(key); } 
					catch(Exception e) { e.printStackTrace(); }
				}
			}
		}		
	}
	
	private void acceptKey(SelectionKey key) {
		try {
			SocketChannel newClient = ((ServerSocketChannel)key.channel()).accept();
			newClient.configureBlocking(false);
			Socket sock = newClient.socket();
			sock.setKeepAlive(true);
			SelectionKey clientKey = newClient.register(selector, SelectionKey.OP_READ, key.attachment());
			
			while(lock) { System.out.println("Server Locked"); }
			clientCount++;
			log.info("New client key: " + clientCount);
			clientKey.attach(new EigenKey(DATA_SIZE,(short)-1));
			keyList.add(clientKey);
			lock = true;
		}
		catch(Exception e) { log.severe("Fatal Error in Accept Key"); e.printStackTrace(); }
	}

	private synchronized void write(SelectionKey key) throws Exception {
				
		SelectableChannel channel = key.channel();
		WritableByteChannel wbc = (WritableByteChannel) channel;
		EigenKey ek = (EigenKey)key.attachment();

		log.fine("Writing to key: "+ ek.id);
		while(ek.writeQue.size() > 0) {
			ByteBuffer bb = (ByteBuffer) (ek.writeQue.removeFirst());
			bb.flip();
			if(bb.limit() > 0) {
				short src_ID = bb.getShort(bb.limit()-2);
				int bytesWrote = 0;
				if(src_ID != ek.id) {
					bytesWrote = wbc.write(bb);
					log.fine("Wrote "+bytesWrote+" from que");
					bb.clear();
					bb.position(bytesWrote);
				}
				else {
					int limit = bb.limit();
					bb.clear();
					bb.position(limit);
				}
			}

			if(ek.id == clientCount)
				bb.clear();
		}
//		if(!(ek.writeQue.size() > 0)) 
		channel.register(selector, key.interestOps() & ~SelectionKey.OP_WRITE, key.attachment());
	}
	
	private synchronized void read(SelectionKey key) throws Exception {
		log.fine("Read");
		SelectableChannel channel = key.channel();
		ReadableByteChannel rbc = (ReadableByteChannel) channel;
		EigenKey ek = (EigenKey)key.attachment();	
		
		ByteBuffer bb = ek.bb;
		bb.clear();
		int bytesRead = rbc.read(bb);
		log.fine("Number of Bytes read: "+ bytesRead);
		if(bytesRead < 0) {
			log.info("Client probably disconnected!");
			keyList.remove(key);
			key.cancel();
			return;
		}
		bb.flip();
		if(ek.id == -1) {
			bb.clear();
			bb.putShort(clientCount);
			ek.id = clientCount;
			lock = false;
			
			channel.register(selector, key.interestOps() | SelectionKey.OP_WRITE, key.attachment());
			WritableByteChannel wbc = (WritableByteChannel) channel;
			bb.flip();
			wbc.write(ek.bb);
			channel.register(selector, key.interestOps() & ~SelectionKey.OP_WRITE, key.attachment());
			bb.clear();
			netHandler.getAllData(ek.writeQue);
			return;
		}
		
		else {
			processDataFromClient(bb);
			ek.bb.clear();
		}
		
	}

	private synchronized void processDataFromClient(ByteBuffer bb) {
		short src_ID = bb.getShort(bb.limit()-2);
		short type = bb.getShort(bb.limit()-4);
		bb.limit(bb.limit()-4);
		
		while(netHandler.receive(src_ID, type, bb) > 0) {
			src_ID = bb.getShort(bb.limit()-2);
			type = bb.getShort(bb.limit()-4);
			bb.limit(bb.limit()-4);
		}
		netHandler.finishedUpdateing();
	}

	private void sendUpdatesToClients() {
		synchronized(serverQue) {
			while(serverQue.size() > 0) {
				ByteBuffer bb = (ByteBuffer) serverQue.removeFirst();
				keyIt = keyList.listIterator();
				while(keyIt.hasNext()) {
					SelectionKey key = (SelectionKey) keyIt.next();
					EigenKey ek = (EigenKey) key.attachment();
					ek.writeQue.add(bb);
				//	System.out.println("------> "+ek.id);
					try {
						(key.channel()).register(selector, key.interestOps()|SelectionKey.OP_WRITE,key.attachment());
					} catch(Exception e) { log.severe("Fatal Error in Process"); e.printStackTrace(); }
				}
			}
		}
	}
}
