package EigenMeat.EigenMaze;

import java.awt.Color;

/**
 * An effect consisting of particles.
 */
public class ParticleEffect implements Cloneable {
	private Vect3d pos; 
	private int numberOfParticles;
	private float minSpeed,maxSpeed;
	private Color minColor;
	private Color maxColor;
	private long minLife, maxLife;
	private Vect3d gravity;
	private float minScale,maxScale;

	private Vect3d base_velocity;
	
	/**
	 * Default Constructor
	 */
	public ParticleEffect(){
		pos = new Vect3d(0,0,0);
		gravity = new Vect3d(0,0,0);
		base_velocity = new Vect3d(0,0,0);
	}

	public void setBaseVelocity(Vect3d base) {
		base_velocity.set(base);
	}
	
	/**
	 * Set the location of the effect.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void setPosition(float x, float y, float z) {
		pos.set(x,y,z);
	}

	/**
	 * Set the location of the effect.
	 * @param pos vector position
	 */
	public void setPosition(Vect3d pos) {
		this.pos.set(pos);
	}

	/**
	 * Set the minimum speed of a particle in the effect.
	 * @param min minimum speed
	 */
	public void setMinSpeed(float min) {
		minSpeed = min;
	}

	/**
	 * Set the maximum speed of a particle in the effect.
	 * @param max max speed
	 */
	public void setMaxSpeed(float max) {
		maxSpeed = max;
	}

	/**
	 * Set the speed range of a particle in the effect.
	 * @param min min speed 
	 * @param max max speed
	 */
	public void setSpeedRange(float min, float max) {
		setMinSpeed(min);
		setMaxSpeed(max);
	}

	/**
	 * Set the number of particles in the effect.
	 * @param num number of particles in the effect
	 */
	public void setSize(int num) {
		numberOfParticles = num;
	}

	/**
	 * Set the maximum RGB color.
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public void setMaxColor(float r, float g, float b) {
		maxColor = new Color(r,g,b);
	}
	
	/**
	 * Sets the minimum RGB color.
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public void setMinColor(float r, float g, float b) {
		minColor = new Color(r,g,b);
	}

	/**
	 * Sets the minimum and maximum RGB color.
	 * @param r1 min r
	 * @param g1 min g
	 * @param b1 min b
	 * @param r2 max r
	 * @param g2 max g
	 * @param b2 max b
	 */
	public void setColorRange(float r1, float g1, float b1, float r2, float g2, float b2) {
		setMinColor(r1,g1,b1);
		setMaxColor(r2,g2,b2);
	}

	/**
	 * Set the maximum life of a particle in the effect.
	 * @param max maximum life
	 */
	public void setMaxLife(long max) {
		maxLife = max;
	}

	/**
	 * Set the minimum life of a particle in the effect.
	 * @param min minimum life
	 */
	public void setMinLife(long min) {
		minLife = min;
	}

	/**
	 * Set the min and max life of a particle in the effect.
	 * @param min minimum life
	 * @param max maximum life
	 */
	public void setLifeRange(long min, long max) {
		setMinLife(min);
		setMaxLife(max);
	}

	/**
	 * Set the gravity vector of the effect.
	 * @param x x component of gravity
	 * @param y y component of gravity
	 * @param z z component of gravity
	 */
	public void setGravity(float x, float y, float z) {
		gravity.set(x,y,z);
	}

	/**
	 * Set the scale range of the particles in the effect.
	 * @param min min scale factor
	 * @param max max scale factor
	 */
	public void setScaleRange(float min, float max) {
		minScale = min;
		maxScale = max;
	}

	/**
	 * Get number of particles in the effect.
	 * @return number of particles
	 */
	public int getSize() {
		return numberOfParticles;
	}

	/**
	 * Get new color in the correct RGB range.
	 * @return new color
	 */
	public Color getNewColor() {
		Color tmp = new Color( 
			(minColor.getRed() + (maxColor.getRed()-minColor.getRed())*(float)Math.random())/255,
			(minColor.getGreen() + (maxColor.getGreen()-minColor.getGreen())*(float)Math.random())/255,
			(minColor.getBlue() + (maxColor.getBlue()-minColor.getBlue())*(float)Math.random())/255);
		return tmp;
	}

	/**
	 * Get new speed in the correct color range.
	 * @return new speed
	 */
	public float getNewSpeed() {
		return (minSpeed + (float)((maxSpeed - minSpeed)*Math.random()));
	}

	/**
	 * Get new particle life in the correct life range.
	 * @return new life time
	 */
	public long getNewLife() {
		return (minLife + (long)((maxLife - minLife)*Math.random()));
	}

	/**
	 * Gets new scale in the correct range.
	 * @return new scale
	 */
	public float getNewScale() {
		return (minScale + (float)((maxScale - minScale)*Math.random()));
	}
	
	/**
	 * create a new particle that has random stats in the given ranges 
	 * @return a new particle
	 */
	public Particle getNewParticle() {
		Particle tmp = new ParticleSphere();
		Vect3d vel = new Vect3d(Math.random()*2,Math.random()*2,Math.random()*2);
		vel.sub(new Vect3d(1,1,1));
		vel.normalize();
		vel.mult(getNewSpeed());

		Vect3d tmpv = new Vect3d(vel);
		tmpv.add(base_velocity);
		
		tmp.setVelocity(tmpv);
		tmp.setPosition(pos.x,pos.y,pos.z);
		tmp.setColor(getNewColor());
		tmp.setTimeToDie(getNewLife()+System.currentTimeMillis());
		tmp.setGravity(gravity.x,gravity.y,gravity.z);
		tmp.setScale(getNewScale());
		
		return tmp;
	}
	
	public Object clone() {
		try {
			ParticleEffect myClone = (ParticleEffect)super.clone();
			myClone.pos = new Vect3d(myClone.pos);
			myClone.gravity = new Vect3d(myClone.gravity);
			return myClone;
		} catch (Exception e) {return null;}
	}
}	
