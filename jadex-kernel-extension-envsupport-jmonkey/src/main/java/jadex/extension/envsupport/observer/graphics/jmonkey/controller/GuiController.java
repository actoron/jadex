package jadex.extension.envsupport.observer.graphics.jmonkey.controller;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import jadex.extension.envsupport.observer.graphics.jmonkey.*;

public class GuiController extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private Screen screen;
    private SimpleApplication app;

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

    
    public void fireFullscreen() {
    	System.out.println("firefullscreen");
    	 ((MonkeyApp)app).fireFullscreen();
    }
    
    public void options() {
        System.out.println("options");
    }
    
    

    public void quitGame() {
        app.stop();
    }
    
    

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    /** jME3 AppState methods */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
    }

    @Override
    public void update(float tpf) {
        /** jME update loop! */
    }
}