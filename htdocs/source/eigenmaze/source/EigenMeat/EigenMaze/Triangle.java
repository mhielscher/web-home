package EigenMeat.EigenMaze;

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.io.*;
import java.nio.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * The Triangle class defines all drawable or collideable triangle surfaces.
 * 
 * @see	Vect3d
 * @see Octree
 * @see Particle
*/
class Triangle implements Comparable { //Comparable for state sorting... oh boy
	private Vect3d v[];
	private Point2D.Float t[]; //uv mapping
	private Color color; //buffers: change to array for blended colors
	private String textureName;
	private Texture texture;
	private Vect3d normal;

	private boolean neighborExist[];
	private Triangle neighborRef[];

	private boolean visible;
	
	//cached values for collision detection speed
	private float planeDistance;
	
	private float possibleEdgeCollision;

	//sphere relationship with triangle plane
	/**
	 * Indicates that a sphere being checked for collision is and will be completely in front
	 * of the plane of the triangle.
	*/
	public static final int FRONT = 0;
	/**
	 * Indicates that a sphere being checked for collision is and will be completely in back
	 * of the plane of the triangle.
	*/
	public static final int BACK = 1;
	/**
	 * Indicates that a sphere being checked for collision will collide with
	 * the plane of the triangle during the next update.
	*/
	public static final int INTERSECT = 2;

	//types of collisions
	/**
	 * Indicates no collision.
	*/
	public static final int NONE = 0;
	/**
	 * Indicates a plane collision - handled by {@link #isCollisionPointInTri(Vect3d, float, Vect3d)}.
	*/
	public static final int DIRECT = 1;
	/**
	 * Indicates an edge collision - handled by {@link #checkEdgeCollision(Vect3d, float, Vect3d)}.
	*/
	public static final int EDGE = 2;

	private float collisionPoint;
	private Vect3d realCollisionPoint;

	private boolean lightmapEnabled;
	private Lightmap lightmap;
	private Point2D.Float lightmapUV[];
	
	private int renderFrame;
	private boolean renderOnce;
	
	/**
	 * Default constructor for Triangle.
	 *
	 * @see		#Triangle(Vect3d, Vect3d, Vect3d, Color)
	*/
	public Triangle() {
		v = new Vect3d[3];
		t = null;
		color = Color.WHITE;
		possibleEdgeCollision = 0;
		realCollisionPoint = null;
		planeDistance = 0;
	
		lightmapEnabled = false;
		setRenderOnce(false);

		neighborRef = new Triangle[3];
		neighborExist = new boolean[3];
		neighborExist[0]=neighborExist[1]=neighborExist[2]=false;
	}
	
	/**
	 * Constructor for Triangle.
	 *
	 * @param	ver1	First vertex that makes up this triangle.
	 * @param	ver2	Second vertex that makes up this triangle.
	 * @param	ver3	Third vertex that makes up this triangle.
	 * @param	c		Uniform color this triangle should take (superceded by texturing).
	 *
	 * @see		#Triangle()
	*/
	public Triangle(Vect3d ver1, Vect3d ver2, Vect3d ver3, Color c) {
		v = new Vect3d[3];
		
		v[0] = ver1;
		v[1] = ver2;
		v[2] = ver3;
		color = c;
		
		t = null;
		textureName = null;

		calculateNormal();
		possibleEdgeCollision = 0;
		realCollisionPoint = null;
		planeDistance = getPlaneDistance();

		lightmapEnabled = false;
		setRenderOnce(false);
	
		neighborRef = new Triangle[3];
		neighborExist = new boolean[3];
		 neighborExist[0]=neighborExist[1]=neighborExist[2]=false;
	}
	
