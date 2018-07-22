package EigenMeat.EigenMaze;

import junit.framework.*;
import java.nio.*;

public class MazeTest extends TestCase {
	public MazeTest() {
		super();
	}
	
	public MazeTest(String name) {
		super(name);
	}
	
	// assumes generate() works as advertised
	// (i.e. all absolute edges closed, every cell has at least one open wall
	public void testIsWallUp() {
		Maze m = new Maze(10,10);
		m.generate();
		assertTrue(m.isWallUp(0,0,Maze.NORTH));
		assertTrue(m.isWallUp(9,9,Maze.EAST));
		assertFalse(m.isWallUp(4,4,Maze.NORTH) && m.isWallUp(4,4,Maze.WEST)
						 && m.isWallUp(4,4,Maze.SOUTH) && m.isWallUp(4,4,Maze.EAST));
	}

	public void testNetDataStuff() {
		int testWidth = 4;
		int testHeight = 4;
		Maze m = new Maze(testWidth,testHeight);
		m.generate();

		
		
		Maze awesome = new Maze(4,4);
		ByteBuffer test = m.getData();
		test.flip();
		awesome.receiveData(test);
		for(int i=0; i < testHeight; i++) {
			for(int j=0; j < testWidth; j++) {
			assertEquals(m.isWallUp(i,j,Maze.NORTH),awesome.isWallUp(i,j,Maze.NORTH));
			assertEquals(m.isWallUp(i,j,Maze.EAST),awesome.isWallUp(i,j,Maze.EAST));
			assertEquals(m.isWallUp(i,j,Maze.SOUTH),awesome.isWallUp(i,j,Maze.SOUTH));
			assertEquals(m.isWallUp(i,j,Maze.WEST),awesome.isWallUp(i,j,Maze.WEST));
			
			}
		}
		
		
	}
}
