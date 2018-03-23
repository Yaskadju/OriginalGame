package me.yaska.Entity;

import java.awt.image.BufferedImage;

public class Animation {
	
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delay; // how long between the frames
	
	private boolean playedOnce; // tell us weather or not the animation was played already, if it has looped
	                            // this is useful for attack animation where the animation of the attack needs
	                            // to happen once
	
	public Animation() {
		
		playedOnce = false;
		
	}
	
	public void setFrames(BufferedImage[] frames) {
		
		this.frames = frames;
		currentFrame = 0; // reset the current frame
		startTime = System.nanoTime();
		playedOnce = false;
		
	}
	
	public void setDelay(long d) { delay = d; }
	public void setFrame(int i) { currentFrame = i; } // in case we want mannually to set the frame number
	
	public void update() {
		
		// handles the logic of weather or not to move to the next frame
		
		if(delay == 1) return; // there is no animation, so just return
		
		// how it has been since the last frame came up
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		
		if(elapsed > delay) {
			
			// if elapsed is greater than the delay, move one to the next frame
			currentFrame++;
			startTime = System.nanoTime();
			
		}
		
		// also make sure that you don't pass the limit of frames in the array
		if(currentFrame == frames.length) {
			
			currentFrame = 0; // loop that to zero
			playedOnce = true; // that means that this particular animation was played once
			
		}
		
	}
	
	public int getFrame() { return currentFrame; }
	public BufferedImage getImage() { return frames[currentFrame]; }
	public boolean hasPlayedOnce() { return playedOnce; }
	
}
