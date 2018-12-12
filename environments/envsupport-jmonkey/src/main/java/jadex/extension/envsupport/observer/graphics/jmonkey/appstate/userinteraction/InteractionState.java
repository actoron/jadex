package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.userinteraction;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;


public class InteractionState extends AbstractAppState
{

	private MonkeyApp		app;

	private Node			rootNode;

	private AssetManager	assetManager;

	private AppStateManager	stateManager;

	private InputManager	inputManager;

	private ViewPort		viewPort;

	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();

		initKeys();
	}
	
	
	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys()
	{


		inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("Hud", new KeyTrigger(KeyInput.KEY_F1));
		

		inputManager.addMapping("Grid", new KeyTrigger(KeyInput.KEY_F8));
		inputManager.addMapping("Fullscreen", new KeyTrigger(KeyInput.KEY_F11), new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

		ActionListener actionListener = new ActionListener()
		{
		    
			public void onAction(String name, boolean keyPressed, float tpf)
			{
				if(keyPressed && name.equals("Fullscreen"))
				{
					app.fireFullscreen();

				}

				else if(keyPressed && name.equals("Random"))
				{
					app.randomizeHeightMap();

				}
				else if(keyPressed && name.equals("Grid"))
				{

					if(rootNode.getChild("gridNode") != null)
					{
						rootNode.detachChild(app.gridNode);

					}
					else
					{
						rootNode.attachChild(app.gridNode);
					}

				}

				if(name.equals("Select") && keyPressed)
				{
//					fireSelection();
				}


				else if(name.equals("ZoomIn"))
				{
					app.moveCamera(6, false);
				}

				else if(name.equals("ZoomOut"))
				{
					app.moveCamera(-6, false);
				}


			}

			private void makeWireframe()
			{


			}

		};


		inputManager.addListener(actionListener, new String[]{"Hud"});
		inputManager.addListener(actionListener, new String[]{"Random"});
		inputManager.addListener(actionListener, new String[]{"Grid"});
		inputManager.addListener(actionListener, new String[]{"ZoomIn"});
		inputManager.addListener(actionListener, new String[]{"ZoomOut"});

		inputManager.addListener(actionListener, new String[]{"Select"});
		inputManager.addListener(actionListener, new String[]{"Fullscreen"});
		inputManager.addListener(actionListener, new String[]{"FollowCam"});
		inputManager.addListener(actionListener, new String[]{"Fullscreen"});

	}

}
