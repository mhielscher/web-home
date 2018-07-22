package EigenMeat.EigenMaze;

/**
* Helper class for various triangle-based algorithms.
*/
public class Plane {
	private Vect3d normal;
	float distance;

	/**
	 * Default constructor.
	 */
	public Plane() { 
		normal = new Vect3d();
	}

	/**
	 * Constructor that allows setting of the plane variables 
	 * (Ax+By+Cz+D=0)
	 * @param a A
	 * @param b B
	 * @param c C
	 * @param d D
	 */
	public Plane(float a, float b, float c, float d) {
		normal = new Vect3d();
		set(a,b,c,d);
	}

	/**
	 * Constructor. Allows setting of plane normal using a Vect3d.
	 * @param normal plane normal
	 * @param distance plane distance
	 */
	public Plane(Vect3d normal, float distance) {
		this.normal = new Vect3d(normal);
		this.distance = distance;
	}

	public Plane(Vect3d normal, Vect3d point) {
		this.normal = new Vect3d(normal);
		this.distance = -normal.dot(point);
	}
	
	/**
	 * Sets the planes variables after Plane object is created.
	 * @param a A
         * @param b B
         * @param c C
         * @param d D
         */
	public void set(float a, float b, float c, float d) {
		normal.set(a,b,c);
		distance = d;
	}

	/**
	 * Get the A component.
	 * @return A component
	 */
	public float getA() {
		return normal.x;
	}

	/**
	 * Get the B component.
	 * @return B component
	 */
	public float getB() {
		return normal.y;
	}
	
	/**
	 * Get the C component.
	 * @return C component
	 */
	public float getC() {
		return normal.z;
	}
	
	/**
	 * Returns the plane distance.
	 * @return plane distance
         */	
	public float getPlaneDistance() {
		return distance;
	}

	/**
	 * Returns the plane's normal.
	 * @return plane's normal
	 */
	public Vect3d getNormal() {
		return normal;
	}
	
	public float getPointDistance(Vect3d point) {
		return normal.dot(point) + getPlaneDistance();	
	}
	
	/**
	 * Normalize the plane.
	 */
	public void normalize() {
		//float t = (float)Math.sqrt(a*a+b*b+c*c);
		float t = normal.length();
		
		if(t != 0) {
			normal.x /= t;
			normal.y /= t;
			normal.z /= t;
			distance /= t;
		}
	}

	/**
	 * Checks to see if a point is in front of the plane.
	 * @param point the point
	 * @return true if point is in front of the plane, else false
	 */
	public boolean isPointInFront(Vect3d point) {
		if((normal.dot(point) + distance) >= 0)	
			return true;

		return false;
	}

	/**
	 * Checks to see if a point is in front of the plane/
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return true of point is in front of the plane, else false
	 */
	public boolean isPointInFront(float x, float y, float z) {
		if((x*normal.x + y*normal.y + z*normal.z + distance) >= 0) 
			return true;

		return false;
	}

	public boolean doesLineIntersect(Vect3d p1, Vect3d p2) {
		return true;	
	}
}
