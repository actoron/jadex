package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.refresh;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;

public class UpdateSpaceObjectsState extends AbstractAppState
{
	
	private MonkeyApp		app;

	private Node			rootNode;
//	private Node			batchNodeStatics, batchNodeDynamics, normalNodeDynamics;


	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();


	}

}
