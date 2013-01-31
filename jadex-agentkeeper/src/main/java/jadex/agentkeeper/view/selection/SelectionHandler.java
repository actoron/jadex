package jadex.agentkeeper.view.selection;

import jadex.agentkeeper.init.map.process.InitMapProcess;
import jadex.agentkeeper.view.GeneralAppState;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;


import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.WireBox;



public class SelectionHandler
{
	private MonkeyApp						app;

	private GeneralAppState					mystate;

	private float							appScaled;

	/* Visuals for Selection*/
	private SelectionBox					visualSelectionBox;

	private Geometry						wireBoxGeo;

	private WireBox							wireBox;

	private Material						matWireBox;

	private Material						matVisualBox;

	/* The selected Area */
	protected SelectionArea					selectionArea;

	protected Vector2f						selectionStart	= new Vector2f(Vector2f.ZERO);


	protected String						selectedStringType;

	private SelectionHandlingKeyListener	selectionListener;


	public SelectionHandler(MonkeyApp app, GeneralAppState mystate)
	{
		this.app = app;
		this.mystate = mystate;
		this.appScaled = app.getAppScaled();
		this.selectionArea = new SelectionArea(appScaled, new Vector2f(0, 0), new Vector2f(0, 0));

		setupVisualsForSelection();

		selectionListener = new SelectionHandlingKeyListener(this);

		this.app.getInputManager().addMapping("Lclick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

		this.app.getInputManager().addListener(selectionListener, "Lclick");


	}

	public void updateHandler()
	{
		if(isOnView())
		{
			updateSelection();
		}
	}


	private void updateSelection()
	{
		Object id = app.getSelectedSpaceObjectId();

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
				SpaceObject selected = mystate.getSpaceObjectById(idlong);

				selectedStringType = selected.getType();

				mystate.updateInfoText(selectedStringType);

				if(selectedStringType.equals(InitMapProcess.ROCK) || selectedStringType.equals(InitMapProcess.REINFORCED_WALL))
				{

					if(getSelectionArea() != null)
					{
						if(!selectionListener.actionIsPressed && !selectionListener.cancelIsPressed)
						{
							placeSelectionBox(getRounded2dMousePos().x, getRounded2dMousePos().y);
						}
						updateSelectionBox();
					}
					else
					{
						selectionListener.actionIsPressed = false;
						selectionListener.cancelIsPressed = false;
					}

				}

				else
				{
					visualSelectionBox.updateGeometry(new Vector3f(0, 0, 0), appScaled / 2, appScaled * 1.5f, appScaled / 2);
					wireBoxGeo.setLocalTranslation(appScaled / 2, -100f, appScaled / 2);
				}
			}
			catch(Exception e)
			{

			}

		}

	}


	/**
	 * Returns the mouse position as a Vector2f with rounded values (int)
	 * 
	 * @return The position of the mouse
	 */
	protected Vector2f getRounded2dMousePos()
	{
		Vector2f ret = null;
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
		if(isOnView())
		{
			selectionStart.set(x, z);
		}

	}

	protected boolean isOnView()
	{
		return this.app.getInputManager().getCursorPosition().y > 100;
	}

	protected SelectionArea getSelectionArea()
	{

		if(isOnView()&&getRounded2dMousePos() != null)
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

	public void updateSelectionBox()
	{
		if(isOnView())
		{
			visualSelectionBox.updateSelectionBoxVertices(getSelectionArea());

			int minusx = Math.round(selectionArea.getDeltaXaxis() / appScaled);
			int minusy = Math.round(selectionArea.getDeltaYaxis() / appScaled);

			wireBoxGeo.setLocalTranslation(selectionArea.start.x + appScaled / 2 * (minusx), appScaled / 2, selectionArea.start.y + appScaled / 2 * (minusy));
			wireBox.updatePositions(appScaled / 2 * minusx, appScaled, appScaled / 2 * minusy);

		}
	}

	private void setupVisualsForSelection()
	{


		matVisualBox = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.getAdditionalRenderState().setWireframe(true);
		matVisualBox.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		matWireBox = matVisualBox.clone();
		matWireBox.setColor("Color", ColorRGBA.Black);
		matVisualBox.setColor("Color", ColorRGBA.Blue.mult(new ColorRGBA(1, 1, 1, 0.15f)));
		matVisualBox.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		visualSelectionBox = new SelectionBox(new Vector3f(0, 0.5f, 0), appScaled / 2, appScaled, appScaled / 2);
		visualSelectionBox.setDynamic();
		visualSelectionBox.getGeo().setMaterial(matVisualBox);
		visualSelectionBox.getGeo().setCullHint(CullHint.Never);
		visualSelectionBox.getGeo().setQueueBucket(Bucket.Transparent);
		this.app.getRootNode().attachChild(visualSelectionBox.getGeo());

		this.wireBox = new WireBox(appScaled / 2, appScaled, appScaled / 2);
		this.wireBox.setDynamic();
		this.wireBoxGeo = new Geometry("wireBox", wireBox);
		this.wireBoxGeo.setMaterial(matWireBox);
		this.wireBoxGeo.setCullHint(CullHint.Never);

		this.app.getRootNode().attachChild(this.wireBoxGeo);

	}

	public void userSubmit(SelectionArea selectionArea)
	{
		mystate.userSubmit(selectionArea);
	}

	/**
	 * @return the selectedStringType
	 */
	public String getSelectedStringType()
	{
		return selectedStringType;
	}

	/**
	 * @param selectedStringType the selectedStringType to set
	 */
	public void setSelectedStringType(String selectedStringType)
	{
		this.selectedStringType = selectedStringType;
	}

	/**
	 * @return the selectionListener
	 */
	public SelectionHandlingKeyListener getSelectionListener()
	{
		return selectionListener;
	}

	/**
	 * @param selectionListener the selectionListener to set
	 */
	public void setSelectionListener(SelectionHandlingKeyListener selectionListener)
	{
		this.selectionListener = selectionListener;
	}

}
