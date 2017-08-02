package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;


/**
 * Matrix class for matrix operations
 */
public class Matrix {

	/**
	 * Default Constructor
	 */
	public Matrix() {}

	/**
	 * Multiplyer method
	 * @param m a 16 sized array
	 * @param v a vector to be multiplied with result stored in v
	 */
	public static void mult(float m[], Vect3d v) {
		float tmp[] = new float[4];
		tmp[0] = m[0]*v.x + m[4]*v.y + m[8]*v.z + m[12];
		tmp[1] = m[1]*v.x + m[5]*v.y + m[9]*v.z + m[13];
		tmp[2] = m[2]*v.x + m[6]*v.y + m[10]*v.z + m[14];
		v.x = tmp[0];
		v.y = tmp[1];
		v.z = tmp[2];	
	}
	
}
