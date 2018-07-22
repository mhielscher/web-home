package EigenMeat.EigenMaze;

import java.util.*;

/**
 * An EntityFactory will store lists of different types of entities loaded by the SoftLoader.
 * Entities are stored as clonable prototypes - any entity that is to be soft-loadable should
 * follow the pattern of the already defined types and have a prototype pattern subclass that
 * implements Clonable for creation (these are package private and thus have no Javadoc).
 * Entities saved in this class must have a getName() method (as part of the Prototype subclass).
 * <p>
 * Entities are created from this class by cloning. An instance of the fully initialized entity
 * is saved here, and when a new copy of that type of entity is to be created, it is cloned and
 * added to the engine.
 */
public class EntityFactory {
	private Vector ships;
	private Vector projectiles;
	
	public EntityFactory() {
		ships = new Vector(0,1);
		projectiles = new Vector(0,1);
	}
	
	/**
	 * Add a new entity to be created. The class of the entity passed will be identified
	 * and the entity will be added only if its class is supported (currently Ship and Projectile
	 * classes only).
	 */
	public void addEntityType(Entity e) {
		if (e instanceof Ship)
			addType((Ship)e);
		else if (e instanceof Projectile)
			addType((Projectile)e);
	}
	
	/**
	 * Add a new Ship type to be created.
	 */
	public void addType(Ship s) {
		if (s != null)
			ships.add(s);
	}
	
	/**
	 * Add a new Projectile type to be created.
	 */
	public void addType(Projectile p) {
		if (p != null)
			projectiles.add(p);
	}
	
	/**
	 * Create a new entity based on the entity stored under the given name.
	 * @param name the name of the entity type to be created.
	 * @return a clone of the entity stored here; or null if the name does not match.
	 */
	public Entity create(String name) {
		for (int i=0; i<ships.size(); i++) {
			if (((PrototypeShip)ships.get(i)).getName().equals(name))
				return (Ship)((PrototypeShip)ships.get(i)).clone();
		}
		for (int i=0; i<projectiles.size(); i++) {
			if (((PrototypeProjectile)projectiles.get(i)).getName().equals(name))
				return (Projectile)((PrototypeProjectile)projectiles.get(i)).clone();
		}
		return null;
	}
}