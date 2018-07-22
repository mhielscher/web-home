package EigenMeat.EigenMaze;

import java.util.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

class Scoreboard implements Drawable {
	private boolean visible;
	
	private List scores;
	
	private class ScoreMap {
		private String playerName;
		private long score;
		
		public ScoreMap() {
			playerName = "";
			score = 0;
		}
		
		public ScoreMap(String player, long s) {
			playerName = player;
			score = s;
		}
		
		public String getName() {
			return playerName;
		}
		
		public long getScore() {
			return score;
		}
		
		public void setName(String p) {
			playerName = p;
		}
		
		public void setScore(long s) {
			score = s;
		}
	}
	
	public Scoreboard() {
		visible = false;
		scores = Collections.synchronizedList(new LinkedList());
	}
	
	public void setScore(String name, long score) {
		synchronized(scores) {
			ListIterator iter = scores.listIterator(0);
			ScoreMap current;
			boolean exists = false;
			while (iter.hasNext()) {
				current = (ScoreMap)iter.next();
				if (current.getName().equals(name)) {
					exists = true;
					current.setScore(score);
				}
			}
			if (!exists)
				scores.add(new ScoreMap(name, score));
		}
	}
	
	public void setVisible(boolean v) {
		visible = v;
	}
	
	public void draw(GLDrawable gldraw) {
		if (visible) {
			GL gl = gldraw.getGL();
			GLUT glut = new GLUT();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisable(GL.GL_COLOR_MATERIAL);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glTranslatef(0.0f,0.0f,-1.0f);
			synchronized(scores) {
				ListIterator iter = scores.listIterator(0);
				ScoreMap current;
				float x = -0.5f;
				float y = 0.8f;
				gl.glColor3f(1.0f, 1.0f, 0.0f);
				while (iter.hasNext()) {
					current = (ScoreMap)iter.next();
					gl.glRasterPos2f(x,y);
					glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_18, current.getName());
					gl.glRasterPos2f(x+0.5f,y);
					glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_18, ""+current.getScore());
					y -= 0.1f;
				}
			}
			gl.glLoadIdentity();
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL.GL_COLOR_MATERIAL);
			gl.glEnable(GL.GL_LIGHTING);
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}
	}
}
