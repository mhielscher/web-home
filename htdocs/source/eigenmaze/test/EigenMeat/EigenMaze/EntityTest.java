package EigenMeat.EigenMaze;

import junit.framework.*;

public class EntityTest extends TestCase {

	private Entity bob;
	public EntityTest() {
		super();
	}

	public EntityTest(String name) {
		super(name);
	}

	public void testBoundingSphere() {
		Entity bob = new Entity();
		float sphere = 10;
		
		bob.setBoundingSphere(sphere);
		assertEquals(bob.getBoundingSphere(), sphere, .0001);
	}
	
	public void testPosition() {
		Entity bob = new Entity();
		Vect3d testVect;
		
		bob.setPosition(10,10,10);
		bob.translate(-2f,5f,77.07f);
		
		testVect = bob.getPosition();
		assertEquals(8f,testVect.getX(),.0001);
		assertEquals(15f,testVect.getY(),.0001);
		assertEquals(87.07f,testVect.getZ(),.0001);
		
	}

	public void testAlive() {
		Entity bob = new Entity();
		bob.setDead(false);
		assertEquals(false, bob.isDead());
	}

	public void testCollide() {
		Entity accord = new Entity();
		Entity airportVan = new Entity();

		accord.setBoundingSphere(10f);
		airportVan.setBoundingSphere(10f);

		accord.setPosition(5f,0f,0f);
		airportVan.setPosition(12f,0f,0f);

		assertEquals(true, accord.checkCollision(airportVan));
		assertEquals(true, airportVan.checkCollision(accord));
		airportVan.translate(0f,20f,0f);
		assertEquals(false, accord.checkCollision(airportVan));
		assertEquals(false, airportVan.checkCollision(accord));
	}
}

	
