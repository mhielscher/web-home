package EigenMeat.EigenMaze;

import java.awt.event.*;
import java.nio.*;
import net.java.games.jogl.*;
import net.java.games.jogl.util.*;

/**
 * The locally-controlled player.
 */
public class Player extends Ship implements KeyListener, MouseListener, MouseMotionListener {
	
	private boolean forward,back,turn_left,turn_right, shooting;
	private boolean respawn;
	
	public Player() {
		super((Ship)EigenEngine.instance().getFactory().create("DefaultShip"));
//		EigenEngine.instance().addNet(this);
		respawn = false;
	}
	
	/**
	 * Updates the player's time-dependent and input-dependent data.
	 */
	public synchronized void update() {
  		Vect3d t = new Vect3d(0,0,0);

		if(forward) {
			t = new Vect3d(getForwardVect());
			t.mult(getAcceleration()*Game.tof);
			getVelocity().add(t);

			//buffers: reusing the t vector...
			//some basic vector math to get the particle's
			//initial velocity and position looking correct
			t.set(getForwardVect());
			t.invert();
			t.mult(8);
			thrustParticles.setBaseVelocity(t);
			t.normalize();
			t.mult(getBoundingSphere());
			t.add(getPosition());
			thrustParticles.setPosition(t);
			EigenEngine.instance().add(thrustParticles);
		} else if(back) {
			t = new Vect3d(getForwardVect());
			t.invert();
			t.mult(getAcceleration()*Game.tof);
			getVelocity().add(t);

			t.set(getForwardVect());
                        t.mult(5);
                        thrustParticles.setBaseVelocity(t);
                        t.normalize();
                        t.mult(getBoundingSphere());
                        t.add(getPosition());
                        thrustParticles.setPosition(t);
                        EigenEngine.instance().add(thrustParticles);
		}

		float rotspeed = 145;
		if((!turn_left && !turn_right) || (!forward && !back))
			if(getZRot() > 3) {
                                setZRot(getZRot()-rotspeed*Game.tof);
                        } else if(getZRot() < -3) {
                                setZRot(getZRot()+rotspeed*Game.tof);
                        } else
				setZRot(0);
			
		if(turn_left) {
			setYRot(getYRot()+Game.tof*getTurnSpeed());

			if(forward) {
				if(getZRot() < 45f)
					setZRot(getZRot()+rotspeed*Game.tof);
			} else if(back)
				if(getZRot() > -45f)
					setZRot(getZRot()-rotspeed*Game.tof);
		} else if(turn_right) {
			setYRot(getYRot()-Game.tof*getTurnSpeed());

			if(forward) {
				if(getZRot() > -45f) 
					setZRot(getZRot()-rotspeed*Game.tof);
			} else if(back)
				if(getZRot() < 45f) 
					setZRot(getZRot()+rotspeed*Game.tof);
		}

		if(getVelocity().length() > getMaxSpeed()) {
                        getVelocity().normalize();
                        getVelocity().mult(getMaxSpeed());
                }

//		if(shooting) {
//			 bulletLauncher.shoot();
//		}
		
		super.update();
		if (!leftButtonDown) {
			Vect3d back = new Vect3d(getForwardVect());
			back.invert();
			back.mult(25);
			back.add(getPosition());
			EigenEngine.instance().setCameraLocation(back.x,back.y+25,back.z);
		}
		else {
			Vect3d tempVect = new Vect3d(cameraVector);
			tempVect.add(getPosition());
			EigenEngine.instance().setCameraLocation(tempVect.x,tempVect.y+25,tempVect.z);
		}
		EigenEngine.instance().setCameraTarget(getPosition().x, getPosition().y, getPosition().z);
		
//		EigenEngine.instance().netUpdate(this);
  	}

	/**
	 * Called when this player dies.
	 */
	public void die() {
		respawn = true;
		super.die();
	}
	
	/**
	 * Gets whether this player should respawn when dead.
	 * @return true if the player should respawn; false otherwise.
	 */
	public boolean getRespawn() {
		return respawn;
	}
	
	/**
	 * Sets whether this player should respawn when dead.
	 * @param r true if the player should respawn; false otherwise.
	 */
	public void setRespawn(boolean r) {
		respawn = r;
	}

	/**
	 * Draw the player.
	 * @param gldraw the GLDrawable context on which to draw the player.
	 */
	public synchronized void draw(GLDrawable gldraw) {
		super.draw(gldraw);
		/*
		GLUT glut = new GLUT();;
		GL gl = gldraw.getGL();
		drawStats(gl,glut);*/
	}
	
	/**
	 * A part of the KeyListener interface. Does nothing at the moment.
	 * @param e the KeyEvent in question.
	 */
	public void keyTyped(KeyEvent e){}

	/**
	 * A part of the KeyListener interface. Handles movement and weapon actions.
	 * @param e the KeyEvent in question.
	 */
	public void keyPressed(KeyEvent e) {
    		if(e.getKeyCode() == KeyEvent.VK_W) {
     		 	forward = true;
		}
    		else if(e.getKeyCode() == KeyEvent.VK_S) {
      			back = true;
		}
	
		if(e.getKeyCode() == KeyEvent.VK_A) {
			 turn_left = true;
					          }       
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			turn_right = true;
		}