	/**
	 * Constructor for Triangle.
	 *
	 * @param	ver1	First vertex that makes up this triangle.
	 * @param	ver2	Second vertex that makes up this triangle.
	 * @param	ver3	Third vertex that makes up this triangle.
	 * @param	uv1		UV texture mapping coordinate for the first vertex.
	 * @param	uv2		UV texture mapping coordinate for the first vertex.
	 * @param	uv3		UV texture mapping coordinate for the first vertex.
	 * @param	c		Uniform color this triangle should take (in case texturing fails).
	 * @param	tex		Filepath to texture to cover this triangle.
	 *
	 * @see		#Triangle()
	*/
	public Triangle(Vect3d ver1, Vect3d ver2, Vect3d ver3, Point2D.Float uv1, Point2D.Float uv2, Point2D.Float uv3, Color c, Texture tex) {
		v = new Vect3d[3];
		
		v[0] = ver1;
		v[1] = ver2;
		v[2] = ver3;
		color = c;
		
		t = new Point2D.Float[3];
		t[0] = uv1;
		t[1] = uv2;
		t[2] = uv3;
		//textureName = tex;
		texture = tex;
			
		calculateNormal();
		possibleEdgeCollision = 0;
		realCollisionPoint = null;
		planeDistance = getPlaneDistance();

		lightmapEnabled = false;
		setRenderOnce(false);

		neighborRef = new Triangle[3];
		neighborExist = new boolean[3];
		 neighborExist[0]=neighborExist[1]=neighborExist[2]=false;
	}
	
	/**
	 * Gets the normal vector of the plane on which this triangle lies.
	 *
	 * @return	The normal vector of this triangle's plane.
	*/
	public Vect3d getNormal() {
		return normal;
	}

	public void setNormal(Vect3d v) {
		normal.set(v);
	}
	
	/**
	 * Gets the vertex corresponding to the argument, starting at 0 (useful for loops). If the argument is not
	 * a valid vertex number (less than 0, greater than 2) then the modulus of the argument and 3 is taken to
	 * get a valid vertex number.
	 *
	 * @param	num		The number of the vertex to get, between 0 and 2.
	 * @return			The vertex corresponding to the given number.
	*/
	public Vect3d getVertex(int num) {
		num = num%3;
		return v[num];
	}
	
	public Vect3d getVertex1() {
		return v[0];
	}
	
	public Vect3d getVertex2() {
		return v[1];
	}
	
	public Vect3d getVertex3() {
		return v[2];
	}
	
	public void setVertex1(Vect3d ver1) {
		v[0] = ver1;

		calculateNormal();
		planeDistance = getPlaneDistance();
	}
	
	public void setVertex2(Vect3d ver2) {
		v[1] = ver2;

		calculateNormal();
		planeDistance = getPlaneDistance();
	}
	
	public void setVertex3(Vect3d ver3) {
		v[2] = ver3;

		calculateNormal();
		planeDistance = getPlaneDistance();
	}
	
	public Point2D.Float getUV(int num) {
		if (t == null)
			return null;
		num = num%3;
		return t[num];
	}
	
	public Point2D.Float getUV1() {
		if (t == null)
			return null;
		return t[0];
	}
	
	public Point2D.Float getUV2() {
		if (t == null)
			return null;
		return t[1];
	}
	
	public Point2D.Float getUV3() {
		if (t == null)
			return null;
		return t[2];
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture id) {
		texture = id;	
	}

	public void setRenderOnce(boolean b) {
		renderOnce = b;
	}	

	public boolean hasLightmap() {
		return lightmapEnabled;
	}

	public void setNeighbor(int whichEdge, Triangle neighbor) {
		if(neighbor == null)
			System.out.println("TROUBLE");
		
		neighborExist[whichEdge] = true;
		neighborRef[whichEdge] = neighbor;
	}

	public void setNeighbor(int whichEdge, boolean b) {
		neighborExist[whichEdge] = b;
	}
	
	public boolean hasNeighbor(int edge) {
		return neighborExist[edge];
	}

	public Triangle getNeighbor(int edge) {
		return neighborRef[edge];
	}
	
	public void setVisible(boolean v) {
		visible = v;
	}

	public boolean isVisible() {
		return visible;
	}
		
	private void calculateNormal() {
		Vect3d a = new Vect3d();
		Vect3d b = new Vect3d();

		v[2].sub(v[0],a);
		v[2].sub(v[1],b);
		
		normal = a.cross(b);
		normal.normalize();
	}

	//get plane distance, the D in Ax+By+Cz+D=0
	public float getPlaneDistance() {
		return -normal.dot(v[0]);
	}
	
