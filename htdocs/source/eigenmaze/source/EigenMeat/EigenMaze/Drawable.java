package EigenMeat.EigenMaze;

import net.java.games.jogl.*;

/**
 * Provides a way for an object to draw itself in an OpenGL context.
*/
public interface Drawable {

	/**
	 * Draws the object in the specified OpenGL context.
	 *
	 * @param gl the GLDrawable context on which to draw the object.
	*/
	public void draw(GLDrawable gl);
}
