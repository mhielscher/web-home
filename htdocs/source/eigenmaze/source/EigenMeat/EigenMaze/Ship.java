package EigenMeat.EigenMaze;

import java.awt.event.*;
import java.nio.*;
import java.util.*;
import net.java.games.jogl.*;

/**
 * Ship object.
 */
public class Ship extends MassEntity implements NetEntity {
	private float turnSpeed, acceleration, maxSpeed;

	protected ParticleEffect dieExplosion, thrustParticles;
	
	//ship stats
	protected float shields;
	
	//flag
	protected Flag flag = null;
	
	//weapons
	protected Vector launchers; // of ProjectileLaunchers

	//Net stuff
	protected final short BYTE_LENGTH = (short)4096;
	protected ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
	protected short ownerID = (short)-1;
	
	//game scoring
	protected long score;
	protected String playerName;

	/**
	 * Default constructor
	 */
	public Ship() {
		//setMesh(MeshLoader.createBox("player",1,1,2,"data/ship.png"));
		setMesh(MeshLoader.loadMesh("data/models/fighter/fighter.obj", .3f));
		//EigenEngine.instance().add(this); //both moved to activate()
		//EigenEngine.instance().addNet(this);

		setBoundingSphere(1.4f);
		setPosition(4,0,4);
		
		setTurnSpeed(120);
		setAcceleration(30);
		setMaxSpeed(15);
		setShields(100);
		
		setMass(500);
		
		launchers = new Vector(2,1);
		ProjectileLauncher bulletLauncher = new ProjectileLauncher(this,30,200,3000);
		bulletLauncher.setProjectile("Bullet");
		launchers.add(bulletLauncher);
		ProjectileLauncher bombLauncher = new ProjectileLauncher(this,10,1000,10000);
		bombLauncher.setProjectile("Bomb");
		launchers.add(bombLauncher);
		
		dieExplosion = new ParticleEffect();
		dieExplosion.setSize(80);
		dieExplosion.setColorRange(.9f,0f,.2f,1f,.5f,.7f);
		dieExplosion.setSpeedRange(20,30);
		dieExplosion.setLifeRange(500,1200);
		dieExplosion.setScaleRange(.2f,1f);

		thrustParticles = new ParticleEffect();
		thrustParticles.setSize(3);
		thrustParticles.setColorRange(0f,0f,.7f,0f,.5f,1f);
		thrustParticles.setSpeedRange(2,3);
		thrustParticles.setLifeRange(100,300);
		thrustParticles.setScaleRange(.15f,.5f);
		
		score = 0;
		playerName = "Player";
	}
	
	public Ship(Ship s) {
		super(s);
		turnSpeed = s.turnSpeed;
		maxSpeed = s.maxSpeed;
		acceleration = s.acceleration;
		dieExplosion = s.dieExplosion;
		thrustParticles = s.thrustParticles;
		shields = s.shields;
		launchers = (Vector)s.launchers.clone();
		for (int i=0; i<launchers.size(); i++) {
			ProjectileLauncher pl = (ProjectileLauncher)((ProjectileLauncher)launchers.get(i)).clone();
			pl.setEntity(this);
			launchers.set(i, pl);
		}
		score = s.score;
		playerName = s.playerName;
	}
	
	/**
	 * Adds the ship to the EigenEngine.
	 */
	public void activate() {
		EigenEngine.instance().add(this);
		EigenEngine.instance().addNet(this);
	}
	
	/**
	 * Net code cludge... Such is life.
	 */
	public void shoot(int typ) {
		ProjectileLauncher pl = (ProjectileLauncher)launchers.get(typ);
		pl.setEntity(this);
		pl.shoot();
	}
  
