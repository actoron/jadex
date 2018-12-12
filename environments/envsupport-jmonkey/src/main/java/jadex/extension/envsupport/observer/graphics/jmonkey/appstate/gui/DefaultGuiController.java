package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;

public class DefaultGuiController extends AbstractAppState implements ScreenController {

    public Nifty nifty;
    public Screen screen;
    public SimpleApplication app;
    public Node rootNode;

    /** custom methods */
    public DefaultGuiController() {
    }

    public DefaultGuiController(SimpleApplication app) {
       this.app = app;
       
    }

    /** Nifty GUI ScreenControl methods */
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    
    public void fireFullscreen() {
    	 ((MonkeyApp)app).fireFullscreen();
    }
    
    public void options() {

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
        rootNode = this.app.getRootNode();
        System.out.println("jME3 AppState methods");

        


    }
    


}