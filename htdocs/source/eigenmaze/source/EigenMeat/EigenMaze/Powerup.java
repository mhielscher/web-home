package EigenMeat.EigenMaze;

/**
 * A Powerup entity that increases a Ship's shields on collision.
 */
public class Powerup extends Entity { 
	
	public Powerup() {
		super();
		setMesh(MeshLoader.loadMesh("data/models/switch/switch.obj", 0.6f));
		EigenEngine.instance().add(this);
	}
	
	public void update() {
		setVelocity(0,0,0);
		super.update();
	}
	
	public boolean collide(MobileEntity e) {
		if (e instanceof Ship ) {
			((Ship)e).setShields(((Ship)e).getShields()+25);
			setDead(true);
		}
		return false;
	}
}
