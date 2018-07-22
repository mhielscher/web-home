package EigenMeat.EigenMaze;

/**
 * Preferences is a singleton soft-loaded class that contains values of global engine variables
 * that are usually hardcoded. Use of the Preferences object and the SoftLoader to save settings
 * to disk can allow for customization appropriate to each user's own system.
 */
public class Preferences {
	private static Preferences instance = null;
	
	public static Preferences instance() {
		if (instance == null)
			reload();
		return instance;
	}
	
	public static void reload() {
		SoftLoader loader = new SoftLoader();
		instance = loader.loadPreferencesFile("data/Preferences.xml");
	}
	
	//list of preference data
	protected String playerName;
	protected int windowedResX;
	protected int windowedResY;
	protected int defaultMazeSizeX;
	protected int defaultMazeSizeY;
	protected boolean startsFullscreen;
	protected boolean entityShadows;
	
	protected Preferences() {
		//hardcoded backup default values
		playerName = "Player";
		windowedResX = 800;
		windowedResY = 600;
		defaultMazeSizeX = 5;
		defaultMazeSizeY = 5;
		startsFullscreen = false;
		entityShadows = true;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getWindowedResX() {
		return windowedResX;
	}
	
	public int getWindowedResY() {
		return windowedResY;
	}
	
	public int getDefaultMazeSizeX() {
		return defaultMazeSizeX;
	}
	
	public int getDefaultMazeSizeY() {
		return defaultMazeSizeY;
	}
	
	public boolean getStartsFullscreen() {
		return startsFullscreen;
	}
	
	public boolean getEntityShadows() {
		return entityShadows;
	}
}