package me.yaska.GameState;

import me.yaska.*;
import me.yaska.Main.GamePanel;

import java.util.ArrayList;
import java.awt.*;

public class GameStateManager {

	private ArrayList <GameState> gameStates;
	private int currentState;
	
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	
	public GameStateManager() {
		
		gameStates = new ArrayList <GameState>();
		
		currentState = MENUSTATE;
		gameStates.add(new MenuState(this));
		gameStates.add(new Level1State(this));
		
	}
	
	public void setState(int state) {
		
		currentState = state;
		gameStates.get(currentState).init();
		
	}
	
	public void update() {
		
		gameStates.get(currentState).update();
		
	}
	
	public void draw(Graphics2D g) {
		
		gameStates.get(currentState).draw(g);
		
	}
	
} 