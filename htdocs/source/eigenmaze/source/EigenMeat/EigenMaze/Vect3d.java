package EigenMeat.EigenMaze;

/**
 * XYZ Vector class.
 */
public class Vect3d {
	public float x, y, z;

	/**
	 * Default constructor for Vect3d. Sets the vector to 0,0,0.
	 */
	public Vect3d() {
		set(0.0f,0.0f,0.0f);
	}

	/**
	 * Constructor. Allows you to set the XYZ of the vector.
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Vect3d(float x, float y, float z) {
		set(x,y,z);
	}

	/**
	 * Constructor. Allows you to set the XYZ using double type variables.
	 * Mainly takes away the hassle of always appending "f" to the end of
	 * decimal numbers.
	 *
	 * @param x x coordinate
         * @param y y coordinate
         * @param z z coordinate
         */
	public Vect3d(double x, double y, double z) {
		set((float)x,(float)y,(float)z);
	}

	/**
	 * Copy constructor that allows you to copy another Vect3d object.
	 * 
	 * @param v Vect3d object to copy.
	 */	
	public Vect3d(Vect3d v) {
		set(v);
	}
	
	/**
	 * Accessor method for X coordinate.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Accessor method for Y coordinate.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Accessor method for Z coordinate.
	 */
	public float getZ() {
		return z;
	}
	
	/**
	 * Set method for XYZ coordinate.
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets XYZ coordinates using another Vect3d object.
	 *
	 * @param v Vect3d object to copy
	 */
	public void set(Vect3d v) {
		set(v.x,v.y,v.z);
	}

	/**
	 * Inverts the vector.
	 */
	public void invert() {
		x *= -1;
		y *= -1;
		z *= -1;
	}
	
	/**
	 * Returns the vector length.
	 *
	 * @return length of vector
	 */
	public float length() {
		return (float)Math.sqrt((x*x)+(y*y)+(z*z));
	}

	/**
	 * Normalize the vector. Changes the length to 1.
	 */
	public void normalize() {
		float len = length();

		//buffers: make sure we don't divide by zero
		if(len != 0) {
			x /= len;
			y /= len;
			z /= len;
		}
	}

	/**
	 * Performs a dot product operation on the vector.
	 *
	 * @param v The second vector to use for dot product operation
	 * @return the dot product
	 */
	public float dot(Vect3d v) {
		return (x*v.x + y*v.y + z*v.z);
	}

	/**
	 * Performs a cross product operation.
	 *
	 * @param v The second Vect3d object to use for the cross product.
	 *
	 * @return a new Vect3d object representing the cross product. 
	 */
	public Vect3d cross(Vect3d v) {
		return new Vect3d(	y*v.z - z*v.y,
					z*v.x - x*v.z,
					x*v.y - y*v.x);
	}

	/**
	 * Gets the angle between 2 vectors.
	 * 
	 * @param v The second vector
	 * @return The angle (float)
	 */
	public float getAngle(Vect3d v) {
		float f;
		f =(float) Math.acos(this.dot(v)/(this.length()*v.length()));
		return f*(float)180/(float)Math.PI;
	}