	/**
	 * Checks to see if the given sphere intersects with the triangle's plane.
	 * 
	 * @param	cen		the center of the sphere to check against
	 * @param	radius	the radius of the sphere to check against
	 * @param	vel		the velocity vector of the sphere - really, the vector defining the
	 *					jump the sphere will take over the next update
	 * @return			INTERSECT if the sphere will intersect the plane of the triangle after the next update;
	 *					FRONT if the sphere is and will be completely on the front side of the plane;
	 *					BACK if the sphere is and will be completely on the back side of the plane.
	 * @see		Game
	*/
	//check to see if sphere intersects with triangle's plane
	private int classifySphere(Vect3d cen, float radius, Vect3d vel) {
		float planed = planeDistance;
		float distance = getPointDistanceFromPlane(cen);
		Vect3d predCen = new Vect3d(cen);
		predCen.add(vel);
		float distance2 = getPointDistanceFromPlane(predCen);
			
		if(Math.abs(distance) <= radius || Math.abs(distance2) <= radius) 
			return INTERSECT;
		else if ((distance-radius > 0 && distance2-radius < 0) || (distance-radius < 0 && distance2-radius > 0))
			return INTERSECT;
		else if(distance2 > radius)
			return FRONT;

		return BACK;
	}

	/**
	 * Get a point's distance from triangle's plane.
	 * @param point the point to check
	 * @return the distance the point is from the triangle's plane
	 */
	public float getPointDistanceFromPlane(Vect3d point) {
		return normal.dot(point) + planeDistance;
	}

	/**
	 * Checks to see if a point is in front of the triangle's plane.
	 * @param point the point to check
	 * @return return true of point is in front of triangle, false if not
	 */
	public boolean isPointInFront(Vect3d point) {
		if(getPointDistanceFromPlane(point) >= 0)
			return true;

		return false;
	}
	
	private boolean isPointInTriangle(Vect3d point) {
		Vect3d t1 = new Vect3d();
		Vect3d t2 = new Vect3d();
		float angle = 0;
		
		for(int i=0; i<3; i++) {
			//get 2 vectors from intersection point to 2 
			//consecutive vertices in the triangle
			v[i].sub(point, t1);
			v[(i+1)%3].sub(point, t2);

			angle += t1.getAngle(t2);
		}
		//if(angle >= ((.990-Game.tof*1.5)*360))
		if(angle >= (.999*360)) //1.8 buffers addition
			return true;

		return false;
	}

	public boolean doesLineIntersectPlane(Vect3d p1, Vect3d p2) {
		float d1 = getPointDistanceFromPlane(p1);
		float d2 = getPointDistanceFromPlane(p2);

		if((d1*d2) < 0)
			return true;

		return false;
	}

	public boolean doesLineIntersect(Vect3d p1, Vect3d p2) {
		if(!doesLineIntersectPlane(p1,p2)) 
			return false;
///*	
		Vect3d collisionPoint;
		
		Vect3d direction = new Vect3d(p1);
		direction.sub(p2);
		direction.normalize();
		float distance = getPlaneDistance();
		
		float n = -getPointDistanceFromPlane(p1);//-getNormal().dot(p1) + distance;
		float d = getNormal().dot(direction);

		if(d == 0) {
			collisionPoint = new Vect3d(p1);
			return isPointInTriangle(collisionPoint);
		}	
			
		distance = (n/d);
		direction.mult(distance);
	
		collisionPoint = new Vect3d(p1);
		collisionPoint.add(direction);
	
		return isPointInTriangle(collisionPoint);
//	*/
//		Vect3d direction = new Vect3d(p2);
//		direction.sub(p1);
//		
//		return isPointInTriangle(Math3D.getCollisionPointWithPlane(getNormal(),getPlaneDistance(),p1,direction));
	}
	
