package controller;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.extension.envsupport.math.Vector3Int;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import jadex.extension.envsupport.observer.graphics.jmonkey.controller.GuiController;
import agentkeeper.gui.UserEingabenManager;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;

public class CustomGuiController extends GuiController {

	
	private Node rootNode;
	private boolean toggleStats = true;

	DirectionalLight dl;
	ISpaceController spaceController;
	
	private UserEingabenManager usermanager;
	
    public CustomGuiController(SimpleApplication app, ISpaceController spacecontroller) {
       this.app = app;
       rootNode = this.app.getRootNode();
       
       setupListener();

       this.spaceController = spacecontroller;
       this.usermanager = (UserEingabenManager) spacecontroller.getProperty("uem");
       setup();


    }

    private void setupListener()
	{
        this.app.getInputManager().addMapping("Leftclick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
 		ActionListener leftClickListener = new ActionListener() {
 			
 			public void onAction(String name, boolean keyPressed, float tpf) {
 				
 				if (name.equals("Leftclick") && keyPressed) {
 					
 					Vector3Int selectedworldcoord = ((MonkeyApp)app).getSelectedWorldCoord();
 					if(selectedworldcoord!=null)
 					{
 						usermanager.userAktion(selectedworldcoord.getXAsInteger(), selectedworldcoord.getZAsInteger(), UserEingabenManager.ABREISSEN);
 					}
 					
 				}
 				
 			}
 				
 		};
 		
 		this.app.getInputManager().addListener(leftClickListener, new String[] { "Leftclick" });
		
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
    	spaceController.getSpaceObjectsByGridPosition(new Vector2Int(10,10), null);
        

    }
    
    public void setPerform() {
    	
    	app.getStateManager().getState(StatsAppState.class).setDisplayStatView(toggleStats);
    	app.getStateManager().getState(StatsAppState.class).setDisplayFps(toggleStats);
    	toggleStats = !toggleStats;
    	
    }
    
    public void setGrid() {
		if (rootNode.getChild("gridNode") != null) {
			rootNode.detachChild(((MonkeyApp)app).getGridNode());

		} else {
			rootNode.attachChild(((MonkeyApp)app).getGridNode());
		}
    }
    
    

    public void quitGame() {
        app.stop();
    }
    


    public void onStartScreen() {

    }

    public void onEndScreen() {
    }
    
    public void setup()
    {
     	dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.001f, -1.0f, -0.01f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.5f,0.4f,0.4f,1f).multLocal(0.7f));
        this.app.getRootNode().addLight(dl);    

        AmbientLight al = new AmbientLight();
//        al.setColor(new ColorRGBA(1.7f,2.2f,3.2f,1f));
        al.setColor(ColorRGBA.White.mult(3.0f));
        this.app.getRootNode().addLight(al);
    }

}