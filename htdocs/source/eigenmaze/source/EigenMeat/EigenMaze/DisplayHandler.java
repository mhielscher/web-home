package EigenMeat.EigenMaze;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;
import java.io.*;
import java.math.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * The DisplayHandler represents a rectangular view of the game world, and controls
 * the drawing of its contents.
 */
public class DisplayHandler extends JPanel implements GLEventListener {
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private boolean lock = false;
	private boolean drawReady = false;
	private long lastFPSTime = 0;
	private int frameCount = 0;
	private float fps;	
	private GLCanvas canvas;
	
	private LinkedList displayableList;
	private LinkedList mobileList;
	
	private float camCordX, camCordY, camCordZ;
	private float targetCordX, targetCordY, targetCordZ;

	private Camera eigenCamera;
	private LightManager lightManager;
	private boolean fpsDisplaying = false;
	private ParticleManager particles;
	private SkyBox sbox;
	private GLExtensions extensions;

	private int renderFrame;
	
	public DisplayHandler() {
	  	super();
	 
		renderFrame = 0;
		
		particles = null;
		
		///CHANGE THIS..
	  	camCordX = 40;
		camCordY = 50;
		camCordZ = 40;
	
		eigenCamera = new Camera();
		lightManager = new LightManager();
		
		displayableList = new LinkedList();
	  
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);
		capabilities.setStencilBits(1);
		
		canvas = GLDrawableFactory.getFactory().createGLCanvas(capabilities);
		
		canvas.addGLEventListener(this);
		