	private boolean isCollisionPointInTri(Vect3d cen, float radius, Vect3d vel) {
		//float distance = getSphereDistanceFromPlane(cen);
		/*
		//project sphere center onto triangle plane
		Vect3d offset = new Vect3d();
		normal.mult(distance,offset);
	
		Vect3d position = new Vect3d();
		cen.sub(offset,position);
		*/
		/* This algorithm uses the formula:
				       D + A*P1x + B*P1y + C*P1z
			mu = ---------------------------------------
				 A*(P2x-P1x) + B*(P2y-P1y) + C*(P2z-P1z)
			to find the point on the move-line that intersects the plane of the triangle.
			Of course, we want to find the point at which the outer edge of the sphere
			intersects the triangle, so we move the plane forward by the radius (the
			sphere will always collide such that its radius is in the opposite direction
			to the normal of the triangle). Then we find where the move-line intersects
			this shifted plane, and that is the location of the center of the sphere when
			it collides with the plane. This position is then moved towards the triangle by
			the radius to make up for the previously "moved" plane. This is now the collision
			point, and we just make sure that it is within the triangle. */
		
		possibleEdgeCollision = 0;
		float D = planeDistance-radius;
		Vect3d tempVec = new Vect3d(cen);
		tempVec.add(vel); //the predicted center of the sphere after the next update
		float denom = (normal.x*(cen.x-tempVec.x)+normal.y*(cen.y-tempVec.y)+normal.z*(cen.z-tempVec.z));
		if (denom == 0)
			return false;
		float mu = (D+(normal.x*cen.x)+(normal.y*cen.y)+(normal.z*cen.z))/denom;
		if (mu >= 0 && mu <= 1) {
			Vect3d position = new Vect3d(cen.x+(mu*(tempVec.x-cen.x)), cen.y+(mu*(tempVec.y-cen.y)),
											cen.z+(mu*(tempVec.z-cen.z)));
			float distance = getPointDistanceFromPlane(position);
			tempVec.set(normal); //now get the vector on which to move the collision center point
			tempVec.mult(-radius);
			position.add(tempVec);
			//System.out.println("Almost true, position is "+position+", planed="+getPlaneDistance());
			//then check the projected point with the tri
			if(isPointInTriangle(position)) {
				collisionPoint = distance;
				//System.out.println("True: "+position+", planed="+getPlaneDistance());
				return true;
			} else
				possibleEdgeCollision = distance;
		}
		
		/* MH: Trying something else here to see what happens...
		Vect3d position = new Vect3d(normal);
		position.invert();
		position.mult(radius);
		position.add(cen);
		*/
		/*
		//then check the projected point with the tri
		if(isPointInTriangle(position)) {
			collisionPoint = distance;
			//System.out.println("True: "+position+", planed="+getPlaneDistance());
			return true;
		}
		*/
		return false;
	}
	
	/**
	 * Gets the most recently-calculated point of collision with a mobile entity.
	 *
	 * @return	the most recently calculated collision point.
	*/
	public float getCollisionPoint() {
		return collisionPoint;
	}
	
	/**
	 * Checks to see if the given sphere will have intersected this triangle after the
	 * next game update, and returns a code defining the type of collision.
	 *
	 * @param	cen		the center point of the sphere against which the check collisions.
	 * @param	radius	the radius of the sphere.
	 * @param	vel		the real velocity vector of the sphere. Really, the vector describing
	 *					the jump this sphere will take over the next update.
	 *
	 * @return			NONE if no collision, DIRECT if plane collision, or EDGE
	 *					if edge collision.
	*/
	public int checkSphereCollision(Vect3d cen, float radius, Vect3d vel) {
		if (vel.getX() == 0 && vel.getY() == 0 && vel.getZ() == 0)
			return NONE;
		
		int classify = classifySphere(cen,radius,vel);

		if(classify == INTERSECT) {
			if(isCollisionPointInTri(cen,radius,vel)) {
				//System.out.println("Collided with triangle.");
				return DIRECT;
			} else if(checkEdgeCollision(cen,radius,vel)) {
				//System.out.println("Edge collision possible: "+normal);
				return EDGE;
			}
		}
		
		return NONE;
	}

	/**
	 * Get real collision point. Used by collision detection code.
	 * @return Vect3d collision point
	 */
	public Vect3d getRealCollisionPoint() {
		return realCollisionPoint;
	}

