package jadex.agentkeeper.view;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;

import de.lessvoid.nifty.screen.ScreenController;
import jadex.agentkeeper.view.camera.AgentKeeperCameraState;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.ICustomStateCreator;


/**
 * Necessary Class to Create the custom Nifty-Gui for the Application over CLASS-Loading
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class CustomControllerCreator implements ICustomStateCreator
{
	KeeperGuiController		controller;
	
	GeneralAppState generalAppState;
	
	AgentKeeperCameraState cameraState;
	

	SimpleApplication		app;

	ArrayList<NiftyScreen>	niftyScreens = new ArrayList<NiftyScreen>();
	
	ArrayList<AbstractAppState> appStates = new ArrayList<AbstractAppState>();

	private NiftyScreen		screen1 = new NiftyScreen("hud", "gui/DungeonHud.xml", true);

	private NiftyScreen		screen2 = new NiftyScreen("default", "gui/DungeonHud.xml", false);

	public CustomControllerCreator(SimpleApplication app, ISpaceController spaceController)
	{
		this.app = app;
		this.controller = new KeeperGuiController(app, spaceController);
		this.generalAppState = new GeneralAppState();
		this.cameraState = new AgentKeeperCameraState();
		appStates.add(generalAppState);
		appStates.add(cameraState);
		this.niftyScreens.add(screen1);
		this.niftyScreens.add(screen2);
	}

	public ScreenController getScreenController()
	{
		return controller;
	}

	public ArrayList<NiftyScreen> getNiftyScreens()
	{
		return niftyScreens;
	}

	public void setNiftyScreens(ArrayList<NiftyScreen> niftyScreens)
	{
		this.niftyScreens = niftyScreens;
	}

	public ArrayList<AbstractAppState> getCustomAppStates()
	{
		return appStates;
	}


}
