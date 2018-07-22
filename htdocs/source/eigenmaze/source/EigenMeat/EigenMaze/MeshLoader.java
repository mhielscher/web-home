package EigenMeat.EigenMaze;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;

/**
 * A factory class for creating standard meshes. Unique meshes are cached.
 */
public class MeshLoader {
	private static LinkedList meshList  = new LinkedList();
  
	/**
	 * Gets a cached mesh by name. Not all meshes are cached by their 
	 * given name - many have suffixes related to size or texture data.
   	 * @param name the name of the Mesh to retrieve.
         * @return The cached Mesh matching the name, or null if no such Mesh 
	 * is cached.
   	 */
	public static Mesh getMesh(String name) {
    		ListIterator iter = meshList.listIterator(0);
		Mesh  current;
		while (iter.hasNext()) {
      			current = (Mesh)iter.next();
      			if(current.getName().equals(name)) 
			return current;
    		}

    		return null;
  	}

	/**
	 * Create a rectangular prism (box) with specified dimensions and cache it.
	 * @param name the name under which to cache this Mesh.
	 * @param x the x dimension (width) of the box.
	 * @param y the y dimension (height) of the box.
	 * @param z the z dimension (depth) of the box.
	 * @return The box Mesh with specified dimensions, centered at (0,0,0).
	 */
	public static Mesh createBox(String name, float x, float y, float z) {
		Mesh aMesh = getMesh(name);
		if(aMesh != null)
			return aMesh;

		aMesh = createBox(x,y,z);

		Mesh mesh = new Mesh(name,aMesh.getTriangles());
		meshList.add(mesh);

		return mesh;
	}

	/**
	 * Create a rectangular prism (box) with specified dimensions and texture and cache it.
	 * The Mesh will actually be cached under the name String(name+"+"+texture).
	 * @param name the base name under which to cache this Mesh.
	 * @param x the x dimension (width) of the box.
	 * @param y the y dimension (height) of the box.
	 * @param z the z dimension (depth) of the box.
	 * @param texture the filepath of the texture to be used.
	 * @return The box Mesh with specified dimensions and texture, centered at (0,0,0).
	 */
	public static Mesh createBox(String name, float x, float y, float z, String texture) {
		Texture tex = TextureManager.getInstance().loadTexture(texture);
		if(tex == null)
			System.out.println("error");
		Mesh result = createBox(name+"+"+texture, x, y, z);
		Triangle[] t = result.getTriangles();
		for (int i=0; i<t.length; i++)
			t[i].setTexture(tex);
		return result;
	}
	
	/**
	 * Create a rectangular prism (box) with specified dimensions and texture, but do not cache it.
	 * @param x the x dimension (width) of the box.
	 * @param y the y dimension (height) of the box.
	 * @param z the z dimension (depth) of the box.
	 * @param texture the filepath of the texture to be used.
	 * @return The box Mesh with specified dimensions and texture, centered at (0,0,0).
	 */
	public static Mesh createBox(float x, float y, float z, String texture) {
		Texture tex = TextureManager.getInstance().loadTexture(texture);
		if(tex == null)
			System.out.println("bob");
		Mesh result = createBox(x, y, z);
		Triangle[] t = result.getTriangles();
		for (int i=0; i<t.length; i++)
			t[i].setTexture(tex);
		return result;
	}

	/**
	 * Creates a square of size X and Z
	 * @param x the size of square in x direction
	 * @param z the size of square in z direction
	 * @param texture location of texture for mesh square
	 */
	public static Mesh createSquareXZ(float x, float z, String texture) {
		Texture tex = TextureManager.getInstance().loadTexture(texture);
		Mesh result = createSquareXZ(x,z);
		Triangle[] t = result.getTriangles();
		for(int i=0; i<t.length; i++)
			t[i].setTexture(tex);
		return result;
	}
	
