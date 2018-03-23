package me.yaska.Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import me.yaska.Main.GamePanel;
import me.yaska.TileMap.Tile;
import me.yaska.TileMap.TileMap;

public abstract class MapObject {
	
	// since it is an abstracted superclass, everything
	// has to be protected so that the subclasses can actually
	// see them
	
	///////////////// FIELDS ////////////////////////
	
	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	// dimensions
	// mainly for reading in the sprite sheets
	protected int width;
	protected int height;
	
	// collision box
	// "real width and heights" (used to determine collisions)
	protected int cwidth;
	protected int cheight;
	
	// collisions
	protected int currRow;
	protected int currCol;
	protected double xdest; // x destination
	protected double ydest; // y destination
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	
	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	
	// movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed; // if you hold the jumping buttom for longer then you go higher
	
	////////////////// CONSTRUCTOR ///////////////////////////
	
	public MapObject(TileMap tm) {
		
		tileMap = tm;
		tileSize = tm.getTileSize();
		
	}
	
	///////////////// METHODS ////////////////////////////////
	
	
	public boolean intersects(MapObject o) {
		
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		
		return r1.intersects(r2);
		
	}
	
	public Rectangle getRectangle() {
		
		return new Rectangle((int) x - cwidth, (int) y - cheight, cwidth, cheight);
		
	}
	
	public void calculateCorners(double x, double y) {
		
		int leftTile = (int) (x - cwidth / 2) / tileSize; 
		int rightTile = (int) (x + cwidth / 2 - 1) / tileSize; // -1 so that we don't step over into the next collumn
		int topTile = (int) (y - cheight / 2) / tileSize;
		int bottomTile = (int) (y + cheight / 2 - 1) / tileSize; // -1 so that we don't go downwards into the next tile
		
		if(topTile < 0 || bottomTile >= tileMap.getNumRows() || leftTile < 0 || rightTile >= tileMap.getNumCols()) {
			
			topLeft = topRight = bottomLeft = bottomRight = false;
			return;
		}
		
		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		
		topLeft = tl == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomLeft = br == Tile.BLOCKED;
		
		
	}
	
	// this checks weather or not we have ran into a blocked or normal tile
	public void checkTileMapCollision() {
		
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
		
		calculateCorners(x, ydest);
		if(dy < 0) {
			if(topLeft || topRight) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		if(dy > 0) {
			if(bottomLeft || bottomRight) {
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		
		calculateCorners(xdest, y);
		if(dx < 0) {
			if(topLeft || bottomLeft) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}
		if(dx > 0) {
			if(topRight || bottomRight) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}
		
		if(!falling) {
			calculateCorners(x, ydest + 1);
			if(!bottomLeft && !bottomRight) {
				falling = true;
			}
			
		}
		
		
	}
	
	public int getx() { return (int) x; }
	public int gety() { return (int) y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getCWidth() { return cwidth; }
	public int getCHeight() { return cheight; }
	public boolean isFacingRight() { return facingRight; }
	
	public void setPosition(double x, double y) {
		
		// regular positions (global):
		this.x = x;
		this.y = y;
		
	}
	
	public void setVector(double dx, double dy) {
		
		this.dx = dx;
		this.dy = dy;
		
	}
	
	// every map object has two different positions:
	// one is the global position which is the regular x and y
	// the other is their local position which is the x and y position
	// plus the tilemap position  
	
	// the map position tell us actually where to draw the character 
	// for example, if our player were at position 1000 he would be out of the screen
	// so we have to find out how far tilemap has moved in order to offset the player 
	// back onto the screen, and that is their final position:
	// x + xmap and y + ymap
	
	public void setMapPosition() {
		
		// map positions (local):
		xmap = tileMap.getx();
		ymap = tileMap.gety();
		
	}
	
	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }
	public void setJumping(boolean b) { jumping = b; }
	
	// we don't want to draw map objects that are not even on the screen
	// so this function returns a boolean to know if the object is or not on the screen
	// so we know if we can draw them at all
	
	public boolean notOnScreen() {
		
		// x+ xmap is the final position of the player on the game screen itself
		return x + xmap + width <  0 || x + xmap - width > GamePanel.WIDTH || y + ymap + height < 0 || 
			   y + ymap - height > GamePanel.HEIGHT; 
		
		// if true, the object is not on the screen
			   
	}
	
	public void draw(Graphics2D g) {
		
		if (facingRight) {

			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), null);

		}

		else {

			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2 + width), (int) (y + ymap - height / 2),
					-width, height, null); // - width flips;

		}
		
	}
	
}
