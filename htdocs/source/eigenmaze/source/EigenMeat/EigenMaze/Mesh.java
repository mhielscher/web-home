package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import java.math.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

/**
 * A Mesh contains vertex and triangle data of a three-dimensional object suitable for
 * drawing into the engine. A game entity is usually represented by a Mesh.
 */
public class Mesh implements Drawable {

	private String name;
	private Triangle[] triangles;
	
	private int displayListID;

	private boolean castShadow;
	private boolean hasNeighborsBeenSet;

	private ShadowVolume shadowVolume;
	
	public Mesh() {
		name = "Bob";
		triangles = new Triangle[0];
		displayListID = -1;
	
		hasNeighborsBeenSet = false;
	}
	
	/**
	 * Constructor for Mesh.
	 * @param name the name of this Mesh.
	 * @param t the array of Triangles that make up the model.
	 */
	public Mesh(String name, Triangle[] t) {
		this.name = name;
		triangles = t;
		Arrays.sort(triangles); //sort for texture ordering for fastest rendering
		displayListID = -1;

		hasNeighborsBeenSet = false;
	}

	public Mesh(Mesh m) {
		this.name = m.getName();
		Triangle tri[] = m.getTriangles();
		triangles = new Triangle[tri.length];
		for(int i=0; i<tri.length;i++) {
			triangles[i] = tri[i];
		}
		Arrays.sort(triangles);
		displayListID = -1;
		hasNeighborsBeenSet = false;
	}
	
	/**
	 * Constructor for Mesh.
	 * @param t the array of Triangles that make up the model.
	 */
	public Mesh(Triangle[] t) {
		name = "null";
		triangles = t;
		Arrays.sort(triangles); //sort for texture ordering for fastest rendering
		displayListID = -1;

		hasNeighborsBeenSet = false;
	}

	/**
	 * Get the mesh name.
	 * @return mesh name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the mesh name.
	 * @param n mesh name
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * Get a reference to the Mesh's triangles.
	 * @return reference to triangles
	 */
	public Triangle[] getTriangles() {
		return triangles;
	}

	/**
	 * Set the triangle data.
	 * @param t triangle array
	 */
	public void setTriangles(Triangle[] t) {
		triangles = t;
		Arrays.sort(triangles); //sort for texture ordering for fastest rendering
		displayListID = -1;
	}

	/**
	 * Copies and translates all Triangles in the mesh by the specified amounts. Meshes are
	 * usually centered at (0,0,0); this will return an array of triangles centered at (x,y,z).
	 * @param x the x shift to apply to each triangle.
	 * @param y the y shift to apply to each triangle.
	 * @param z the z shift to apply to each triangle.
	 * @return the shifted array of Triangles.
	 */
	public Triangle[] translate(float x, float y, float z) {
		Triangle[] newTri = new Triangle[triangles.length];
		for (int i=0; i<triangles.length; i++) {
			Vect3d vert1 = new Vect3d(triangles[i].getVertex1().getX()+x, triangles[i].getVertex1().getY()+y, triangles[i].getVertex1().getZ()+z);
			Vect3d vert2 = new Vect3d(triangles[i].getVertex2().getX()+x, triangles[i].getVertex2().getY()+y, triangles[i].getVertex2().getZ()+z);
			Vect3d vert3 = new Vect3d(triangles[i].getVertex3().getX()+x, triangles[i].getVertex3().getY()+y, triangles[i].getVertex3().getZ()+z);
			newTri[i] = new Triangle(vert1, vert2, vert3, triangles[i].getUV1(), triangles[i].getUV2(), triangles[i].getUV3(), triangles[i].getColor(), triangles[i].getTexture());
			//newTri[i] = new Triangle(vert1, vert2, vert3, triangles[i].getColor());
		}
		Arrays.sort(newTri); //sort for texture ordering for fastest rendering
		return newTri;
	}
	
	private void generateDisplayList(GLDrawable glDraw) {
		//System.out.println("Building display list for "+this.toString());
		GL gl = glDraw.getGL();
		displayListID = gl.glGenLists(1);
		gl.glNewList(displayListID, gl.GL_COMPILE);
		TextureManager.getInstance().unbindTexture(gl);
		for (int i=0; i<triangles.length; i++)
			triangles[i].draw(glDraw);
		gl.glEndList();
	}

	/**
	 * Draw the mesh.
	 * @param glDraw JOGL GLDrawable
	 */
	public void draw(GLDrawable glDraw) {
		GL gl = glDraw.getGL();
	//	if (displayListID == -1)
	//		generateDisplayList(glDraw);
		
		TextureManager.getInstance().unbindTexture(gl);
		//glDraw.getGL().glDisable(GL.GL_CULL_FACE);
		for (int i=0; i<triangles.length; i++) {
			triangles[i].draw(glDraw);
		}
		
		//trying for display lists
	//	gl.glCallList(displayListID);
	}

	/**
	 * Remove all triangles with the specified normal.
	 * @param norm triangle normal
	 */
	public void removeTrianglesWithNormal(Vect3d norm) {
		Vector tri = new Vector();
		
		for(int i=0; i<triangles.length; i++) {
			Vect3d n = triangles[i].getNormal();
			if(!(n.x == norm.x && n.y == norm.y && n.z == norm.z))
				tri.add(triangles[i]);
		}

		Triangle triArray[] = new Triangle[tri.size()];
		for(int i=0; i<tri.size(); i++) {
			triArray[i] = (Triangle)tri.elementAt(i);
		}

		triangles = triArray;
	}

	/**
	 * Draw mesh's shadow.
	 * @param gldraw JOGL GLDrawable
	 * @param position shadow location
	 * @param rotation shadow rotation
	 * @param l light id that will create the shadow
	 */
	public void drawShadow(GLDrawable gldraw, Vect3d position, Vect3d rotation,int l) {
		GL gl = gldraw.getGL();
		Vect3d light = new Vect3d(EigenEngine.instance().getLightManager().getLight(l).getPosition());

		if(hasNeighborsBeenSet == false) {
			calculateTriangleNeighbors();
		}
		
		if(shadowVolume == null)
			shadowVolume = new ShadowVolume(triangles);
		
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRotatef(-rotation.z,0,0,1);
		gl.glRotatef(-rotation.y,0,1,0);
		gl.glTranslatef(-position.x,-position.y,-position.z);
		float m[] = new float[16];
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX,m);
		Matrix.mult(m,light);
		gl.glPopMatrix();

		shadowVolume.draw(gldraw,light);
		
	}

	/**
	 * Calculate triangle neighbors.
	 */
	public void calculateTriangleNeighbors() {
		if(hasNeighborsBeenSet == false)
			Triangle.calculateTriangleNeighbors(triangles);

		hasNeighborsBeenSet = true;
	}
}
