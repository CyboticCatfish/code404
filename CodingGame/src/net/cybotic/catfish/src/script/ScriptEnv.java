package net.cybotic.catfish.src.script;

import java.io.IOException;
import java.io.StringReader;

import net.cybotic.catfish.src.game.object.GameObject;
import net.cybotic.catfish.src.game.object.GameObjectController;

import org.mozilla.javascript.*;

public class ScriptEnv {
	
	private GameObject target;
	private GameObjectController controller;
	private Thread scriptThread;
	
	public ScriptEnv(GameObject target) {
		
		this.target = target;
	}
	
	public void launchScript() {
		
		scriptThread = new Thread(new ScriptThread());
		scriptThread.start();
		
	}
	
	@SuppressWarnings("deprecation")
	public void interupt() {
		
		controller.disable();
		target.scriptComplete();
		scriptThread.stop();
		
	}

	public class ScriptThread implements Runnable {
		
		@Override
		public void run() {
			
			Context context = Context.enter();
			Scriptable scope = context.initStandardObjects();
			
			controller = new GameObjectController(target);
			
            scope.put("object", scope, controller);

            Script executable;
			
            try {
				
            	executable = context.compileReader(new StringReader(target.getScript().replaceAll("\n", "")), "", 1, null);
				executable.exec(context, scope);
				target.scriptComplete();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
            
		}
		
		
	}
}
