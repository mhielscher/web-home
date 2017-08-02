package EigenMeat.EigenMaze;

import java.util.Stack;
import java.nio.*;

/**
 * Creates a random maze.
 */
public class Maze implements NetEntity {
	private int width, height;
	private Cell cells[][];
	private Cell nullCell = new NullCell();

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
  	public static final int WEST = 3;

	//Net code
	private final short BYTE_LENGTH = (short)4096;
	private ByteBuffer netBuffer = ByteBuffer.allocate(BYTE_LENGTH);
	private short netID = (short)-1;
			

	/**
	 * Maze Default Constructor 
	 */
	public Maze() {
		setSize(10,10);
	}

	/**
	 * Maze Constructor. Allows you to set the size.
	 */
	public Maze(int w, int h) {
		setSize(w,h);
	}

	/**
	 * Set the maze dimensions. 
	 * @param w maze width
	 * @param h maze height
	 */
	public void setSize(int w, int h) {
		width = w;
		height = h;

		initializeCells();
	}
	
	/**
	 * Gets the width of the maze, in cells.
	 * @return maze width
	 */
	public int getWidthCells() {
		return width;
	}

	/**
	 * Gets the height of the maze, in cells.
	 * @return maze height
	 */
	public int getHeightCells() {
		return height;
	}

	/**
	 * Checks to see if a specific wall is currently up.
	 * @param x the x location of the cell you want to check
	 * @param y the y location of the cell you want to check
	 * @param dir which wall you want to check in the specified wall. 
	 * @return true if the wall is up, false if the wall is down
	 */
	public boolean isWallUp(int x, int y, int dir) {
		if(dir == NORTH)
			return cells[y][x].north;
		if(dir == SOUTH)
			return cells[y][x].south;
		if(dir == EAST)
			return cells[y][x].east;
		if(dir == WEST)
			return cells[y][x].west;

		return false;
	}

	/**
	 * Generates a new maze.
	 */
	public void generate() {
		int cellCounter = width*height;
		int direction;
		int histCell = 0;
		Cell currentCell;
	
		initializeCells();
		
		Point location = new Point();
		Stack pointStack = new Stack();
	
		location.x = (int)(Math.random()%width);
		location.y = (int)(Math.random()%height);
		
		pointStack.push(new Point(location));
		
		while(cellCounter > 1) {
			currentCell = cells[location.y][location.x];
		
			direction = currentCell.getRandomDirection();
			if(direction != -1) {
				currentCell.breakDownWall(direction);
				
				switch(direction) {
					case NORTH:
						location.y--;
						break;
					case EAST:
						location.x++;
						break;
					case WEST:
						location.x--;
						break;
					case SOUTH:
						location.y++;
						break;
				}
		
				pointStack.push(new Point(location));
				cellCounter--;
			} else {
				pointStack.pop();
				location = (Point) pointStack.peek();
			}
		}
	}

	/**
	 * Returns maze data as a 2d byte array. Cells are serialized by a
	 * byte, using individuals bits as a flag. 
	 * 0001 = north wall
	 * 0010 = east wall
	 * 0100 = south wall
	 * 1000 = west wall
	 */
	public byte[][] getMazeData(){
		byte returnByte[][] = new byte[height][width];
		
		for(int i = 0; i < height; i++) {
			for(int j = 0;j < width; j++) {
				returnByte[i][j] = getCell(cells[i][j],returnByte[i][j]);
			}
		}

		return returnByte;
	}
		

	/**
	 * Builds a maze from a 2d byte buffer.
	 */
	public void generate(byte cellArray[][]) {
		initializeCells();	
		for(int i = 0; i < height; i++) {
			for(int j = 0;j < width; j++) {
				setCell(cells[i][j],cellArray[i][j]);
			}
		}
	}

	private void setCell(Cell currentCell, byte data) {
		if((data & 1) > 0) {
			currentCell.breakDownWall(NORTH);
		} 
		if((data & 2) > 0) {
			currentCell.breakDownWall(EAST);
		}
		if((data & 4) > 0) {
			currentCell.breakDownWall(SOUTH);
		}
		if((data & 8) > 0) {
			currentCell.breakDownWall(WEST);
		}
	}
	private byte getCell(Cell currentCell, byte data) {
		if(!currentCell.north)
			data += 1;
		if(!currentCell.east)
			data += 2;
		if(!currentCell.south)
			data += 4;
		if(!currentCell.west)
			data += 8;
		return data;
	}

