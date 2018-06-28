package jadex.extension.envsupport.observer.perspective;

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import jadex.commons.meta.TypedPropertyObject;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.GridController;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.IObserverCenter;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;


/**
 * Perspective for viewing in 3D.
 */
public class Perspective3D extends TypedPropertyObject implements IPerspective
{
	/** Name of the presentation */
	protected String							name;

	/** The ObserverCenter */
	protected IObserverCenter					obscenter;

	/** The viewport */
	protected IViewport3d						viewport3d;

	/** The selected object */
	protected Object							selectedobject;

	/** Selection cycle for stacked objects */
	protected int								selectCycle;


	/** Maximum selection distance */
	protected IVector1							selectorDistance;

	/** Flag if the x-axis should be inverted */
	protected boolean							invertxaxis;

	/** Flag if the y-axis should be inverted */
	protected boolean							invertyaxis;

	/** Try OpenGL if true */
	protected boolean							tryopengl;

	/** The background color. */
	protected Color								bgColor;

	/** The visuals (DrawableCombiners) */
	protected Map<Object, Object>				visuals;

	/** The static visuals (DrawableCombiners) */
	protected Collection<DrawableCombiner3d>	staticvisuals	= Collections.synchronizedCollection(new ArrayList<DrawableCombiner3d>());

	/** The marker drawable combiner */
	protected DrawableCombiner3d				marker;

	/** The marker drawable combiner */
	protected DrawableCombiner3d				marker3d;

	/** The fetcher. */
	protected SimpleValueFetcher				fetcher;

	/** Flag to indicate that rendering has been called but not yet started. */
//	private boolean								rendering;

	private Primitive3d							_markerPrimitive;

	boolean										wireframe;
	
	private boolean shader = true;
	
	private String camera = "Default";
	
	private String guiCreatorPath = "None";

	/**
	 * Creates a 3D-Perspective.
	 * @param ncreens 
	 */
	public Perspective3D(boolean shader, String camera, String guiCreatorPath)
	{

		super(null);
		
		this.shader = shader;
		
		this.camera = camera;
		
		this.guiCreatorPath = guiCreatorPath;
		
		System.out.println("Perspective3D --->>> new Perspective3D");
		this.visuals = Collections.synchronizedMap(new HashMap());


		this.selectorDistance = new Vector1Double(1.0);
		this.selectCycle = 0;
		this.tryopengl = true;
		this.name = getClass().getName();
		viewport3d = null;
		
		_markerPrimitive = new Primitive3d(Primitive3d.PRIMITIVE_TYPE_SPHERE, Vector3Double.getVector3(0.0, 0.0, 0.0), Vector3Double.getVector3(0.0,
				0.0, 0.0), Vector3Double.getVector3(1.25, 1.25, 1.25), new Color(0f, 0f, 1f, 0.1f));

	}

	/**
	 * Returns a property.
	 * 
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		Object ret = super.getProperty(name);

		if(ret instanceof IParsedExpression)
		{
			ret = ((IParsedExpression)ret).getValue(getFetcher());
		}

		return ret;
	}

	/**
	 * Get the value fetcher.
	 * 
	 * @return The fetcher.
	 */
	public SimpleValueFetcher getFetcher()
	{
		if(fetcher == null)
		{
			this.fetcher = new SimpleValueFetcher(obscenter.getSpace().getFetcher());
			// fetcher.setValue("$space", obscenter.getSpace());
			fetcher.setValue("$perspective", this);
		}
		return this.fetcher;
	}

	/**
	 * Returns the name of the perspective
	 * 
	 * @return name of the perspective
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the perspective
	 * 
	 * @param name name of the perspective
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * Returns the currently selected object.
	 * 
	 * @return currently selected object
	 */
	public Object getSelectedObject()
	{
		return selectedobject;
	}

	/**
	 * Sets the selected object.
	 * 
	 * @param obj selected object
	 */
	public void setSelectedObject(Object obj)
	{
		selectedobject = obj;
		obscenter.fireSelectedObjectChange();
	}

