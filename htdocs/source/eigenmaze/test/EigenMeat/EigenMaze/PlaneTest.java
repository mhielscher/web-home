package EigenMeat.EigenMaze;

import junit.framework.*;

public class PlaneTest extends TestCase {
	public PlaneTest() {
		super();
	}
	
	public PlaneTest(String name) {
		super(name);
	}
	
	public void testSetAndGet() {
		Plane bob = new Plane();
		bob.set(1,1,1,2);
		assertTrue(bob.getA() == 1);
		assertTrue(bob.getB() == 1);
		assertTrue(bob.getC() == 1);
		assertTrue(bob.getPlaneDistance() == 2);
	}

	public void testPointInFront() {
		Plane bob = new Plane();
		bob.set(0,1,0,0);
		
		Vect3d point = new Vect3d(0,5,0);
		assertTrue(bob.isPointInFront(point));

		point.set(0,-5,0);
		assertFalse(bob.isPointInFront(point));
	}
}
