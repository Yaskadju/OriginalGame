package me.yaska.TileMap;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;

import me.yaska.Main.GamePanel;

import java.io.*;

public class TileMap {
	
	// position
	private double x;
	private double y;
	
	// bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;
	
	// smoothly scroll the camera towards the player
	private double tween;
	
	// map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	
	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles; // this 2d array represents the tileset after importing it
	
	// drawing - instead of drawing all the tiles, draw only the
	// ones on the screen
	private int rowOffset; // which row to start drawing 
	private int colOffset; // which column 
	private int numRowsToDraw; // how many rows to draw
	private int numColsToDraw;
	
	public TileMap(int tileSize) {
		
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		tween = 0.07;
		
	}
	
	// loads the tileset into memory
	public void loadTiles(String s) {
		
		try {
			
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
		
			numTilesAcross = tileset.getWidth() / tileSize;
			
			// representation of the tiles set
			tiles = new Tile[2][numTilesAcross];
			
			BufferedImage subimage;
			
			// import the entire tile set
			for(int col = 0; col < numTilesAcross; col++) {
				
				subimage = tileset.getSubimage(col* tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				
				// everything in the first row is a normal tile, everything in the second is a block tile
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
				
			}
			
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
		
	}
	
	// load the map file into the memory
	public void loadMap(String s) {
		
		// first line is the number of columns
		// second line is the number of rows
		
		try {
			
			InputStream in = getClass().getResourceAsStream(s);
			// using a bufferedReader to load and read in the text file 
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			
			// delimeters
			String delims = "\\s+";
			for(int row = 0; row < numRows; row++) {
				
				// read each line and split them into tokens using our delimiters
				String line = br.readLine();
				String[] tokens = line.split(delims);
				
				// go through the array and put them into the map
				for(int col = 0; col < numCols; col++) {
					
					map[row][col] = Integer.parseInt(tokens[col]);
					
				}
				
			}
			
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public int getTileSize() { return tileSize; }
	public double getx() { return x; }
	public double gety() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getNumRows() { return numRows; }
	public int getNumCols() { return numCols; }
	
	public void setTween(double d) { tween = d; }
	
	public int getType(int row, int col) {
	
		// rc = row column
		int rc = map[row][col];
		
		// find out which tile that is looking at your tileset
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		
		return tiles[r][c].getType();
		
	}
	
	public void setPosition(double x, double y) {
		
		// normally, this two lines below already makes tha camera to
		// follow the player
		//this.x = x;
		//this.y = y;
		
		// camera follows the player smoothly
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		
		// makes sure that the bounds are not being passed
		fixBounds();
		
		// where to start drawing
		colOffset = (int) -this.x / tileSize;
		rowOffset = (int) -this.y / tileSize;
		
	}
	
	// makes sure the bounds are not being passed
	private void fixBounds() {
		
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
		
	}
	
	public void draw(Graphics2D g) {
		
		for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			
			if(row >= numRows) break;
			
			for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
				
				if(col >= numCols) break;
				if(map[row][col] == 0) continue;
				
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				
				g.drawImage(
					tiles[r][c].getImage(),
					(int)x + col * tileSize,
					(int)y + row * tileSize,
					null
				);
				
			}
			
		}
		
	}
	
}