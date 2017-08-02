package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import java.awt.Color;

/**
 * A single Particle for the particle engine.
 */
public class ParticleSphere extends Particle {
	private static String textureName;
	private static Texture t;
	static {
		textureName = "data/textures/particle_noalpha.png";
		t = TextureManager.getInstance().loadTexture(textureName);
	};
	
	/**
	 * Default constructor.
	 */
	public ParticleSphere() {
		super();
	}
	
	/**
	 * Draw the particle.
	 * @param gldraw jogl gl interface
	 */
	public void draw(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		gl.glPushMatrix();

		Vect3d pos = getPosition();
		
		gl.glTranslatef(pos.x, pos.y, pos.z);
		EigenEngine.instance().getCamera().sphericalBillboard(gldraw,getPosition());
		
		gl.glScalef(scale,scale,scale);
	
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);
		
                t.bind(gldraw);
		gl.glBegin(GL.GL_QUADS);
			gl.glColor3f(r/255.0f,g/255.0f,b/255.0f);//,.70f);
			gl.glTexCoord2f(0.0f,1.0f);
			gl.glVertex3f(-0.5f,-0.5f,0.0f);
			gl.glTexCoord2f(1.0f,1.0f);
			gl.glVertex3f(0.5f,-0.5f,0.0f);
			gl.glTexCoord2f(1.0f,0.0f);
			gl.glVertex3f(0.5f,0.5f,0.0f);
			gl.glTexCoord2f(0.0f,0.0f);
			gl.glVertex3f(-0.5f,0.5f,0.0f);
		gl.glEnd();

		gl.glEnable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_BLEND);
		
		gl.glPopMatrix();
	
	}
}

