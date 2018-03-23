package me.yaska.Main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import me.yaska.GameState.GameStateManager;
import me.yaska.Handlers.Keys;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener{

	// Dimensions
	public static final int  WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	
	// Game Thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	// Image 
	private BufferedImage image;
	private Graphics2D g;
	
	// Game State Manager
	private GameStateManager gsm;
	
	public GamePanel() {
		
		super();
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		this.setFocusable(true);
		requestFocus();
	
	}

	public void addNotify() {
		
		super.addNotify();
		if(thread == null) {
			
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
			
		}
		
	}
	
	private void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gsm = new GameStateManager();
		
	}
	
	
	@Override
	public void run() {
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		// Game Loop
		
		while(running) {
			
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			elapsed = (System.nanoTime() - start)/1000000;
			
			wait = targetTime - elapsed;
			
			if(wait < 0) wait = 5;
			
			try {
				
				Thread.sleep(wait);
				
			}
			
			catch(Exception e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	private void update() {
		
		gsm.update();
		Keys.update();
		
	}
	
	private void draw() {
		
		gsm.draw(g);
		
	}
	
	private void drawToScreen() {
		
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		//g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	
		Keys.keySet(e.getKeyCode(), true);
	
	}

	@Override
	public void keyReleased(KeyEvent e) {
	
		//gsm.keyReleased(e.getKeyCode());
		
		Keys.keySet(e.getKeyCode(),false);
		
	}

	
}
