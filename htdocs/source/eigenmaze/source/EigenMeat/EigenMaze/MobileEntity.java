package EigenMeat.EigenMaze;

/**
 * Classifies an object as a movable game entity.
 */
public interface MobileEntity extends Drawable {
	
	/**
	 * Updates the entity's statistics based on each time step update.
	 */
	public void update();
	
	/**
	 * Gets the radius of this entity' bounding sphere.
	 * @return the radius of the bounding sphere.
	 */
	public float getBoundingSphere();
	
	/**
	 * Gets the position of this entity.
	 * @return the position, in Vect3d form.
	 */
	public Vect3d getPosition();
	
	/**
	 * Sets the position of this entity.
	 * @param pos the position, in Vect3d form.
	 */
	public void setPosition(Vect3d pos);
	
	/**
	 * Gets the velocity of this entity.
	 * @return the velocity, in Vect3d form.
	 */
	public Vect3d getVelocity();
	
	/**
	 * Sets the velocity of this entity.
	 * @param vel the velocity, in Vect3d form.
	 */
	public void setVelocity(Vect3d vel);
	
	/**
	 * Gets the vector of the direction this entity is facing.
	 * @return the direction vector.
	 */
	public Vect3d getForwardVect();
	
	/**
	 * Gets the raw velocity vector. This vector is the time-step-based vector of movement.
	 * @return the raw velocity vector.
	 */
	public Vect3d getRawVelocity();
	
	/**
	 * Gets the facing angle about the Y axis of this entity.
	 * @return the facing angle with respect to [1,0,0] about the Y axis.
	 */
	public float getYRot();
	
	/**
	 * Sets the facing angle about the Y axis of this entity.
	 * @return the facing angle with respect to [1,0,0] about the Y axis.
	 */
	public void setYRot(float r);
	
	//die stuff
	/**
	 * Tests if this entity is dead.
	 * @return true if the entity is dead; false otherwise.
	 */
	public boolean isDead();
	
	/**
	 * Sets whether this entity is dead.
	 * @param dead the state of deadness the apply.
	 */
	public void setDead(boolean dead);
	
	/**
	 * Sets the death timer for this entity.
	 * @param life the lifetime in milliseconds to set the timer to.
	 */
	public void setTimedDeath(int life);
	
	/**
	 * Kills this entity. The entity's specific death actions will be taken, and the entity will
	 * cease to exist in the game world.
	 */
	public void die();

	//collision
	/**
	 * Check this entity for collisions with another movable entity. This is currently deprecated.
	 * @param entity the entity to check collision against.
	 * @return true if this entity will collide with the other entity during the next update;
	 *		   false otherwise.
	 * @see Physics#checkSphereCollision(Vect3d, float, Vect3d, float)
	 */
	public boolean checkCollision(MobileEntity entity);
	
	/**
	 * This function is called when the entity collides with something.
	 * @param entity the entity that current entity collided with (null
	 * if collided with wall)
	 */
	public boolean collide(MobileEntity entity);
}
