package EigenMeat.EigenMaze;

import junit.framework.*;

public class ProjectileTest extends TestCase {
	public ProjectileTest() {
		super();
	}
	
	public ProjectileTest(String name) {
		super(name);
	}
	
	public void testSetDamage() {
		Projectile bob = new Projectile();
		bob.setDamage(20f);
		assertTrue(bob.getDamage() == 20f);
	}
}