		if(e.getKeyCode() == KeyEvent.VK_Q) {
			setVelocity(0,0,0);
		}

		if(e.getKeyCode() == KeyEvent.VK_E) {
			setPosition(3,0,3);
		}

		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			shooting = true;
			if (launchers.size() > 0) {
				ProjectileLauncher pl = (ProjectileLauncher)launchers.get(0);
				if (pl != null) {
					((ProjectileLauncher)launchers.get(0)).shoot();
					NetProjectileManager.instance().fire((short)0);
				}
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_Z) {
			if (launchers.size() > 1) {
				ProjectileLauncher pl = (ProjectileLauncher)launchers.get(1);
				if (pl != null) {
					((ProjectileLauncher)launchers.get(1)).shoot();
					NetProjectileManager.instance().fire((short)1);
				}
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_X) {
			super.die();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_F) {
			if(flag != null) {
				System.out.println("Dropped.");
				flag.drop();
				flag = null;
			}
		}
		
	//	if (e.getKeyCode() == KeyEvent.VK_P)
	//		EigenEngine.instance().getScoreboard().setVisible(true);
			
  	}
	
	/**
	 * A part of the KeyListener interface. Handles movement actions.
	 * @param e the KeyEvent in question.
	 */
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
		        forward = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
		  	back = false;
		}
	
		if(e.getKeyCode() == KeyEvent.VK_A) {
			turn_left = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			turn_right = false;
		} 

		if(e.getKeyCode() == KeyEvent.VK_Z) {
			shooting = false;
		}
		
	//	if (e.getKeyCode() == KeyEvent.VK_P)
	//		EigenEngine.instance().getScoreboard().setVisible(false);
	}
	
	private boolean leftButtonDown = false;
	int mouseX=0, mouseY=0;
	Vect3d cameraVector = null;
	
	/**
	 * A part of the MouseListener interface. Does nothing at the moment.
	 * @param e the MouseEvent in question.
	 */
	public void mouseClicked(MouseEvent e) {
		//nothing here, use mousePressed, mouseReleased
	}
	
	/**
	 * A part of the MouseListener interface. Does nothing at the moment.
	 * @param e the MouseEvent in question.
	 */
	public void mouseEntered(MouseEvent e) {
	}
	
	/**
	 * A part of the MouseListener interface. Does nothing at the moment.
	 * @param e the MouseEvent in question.
	 */
	public void mouseExited(MouseEvent e) {
	}
	
	/**
	 * A part of the MouseListener interface. Begins camera tracking if the left
	 * mouse button is down.
	 * @param e the MouseEvent in question.
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftButtonDown = true;
			mouseX = e.getX();
			mouseY = e.getY();
			cameraVector = new Vect3d(getForwardVect());
			cameraVector.invert();
			cameraVector.mult(25);
		}
	}
	
	/**
	 * A part of the MouseListener interface. Ends camera tracking if the left
	 * mouse button is released.
	 * @param e the MouseEvent in question.
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			leftButtonDown = false;
	}
	
	/**
	 * A part of the MouseMotionListener interface. Rotates the camera view
	 * depending on mouse movement.
	 * @param e the MouseEvent in question.
	 */
	public void mouseDragged(MouseEvent e) {
		if (leftButtonDown) {
			//in degrees
			cameraVector.rotateY((float)(mouseX-e.getX())/5f);
			mouseX = e.getX();
			mouseY = e.getY();
		}
	}
	
	/**
	 * A part of the MouseMotionListener interface. Does nothing at the moment.
	 * @param e the MouseEvent in question.
	 */
	public void mouseMoved(MouseEvent e) {
	}
	
	/**
	 * Draws shield and other statistics to the OpenGL context in 2D.
	 * @param gl the OpenGL context on which to draw the stats.
	 * @param glut any instance of the GLUT class.
	 */
	public void drawStats(GL gl, GLUT glut) {
		gl.glColor3f(1f*(100f-getShields())/100f, 1f*getShields()/100f,0.0f);
		gl.glLoadIdentity();
		gl.glDisable(GL.GL_LIGHTING);
		gl.glTranslatef(0.0f,0.0f,-1.0f);
		gl.glRasterPos2f(-.96f, -.76f);
		glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_18, "Shields: "+Math.round(getShields()*10)/10f);
		gl.glLoadIdentity();
		gl.glEnable(GL.GL_LIGHTING);
		/*
		gl.glBegin(GL.GL_QUADS);
		gl.glColor3f(1f, .1f, .1f);
		gl.glVertex2f(-.97f, -.63f);
		gl.glVertex2f(-.80f, -.63f);
		gl.glVertex2f(-.83f+(getShields()/100f)*.3f, -.96f);
		gl.glVertex2f(-.83f+(getShields()/100f)*.3f, -.92f);
		gl.glEnd();*/
	}

}