	/**
	 * Multiply vector by a scalar.
	 * 
	 * @param scalar The number to multiply the vector with.
	 */
	public void mult(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	/**
	 * Divide vector by a scalar.
	 * 
	 * @param scalar The number to divide the vector with.
	 */
	public void div(float scalar) {
		x /= scalar;
		y /= scalar;
		z /= scalar;
	}
	
	/**
	 * Multiply vector by a scalar and store the result in another vector.
	 * @param scalar The number to multiply the vector with.
	 * @param result The vector the result is stored in.
	 */
	public void mult(float scalar, Vect3d result) {
		result.x = x*scalar;
		result.y = y*scalar;
		result.z = z*scalar;
	}
	
	/**
	 * Subtract the vector by another vector
	 * @param s The vector used to subtract with.
	 */
	public void sub(Vect3d s) {
		x -= s.x;
		y -= s.y;
		z -= s.z;
	}

	/**
	 * Subtract by a vector and store the results in another vector.
	 * @param s The vector used to subtract with.
	 * @param result The vector the results are stored in.
	 */
	public void sub(Vect3d s, Vect3d result) {
		result.x = x - s.x;
		result.y = y - s.y;
		result.z = z - s.z;
	}
	
	/** 
	 * Add a vector.
	 * @param add The vector used for addition.
	 */
	public void add(Vect3d add) {
		x += add.x;
		y += add.y;
		z += add.z;
	}

	/**
	 * Helper function that multiplies a vector by a scalar before using it for addition.
	 * @param vector the Vector used for addition
	 * @param scalar scalar number to multiply vector before adding with it
	 */
	public void addm(Vect3d vector, float scalar) {
		x += vector.x*scalar;
		y += vector.y*scalar;
		z += vector.z*scalar;
	}

	/**
	 * Helper function that multiplies a vector by a scalar before using it for subtraction
	 * @param vector vector to use for subtraction
	 * @param scalar the number to multiply the vector
	 */
	public void subm(Vect3d vector, float scalar) {
		x -= vector.x*scalar;
		y -= vector.y*scalar;
		z -= vector.z*scalar;
	}

	/**
	 * Rotate vector around the Y Axis by an angle.
	 * @param angle The angle to rotate the vector
	 */
	public void rotateY(float angle) {
		float ox = x,oz = z;
		
		angle /= (180/Math.PI);
		
		x = oz * (float)Math.sin(angle) + ox * (float)Math.cos(angle);
		z = oz * (float)Math.cos(angle) - ox * (float)Math.sin(angle);
	}
	
	public boolean equals(Vect3d rhs) {
		return (x == rhs.x && y == rhs.y && z == rhs.z);
	}

	public float getDistance(Vect3d point) {
		Vect3d t = new Vect3d();

		sub(point,t);

		return t.length();
	}

	public float[] getArray() {
		float array[] = new float[3];
		array[0] = x;
		array[1] = y;
		array[2] = z;

		return array;
	}
	
	/**
	 * Method used to print the vector.
	 * @return the Vector in a formatted string
	 */
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}
	
	/**
	 * Get the resulting vector after a bounce against a plane.
	 * Uses the formula: "R = 2*(-I dot N)*N + I"
	 * (N is the surface normal, I is the normalized direction of the original vector)
	 * @param vel the initial vector before the bounce
	 * @param normal the normal of the surface to bounce the vector around
	 * @param result the resulting bounced vector
	 */
	public static void getBounceVect(Vect3d vel, Vect3d normal, Vect3d result) {
		Vect3d vel_i;
		
		float len = vel.length();
		vel.normalize();

		vel_i = new Vect3d(vel);
		vel_i.invert();

		result.set(0f,0f,0f);
		normal.mult(2*normal.dot(vel_i),result);

		result.add(vel);
		result.mult(len);
	}

	/**
	 * Get the closest point on the line between it and another point.
	 * @param a starting point of a line
	 * @param b end point of a line
	 * @param p a point
	 * @return a new vector representing the closest point on the line to point p
	 */
	public static Vect3d getClosestPointOnLine(Vect3d a, Vect3d b, Vect3d p) {
		Vect3d pa = new Vect3d();
		p.sub(a,pa);

		Vect3d dba = new Vect3d();
		b.sub(a,dba);
		float lba = dba.length();
		dba.normalize();

		float len = pa.dot(dba);
		
		if(len < 0)
			return new Vect3d(a);
		if(len > lba)
			return new Vect3d(b);

		dba.mult(len);
		dba.add(a);

		return new Vect3d(dba);
	}

	/**
	 * Get the distance between two points.
	 * @param a first point
	 * @param b second point
	 * @return the distance between the 2 points
	 */
	public static float getDistanceBetweenPoints(Vect3d a, Vect3d b) {
		Vect3d t = new Vect3d();

		a.sub(b,t);

		return t.length();
	}

}
