package EigenMeat.EigenMaze;

import java.nio.*;

	/**
         * This object manages projectiles across a net game.
         * Indeed, that is its purpose in life, and in life, that is its purpose.
         */
	
public class NetProjectileManager implements NetEntity {
	
	private static final NetProjectileManager instance = new NetProjectileManager();
	protected final short BYTE_LENGTH = (short)4096;
	protected ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
	protected short netID = (short)-1;
	protected short launcherNumber;
	protected short launcherSource;

	/**
	 * Gets the single existing instance of the projectile manager
	 */
	public static NetProjectileManager instance() {
		return instance;
	}

	private NetProjectileManager() {
		EigenEngine.instance().addNet(this);
	}
	/**
	 * Client side call to fire a projectile type.
	 */

	public synchronized void fire(short typ) {
		launcherNumber = typ;
		launcherSource = NetHandler.this_ID;
		EigenEngine.instance().netUpdate(this);
	}
	/**
	 * Server side call to fire a projectile type.
	 */

	public synchronized void fire(short num, short src) {
		launcherNumber = num;
		launcherSource = src;
		EigenEngine.instance().netUpdate(this);
	}

	/**
	 * @see NetEntity
	 */
	public short getOwnerID() {
		return netID;
	}

	/**
	 * @see NetEntity
	 */
	public void setOwnerID(short id) {
		netID = id;
	}	

	/**
	 * @see NetEntity
	 */
	public short getType() {
		return (short) 1984;					        	
	}

	/**
	 * @see NetEntity
	 */
	public short getDataSize() {
		return (short) 4;
	}

	/**
	 * @see NetEntity
	 */
	public synchronized ByteBuffer getData() {
		netBuffer.clear();
		netBuffer.putShort(launcherSource);
		netBuffer.putShort(launcherNumber);
		return netBuffer;
	}

	/**
	 * @see NetEntity
	 */
	public synchronized void receiveData(ByteBuffer bb) {
		short src = bb.getShort();
		short num = bb.getShort();
		if(src != NetHandler.this_ID) {
			Ship ship = NetHandler.getShip(src);
			ship.shoot((int)num);
		}
		if(NetHandler.this_ID == 0 && src != 0)//bypasses forward checking! 
			fire(num, src);
	}
}
