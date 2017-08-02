package EigenMeat.EigenMaze;
import java.nio.*;
/**
 * Interface for the network code. Classes the properly implement this can be used to send information across the game to the server and other clients.
 * One thing to note.. you MUST add to the build method in NetHandler to use a implementing class. That method is a simple switch, using the getType method.
 * Hence, ensureing each class has its own type, and that the NetHandler code is modified. Sucks, I know, but im not smart enough to make it better at the moment.
 */

public interface NetEntity {
	/**
	 * Returns the type of this entity. 
	 * Care must be taken to not give to seperate implementing classes
	 * the same type. The network code will not be happy.
	 */
	public short getType();

	/**
	 * Returns the ID of the owning client instance of this NetEntity.
	 */
	public short getOwnerID();
	
	/**
	 * Sets the ID of the owning client instance of this NetEntity.
	 */
	public void setOwnerID(short id);
	
	/**
	 * Returns the number of bytes written to the ByteBuffer.
	 * Very farkin important. Don't mess this up. I'll kill you.
	 */
	public short getDataSize();
	
	/**
	 * Gets the data to reconstruct this object.
	 * This uses as ByteBuffer, thus understanding that class is of particular importance.
	 * @see java.nio.ByteBuffer
	 */

	public ByteBuffer getData();
	/**
	 * Method used to reconstruct this object from the network.
	 * This method is called in NetHandler, and is used to update this object. Again, uses a ByteBuffer to encapsulate the data.
	 * @see java.nio.ByteBuffer
	 * @see NetHandler
	 */
	public void receiveData(ByteBuffer bb);
}