	/**
	 * Create a rectangular prism (box) with specified dimensions and default texture,
	 * but do not cache it.
	 * @param x the x dimension (width) of the box.
	 * @param y the y dimension (height) of the box.
	 * @param z the z dimension (depth) of the box.
	 * @return The box Mesh with specified dimensions, centered at (0,0,0).
	 */
	public static Mesh createBox(float x, float y, float z) {
		Vect3d[] vertices = new Vect3d[8];
		vertices[0] = new Vect3d(-x/2, -y/2, -z/2);
		vertices[1] = new Vect3d(-x/2, y/2, -z/2);
		vertices[2] = new Vect3d(x/2, y/2, -z/2);
		vertices[3] = new Vect3d(x/2, -y/2, -z/2);
		vertices[4] = new Vect3d(-x/2, -y/2, z/2);
		vertices[5] = new Vect3d(-x/2, y/2, z/2);
		vertices[6] = new Vect3d(x/2, y/2, z/2);
		vertices[7] = new Vect3d(x/2, -y/2, z/2);

		//possibly temporary hard-coded texture stuff; basing proportions on height and smallest dimension
		float xProp = (x/y)*0.5f;
		float yProp = 0.5f;
		float zProp = (z/y)*0.5f;
		Point2D.Float[] uvPoints = new Point2D.Float[12];
		uvPoints[0] = new Point2D.Float(0, 0);
		uvPoints[1] = new Point2D.Float(0, yProp);
		uvPoints[2] = new Point2D.Float(xProp, yProp);
		uvPoints[3] = new Point2D.Float(xProp, 0);
		uvPoints[4] = new Point2D.Float(0, yProp*2+yProp*(z/y));
		uvPoints[5] = new Point2D.Float(0, yProp+yProp*(z/y));
		uvPoints[6] = new Point2D.Float(xProp, yProp+yProp*(z/y));
		uvPoints[7] = new Point2D.Float(xProp, yProp*2+yProp*(z/y));
		uvPoints[8] = new Point2D.Float(zProp, 0);
		uvPoints[9] = new Point2D.Float(zProp, yProp);
		uvPoints[10] = new Point2D.Float(xProp+zProp, 0);
		uvPoints[11] = new Point2D.Float(xProp+zProp, yProp);

		//String boxTexture = "data/stone.png";
		Texture boxTexture = TextureManager.getInstance().loadTexture("data/textures/stone.png");

		Triangle[] triangles = new Triangle[12];
		triangles[0] = new Triangle(vertices[0], vertices[2], vertices[3], uvPoints[0], uvPoints[2], uvPoints[3], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[1] = new Triangle(vertices[0], vertices[1], vertices[2], uvPoints[0], uvPoints[1], uvPoints[2], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[2] = new Triangle(vertices[0], vertices[4], vertices[1], uvPoints[0], uvPoints[8], uvPoints[1], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[3] = new Triangle(vertices[4], vertices[5], vertices[1], uvPoints[8], uvPoints[9], uvPoints[1], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[4] = new Triangle(vertices[7], vertices[5], vertices[4], uvPoints[7], uvPoints[5], uvPoints[4], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[5] = new Triangle(vertices[7], vertices[6], vertices[5], uvPoints[7], uvPoints[6], uvPoints[5], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[6] = new Triangle(vertices[7], vertices[2], vertices[6], uvPoints[10], uvPoints[2], uvPoints[11], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[7] = new Triangle(vertices[7], vertices[3], vertices[2], uvPoints[10], uvPoints[3], uvPoints[2], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[8] = new Triangle(vertices[0], vertices[3], vertices[4], uvPoints[0], uvPoints[5], uvPoints[4], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[9] = new Triangle(vertices[7], vertices[4], vertices[3], uvPoints[7], uvPoints[4], uvPoints[3], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[10] = new Triangle(vertices[1], vertices[5], vertices[2], uvPoints[1], uvPoints[5], uvPoints[2], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		triangles[11] = new Triangle(vertices[5], vertices[6], vertices[2], uvPoints[5], uvPoints[6], uvPoints[2], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), boxTexture);
		/*
		Triangle[] triangles = new Triangle[12];
		triangles[0] = new Triangle(0, 2, 3, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[1] = new Triangle(0, 1, 2, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[2] = new Triangle(0, 4, 1, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[3] = new Triangle(4, 5, 1, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[4] = new Triangle(7, 5, 4, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[5] = new Triangle(7, 6, 5, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[6] = new Triangle(7, 2, 6, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[7] = new Triangle(7, 3, 2, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[8] = new Triangle(0, 5, 4, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[9] = new Triangle(7, 4, 3, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[10] = new Triangle(1, 5, 2, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		triangles[11] = new Triangle(5, 6, 2, new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
		*/
		Mesh mesh = new Mesh(triangles);

		return mesh;
	}



	/**
	 * Creates a square mesh of size x and y
	 * @param x size of square in x direction
	 * @param z size of square in y direction
	 */
      public static Mesh createSquareXZ(float x, float z) {
                Vect3d[] vertices = new Vect3d[4];
                vertices[0] = new Vect3d(-x/2, 0, z/2);
                vertices[1] = new Vect3d(x/2, 0, z/2);
                vertices[2] = new Vect3d(x/2, 0, -z/2);
                vertices[3] = new Vect3d(-x/2, 0, -z/2);

                Point2D.Float[] uvPoints = new Point2D.Float[4];
                uvPoints[0] = new Point2D.Float(0, 1);
                uvPoints[1] = new Point2D.Float(1, 1);
                uvPoints[2] = new Point2D.Float(1, 0);
                uvPoints[3] = new Point2D.Float(0, 0);
                
		//String texture = "data/stone.png";
                Texture texture = TextureManager.getInstance().loadTexture("data/textures/stone.png");

                Triangle[] triangles = new Triangle[2];
                triangles[0] = new Triangle(vertices[0], vertices[1], vertices[2], uvPoints[0], uvPoints[1], uvPoints[2], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), texture);
                triangles[1] = new Triangle(vertices[2], vertices[3], vertices[0], uvPoints[2], uvPoints[3], uvPoints[0], new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)), texture);

		Mesh mesh = new Mesh(triangles);

		return mesh;
	}
/*
1------------------2
|\		   |\
| \                | \
|  5-------------------6
|  |               |   |
|  |               |   |
|  |               |   |
0--|---------------3   |
 \ |                \  |
  \|                 \ |
   4-------------------7
*/

	private static class Material {
		String name;
		Color color;
		String texture;
	
		public Material() {
		}
		
		public Material(String n, Color c, String t) {
			name = n;
			color = c;
			texture = t;
		}
		
		public String getName() {
			return name;
		}
		
		public Color getColor() {
			return color;
		}
		
		public String getTextureName() {
			return texture;
		}
		
		public void setName(String n) {
			name = n;
		}
		
		public void setColor(Color c) {
			color = c;
		}
		
		public void setTexture(String t) {
			texture = t;
		}
	}
	
	/**
	 * Loads a mesh object from a Wavefront OBJ file, complete with color and
	 * UV texture mapping if applicable. This method recognizes only a subset
	 * of the Wavefront format corresponding to the features of the engine.
	 * @param filepath the filepath to the model file.
	 * @param scale the scale at which to generate the resulting Mesh.
	 * @return the Mesh representing the scaled and textured model.
	 */
	public static Mesh loadMesh(String filepath, float scale) {
		if (filepath == null || scale <= 0)
			return null;
		
		Mesh cache = getMesh(filepath+"+"+scale);

		if(cache != null)
			return cache;
		
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Logging goes here.");
			return null;
		}
		Vector vertices = new Vector(0,1);
		Vector triangles = new Vector(0,1);
		Vector texPoints = new Vector(0,1);
		Vector materials = new Vector(0,1);
		Vector normals = new Vector(0,1);
		
		String line = null;
		try {
			line = file.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Logging goes here.");
			return null;
		}
		
		Vector allMtls = new Vector(0,1);
		Material currentMaterial = new Material("blah", Color.WHITE, null);
		while (line != null) {
			//System.out.println(line);
			if (line.length() == 0 || line.charAt(0) != '#') { //skip empty lines and comments
				String[] elements = line.split("\\s+"); //regex to match one or more whitespace characters
				if (elements[0].equals("mtllib")) {
					Vector newmtl = loadMaterialLibrary(filepath.substring(0, filepath.lastIndexOf('/')+1)+elements[1]);
					for (int i=0; i<newmtl.size(); i++)
						allMtls.add(newmtl.get(i));
				}
				else if (elements[0].equals("v")) {
					vertices.add(new Vect3d(Float.parseFloat(elements[1])*scale, Float.parseFloat(elements[2])*scale, Float.parseFloat(elements[3])*scale));
				}
				else if (elements[0].equals("vn")) {
					normals.add(new Vect3d(Float.parseFloat(elements[1]), Float.parseFloat(elements[2]), Float.parseFloat(elements[3])));
				}
				else if (elements[0].equals("vt")) {
					texPoints.add(new Point2D.Float(Float.parseFloat(elements[1]), Float.parseFloat(elements[2])));
				}
				else if (elements[0].equals("g")) {
				}
				else if (elements[0].equals("usemtl")) {
					for (int i=0; i<allMtls.size(); i++) {
						Material thisMtl = (Material)allMtls.get(i);
						if (thisMtl.getName().equals(elements[1])) {
							currentMaterial = thisMtl;
							break;
						}
					}
				}
				else if (elements[0].equals("f")) {
					if(elements.length != 4) {
						System.out.println("OBJ Loader: found non-triangle face. only triangles are supported");
					} else {
						String[] vertex = elements[1].split("/");
						Vect3d v1 = (Vect3d)vertices.get(Integer.parseInt(vertex[0])-1);
						Point2D.Float t1 = null;
						if (vertex.length > 1)
							t1 = (Point2D.Float)texPoints.get(Integer.parseInt(vertex[1])-1);
						Vect3d norm = new Vect3d();
						if(vertex.length > 2)
							norm = (Vect3d)normals.get(Integer.parseInt(vertex[2])-1);

						vertex = elements[2].split("/");
						Vect3d v2 = (Vect3d)vertices.get(Integer.parseInt(vertex[0])-1);
						Point2D.Float t2 = null;
						if (vertex.length > 1)
							t2 = (Point2D.Float)texPoints.get(Integer.parseInt(vertex[1])-1);
						vertex = elements[3].split("/");
						Vect3d v3 = (Vect3d)vertices.get(Integer.parseInt(vertex[0])-1);
						Point2D.Float t3 = null;
						if (vertex.length > 1)
							t3 = (Point2D.Float)texPoints.get(Integer.parseInt(vertex[1])-1);
						if(currentMaterial.getTextureName() != null) {
							Texture tex = TextureManager.getInstance().loadTexture(currentMaterial.getTextureName());
							Triangle tri = new Triangle(v1, v2, v3, t1, t2, t3, currentMaterial.getColor(), tex);
							triangles.add(tri);
						} else {
							triangles.add(new Triangle(v1, v2, v3,currentMaterial.getColor()));
	
						}
					}
				}
			}
			try {
				line = file.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Logging goes here.");
				return null;
			}
		}
		System.out.println("loaded model "+filepath+" ("+triangles.size()+" triangles)");
		
		// create the mesh
		Vect3d[] vert = new Vect3d[vertices.size()];
		
		//center the model on the origin
		float minx=0,maxx=0,miny=0,maxy=0,minz=0,maxz=0;
		for (int i=0; i<vertices.size(); i++) {
			vert[i] = (Vect3d)vertices.elementAt(i);
			
			if(vert[i].x < minx)
                        	minx = vert[i].x;
                        if(vert[i].x > maxx)
                     		maxx = vert[i].x;
                        if(vert[i].y < miny)
                        	miny = vert[i].y;
                        if(vert[i].y > maxy)
                        	maxy = vert[i].y;
                        if(vert[i].z < minz)
                        	minz = vert[i].z;
                        if(vert[i].z > maxz)
                        	maxz = vert[i].z;
		}
		float xoffset = minx + (maxx-minx)/2;
                float yoffset = miny + (maxy-miny)/2;
                float zoffset = minz + (maxz-minz)/2;
                for(int i=0; i<vert.length; i++) {
                	vert[i].x -= xoffset;
             	   	vert[i].y -= yoffset;
                	vert[i].z -= zoffset;
                }

		Triangle[] tris = new Triangle[triangles.size()];
		for (int i=0; i<triangles.size(); i++)
			tris[i] = (Triangle)triangles.elementAt(i);
		Mesh loaded = new Mesh(filepath+"+"+scale, tris);
		meshList.add(loaded);
		
		return loaded;
	}
	
	private static Vector loadMaterialLibrary(String filepath) {
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Logging goes here.");
			return null;
		}
		
		Vector materials = new Vector(0,1);
		
		String line = null;
		try {
			line = file.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Logging goes here.");
			return null;
		}
		
		while (line != null) {
			if (line.length() == 0 || line.charAt(0) != '#') { //skip empty lines and comments
				String[] elements = line.split("\\s+");
				if (elements[0].equals("newmtl")) {
					materials.add(new Material(elements[1], Color.WHITE, null));
				}
				else if (elements[0].equals("Ka")) {
				}
				else if (elements[0].equals("Kd")) {
					((Material)materials.get(materials.size()-1)).setColor(new Color(Float.parseFloat(elements[1]), Float.parseFloat(elements[2]), Float.parseFloat(elements[3])));
				}
				else if (elements[0].equals("map_Kd")) {
					String texturePath = filepath.substring(0, filepath.lastIndexOf('/')+1)+elements[1];
					((Material)materials.get(materials.size()-1)).setTexture(texturePath);
					System.out.println("Loading texture "+texturePath);
					TextureManager.getInstance().loadTexture(texturePath);
				}
			}
			try {
				line = file.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Logging goes here.");
				return null;
			}
		}
		return materials;
	}
	
	/* This function is deprecated. I have switched to Wavefront OBJ files due to their
	   similarity to our existing mesh files, because they support UV mapping
	   perfectly, and because almost any modeling program can export to .OBJ.
	
	File format:
	x y z r g b
	x y z r g b
	
	v1 v2 v3 r g b
	v1 v2 v3 r g b
	*/
	/**
	 * @deprecated As of iteration 2, replaced by {@link #loadMesh(String, float)}. Loads
	 * a colored but non-textured Mesh from the older .mesh file type and caches it.
	 * @param filepath the file path to the model file.
	 * @return the Mesh generated from the file.
	 */
	public static Mesh loadOldMesh(String filepath) {
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Logging goes here.");
			return null;
		}
		Vector vertices = new Vector(0,1);
		Vector triangles = new Vector(0,1);
		
		// first the vertices
		String line = null;
		try {
			line = file.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		while (!line.equals("!")) {
			String[] numbers = line.split(" ");
			Color c = null;
			if (Float.parseFloat(numbers[3]) >= 0 && Float.parseFloat(numbers[4]) >= 0 && Float.parseFloat(numbers[5]) >= 0)
				c = new Color(Float.parseFloat(numbers[3]), Float.parseFloat(numbers[4]), Float.parseFloat(numbers[5]));
			vertices.add(new Vect3d(Float.parseFloat(numbers[0]), Float.parseFloat(numbers[1]), Float.parseFloat(numbers[2])));
			try {
				line = file.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		// now the triangles
		try {
			line = file.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		while (!line.equals("!")) {
			String[] numbers = line.split(" ");
			Color c = null;
			if (Float.parseFloat(numbers[3]) >= 0 && Float.parseFloat(numbers[4]) >= 0 && Float.parseFloat(numbers[5]) >= 0)
				c = new Color(Float.parseFloat(numbers[3]), Float.parseFloat(numbers[4]), Float.parseFloat(numbers[5]));
			triangles.add(new Triangle((Vect3d)vertices.elementAt(Integer.parseInt(numbers[0])), (Vect3d)vertices.elementAt(Integer.parseInt(numbers[1])), (Vect3d)vertices.elementAt(Integer.parseInt(numbers[2])), c));
			try {
				line = file.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		// create the mesh
		Vect3d[] vert = new Vect3d[vertices.size()];
		for (int i=0; i<vertices.size(); i++)
			vert[i] = (Vect3d)vertices.elementAt(i);
		Triangle[] tris = new Triangle[triangles.size()];
		for (int i=0; i<triangles.size(); i++)
			tris[i] = (Triangle)triangles.elementAt(i);
		Mesh loaded = new Mesh(filepath, tris);
		meshList.add(loaded);
		
		return loaded;
	}
}
