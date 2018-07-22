package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import java.awt.Color;

/**
 * A single Particle for the particle engine.
 */
public class Particle {
	protected Vect3d position;
	protected float xvel, yvel, zvel;
	protected float xrot, yrot, zrot;
	protected float xgrav, ygrav, zgrav;
	protected float r,g,b;
	protected long timeToDie;
	protected float scale;

	/**
	 * Default constructor.
	 */
	public Particle() {
		position = new Vect3d();
		
		setRotation((float)Math.random()*360,(float)Math.random()*360,(float)Math.random()*360);
		setPosition(0,0,0);
		setColor(new Color(0f,0f,0.2f));
		setVelocity(0,0,0);
		setGravity(0,0,0);
		setScale(1);
	}

	/**
	 * Sets the scale of the particle.
	 * @param scale scale factor
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Get the particle's scale.
	 * @return scale
	 */
	public float getScale() {
		return scale;
	}
	
	/**
	 * Sets the particle's position.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void setPosition(float x,float y,float z) {
		position.set(x,y,z);
	}

	/**
	 * Get the particle's position
	 * @return Vect3d position
	 */
	public Vect3d getPosition() {
		return position;
	}
	
	/**
	 * Sets the particle's rotation.
	 * @param xr x rotation
	 * @param yr y rotation
	 * @param zr z rotation
	 */
	public void setRotation(float xr, float yr, float zr) {
		xrot = xr;
		yrot = yr;
		zrot = zr;
	}

	/**
	 * Sets the particle's velocity
	 * @param xvel x velocity
	 * @param yvel y velocity
	 * @param zvel z velocity
	 */ 
	public void setVelocity(float xvel, float yvel, float zvel) {
		this.xvel = xvel;
		this.yvel = yvel;
		this.zvel = zvel;
	}

	/**
	 * Sets the particle's velocity.
	 * @param v vect3d velocity
	 */
	public void setVelocity(Vect3d v) {
		this.xvel = v.x;
		this.yvel = v.y;
		this.zvel = v.z;
	}
	
	/**
	 * Set particle's color.
	 * @param c color
	 */
	public void setColor(Color c) {
		this.r = c.getRed();
		this.b = c.getBlue();
		this.g = c.getGreen();
	}
	
	/**
	 * The the time of particle's death.
	 * @param time time of death
	 */
	public void setTimeToDie(long time) {
		timeToDie = time;	
	}

	/**
	 * Get the particle's time to die.
	 * @return time to die of particle
	 */
	public long getTimeToDie() {
		return timeToDie;
	}

	/**
	 * Set the gravity vector of the particle.
	 * @param x x component of gravity vector
	 * @param y y component of gravity vector
	 * @param z z component of gravity vector
	 */
	public void setGravity(float x, float y, float z) {
		xgrav = x;
		ygrav = y;
		zgrav = z;
	}
		
	/**
	 * Process the particle.
	 */
	public void process() {
		position.x += xvel*Game.tof;
		position.y += yvel*Game.tof;
		position.z += zvel*Game.tof;

		xvel += xgrav*Game.tof;
		yvel += ygrav*Game.tof;
		zvel += zgrav*Game.tof;
	}

	/**
	 * Draw the particle.
	 * @param gldraw jogl gl interface
	 */
	public void draw(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		gl.glPushMatrix();
	
		gl.glTranslatef(position.x, position.y, position.z);
		gl.glRotatef(xrot,1,0,0);
		gl.glRotatef(yrot,0,1,0);
		gl.glRotatef(zrot,0,0,1);

		gl.glScalef(scale,scale,scale);
	
		gl.glDisable(GL.GL_LIGHTING);
		
		gl.glBegin(GL.GL_TRIANGLES);
			gl.glColor3f(r/255.0f,g/255.0f,b/255.0f);
			gl.glVertex3f(0.0f,0.0f,0.0f);
			gl.glVertex3f(0.0f,1f,0.0f);
			gl.glVertex3f(-1f,0.0f,0.0f);
		gl.glEnd();

		gl.glEnable(GL.GL_LIGHTING);
		
		gl.glPopMatrix();
	}
}

