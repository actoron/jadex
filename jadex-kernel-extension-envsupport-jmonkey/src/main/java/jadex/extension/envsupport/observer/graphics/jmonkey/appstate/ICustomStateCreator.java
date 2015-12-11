
package jadex.extension.envsupport.observer.graphics.jmonkey.appstate;

import java.util.ArrayList;

import com.jme3.app.state.AbstractAppState;

import de.lessvoid.nifty.screen.ScreenController;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;

public interface ICustomStateCreator
{

	ScreenController getScreenController();
	
	ArrayList<AbstractAppState> getCustomAppStates();
	
	ArrayList<NiftyScreen> getNiftyScreens();
}
