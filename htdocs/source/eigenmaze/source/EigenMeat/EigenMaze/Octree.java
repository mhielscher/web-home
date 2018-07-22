package EigenMeat.EigenMaze;

import java.util.*;
import java.awt.Color;
import net.java.games.jogl.*;

/**
 * Octree data structure for static triangle data
 */
public class Octree implements Drawable {
	private Vector triangleList;
	private boolean init;

	private int numberOfTriangles;
	private int numberOfNodes;
	
	private Vect3d minBounds,maxBounds;
	private float width;

	private int drawNodes;
	private int renderedTris;

	private ShadowVolume shadowVolume;
	
	private class Node {
		private Node parent;
		private Node[] children;
		private Vect3d center;
		private float width; // this is the total width of the cube, i.e. diameter of inscribed sphere
		private Triangle[] triangles;
		private int displayListID;
		private Plane planes[];
		
		public Node() {
			parent = null;
			children = new Node[8];
			center = new Vect3d();
			width = 0;
			triangles = null;
			displayListID = -1;
			planes = new Plane[6];
		}
		
		public Node(Node p, float cen_x, float cen_y, float cen_z, float w, Triangle[] tri) {
			parent = p;
			children = new Node[8];
			center = new Vect3d(cen_x,cen_y,cen_z);
			width = w;
			displayListID = -1;
			triangles = tri;
			
			planes = new Plane[6];
			Vect3d tmp = new Vect3d(center);
			tmp.x += w;
			planes[0] = new Plane(new Vect3d(1,0,0),tmp);
			tmp.set(center);
			tmp.x -= w;
			planes[1] = new Plane(new Vect3d(-1,0,0),tmp);
			tmp.set(center);
			tmp.y += w;
			planes[2] = new Plane(new Vect3d(0,1,0),tmp);
			tmp.set(center);
			tmp.y -= w;
			planes[3] = new Plane(new Vect3d(0,-1,0),tmp);
			tmp.set(center);
			tmp.z += w;
			planes[4] = new Plane(new Vect3d(0,0,1),tmp);
			tmp.set(center);
			tmp.z -= w;
			planes[5] = new Plane(new Vect3d(0,0,-1),tmp);
			
			if (triangles.length > 200)
				buildChildren();
			else
				Arrays.sort(triangles); //sort for texture ordering for fastest rendering
		}
	
		public float getWidth() {
			return width;
		}

		public Vect3d getCenter() {
			return center;
		}
		
		public void setDimensions(float cen_x, float cen_y, float cen_z, float w) {
			center.set(cen_x,cen_y,cen_z);
			width = w;
		}
		
		public void setTriangles(Triangle[] tri) {
			triangles = tri;
			if (triangles.length > 200)
				buildChildren();
			else
				Arrays.sort(triangles); //sort for texture ordering for fastest rendering
			displayListID = -1;
		}

		public boolean isSphereInNode(Vect3d cen, float radius) {
			float hw = width/2;

			if((center.x-hw-radius) <= cen.x && (center.x+hw+radius) >= cen.x && (center.y-hw-radius) <= cen.y && (center.y+hw+radius) >= cen.y && (center.z-hw-radius) <= cen.z && (center.z+hw+radius) >= cen.z)
				return true;
				
			return false;
		}
		
		public void checkCollision(CollisionInfo info) {
			//if triangles != null, its a leaf, so we
			//check the tris for collisions
			if(triangles != null)
				checkCollisionWithTris(info);
			else //triangles == null means its further subdivided
				for(int i=0; i<8; i++) {
					if(children[i] != null && children[i].isSphereInNode(info.getProposedPosition(),info.getBoundingSphere()))
						children[i].checkCollision(info);
				}
		}
				