	/**
	 * Updates the ship. Checks for collisions and make sure it's not
	 * overspeeding. Then calls super.update() to move the ship.
	 */
	public synchronized void update() {
		EigenEngine.instance().checkCollision(this);
		
		if(getVelocity().length() > getMaxSpeed()) {
			getVelocity().normalize();
			getVelocity().mult(getMaxSpeed());
		}
		
		//if (flag != null)
		//	score += Game.tof * 1000;
		//EigenEngine.instance().getScoreboard().setScore(playerName, score);
	
		if(getShields() <= 0) {
			setDead(true);
		}
		if(NetHandler.this_ID == -1 || NetHandler.this_ID == getOwnerID())
			EigenEngine.instance().netUpdate(this);
		super.update();
  	}

	/**
	 * Sets the ship turn speed.
	 * @param turnspeed in degrees
	 */
	public synchronized void setTurnSpeed(float turnspeed) {
		turnSpeed = turnspeed;
	}

	/** 
	 * Get the ship's turn speed
	 * @return the turn speed in degrees
	 */
	public synchronized float getTurnSpeed() {
		return turnSpeed;
	}

	/**
	 * Set the ship's acceleration.
	 * @param accel the acceleration
	 */
	public synchronized void setAcceleration(float accel) {
		acceleration = accel;
	}

	/**
	 * Get the ship acceleration.
	 * @return the acceleration
	 */
	public synchronized float getAcceleration() {
		return acceleration;
	}

	/**
	 * Set the ship's max speed.
	 * @param maxspeed max speed of ship
	 */
	public synchronized void setMaxSpeed(float maxspeed) {
		maxSpeed = maxspeed;
	}

	/**
	 * Get the ship's max speed.
	 * @return max speed
	 */
	public synchronized float getMaxSpeed() {
		return maxSpeed;
	}
	
	/**
	 * Gets the ship's shield status. Has range between 0 and 100.
	 * @return the ship's shield proportion.
	 */
	public synchronized float getShields() {
		//System.out.println("GETTING SHIELDS!!!!!!!! "+this);
		return shields;
	}

	public synchronized void setShields(float shields) {
		//System.out.println("SETTING SHEILDS!!!!!!!!!!!!! "+ this);
		this.shields = shields;	
	}
	
	/**
	 * Called when ship collides with something.
	 * @param entity the entity this ship collided with
	 */
	public synchronized boolean collide(MobileEntity entity) {
		super.collide(entity);
		return true;
	}
	
	/**
	 * Ship's die function. Creates die particle explosion.
	 */
	public synchronized void die() {
		dieExplosion.setPosition(getPosition());
		EigenEngine.instance().add(dieExplosion);
		if(flag != null) {
			flag.drop();
			flag = null;
		}
	}

	/**
	 * Ships set flag function. Connects a ship to a flag object.
	 * @param flag the Flag object that ship now holds.
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public long getScore() {
		return score;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String p) {
		playerName = p;
	}
	
	public short getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(short id) {
		ownerID = id;
	}

	/**
	 * Get the entity's type.
	 * @return the type 
	 */
	public short getType() {
		return (short) 42;
	}

	/**
	 * Returns the number of bytes written to the ByteBuffer.
	 */
	public short getDataSize() {
		return (short) 40;
	}

	/**
	 * Get the ship's data as a ByteBuffer.
	 * @return bytebuffer with ship data
	 */
	public synchronized ByteBuffer getData() {
		netBuffer.clear();
		netBuffer.putFloat(position.x);
		netBuffer.putFloat(position.y);
		netBuffer.putFloat(position.z);
		netBuffer.putFloat(velocity.x);
		netBuffer.putFloat(velocity.y);
		netBuffer.putFloat(velocity.z);
		netBuffer.putFloat(getYRot());
		netBuffer.putFloat(shields);
		netBuffer.putLong(score);
		return netBuffer; 
	}

	/**
	 * Set ship data according with information from a ByteBuffer.
	 * @param bb bytebuffer
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
			setYRot(bb.getFloat());
			shields = bb.getFloat();
			score = bb.getLong();
		}

		if(NetHandler.this_ID == 0)
			EigenEngine.instance().netUpdate(this);
		if(isDead()) {
			setDead(false);
			EigenEngine.instance().add(this);
		}
				
	}
}

