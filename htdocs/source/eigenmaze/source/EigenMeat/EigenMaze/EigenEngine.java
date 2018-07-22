package EigenMeat.EigenMaze;

import javax.swing.*;
import java.io.*;
import java.util.Vector;

/**
 * This is the facade for the entire engine. It is a singleton, and provides
 * simple methods to inject entities into the engine. It makes sure those entities
 * get to where they need to be. It also provides some accessor functions for
 * important objects.
 */
public class EigenEngine  {
	
	private DisplayHandler 	eigenDisplay;
	private MobileHandler 	eigenMobileHandler;
	private ParticleManager eigenParticle;
	private Octree		eigenOctree;
	private NetHandler	eigenNet;
	private EntityFactory	eigenFactory;
	private TextDisplay	tDisplay;
	private Player localPlayer;
//	private Scoreboard scoreboard;

	/* (non-Javadoc)
	 * The thread that handles network communication. Reminder: ask Nick if this
	 * really needs to be public.
	 */
	//public Thread 		netThread;
	
  	private static final EigenEngine instance = new EigenEngine();
	
	/**
	 * Gets the single existing instance of the engine.
	 * @return The engine instance.
	 */
	public static EigenEngine instance() {
	  return instance;
	}
	
  	private EigenEngine() {
		eigenDisplay = new DisplayHandler();	
		eigenMobileHandler = new MobileHandler();
		eigenParticle = new ParticleManager();
		eigenDisplay.add(eigenParticle);
		eigenFactory = new EntityFactory();
		tDisplay = new TextDisplay(6);
		eigenDisplay.add(tDisplay);

		eigenOctree = new Octree();

		eigenNet = new NetHandler();
		
		localPlayer = null;
//		scoreboard = new Scoreboard();
//		eigenDisplay.add(scoreboard);
	}
	
	/**
	 * Start a server or connect to a server for a net game. If isServer is
	 * true, ip will be disregarded, but port will be adhered to.
	 * @param isServer true if this game will be a server; false if it will be a client.
	 * @param ip the IP or hostname to connect to if this game is a client.
	 * @param port the port to host or connect on.
	 */
	public void connect(boolean isServer, String ip, int port) {
		eigenNet.connect(isServer, ip, port);
	}
	
	/**
	 * Updates the state of the engine based on Game.tof.
	 */
	public void update() {
		//eigenDisplay.update();
		eigenParticle.update();
		eigenMobileHandler.update();
		eigenDisplay.update();
	}
	
	/**
	 * Adds a Drawable to the engine. Its draw() method will be called
	 * every time the engine's display updates.
	 * @param entity the Drawable to add.
	 * @see DisplayHandler
	 */
	public void add(Drawable entity) {
		if(entity != null) {
			eigenDisplay.add(entity);
		}
	}
	
	/**
	 * Adds a MobileEntity to the engine. Its update() method will be called
	 * every time the engine's game world updates.
	 * @param entity the MobileEntity to add.
	 */
	public void add(MobileEntity entity) {
		if(entity != null) {
			eigenMobileHandler.add(entity);
			eigenDisplay.add(entity);
			System.out.println("Created: " + entity);
		}
	}
	
	/**
	 * Adds a NetEntity to the engine. As often as possible, it will be
	 * serialized and sent to all other clients in a net game.
	 * @param entity the NetEntity to add.
	 * @see NetHandler
	 */
	public void addNet(NetEntity entity) {
		if(entity != null) {
			System.out.println("NET --- Adding a: "+entity.getType());
			eigenNet.add(entity);
			System.out.println("Added to que succesfully: "+entity.getType());
		}
	}

	/*public void removeNet(NetEntity entity) {
		if(entity!=null) {
			eigenNet.remove(entity);
		}
	}*/
	
	/**
	 * Updates the given NetEntity across all connections to the current game.
	 * This works only if the current entity has been added to the engine.
	 * @param entity the NetEntity to update.
	 * @see NetHandler
	 */
	

