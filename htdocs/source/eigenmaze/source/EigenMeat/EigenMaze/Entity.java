package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * Base class for any independent game world object.
 */
public class Entity implements MobileEntity, Cloneable {
	/**
	 * Position vector
	 */
  	protected Vect3d position;
	/**
	 * Velocity vector
	 */
	protected Vect3d velocity;
	/**
	 * Raw Velocity vector - tof
	 */
	protected Vect3d rawVelocity; //Game.tof-ified
	/**
	 * Forward Vector, where entity is facing
	 */
	protected Vect3d forwardVect;
	private Vect3d rotation;
	private Mesh mesh;
	private float boundingSphere;

	//death stuf
	/**
	 * Death Flag (true = dead, false = alive)
	 */
	protected boolean dead;
	/**
	 * True if entity is to die on a timer
	 */
	protected boolean timedDeath;
	/**
	 * Life span of entity
	 */
	protected long timeToDie;

	private boolean castShadow;
	
	/**
	 * Default constructor.
	 */
	public Entity() {
		position = new Vect3d(0,0,0);
		velocity = new Vect3d(0,0,0);
		rawVelocity = new Vect3d(0,0,0);
		forwardVect = new Vect3d(0,0,-1);	
		rotation = new Vect3d(0,0,0);
	
		setDead(false);
		timedDeath = false;
		setCastShadow(true);
	}
	
	/**
	 * Copy constructor.
	 */
	public Entity(Entity e) {
		position = new Vect3d(e.position);
		velocity = new Vect3d(e.velocity);
		rawVelocity = new Vect3d(e.rawVelocity);
		forwardVect = new Vect3d(e.forwardVect);	
		rotation = new Vect3d(e.rotation);
		dead = e.dead;
		timedDeath = e.timedDeath;
		timeToDie = e.timeToDie;
		mesh = e.mesh;
		boundingSphere = e.boundingSphere;
		setCastShadow(e.hasShadow());
	}
	
	/**
	 * Sets the entity's position.
	 * @param x x position
	 * @param y y position
	 * @param z z position
	 */
	public synchronized void setPosition(float x, float y, float z) {
		position.set(x,y,z);
	}

	/** 
	 * Sets the entity's position.
	 * @param p vector position
	 */
	public synchronized void setPosition(Vect3d p) {
		position.set(p);
	}
	
	/**
	 * Sets the entity's velocity.
	 * @param x x velocity
	 * @param y y velocity
	 * @param z z velocity
	 */
	public synchronized void setVelocity(float x, float y, float z) {
		velocity.set(x,y,z);
	}

	/**
	 * Sets the entity's velocity.
	 * @param v vector velocity
	 */
	public synchronized void setVelocity(Vect3d v) {
		velocity.set(v);
	}
	
	/**
	 * Get the entity's velocity. This is in units per second.
	 * @return reference to velocity vector
	 */
	public synchronized Vect3d getVelocity() {
		return velocity;
	}

	/**
	 * Get the entity's raw velocity. This is the actual change in position
	 * the entity is undergoing each frame. (velocity*time_of_frame)
	 * @return raw velocity
	 */
	public synchronized Vect3d getRawVelocity() {
		return rawVelocity;
	}
	
	/**
	 * Sets the entity's bounding sphere. The Bounding sphere is used for
	 * collisions.
	 * @param s bounding sphere
	 */
	public synchronized void setBoundingSphere(float s) {
		boundingSphere = s;
	}

	/**
	 * Get the entity's current bounding sphere.
	 * @return bounding sphere
	 */
	public synchronized float getBoundingSphere() {
		return boundingSphere;
	}
	
	/** 
	 * Sets the rotation around the Y axis of the entity.
	 * @param y rotation in degrees
	 */
	public synchronized void setYRot(float y) {
		rotation.y = y;	

		forwardVect.set(0,0,-1);
		forwardVect.rotateY(rotation.y);
	}

	/**
	 * Get the current rotation around the Y axis.
	 * @return rotation in degrees
	 */
	public synchronized float getYRot() {
		return rotation.y;
	}

	/** 
	 * Sets the rotation around the Z axis of the entity.
	 * @param z rotation in degrees
	 */
	public synchronized void setZRot(float z) {
		rotation.z = z;
	}

	/**
	 * Get the current rotation around the Z axis.
	 * @return rotation in degrees
	 */
	public synchronized float getZRot() {
		return rotation.z;	
	}
	
