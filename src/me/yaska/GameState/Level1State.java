package me.yaska.GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import me.yaska.TileMap.*;
import java.awt.*;

import me.yaska.*;
import me.yaska.Entity.Enemy;
import me.yaska.Entity.Explosion;
import me.yaska.Entity.HUD;
import me.yaska.Entity.Player;
import me.yaska.Entity.Enemies.Slugger;
import me.yaska.Handlers.Keys;
import me.yaska.Main.GamePanel;

public class Level1State extends GameState {
	
	///////////// FIELDS ///////////////
	
	private TileMap tileMap;
	private Background bg;
	private Player player;
	private ArrayList <Enemy> enemies;
	private ArrayList<Explosion> explosions;
	private HUD hud;
	
	//////////// CONSTRUCTOR ///////////////
	
	public Level1State(GameStateManager gsm) {
		
		super(gsm);
		
		init();
		
	}

	///////////// METHODS ////////////////
	
	@Override
	public void init() {
	
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		
		// starting point
		tileMap.setPosition(140, 0);
		tileMap.setTween(0.07);
		
		bg = new Background("/Backgrounds/grassbg1.gif", 0.1);
		
		player = new Player(tileMap);
		player.setPosition(100, 100);
		
		populateEnemies();
		
		/* creates one enemy
		 * enemies = new ArrayList<Enemy>();
		Slugger s;
		s = new Slugger(tileMap);
		s.setPosition(100, 100);
		enemies.add(s);
		*/
		
		explosions = new ArrayList<Explosion>();
		
		hud = new HUD(player);
		
	}

	public void populateEnemies() {
		
		enemies = new ArrayList<Enemy>();
		
		Slugger s;
		Point[] points = new Point[] {
			
			new Point(200, 100),
			new Point(860, 200),
			new Point(1525, 200),
			new Point(1680, 200),
			new Point(1800, 200)
			
		};
		
		for(int i = 0; i < points.length; i++) {
			
			// the following will fill up the enemies with a bunch of these
			// 4 sluggers at the points positions declared above
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
			
		}
		
		//s = new Slugger(tileMap);
		//s.setPosition(860, 200);
		
	}
	
	@Override
	public void update() {
		
		// update keys
		handleInput();
		
		// set background
		bg.setPosition(tileMap.getx(), tileMap.gety());
		
		// update player
		player.update();
		
		// update tilemap
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
		
		// attack enemies
		player.checkAttack(enemies);
		
		// update all enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			e.update();
			if(e.isDead()) {
				
				enemies.remove(i);
				i--;
				// remove the enemy and add a new explosion when he dies
				explosions.add(new Explosion(e.getx(), e.gety()));
				
			}
			
		}
		
		// update explosions
		for(int i = 0; i < explosions.size(); i++) {
			
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove()) {
				
				explosions.remove(i);
				i--;
				
			}
			
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		
		/*
		// clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		*/
		
		// draw bg
		bg.draw(g);
		
		// draw tilemap
		tileMap.draw(g);
		
		// draw player
		player.draw(g);
		
		// draw enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			enemies.get(i).draw(g);
			
		}
		
		// draw explosions
		for(int i = 0; i < explosions.size(); i++) {
			
			explosions.get(i).setMapPosition((int) tileMap.getx(), (int) tileMap.gety());
			explosions.get(i).draw(g);
			
		}
		
		// draw hud
		
		hud.draw(g);
		
	}

	@Override
	public void handleInput() {
		
		player.setUp(Keys.keyState[Keys.UP]);
		player.setDown(Keys.keyState[Keys.DOWN]);
		player.setLeft(Keys.keyState[Keys.LEFT]);
		player.setRight(Keys.keyState[Keys.RIGHT]);
		player.setJumping(Keys.keyState[Keys.BUTTON1]);
		player.setGliding(Keys.keyState[Keys.BUTTON2]);
		if(Keys.isPressed(Keys.BUTTON3)) player.setScratching();
		if(Keys.isPressed(Keys.BUTTON4)) player.setFiring();
		
	}
	
}