	/**
	 * Sets the ObserverCenter.
	 * 
	 * @param obscenter the ObserverCenter
	 */
	public void setObserverCenter(IObserverCenter obscenter)
	{
		this.obscenter = obscenter;
	}

	/**
	 * Get the ObserverCenter.
	 * 
	 * @return The observer center.
	 */
	public IObserverCenter getObserverCenter()
	{
		return obscenter;
	}

	/**
	 * Adds a new visual object.
	 * 
	 * @param id identifier of the object
	 * @param visual the visual object
	 */
	public void addVisual(Object id, Object visual)
	{
		visuals.put(id, visual);
	}

	/**
	 * Removes a new visual object.
	 * 
	 * @param id identifier of the object
	 */
	public void removeVisual(Object id)
	{
		visuals.remove(id);
	}


	/**
	 * Gets the view of the perspective.
	 * 
	 * @return the view
	 */
	public Component getView()
	{
		if(viewport3d == null)
		{
			if(marker == null)
			{
				marker = new DrawableCombiner3d();

				marker.addPrimitive(_markerPrimitive);
			}

			ClassLoader cl = obscenter.getClassLoader();
			boolean isGrid = obscenter.getSpace().getClass().getSimpleName().startsWith("Grid");

			// TODO alles ok hier?
			AbstractEnvironmentSpace space = obscenter.getSpace();
			GridController gridcontrol = new GridController(space);
			if(space instanceof Space2D)
			{
				IVector2 tmpsize = ((Space2D)space).getAreaSize();
				
				IVector3 tmp3dsize = new Vector3Double(tmpsize.getXAsDouble(),(tmpsize.getXAsDouble()+tmpsize.getYAsDouble())/2,tmpsize.getYAsDouble());
				
				viewport3d = createViewport(this, cl, tmp3dsize, isGrid, shader, camera, guiCreatorPath, gridcontrol);
			}
			else if(space instanceof Space3D)
			{
				IVector3 tmp3dsize = ((Space3D)space).getAreaSize();
				viewport3d = createViewport(this, cl, tmp3dsize, isGrid, shader, camera, guiCreatorPath, gridcontrol);
			}
			else
			{
				throw new RuntimeException("Space type incompatible with Perspective3D: " + space);
			}
		}
		return viewport3d.getCanvas();
	}

	/**
	 * Gets the viewport
	 * 
	 * @return the viewport
	 */
	public IViewport3d getViewport()
	{
		if(viewport3d == null)
			getView();
		return viewport3d;
	}

	/**
	 * Gets x-axis inversion.
	 * 
	 * @return true, if the x-axis should be inverted.
	 */
	public boolean getInvertXAxis()
	{
		return invertxaxis;
	}

	/**
	 * Sets x-axis inversion.
	 * 
	 * @param invert true, if the x-axis should be inverted.
	 */
	public void setInvertXAxis(boolean invert)
	{
		invertxaxis = invert;
	}

	/**
	 * Gets y-axis inversion.
	 * 
	 * @return true, if the y-axis should be inverted.
	 */
	public boolean getInvertYAxis()
	{
		return invertyaxis;
	}

	/**
	 * Sets y-axis inversion.
	 * 
	 * @param invert true, if the y-axis should be inverted.
	 */
	public void setInvertYAxis(boolean invert)
	{
		invertyaxis = invert;
	}

	/**
	 * Resets position and flushes render info
	 */
	public void reset()
	{
		System.out.println("reset!?");
	}

	boolean	firsttime	= true;