		public void checkCollisionWithTris(CollisionInfo info) {
			float distance = 100, tdis; //collision distance vars
			int triID = -1; //id of tri with nearest collision
			float rad = info.getBoundingSphere(); //sphere radius
			Vect3d pos = info.getCurrentPosition(); //position
			Vect3d vel = info.getRealVelocity(); //velocity
			int type = 0;
			Vect3d normal = null;
			int edgeCount = 0;
			
			//loop through the triangles to check for collisions
			//with each one. we keep track of the distances so we
			//get the triID of the triangle with the closest 
			//collision
			for(int i=0; i<triangles.length; i++) {
				type =triangles[i].checkSphereCollision(pos,rad,vel);
				if(type > 0) {
					tdis = triangles[i].getCollisionPoint();
					//System.out.println("cen: "+pos+"; cp: "+triangles[i].getRealCollisionPoint());
					if (triangles[i].edgeContainsPoint(info.getCollisionPoint()))
						edgeCount++;
					if (edgeCount > 2) {
						info.setEdgeCollision(false);
						//System.out.println("edgeCount > 2");
						return;
					}
					if(Math.abs(tdis) <= Math.abs(info.getDistance())) {
						//buffers: work in progress 
						if(type == Triangle.EDGE) {
							if (!info.getEdgeCollision())
								edgeCount++;
							info.setEdgeCollision(true);
							info.setTriNormal(new Vect3d(triangles[i].getNormal()));
							info.setCollisionPoint(triangles[i].getRealCollisionPoint());
							//System.out.println("Edge collision handled: "+triangles[i].getNormal());
						} else {
							//System.out.println("Plane collision handled.");
							info.setEdgeCollision(false);
							info.setTriNormal(new Vect3d(triangles[i].getNormal()));
						}
						info.setDistance(tdis);
						info.setCollision(true);
					}
				}
				info.trianglesChecked++;
			}
		}
		
		public void generateDisplayList(GLDrawable glDraw) {
			//System.out.println("Building display list for "+this.toString());
			GL gl = glDraw.getGL();
			displayListID = gl.glGenLists(1);
			gl.glNewList(displayListID, gl.GL_COMPILE);
			TextureManager.getInstance().bindTexture(glDraw,triangles[0].getTexture());
			gl.glBegin(GL.GL_TRIANGLES);
			for (int i=0; i<triangles.length; i++)
				triangles[i].drawRaw(glDraw);
			gl.glEnd();
			//TextureManager.getInstance().unbindTexture(gl);
			gl.glEndList();
		}
		
		public void draw_tris(GLDrawable gldraw) {
			GL gl = gldraw.getGL();
			//if (displayListID == -1)
			//	generateDisplayList(gldraw);

			for(int i=0; i<triangles.length; i++) {
				triangles[i].draw(gldraw);
			}	
			
			
			//trying for display lists
			//gl.glCallList(displayListID);
		}
			
		public void draw(GLDrawable gldraw) {
			if(triangles == null) {
				for(int i=0; i<8; i++)
					if(children[i] != null)
						children[i].draw(gldraw);
			} else {
				draw_tris(gldraw);
			}

		}
		
		public void draw(GLDrawable gldraw, Frustum frustum) {
			if(frustum.isCubeInFrustum(new Vect3d(center.x,center.y,center.z),width)) {
				if(triangles == null) {
					for(int i=0; i<8; i++) 
						if(children[i] != null)
							children[i].draw(gldraw,frustum);
				} else {
					drawNodes++;
					draw_tris(gldraw);
				}
			}
		}	

		public boolean doesLineIntersectNode(Vect3d p1, Vect3d p2) {
			return Math3D.doesLineIntersectAABCube(center,width/2,p1,p2);
		}
		
		/**
		 * Check to see if a line is blocked by Triangles inside
		 * this node.
		 * @param p1 point 1
		 * @param p2 point 2
		 */
		public boolean isLineBlocked(Vect3d p1, Vect3d p2) {
			boolean blocked = false;
	
			if(!Math3D.doesLineIntersectAABCube(center,width/2,p1,p2)) {
				return false;
			}
			
			if(triangles != null) {
				for(int i=0; i<triangles.length; i++) {
					if(triangles[i].doesLineIntersect(p1,p2))
						return true;
				}
			} else {
				for(int i=0; i<8; i++)
					if(children[i] != null) {
						if(children[i].isLineBlocked(p1,p2))
							return true;
					}
			}	
			
			return false;
		}
		