	private boolean checkEdgeCollision(Vect3d cen, float radius, Vect3d vel) {
		boolean collision = false;
		collisionPoint = 100;
		/*
		for(int i=0; i<3; i++) {
			Vect3d t = Vect3d.getClosestPointOnLine(v[i],v[(i+1)%3],cen);
			float distance = Vect3d.getDistanceBetweenPoints(t,cen);
			if(distance < radius) {
				if(distance < dis) 
					collisionPoint = distance;	
				collision = true;
				//System.out.println("Edge collided.");
			}
		}
		*/
		/* This algorithm calculates the shortest distance between the move-line
		   and the two vertical lines of the triangle. First it gets the vectors related
		   to the move-line and a triangle line, then it gets their mutual perpendicular
		   unit vector with a cross-product and by normalizing. Then it gets the vector
		   between two points on those lines - one point on the triangle, and the
		   current center point - and dots that with the unit perpendicular vector.
		   This results in the shortest distance between the two lines. If it is less
		   than the bounding sphere's radius, there is a collision. */
		
		Vect3d predCen = new Vect3d(cen);
		predCen.add(vel);
		
		/* Next strategy:
			Move cen perpendicular to vel and the triangle edge by radius, towards
			the edge, then run isCollisionPointInTri() with the new cen. Returns
			true = edge collision, return false = no collision.
		*/
		
		for(int i=0; i<3; i++) {
			if (!(v[i].getX() == v[(i+1)%3].getX() && v[i].getZ() == v[(i+1)%3].getZ())) //use only the vertical edges
				continue;
			Vect3d s = new Vect3d(v[i]);
			s.sub(v[(i+1)%3]); //create the edge vector
			Vect3d t = new Vect3d(vel); //create the movement vector
			t = t.cross(s);
			t.normalize(); //get the vector normal to both lines
			/*
			Vect3d r = new Vect3d(v[i]);
			r.sub(cen); //get the vector joining the two points
			float sign = r.dot(t)/Math.abs(r.dot(t)); //get the perpendicular projection's length
			t.mult(radius*sign);
			Vect3d p = new Vect3d(cen);
			p.add(t);
			//System.out.println("Calling plane collision for edge detection.");
			return isCollisionPointInTri(p, 0, vel);
			*/
			
			Vect3d r = new Vect3d(v[i]);
			r.sub(cen); //get the vector joining the two points
			float distance = Math.abs(r.dot(t)); //get the perpendicular projection's length
			
			/*
			Vect3d p = new Vect3d(t); //duplicate the lines' normal vector
			t.mult(distance); //multiply by the shortest length to get the vector connecting the lines
			
			Vect3d q = new Vect3d(cen);
			q.add(t); //translate cen so that the movement vector's line would intersect the edge
			q.sub(v[i]); //now we have a vector from the translated cen to v[i]
			t.set(predCen);
			t.sub(cen);
			float moveDistance = t.length(); //save this for later
			t.normalize(); //get the projection vector
			distance = Math.abs(q.dot(t)); //get the length of q after it's projected onto the movement vector
			*/
			
			/*
			t.add(cen); //add cen; this moves the cen point to where we can...
			t.sub(predCen); //make the movement vector again, but this time it intersects the triangle edge
			r.set(v[i]);
			r.sub(t); //set up a vector from v[i] to the new "center" point
			s.set(predCen);
			s.sub(cen); //re-setup the movement vector
			*/
			
		//	if (distance <= moveDistance-radius) { //compare the lengths of the v[i]-newcen and movement vectors
				if(distance < radius /*&& v[i].getX() == v[(i+1)%3].getX() && v[i].getZ() == v[(i+1)%3].getZ()*/) {
					realCollisionPoint = new Vect3d(v[i].getX(), cen.getY(), v[i].getZ());
					Vect3d u = new Vect3d(cen);
					u.sub(realCollisionPoint);
					float dis = u.length();
					if(dis < collisionPoint && dis <= radius) { //bit of a workaround for a bad algorithm
						//System.out.println("u: "+u+"; u.length: "+u.length());
						//System.out.println("Internal distance: "+distance);
						collisionPoint = dis;
						collision = true;
					}
					//collision = true;
				}
		//	}
			
		}
		
		//edgeCollisionPossible = 0;
		return collision;
		//return false;
	}

	/**
	 * Check to see if Triangle edges contain a specified point.
	 * @param p the point
	 * @return true/false
	 */
	public boolean edgeContainsPoint(Vect3d p) {
		if (p == null)
			return false;
		
		boolean contains = false;
		for (int i=0; i<3; i++) {
			if (v[i].getX() == p.getX() && v[i].getZ() == p.getZ() &&
				v[(i+1)%3].getX() == p.getX() && v[(i+1)%3].getZ() == p.getZ())
					contains = true;
		}
		return contains;
	}

	/**
	 * Prints out this Triangle in the form "[[x,y,z][x,y,z][x,y,z]]".
	 *
	 * @returns	A string that makes this triangle human-readble.
	 * @see		Vect3d#toString()
	*/
	public String toString() {
		return "["+v[0]+v[1]+v[2]+"]";	
	}

