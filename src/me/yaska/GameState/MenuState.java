package me.yaska.GameState;

import me.yaska.Handlers.Keys;
import me.yaska.TileMap.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import me.yaska.TileMap.Background;

public class MenuState extends GameState {

	private Background bg;
	
	private int currentChoice = 0;
	
	private String[] options = { 
			
			"Start", "Help", "Quit"
			
	};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;
	
	public MenuState(GameStateManager gsm) {
		
		super(gsm);
		try {
			
			bg = new Background("/Backgrounds/menubg.gif", 1);
			bg.setVector(-0.5, 0);
			
			titleColor = new Color(128, 0, 0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			
			font = new Font("Arial", Font.PLAIN, 12);
			
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void init() {
	}

	@Override
	public void update() {
		
		bg.update();
		handleInput();
		
	}

	@Override
	public void draw(Graphics2D g) {
		
		// Draw bg
		bg.draw(g);
		
		// Draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Dragon Tale", 80, 70);
		
		// Draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) {
			
			if(i == currentChoice) {
				
				g.setColor(Color.RED);
				
			}
			
			else {
				
				g.setColor(Color.BLACK);
				
			}
			
			g.drawString(options[i], 145, 140 +  i * 15);
			
		}
		
	}

	private void select() {
		
		if(currentChoice == 0) {
			
			//start
			gsm.setState(GameStateManager.LEVEL1STATE);
			
		}
		
		else if(currentChoice == 1) {
			
			//help
			
		
		}
		
		else if(currentChoice == 2) {
			
			System.exit(0);
			
		}
		
	}
	
	public void handleInput() {
		
		if(Keys.isPressed(Keys.ENTER)) select();
		
		if(Keys.isPressed(Keys.UP)) {
			
			if(currentChoice > 0) {
				
				currentChoice--;
				
			}
			
		}
		
		if(Keys.isPressed(Keys.DOWN)) {
			
			if(currentChoice < options.length - 1) {
				
				currentChoice++;
				
			}
			
		}
		
	}
	
}
