package EigenMeat.EigenMaze;

import java.util.*;
import java.nio.*;

/**
 * Basic class for any projectiles.
 */
public class Projectile extends MassEntity {//implements NetEntity {
	protected ParticleEffect peffect;

	private float damage;
	private int owner_id;
	protected boolean bounces;
	protected boolean weathervane;
	protected float blastRadius;
	
	//Net stuff
	 protected final short BYTE_LENGTH = (short)4096;
	 protected ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
	 protected short netID = (short)-1;
			 
	/**
	 * Default constructor.
	 */
	public Projectile() {
		init();
	}

	/**
	 * Set the projectile's damage.
	 * @param damage damage
	 */
	public void setDamage(float damage) {
		this.damage = damage;
	}

	/**
	 * Get the damage the projectile can inflict
	 * @return damage
	 */
	public float getDamage() {
		return damage;
	}

	/**
	 * Set the owner id of the projectile.
	 * @param id owner id
	 */
//	public void setOwnerID(int id) {
//		owner_id = id;
//	}

	/**
	 * Get the owner id of the projectile.
	 * @return owner id
	 */
//	public int getOwnerID() {
//		return owner_id;
//	}
	
	private void init() {
		setMesh(MeshLoader.createBox("Projectile",.2f,.2f,.2f,"data/textures/goo.png")); //default bland projectile cube
		setBoundingSphere(.3f);	
		setMass(15);
		setDamage(10);
		
		bounces = false;
		weathervane = true;
		blastRadius = 0;
		
		peffect = new ParticleEffect();
		peffect.setSpeedRange(10,30);
		peffect.setColorRange(0f,0f,.4f,0f,.8f,.8f);
		peffect.setSize(60);
		peffect.setLifeRange(300,600);
		peffect.setScaleRange(.25f, 1f);
	}
	
	/**
	 * Update function
	 */
	public void update() {
		EigenEngine.instance().checkCollision(this);
		if (weathervane) {
			float angle = velocity.getAngle(new Vect3d(1,0,0));
			if (velocity.getAngle(new Vect3d(0,0,1))<90)
				angle *= -1;
			setYRot(angle);
		}
		
		super.update();
	}

	/**
	 * Called when the Projectile dies, either by impact or old age.
	 */
	public void die() {
		peffect.setPosition(getPosition());
		EigenEngine.instance().add(peffect);

		//handle bomb blast - messy but it works
		if (blastRadius > 0) {
			Vector v = EigenEngine.instance().getEntities(getPosition(),blastRadius);
			for(int i=0; i < v.size(); i++) {
				MobileEntity e = (MobileEntity)v.get(i);
				if(e != this) {
					float dis = Vect3d.getDistanceBetweenPoints(getPosition(), e.getPosition());
					Vect3d tmp = new Vect3d(e.getPosition());
					tmp.sub(getPosition());
					tmp.normalize();
					//tmp.mult((float)(20*(10.1-(10-dis))));
					tmp.mult(getMass()*(1/(dis)));
					e.getVelocity().add(tmp);

					//handle splash damage
					if(e instanceof Ship) {
						((Ship)e).setShields(((Ship)e).getShields()-getDamage()*(1/dis));
					}
				}
			}
		}
	}

	/**
	 * Called when Bomb hits something.
	 * @param entity the entity bomb collided with
	 */
	public boolean collide(MobileEntity entity) {
		if(entity != null) {
			if(entity instanceof Projectile) {
				return false; //don't collide with other Projectiles
			}
			
			if(entity instanceof Ship) {
				Ship t = (Ship)entity;
				t.setShields(t.getShields()-getDamage());
				setDead(true);
			}
		}
		if (!bounces)
			setDead(true);

		return true;
	}
	
	public short getOwnerID() {
		return netID;
	}

	public void setOwnerID(short id) {
		netID = id;
	}
	
	public short getType() {
		return (short) 7;
	}

	public short getDataSize() {
		return (short) 28;
	}
		
	public synchronized ByteBuffer getData() {
		netBuffer.clear();
		netBuffer.putFloat(position.x);
		netBuffer.putFloat(position.y);
		netBuffer.putFloat(position.z);
		netBuffer.putFloat(velocity.x);
		netBuffer.putFloat(velocity.y);
		netBuffer.putFloat(velocity.z);
		netBuffer.putFloat(getYRot());
		return netBuffer;
	}
	public synchronized void receiveData(ByteBuffer bb) {
		float x = bb.getFloat();
		float y = bb.getFloat();
		float z = bb.getFloat();
		setPosition(x,y,z);

		x = bb.getFloat();
		y = bb.getFloat();
		z = bb.getFloat();
		setVelocity(x,y,z);

		setYRot(bb.getFloat());
	}
}
