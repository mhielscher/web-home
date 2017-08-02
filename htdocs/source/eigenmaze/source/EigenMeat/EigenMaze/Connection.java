package EigenMeat.EigenMaze;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

/**
 * Client connection to a server.
 * This is created by NetHandlers connection method.
 * @see NetHandler
 */

public class Connection {

//	private static String ip = "192.168.1.100";/*"169.231.19.227";//*/
	private static String ip = "68.227.81.78";
	private static List writeQue = Collections.synchronizedList(new LinkedList());
	private SocketChannel sChannel;
	private final int DATA_SIZE = 4096;
	private ByteBuffer byteBuffer = ByteBuffer.allocate(DATA_SIZE);
	private Thread readThread;
	private short this_ID;
	private NetHandler netHandler;
	
	/**
	 * Sends data to server.
	 * @param bb the ByteBuffer to send.
	 */

	public static void send(ByteBuffer bb) {
		synchronized (writeQue) {
			Iterator it = writeQue.iterator();
			while(it.hasNext()) {
				if(bb == (ByteBuffer) it.next())
					return;
			}
		}
			writeQue.add(bb);
	}

	public Connection(NetHandler nh, String ip, int port) {
		netHandler = nh;
		try {
			sChannel = SocketChannel.open();
			sChannel.configureBlocking(false);
			sChannel.connect(new InetSocketAddress(ip, port));
			while(!sChannel.finishConnect()) {}
			start();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void start() throws Exception {
		byteBuffer.put((byte)0x2A);
		writeToServer(byteBuffer);
		short this_ID = getIDFromServer();
		while(this_ID == -1) {try{ Thread.sleep(5); this_ID = getIDFromServer(); } catch(Exception e){} }
		netHandler.setID(this_ID);

		System.out.println("My ID = " + this_ID);
			
		readThread = new Thread(
			new Runnable() {
				public void run() {
					while(true) {
						connectionRunning();
						try {Thread.sleep(50); } catch(Exception e){}
					}
				}
			}
		);
		readThread.start();
	}

	private void connectionRunning() {
		while(writeQue.size() > 0) {
			try {
				writeToServer((ByteBuffer)writeQue.remove(0));
			} catch(Exception e) { e.printStackTrace(); }
		}
		readFromServer();
	}

	private void readFromServer() {
		try {
			int bytesRead = getFromServer();
			if(bytesRead > 0) {
				sendToClient();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToClient() {
		
		short src_ID = byteBuffer.getShort(byteBuffer.limit()-2);
		short type = byteBuffer.getShort(byteBuffer.limit()-4);
		byteBuffer.limit(byteBuffer.limit()-4);
		
		while(netHandler.receive(src_ID, type, byteBuffer) > 0) {
			src_ID = byteBuffer.getShort(byteBuffer.limit()-2);
			type = byteBuffer.getShort(byteBuffer.limit()-4);
			byteBuffer.limit(byteBuffer.limit()-4);
		}
		netHandler.finishedUpdateing();
	}

	private short getIDFromServer() throws Exception {
		
		byteBuffer.clear();
		int bytesRead = sChannel.read(byteBuffer);
		short returnValue = (short)-1;
		
		if(bytesRead > 0) {
			byteBuffer.flip();
			returnValue = byteBuffer.getShort();
			while(byteBuffer.hasRemaining()) {
				sendToClient();
			}
		}
		return returnValue;
	}
				

	private int getFromServer() throws Exception {
		byteBuffer.clear();
		int bytesRead = sChannel.read(byteBuffer);
		if(bytesRead < 0) {
			System.out.println("Server proabably died");
			return 0;
		}
		
		byteBuffer.flip();
		return bytesRead;

	}

	private void writeToServer(ByteBuffer bb) throws Exception {
		
		bb.flip();
		int bytesWrote = sChannel.write(bb);
		bb.clear();
	}
}
