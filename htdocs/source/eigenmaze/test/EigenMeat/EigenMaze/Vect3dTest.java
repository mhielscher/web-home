package EigenMeat.EigenMaze;

import junit.framework.*;

public class Vect3dTest extends TestCase {

	public Vect3dTest() {
		super();
	}
	
	public Vect3dTest(String name) {
		super(name);
	}

	public void testGetX() {
		Vect3d v = new Vect3d(12,13,14);
		assertEquals(v.getX(), 12f, .0001);
	}
	
	public void testGetY() {
		Vect3d v = new Vect3d(12,13,14);
		assertEquals(v.getY(), 13f, .0001);
	}
	
	public void testGetZ() {
		Vect3d v = new Vect3d(12,13,14);
		assertEquals(v.getZ(), 14f, .0001);
	}
	
	public void testSet() {
		Vect3d v = new Vect3d(7,8,9);
		v.set(5,4,3);
		assertEquals(v.getX(), 5f, .0001);
		assertEquals(v.getY(), 4f, .0001);
		assertEquals(v.getZ(), 3f, .0001);
		Vect3d t = new Vect3d(0,0,0);
		t.set(v);
		assertEquals(t.getX(), 5f, .0001);
		assertEquals(t.getY(), 4f, .0001);
		assertEquals(t.getZ(), 3f, .0001);
	}
	
	public void testInvert() {
		Vect3d v = new Vect3d(2,3,4);
		v.invert();
		assertEquals(v.getX(), -2f, .0001);
		assertEquals(v.getY(), -3f, .0001);
		assertEquals(v.getZ(), -4f, .0001);
	}
	
	public void testLength() {
		Vect3d v = new Vect3d(1,1,1);
		assertEquals(v.length(), (float)Math.sqrt(3), .0001);
	}
	
	public void testNormalize() {
		Vect3d v = new Vect3d(16,3,-1);
		v.normalize();
		assertEquals(v.length(), 1, .0001);
	}
	
	public void testDot() {
		Vect3d v = new Vect3d(2,-3,1);
		Vect3d t = new Vect3d(4,4,6);
		assertEquals(v.dot(t), 2, .0001);
	}
	
	public void testCross() {
		Vect3d v = new Vect3d(2,-3,1);
		Vect3d t = new Vect3d(4,4,6);
		Vect3d s = v.cross(t);
		assertEquals(s.getX(), -22f, .0001);
		assertEquals(s.getY(), -8f, .0001);
		assertEquals(s.getZ(), 20f, .0001);
	}

	public void testGetAngle() {
		Vect3d v = new Vect3d(0,1,0);
		Vect3d t = new Vect3d(1,0,0);
		assertEquals(v.getAngle(t), 90, .01);
	}
	
	public void testMult() {
		Vect3d v = new Vect3d(1,1,1);
		v.mult(3);
		assertEquals(v.getX(), 3f, .0001);
		assertEquals(v.getY(), 3f, .0001);
		assertEquals(v.getZ(), 3f, .0001);
		Vect3d t = new Vect3d(0,0,0);
		v.mult(2,t);
		assertEquals(t.getX(), 6f, .0001);
		assertEquals(t.getY(), 6f, .0001);
		assertEquals(t.getZ(), 6f, .0001);
	}
	
	public void testDiv() {
		Vect3d v = new Vect3d(6,6,6);
		v.div(3);
		assertEquals(v.getX(), 2f, .0001);
		assertEquals(v.getY(), 2f, .0001);
		assertEquals(v.getZ(), 2f, .0001);
		/*
		Vect3d t = new Vect3d(0,0,0);
		v.div(2,t);
		assertEquals(t.getX(), 1f, .0001);
		assertEquals(t.getY(), 1f, .0001);
		assertEquals(t.getZ(), 1f, .0001);
		*/
	}
	
	public void testSub() {
		Vect3d v = new Vect3d(2,3,4);
		Vect3d t = new Vect3d(3,2,1);
		v.sub(t);
		assertEquals(v.getX(), -1f, .0001);
		assertEquals(v.getY(), 1f, .0001);
		assertEquals(v.getZ(), 3f, .0001);
		Vect3d s = new Vect3d(0,0,0);
		v.sub(t,s);
		assertEquals(s.getX(), -4f, .0001);
		assertEquals(s.getY(), -1f, .0001);
		assertEquals(s.getZ(), 2f, .0001);
	}
	
	public void testAdd() {
		Vect3d v = new Vect3d(2,3,4);
		Vect3d t = new Vect3d(3,2,1);
		v.add(t);
		assertEquals(v.getX(), 5f, .0001);
		assertEquals(v.getY(), 5f, .0001);
		assertEquals(v.getZ(), 5f, .0001);
		/*
		Vect3d s = new Vect3d(0,0,0);
		v.add(t,s);
		assertEquals(s.getX(), 8f, .0001);
		assertEquals(s.getY(), 7f, .0001);
		assertEquals(s.getZ(), 6f, .0001);
		*/
	}
	
	public void testAddm() {
		Vect3d v = new Vect3d(1,1,1);
		Vect3d t = new Vect3d(2,3,4);
		v.addm(t, 2);
		assertEquals(v.getX(), 5f, .0001);
		assertEquals(v.getY(), 7f, .0001);
		assertEquals(v.getZ(), 9f, .0001);
	}
	
	public void testSubm() {
		Vect3d v = new Vect3d(1,1,1);
		Vect3d t = new Vect3d(2,3,4);
		v.subm(t, 2);
		assertEquals(v.getX(), -3f, .0001);
		assertEquals(v.getY(), -5f, .0001);
		assertEquals(v.getZ(), -7f, .0001);
	}
	
	public void testRotateY() {
		Vect3d v = new Vect3d(0,3,1);
		v.rotateY(90);
		assertEquals(v.getX(), 1f, .0001);
		assertEquals(v.getY(), 3f, .0001);
		assertEquals(v.getZ(), 0f, .0001);
	}
	
	public void testEquals() {
		Vect3d v = new Vect3d(7,2,6);
		Vect3d t = new Vect3d(7,2,6);
		assertTrue(v.equals(t));
	}
	
	public void testToString() {
		Vect3d v = new Vect3d(4,2,3);
		assertTrue(v.toString().equals("[4.0,2.0,3.0]"));
	}
	
	public void testGetBounceVect() {
		Vect3d v = new Vect3d(2,3,2);
		Vect3d t = new Vect3d(0,1,0);
		Vect3d s = new Vect3d(0,0,0);
		Vect3d r = new Vect3d(2,-3,2);
		Vect3d.getBounceVect(v,t,s);
		assertTrue(s.equals(r));
	}
	
	public void testGetClosestPointOnLine() {
		Vect3d a = new Vect3d(2,0,2);
		Vect3d b = new Vect3d(2,4,2);
		Vect3d p = new Vect3d(3,2,3);
		Vect3d v = new Vect3d(2,2,2);
		assertTrue(v.equals(Vect3d.getClosestPointOnLine(a,b,p)));
	}
	
	public void testGetDistanceBetweenPoint() {
		Vect3d a = new Vect3d(0,0,0);
		Vect3d b = new Vect3d(4,5,6);
		assertEquals(b.length(), Vect3d.getDistanceBetweenPoints(a,b), .0001);
	}
}