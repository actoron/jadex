package controller;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.SpaceObject;
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
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;


public class CustomGuiController extends GuiController
{


	private Node				rootNode;

	private boolean				toggleStats	= true;

	private SpaceObject			selected;

	private ISpaceController	spaceController;

	private UserEingabenManager	usermanager;
	
	private MonkeyApp app;

	public CustomGuiController(SimpleApplication app, ISpaceController spacecontroller)
	{
		this.app = (MonkeyApp)app;
		rootNode = this.app.getRootNode();

		setupListener();

		this.spaceController = spacecontroller;
		this.usermanager = (UserEingabenManager)spacecontroller.getProperty("uem");


	}


	private void setupListener()
	{
		this.app.getInputManager().addMapping("Leftclick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		ActionListener myclickListener = new ActionListener()
		{

			public void onAction(String name, boolean keyPressed, float tpf)
			{

				if(name.equals("Leftclick") && keyPressed)
				{

				}
				else if(name.equals("Leftclick") && !keyPressed)
				{
//					Vector3Int selectedworldcoord = ((MonkeyApp)app).getSelectedWorldCoord();
//					if(selectedworldcoord != null)
//					{
//						usermanager.userAktion(selectedworldcoord.getXAsInteger(), selectedworldcoord.getZAsInteger(), UserEingabenManager.ABREISSEN);
//					}
					

				}


			}


		};

//		this.app.getInputManager().addListener(myclickListener, new String[]{"Leftclick"});

	}

	/** Nifty GUI ScreenControl methods */
	public void bind(Nifty nifty, Screen screen)
	{
		this.nifty = nifty;
		this.screen = screen;
	}


	public void fireFullscreen()
	{
		app.fireFullscreen();

	}

	public void guiActive()
	{
		app.setGuiActive(true);
	}

	public void options()
	{
		// spaceController.getSpaceObjectsByGridPosition(new Vector2Int(10, 10),
		// null);


	}

	public void setPerform()
	{

		app.getStateManager().getState(StatsAppState.class).setDisplayStatView(toggleStats);
		app.getStateManager().getState(StatsAppState.class).setDisplayFps(toggleStats);
		toggleStats = !toggleStats;

	}

	public void setGrid()
	{
		if(rootNode.getChild("gridNode") != null)
		{
			rootNode.detachChild(((MonkeyApp)app).getGridNode());

		}
		else
		{
			rootNode.attachChild(((MonkeyApp)app).getGridNode());
		}
	}


	public void quitGame()
	{
		app.stop();
	}


	public void onStartScreen()
	{

	}

	public void onEndScreen()
	{
	}


}