		/*
		Using a three-bit mask to generate cube segment tests for each vertex/triangle:
		+x, +y, +z = 000
		-x, -y, -z = 111
		*/
		public void buildChildren() {
			int mask = 0;
			float xMult=0,yMult=0,zMult=0;
			for (mask=0; mask<8; mask++) {
				// fixed the y and z masks (stupid me)
				xMult =(float)Math.pow(-1.0, (mask&0x1));
				yMult =(float)Math.pow(-1.0, (mask&0x2)>>1);
				zMult =(float)Math.pow(-1.0, (mask&0x4)>>2);
				
				float nwidth = width/2; //width of each child
				float pos_offset = nwidth/2; //cen xyz offset			
				//System.out.println("checking node with cen: "+(center.x+pos_offset*xMult)+" "+(center.y+pos_offset*yMult)+" "+(center.z+pos_offset*zMult));
	
				Vector divTris = new Vector(0,1);
				for (int i=0; i<triangles.length; i++) {
					boolean contained = false;
					Vect3d vert1 = triangles[i].getVertex1();
					Vect3d vert2 = triangles[i].getVertex2();
					Vect3d vert3 = triangles[i].getVertex3();
					if(Math3D.doesLineIntersectAABCube(center,width/2,vert1,vert2) || Math3D.doesLineIntersectAABCube(center,width/2,vert1, vert3) || Math3D.doesLineIntersectAABCube(center,width/2,vert2, vert3))
						contained = true;
					
					if(isVertInCube(vert1,center.x+pos_offset*xMult,
							center.y+pos_offset*yMult,
							center.z+pos_offset*zMult,nwidth))						contained = true;
					if(isVertInCube(vert2,center.x+pos_offset*xMult,
                                                        center.y+pos_offset*yMult,
                                                        center.z+pos_offset*zMult,nwidth))
                                                contained = true;
					if(isVertInCube(vert3,center.x+pos_offset*xMult,
                                                        center.y+pos_offset*yMult,
                                                        center.z+pos_offset*zMult,nwidth))
                                                contained = true;				
					if (contained)
						divTris.addElement(triangles[i]);
				}
			
				//only init child if it has triangles
				if(divTris.size() > 0) {
					Triangle[] dividedTriangles = new Triangle[divTris.size()];
					for (int i=0; i<divTris.size(); i++) {
						dividedTriangles[i] = (Triangle)divTris.elementAt(i);
					dividedTriangles[i].setRenderOnce(true);
					}
					numberOfNodes++;	
					children[mask] = new Node(this, center.x+pos_offset*xMult, center.y+pos_offset*yMult,center.z+pos_offset*zMult, nwidth, dividedTriangles);
				} else
					children[mask] = null;
			}
			triangles = null;
		}

		/**
		 * Check to see if a vertex is in the node.
		 * @param v the point to check
		 * @param cx node x
		 * @param cy node y
		 * @param cz node z
		 * @param width cube width
		 */
		public boolean isVertInCube(Vect3d v, float cx, float cy, float cz, float width) {
			if(v.x > (cx-width/2) && v.x < (cx+width/2) &&
				v.y > (cy-width/2) && v.y < (cy+width/2) &&
				v.z > (cz-width/2) && v.z < (cz+width/2))
				return true;
			return false;
		}

		/**
		 * Create lightmaps for Triangles in node.
		 */
		public void createLightmaps() {
			if(triangles == null) {
				for(int i=0; i<8; i++) 
					if(children[i] != null)
						children[i].createLightmaps();
			} else {
				for(int i=0; i<triangles.length; i++) {
					if(!triangles[i].hasLightmap())
						triangles[i].createLightmap();
				}

				System.out.println("finished creating lightmaps for node");
			}
		}
	}

	private Node root;
	
	public Octree() {
		init = true;
		triangleList = new Vector(0,1);

		minBounds = new Vect3d(0,0,0);
		maxBounds = new Vect3d(0,0,0);

		numberOfTriangles = 0;
		numberOfNodes = 0;
	}

	/**
	 * Adds an array of Triangle objects to the main triangle list 
	 * for the Octree. This is called before the Octree itself is
	 * created.
	 * @param triangles an array of triangles to be added
	 */
	public void addTriangles(Triangle[] triangles) {
		if (!init)
			return;
		
		for (int i=0; i<triangles.length; i++) {
			Triangle t = triangles[i];
			triangleList.add(t);

			for(int d=0; d<3; d++) {
				Vect3d v = t.getVertex(d);
				if(v.x < minBounds.x)
					minBounds.x = v.x;
				if(v.y < minBounds.y)
					minBounds.y = v.y;
				if(v.z < minBounds.z)
					minBounds.z = v.z;
				if(v.x > maxBounds.x)
					maxBounds.x = v.x;
				if(v.y > maxBounds.y)
					maxBounds.y = v.y;
				if(v.z > maxBounds.z)
					maxBounds.z = v.z;
			}
		}
	}

