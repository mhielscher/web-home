package EigenMeat.EigenMaze;

import junit.framework.*;

public class MeshLoaderTest extends TestCase {
	public MeshLoaderTest() {
		super();
	}
	
	public MeshLoaderTest(String name) {
		super(name);
	}
	
	public void testCreateBox() {
		Mesh m = MeshLoader.createBox("test", 2, 4, 2);
		assertTrue(m.getName().equals("test"));
		Triangle[] t = m.getTriangles();
		assertTrue(t[0].equals(new Triangle(new Vect3d(-1,-2,-1), new Vect3d(1,2,-1), new Vect3d(1,-2,-1), null)));
		assertTrue(t[1].equals(new Triangle(new Vect3d(-1,-2,-1), new Vect3d(-1,2,-1), new Vect3d(1,2,-1), null)));
		assertTrue(t[2].equals(new Triangle(new Vect3d(-1,-2,-1), new Vect3d(-1,-2,1), new Vect3d(-1,2,-1), null)));
		assertTrue(t[3].equals(new Triangle(new Vect3d(-1,-2,1), new Vect3d(-1,2,1), new Vect3d(-1,2,-1), null)));
		assertTrue(t[4].equals(new Triangle(new Vect3d(1,-2,1), new Vect3d(-1,2,1), new Vect3d(-1,-2,1), null)));
		assertTrue(t[5].equals(new Triangle(new Vect3d(1,-2,1), new Vect3d(1,2,1), new Vect3d(-1,2,1), null)));
		assertTrue(t[6].equals(new Triangle(new Vect3d(1,-2,1), new Vect3d(1,2,-1), new Vect3d(1,2,1), null)));
		assertTrue(t[7].equals(new Triangle(new Vect3d(1,-2,1), new Vect3d(1,-2,-1), new Vect3d(1,2,-1), null)));
		assertTrue(t[8].equals(new Triangle(new Vect3d(-1,-2,-1), new Vect3d(1,-2,-1), new Vect3d(-1,-2,1), null)));
		assertTrue(t[9].equals(new Triangle(new Vect3d(1,-2,1), new Vect3d(-1,-2,1), new Vect3d(1,-2,-1), null)));
		assertTrue(t[10].equals(new Triangle(new Vect3d(-1,2,-1), new Vect3d(-1,2,1), new Vect3d(1,2,-1), null)));
		assertTrue(t[11].equals(new Triangle(new Vect3d(-1,2,1), new Vect3d(1,2,1), new Vect3d(1,2,-1), null)));
	}
	
	public void testGetMesh() {
		Mesh m = MeshLoader.createBox("name", 3, 4, 6);
		Mesh n = MeshLoader.getMesh("name");
		assertTrue(m.equals(n));
	}
}