		this.add(canvas);
		this.setBackground(Color.GREEN);
		this.setSize(WIDTH, HEIGHT);
		//this.setVisible(true);
		this.setFocusable(false);
		//canvas.setSize(WIDTH, HEIGHT);
		//canvas.setVisible(true);
		canvas.setFocusable(false);
	}

	/**
	 * Get the number of the frame that is being rendered.
	 * @return frame number
	 */
	public int getRenderFrame() {
		return renderFrame;
	}
	
	/**
	 * Sets the size of the viewport (JPanel/GLCanvas).
	 * @param w width
	 * @param h height
	 */
	public void setSize(int w, int h) {
		super.setSize(w, h);
		canvas.setSize(w, h);
		System.out.println("Resizing to "+w+"x"+h);
	}
	
	/**
	 * Sets camera-tracking mouse action handlers. It is recommended that both listeners
	 * be implemented in the same class and the same object be passed in twice.
	 * @param m1 the MouseListener.
	 * @param m2 the MouseMotionListener.
	 */
	public void setMouseListeners(MouseListener m1, MouseMotionListener m2) {
		canvas.addMouseListener(m1);
		canvas.addMouseMotionListener(m2);
	}
	
	/**
	 * Gets the viewport's hard-coded preferred size.
	 * @return the display's preferred size.
	 */
	public Dimension getPreferredSize() {
	  return new Dimension(WIDTH, HEIGHT);
	}
	
	/**
	 * Adds a Drawable to the handler. Its draw() method will be called every update.
	 * @param entity the Drawable to add.
	 */
	public void add(Drawable entity) {
		synchronized(this) {
			displayableList.add(entity);
		}
	}
	
	/**
	 * Sets the display's ParticleManager.
	 * @param p the ParticleManager to use.
	 */
	public void add(ParticleManager p) {
		particles = p;
	}
	
	/**
	 * Called by the drawable immediately after the OpenGL context is initialized for
	 * the first time. Initializes the perspective and view matrices, and activates
	 * lighting and rendering options. Part of the GLEventListener interface.
	 */
	public void init(GLDrawable glDrawable) {
		GL myGL = glDrawable.getGL();
		
		extensions = new GLExtensions(myGL);
		extensions.print();

		myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		myGL.glClearStencil(0);
		myGL.glShadeModel(GL.GL_SMOOTH);
		myGL.glEnable(GL.GL_CULL_FACE);
		myGL.glCullFace(GL.GL_BACK);
		myGL.glFrontFace(GL.GL_CCW);
		myGL.glEnable(GL.GL_DEPTH_TEST);
		myGL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		fps = 0;
		myGL.glEnable(GL.GL_LIGHTING);
		myGL.glEnable(GL.GL_LIGHT0);
	
		//set global ambient light
		float[] globalAmbient = {.2f,.2f,.2f,1f};
		myGL.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT,globalAmbient);
	
		myGL.glEnable(GL.GL_COLOR_MATERIAL);
		myGL.glColorMaterial(GL.GL_FRONT_AND_BACK,GL.GL_AMBIENT_AND_DIFFUSE);
	
		//blending
		myGL.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE);
	
		TextureManager.getInstance().init(myGL);
		//TextureManager.getInstance().loadTextureIntoGL(myGL, "data/stone.png");
		//TextureManager.getInstance().loadTextureIntoGL(myGL, "data/goo.png");
		
		lastFPSTime = System.currentTimeMillis();
		sbox = new SkyBox(eigenCamera.getPosition());
	}
	
	/**
	 * Called by the drawable during the first repaint after the component has been resized.
	 * The client can update the viewport and view volume of the window appropriately. Part
	 * of the GLEventListener interface.
	 */
	public void reshape(GLDrawable glDrawable, int i, int i1, int i2, int i3) {
		System.out.println("reshape has been called");
		GL myGL=glDrawable.getGL();
        	GLU myGLU = glDrawable.getGLU();	
		int width=canvas.getWidth();
        	int height=canvas.getHeight();

        	myGL.glMatrixMode(GL.GL_PROJECTION);

        	myGL.glLoadIdentity();

        	//myGL.glOrtho(-width/2,width/2,-height/2,height/2,-10,10);
		myGLU.gluPerspective(60,width/height,.1,600);
		
		//this.setVisible(true);
		//canvas.setVisible(true);
	}
	
	/**
	 * Called by the glDrawable to initiate OpenGL rendering by the client. After all
	 * GLEventListeners have been notified of a display event, the glDrawable will
	 * swap its buffers if necessary. Part of the GLEventListener interface.
	 */
	public void display(GLDrawable glDrawable) {
		renderFrame++;
		frameCount++;
		long currentTime = System.currentTimeMillis();
		DecimalFormat format = new DecimalFormat("0.00");
		if (currentTime-lastFPSTime>=2000) {
			fps = ((float)frameCount/(currentTime-lastFPSTime)*1000);
			System.out.println(format.format(fps)+" FPS ("+frameCount+" frames in "+(currentTime-lastFPSTime)/1000f+" seconds).");
			lastFPSTime = currentTime;
			frameCount = 0;
		}
		GL myGL = glDrawable.getGL();
		GLU myGLU = glDrawable.getGLU();
		myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		
		myGL.glMatrixMode(GL.GL_MODELVIEW);
		myGL.glLoadIdentity();
		
		myGL.glEnable(GL.GL_TEXTURE_2D);
		myGL.glEnable(GL.GL_LIGHTING);	
		eigenCamera.update(glDrawable);
		lightManager.update(glDrawable);
		sbox.update(eigenCamera.getPosition());
		EigenEngine.instance().getOctree().draw(glDrawable,eigenCamera.getFrustum());
		synchronized(this) {
			ListIterator iter = displayableList.listIterator(0);
			Drawable current;
			while (iter.hasNext()) {
				current = (Drawable)iter.next();
				current.draw(glDrawable);

				if(current instanceof MobileEntity) {
					if(((MobileEntity)current).isDead()) {
						iter.remove();
					}
				}

			}
		}

		drawShadows(glDrawable);
		
		sbox.draw(glDrawable);

		if(particles != null)
			particles.draw(glDrawable);

		myGL.glMatrixMode(GL.GL_PROJECTION);
		myGL.glPushMatrix();
		myGL.glLoadIdentity();
		myGL.glMatrixMode(GL.GL_MODELVIEW);
		myGL.glPushMatrix();
		myGL.glLoadIdentity();
		myGL.glDisable(GL.GL_LIGHTING); 
		GLUT glut = new GLUT();
		TextureManager.getInstance().unbindTexture(myGL);
		if(fpsDisplaying) {
			myGL.glColor3f(1.0f,1.0f,1.0f);
			//myGL.glTranslatef(0.0f,0.0f,-1.0f);
			myGL.glRasterPos2f(0.7f,0.7f);
			glut.glutBitmapString(myGL,GLUT.BITMAP_HELVETICA_18,format.format(fps)+" FPS");
		}
		EigenEngine.instance().getLocalPlayer().drawStats(myGL, glut);
		myGL.glEnable(GL.GL_LIGHTING);
		myGL.glPopMatrix();
		myGL.glMatrixMode(GL.GL_PROJECTION);
		myGL.glPopMatrix();
		if (!drawReady)
			drawReady = true;
	
	}

	private void drawShadows(GLDrawable gldraw) {
		//drawShadowPass(gldraw,0);
		//drawShadowRect(gldraw);
		//gldraw.getGL().glClear(GL.GL_STENCIL_BUFFER_BIT);
		drawShadowPass(gldraw,1);
		drawShadowRect(gldraw);
		gldraw.getGL().glClear(GL.GL_STENCIL_BUFFER_BIT);
                drawShadowPass(gldraw,2);
                drawShadowRect(gldraw);

	}

	private void drawShadowPass(GLDrawable gldraw, int light) {
		EigenEngine.instance().getOctree().drawShadowVolume(gldraw,light);

                //buffers: we need to loop through the entities again to draw
                //the shadows
                synchronized(this) {
                        ListIterator iter = displayableList.listIterator(0);
                        Drawable current;
                        while (iter.hasNext()) {
                                current = (Drawable)iter.next();
                                if(current instanceof Entity)
                                        ((Entity)current).drawShadow(gldraw,light);
                        }
                }

	}
	
	private void drawShadowRect(GLDrawable gldraw) {
		GL gl = gldraw.getGL();
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_ENABLE_BIT | GL.GL_POLYGON_BIT | GL.GL_STENCIL_BUFFER_BIT);
                gl.glDisable(GL.GL_LIGHTING);
                gl.glDepthMask(false);
                gl.glDepthFunc(GL.GL_LEQUAL);
                gl.glEnable(GL.GL_STENCIL_TEST);
                gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xFFFFFFFF);
                gl.glStencilOp(GL.GL_KEEP,GL.GL_KEEP,GL.GL_KEEP);
                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                        gl.glVertex3f(-0.1f, 0.1f,-0.10f);
                        gl.glVertex3f(-0.1f,-0.1f,-0.10f);
                        gl.glVertex3f( 0.1f, 0.1f,-0.10f);
                        gl.glVertex3f( 0.1f,-0.1f,-0.10f);
                gl.glEnd();
                gl.glPopMatrix();
                gl.glPopAttrib();
	}

	/**
	 * Currently unimplemented by JOGL, but required by GLEventListener. Does nothing.
	 */
	public void displayChanged(GLDrawable glDrawable, boolean b, boolean b1) {
	}
	
	/**
	 * Tells the GLCanvas that it should repaint itself ASAP.
	 */
	public void update() {
		if (drawReady)
			canvas.repaint();
	}

	/**
	 * Gets the rendering Camera object associated with this DisplayHandler.
	 * @return the Camera object.
	 */
	public Camera getCamera() {
		return eigenCamera;
	}

	/**
	 * Get reference to the LightManager.
	 * @return reference of LightManager
	 */
	public LightManager getLightManager() {
		return lightManager;
	}
	
	/**
	 * Set the game world point at which the rendering camera points.
	 * @param xCord the x-coordinate of the point at which to look.
	 * @param yCord the y-coordinate of the point at which to look.
	 * @param zCord the z-coordinate of the point at which to look.
	 */
	public void setCameraTarget(float xCord, float yCord, float zCord) {
		//targetCordX = xCord;
		//targetCordY = yCord;
		//targetCordZ = zCord;
		eigenCamera.setLookAt(xCord,yCord,zCord);
	}
	
	/**
	 * Set the rendering camera's game world position.
	 * @param xCord the x-coordinate of the camera location.
	 * @param yCord the y-coordinate of the camera location.
	 * @param zCord the z-coordinate of the camera location.
	 */
	public void setCameraLocation(float xCord, float yCord, float zCord) {
	  	//camCordX = xCord;
		//camCordY = yCord;
		//camCordZ = zCord;
		eigenCamera.setPosition(xCord,yCord,zCord);
	}
	
	/**
	 * Toggles whether or not the fps is displayed
	 * @param displaying Sets the fps display (true = displaying)
	 */
	public void setFPSDisplay(boolean displaying) {
		fpsDisplaying = displaying;
	}
}
