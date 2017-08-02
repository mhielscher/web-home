package EigenMeat.EigenMaze;

import java.util.Vector;
import net.java.games.jogl.*;

/**
 * A class to help in the creation and updating of shadow volumes.
 */
public class ShadowVolume {
	private ShadowVolumeEdge edges[];
	private Triangle tris[];
	private boolean triArray;	
		
	/**
	 * Constructor. Pass in an array of Triangles and the class will 
	 * process them and initialize the edges.
	 */
	public ShadowVolume(Triangle tris[]) {
		initializeEdges(tris);
		triArray = false;
	}

	/**
	 * Constructor. Pass in an array of Triangles. These Triangles are
	 * referenced directly. This does not create a list of edges.
	 */
	public ShadowVolume(Triangle tris[], boolean t) {
		this.tris = tris;
		triArray = true;
	}

	private void initializeEdges(Triangle tris[]) {
		boolean t;
		ShadowVolumeEdge edge;
		Vector e = new Vector();
                Triangle.calculateTriangleNeighbors(tris);
                for(int i=0; i<tris.length; i++) {
                        for(int j=0; j<3; j++) {
                                if(tris[i].hasNeighbor(j)) {
                                        if(!tris[i].getNormal().equals(tris[i].getNeighbor(j).getNormal())) {
						Vect3d p1 = tris[i].getVertex(j);
						Vect3d p2 = tris[i].getVertex((j+1)%3);
						t = false;
						for(int k=0; k<e.size(); k++) {
							edge = (ShadowVolumeEdge)e.elementAt(k);
							//if((edge.getPoint(0).equals(p1) && edge.getPoint(1).equals(p2) && edge.getNormal(0).equals(tris[i].getNormal())) ||
							  // (edge.getPoint(0).equals(p2) && edge.getPoint(1).equals(p1) && edge.getNormal(1).equals(tris[i].getNormal())))
							 if((edge.getPoint(0).equals(p1) && edge.getPoint(1).equals(p2)) ||
                                                          (edge.getPoint(0).equals(p2) && edge.getPoint(1).equals(p1) ))

								t = true; 
						}
					
						if(t == false) {
                                              		edge = new ShadowVolumeEdge();
                                                	edge.setPoints(p1,p2);
							edge.setNeighborNormals(tris[i].getNormal(),tris[i].getNeighbor(j).getNormal());
							edge.setNeighborDistance(tris[i].getPlaneDistance(),tris[i].getNeighbor(j).getPlaneDistance());
							e.add(edge);
						}	

                                	}
                                } else {
					Vect3d p1 = tris[i].getVertex(j);
                                        Vect3d p2 = tris[i].getVertex((j+1)%3);
					
					edge = new ShadowVolumeEdge(p1,p2,tris[i].getNormal(),tris[i].getPlaneDistance());
                                       	e.add(edge);	
				}
                        }
                }

		edges = new ShadowVolumeEdge[e.size()];
		for(int i=0; i<edges.length; i++) {
			edges[i] = (ShadowVolumeEdge)e.elementAt(i);
		}
	
		//System.out.println("out of "+tris.length+" triangles, "+edges.length+" edges are able to cast shadows");
	}

	/**
	 * Draw the shadow with respect to a light.
	 * @param light the light id to use to calculate the shadow
	 */
	public void draw(GLDrawable gldraw,int light) {
		draw(gldraw,EigenEngine.instance().getLightManager().getLight(light).getPosition());
	}

