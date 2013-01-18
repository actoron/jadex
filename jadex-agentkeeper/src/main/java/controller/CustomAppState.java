package controller;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;

import agentkeeper.map.InitMapProcess;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

import controller.selection.SelectionBox;


public class CustomAppState extends AbstractAppState
{

	private MonkeyApp		app;
	
	private MonkeyApp		monkeyapp;

	private Node			rootNode;

	private AssetManager	assetManager;

	private AppStateManager	stateManager;

	private InputManager	inputManager;

	private ViewPort		viewPort;
	
	private DirectionalLight			dl;
	
	private ISpaceController			spaceController;
	
	private SpaceObject			selected;

	private SelectionBox		selectionBox;
	
	private float appScaled;
	


	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.monkeyapp = (MonkeyApp)app;
		this.spaceController = monkeyapp.getSpaceController();
		this.appScaled = monkeyapp.getAppScaled();

		setup();
		createSelectionBox();
		setupListener();
	}
	
	public void update(float tpf)
	{
		updateSelection();
	}
	
	private void updateSelection()
	{
		Object id = ((MonkeyApp)app).getSelectedSpaceObjectId();

		long idlong = -1;

		if(id instanceof String)
		{
			String idString = (String)id;
			if(Character.isDigit(idString.charAt(0)))
				;
			{
				try
				{
					idlong = Integer.parseInt(idString);
				}
				catch(NumberFormatException e)
				{
					System.out.println("cant parse: " + id);
					idlong = -1;
				}

			}

		}
		else if(id instanceof Integer)
		{
			idlong = (Integer)id;
		}


		if(idlong != -1)
		{
			try
			{
				selected = (SpaceObject)spaceController.getSpaceObject(idlong);
				if(selected.getType().equals(InitMapProcess.ROCK)||selected.getType().equals(InitMapProcess.REINFORCED_WALL))
				{
				
				Vector2Double pos = (Vector2Double)selected.getProperty(Space2D.PROPERTY_POSITION);
				Vector3f center = new Vector3f(pos.getXAsFloat(), 0.5f, pos.getYAsFloat());

				selectionBox.updateGeometry(center.multLocal(((MonkeyApp)app).getAppScaled()), appScaled/2, appScaled, appScaled/2);
				}
				else
				{
					selectionBox.updateGeometry(new Vector3f(0, 0.5f, 0), appScaled/2, appScaled, appScaled/2);
				}
			}
			catch(Exception e)
			{
				
			}

		}

	}
	
	private void setupListener()
	{
		this.app.getInputManager().addMapping("Middleclick", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
		ActionListener myclickListener = new ActionListener()
		{

			public void onAction(String name, boolean keyPressed, float tpf)
			{

				if(name.equals("Middleclick") && keyPressed)
				{
					updateSelection();
				}

			}


		};

		this.app.getInputManager().addListener(myclickListener, new String[]{"Middleclick"});

	}
	
	private void createSelectionBox()
	{
		selectionBox = new SelectionBox(new Vector3f(0, 0.5f, 0), appScaled/2, appScaled, appScaled/2);
		selectionBox.setDynamic();
		
		
		Material mat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setColor("Color", ColorRGBA.Red);

		selectionBox.getGeo().setMaterial(mat);
		selectionBox.getGeo().setCullHint(CullHint.Never);

		this.app.getRootNode().attachChild(selectionBox.getGeo());

	}
	
	
	public void setup()
	{
		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.001f, -1.0f, -0.01f).normalizeLocal());
		dl.setColor(new ColorRGBA(0.5f, 0.4f, 0.4f, 1f).multLocal(0.7f));
		this.app.getRootNode().addLight(dl);

		AmbientLight al = new AmbientLight();
		// al.setColor(new ColorRGBA(1.7f,2.2f,3.2f,1f));
		al.setColor(ColorRGBA.White.mult(3.0f));
		this.app.getRootNode().addLight(al);
	}

}
