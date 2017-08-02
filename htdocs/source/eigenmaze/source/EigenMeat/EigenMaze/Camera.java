package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * Rendering camera object - point of view.
 */
public class Camera {
	private Vect3d position;
	private Vect3d lookat;

	private Frustum frustum;

	/**
	 * Default Constructor
	 */
	public Camera() {
		position = new Vect3d(0,0,0);
		lookat = new Vect3d(0,0,0);

		frustum = new Frustum();
	}

	/** 
	 * Get camera's position.
	 * @return pointer to camera's position vector 
	 */
	public Vect3d getPosition() {
		return position;
	}

	/**
	 * Get the point where the camera is looking at.
	 * @return pointer to camera's lookat vector
	 */
	public Vect3d getLookAt() {
		return lookat;
	}

	/**
	 * Set the camera's position.
	 * @param pos vector position where you want to move the camera too
	 */
	public void setPosition(Vect3d pos) {
		position.set(pos);
	}

	/** 
	 * Set the camera's position.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void setPosition(float x, float y, float z) {
		position.set(x,y,z);
	}

	/**
	 * Set the camera's look at vector
	 * @param lookat lookat vector
	 */
	public void setLookAt(Vect3d lookat) {
		this.lookat.set(lookat);
	}

	/**
	 * Set the camera's look at vector
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void setLookAt(float x, float y, float z) {
		lookat.set(x,y,z);
	}

	/**
	 * Get a reference to the camera's frustum.
	 * @return the frustum
	 */
	public Frustum getFrustum() {
		return frustum;
	}

	/**
	 * Update the camera. Sets the position, calculates new frustum, etc.
	 * @param gldraw jogl gl interface
	 */
	public void update(GLDrawable gldraw) {
		GLU glu = gldraw.getGLU();

		glu.gluLookAt(position.x,position.y,position.z,
				lookat.x,lookat.y,lookat.z,
				0,1,0);

		frustum.calculate(gldraw);
	}

	/**
	 * Executes the proper glRotatef command to rotate an object
	 * so it faces the camera (Billboarding). This assumes object
	 * is initially facing down the -Z axis.
	 * @param gldraw gl jogl object
	 * @param p position of object
	 */
	public void sphericalBillboard(GLDrawable gldraw, Vect3d p) {
		GL gl = gldraw.getGL();
		Vect3d objToCamXZ = new Vect3d(getPosition());
		objToCamXZ.sub(p);
		objToCamXZ.y = 0;
		objToCamXZ.normalize();
	
		Vect3d objlook = new Vect3d(0,0,-1);
		Vect3d up = new Vect3d(objlook);
		up = up.cross(objToCamXZ);

		float angle = objlook.dot(objToCamXZ);
	
		if((angle < .9999) && (angle > -0.9999)) {
			gl.glRotatef((float)(Math.acos(angle)*180/3.14),up.x,up.y,up.z);
		}

		Vect3d objToCam = new Vect3d(getPosition());
		objToCam.sub(p);
		objToCam.normalize();
		//angle = objToCamXZ.dot(objToCam);
		angle = objToCamXZ.getAngle(objToCam);
		gl.glRotatef(angle,1,0,0);
		
		/*if((angle < .9999) && angle > -0.9999) {
			if(objToCam.y < 0)
				gl.glRotatef((float)(Math.acos(angle)*180/3.14),1,0,0);
			else
				gl.glRotatef((float)(Math.acos(angle)*180/3.14),-1,0,0);
		}*/


	}
}
