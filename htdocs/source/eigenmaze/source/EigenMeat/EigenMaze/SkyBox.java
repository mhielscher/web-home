package EigenMeat.EigenMaze;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * A Sky Box Object that simulates scenery
 */
public class SkyBox implements Drawable {
	private Vect3d position = new Vect3d();
	private static String textureName;
	private static Texture xn,xp,yn,yp,zn,zp;
	static {
		textureName = "data/textures/neg_z.png";
		xp = TextureManager.getInstance().loadTexture("data/textures/neg_x.png");
		xn = TextureManager.getInstance().loadTexture("data/textures/pos_x.png");
		zp = TextureManager.getInstance().loadTexture("data/textures/pos_z.png");
		yn = TextureManager.getInstance().loadTexture("data/textures/neg_y.png");
		zn = TextureManager.getInstance().loadTexture(textureName);
		
	};
	/**
	 * Constructor
	 * @param p Center Position of Skybox (normally at camera)
	 */
	public SkyBox(Vect3d p) {
		position.set(p);
	}

	/**
	 * Updates postion of skybox
	 * @param p the new position of the skybox
	 */
	public void update(Vect3d p) {
		position.set(p);
	}

	/**
	 * Draws the skybox
	 * @param gldraw the GLDrawable object fore gl drawing interface
	 */
	public void draw(GLDrawable gldraw) {

		float xmax=250.0f,ymax=250.0f,zmax=250.0f;
		GL gl = gldraw.getGL();
		gl.glPushMatrix();
		//gl.glLoadIdentity();
		gl.glTranslatef(position.x,position.y-150,position.z);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);
		zn.bind(gldraw);
                gl.glColor3f(1f,1f,1f);
		gl.glBegin(GL.GL_QUADS);
                        gl.glTexCoord2f(0.0f,1.0f);
                        gl.glVertex3f(-xmax,-ymax,-zmax);
                        gl.glTexCoord2f(1.0f,1.0f);
                        gl.glVertex3f(xmax,-ymax,-zmax);
                        gl.glTexCoord2f(1.0f,0.0f);
                        gl.glVertex3f(xmax,ymax,-zmax);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(-xmax,ymax,-zmax);
		gl.glEnd();
			yn.bind(gldraw);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f,1.0f);
			gl.glVertex3f(-xmax,-ymax,-zmax);
			gl.glTexCoord2f(1.0f,1.0f);
			gl.glVertex3f(-xmax,-ymax,zmax);
			gl.glTexCoord2f(1.0f,0.0f);
			gl.glVertex3f(xmax,-ymax,zmax);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(xmax,-ymax,-zmax);
		gl.glEnd();
			xn.bind(gldraw);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1.0f,1.0f);
			gl.glVertex3f(-xmax,-ymax,-zmax);
			gl.glTexCoord2f(1.0f,0.0f);
			gl.glVertex3f(-xmax,ymax,-zmax);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(-xmax,ymax,zmax);
			gl.glTexCoord2f(0.0f,1.0f);
			gl.glVertex3f(-xmax,-ymax,zmax);
		gl.glEnd();	
			zp.bind(gldraw);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1.0f,1.0f);
			gl.glVertex3f(-xmax,-ymax,zmax);
			gl.glTexCoord2f(1.0f,0.0f);
			gl.glVertex3f(-xmax,ymax,zmax);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(xmax,ymax,zmax);
			gl.glTexCoord2f(0.0f,1.0f);
			gl.glVertex3f(xmax,-ymax,zmax);
		gl.glEnd();
			xp.bind(gldraw);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f,1.0f);
			gl.glVertex3f(xmax,-ymax,-zmax);
			gl.glTexCoord2f(1.0f,1.0f);
			gl.glVertex3f(xmax,-ymax,zmax);
			gl.glTexCoord2f(1.0f,0.0f);
			gl.glVertex3f(xmax,ymax,zmax);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(xmax,ymax,-zmax);
                gl.glEnd();
		gl.glPopMatrix();
		gl.glDisable(GL.GL_BLEND);



	}	

	
}	
