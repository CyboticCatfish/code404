package net.cybotic.catfish.src.game.object;

import net.cybotic.catfish.src.SoundBank;
import net.cybotic.catfish.src.game.Game;
import net.cybotic.catfish.src.script.ScriptEnv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public abstract class GameObject {
	
	private int x, y, z = 0, dir, listenerLevel = 0, targetWait, waitTime = 0;
	private float renderingX, renderingY;
	private boolean moving = false, scriptRunning = false, scriptable = false, waiting = false;
	protected boolean collidable = false;
	private ScriptEnv scriptEnv;
	private String script, name;
	Game game;
	private boolean dead = false;
	private boolean errored = false;
	
	public GameObject(int x, int y, int z, int dir, String script, boolean scriptable, Game game, String name, boolean collidable, int listenerLevel) {
		
		this.scriptEnv = new ScriptEnv(this);
		this.x = x;
		this.y = y;
		this.setZ(z);
		this.renderingX = x * 32;
		this.renderingY = y * 32;
		this.dir = dir;
		this.script = script;
		this.game = game;
		this.scriptable = scriptable;
		this.name = name;
		this.collidable =collidable;
		this.listenerLevel = listenerLevel;
		
	}
	
	public abstract void update(GameContainer gc, int delta) throws SlickException;
	public abstract void render(GameContainer gc, Graphics g);
	
	public void preUpdate(GameContainer gc, int delta) throws SlickException {
		
		if (!this.isDead()) {
		
			if (this.moving) {
				
				if (renderingY < y * 32 && dir == 2) renderingY += 0.07f * delta;
				else if (renderingX > x * 32 && dir == 3) renderingX -= 0.07f * delta;
				else if (renderingY >  y * 32 && dir == 0) renderingY -= 0.07f * delta;
				else if (renderingX <  x * 32 && dir == 1) renderingX += 0.07f * delta;
				else {
					
					this.renderingX = x * 32;
					this.renderingY = y * 32;
					
					moving = false;
					
				}
				
			} else if (this.waiting) {
				
				waitTime += delta;
				if (waitTime > targetWait) {
					
					waiting = false;
					
				}
				
			}
			
			this.update(gc, delta);
		
		}
		
	}
	
	public void runScript() {
		
		if (!scriptRunning) {
			
			scriptEnv.launchScript();
			scriptRunning = true;
			
		}
		
	}
	
	public void setScript(String script) {
		
		this.script = script;
		
	}
	
	public abstract int getObjectTypeID();

	public String getScript() {
		
		return this.script;
		
	}
	
	public float getRenderingX() {
		
		return (int) Math.ceil(renderingX);
		
	}
	
	public float getRenderingY() {
		
		return (int) Math.ceil(renderingY);
		
	}
	
	public boolean isMoving() {
		
		return this.moving;
		
	}
	
	public void moveForward() {

		if (!moving) {
			
			moving = true;
					
			if (dir == 0) {
				
				if (y > 0 && !collidableObjectToFront()) y -= 1;
				else moving = false;
				
			} else if (dir == 1) {
				
				if (x < game.getWidth() - 1 && !collidableObjectToFront()) x += 1;
				else moving = false;
				
			} else if (dir == 2) {
				
				if (y < game.getHeight() - 1 && !collidableObjectToFront()) y += 1;
				else moving = false;
				
			} else if (dir == 3) {
				
				if (x > 0 && !collidableObjectToFront()) x -= 1;
				else moving = false;
				
			} else moving = false;
			
		}
	}
	
	private boolean collidableObjectToFront() {
		
		for (GameObject object : game.getGameObjects()) {
			
			if (dir == 0 && object.getX() == this.getX() && object.collidable && object.getY() == this.getY() - 1 && !object.dead) return true;
			else if (dir == 1 && object.getX() == this.getX() + 1 && object.collidable && object.getY() == this.getY() && !object.dead) return true;
			else if (dir == 2 && object.getX() == this.getX() && object.collidable && object.getY() == this.getY() + 1 && !object.dead) return true;
			else if (dir == 3 && object.getX() == this.getX() - 1 && object.collidable && object.getY() == this.getY() && !object.dead) return true;
			
		}
		 
		return false;
		
	}

	public void turnClockwise() {
		
		dir += 1;
		if (dir > 3) dir = 0;
		
	}
	
	public void turnAntiClockwise() {
		
		dir -= 1;
		if (dir < 0) dir = 3;
		
	}
	
	public ScriptEnv getScriptEnv() {
		
		return this.scriptEnv;
		
	}

	public void scriptComplete() {
		
		this.scriptRunning = false;
		
	}

	public boolean isScriptRunning() {
	
		return scriptRunning;
		
	}

	public int getX() {
		
		return this.x;
		
	}
	
	public int getY() {
		
		return this.y;
		
	}
	
	public Game getGame() {
		
		return this.game;
		
	}

	public int getDir() {
		
		return this.dir;
		
	}
	
	public String getName() {
		
		return name;
		
	}

	public boolean isScriptable() {
		
		return this.scriptable;
		
	}
	
	public boolean isCollidable() {
		
		return this.collidable;
		
	}

	public int getZ() {
		
		return this.z;
		
	}
	
	public int getListenerLevel() {
		
		return this.listenerLevel;
		
	}
	
	public abstract void trigger();
	
	public void die() {
		
		if (!dead) {
			
			SoundBank.HURT.play();
		
			this.dead  = true;
			this.collidable = false;
			this.scriptable = false;
			
		}
		
	}

	public boolean isDead() {
		
		return this.dead;
		
	}

	public void setZ(int z) {
		
		this.z = z;
		
	}

	public void startWaiting(int targetWait) {
		
		waiting = true;
		
		this.targetWait = targetWait;
		this.waitTime = 0;
		
	}

	public boolean isWaiting() {
		
		return this.waiting;
		
	}

	public void error() {
		
		this.errored = true;
		
	}

	public boolean isErrored() {
		
		return this.errored;
		
	}
	
}