	/**
	 * Check to see if a Triangle has the same vertices as another.
	 * @param rhs other Triangle to check
	 * @return true/false
	 */
	public boolean equals(Triangle rhs) {
		return (v[0].equals(rhs.v[0]) && v[1].equals(rhs.v[1]) && v[2].equals(rhs.v[2]));
	}

	/**
	 * Draw the triangle.
	 * @param gldraw GLDrawable
	 */
	public void draw(GLDrawable gldraw) {
		GL gl = gldraw.getGL();

		if(renderOnce && renderFrame == EigenEngine.instance().getRenderFrame())
			return;	
		renderFrame = EigenEngine.instance().getRenderFrame();
		
		if(texture == null || t == null) 
			draw_plain(gldraw);
		else if(lightmapEnabled) {
			gl.glDisable(GL.GL_LIGHTING);
			draw_multitexture(gldraw);
			gl.glEnable(GL.GL_LIGHTING);
		} else {	
			gl.glEnable(GL.GL_LIGHTING);
		
			TextureManager.getInstance().bindTexture(gldraw,texture);
			//texture.bind(gldraw);			

			gl.glBegin(GL.GL_TRIANGLES);
			gl.glNormal3f(normal.x,normal.y,normal.z);
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2f((float)t[0].getX(), (float)t[0].getY());
			gl.glVertex3f(v[0].x, v[0].y, v[0].z);
			gl.glTexCoord2f((float)t[1].getX(), (float)t[1].getY());
			gl.glVertex3f(v[1].x, v[1].y, v[1].z);
			gl.glTexCoord2f((float)t[2].getX(), (float)t[2].getY());
			gl.glVertex3f(v[2].x, v[2].y, v[2].z);
			gl.glEnd();

			//texture.unbind(gldraw);
		}
	}

	/**
	 * Draws triangle without using textures.
	 * @param gldraw GLDrawable
	 */
	public void draw_plain(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		gl.glBegin(gl.GL_TRIANGLES);
        	        gl.glColor3f((float)color.getRed()/255f, (float)color.getGreen()/255f, (float)color.getBlue()/255f);
			gl.glNormal3f(normal.x,normal.y,normal.z);
			gl.glVertex3f(v[0].x, v[0].y, v[0].z);
			gl.glVertex3f(v[1].x, v[1].y, v[1].z);
                	gl.glVertex3f(v[2].x, v[2].y, v[2].z);
		gl.glEnd();

	}

	/**
	 * Draw the triangle using its normal texture and a lightmap texture.
	 * Uses multitexturing.
	 * @param gldraw GLDrawable
	 */
	public void draw_multitexture(GLDrawable gldraw) {
		GL gl = gldraw.getGL();

		gl.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
		gl.glEnable(GL.GL_TEXTURE_2D);
		//texture.bind(gldraw);
		TextureManager.getInstance().bindTexture(gldraw,texture);
		gl.glTexEnvf(gl.GL_TEXTURE_ENV,gl.GL_TEXTURE_ENV_MODE,gl.GL_COMBINE_EXT);
    		gl.glTexEnvf(gl.GL_TEXTURE_ENV,gl.GL_COMBINE_RGB_EXT,gl.GL_REPLACE);
    
		gl.glActiveTextureARB(gl.GL_TEXTURE1_ARB);
    		gl.glEnable(gl.GL_TEXTURE_2D);
    		lightmap.bind(gldraw);
		//texture.bind(gldraw);
		gl.glTexEnvf(gl.GL_TEXTURE_ENV,gl.GL_TEXTURE_ENV_MODE,gl.GL_COMBINE_EXT);
    		gl.glTexEnvf(gl.GL_TEXTURE_ENV,gl.GL_COMBINE_RGB_EXT,gl.GL_MULT);	

		gl.glBegin(GL.GL_TRIANGLES);
                gl.glMultiTexCoord2fARB(gl.GL_TEXTURE0_ARB, (float)t[0].getX(), (float)t[0].getY());
		gl.glMultiTexCoord2fARB(gl.GL_TEXTURE1_ARB,(float)lightmapUV[0].getX(),(float)lightmapUV[0].getY());
                gl.glVertex3f(v[0].x, v[0].y, v[0].z);
                gl.glMultiTexCoord2fARB(gl.GL_TEXTURE0_ARB,(float)t[1].getX(), (float)t[1].getY());
		gl.glMultiTexCoord2fARB(gl.GL_TEXTURE1_ARB,(float)lightmapUV[1].getX(),(float)lightmapUV[1].getY());
                gl.glVertex3f(v[1].x, v[1].y, v[1].z);
                gl.glMultiTexCoord2fARB(gl.GL_TEXTURE0_ARB,(float)t[2].getX(), (float)t[2].getY());
                gl.glMultiTexCoord2fARB(gl.GL_TEXTURE1_ARB,(float)lightmapUV[2].getX(),(float)lightmapUV[2].getY());
		gl.glVertex3f(v[2].x, v[2].y, v[2].z);
                gl.glEnd();

		gl.glActiveTextureARB(GL.GL_TEXTURE1_ARB);
	        gl.glDisable(GL.GL_TEXTURE_2D);

		gl.glActiveTextureARB(GL.GL_TEXTURE0_ARB);
	        gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexEnvf(GL.GL_TEXTURE_ENV,GL.GL_TEXTURE_ENV_MODE,GL.GL_MODULATE);
	}

