package EigenMeat.EigenMaze;

/**
 * 3d math methods.
 */
public class Math3D {
	/**
	 * Default constructor.
	 */
	public Math3D() {
	}

	/** 
	 * Get plane distance to the origin of a specified plane. 
	 * @param planeNormal normal of the plane
	 * @param pointOnPlane a point of on the plane
	 * @return plane distance
	 */
	public static float getPlaneDistance(Vect3d planeNormal, Vect3d pointOnPlane) {
		return -planeNormal.dot(pointOnPlane);
	}

	/**
	 * Get the distance a point is from a plane.
	 * @param planeNormal normal of a plane
	 * @param distance plane distance of the plane
	 * @param point to check
	 * @return distance
	 */
	public static float getPointDistanceFromPlane(Vect3d planeNormal, float distance, Vect3d point) {
		return planeNormal.dot(point) + distance;
	}

	/**
	 * Checks to see if a point is in front of a plane.
	 * @param planeNormal normal of plane
	 * @param distance plane distance of plane
	 * @param point point to check
	 * @return true if point is in front of plane, else false
	 */
	public static boolean isPointInFrontOfPlane(Vect3d planeNormal, float distance, Vect3d point) {
		if(planeNormal.dot(point) + distance >= 0)
			return true;

		return false;
	}

	/**
	 * Gets the collision point between a plane and a vector.
	 * @param plane a plane
	 * @param point starting point of vector
	 * @param direction direction of vector
	 * @return collision point
	 */
	public static Vect3d getCollisionPointWithPlane(Plane plane, Vect3d point, Vect3d direction) {
		return getCollisionPointWithPlane(plane.getNormal(),plane.getPlaneDistance(),point,direction);
	}
	
	/**
	 * Gets the collision point between a plane and a vector.
	 * @param planeNormal plane normal
	 * @param distance plane distance
	 * @param point starting point of vector
	 * @param direction vector direction
	 * @return collision point
	 */
	public static Vect3d getCollisionPointWithPlane(Vect3d planeNormal, float distance, Vect3d point, Vect3d direction) {
		Vect3d collisionPoint;
		
		float n = -getPointDistanceFromPlane(planeNormal,distance,point);
		float d = planeNormal.dot(direction);
		
		if(d == 0)
			return new Vect3d(point);

		float lineDistance = (n/d);
		collisionPoint = new Vect3d(direction);
		collisionPoint.mult(lineDistance);
		collisionPoint.add(point);

		return collisionPoint;
	}

	//uses Woo's Method - http://www.acm.org/tog/GraphicsGems/gems/RayBox.c
	//havn't bothered to optimize
	/**
	 * Checks to see if line intersects an axis aligned bounding cube.
	 * @param cen cube center
	 * @param radius cube size
	 * @param p1 start point of line
	 * @param p2 end point of line
	 * @return true of line intersects cube, else false
	 */
	public static boolean doesLineIntersectAABCube(Vect3d cen,float radius, Vect3d p1, Vect3d p2) {
	 	Vect3d direction = new Vect3d(p2);
		direction.sub(p1);
		direction.normalize();
                
		//we convert our Vect3ds to float arrays to make it easier
		//to process them
		float center[] = cen.getArray();
		float min[] = cen.getArray();
                min[0]-=radius;min[1]-=radius;min[2]-=radius;
                float max[] = cen.getArray();
                max[0]+=radius;max[1]+=radius;max[2]+=radius;
                float point[] = p1.getArray();
                float dir[] = direction.getArray();
		float checkPlane[] = new float[3];
                checkPlane[0]=checkPlane[1]=checkPlane[2]=1000; //omg hack
                float maxt[] = new float[3];
                maxt[0]=maxt[1]=maxt[2]=-1;

		//calculate possible planes line can intersect
                for(int i=0; i<3; i++) {
                	if(point[i] < center[i]-radius)
                        	checkPlane[i] = center[i]-radius;
                        else if(point[i] > center[i]+radius)
                                checkPlane[i] = center[i]+radius;
                }

		//check to see if line origin is inside box
		//ugly ugly hack
                if(checkPlane[0] == 1000 && checkPlane[1] == 1000 && checkPlane[2] == 1000)
                	return true;

		//calculate distance to each plane it  
                for(int i=0; i<3; i++) {
                	if(checkPlane[i] != 1000) {
                        	maxt[i] = (checkPlane[i]-point[i])/dir[i];
			}
                }

		//which plane has the furthest possible collision point
		//according to Woo, the plane furthest way is the plane
		//that the line can intersect
                int maxPlane = 0;
                if(maxt[0] > maxt[1] && maxt[0] > maxt[2])
                	maxPlane = 0;
                if(maxt[1] > maxt[0] && maxt[1] > max[2])
                	maxPlane = 1;
                if(maxt[2] > maxt[0] && maxt[2] > maxt[1])
                	maxPlane = 2;

		//once we know which plane to check, calculate the collision
		//point and see if it is inside the cube face
		float cPoint[] = new float[3];
                for(int i=0; i<3; i++) {
                	if(i != maxPlane) {
                		cPoint[i] = point[i]+maxt[maxPlane]*dir[i];
                        	if(cPoint[i] < min[i] || cPoint[i] > max[i])
                                	return false;
                	} else {
				cPoint[i] = checkPlane[i];	
			}	
		}

		//we need to make sure the collision point is on our line
		if(p1.getDistance(p2) < p1.getDistance(new Vect3d(cPoint[0],cPoint[1],cPoint[2])))
			return false;
		
		return true;
	
	}

	/**
	 * Calculates normal of a triangle.
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param v3 third vertex
	 * @return normal
	 */
	public static Vect3d calculateNormal(Vect3d v1, Vect3d v2, Vect3d v3) {
		Vect3d a = new Vect3d();
		Vect3d b = new Vect3d();

		v3.sub(v1,a);
		v3.sub(v2,b);

		Vect3d normal = a.cross(b);
		normal.normalize();
		return normal;
	}
}
