package me.yaska.Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import me.yaska.TileMap.TileMap;

public class Player extends MapObject {

	/////////////// FIELDS ////////////////

	// player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;

	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;

	// scratch attack
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;

	// gliding
	private boolean gliding;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {

			// this will give the numbers of frames inside each of those animations actions
			// in order (idle, walking, jumping, falling, gliding, fireball, scratching)

			2, 8, 1, 2, 4, 2, 5

	};

	// Animations actions (using Enums)
	// works similar to the gamestate manager
	// this will determine the indexes of the sprites animation

	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;

	/////////////// CONSTRUCTOR ////////////////

	public Player(TileMap tm) {

		super(tm);

		width = 30; // for reading the sprite sheet
		height = 30;

		cwidth = 20; // real width and height
		cheight = 20;

		moveSpeed = 0.3;
		maxSpeed = 2.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -5.8;
		stopJumpSpeed = 0.3;

		facingRight = true;

		health = maxHealth = 5;
		fire = maxFire = 2500;

		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();

		scratchDamage = 8;
		scratchRange = 40; // 40 pixels

		// load sprites

		try {

			BufferedImage spritesheet = ImageIO
					.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.gif"));

			sprites = new ArrayList<BufferedImage[]>();

			// now we have to extract each of the animation action from it

			for (int i = 0; i < 7; i++) {

				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				// read in the individual sprites
				for (int j = 0; j < numFrames[i]; j++) {

					if (i != 6) {

						// the sprite in the index 6 has a width of 60 pixels, not 30
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);

					}

					else {

						bi[j] = spritesheet.getSubimage(j * 2 * width, i * height, width * 2, height);

					}

				}

				// after reading all the sprites
				// now add the bufferedimage array to the animation list
				sprites.add(bi);

			}

		}

		catch (Exception e) {

			e.printStackTrace();

		}

		animation = new Animation();

		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);

	}

	/////////////// METHODS ////////////////

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getFire() {
		return fire;
	}

	public int getMaxFire() {
		return maxFire;
	}

	public void setFiring() {
		firing = true;
	}

	public void setScratching() {
		scratching = true;
	}

	// we set boolean since we can stop gliding at any moment, but
	// when we perform a fire or scratch we have to carry out that
	// attack out all the way through, so there is no point giving them a boolean
	public void setGliding(boolean b) {
		gliding = b;
	}

	public void checkAttack(ArrayList<Enemy> enemies) {

		// loop through enemies
		for (int i = 0; i < enemies.size(); i++) {

			Enemy e = enemies.get(i);

			// scratch attack

			if (scratching) {

				if (facingRight) {

					if (e.getx() > x && e.getx() < x + scratchRange && e.gety() > y - height / 2
							&& e.gety() < y + height / 2

					) {

						e.hit(scratchDamage);
					}

				}

				else {
					
					if(
						e.getx() < x &&
						e.getx() > x - scratchRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2) {
						
						e.hit(scratchDamage);
						
					}

				}

			}
			
			// fireballs
			for(int j = 0; j < fireBalls.size(); j++) {
				
				if(fireBalls.get(j).intersects(e)) {
					
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
					
				}
				
			}
			
			// check enemy collision
			if(intersects(e)) {
				
				hit(e.getDamage());
				
			}
			
		}

	}
	
	private void hit(int damage) {
		
		if(flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
		
	}

	private void getNextPosition() {

		// this method determines where the next position of the player should be
		// by reading the keyboard input

		// movement
		if (left) {

			dx -= moveSpeed;
			if (dx < -maxSpeed) {

				dx = -maxSpeed;

			}

		}

		else if (right) {

			dx += moveSpeed;
			if (dx > maxSpeed) {

				dx = maxSpeed;

			}

		}

		else {

			if (dx > 0) {

				dx -= stopSpeed;
				if (dx < 0) {

					dx = 0;

				}

			}

			else if (dx < 0) {

				dx += stopSpeed;
				if (dx > 0) {

					dx = 0;

				}

			}

		}

		// cannot move while attacking, except in air
		if ((currentAction == SCRATCHING || currentAction == FIREBALL) && !(jumping || falling)) {

			dx = 0; // cannot move

		}

		// jumping
		if (jumping && !falling) {

			dy = jumpStart;
			falling = true;

		}

		if (falling) {

			if (dy > 0 && gliding)
				dy += fallSpeed * 0.1;
			else
				dy += fallSpeed;

			if (dy > 0)
				jumping = false;
			if (dy < 0 && !jumping)
				dy += stopJumpSpeed; // the longer you hold the jumping button the higher you will jump

			if (dy > maxFallSpeed)
				dy = maxFallSpeed;

		}

	}

	public void update() {

		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check attack has stopped
		if (currentAction == SCRATCHING) {

			if (animation.hasPlayedOnce())
				scratching = false;

		}

		if (currentAction == FIREBALL) {

			if (animation.hasPlayedOnce())
				firing = false;

		}

		// fireball attack

		fire += 1; // keep regenerating the fire energy
		if (fire > maxFire)
			fire = maxFire; // limit the maxFire
		if (firing && currentAction != FIREBALL) {

			if (fire > fireCost) {

				// if have enough energy to perform the fireball attack
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight); // whichever direction the player is facing, that
																	// is the same direction that the fireball is going
																	// to shoot
				fb.setPosition(x, y); // same position as the player
				fireBalls.add(fb);

			}

		}

		// update fireballs
		for (int i = 0; i < fireBalls.size(); i++) {

			fireBalls.get(i).update();
			if (fireBalls.get(i).shouldRemove()) {

				fireBalls.remove(i);
				i--;

			}

		}

		// check done flinching
		if(flinching) {
			
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			
			if(elapsed > 1000) {
				
				flinching = false;
				
			}
			
		}
		
		// set animation
		if (scratching) {

			if (currentAction != SCRATCHING) {

				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;

			}

		}

		else if (firing) {

			if (currentAction != FIREBALL) {

				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;

			}

		}

		else if (dy > 0) {

			// two way to fall: gliding or regular falling

			if (gliding) {

				if (currentAction != GLIDING) {

					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;

				}

			}

			else if (currentAction != FALLING) {

				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;

			}

		}

		else if (dy < 0) {

			if (currentAction != JUMPING) {

				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1); // since there is only one jumping sprite we don't need any animation
				width = 30;

			}

		}

		else if (left || right) {

			if (currentAction != WALKING) {

				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;

			}

		}

		else {

			if (currentAction != IDLE) {

				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;

			}

		}

		animation.update();

		// set direction
		// we don't want the player moving while he is attacking:
		if (currentAction != SCRATCHING && currentAction != FIREBALL) {

			if (right)
				facingRight = true;
			if (left)
				facingRight = false;

		}

	}

	public void draw(Graphics2D g) {

		setMapPosition(); // first thing to be called in any map object

		// draw fireballs
		for (int i = 0; i < fireBalls.size(); i++) {

			fireBalls.get(i).draw(g);

		}

		// draw the player
		// after getting hit the player starts blinking

		if (flinching) {

			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;

			// this will give the appearance of blinking every 100 ms:
			if (elapsed / 100 % 2 == 0) {

				return;

			}

		}

		super.draw(g);

	}

}