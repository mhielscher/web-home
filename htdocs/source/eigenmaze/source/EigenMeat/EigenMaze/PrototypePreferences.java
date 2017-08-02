package EigenMeat.EigenMaze;

class PrototypePreferences extends Preferences {
	public PrototypePreferences() {
		super();
	}
	
	public void setPlayerName(String p) {
		playerName = p;
	}
	
	public void setWindowedResX(int x) {
		windowedResX = x;
	}
	
	public void setWindowedResY(int y) {
		windowedResY = y;
	}
	
	public void setDefaultMazeSizeX(int x) {
		defaultMazeSizeX = x;
	}
	
	public void setDefaultMazeSizeY(int y) {
		defaultMazeSizeY = y;
	}
	
	public void setStartsFullscreen(boolean f) {
		startsFullscreen = f;
	}
	
	public void setEntityShadows(boolean e) {
		entityShadows = e;
	}
}