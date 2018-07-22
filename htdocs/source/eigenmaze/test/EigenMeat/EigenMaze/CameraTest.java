package EigenMeat.EigenMaze;

import junit.framework.*;

public class CameraTest extends TestCase {
	public CameraTest() {
		super();
	}
	
	public CameraTest(String name) {
		super(name);
	}
	
	public void testSetPosition() {
		Camera bob = new Camera();
		bob.setPosition(new Vect3d(2,5,-2));
		Vect3d v = bob.getPosition();
		assertTrue(2 == v.x);
		assertTrue(5 == v.y);
		assertTrue(-2 == v.z);
	}

	public void testLookAt() {
		Camera bob = new Camera();
		bob.setLookAt(new Vect3d(0,-2,5));
		Vect3d v = bob.getLookAt();
		assertTrue(0 == v.x);
		assertTrue(-2 == v.y);
		assertTrue(5 == v.z);
	}
}
