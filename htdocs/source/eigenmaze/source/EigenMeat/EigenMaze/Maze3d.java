package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import java.awt.event.*;
import java.awt.Color;
import java.nio.*;

/**
 * Draws a Maze in 3d.
 */
public class Maze3d extends Maze implements KeyListener {
	private Mesh xwall, zwall,floor,spacer;

	//dimensions of the maze cells and walls
	private float cellSizeX, cellSizeZ;
	private float wallWidthX, wallWidthZ, wallHeight;
	private float mazeSizeX, mazeSizeZ;

	private ParticleEffect shell,inner;

	/**
	 * Constructor
	 * @param w Number of cells wide the maze is
	 * @param h Number of cells long the maze is
	 * @param wx width of wall in x direction
	 * @param wy width of wall in y diretion
	 * @param wz width of wall in z direction
	 * @param cx cell size in x direction
	 * @param cy cell size in long direction
	 */
	public Maze3d(int w, int h,float wx,float wy, float wz, float cx,float cy) {
		super(w,h);
		
		setWallSize(wx,wy,wz);
		setCellSize(cx,cy);

		shell = new ParticleEffect();
		shell.setPosition(0,0,0);
		shell.setSpeedRange(75,80);
		shell.setColorRange(.1f,.1f,0f,.5f,.2f,0);
		shell.setSize(200);
		shell.setLifeRange(750,1000);
		shell.setGravity(0,-150,0);
		
		inner = new ParticleEffect();
		inner.setPosition(0,0,0);
		inner.setSpeedRange(10,75);
		inner.setColorRange(0f,.1f,.3f,0f,.2f,.8f);
		inner.setSize(200);
		inner.setLifeRange(500,1000);
		inner.setGravity(0,-150,0);
	
		initializeMazeMeshes();
		EigenEngine.instance().addNet(this);
	}
	
	/**
	 * Sets the wall sizes.
	 * @param wx the width of the walls that are aligned with the x axis
	 * @param wh wall height
	 * @param wz the width of the walls that are aligned with the z axis
	 */
	public void setWallSize(float wx, float wh, float wz) {
		wallWidthX = wx;
		wallHeight = wh;
		wallWidthZ = wz;
	}


	/**
	 * Returns the width of the wall in the x direction
	 * @return width of the wall in the x direction
	 */
	public float getWallSizeWX() {
		return wallWidthX;
	}
	

	/**
	 * Returns the hieght of the wall
	 * @return hieght of the wall
	 */
	public float getWallSizeWH() {
		return wallHeight;
	}


	/**
	 * Returns the width of the wall in the z direction
	 * @return width of the wall in the z direction
	 */
	public float getWallSizeWZ() {
		return wallWidthZ;
	}

	/**
	 * Sets the cell size.
	 * @param cellx cell size in the x direction
	 * @param cellz cell size in the z direction
	 */
	public void setCellSize(float cellx, float cellz) {
		cellSizeX = cellx;
		cellSizeZ = cellz;
	}
	

	/**
	 * Returns cell size n the x direction
	 * @return cell size in the x direction
	 */
	public float getCellSizeX() {
		return cellSizeX;
	}

	/**
	 * Returns the cell size in the z direction
	 * @return cell size in the z direction
	 */
	public float getCellSizeZ() {
		return cellSizeZ;
	}
	
	/**
	 * Initializes the Maze Meshes
	 */
	private void initializeMazeMeshes() {
		mazeSizeX = (cellSizeX+wallWidthX)*getWidthCells() + wallWidthX;
		mazeSizeZ = (cellSizeZ+wallWidthZ)*getHeightCells() + wallWidthZ;
	
		xwall = MeshLoader.createBox(cellSizeX,wallHeight,wallWidthX, "data/textures/stone.png");
		xwall.removeTrianglesWithNormal(new Vect3d(1,0,0));
		xwall.removeTrianglesWithNormal(new Vect3d(-1,0,0));
		xwall.removeTrianglesWithNormal(new Vect3d(0,-1,0));
		
		zwall = MeshLoader.createBox(wallWidthZ,wallHeight,cellSizeZ, "data/textures/stone.png");
		zwall.removeTrianglesWithNormal(new Vect3d(0,0,1));
		zwall.removeTrianglesWithNormal(new Vect3d(0,0,-1));
		zwall.removeTrianglesWithNormal(new Vect3d(0,-1,0));

		
		spacer = MeshLoader.createBox(wallWidthZ,wallHeight,wallWidthX, "data/textures/stone.png");
		spacer.removeTrianglesWithNormal(new Vect3d(0,-1,0));
		
		floor = MeshLoader.createSquareXZ(getCellSizeX()+wallWidthZ,getCellSizeZ()+wallWidthX,"data/textures/stone.png");
		
		generate();
	}
	
