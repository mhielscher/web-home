package EigenMeat.EigenMaze;

import java.nio.*;
import net.java.games.jogl.*;

/**
 * Manages a single texture.
 */
public class Texture {
	private int textureID;
	private int height;
	private int width;
	private String name;
	private boolean bound;

	private ByteBuffer data;
	private int internalFormat;
	private int format;

	/**
	 * Default constructor.
	 */
	public Texture() {
		name = null;
		textureID = -1;
		height = 0;
		width = 0;
	}

	/**
	 * Constructor.
	 * @param textureid texture id of texture
	 * @param n string name
	 * @param w texture width
	 * @param h texture height
	 */
	public Texture(int textureid, String n, int w, int h) {
		textureID = textureid;
		name = n;
		height = h;
		width = w;
	}

	/**
	 * Constructor.
	 * @param n string name
	 * @param data texture data
	 * @param internalformat internal format of tecture
	 * @param format texture format
	 * @param w width
	 * @param h height
	 */
	public Texture(String n, ByteBuffer data, int internalformat, int format, int w, int h) {
		name = n;
		this.data = data;
		this.internalFormat = internalformat;
		this.format = format;
		width = w;
		height = h;
		textureID = -1;
	}

	/**
	 * Constructor.
	 * @param data texture data
	 * @param internalformat internal format
	 * @param format textue format
	 * @param w width
	 * @param h height
	 */
	public Texture(ByteBuffer data, int internalformat, int format, int w, int h) {
		setID(-1);
		width = w;
		height = h;
		this.format = format;
		this.internalFormat = internalformat;
		this.data = data;
	}

	/**
	 * Set texture data.
	 * @param data texture data
	 * @param internalformat internal format
	 * @param format texture format
	 * @param w width
	 * @param h height
	 */
	public void set(ByteBuffer data, int internalformat, int format, int w, int h) {
		setID(-1);
                width = w;
                height = h;
                this.format = format;
                this.internalFormat = internalformat;
                this.data = data;
        }

	/**
	 * Load the texture into OpenGL
	 * @param gl JOGL GLDrawable
	 */
	public void loadIntoGL(GLDrawable gl) {
		TextureManager.getInstance().loadTextureIntoGL(gl,this);
		//data = null;
	}
	
	/**
	 * Bind the texture for use.
	 * @param gl JOGL GLDrawable
	 */
	public void bind(GLDrawable gl) {
		if (textureID == -1) {
			TextureManager.getInstance().loadTextureIntoGL(gl,this);
		}	
		gl.getGL().glBindTexture(GL.GL_TEXTURE_2D, textureID);
	}

	/**
	 * Unbind the texture.
	 * @param gl JOGL GLDrawable
	 */
	public void unbind(GLDrawable gl) {
		gl.getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
	}

	/**
	 * Get texture name.
	 * @return texture name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get texture data.
	 * @return reference to texture's ByteBuffer
	 */
	public ByteBuffer getBuffer() {
		return data;
	}

	/**
	 * Get height of texture.
	 * @return height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Get width of texture.
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get texture ID.
	 * @return texture id
	 */
	public int getID() {
		return textureID;
	}
	
	/**
	 * Set texture ID.
	 * @param id texture id
	 */
	public void setID(int id) {
		textureID = id;
	}

	/**
	 * Get internal format of texture.
	 * @return internal format
	 */
	public int getInternalFormat() {
		return internalFormat;
	}

	/**
	 * Get format of texture.
	 * @return texture format
	 */
	public int getFormat() {
		return format;
	}
}
