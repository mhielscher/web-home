package EigenMeat.EigenMaze;

import junit.framework.*;

public class ParticleTest extends TestCase {
	public ParticleTest() {
		super();
	}
	
	public ParticleTest(String name) {
		super(name);
	}

	public void testSetPosition() {
		Particle bob = new Particle();
		bob.setPosition(20,-10,0);
		Vect3d v = bob.getPosition();
		assertTrue(v.x == 20);
		assertTrue(v.y == -10);
		assertTrue(v.z == 0);
	}

	public void testSetTimeToDie() {
		Particle bob = new Particle();
		bob.setTimeToDie(2000);
		assertTrue(2000 == bob.getTimeToDie());
	}

	public void testSetScale() {
		Particle bob = new Particle();
		bob.setScale(1.5f);
		assertTrue(1.5f == bob.getScale());
	}
}
