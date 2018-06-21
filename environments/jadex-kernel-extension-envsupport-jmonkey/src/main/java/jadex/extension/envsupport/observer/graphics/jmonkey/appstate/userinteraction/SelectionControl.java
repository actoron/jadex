package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.userinteraction;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Int;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import jadex.extension.envsupport.observer.graphics.jmonkey.util.StringNames;


public class SelectionControl extends AbstractAppState
{

	private MonkeyApp		app;

	private Node			rootNode;

	private AssetManager	assetManager;

	private AppStateManager	stateManager;

	private InputManager	inputManager;

	private ViewPort		viewPort;
	
	private SelectionLogic selectionLogic;

	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.selectionLogic = new SelectionLogic(stateManager, app);
	}

	
	
	public IVector3 getMouseContactPoint()
	{
		return selectionLogic.getMouseContactPoint(rootNode);
	}
	
	//TODO: still big hack, todo: make it more general
	public Vector3Int getSelectedWorldCoord()
	{
		return selectionLogic.getSelectedVector3Int((Node)this.rootNode.getChild(StringNames.BATCH_NODE));
	}

	public Object computeSelectedId()
	{
		return selectionLogic.computeSelectedId(rootNode);
	}

	
	
	

}
