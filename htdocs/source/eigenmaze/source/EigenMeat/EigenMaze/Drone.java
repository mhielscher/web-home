package EigenMeat.EigenMaze;

import java.nio.*;

/**
 * Drone ship.
 */
class Drone extends Ship {
	        
	private final short BYTE_LENGTH = (short)256;
	private ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
			
	/** 
	 * Default Constructor.
	 */
	public Drone() {
		super((Ship)EigenEngine.instance().getFactory().create("DefaultShip"));
		//setShields(100);
		this.activate();
		setPlayerName("Drone");
//		EigenEngine.instance().addNet(this);
	}

	/**
	 * Drone's update function.
	 */
	public void update() {
		setYRot(getYRot()+90*Game.tof);
		super.update();
	}

	public void respawn() {
		setShields(100);
		setDead(false);
		EigenEngine.instance().add(this);
	}

	/**
	 * Collide function/
	 */
	public boolean collide(MobileEntity entity) {
		return true;
	}

	/**
	 * Get the entity type.
	 * @return the entity type
	 */
	public short getType() {
		return (short) 43;
	}

	/**
	 * Returns the size in bytes of the ByteBuffer.
	 */
	public short getDataSize() {
		return (short) 28;
	}

	/**
	 * Get data to send.
	 * @return data ByteBuffer
	 */
	public synchronized ByteBuffer getData() {
		netBuffer.clear();
		netBuffer.putFloat(position.x);
		netBuffer.putFloat(position.y);
		netBuffer.putFloat(position.z);
		netBuffer.putFloat(velocity.x);
		netBuffer.putFloat(velocity.y);
		netBuffer.putFloat(velocity.z);
		netBuffer.putFloat(shields);
		return netBuffer;
	}
	/**
	 * Process net data.
	 * @param bb bytebuffer to read
	 */
	public void receiveData(ByteBuffer bb) {
		synchronized(this) {
			float x = bb.getFloat();
			float y = bb.getFloat();
			float z = bb.getFloat();
			setPosition(x,y,z);
			
			x = bb.getFloat();
			y = bb.getFloat();
			z = bb.getFloat();
			setVelocity(x,y,z);

			shields = bb.getFloat();
		}
		
		if(NetHandler.this_ID == 0)
			EigenEngine.instance().netUpdate(this);
		if(isDead()) {
			setDead(false);
			EigenEngine.instance().add(this);
		}
	}
		
}
