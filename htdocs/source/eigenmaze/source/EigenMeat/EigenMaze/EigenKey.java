package EigenMeat.EigenMaze;

import java.nio.*;
import java.util.*;

/**
 * In the process of making this a subclass of Server.
 * Hence dont touch.. or look at.. STOP! BAD! BACK I SAY, BACK!
 * ... Did you mean inner class?
 */

public class EigenKey {
	public short id;
	public ByteBuffer bb;
	public LinkedList writeQue = new LinkedList();

	private ByteBuffer resetPointer;
	
	public EigenKey(int size, short id) {
		bb = ByteBuffer.allocate(size);
		this.id = id;
	}
}