	private Mesh getSpacer(int x, int y) {
		Mesh m = new Mesh(spacer);

		if(isWallUp(x,y,EAST))
			m.removeTrianglesWithNormal(new Vect3d(0,0,-1));
		if(isWallUp(x,y,SOUTH))
			m.removeTrianglesWithNormal(new Vect3d(-1,0,0));
		if(x != getWidthCells()-1 && isWallUp(x+1,y,SOUTH)) 
			m.removeTrianglesWithNormal(new Vect3d(1,0,0));
		if(y != getHeightCells()-1 && isWallUp(x,y+1,EAST))
			m.removeTrianglesWithNormal(new Vect3d(0,0,1));
		
		return m;
	}
	
	/**
	 * Adds all the maze triangles to the Octree
	 */
	public void getTriangles() {
		float x,y,z;
		y = 0;
	
		Mesh topSpace = new Mesh(spacer);
		topSpace.removeTrianglesWithNormal(new Vect3d(1,0,0));
		topSpace.removeTrianglesWithNormal(new Vect3d(-1,0,0));
		Mesh leftSpace = new Mesh(spacer);
		leftSpace.removeTrianglesWithNormal(new Vect3d(0,0,1));
		leftSpace.removeTrianglesWithNormal(new Vect3d(0,0,-1));
		
		EigenEngine.instance().getOctree().clearTriangles();

// buffers: simple world for testing...
//		EigenEngine.instance().addTriangles(spacer.translate(0,-5,0));
//		EigenEngine.instance().addTriangles(floor.translate(-5,-10,5));

		for(int i=0; i<getHeightCells(); i++)
			for(int j=0; j<getWidthCells(); j++) {
				x = j*(wallWidthX+cellSizeX) - mazeSizeX/2;
				z = i*(wallWidthZ+cellSizeZ) - mazeSizeZ/2;

				if(isWallUp(j,i,NORTH) && i==0) {
					EigenEngine.instance().addTriangles(xwall.translate(x+wallWidthZ+cellSizeX/2.0f,y,z+wallWidthX/2.0f));
					EigenEngine.instance().addTriangles(topSpace.translate(x+wallWidthZ*1.5f+cellSizeX,y,z+wallWidthX/2));
					
				}

				if(isWallUp(j,i,WEST) && j==0) {
					EigenEngine.instance().addTriangles(zwall.translate(x+wallWidthZ/2.0f,y,z+wallWidthX+cellSizeZ/2.0f));
					EigenEngine.instance().addTriangles(leftSpace.translate(x+wallWidthZ/2,y,z+wallWidthX/2));
				}

				if(isWallUp(j,i,EAST)) {
					EigenEngine.instance().addTriangles(zwall.translate(x+wallWidthZ*1.5f+cellSizeX,y,z+wallWidthX+cellSizeZ/2.0f));
				}

				if(isWallUp(j,i,SOUTH)) {
					EigenEngine.instance().addTriangles(xwall.translate(x+wallWidthZ+cellSizeX/2.0f,y,z+wallWidthX*1.5f+cellSizeZ));
				}

				if(j == 0 && i == getHeightCells()-1) {
					EigenEngine.instance().addTriangles(spacer.translate(x+wallWidthZ/2.0f,y,z+wallWidthX*1.5f+cellSizeZ));
				}
							
				EigenEngine.instance().addTriangles(getSpacer(j,i).translate(x+wallWidthZ*1.5f+cellSizeX,y,z+wallWidthX*1.5f+cellSizeZ));

				EigenEngine.instance().addTriangles(floor.translate(x+wallWidthZ+cellSizeX/2.0f,y-wallHeight/2.0f,z+wallWidthX+cellSizeZ/2.0f));
			}
			
		EigenEngine.instance().generateOctree();
	}
	/**
	 * Generates a new maze and adds all the data to the Octree.
	 */
	public void generate() {
		super.generate();
		deleteRandomWalls(.3f);
		getTriangles();
	}

	/**
	 * Get random location, located at the center of a random cell.
	 * @return Vect3d of position
	 */
	public Vect3d getRandomLocation() {
		Vect3d v = new Vect3d();

		v.y = 0;
		v.x = (int)(getWidthCells()*(float)Math.random());
		v.z = (int)(getHeightCells()*(float)Math.random());

		v.x = (wallWidthZ+cellSizeX/2.0f)+ v.x*(cellSizeX+wallWidthZ) -  mazeSizeX/2.0f;
		v.z = (wallWidthX+cellSizeX/2.0f)+ v.z*(cellSizeZ+wallWidthX) - mazeSizeZ/2.0f;

		return v;
	}
	
	/**
	 * For key listener.
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * For key listener.
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R) {
			generate();
			
			EigenEngine.instance().add(shell);
	  		EigenEngine.instance().add(inner);
			EigenEngine.instance().netUpdate(this);
		}
	}

	/**
	 * For key listener.
	 */
	public void keyReleased(KeyEvent e) {
	}

	public void receiveData(ByteBuffer bb) {
		super.receiveData(bb);
		getTriangles();
	}
}