	/** 
	 * Clear all the triangles from the Octree
	 */
	public void clearTriangles() {
		triangleList = new Vector(0,1);
	}
	
	/**
	 * Generate the Octree with the triangles in the triangle list.
	 */
	public void generate() {
		numberOfNodes = 0;
		numberOfTriangles = 0;
		
		//init = false;
	
		//buffers: doesn't give the absolute min width for all the
		//triangles but should be fine for our needs at the moment
		Vect3d t = new Vect3d();
		maxBounds.sub(minBounds,t);
		width = t.length();
		
		Triangle[] triArray = new Triangle[triangleList.size()];
		for (int i=0; i<triangleList.size(); i++) {
			triArray[i] = (Triangle)triangleList.elementAt(i);
		}

		Triangle.calculateTriangleNeighbors(triArray);
		shadowVolume = new ShadowVolume(triArray);

		
		numberOfTriangles = triArray.length;
		System.out.println("trying to build octree with "+numberOfTriangles+" tris");
		root = new Node(null, 0, 0, 0, width, triArray);

		System.out.println("octree built: "+numberOfTriangles+" triangles with "+numberOfNodes+" nodes");

		//Triangle.calculateTriangleNeighbors(triArray);
		//shadowVolume = new ShadowVolume(triArray);
		
		//**************
		//buffers: uncomment createLightmaps() to enable lightmaps
		//disabled by default for loading reasons.
		//Lightmap.width and Lightmap.height controls the lightmap
		//size. it is currently 16x16. 8x8 is way faster but doesn't
		//look as good. 
		//**************
		//createLightmaps();
	}

	/**
	 * Brute force drawing of the Octree.
	 * @param gldraw jogl gl interface
	 */
	public void draw(GLDrawable gldraw) {
		if(root != null)	
			root.draw(gldraw);
	}

	/**
	 * Draws the Octree by only drawing nodes that are in the camera
	 * frustum.
	 * @param gldraw jogl gl interface
	 * @param frustum the frustum to check against
	 */
	public void draw(GLDrawable gldraw, Frustum frustum) {
		drawNodes = 0;
		renderedTris = 0;
	
		if(root != null)
			root.draw(gldraw, frustum);

	//	System.out.println("drew "+drawNodes+" nodes ("+Triangle.triDrawCount+" triangles)");
	}

	/**
	 * Draw the Octree's shadow volume.
	 * @param gldraw GLDrawable
	 * @param light light id
	 */
	public void drawShadowVolume(GLDrawable gldraw,int light) {
		if(shadowVolume != null)
			shadowVolume.draw(gldraw,light);
	}
	
	/**
	 * Checks a collision between an entity and triangles in the Octree.
	 * This also handles collision reaction if collision is detected.
	 * @param entity entity to check
	 */
	public void checkCollision(MobileEntity entity) {
		CollisionInfo info = new CollisionInfo(entity.getPosition(),entity.getVelocity(),entity.getBoundingSphere());
		
		if(root != null)
			root.checkCollision(info);

		//temporary collision reaction code
		if(info.getCollision()) {
			if(info.edgeCollision) {
				//entity.getVelocity().invert();
				Physics.handleEdgeCollision(entity, info.getCollisionPoint());
				//System.out.println("Distance = "+info.getDistance());
				//System.out.println("Reacting to edge collision.");
				//System.out.print("edge ");
			} else {
				Vect3d nvel = new Vect3d();
				Vect3d.getBounceVect(entity.getVelocity(),info.getTriNormal(),nvel);
				entity.setVelocity(nvel);
			}
			//System.out.println("Distance = "+info.getDistance());
			
			boolean t = entity.collide(null);

			//System.out.println("collision");
			//System.out.println("triangles checked: "+info.trianglesChecked);
		}

	}

	/**
	 * Create lightmaps for Triangles in Octree.
	 */
	public void createLightmaps() {
		if(root != null) 
			root.createLightmaps();
	}

	/**
	 * Check to see if a line is blocked by a Triangle in the Octree.
	 */
	public boolean isLineBlocked(Vect3d p1, Vect3d p2) {
		if(root != null)
			return root.isLineBlocked(p1,p2);

		return false;
	}
}
