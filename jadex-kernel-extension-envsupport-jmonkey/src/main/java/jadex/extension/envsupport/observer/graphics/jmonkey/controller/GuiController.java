package jadex.extension.envsupport.observer.graphics.jmonkey.controller;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import jadex.extension.envsupport.observer.graphics.jmonkey.*;

public class GuiController extends AbstractAppState implements ScreenController {

    public Nifty nifty;
    public Screen screen;
    public SimpleApplication app;
    public Node rootNode;

    /** custom methods */
    public GuiController() {
    }

    public GuiController(SimpleApplication app) {
       this.app = app;
    }

    /** Nifty GUI ScreenControl methods */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    
//    public void fireFullscreen() {
//    	System.out.println("firefullscreen aus nifty");
//    	 ((MonkeyApp)app).fireFullscreen();
//    }
    
    public void options() {
        
        
    }
    
    public void setBindZero() {
    	System.out.println("set bind zero");
    	
    	app.getInputManager().deleteMapping("Leftclick");
//    	app.getInputManager().clearMappings();
//    	inputManager.addListener(actionListener, new String[] { "Leftclick" });
        
    }
    
    

    public void quitGame() {
        app.stop();
    }
    
    

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    /** jME3 AppState methods */
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        rootNode = this.app.getRootNode();
        


    }
    
    

    @Override
    public void update(float tpf) {
        /** jME update loop! */
    }
}