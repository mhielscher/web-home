package EigenMeat.EigenMaze;

import junit.framework.*;
import java.awt.Color;

public class Math3DTest extends TestCase {
	public Math3DTest() {
		super();
	}
	
	public Math3DTest(String name) {
		super(name);
	}

	public void testLineIntersectAABCube() {
		assertTrue(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
							1,
							new Vect3d(0,0,0),
							new Vect3d(2,0,0)));
		assertTrue(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
                                                        1,
                                                        new Vect3d(0,0,0),
                                                        new Vect3d(10,5,-1)));
		assertTrue(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
                                                        1,
                                                        new Vect3d(0,5,0),
                                                        new Vect3d(0,-5,0)));
		assertTrue(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
                                                        10,
                                                        new Vect3d(-8,-9,-10),
                                                        new Vect3d(10,9,9)));
		assertFalse(Math3D.doesLineIntersectAABCube(new Vect3d(0,1,0),
							1,
							new Vect3d(0,10,0),
							new Vect3d(0,3,0)));
		assertFalse(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
							2,
							new Vect3d(4,-3,4),
							new Vect3d(-4,-3,4)));
		assertFalse(Math3D.doesLineIntersectAABCube(new Vect3d(0,0,0),
							1,
							new Vect3d(10,10,10),
							new Vect3d(5,5,5)));
		assertTrue(Math3D.doesLineIntersectAABCube(new Vect3d(10,50,20),
							3,
							new Vect3d(5,50,20),
							new Vect3d(15,50,20)));
	}
}
