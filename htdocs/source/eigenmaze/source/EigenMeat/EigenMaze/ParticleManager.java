package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import java.util.*;

/**
 * The class that handles the creation, movement, and destruction of particles.
 */
public class ParticleManager implements Drawable {
	
  	private LinkedList particles;	

	/**
	 * Default constuctor.
	 */
	public ParticleManager() {
		particles = new LinkedList();
	}

	/**
	 * Adds a new particle effect.
	 * @param peffect the particle effect object that describes the effect
	 */
	public void createEffect(ParticleEffect peffect) {
		Particle tmp;
		synchronized(this) {		
			for(int i=0; i < peffect.getSize(); i++) {
				tmp = peffect.getNewParticle();
				particles.add(tmp);
			}
		}
	}

	/**
	 * Updates all the particles.
	 */
	public void update() {
		Particle current;
		long time = System.currentTimeMillis();
		
		synchronized(this) {
			ListIterator iter = particles.listIterator(0);
			while(iter.hasNext()) {
				current = (Particle)iter.next();	
				current.process();

				if(current.getTimeToDie() < time) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Draw all the particles
	 * @param gldraw jogl gl interface
	 */
	public void draw(GLDrawable gldraw) {
		Particle current;
		
		gldraw.getGL().glDisable(GL.GL_CULL_FACE);

		synchronized(this) {
			ListIterator iter = particles.listIterator(0);
			while(iter.hasNext()) {
				current = (Particle)iter.next();
				current.draw(gldraw);
			}
		}
		
		gldraw.getGL().glEnable(GL.GL_CULL_FACE);
	}
}
