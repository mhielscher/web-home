package EigenMeat.EigenMaze;

import junit.framework.*;
import java.awt.Color;

public class TriangleTest extends TestCase {
	public TriangleTest() {
		super();
	}
	
	public TriangleTest(String name) {
		super(name);
	}

	public Triangle getTri() {
		Vect3d verts[] = new Vect3d[3];
		verts[0] = new Vect3d(0,0,5);
		verts[1] = new Vect3d(5,0,0);
		verts[2] = new Vect3d(0,5,0);

		Triangle t = new Triangle(verts[0],verts[1],verts[2],new Color(0,0,0));
		return t;
	}

	public Triangle getXZTri() {
		Vect3d verts[] = new Vect3d[3];
                verts[0] = new Vect3d(0,0,5);
                verts[1] = new Vect3d(5,0,0);
                verts[2] = new Vect3d(0,0,0);

                Triangle t = new Triangle(verts[0],verts[1],verts[2],new Color(0,0,0));
                return t;
	}

	Triangle getYZTri() {
                Vect3d verts[] = new Vect3d[3];
                verts[0] = new Vect3d(0,5,0);
                verts[1] = new Vect3d(0,0,5);
                verts[2] = new Vect3d(0,0,0);

                Triangle t = new Triangle(verts[0],verts[1],verts[2],new Color(0,0,0));
                return t;
        }
	
	public void testLineIntersectPlane() {
		Triangle t = getTri();
		assertTrue(t.doesLineIntersectPlane(new Vect3d(0,0,0),
						 new Vect3d(10,10,10)));
		assertTrue(t.doesLineIntersectPlane(new Vect3d(-10,-10,-10),
						new Vect3d(0,10,0)));

		
		assertFalse(t.doesLineIntersectPlane(new Vect3d(0,0,0),
						new Vect3d(.5,.5,.5)));
		assertFalse(t.doesLineIntersectPlane(new Vect3d(100,100,100),
						new Vect3d(200,200,200)));
	}

	public void testLineIntersect() {
		Triangle t = getTri();
		
		assertTrue(t.doesLineIntersect(new Vect3d(0,0,0),
						new Vect3d(10,10,10)));
		assertTrue(t.doesLineIntersect(new Vect3d(6,5,5),
						new Vect3d(-1,-1,-1)));

		assertFalse(t.doesLineIntersect(new Vect3d(1,1,1),
						new Vect3d(-5,-5,2)));
		assertFalse(t.doesLineIntersect(new Vect3d(0,10,0),
						new Vect3d(-100,-100,-100)));

		t = getXZTri();

		assertTrue(t.doesLineIntersect(new Vect3d(2,2,2),
						new Vect3d(2,-2,2)));
		assertTrue(t.doesLineIntersect(new Vect3d(-1,2,-1),
						new Vect3d(2,-1,2)));

		assertFalse(t.doesLineIntersect(new Vect3d(4,1,4),
						new Vect3d(4,-1,4)));
	
		t = getYZTri();

		assertTrue(t.doesLineIntersect(new Vect3d(2,2,2),
						new Vect3d(-2,2,2)));

		assertFalse(t.doesLineIntersect(new Vect3d(2,5,5),
						new Vect3d(-2,5,5)));
	}
}
