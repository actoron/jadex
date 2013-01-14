package controller;

import com.jme3.app.SimpleApplication;

import de.lessvoid.nifty.screen.ScreenController;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.observer.graphics.jmonkey.controller.IGuiControllerCreator;

public class CustomControllerCreator implements IGuiControllerCreator
{
	CustomGuiController controller;
	
	SimpleApplication app;
	
	public CustomControllerCreator(SimpleApplication app, ISpaceController spaceController)
	{
		this.app = app;
		this.controller = new CustomGuiController(app, spaceController);
	}

	public ScreenController getScreenController()
	{
		return controller;
	}

}
