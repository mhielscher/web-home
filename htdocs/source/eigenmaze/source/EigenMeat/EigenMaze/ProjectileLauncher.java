package EigenMeat.EigenMaze;

/**
 * Projectile Launcher object
 */
public class ProjectileLauncher implements Cloneable {
	private float speed; 
	private MobileEntity entity;
	private int shotDelay;
	private long lastShot;
	private int lifeTime;
	private String projectile;

	/**
	 * Constructor.
	 * @param e the owner of the launcher
	 * @param speed initial speed of bullets
	 * @param delay the delay between shots
	 * @param life the life of the bullet
	 */
	public ProjectileLauncher(MobileEntity e, float speed, int delay, int life) {
		entity = e;
		setSpeed(speed);
		setShotDelay(delay);
		setLifeTime(life);
		projectile = null;
		
		lastShot = 0;
	}

	/**
	 * Set the speed of the bullets
	 * @param speed bullet speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;	
	}

	/**
	 * Set delay between shots.
	 * @param shotDelay delay between shots
	 */
	public void setShotDelay(int shotDelay) {
		this.shotDelay = shotDelay;	
	}

	/**
	 * Set life.
	 * @param life the life of the bullet
	 */
	public void setLifeTime(int life) {
		this.lifeTime = life;
	}
	
	/**
	 * Set the projectile this launcher will fire.
	 * @param projname the softloaded name of the Projectile to be fired.
	 */
	public void setProjectile(String projname) {
		projectile = projname;
	}
	
	/**
	 * Set the entity this launcher is attached to.
	 * @param e the entity to attach to.
	 */
	public void setEntity(MobileEntity e) {
		entity = e;
	}

	/**
	 * This function will shoot a bullet
	 */
	public void shoot(Projectile proj) {
		if((lastShot + shotDelay) < System.currentTimeMillis()) {
			Vect3d tmp = new Vect3d(entity.getForwardVect());
			tmp.normalize();
			tmp.mult(speed);
			tmp.add(entity.getVelocity());

			//buffers: HACK ALERT
			//we need to move the bullet's position outside
			//of the ship's bounding sphere so it doesn't collide
			//with it.
			Vect3d pos = new Vect3d(0,0,-1);
			pos.rotateY(entity.getYRot());
			pos.mult((float)(entity.getBoundingSphere()+proj.getBoundingSphere()*2+entity.getRawVelocity().length()*2));
			pos.add(entity.getPosition());
			
			proj.setPosition(pos);
			proj.setVelocity(tmp);
			proj.setTimedDeath(lifeTime);
			EigenEngine.instance().add(proj);
			
			lastShot = System.currentTimeMillis();
		}
	}
	
	/**
	 * Fires the default Projectile, if any.
	 * @see #setProjectile(String)
	 */
	public void shoot() {
		try {
			if (projectile != null)
				shoot((Projectile)EigenEngine.instance().getFactory().create(projectile));
		} catch (Exception e) {
			//don't shoot
		}
	}
	
	/**
	 * Clones this ProjectileLauncher.
	 * @see Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone(); //nothing to deep clone (DON"T deep clone the entity!)
		} catch (Exception e) {return null;}
	}
}