	/** 
	 * Initializes the cells before creation.
	 */
	private void initializeCells() {
		cells = new Cell[height][width];

		//initialize cells
		for(int i=0; i < height; i++)
			for(int j=0; j < width; j++)
				cells[i][j] = new Cell();
		
		//create linked pointers
		for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				if(j == 0)
					cells[i][j].westCell = nullCell;
				else
					cells[i][j].westCell = cells[i][j-1];

				if(j == (width-1))
					cells[i][j].eastCell = nullCell;
				else
					cells[i][j].eastCell = cells[i][j+1];

				if(i == 0)
					cells[i][j].northCell = nullCell;
				else
					cells[i][j].northCell = cells[i-1][j];

				if(i == (height-1)) 
					cells[i][j].southCell = nullCell;
				else
					cells[i][j].southCell = cells[i+1][j];
			}
		}
	}
	/** Deletes a random walls.
	 * The number of walls is determined by a float percent between 0 and 1
	 */
	public void deleteRandomWalls(float percent) {
		for(int i=0; i < height; i++)
			for(int j=0; j < width; j++) {
				if(j != (width-1)) {
					if(Math.random() < percent)
						cells[i][j].breakDownWall(EAST);				
				}

				if(i != (height-1)) {
					if(Math.random() < percent)
						cells[i][j].breakDownWall(SOUTH);
				}
			}
	}
	
	/** 
	 * Debug print of the maze. Shows which cells have been visited
	 * during maze creation
	 */
	public void print() {
		for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				if(cells[i][j].canMoveInto())
					System.out.print("X");
				else
					System.out.print(".");
			}
			System.out.println("");
		}
	}

	/**
	 * @see NetEntity
	 */
	public short getType() {
		return 25;
	}
	/**
	 * @see NetEntity
	 */
	public short getOwnerID() {
		return netID;
	}

	/**
	 * @see NetEntity
	 */
	public void setOwnerID(short id) {
		netID = id;
	}
	
	/**
	 * @see NetEntity
	 */
	public short getDataSize() {
		return (short)(height*width);
	}
	
	/**
	 * @see NetEntity
	 */
	public ByteBuffer getData() {
		byte data[][] = getMazeData();
		netBuffer.clear();
		for(int i=0; i < height; i++)
			for(int j=0; j < width; j++) {
				netBuffer.put(data[i][j]);
				System.out.println(data[i][j]);
			}
		return netBuffer;
	}
	
	/**
	 * @see NetEntity
	 */
	public void receiveData(ByteBuffer bb) {
		byte[][] data = new byte[height][width];
		
		 for(int i=0; i < height; i++) 
			 for(int j=0; j < width; j++) {
				data[i][j] = bb.get();
				System.out.println(data[i][j]);
			 }
		 generate(data);
		 if(NetHandler.this_ID == 0)
			 EigenEngine.instance().netUpdate(this);
	}
							
		
	private class Cell {
		public boolean north, east, south, west;
		public Cell northCell, eastCell, southCell, westCell;
		
		public Cell() {
			north = east = south = west = true;
		}
	
		public Cell(Cell n, Cell e, Cell s, Cell w) {
			northCell = n;
			eastCell = e;
			southCell = s;
			westCell = w;

			north = east = south = west = true;
		}

		public boolean canMoveInto() {
			return (north && south && east && west);
		}

		public int getRandomDirection() {
			boolean possible[] = new boolean[4];
			int check = -1;
				
			possible[NORTH] = northCell.canMoveInto();
			possible[EAST] = eastCell.canMoveInto();
			possible[SOUTH] = southCell.canMoveInto();
			possible[WEST] = westCell.canMoveInto();

			if(!possible[NORTH] && !possible[EAST] && !possible[SOUTH] && !possible[WEST]) {
			
			return -1;
			}
			
			while(check == -1) {
				check = (int)(Math.random()*4);
				if(possible[check]) {
					return check;
				}
				check = -1;
			}

			return -1;
		}
	
		public void breakDownWall(int direction) {
			switch(direction) {
				case NORTH:
					north = false;
					northCell.south = false;
					break;
				case SOUTH:
					south  = false;
					southCell.north = false;
					break;
				case EAST:
					east = false;
					eastCell.west = false;
					break;
				case WEST:
					west = false;
					westCell.east = false;
					break;
			}
		}
	}

	private class NullCell extends Cell {
		public NullCell() {
			super();
		}

		public boolean canMoveInto() {
			return false;
		}
	}

	private class Point {
		public int x,y;

		public Point() {
			x = y = 0;
		}
		public Point(Point aLoc) {
			x = aLoc.x;
			y = aLoc.y;
		}

		public String toString() {
			return "["+x+","+y+"]";
		}
	}		
}
