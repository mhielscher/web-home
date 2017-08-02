package EigenMeat.EigenMaze;

/**
 * Entity that has mass. May be renamed and refactor soon (Physics object...)
 */
public class MassEntity extends Entity {
	private float mass;

	/**
	 * Default constructor
	 */
	public MassEntity() {
	}
	
	/**
	 * Copy constructor.
	 */
	public MassEntity(MassEntity m) {
		super(m);
		mass = m.mass;
	}

	/**
	 * Set the entity's mass.
	 * @param mass mass, kilograms
	 */
	public synchronized void setMass(float mass) {
		this.mass = mass;
	}

	/**
	 * Get the entity's mass.
	 * @return mass
	 */
	public synchronized float getMass() {
		return mass;
	}
}
