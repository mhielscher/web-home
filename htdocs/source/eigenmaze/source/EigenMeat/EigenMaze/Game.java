package EigenMeat.EigenMaze;
import java.applet.Applet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.io.*;
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.GridBagLayout;
import java.applet.*;
/**
 * The class that run the engine and links it to an interface.
 */
public class Game extends JFrame implements WindowListener, Runnable {
	
	private Thread gameLoop;
	
	private static Maze3d maze;
	private ParticleEffect effect;
	
	/**
	 * The time step between updates. This variable changes with each update depending
	 * on processor load and other things, and is a real indicator of time passed.
	 */
	public static float tof; 
	
	private boolean isFullscreen = false;
	private int windowedResX = 800;
	private int windowedResY = 600;
	private MazeOptionWindow mazeOptionWindow;
	private MultiplayerWindow multiplayerWindow;
	private PreferencesWindow preferencesWindow;
	private SoftLoader loader;

	Player eigenPlayer;
	Ship drone;
	static Flag flag;
	
	private AppKeyListener listener;
	
	public Game() {
		//EigenEngine.instance().connect(false);
		init();
	}
	public Game(String s){
		//EigenEngine.instance().connect(false);
		init();
		
	}

	/**
	 * Get the Maze3d object being used by this Game.
	 */
	public static Maze3d getMaze() {
		return maze;
	}
	
