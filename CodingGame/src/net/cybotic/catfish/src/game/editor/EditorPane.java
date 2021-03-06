package net.cybotic.catfish.src.game.editor;

import java.util.ArrayList;
import java.util.List;

import net.cybotic.catfish.src.Main;
import net.cybotic.catfish.src.game.Game;
import net.cybotic.catfish.src.game.object.GameObject;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.MouseOverArea;

public class EditorPane {
	
	private List<String> currentScript = new ArrayList<String>();
	private float editorTimer = 0f, flash = 0f, x = 0f, targetWidth = 200f;
	private int cursorPosition = 0, currentLine = 0;
	private boolean firstPress = true, cursorShow = false, closing = false;
	private Game game;
	private GameObject object;
	private MouseOverArea exit;
	private SpriteSheet buttons;
	
	public EditorPane(Game game, GameObject target, GameContainer gc) throws SlickException {
		
		String[] script = target.getScript().split("\n");
		
		for (int i = 0; i < script.length; i++) {
			
			currentScript.add(script[i]);
		}
		
		target.setScript(this.getCurrentScript());
		
		this.game = game;
		this.object = target;
		this.buttons = new SpriteSheet(Main.loadImage("res/buttons.png"), 24, 24);
		
		exit = new MouseOverArea(gc, buttons.getSubImage(0, 1), (int) Math.ceil(gc.getWidth() - (targetWidth - 10)), gc.getHeight() - 32);
			exit.setMouseOverImage(buttons.getSubImage(1, 1));
			exit.setMouseDownImage(buttons.getSubImage(2, 1));

	}
	
	public void appendCharacter(char character) {
		
		String start = currentScript.get(currentLine).substring(0, cursorPosition);
		String end = currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length());
		