	/**
	 * Draw the Triangle without using glBegin/glEnd functions.
	 */
	public void drawRaw(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		gl.glNormal3f(normal.x,normal.y,normal.z);
                gl.glColor3f(1.0f, 1.0f, 1.0f);
                gl.glTexCoord2f((float)t[0].getX(), (float)t[0].getY());
                gl.glVertex3f(v[0].x, v[0].y, v[0].z);
                gl.glTexCoord2f((float)t[1].getX(), (float)t[1].getY());
                gl.glVertex3f(v[1].x, v[1].y, v[1].z);
                gl.glTexCoord2f((float)t[2].getX(), (float)t[2].getY());
                gl.glVertex3f(v[2].x, v[2].y, v[2].z);
	}

	/**
	 * Compare Triangle to another Triangle
	 */
	public int compareTo(Object rhs) {
		if (rhs == null)
			return 0;
		if (textureName == null && ((Triangle)rhs).textureName == null)
			return 0;
		else if (textureName == null)
			return -100;
		else if (((Triangle)rhs).textureName == null)
			return 100;
		else
			return textureName.compareTo(((Triangle)rhs).textureName);
	}

	/**
	 * Create lightmap for Triangle.
	 */
	public void createLightmap() {
		lightmap = new Lightmap(v[0],v[1],v[2],normal);
		
		lightmapUV = lightmap.getUV();
		
		lightmapEnabled = true;
	}

	/**
	 * Static method that sets edge connectivity in an array of Triangles.
	 * @param triangles array to process
	 */
	public static void calculateTriangleNeighbors(Triangle triangles[]) {
		int num = triangles.length;
                Triangle cur,tmp;
		int connections = 0;
		boolean hasNeighbor;
	
                //buffers: NESTED LOOPS OF DOOM
                //(yes, I know it is scary)
                for(int i=0; i<num; i++) {
                        cur = triangles[i];

                        //loop through each edge in cur triangle
                        for(int j=0; j<3; j++) {
                                //if edge does not already have a neighbor set
                                if(!cur.hasNeighbor(j)) {
                                        //get the vertices of the edge
                                        Vect3d va1 = cur.getVertex(j);
                                        Vect3d va2 = cur.getVertex((j+1)%3);
                                        //loop through all the triangles again
                                        hasNeighbor = false;
					for(int k=0; k<num; k++) {
                                                tmp = triangles[k];
						//make sure we are not processing the current triangle
                                                if(tmp != cur) {
                                                        //loop through each edge
                                                        for(int l=0; l<3; l++) {
                                                                Vect3d vb1 = tmp.getVertex(l);
                                                                Vect3d vb2 = tmp.getVertex((l+1)%3);
                                                                //check to see if edges are the same
                                                                if((va1.equals(vb1) && va2.equals(vb2)) ||
                                                                   (va1.equals(vb2) && va2.equals(vb1))) {
                                                                        tmp.setNeighbor(l,cur);
                                                                        cur.setNeighbor(j,tmp);
									connections++;
									hasNeighbor = true;
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }

                }
		
	}
}
