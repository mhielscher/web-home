package EigenMeat.EigenMaze;

import net.java.games.jogl.*;
import net.java.games.jogl.util.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.logging.*;

class TextureManager {
	private static Logger log = Logger.getLogger(TextureManager.class.getName()); 
	private GL gl;
	
	static {
		//log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
	}
	
	// singleton for now, until I can think of a better way to let Triangles access Textures
	public static final TextureManager instance = new TextureManager();
	
	public static TextureManager getInstance() {
		return instance;
	}

	private Vector textureCache; //of Texture's
	private int currentlyBoundID;
	private String currentlyBoundName;

	private boolean multiTextureSupport;
	private int maxTextureUnits;
	
	public TextureManager() {
		textureCache = new Vector(0,1);
		currentlyBoundName = null;

		maxTextureUnits = 0;
		multiTextureSupport = false;
	}

	public void init(GL gl) {
		this.gl = gl;
		
		if(GLExtensions.checkExtension("GL_ARB_multitexture")) {
			int t[] = new int[1];
			gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS_ARB,t);
			maxTextureUnits= t[0];
				
			//log.info("GL_ARB_multitexture supported");
			System.out.println("GL_ARB_multitexture supported - "+maxTextureUnits+" texture units avaiable");

			multiTextureSupport = true;
		} else {
			log.warning("GL_ARB_multitexture not supported");
		}
	}

	public boolean multiTextureSupport() {
		return multiTextureSupport;
	}
	
	public void bindTexture(GLDrawable gldraw, String filename) {
		if (currentlyBoundName != null && currentlyBoundName.equals(filename))
			return;
		
		Texture texture = retrieveCachedTexture(filename);
		if (texture == null) {
			texture = loadTexture(filename);
		}
		texture.bind(gldraw);
		currentlyBoundID = texture.getID();
		currentlyBoundName = filename;
	}
	
	public void bindTexture(GLDrawable gldraw, Texture tex) {
		if(currentlyBoundID == tex.getID())
			return;

		tex.bind(gldraw);
		currentlyBoundID = tex.getID();
	}
	
	public void unbindTexture(GL gl) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		currentlyBoundName = null;
		currentlyBoundID = 0;
	}
	
	public Texture loadTexture(String filename) {
		if (filename == null)
			return null;
		
		Texture cache = retrieveCachedTexture(filename);
		if (cache != null) { 
			//System.out.println("texture cache hit for "+filename);
			return cache;
		}
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filename));
		} catch (IOException e) {
			e.printStackTrace(); //for release, replace with non-fatal, non-debug error
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		int imageType = image.getType();
		Raster r = image.getRaster();
		int[] img = null;
		img = r.getPixels(0, 0, width, height, img);
		ByteBuffer bytes = ByteBuffer.allocateDirect(image.getHeight() * image.getWidth() * 3);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				bytes.put((byte) img[(y * width + x) * 3]);
				bytes.put((byte) img[(y * width + x) * 3 + 1]);
				bytes.put((byte) img[(y * width + x) * 3 + 2]);
			}
		}

		Texture tex;
		tex = new Texture(filename,bytes,3,GL.GL_RGB,width,height);
		
		textureCache.add(tex);

		return tex;
	}

	public void loadTextureIntoGL(GLDrawable gldraw, Texture tex) {
		GL gl = gldraw.getGL();
		int [] texID = new int[1];
		gl.glGenTextures(1,texID);
		gl.glBindTexture(GL.GL_TEXTURE_2D,texID[0]);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);//_MIPMAP_NEAREST);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, tex.getInternalFormat(),tex.getWidth(),tex.getHeight(), 0,tex.getFormat(), GL.GL_UNSIGNED_BYTE, tex.getBuffer());
		
//		gldraw.getGLU().gluBuild2DMipmaps(GL.GL_TEXTURE_2D,tex.getInternalFormat(),tex.getWidth(),tex.getHeight(),tex.getFormat(),GL.GL_UNSIGNED_BYTE,tex.getBuffer());
		
		gl.glBindTexture(GL.GL_TEXTURE_2D,0);

		tex.setID(texID[0]);
		
		System.out.println("loaded texture "+tex.getName()+" to id "+texID[0]);
	}
/*	
	public void loadTextureIntoGL(GL gl, String filename) {
		Texture t = retrieveCachedTexture(filename);
		if (t == null) {
			loadTexture(filename);
			t = retrieveCachedTexture(filename);
		}
		int[] texID = new int[1];
		gl.glGenTextures(1, texID);
		t.setID(texID[0]);
		t.bind(gl);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, t.getWidth(), t.getHeight(), 0,
							GL.GL_RGB, GL.GL_UNSIGNED_BYTE, t.getBuffer());
		log.fine("Loaded texture "+filename+" into ID "+texID[0]);
	}
*/
	
	private Texture retrieveCachedTexture(String name) {
		int size = textureCache.size();
		Texture t;
		for (int i=0; i<size; i++) {
			t = (Texture)textureCache.get(i);
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}
}