	public void netUpdate(NetEntity entity) {
		if(entity != null) {
			eigenNet.update(entity);
		}
	}
	
	/**
	 * Adds a ParticleEffect to the engine.
	 * @param peffect the ParticleEffect to add.
	 */
	public void add(ParticleEffect peffect) {
		eigenParticle.createEffect(peffect);
	}
	
	/**
	 * Adds Triangles to the engine's Octree. These triangles will both be
	 * drawn based on frustum and used for collision detection.
	 * @param triangles the array of Triangles to add.
	 * @see Octree
	 */
	public void addTriangles(Triangle[] triangles) {
		eigenOctree.addTriangles(triangles);
	}

	/**
	 * Checks to see if an entity is colliding with any triangles in the Octree.
	 * The Octree will take appropriate action on the entity if it is colliding.
	 * @param entity the MobileEntity to check against.
	 * @see Octree#checkCollision(MobileEntity)
	 */
	public void checkCollision(MobileEntity entity) {
		eigenOctree.checkCollision(entity);
	}
	
	/**
	 * Generates the engine's static Octree.
	 * @see Octree#generate()
	 */
	public void generateOctree() {
		eigenOctree.generate();
	}

	/**
	 * Gets the engine's static Octree.
	 * @return the engine's Octree.
	 */
	public Octree getOctree() {
		return eigenOctree;
	}
	
	/**
	 * Set the game world point at which the rendering camera points.
	 * @param xCord the x-coordinate of the point at which to look.
	 * @param yCord the y-coordinate of the point at which to look.
	 * @param zCord the z-coordinate of the point at which to look.
	 */
	public void setCameraTarget(float xCord, float yCord, float zCord) {
		//eigenDisplay.setCameraTarget(xCord, yCord, zCord);
		eigenDisplay.getCamera().setLookAt(new Vect3d(xCord,yCord,zCord));
	}
	
	/**
	 * Set the rendering camera's game world position.
	 * @param xCord the x-coordinate of the camera location.
	 * @param yCord the y-coordinate of the camera location.
	 * @param zCord the z-coordinate of the camera location.
	 */
	public void setCameraLocation(float xCord, float yCord, float zCord) {
		//eigenDisplay.setCameraLocation(xCord, yCord, zCord);
		eigenDisplay.getCamera().setPosition(new Vect3d(xCord,yCord,zCord));
	}

	/**
	 * Get the rendering Camera object.
	 * @return the Camera object.
	 */
	public Camera getCamera() {
		return eigenDisplay.getCamera();
	}
	
	/**
	 * Get the engine's DisplayHandler.
	 * @return the DisplayHandler object.
	 */
	public DisplayHandler getDisplay() {
		return eigenDisplay;
	}
	
	/**
	 * Get the engine's local Player object (as opposed to other networked players).
	 * @return the locally controlled Player object.
	 */
	public Player getLocalPlayer() {
		return localPlayer;
	}
	
	/**
	 * Set the engine's local Player object.
	 * @return the Player object.
	 */
	public void setLocalPlayer(Player p) {
		localPlayer = p;
	}

	/**
	 * Get all entities within range of a point
	 */
	public Vector getEntities(Vect3d point, float radius) {
		return eigenMobileHandler.getEntities(point,radius);
	}

	public int getRenderFrame() {
		return eigenDisplay.getRenderFrame();
	}
	
	/**
	 * Get the universal soft-loader factory for this engine.
	 */
	public EntityFactory getFactory() {
		return eigenFactory;
	}

	/**
	 * Get the engine's user interface text display object.
	 */
	public TextDisplay getTextDisplay() {
		return tDisplay;
	}

	/**
	 * Get the instance of LightManager that handles the engine's light sources.
	 */
	public LightManager getLightManager() {
		return eigenDisplay.getLightManager();
	}
	
	/**
	 * Get the game's scoreboard.
	 */
//	public Scoreboard getScoreboard() {
//		return scoreboard;
//	}
}
