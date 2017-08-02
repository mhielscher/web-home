package EigenMeat.EigenMaze;

import java.awt.*;
import java.awt.geom.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;
import java.nio.*;

/**
 * Class to create lightmap textures.
 */
public class Lightmap extends Texture {
	public static final int YZ = 0;
	public static final int XZ = 1;
	public static final int XY = 2;

	private int primaryPlane;

	private Vect3d v[];
	private Vect3d normal;

	private Point2D.Float UV[];
	private float maxu,maxv,minu,minv;

	private int width, height;

	/**
	 * Constructor. Pass in 3 vertices of a triangle, in world coordinates,
	 * and triangle normal. The lightmap will be created for this triangle.
	 * @param v1 vertex1
	 * @param v2 vertex2
	 * @param v3 vertex3
	 * @param normal triangle plane
	 */
	public Lightmap(Vect3d v1, Vect3d v2, Vect3d v3, Vect3d normal) {
		super();

		width = 16;
		height = 16;

		v = new Vect3d[3];
		v[0] = v1;
		v[1] = v2;
		v[2] = v3;

		this.normal = normal;

		calculatePrimaryPlane();
		calculateUVCoordinates();
		calculateLightmap();
	}

	/**
	 * Get UV coordinates.
	 * @return reference to UV array
	 */
	public Point2D.Float[] getUV() {
		return UV;
	}

	private void calculatePrimaryPlane() {
		if(Math.abs(normal.x) > Math.abs(normal.y) && Math.abs(normal.x) > Math.abs(normal.z)) {
                        primaryPlane = XY;
                } else if(Math.abs(normal.y) > Math.abs(normal.x) && Math.abs(normal.y) > Math.abs(normal.z)) {
                        primaryPlane = XZ;
                } else {
                        primaryPlane = XY;
                }
	
	}

	private void calculateUVCoordinates() {
		UV = new Point2D.Float[3];	
	
		switch(primaryPlane) {
			case YZ:
				UV[0] = new Point2D.Float(v[0].y,v[0].z);
				UV[1] = new Point2D.Float(v[1].y,v[1].z);
				UV[2] = new Point2D.Float(v[2].y,v[2].z);
				break;
			case XZ:
                                UV[0] = new Point2D.Float(v[0].x,v[0].z);
                                UV[1] = new Point2D.Float(v[1].x,v[1].z);
                                UV[2] = new Point2D.Float(v[2].x,v[2].z);
                                break;
			case XY:
                                UV[0] = new Point2D.Float(v[0].x,v[0].y);
                                UV[1] = new Point2D.Float(v[1].x,v[1].y);
                                UV[2] = new Point2D.Float(v[2].x,v[2].y);
                                break;
	
		}

		//scale lightmap coordinates
		float minx = UV[0].x;
		float maxx = UV[0].x;
		float miny = UV[0].y;
		float maxy = UV[0].y;
		for(int i=0; i<3; i++) {
			if(UV[i].x < minx)
				minx = UV[i].x;
			if(UV[i].x > maxx)
				maxx = UV[i].x;
			if(UV[i].y < miny)
				miny = UV[i].y;
			if(UV[i].y > maxy)
				maxy = UV[i].y;
		}

		float w = maxx-minx;
		float h = maxy-miny;
		for(int i=0; i<3; i++) {
			UV[i].x = (UV[i].x-minx) / w;
			UV[i].y = (UV[i].y-miny) / h;
		}

		minu = minx;
		maxu = maxx;
		minv = miny;
		maxv = maxy;
	}

	private void calculateLightmap() {
		Color lightmap[][] = new Color[height][width];	
	
		float distance = -normal.dot(v[0]);
                Vect3d UVVector, Vect1, Vect2;
                
		if(primaryPlane == 0) {
                        float x = -(normal.y*minu+normal.z*minv+distance)/normal.x;
                        UVVector = new Vect3d(x,minu,minv);
                        x = -(normal.y*maxu+normal.z*minv+distance)/normal.x;
                        Vect1 = new Vect3d(x,maxu,minv);
                        x = -(normal.y*minu+normal.z*maxv+distance)/normal.x;
                        Vect2 = new Vect3d(x,minu,maxv);
                } else if(primaryPlane == 1) {
                        float y = -(normal.x*minu+normal.z*minv+distance)/normal.y;
                        UVVector = new Vect3d(minu,y,minv);
                        y = -(normal.x*maxu+normal.z*minv+distance)/normal.y;
                        Vect1 = new Vect3d(maxu,y,minv);
                        y = -(normal.x*minu+normal.z*maxv+distance)/normal.y;
                        Vect2 = new Vect3d(minu,y,maxv);
                } else {
                        float z = -(normal.x*minu+normal.y*minv+distance)/normal.z;
                        UVVector = new Vect3d(minu,minv,z);
                        z = -(normal.x*maxu+normal.y*minv+distance)/normal.z;
                        Vect1 = new Vect3d(maxu,minv,z);
                        z = -(normal.x*minu+normal.y*maxv+distance)/normal.z;
                        Vect2 = new Vect3d(minu,maxv,z);
                }
                Vect3d edge1 = new Vect3d(Vect1);
                edge1.sub(UVVector);
                Vect3d edge2 = new Vect3d(Vect2);
                edge2.sub(UVVector);

		 for(int i=0; i<height; i++) {
                        for(int j=0; j<width; j++) {
                                float ufactor = (j/(float)width);
                                float vfactor = (i/(float)height);
                                Vect3d new1 = new Vect3d(edge1);
                                new1.mult(ufactor);
                                Vect3d new2 = new Vect3d(edge2);
                                new2.mult(vfactor);

                                Vect3d lumel = new Vect3d(UVVector);
                                lumel.add(new1);
                                lumel.add(new2);

                                Vect3d light = new Vect3d(5,20,5);

                                Vect3d vlight = new Vect3d(light);
                                vlight.sub(lumel);
                                vlight.normalize();

                                Vect3d tmp = new Vect3d(lumel);
                                tmp.addm(normal,.02f);

                                if(vlight.getAngle(normal) <=  90) {
                                        if(!EigenEngine.instance().getOctree().isLineBlocked(tmp,light)) {

                                        float angle = vlight.getAngle(normal);
                                        if(angle < 90 && angle > -90) {
                                                angle = 1- (Math.abs(angle) / 90f);
                                        } else {
				        	angle = 0;
                                        }

                                        lightmap[i][j] = new Color(50+(int)(205*angle),50+(int)(205*angle),50+(int)(205*angle));
                                        } else
                                                lightmap[i][j] = new Color(50,50,50);
                                } else
                                        lightmap[i][j] = new Color(50,50,50);
                        }
                }


                ByteBuffer data = ByteBuffer.allocateDirect(width*height*3);
                for(int i=0; i<height; i++)
                        for(int j=0; j<width; j++) {
                                data.put((byte)lightmap[i][j].getRed());
                                data.put((byte)lightmap[i][j].getGreen());
                                data.put((byte)lightmap[i][j].getBlue());
                        }

        	set(data,3,GL.GL_RGB,width,height);
	}
}