	private class MenuActionListener implements ActionListener {
		public MenuActionListener() {}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem)(e.getSource());
			if(source.getText().equals("Quit"))
			{
				endGame();
				System.exit(0);
			}
			else if(source.getText().equals("New Maze"))
			{ 
				mazeOptionPrompt();
			}
			else if(source.getText().equals("Create a Net Game"))
			{
				EigenEngine.instance().connect(true, "127.0.0.1", 4224);
			}
			else if(source.getText().equals("Join a Net Game"))
			{
				multiplayerPrompt();
			}
			else if(source.getText().equals("FPS Display OFF"))
			{
				EigenEngine.instance().getDisplay().setFPSDisplay(false);
			}
			else if(source.getText().equals("FPS Display ON"))
			{
				EigenEngine.instance().getDisplay().setFPSDisplay(true);
			}
			else if(source.getText().equals("Preferences"))
			{
				preferencesPrompt();
			}
		}
	}
	
	private class AppKeyListener implements KeyListener {
		public AppKeyListener() {
			System.out.println("AppKeyListener created.");
		}
		
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == 'F') {
				System.out.println("F typed.");
				//isFullscreen = !isFullscreen;
				//setFullscreen(isFullscreen);
			}
		}

		public void keyPressed(KeyEvent e) {
		}
		
		public void keyReleased(KeyEvent e) {
		}
	}
	
	private void init() {

		windowedResX = Preferences.instance().getWindowedResX();
		windowedResY = Preferences.instance().getWindowedResY();
		isFullscreen = Preferences.instance().getStartsFullscreen();

		setSize(windowedResX,windowedResY+45); //+45 to account for menubar
		EigenEngine.instance().getDisplay().setSize(windowedResX, windowedResY);
		
		if (isFullscreen)
			setFullscreen(true);
		
		loader = new SoftLoader();
		NetProjectileManager.instance();
		getContentPane().add(EigenEngine.instance().getDisplay());

		final int defaultMazeSizeX = Preferences.instance().getDefaultMazeSizeX();
		final int defaultMazeSizeY = Preferences.instance().getDefaultMazeSizeY();
		
		maze = new Maze3d(defaultMazeSizeX,defaultMazeSizeY,3f,8f,3f,16f,16f);
		
		addKeyListener(maze);
		listener = new AppKeyListener();
		addKeyListener(listener);
		
		File entityDirectory = new File("data/entities");
		String[] files = entityDirectory.list();
		for (int i=0; i<files.length; i++)
			if (files[i].indexOf(".xml") != -1)
				EigenEngine.instance().getFactory().addEntityType(loader.loadEntityFile("data/entities/"+files[i]));
		
		File textureDirectory = new File("data/textures");
		String[] textures = textureDirectory.list();
		for (int i=0; i<textures.length; i++)
			if (textures[i].indexOf(".png") != -1 || textures[i].indexOf(".jpg") != -1 || textures[i].indexOf(".gif") != -1)
				TextureManager.getInstance().loadTexture("data/textures/"+textures[i]);

		eigenPlayer = new Player();
		addKeyListener(eigenPlayer);
		eigenPlayer.activate();
		eigenPlayer.setPlayerName(Preferences.instance().getPlayerName());
		EigenEngine.instance().getDisplay().setMouseListeners(eigenPlayer, eigenPlayer);
		EigenEngine.instance().setLocalPlayer(eigenPlayer);
		
		drone = new Drone();
		drone.setPosition(maze.getRandomLocation());
		
		flag = new Flag();
		flag.setPosition(maze.getRandomLocation());
		
		Powerup[] powerups = new Powerup[4];
		for (int i=0; i<powerups.length; i++) {
			powerups[i] = new Powerup();
			powerups[i].setPosition(maze.getRandomLocation());
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		if (!isFullscreen) {
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			JMenu menu = new JMenu("Menu");
			menu.setMnemonic(KeyEvent.VK_M);
			JMenuBar mbar = new JMenuBar();
			JMenuItem mitemnewmaze = new JMenuItem("New Maze");
			mitemnewmaze.setMnemonic(KeyEvent.VK_N);
			mitemnewmaze.setFocusable(false);
			JMenuItem mitemmults = new JMenuItem("Create a Net Game");
			mitemmults.setMnemonic(KeyEvent.VK_C);
			mitemmults.setFocusable(false);
			JMenuItem mitemmultc = new JMenuItem("Join a Net Game");
			mitemmultc.setMnemonic(KeyEvent.VK_J);
			mitemmultc.setFocusable(false);
			JMenuItem mitempref = new JMenuItem("Preferences");
			mitempref.setMnemonic(KeyEvent.VK_P);
			mitempref.setFocusable(false);
			JMenuItem mitemquit = new JMenuItem("Quit");
			mitemquit.setMnemonic(KeyEvent.VK_Q);
			mitemquit.setFocusable(false);
			JMenu mfps = new JMenu("FPS Display");
			ButtonGroup grp = new ButtonGroup();
			mfps.setMnemonic(KeyEvent.VK_F);
			mfps.setFocusable(false);
			JRadioButtonMenuItem rbMenuitem = new JRadioButtonMenuItem("FPS Display ON");
			rbMenuitem.setMnemonic(KeyEvent.VK_D);
			rbMenuitem.setFocusable(false);
			JRadioButtonMenuItem rbMenuitem2 = new JRadioButtonMenuItem("FPS Display OFF");
			rbMenuitem2.setMnemonic(KeyEvent.VK_S);
			rbMenuitem2.setFocusable(false);
			rbMenuitem2.setSelected(true);
			grp.add(rbMenuitem);
			grp.add(rbMenuitem2);
			menu.add(mitemnewmaze);
			menu.add(mfps);
			mfps.add(rbMenuitem);
			mfps.add(rbMenuitem2);
			menu.add(mitempref);
			menu.addSeparator();
			menu.add(mitemmults);
			menu.add(mitemmultc);
			menu.addSeparator();
			menu.add(mitemquit);
			mbar.add(menu);	
			this.setJMenuBar(mbar);
			mbar.setFocusable(false);
			MenuActionListener listener = new MenuActionListener();
			mitemquit.addActionListener(listener);
			mitempref.addActionListener(listener);
			mitemmults.addActionListener(listener);
			mitemmultc.addActionListener(listener);
			rbMenuitem2.addActionListener(listener);
			rbMenuitem.addActionListener(listener);
			mitemnewmaze.addActionListener(listener);
		}
		
		gameLoop = new Thread(this);
		gameLoop.start();
		setVisible(true);
	}
	
	private void setFullscreen(boolean f) {
		//setVisible(false);
		if (f) {
			if (isFullscreen && !isUndecorated())
				setUndecorated(true);
			GraphicsEnvironment ge=null;
			GraphicsDevice gd=null;
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gd = ge.getDefaultScreenDevice();
			//if (gd.isFullScreenSupported()) {
				gd.setFullScreenWindow(this);
				DisplayMode dm = gd.getDisplayMode();
				EigenEngine.instance().getDisplay().setSize(dm.getWidth(), dm.getHeight());
			//}
		}
		else {
			//setUndecorated(false);
			GraphicsEnvironment ge=null;
			GraphicsDevice gd=null;
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gd = ge.getDefaultScreenDevice();
		//	if(gd.isFullScreenSupported())
				gd.setFullScreenWindow(null);
				EigenEngine.instance().getDisplay().setSize(windowedResX, windowedResY);
		}
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		//setVisible(true);
	}
	
	/**
	 * Ends the currently running game. Does nothing at the moment.
	 */
	private void endGame() {}
	
	/**
	 * Part of the Runnable interface. Begins the game loop.
	 * @see EigenMeat.EigenMaze.Game#gameLoop()
	 */
	public void run() {
		gameLoop();
	}
	
	/**
	 * Opens a window with maze options.
	 */
	private void mazeOptionPrompt() {
		mazeOptionWindow = new MazeOptionWindow(maze);
		mazeOptionWindow.addWindowListener(this);
	}
	
	/**
	 * Opens a window for connecting to a multiplayer server.
	 */
	private void multiplayerPrompt() {
		multiplayerWindow = new MultiplayerWindow();
		multiplayerWindow.addWindowListener(this);
	}
	
	private void preferencesPrompt() {
		Preferences.reload();
		preferencesWindow = new PreferencesWindow();
		preferencesWindow.addWindowListener(this);
	}
	
	private class MultiplayerWindow extends JFrame implements ActionListener {
		private JTextField ipField, portField;
		private JButton okButton;
		
		MultiplayerWindow() {
			init();
		}

		private void init() {
			setSize(350,150);
			Container c = getContentPane();
			c.setLayout(null);
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setVisible(true);
			setTitle("Join a Net Game");
			JLabel title = new JLabel("Join by IP");
			title.setFont(new Font("SansSerif",Font.BOLD,12));
			title.setBounds(3,1,175,30);
			c.add(title);
			JLabel ipLabel = new JLabel("IP Address and Port:");
			ipLabel.setFont(new Font("Arial",Font.PLAIN,10));
			ipLabel.setBounds(20,30,100,20);
			c.add(ipLabel);
			ipField = new JTextField("127.0.0.1");
			ipField.setBounds(120,30,120,20);
			c.add(ipField);
			portField = new JTextField("4224");
			portField.setBounds(250,30,50,20);
			c.add(portField);
			
			okButton = new JButton("Ok");
			okButton.setBounds(230,70,75,20);
			okButton.addActionListener(this);
			c.add(okButton);
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e) {
			if((Object)okButton == e.getSource())
			{
				System.out.println("test");
				EigenEngine.instance().connect(false, ipField.getText(), Integer.parseInt(portField.getText()));
				this.dispose();
			}
				
		}
	}



	
	private class MazeOptionWindow extends JFrame implements ActionListener{
		
		private JTextField msizeWField,msizeLField,csizeWField,csizeLField,wsizeXField,wsizeYField;
		private JButton okButton;
		private Maze3d maze;
		//private Jbutton cancelButton
		private boolean changed;
		
		MazeOptionWindow() {
			//maze = new Maze3d();
			init();
			
		}
		MazeOptionWindow(Maze3d m) {
			maze = m;
			init();
		}
		private void init() {

			changed = false;
			JLabel title = new JLabel("New Maze");
			Container c = getContentPane();
			c.setLayout(null);
			setSize(400,200);
			title.setFont(new Font("SansSerif",Font.BOLD,12));
			title.setBounds(3,1,175,30);
			c.add(title);
			JLabel msizeLabel = new JLabel("Maze Size:");
			msizeLabel.setFont(new Font("Arial",Font.PLAIN,10));
			msizeLabel.setBounds(20,20,120,20);
			c.add(msizeLabel);
			msizeWField = new JTextField(3);
			msizeLField = new JTextField(3);
			msizeWField.setText(""+maze.getWidthCells());
			msizeLField.setText("" + maze.getHeightCells());
			msizeWField.setBounds(150,20,50,20);
			msizeLField.setBounds(210,20,50,20);
			c.add(msizeWField);
			c.add(msizeLField);
			csizeWField = new JTextField(3);
			csizeLField = new JTextField(3);
			csizeWField.setText("" + maze.getCellSizeX());
			csizeLField.setText("" + maze.getCellSizeZ());
			csizeWField.setBounds(150,45,50,20);
			csizeLField.setBounds(210,45,50,20);
			c.add(csizeWField);
			c.add(csizeLField);

			JLabel csize,wsize;
			csize = new JLabel("Cell Size (WxL):");
			csize.setFont(new Font("Arial",Font.PLAIN,10));
			csize.setBounds(20,45,120,20);
			c.add(csize);
			wsize = new JLabel("Wall Size (WxH):");
			wsize.setFont(new Font("Arial",Font.PLAIN,10));
			wsize.setBounds(20,70,120,20);
			c.add(wsize);
			wsizeXField = new JTextField("" + maze.getWallSizeWX());
			wsizeXField.setBounds(150,70,50,20);
			wsizeYField = new JTextField("" + maze.getWallSizeWH());
			wsizeYField.setBounds(210,70,50,20);
			c.add(wsizeXField);
			c.add(wsizeYField);
			
			okButton = new JButton("Ok");
			okButton.setBounds(220,120,75,20);
			okButton.addActionListener(this);
			c.add(okButton);
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setVisible(true);
		}
		public Maze3d getStuff() {
			return maze;
		}
		
		public void actionPerformed(ActionEvent e) {
			if((Object)okButton == e.getSource())
			{
				System.out.println("test");
				changed = true;
				if(changed) {
					float wx,wy,wz,cx,cy;
					try {
						wx = Float.parseFloat(wsizeXField.getText());
						wy = Float.parseFloat(wsizeYField.getText());
						cx = Float.parseFloat(csizeWField.getText());
						cy = Float.parseFloat(csizeLField.getText());
						maze = new Maze3d(Integer.parseInt(msizeWField.getText()),Integer.parseInt(msizeLField.getText()),wx,wy,wx,cx,cy);
						maze.generate();
						this.dispose();
					} catch (Exception es) {
						 JOptionPane.showMessageDialog(null, "Invalid Input Values ","Error", JOptionPane.ERROR_MESSAGE);
					}			
				}
			}
				
		}
	}
	
	private class PreferencesWindow extends JFrame implements ActionListener{
		
		private JTextField playerNameField;
		private JTextField resXField, resYField;
		private JTextField defMazeXField, defMazeYField;
		private JRadioButton fullscreenOnRadio, fullscreenOffRadio;
		private JCheckBox entityShadowsBox;
		private JButton okButton;
		private JButton cancelButton;
		
		PreferencesWindow() {
			init();
		}
		
		private void init() {
			JLabel title = new JLabel("Preferences");
			Container c = getContentPane();
			c.setLayout(null);
			setSize(400,250);
			title.setFont(new Font("SansSerif",Font.BOLD,12));
			title.setBounds(3,1,175,30);
			c.add(title);
			
			JLabel nameLabel = new JLabel("Player Name");
			nameLabel.setFont(new Font("Arial",Font.PLAIN,10));
			nameLabel.setBounds(20,20,120,20);
			c.add(nameLabel);
			playerNameField = new JTextField(3);
			playerNameField.setText(Preferences.instance().getPlayerName());
			playerNameField.setBounds(150,20,150,20);
			c.add(playerNameField);
			
			JLabel resLabel = new JLabel("Windowed Resolution");
			resLabel.setFont(new Font("Arial",Font.PLAIN,10));
			resLabel.setBounds(20,45,120,20);
			c.add(resLabel);
			resXField = new JTextField(3);
			resYField = new JTextField(3);
			resXField.setText(""+windowedResX);
			resYField.setText(""+windowedResY);
			resXField.setBounds(150,45,50,20);
			resYField.setBounds(210,45,50,20);
			c.add(resXField);
			c.add(resYField);

			JLabel msizeLabel,fullscreenLabel;
			msizeLabel = new JLabel("Default Maze Size");
			msizeLabel.setFont(new Font("Arial",Font.PLAIN,10));
			msizeLabel.setBounds(20,70,120,20);
			c.add(msizeLabel);
			defMazeXField = new JTextField(3);
			defMazeYField = new JTextField(3);
			defMazeXField.setText(""+Preferences.instance().getDefaultMazeSizeX());
			defMazeYField.setText(""+Preferences.instance().getDefaultMazeSizeY());
			defMazeXField.setBounds(150,70,50,20);
			defMazeYField.setBounds(210,70,50,20);
			c.add(defMazeXField);
			c.add(defMazeYField);
			
			fullscreenLabel = new JLabel("Fullscreen");
			fullscreenLabel.setFont(new Font("Arial",Font.PLAIN,10));
			fullscreenLabel.setBounds(20,95,120,20);
			c.add(fullscreenLabel);
			fullscreenOnRadio = new JRadioButton("Fullscreen", Preferences.instance().getStartsFullscreen());
			fullscreenOnRadio.setBounds(150,95,150,20);
			fullscreenOffRadio = new JRadioButton("Windowed", !Preferences.instance().getStartsFullscreen());
			fullscreenOffRadio.setBounds(150,120,150,20);
			fullscreenOnRadio.addActionListener(this);
			fullscreenOffRadio.addActionListener(this);
			c.add(fullscreenOnRadio);
			c.add(fullscreenOffRadio);
			
			entityShadowsBox = new JCheckBox("Draw Entity Shadows", Preferences.instance().getEntityShadows());
			entityShadowsBox.setBounds(150,145,160,20);
			c.add(entityShadowsBox);
			
			okButton = new JButton("Okay");
			okButton.setBounds(220,180,75,20);
			okButton.addActionListener(this);
			c.add(okButton);
			
			cancelButton = new JButton("Cancel");
			cancelButton.setBounds(130,180,75,20);
			cancelButton.addActionListener(this);
			c.add(cancelButton);
			
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == fullscreenOnRadio) {
				fullscreenOnRadio.setSelected(true);
				fullscreenOffRadio.setSelected(false);
			}
			else if (e.getSource() == fullscreenOffRadio) {
				fullscreenOnRadio.setSelected(false);
				fullscreenOffRadio.setSelected(true);
			}
			else if((Object)okButton == e.getSource())
			{
				String name;
				int resX, resY;
				int mazeX, mazeY;
				boolean fullscreen, shadows;
				try {
					name = playerNameField.getText();
					resX = Integer.parseInt(resXField.getText());
					resY = Integer.parseInt(resYField.getText());
					mazeX = Integer.parseInt(defMazeXField.getText());
					mazeY = Integer.parseInt(defMazeYField.getText());
					fullscreen = fullscreenOnRadio.isSelected();
					shadows = entityShadowsBox.isSelected();
					PrototypePreferences proto = new PrototypePreferences();
					proto.setPlayerName(name);
					proto.setWindowedResX(resX);
					proto.setWindowedResY(resY);
					proto.setDefaultMazeSizeX(mazeX);
					proto.setDefaultMazeSizeY(mazeY);
					proto.setStartsFullscreen(fullscreen);
					proto.setEntityShadows(shadows);
					SoftLoader loader = new SoftLoader();
					loader.writePreferencesFile(proto, "data/Preferences.xml");
					Preferences.reload();
					this.dispose();
				} catch (Exception es) {
					 JOptionPane.showMessageDialog(null, "Invalid Input Values ","Error", JOptionPane.ERROR_MESSAGE);
				}			
			}
			else if (e.getSource() == cancelButton)
				this.dispose();
			
		}
	}
	
	/**
	 * Runs the game loop. Should be run in its own thread.
	 */
	public void gameLoop() {
		long lastDisplayFPS = System.currentTimeMillis();
		int frames = 0;
		DecimalFormat format = new DecimalFormat("0.00");
		boolean done = false;
		boolean thread = true;
		
		while (!done) {
			long startOfFrame = System.currentTimeMillis();
			
			if(eigenPlayer.getRespawn()) {
				eigenPlayer.setDead(false);
				eigenPlayer.setVelocity(new Vect3d(0,0,0));
				eigenPlayer.setYRot((float)(Math.random()*360));
				eigenPlayer.setShields(100);
				eigenPlayer.setPosition(maze.getRandomLocation());
				eigenPlayer.setRespawn(false);
				
				EigenEngine.instance().add(eigenPlayer);
			}
		
			if(drone.isDead()) {
				((Drone)drone).respawn();
				drone.setPosition(maze.getRandomLocation());
			}
			
			EigenEngine.instance().update();
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {}
	
			long endOfFrame = System.currentTimeMillis();
			tof = (float) (((double)(endOfFrame - startOfFrame))/1000.0);
			frames++;
			if((endOfFrame-lastDisplayFPS)>=2000) {
				System.out.println(format.format(frames/((endOfFrame-lastDisplayFPS)/1000.0f)) + " UPS ("+frames+" updates in "+(endOfFrame-lastDisplayFPS)/1000.0f+" seconds) (tof="+tof+")");
				EigenEngine.instance().getTextDisplay().addLine("" + eigenPlayer.getScore());
				frames = 0;
				lastDisplayFPS = System.currentTimeMillis();
			}
		}
	}
	
	/**
	 * Ends fullscreen mode if it is activated.
	 */
	public void finalize() {
		if (isFullscreen)
			setFullscreen(false);
	}
	
	/**
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {}
	
	/**
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
		if((Object)mazeOptionWindow==e.getSource())
                {
                        maze = mazeOptionWindow.getStuff();
			addKeyListener(maze);
                }
		this.toFront();
	}
	
	/**
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		if((Object)mazeOptionWindow==e.getSource())
		{
			maze =  mazeOptionWindow.getStuff();
			addKeyListener(maze);	
		//	maze = new Maze3d(x[0],x[1]);
		}
	}
	
	/**
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {}
	
	/**
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {}
	
	/**
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {}
	
	/**
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {}
}
