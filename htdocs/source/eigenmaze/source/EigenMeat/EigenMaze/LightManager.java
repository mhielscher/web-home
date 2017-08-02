package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * Light manager.
 */
public class LightManager {
	private Light light[];

	/**
	 * Default constructor.
	 */
	public LightManager() {
		light = new Light[3];
		
		light[0] = new Light(GL.GL_LIGHT0);
		light[0].setPosition(new Vect3d(5,20,5));
		light[1] = new Light(GL.GL_LIGHT1);
		light[1].setPosition(new Vect3d(-5,20,-5));
		light[2] = new Light(GL.GL_LIGHT2);
		light[2].setPosition(new Vect3d(20,20,10));
	}

	/**
	 * Update all the lights.
	 * @param gldraw JOGL GLDrawable object
	 */
	public void update(GLDrawable gldraw) {
		//Vect3d pos = new Vect3d(EigenEngine.instance().getLocalPlayer().getPosition());
		//pos.y = 5;
		//light[0].setPosition(pos);
		
		//light[0].update(gldraw);
		light[1].update(gldraw);
		light[2].update(gldraw);
	}

	/**
	 * Get a light.
	 * @param i light number
	 * @return reference of light
	 */
	public Light getLight(int i) {
		return light[i];
	}
}