	/**
	 * Get the direction the entity is pointing.
	 * @return forward vector
	 */
	public synchronized Vect3d getForwardVect() {
		return forwardVect;
	}

	/**
	 * Get the entity's position.
	 * @return position vector
	 */
	public synchronized Vect3d getPosition() {
		return position;
	}
	
	/**
	 * Translate the entity.
	 * @param x x coord 
	 * @param y y coord
	 * @param z z coord
	 */
	public synchronized void translate(float x, float y, float z) {
		position.x += x;
	  	position.y += y;
	  	position.z += z;
	}

	/**
	 * Set the entity's mesh.
	 * @param m the mesh object to use
	 */
	public synchronized void setMesh(Mesh m) {
		mesh = m;
	}
	
	/**
	 * Get the entity's mesh.
	 * @return The entity's mesh.
	 */
	public synchronized Mesh getMesh() {
		return mesh;
	}

	/**
	 * Is the entity dead?
	 * @return true/false if entity is dead or alive
	 */
	public synchronized boolean isDead() {
		return dead;
	}

	/**
	 * Set the dead status of entity.
	 * @param dead true or false
	 */
	public synchronized void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * Set the time the entity should die
	 * @param life the ammount of time the entity should be alive
	 */
	public synchronized void setTimedDeath(int life) {
		timeToDie = System.currentTimeMillis()+life;
		timedDeath = true;
	}

	/**
	 * Sets whether this entity draws a shadow.
	 */
	public void setCastShadow(boolean b) {
		castShadow = b;
	}

	/**
	 * Gets whether this entity draws a shadow.
	 */
	public boolean hasShadow() {
		return castShadow;
	}
	
	/**
	 * Called when this entity dies.
	 */
	public synchronized void die() {
		//dead = true;
	}

	/**
	 * Check collision. WIP - currently handled in Physics.
	 * @param entity entity to check collision with
	 */
	public synchronized boolean checkCollision(MobileEntity entity) {
		float distance = Vect3d.getDistanceBetweenPoints(getPosition(),entity.getPosition());

		if(distance <= getBoundingSphere()+entity.getBoundingSphere())
			return true;
		
		return false;
	}
	
	/**
	 * This function is called when the entity collides with something.
	 * @param entity the entity that current entity collided with (null
	 * if collided with wall)
	 */
	public synchronized boolean collide(MobileEntity entity) {
		return true;
	}
	
	/**
	 * Entity update function. Moves entity according to velocity. Also
	 * handles the timed death if it is set.
	 */
	public synchronized void update() {
		rawVelocity.set(velocity);
		rawVelocity.mult(Game.tof);
		getPosition().add(rawVelocity);
		
		if(timedDeath && timeToDie < System.currentTimeMillis())
			setDead(true);
	}

	/**
	 * Draw the entity.
	 * @param gldraw jogl gl interface
	 */
	public synchronized void draw(GLDrawable gldraw) {
		if (mesh == null)
			return;

		GL gl = gldraw.getGL();

		gl.glPushMatrix();
		gl.glTranslatef(position.x, position.y, position.z);
		gl.glRotatef(rotation.y,0,1,0);
		gl.glRotatef(rotation.z,0,0,1);
		
		mesh.draw(gldraw);

		gl.glPopMatrix();
	}
	
	/**
	 * Draw the entity's shadow based on an indexed light source.
	 */
	public void drawShadow(GLDrawable gldraw, int light) {
		GL gl = gldraw.getGL();

		if(castShadow == true && Preferences.instance().getEntityShadows()) {
             		gl.glPushMatrix();
               		 	gl.glTranslatef(position.x, position.y, position.z);
               		 	gl.glRotatef(rotation.y,0,1,0);
               	 		gl.glRotatef(rotation.z,0,0,1);		
				if(castShadow)
					getMesh().drawShadow(gldraw,position,rotation,light);
			gl.glPopMatrix();
		}
	}
		
	
	/**
	 * Clone the entity.
	 * @see Cloneable
	 */
	public Object clone() {
		try {
			Entity myClone = (Entity)super.clone();
			myClone.position = new Vect3d(myClone.position);
			myClone.velocity = new Vect3d(myClone.velocity);
			myClone.rawVelocity = new Vect3d(myClone.rawVelocity);
			myClone.forwardVect = new Vect3d(myClone.forwardVect);
			myClone.rotation = new Vect3d(myClone.rotation);
			return myClone;
		} catch (Exception e) {
			return null;
		}
	}
}
