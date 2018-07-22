package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * Class to query OpenGL Extensions.
 */
public class GLExtensions {
	private static String extensions;

	/**
	 * Default constructor.
	 * @param gl GL object from JOGL
	 */
	public GLExtensions(GL gl) {
		extensions = gl.glGetString(gl.GL_EXTENSIONS);
	}

	/**
	 * Print the list of supported extensions.
	 */
	public void print() {
		System.out.println("Supported OpenGL Extensions: "+extensions);
	}
	
	/**
	 * Check if a specific extension is supported.
	 * @param ext the extension
	 * @return true/false
	 */
	public static boolean checkExtension(String ext) {
		if(extensions.indexOf(ext) != -1)
			return true;

		return false;
	}
}
