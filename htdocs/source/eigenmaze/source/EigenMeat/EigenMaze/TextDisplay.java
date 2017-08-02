package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;
import java.util.*;

class TextDisplay implements Drawable{
	//sets relative position
	private float xpos,ypos;
	private int maxLines;
	private List list;
	TextDisplay () {
		xpos = 0.0f;
		ypos = 0.0f;
		maxLines = 6;
		list = new LinkedList();
	}
	TextDisplay(int lines)
	{
		this();
		maxLines = 6;
	}
	TextDisplay(float x, float y, int lines) {
		xpos = x;
		ypos = y;
		maxLines = lines;
	}

	public void addLine(String s) {
		addLine(s,1.0f,0.0f,0.5f);
		
	}
	public synchronized void addLine(String s, float r, float g, float b) {
		if(list.size()>=maxLines){
			((LinkedList)list).removeFirst();
		}
		list.add(new DString(s,r,g,b));
	}
	public void clear(){
		list = new LinkedList();
	}
	
	public synchronized void draw(GLDrawable gldraw) {
		
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
		synchronized(list) {
			Iterator l = list.listIterator(0);
			float x =-.9f,y=0.9f;
			DString ds;
			while(l.hasNext()) {
				ds = (DString)l.next();
				gl.glColor3f(ds.r,ds.g,ds.b);
				gl.glRasterPos2f(x,y);
				glut.glutBitmapString(gl,GLUT.BITMAP_HELVETICA_12,ds.s);
				y-=.05f;
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
	private class DString{
		private String s;
		float r, g, b;
		DString(String s, float r, float g, float b) {
			this.s = s;
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
			
}
	
		
	
