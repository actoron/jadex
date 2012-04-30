package jadex.extension.envsupport.observer.perspective;

import jadex.commons.meta.TypedPropertyObject;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport3d;
import jadex.extension.envsupport.observer.graphics.IViewportListener;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;


/**
 * Perspective for viewing in 3D.
 */
public class Perspective3D extends TypedPropertyObject implements IPerspective
{
	/** Name of the presentation */
	protected String							name;

	/** The ObserverCenter */
	protected ObserverCenter					obscenter;

	/** The viewport */
	protected IViewport3d						viewport3d;

	/** The selected object */
	protected Object							selectedobject;

	/** Selection cycle for stacked objects */
	protected int								selectCycle;

	/** The object shift */
	protected IVector2							objectShift;

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

	/** The maximum zoom */
	protected double							zoomlimit;

	/** The fetcher. */
	protected SimpleValueFetcher				fetcher;

	/** Flag to indicate that rendering has been called but not yet started. */
	private boolean								rendering;

	private Primitive3d							_markerPrimitive;


	boolean										wireframe;

	/**
	 * Creates a 3D-Perspective.
	 */
	public Perspective3D()
	{

		super(null);

		System.out.println("Perspective3D --->>> new Perspective3D");
		this.visuals = Collections.synchronizedMap(new HashMap());


		this.objectShift = new Vector2Double();
		this.selectorDistance = new Vector1Double(1.0);
		this.selectCycle = 0;
		this.tryopengl = true;
		this.name = getClass().getName();
		viewport3d = null;
		
		_markerPrimitive = new Primitive3d(Primitive3d.PRIMITIVE_TYPE_SPHERE, Vector3Double.getVector3(0.0, 0.0, 0.0), Vector3Double.getVector3(0.0,
				0.0, 0.0), Vector3Double.getVector3(1.0, 1.0, 1.0), new Color(0f, 0f, 1f, 0.1f));

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
	public void setObserverCenter(ObserverCenter obscenter)
	{
		this.obscenter = obscenter;
	}

	/**
	 * Get the ObserverCenter.
	 * 
	 * @return The observer center.
	 */
	public ObserverCenter getObserverCenter()
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
			viewport3d = createViewport(this, cl);

			// TODO alles ok hier?
			viewport3d.setAreaSize(obscenter.getAreaSize());
			// viewport3d.addViewportListener(selectioncontroller);
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
	 * Gets the object shift.
	 * 
	 * @return the object shift
	 */
	public synchronized IVector2 getObjectShift()
	{
		return objectShift.copy();
	}

	/**
	 * Sets the object shift.
	 * 
	 * @param shift the object shift
	 */
	public synchronized void setObjectShift(IVector2 shift)
	{
		objectShift = shift.copy();
	}

	/**
	 * Resets position and flushes render info
	 */
	public void reset()
	{
		System.out.println("reset!?");
	}

	/**
	 * Flushes the render information.
	 */
	public void flushRenderInfo()
	{
		synchronized(visuals)
		{
			for(Iterator it = visuals.values().iterator(); it.hasNext();)
				((DrawableCombiner3d)it.next()).flushRenderInfo();
		}
	}

	boolean	firsttime	= true;

	/**
	 * Refreshes the perspective.
	 */
	public void refresh()
	{
		if(!rendering)
		{
			rendering = true;
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					rendering = false;

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
						System.out.println("staticvisuals after: " + staticvisuals.size() + " " + staticvisuals.toString());
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

					viewport3d.setObjectList(objectList);
					viewport3d.setStaticList(staticvisuals);
					viewport3d.refresh();
				}
			});
		}
	}

	private IViewport3d createViewport(IPerspective persp, ClassLoader cl)
	{
		System.out.println("Perspective3D - > Create new Viewport!");
		try
		{
			Constructor con = Class.forName("jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey", true,
					Thread.currentThread().getContextClassLoader()).getConstructor(new Class[]{IPerspective.class, ClassLoader.class});

			IViewport3d vp = (IViewport3d)con.newInstance(new Object[]{persp, cl});


			viewport3d = vp;
			vp.startApp();


		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return viewport3d;
	}


	@Override
	public boolean getOpenGl()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
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
			int id = Integer.valueOf(identification).intValue();

			Object[] objects = obscenter.getSelectedDataView().getObjects();
			for(int j = 0; j < objects.length; ++j)
			{

				Object obj = objects[j];
				Object identifier = SObjectInspector.getId(obj);
				int sobjid = Integer.valueOf(identifier.toString()).intValue();
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

	@Override
	public void resetZoomAndPosition()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown()
	{
		viewport3d.stopApp();

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


}