		if (character == ')' && end.startsWith(")")) {
		
			cursorPosition += 1;
		
		} else {
			
			currentScript.set(currentLine, start + character + end);
			cursorPosition += 1;
			
		}
		
	}
	
	public void appendString(String string) {
		
		String start = currentScript.get(currentLine).substring(0, cursorPosition);
		String end = currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length());
		
		currentScript.set(currentLine, start + string + end);
		
	}

	public void backSpace() {
		
		if (currentScript.get(currentLine).length() > 0 && cursorPosition > 0) {
			
			String start = currentScript.get(currentLine).substring(0, cursorPosition - 1);			
			String end = currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length());
			
			if (start.endsWith("  ")) {
			
				start = currentScript.get(currentLine).substring(0, cursorPosition - 2);
				cursorPosition -= 2;
			
			}
			
			currentScript.set(currentLine, start + end);
			cursorPosition -= 1;
			
		} else if (currentLine != 0) {
			
			String temp = currentScript.get(currentLine);
			currentScript.remove(currentLine);
			currentLine -= 1;
			cursorPosition = currentScript.get(currentLine).length();
			this.appendString(temp);
			
		}
		
	}
	
	public void render(GameContainer gc, Graphics g) {
		
		g.setColor(new Color(60, 60 , 60));
		g.fillRect(gc.getWidth() - x,  0, targetWidth, gc.getHeight());
		
		g.setColor(new Color(0, 0 , 0, 70));
		g.fillRect(gc.getWidth() - x, 3 + 13 * currentLine, targetWidth, 13);
		
		g.setColor(new Color(0, 0 , 0, 51));
		g.fillRect(gc.getWidth() - x,  0, 6, gc.getHeight());
		
		g.setColor(Color.white);
		
		for (int i = 0; i < currentScript.size(); i++) g.drawString(currentScript.get(i), gc.getWidth() - x + 8, 3 + i * 13);
		if (cursorShow) g.drawLine(gc.getWidth() - x + 8 + cursorPosition * 6.55f, 3 + currentLine * 13f, gc.getWidth() - x + 8 + cursorPosition * 6.55f, 15 + currentLine * 13f);
		
		g.translate(- x + targetWidth, 0);
		
		exit.render(gc, g);
		
		g.resetTransform();
		
	}

	public String getCurrentScript() {
		
		String script = "";
		
		for (int i = 0; i < currentScript.size(); i++) script += currentScript.get(i) + "\n";
		
		return script;
		
		
	}

	public void update(GameContainer gc, int delta) {
		
		if (x < targetWidth && this.game.isEditorOpen()) x += 1f * delta;
		else if (x > targetWidth && this.game.isEditorOpen()) x = targetWidth;
		else if (x >= 0f && !this.game.isEditorOpen()) x -= 1f * delta;
		
		if (closing) game.closeEditor(gc);
		else {
			
			flash += 0.1f * delta;
			if (flash > 30f) {
				
				cursorShow = !cursorShow;
				flash = 0f;
				
			}
			
			if (gc.getInput().isKeyDown(Input.KEY_BACK) ||
					gc.getInput().isKeyDown(Input.KEY_UP) ||
					gc.getInput().isKeyDown(Input.KEY_DOWN) ||
					gc.getInput().isKeyDown(Input.KEY_LEFT) ||
					gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
				
				if (editorTimer < 50f && firstPress) editorTimer += 0.1f * delta;
				
				else if (editorTimer  < 5f && !firstPress) editorTimer += 0.1f * delta;
					
				else {
					
					if (gc.getInput().isKeyDown(Input.KEY_BACK)) backSpace();
					else if (gc.getInput().isKeyDown(Input.KEY_UP)) cursorUp();
					else if (gc.getInput().isKeyDown(Input.KEY_DOWN)) cursorDown();
					else if (gc.getInput().isKeyDown(Input.KEY_LEFT)) cursorLeft();
					else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) cursorRight();
					
					firstPress = false;
					editorTimer = 0f;
					
				}
				
			} else if (!gc.getInput().isKeyDown(Input.KEY_BACK) && 
					!gc.getInput().isKeyDown(Input.KEY_UP) && 
					!gc.getInput().isKeyDown(Input.KEY_DOWN) && 
					!gc.getInput().isKeyDown(Input.KEY_LEFT) && 
					!gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
				
				firstPress = true;
				editorTimer = 0f;
			}
		
		}
		
		for (int i = 0; i < this.currentScript.size(); i++) {
			
			if (this.currentScript.get(i).contains("=new ") | this.currentScript.get(i).contains(" new ") | this.currentScript.get(i).contains("(new ") | this.currentScript.get(i).contains("import(")) {
				
				currentScript.remove(i);
				currentScript.add(i, "");
				cursorPosition = 0;
				
			}
			
		}
		
		this.getTarget().setScript(getCurrentScript());
		
	}

	public void newLine() {
		
		if (currentScript.get(currentLine).length() > this.cursorPosition) {
		
			if (currentScript.get(currentLine).charAt(cursorPosition - 1) == '{' && currentScript.get(currentLine).charAt(cursorPosition) == '}') {
					
				currentScript.add(currentLine + 1, currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length()));
				currentScript.set(currentLine, currentScript.get(currentLine).substring(0, cursorPosition));
				currentLine += 1;
				cursorPosition = 0;
				currentScript.add(currentLine + 1, currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length()));
				currentScript.set(currentLine, currentScript.get(currentLine).substring(0, cursorPosition));
				
				cursorPosition = 0;
				this.currentScript.set(currentLine, "   ");
				cursorPosition = 3;
					
	
			} 
		
		} else if (currentScript.size() < 42) {
		
				currentScript.add(currentLine + 1, currentScript.get(currentLine).substring(cursorPosition, currentScript.get(currentLine).length()));
				currentScript.set(currentLine, currentScript.get(currentLine).substring(0, cursorPosition));
				currentLine += 1;
				
				cursorPosition = 0;

		}
		
	}

	public void cursorLeft() {
		
		if (cursorPosition > 0) cursorPosition -= 1;
		else {
			
			if (currentLine > 0) {
			
				currentLine -= 1;
				cursorPosition = currentScript.get(currentLine).length();
				
			}
			
		}
		
	}

	public void cursorRight() {
		
		if (cursorPosition < currentScript.get(currentLine).length()) cursorPosition += 1;
		else {
			
			if (currentLine < currentScript.size() - 1) {
				
				currentLine += 1;
				cursorPosition = 0;
				
			}
			
		}
		
	}

	public void cursorUp() {
		
		if (currentLine > 0) {
			
			currentLine -= 1;
			
			if (cursorPosition > currentScript.get(currentLine).length()) cursorPosition = currentScript.get(currentLine).length();
			
		}
		
	}

	public void cursorDown() {
		
		if (currentLine < currentScript.size() - 1) {
			
			currentLine += 1;
			
			if (cursorPosition > currentScript.get(currentLine).length()) cursorPosition = currentScript.get(currentLine).length();
			
		}
		
	}

	public GameObject getTarget() {
		
		return this.object;
		
	}

	public void close() {
		
		this.closing = true;
		
	}

	public float getTargetWidth() {
		
		return this.targetWidth;
		
	}
	
	public void resizeBy(int width, GameContainer gc) {
		
		this.x += width;
		this.targetWidth += width;
		
		if (targetWidth < 126) {
			
			x = 126;
			targetWidth = 126;
			
		}
		
		exit.setX((int) Math.ceil(gc.getWidth() - (targetWidth - 10)));
		
	}
	
	public void checkExit(GameContainer gc) {
		
		if (gc.getInput().getAbsoluteMouseX() > gc.getWidth() - targetWidth + 6) {
			
			if (exit.isMouseOver()) {
				
				this.getTarget().setScript(this.getCurrentScript());
				this.close();
				
			} else {
				
				currentLine = (int) Math.floor(gc.getInput().getAbsoluteMouseY() / 13f);
				if (currentLine > this.currentScript.size() - 1) currentLine = currentScript.size() - 1;
				cursorPosition = (int) Math.floor((gc.getInput().getAbsoluteMouseX() - gc.getWidth() + targetWidth)/ 6.5f);
				if (cursorPosition > this.currentScript.get(currentLine).length()) cursorPosition = currentScript.get(currentLine).length();
				else if (cursorPosition < 0) cursorPosition = 0;
				
			}
			
		}
		
	}

}
