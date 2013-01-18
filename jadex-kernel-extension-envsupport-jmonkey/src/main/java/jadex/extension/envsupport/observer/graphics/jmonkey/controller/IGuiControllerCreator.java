package jadex.extension.envsupport.observer.graphics.jmonkey.controller;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;

import java.util.ArrayList;

import com.jme3.app.state.AppState;

import de.lessvoid.nifty.screen.ScreenController;

public interface IGuiControllerCreator
{

	ScreenController getScreenController();
	
	AppState getCustomAppState();
	
	ArrayList<NiftyScreen> getNiftyScreens();
}