	/**
	 * Draw the shadow with respect to a light.
	 * @param light Vect3d position of the light
	 */
	public void draw(GLDrawable gldraw,Vect3d light) {
		GL gl = gldraw.getGL();	
	
		if(triArray) {
			for(int i=0; i<tris.length; i++) {
				if(tris[i].isPointInFront(light))
					tris[i].setVisible(true);
				else
					tris[i].setVisible(false);
			}
		} else {	
			for(int i=0; i<edges.length; i++) {
				edges[i].setCastShadow(light);
			}
		}		

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_ENABLE_BIT | GL.GL_POLYGON_BIT | GL.GL_STENCIL_BUFFER_BIT);
                gl.glDisable(GL.GL_LIGHTING);
                gl.glDepthMask(false);
                gl.glDepthFunc(GL.GL_LEQUAL);
                gl.glEnable(GL.GL_STENCIL_TEST);
                gl.glColorMask(false,false,false,false);
                gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xFFFFFFFF);

                gl.glFrontFace(GL.GL_CCW);
                gl.glStencilOp(GL.GL_KEEP,GL.GL_KEEP,GL.GL_INCR);
		if(triArray)
			drawSinglePassTri(gldraw, light);
		else
                	drawSinglePass(gldraw, light);
                gl.glFrontFace(GL.GL_CW);
                gl.glStencilOp(GL.GL_KEEP,GL.GL_KEEP,GL.GL_DECR);
                if(triArray)
			drawSinglePassTri(gldraw, light);
		else
			drawSinglePass(gldraw,light);

		gl.glPopAttrib();
	}

	private void drawSinglePassTri(GLDrawable gldraw, Vect3d light) {
                GL gl = gldraw.getGL();

                for(int i=0; i<tris.length; i++) {
                        if(tris[i].isVisible()) {
				for(int j=0; j<3; j++) {
				if(!tris[i].hasNeighbor(j) || (tris[i].hasNeighbor(j) && !tris[i].getNeighbor(j).isVisible())) {
                                Vect3d v1 = tris[i].getVertex(j);
                                Vect3d v2 = tris[i].getVertex((j+1)%3);

                                Vect3d v3 = new Vect3d(v1);
                                v3.sub(light);
                                v3.mult(300);

                                Vect3d v4 = new Vect3d(v2);
                                v4.sub(light);
                                v4.mult(300);

                                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                                        gl.glVertex3f(v1.x,v1.y,v1.z);
                                        gl.glVertex3f(v1.x+v3.x,v1.y+v3.y,v1.z+v3.z);
                                        gl.glVertex3f(v2.x,v2.y,v2.z);
                                        gl.glVertex3f(v2.x+v4.x,v2.y+v4.y,v2.z+v4.z);
                                gl.glEnd();
				}
				}

                        }
		}
	}

	private void drawSinglePass(GLDrawable gldraw, Vect3d light) {
		GL gl = gldraw.getGL();

		for(int i=0; i<edges.length; i++) {
			if(edges[i].canCastShadow() == true) {
				Vect3d v1 = edges[i].getPoint(0);
				Vect3d v2 = edges[i].getPoint(1);
				
				if(edges[i].neighborNormals.length > 1 && edges[i].neighborNormals[1].dot(light)+edges[i].neighborDistance[1] > 0) {
					v1 = edges[i].getPoint(1);
                                	v2 = edges[i].getPoint(0);
				}	
				
				Vect3d v3 = new Vect3d(v1);
                                v3.sub(light);
                                v3.mult(300);

                                Vect3d v4 = new Vect3d(v2);
                                v4.sub(light);
                                v4.mult(300);

                                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                                	gl.glVertex3f(v1.x,v1.y,v1.z);
                                        gl.glVertex3f(v1.x+v3.x,v1.y+v3.y,v1.z+v3.z);
                                        gl.glVertex3f(v2.x,v2.y,v2.z);
                                        gl.glVertex3f(v2.x+v4.x,v2.y+v4.y,v2.z+v4.z);
                                gl.glEnd();

			}
		}
	}
	
	private class ShadowVolumeEdge {
		private Vect3d points[];
                public Vect3d neighborNormals[];
                public float neighborDistance[];
	
		private boolean castsShadow;
	
		private boolean singleTri;
		
                public ShadowVolumeEdge() {
                        points = new Vect3d[2];
                        neighborNormals = new Vect3d[2];
                        neighborDistance = new float[2];
                }

		public ShadowVolumeEdge(Vect3d a, Vect3d b, Vect3d norm, float dis) {
			points = new Vect3d[2];
			setPoints(a,b);
			neighborNormals = new Vect3d[1];
			neighborNormals[0] = new Vect3d(norm);
			neighborDistance = new float[1];
			neighborDistance[0] = dis;
		}
		
                public void setPoints(Vect3d a, Vect3d b) {
                        points[0] = new Vect3d(a);
                        points[1] = new Vect3d(b);
                }

		public Vect3d getPoint(int i) {
			return points[i];
		}
	
		public Vect3d getNormal(int i) {
			return neighborNormals[i];
		}
		
                public void setNeighborNormals(Vect3d a, Vect3d b) {
                        neighborNormals[0] = new Vect3d(a);
                        neighborNormals[1] = new Vect3d(b);
                }

                public void setNeighborDistance(float d1, float d2) {
                        neighborDistance[0] = d1;
                        neighborDistance[1] = d2;
                }

		public void setCastShadow(Vect3d light) {
			castsShadow = false;
			
			float d1 = neighborNormals[0].dot(light)+neighborDistance[0];
			if(neighborNormals.length == 1) {
				if(d1 > 0)
					castsShadow = true;
			} else {
				float d2 = neighborNormals[1].dot(light)+neighborDistance[1];

				if((d1*d2) < 0)
					castsShadow = true;
			}
		}

		public boolean canCastShadow() {
			return castsShadow;
		}
	}
}
