package EigenMeat.EigenMaze;

import java.nio.*;

/**
 * Flag object for multiplayer game types. The flag will stick to the first player that touches it.
 * It will drop on command, or when the player carrying it dies. It does not obey inertia.
 */
public class Flag extends Entity implements NetEntity { 
	private Ship carrier;
	private long dropTimer;
		
	//Net stuff
	protected final short BYTE_LENGTH = (short)18;
	protected ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
	protected short netID = (short)-1;
	
	/**
	 * Create a new flag.
	 */	
	public Flag() {
		super();
		carrier = null;
		dropTimer = 0;
		setMesh(MeshLoader.loadMesh("data/models/flag/flag.obj", 0.8f));
		EigenEngine.instance().add(this);
		EigenEngine.instance().addNet(this);
	}
	
	/**
	 * Create a new Flag carried by the designated Ship.
	 */
	public Flag(Ship e) {
		super();
		carrier = e;
		dropTimer = 0;
		setPosition(e.getPosition());
		setMesh(MeshLoader.loadMesh("data/models/flag/flag.obj", 0.8f));
		EigenEngine.instance().add(this);
		EigenEngine.instance().addNet(this);
	}
	
	/**
	 * Set the Ship this flag is attached to.
	 * @param e the Ship to attach to.
	 */
	public void setCarrier(Ship e) {
		carrier = (Ship)e;
	}
	
	public void update() {
		super.update();
		if (carrier != null) {
			if(!carrier.isDead()) {
				Vect3d pos = new Vect3d(carrier.getPosition());
				pos.add(new Vect3d(0.0f, 0.2f, 1.5f)); //hold it up correctly
				setPosition(pos);
				setVelocity(carrier.getVelocity());
			} 
			else {
				carrier = null;
				EigenEngine.instance().netUpdate(this);
			}
		}
		else
			setVelocity(0,0,0);
		if (dropTimer > 0)
			dropTimer -= Game.tof*1000;
	}
	
	/**
	 * Drop the flag (unattach it from the carrying Ship). The flag cannot be
	 * picked up again for 1.5 seconds.
	 */
	public void drop() {
		carrier = null;
		setVelocity(0,0,0);
		Vect3d pos = new Vect3d(getPosition());
		pos.sub(new Vect3d(0.0f, 0.2f, 1.5f)); //drop it back to the ground
		setPosition(pos);
		dropTimer = 1500;
		EigenEngine.instance().netUpdate(this);
	}
	
	public boolean collide(MobileEntity e) {
		if(carrier == null) {
			if (e instanceof Ship && dropTimer <= 0) {
				setCarrier((Ship)e);
				carrier.setFlag(this);
				EigenEngine.instance().netUpdate(this);
			}
		}
		return false;
	}
	
	public short getType() {
		return (short) 5;
	}

	public short getOwnerID() {
		return netID;
	}
	
	public void setOwnerID(short id) {
		netID = id;
	}

	public short getDataSize() {
		return (short) 14;
	}

	public ByteBuffer getData() {
		netBuffer.clear();
		netBuffer.putFloat(position.x);
		netBuffer.putFloat(position.y);
		netBuffer.putFloat(position.z);
		if(carrier != null) 
			netBuffer.putShort(carrier.getOwnerID());
		else
			netBuffer.putShort((short)-1);
		return netBuffer;
	}

	public void receiveData(ByteBuffer bb) {
		float x = bb.getFloat();
		float y = bb.getFloat();
		float z = bb.getFloat();
		setPosition(x,y,z);

		short owner = bb.getShort();
		if(owner != -1)
			carrier = NetHandler.getShip(owner);
		else {
			carrier = null;
			setVelocity(0,0,0);
			Vect3d pos = new Vect3d(getPosition());
			pos.sub(new Vect3d(0.0f, 0.2f, 1.5f)); //drop it back to the ground
			setPosition(pos);
			dropTimer = 1500;
		}
		if(NetHandler.this_ID == 0)
			EigenEngine.instance().netUpdate(this);
						
	}
}
