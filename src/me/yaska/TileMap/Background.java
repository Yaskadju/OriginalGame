package me.yaska.TileMap;

import java.awt.image.*;
import javax.imageio.ImageIO;

import me.yaska.Main.GamePanel;

import java.awt.*;

public class Background {

	private BufferedImage image;
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	// scale in which the background moves
	private double moveScale;
	
	public Background(String s, double ms) {
		
		try {
			
			// pass a resource folder instead of a file
			image = ImageIO.read(getClass().getResourceAsStream(s));
			
			moveScale = ms;
			
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void setPosition(double x, double y) {
		
		// we don't want this thing keep going the way off of the screen
		// if it goes passed the screen, we want to reset it
		this.x = (x * moveScale) % GamePanel.WIDTH;
		this.y = (y * moveScale) % GamePanel.HEIGHT;
		
	}
	
	// makes the background automatically scroll
	public void setVector(double dx, double dy) {
		
		this.dx = dx;
		this.dy = dy;
		
	}
	
	// in case we are automatically scrolling
	public void update() {
		
		x += dx;
		y += dy;
		
	}
	
	public void draw(Graphics2D g) {
		
		g.drawImage(image, (int) x, (int) y, null);
		
		// keeps drawing the background as we move
		if(x < 0) {
			
			g.drawImage(image, (int) x + GamePanel.WIDTH, (int) y, null); 
			
		}
		
		if(x > 0) {
			
			g.drawImage(image, (int) x - GamePanel.WIDTH, (int) y, null); 
			
		}
		
	}
	
}
