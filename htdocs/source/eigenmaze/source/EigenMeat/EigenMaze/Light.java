package EigenMeat.EigenMaze;

import net.java.games.jogl.*;

class Light {
	private float[] position;
	private float[] ambient;
	private float[] diffuse;
	private int id;
	
	public Light(int glid) {
                position = new float[4];
                ambient = new float[4]; 
                diffuse = new float[4];

                id = glid;

		setAmbient(.0f,.0f,.0f);
                setDiffuse(1f,1f,1f);
	}
	
	public Light(Vect3d pos) {
		position = new float[4];
		ambient = new float[4];
		diffuse = new float[4];
		
		id = GL.GL_LIGHT0;
		
		setPosition(pos);
		setAmbient(.0f,.0f,.0f);
		setDiffuse(1f,1f,1f);
	}

	public void setPosition(Vect3d pos) {
		position[0] = pos.x;
		position[1] = pos.y;
		position[2] = pos.z;
		position[3] = 1f;
	}

	public Vect3d getPosition() {
		return new Vect3d(position[0],position[1],position[2]);
	}
	
	public void setAmbient(float r, float g, float b) {
		ambient[0] = r;
		ambient[1] = g;
		ambient[2] = b;
		ambient[3] = 1f;
	}

	public void setDiffuse(float r, float g, float b) {
		diffuse[0] = r;
		diffuse[1] = g;
		diffuse[2] = b;
		diffuse[3] = 1f;
	}
		
	public void update(GLDrawable gldraw) {
		gldraw.getGL().glLightfv(id,GL.GL_DIFFUSE,diffuse);
		gldraw.getGL().glLightfv(id,GL.GL_AMBIENT,ambient);
		gldraw.getGL().glLightfv(id,GL.GL_POSITION,position);
	}
}