	/**
	 * Refreshes the perspective.
	 */
	public void refresh()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(viewport3d!=null)
		{
			IDataView dataview = obscenter.getSelectedDataView();
			if(dataview == null)
			{
				return;
			}

			Object[] objects = dataview.getObjects();

			if(firsttime)
			{
				Collection<Object> tmp = Collections.synchronizedList(new ArrayList<Object>());

				tmp = (Collection<Object>)visuals.values();

				for(Object ob : tmp)
				{
					if(ob instanceof DrawableCombiner3d)
					{
						DrawableCombiner3d combi = (DrawableCombiner3d)ob;
						if(!combi.hasSpaceobject())
						{
							staticvisuals.add(combi);
						}
					}

				}
//				System.out.println("staticvisuals after: " + staticvisuals.size() + " " + staticvisuals.toString());
			}
			firsttime = false;

			List<Object[]> objectList = null;

			objectList = new ArrayList<Object[]>(objects.length + 1);
			for(int j = 0; j < objects.length; ++j)
			{
				Object obj = objects[j];


				DrawableCombiner3d d = (DrawableCombiner3d)visuals.get(SObjectInspector.getType(obj));

				if(d == null)
				{
					continue;
				}

				Object[] viewObj = new Object[2];
				viewObj[0] = obj;
				viewObj[1] = d;
				objectList.add(viewObj);
			}

			// SELECTION

			if(selectedobject != null)
			{
				int selected = ((Long)((SpaceObject)selectedobject).getId()).intValue();
				DrawableCombiner3d dc = (DrawableCombiner3d)visuals.get(SObjectInspector.getType(selectedobject));
				IVector3 size = (IVector3)dc.getBoundValue(selectedobject, dc.getSize(), viewport3d);

				marker.setSize((IVector3)size);
				viewport3d.setSelected(selected, marker);
			}
			else
			{
				viewport3d.setSelected(-1, marker);
			}

//			viewport3d.setObjectList(objectList);
//			viewport3d.setStaticList(staticvisuals);
			viewport3d.refresh(objectList, staticvisuals);
		}
	}

	private IViewport3d createViewport(IPerspective persp, ClassLoader cl, IVector3 spacesize, boolean isGrid, boolean shader, String camera, String guiCreatorPath, ISpaceController spaceController)
	{
		System.out.println("Perspective3D - > Create new Viewport!");
		try
		{
			//(Class<List<NiftyScreen>>)(Class<?>)List.class
			Constructor con = Class.forName("jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey", true,
					Thread.currentThread().getContextClassLoader()).getConstructor(new Class[]{IPerspective.class, ClassLoader.class, IVector3.class, boolean.class, boolean.class, String.class, String.class, ISpaceController.class});

			IViewport3d vp = (IViewport3d)con.newInstance(new Object[]{persp, cl, spacesize, isGrid, shader, camera, guiCreatorPath, spaceController});

			viewport3d = vp;
			vp.startApp();
		}
		catch(ClassNotFoundException cnfe)
		{
			throw new RuntimeException("Cannot create 3D viewport. 3D add-on not installed?", cnfe);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		return viewport3d;
	}

	public boolean getOpenGl()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setOpenGl(boolean opengl)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setPostlayers(Layer[] array)
	{
		// TODO Auto-generated method stub

	}

	public void setPrelayers(Layer[] array)
	{
		// TODO Auto-generated method stub

	}

	/**
	 *  Set the selected Object in the Perspective. Called by the Viewport3d when the selected Object changes
	 * @param identification
	 */
	public void leftClicked(String identification)
	{
		if(!(identification == null)&&!identification.equals("-1"))
		{
			int id = Integer.parseInt(identification);

			Object[] objects = obscenter.getSelectedDataView().getObjects();
			for(int j = 0; j < objects.length; ++j)
			{

				Object obj = objects[j];
				Object identifier = SObjectInspector.getId(obj);
				int sobjid = Integer.parseInt(identifier.toString());
				if(sobjid == id)
				{
					setSelectedObject(obj);
					continue;
				}

			}
		}
		else
		{
			setSelectedObject(null);
		}

	}

	public void resetZoomAndPosition()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return the wireframe
	 */
	public boolean isWireframe()
	{
		return wireframe;
	}

	/**
	 * @param wireframe the wireframe to set
	 */
	public void setWireframe(boolean wireframe)
	{
		this.wireframe = wireframe;
	}

	public DrawableCombiner3d getMarkerDrawCombiner() {
		return marker;
	}
	
	public void setMarkerDrawCombiner(DrawableCombiner3d marker) {
		this.marker = marker;
	}




}
