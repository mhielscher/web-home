package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;
import java.math.*;

class Frustum {
	private Plane frustum[];

	/** 
	 * Default constructor
	 */
	public Frustum() {
		frustum = new Plane[6];
		for(int i=0; i<6; i++) {
			frustum[i] = new Plane();
		}
	}

	/**
	 * Calculates the frustum dimensions.
	 * @param gldraw The jogl gl interface
	 */
	public void calculate(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		float[] model = new float[16];
		float[] projection = new float[16];
		float[] clip = new float[16];
		
		gl.glGetFloatv(gl.GL_PROJECTION_MATRIX,projection);
		gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX,model);

	
		clip[0] = model[0]*projection[0] + model[1]*projection[4] + model[2]*projection[8] + model[3]*projection[12];
		clip[1] = model[0]*projection[1] + model[1]*projection[5] + model[2]
*projection[9] + model[3]*projection[13];
		clip[2] = model[0]*projection[2] + model[1]*projection[6] + model[2]*projection[10] + model[3]*projection[14];
		clip[3] = model[0]*projection[3] + model[1]*projection[7] + model[2]*projection[11] + model[3]*projection[15];
		
		clip[4] = model[4]*projection[0] + model[5]*projection[4] + model[6]*projection[8] + model[7]*projection[12];
		clip[5] = model[4]*projection[1] + model[5]*projection[5] + model[6]*projection[9] + model[7]*projection[13];
		clip[6] = model[4]*projection[2] + model[5]*projection[6] + model[6]*projection[10] + model[7]*projection[14];
		clip[7] = model[4]*projection[3] + model[5]*projection[7] + model[6]*projection[11] + model[7]*projection[15];
		
		clip[8] = model[8]*projection[0] + model[9]*projection[4] + model[10]*projection[8] + model[11]*projection[12];
		clip[9] = model[8]*projection[1] + model[9]*projection[5] + model[10]*projection[9] + model[11]*projection[13];
		clip[10] = model[8]*projection[2] + model[9]*projection[6] + model[10]*projection[10] + model[11]*projection[14];
		clip[11] = model[8]*projection[3] + model[9]*projection[7] + model[10]*projection[11] + model[11]*projection[15];
		
		clip[12] = model[12]*projection[0] + model[13]*projection[4] + model[14]*projection[8] + model[15]*projection[12];
		clip[13] = model[12]*projection[1] + model[13]*projection[5] + model[14]*projection[9] + model[15]*projection[13];
		clip[14] = model[12]*projection[2] + model[13]*projection[6] + model[14]*projection[10] + model[15]*projection[14];
		clip[15] = model[12]*projection[3] + model[13]*projection[7] + model[14]*projection[11] + model[15]*projection[15];

		//right plane
		frustum[0].set(clip[3]-clip[0],clip[7]-clip[4],clip[11]-clip[8],clip[15]-clip[12]);
		frustum[0].normalize();

		//left plane
		frustum[1].set(clip[3]+clip[0],clip[7]+clip[4],clip[11]+clip[8],clip[15]+clip[12]);
		frustum[1].normalize();

		//bottom plane
		frustum[2].set(clip[3]+clip[1],clip[7]+clip[5],clip[11]+clip[9],clip[15]+clip[13]);
		frustum[2].normalize();

		//top plane
		frustum[3].set(clip[3]-clip[1],clip[7]-clip[5],clip[11]-clip[9],clip[15]-clip[13]);
		frustum[3].normalize();

		//far plane
		frustum[4].set(clip[3]-clip[2],clip[7]-clip[6],clip[11]-clip[10],clip[15]-clip[14]);
		frustum[4].normalize();

		//near plane
		frustum[5].set(clip[3]+clip[2],clip[7]+clip[6],clip[11]+clip[10],clip[15]+clip[14]);
		frustum[5].normalize();	
	}

	/**
	 * Checks to see if a cube is in the frustum.
	 * @param center the location of the cube's center
	 * @param size cube size
	 * @return true/false depending if the cube is in the frustum or not
	 */
	public boolean isCubeInFrustum(Vect3d center, float size) {
		float sh = size/2;
		float x=center.x,y=center.y,z=center.z;
			
		for(int i=0; i<6; i++) {
			if(!frustum[i].isPointInFront(x-sh,y-sh,z-sh) &&
				!frustum[i].isPointInFront(x+sh,y-sh,z-sh) &&
				!frustum[i].isPointInFront(x+sh,y+sh,z-sh) &&
				!frustum[i].isPointInFront(x-sh,y+sh,z-sh) &&
				!frustum[i].isPointInFront(x-sh,y-sh,z+sh) &&
				!frustum[i].isPointInFront(x+sh,y-sh,z+sh) &&
				!frustum[i].isPointInFront(x+sh,y+sh,z+sh) &&
				!frustum[i].isPointInFront(x-sh,y+sh,z+sh))
				return false;
		}

		return true;
	}
}
