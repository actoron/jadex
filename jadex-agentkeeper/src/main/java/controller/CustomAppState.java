package controller;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import agentkeeper.map.InitMapProcess;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Line;

import controller.selection.SelectionArea;
import controller.selection.SelectionBox;


public class CustomAppState extends AbstractAppState
{

	private MonkeyApp			app;

	private MonkeyApp			monkeyapp;

	private Node				rootNode;

	private AssetManager		assetManager;

	private AppStateManager		stateManager;

	private InputManager		inputManager;

	private ViewPort			viewPort;

	private DirectionalLight	dl;

	private ISpaceController	spaceController;

	private SpaceObject			selected;

	protected SelectionBox		selectionBox;

	private Geometry			wireBoxGeo;

	private WireBox				wireBox;

	private float				appScaled;

	protected Vector2f			selectionStart	= new Vector2f(Vector2f.ZERO);

	protected SelectionArea		selectionArea;

	CustomAppStateKeyListener	listener;

	Line						line1;

	Geometry					geoline1;


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
		this.selectionArea = new SelectionArea(appScaled, new Vector2f(0, 0), new Vector2f(0, 0));

		setup();
		createSelectionBox();
		setupListener();
	}


	protected SelectionArea getSelectionArea()
	{

		if(getRounded2dMousePos() != null)
		{
			if(getRounded2dMousePos().x < selectionStart.x)
			{
				selectionArea.start.x = getRounded2dMousePos().x;
				selectionArea.end.x = selectionStart.x;
			}
			else
			{
				selectionArea.start.x = selectionStart.x;
				selectionArea.end.x = getRounded2dMousePos().x;
			}
			if(getRounded2dMousePos().y < selectionStart.y)
			{
				selectionArea.start.y = getRounded2dMousePos().y;
				selectionArea.end.y = selectionStart.y;
			}
			else
			{
				selectionArea.start.y = selectionStart.y;
				selectionArea.end.y = getRounded2dMousePos().y;
			}
			return selectionArea;
		}
		return null;
	}

	protected void updateSelectionBox()
	{
		if(isOnView())
		{
			selectionBox.updateSelectionBoxVertices(getSelectionArea());

			int minusx = Math.round(selectionArea.getDeltaXaxis() / appScaled);
			int minusy = Math.round(selectionArea.getDeltaYaxis() / appScaled);

			wireBoxGeo.setLocalTranslation(selectionArea.start.x + appScaled / 2 * (minusx), appScaled / 2, selectionArea.start.y + appScaled / 2 * (minusy));
			wireBox.updatePositions(appScaled / 2 * minusx, appScaled, appScaled / 2 * minusy);

		}


	}

	/**
	 * Returns the mouse position as a Vector2f with rounded values (int)
	 * 
	 * @return The position of the mouse
	 */
	protected Vector2f getRounded2dMousePos()
	{
		Vector2f ret = new Vector2f(0, 0);
		IVector3 tmp = ((MonkeyApp)app).getWorldContactPoint();
		if(tmp != null)
		{

			ret = new Vector2f(Math.round(tmp.getXAsFloat()), Math.round(tmp.getZAsFloat()));
			ret.multLocal(appScaled);
		}
		return ret;
	}


	protected void placeSelectionBox(float x, float z)
	{
		selectionStart.set(x, z);
	}

	protected boolean isOnView()
	{
//		System.out.println("getRounded2dMousePos().y " + getRounded2dMousePos().y );
//		System.out.println("this.app.getViewPort().getCamera().getHeight() * 0.15f " + this.app.getViewPort().getCamera().getHeight() * 0.15f );
		return (getRounded2dMousePos().y < this.app.getViewPort().getCamera().getHeight() * 0.85f);

	}

	public void update(float tpf)
	{
		if(isOnView())
		{
			updateSelection();
		}
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
					// System.out.println("cant parse: " + id);
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
				if(selected.getType().equals(InitMapProcess.ROCK) || selected.getType().equals(InitMapProcess.REINFORCED_WALL))
				{

					if(getSelectionArea() != null)
					{
						if(!listener.actionIsPressed && !listener.cancelIsPressed)
						{
							placeSelectionBox(getRounded2dMousePos().x, getRounded2dMousePos().y);
						}
						updateSelectionBox();
					}
					else
					{
						listener.actionIsPressed = false;
						listener.cancelIsPressed = false;
					}

				}

				else
				{
					selectionBox.updateGeometry(new Vector3f(0, 0, 0), appScaled / 2, appScaled * 1.5f, appScaled / 2);
					wireBoxGeo.setLocalTranslation(appScaled / 2, -100f, appScaled / 2);
				}
			}
			catch(Exception e)
			{

			}

		}

	}



	private void setupListener()
	{
		listener = new CustomAppStateKeyListener(this);

		this.app.getInputManager().addListener(listener, new String[]{"Leftclick"});
		// this.app.getInputManager().addListener(myclickListener, new
		// String[]{"Leftclick"});

	}


	private void createSelectionBox()
	{

		selectionBox = new SelectionBox(new Vector3f(0, 0.5f, 0), appScaled / 2, appScaled, appScaled / 2);
		selectionBox.setDynamic();


		Material mat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.getAdditionalRenderState().setWireframe(true);
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		Material mat2 = mat.clone();
		mat2.setColor("Color", ColorRGBA.Black);
		mat.setColor("Color", ColorRGBA.Blue.mult(new ColorRGBA(1, 1, 1, 0.15f)));
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		selectionBox.getGeo().setMaterial(mat);
		selectionBox.getGeo().setCullHint(CullHint.Never);
		selectionBox.getGeo().setQueueBucket(Bucket.Transparent);
		// selectionBox.getGeo().setQueueBucket(Bucket.Translucent);

		this.app.getRootNode().attachChild(selectionBox.getGeo());


		wireBox = new WireBox(appScaled / 2, appScaled, appScaled / 2);
		wireBox.setDynamic();

		this.wireBoxGeo = new Geometry("wireBox", wireBox);

		this.wireBoxGeo.setMaterial(mat2);
		this.wireBoxGeo.setCullHint(CullHint.Never);

		this.app.getRootNode().attachChild(this.wireBoxGeo);


		line1 = new Line(Vector3f.ZERO, Vector3f.UNIT_XYZ);
		line1.setDynamic();
		geoline1 = new Geometry("line1", line1);

		geoline1.setMaterial(mat2);


		this.app.getRootNode().attachChild(geoline1);


	}


	public void setup()
	{
		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.01f, -1.0f, -0.01f).normalizeLocal());
		dl.setColor(new ColorRGBA(0.5f, 0.4f, 0.4f, 1f).multLocal(0.3f));
		this.app.getRootNode().addLight(dl);

		AmbientLight al = new AmbientLight();
		// al.setColor(new ColorRGBA(1.7f,2.2f,3.2f,1f));
		al.setColor(ColorRGBA.White.mult(0.5f));
		this.app.getRootNode().addLight(al);
	}

}
