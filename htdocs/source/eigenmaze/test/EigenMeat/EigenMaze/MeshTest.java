package EigenMeat.EigenMaze;

import junit.framework.*;

public class MeshTest extends TestCase {
	public MeshTest() {
		super();
	}
	
	public MeshTest(String name) {
		super(name);
	}
	
	public void testTranslate() {
		Triangle[] t = new Triangle[2];
		t[0] = new Triangle(new Vect3d(0,0,0), new Vect3d(1,0,0), new Vect3d(0,0,1), null);
		t[1] = new Triangle(new Vect3d(1,0,1), new Vect3d(1,0,0), new Vect3d(0,0,1), null);
		Mesh m = new Mesh("temp", t);
		Triangle[] t2 = m.translate(1, 4, 2);
		t[0] = new Triangle(new Vect3d(1,4,2), new Vect3d(2,4,2), new Vect3d(1,4,3), null);
		t[1] = new Triangle(new Vect3d(2,4,3), new Vect3d(2,4,2), new Vect3d(1,4,3), null);
		assertTrue(t[0].equals(t2[0]));
		assertTrue(t[1].equals(t2[1]));
	}
}
