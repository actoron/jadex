package jadex.extension.envsupport.observer.perspective;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import jadex.commons.meta.TypedPropertyObject;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.observer.graphics.IViewport;
import jadex.extension.envsupport.observer.graphics.IViewportListener;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.extension.envsupport.observer.graphics.java2d.ViewportJ2D;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.IObserverCenter;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Perspective for viewing in 2D.
 */
public class Perspective2D extends TypedPropertyObject implements IPerspective
{
	//-------- constants --------
	
	/** Global flag for turning off OpenGL from starter. */
	public static boolean	OPENGL	= true;
	
	//-------- attributes --------
	
	/** Name of the presentation */
	protected String name;
	
	/** The ObserverCenter */
	protected IObserverCenter obscenter;
	
	/** The viewport */
	protected IViewport viewport;
	
	/** Selection controller. */
	protected SelectionController selectioncontroller;
	
	/** The selected object */
	protected Object selectedobject;
	
	/** Selection cycle for stacked objects */
	protected int selectCycle;
	
	/** Order in which objects are displayed */
	protected Comparator displayorder;
	
	/** The object shift */
	protected IVector2 objectShift;
	
	/** Maximum selection distance */
	protected IVector1 selectorDistance;
	
	/** Flag if the x-axis should be inverted */
	protected boolean invertxaxis;
	
	/** Flag if the y-axis should be inverted */
	protected boolean invertyaxis;
	
	/** Try OpenGL if true */
	protected boolean tryopengl;
	
	/** The background color. */
	protected Color bgColor;
	
	/** The visuals (DrawableCombiners) */
	protected Map visuals;
	
	/** The prelayers */
	protected Layer[] prelayers;
	
	/** The postlayers */
	protected Layer[] postlayers;
	
	/** The marker drawable combiner */
	protected DrawableCombiner marker;
	
	/** The maximum zoom */
	protected double zoomlimit;
	
	/** The fetcher. */
	protected SimpleValueFetcher fetcher;
	
	/** Flag to indicate that rendering has been called but not yet started. */
	private boolean			rendering;

	/**
	 * Creates a 2D-Perspective.
	 */
	public Perspective2D()
	{
		// TODO: give the perspective the right meta information about his properties
		super(null);
		
		zoomlimit = 20.0;
		setBackground(null);
		this.visuals = Collections.synchronizedMap(new HashMap());
		this.prelayers = new Layer[0];
		this.postlayers = new Layer[0];
		
		this.objectShift = new Vector2Double();
		this.selectorDistance = new Vector1Double(1.0);
		this.selectCycle = 0;
		this.tryopengl = OPENGL;
		
		this.displayorder = null;
		
		this.name = getClass().getName();
		viewport = null;
		selectioncontroller = new SelectionController();
	}
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		Object ret = super.getProperty(name);
		
		if(ret instanceof IParsedExpression)
		{
			ret = ((IParsedExpression) ret).getValue(getFetcher());
		}
		
		return ret;
	}
	
	/**
	 *  Get the value fetcher.
	 *  @return The fetcher.
	 */
	public SimpleValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			this.fetcher = new SimpleValueFetcher(obscenter.getSpace().getFetcher());
			fetcher.setValue("$perspective", this);
		}
		return this.fetcher;
	}
	
	/**
	 * Returns the name of the perspective
	 * @return name of the perspective
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the perspective
	 * @param name name of the perspective
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/** Returns the currently selected object.
	 * 
	 *  @return currently selected object
	 */
	public Object getSelectedObject()
	{
		return selectedobject;
	}
	
	/**
	 * Sets the selected object.
	 * 
	 *  @param obj selected object
	 */
	public void setSelectedObject(Object obj)
	{
		selectedobject = obj;
		obscenter.fireSelectedObjectChange();
	}
	
	/**
	 * Sets the ObserverCenter.
	 * @param obscenter the ObserverCenter
	 */
	public void setObserverCenter(IObserverCenter obscenter)
	{
		this.obscenter = obscenter;
	}
	
	/**
	 *  Get the ObserverCenter.
	 *  @return The observer center.
	 */
	public IObserverCenter getObserverCenter()
	{
		return obscenter;
	}
	
	/**
	 * Adds a new visual object.
	 * @param id identifier of the object
	 * @param visual the visual object
	 */
	public void addVisual(Object id, Object visual)
	{
		visuals.put(id, visual);
	}
	
	/**
	 * Removes a new visual object.
	 * @param id identifier of the object
	 */
	public void removeVisual(Object id)
	{
		visuals.remove(id);
	}
	
	/**
	 * Returns the prelayers.
	 * @return the prelayers
	 */
	public Layer[] getPrelayers()
	{
		return prelayers;
	}
	
	/**
	 * Sets the prelayers.
	 * @param prelayers the prelayers
	 */
	public void setPrelayers(Layer[] prelayers)
	{
		this.prelayers = prelayers;
	}
	
	/**
	 * Returns the Postlayers.
	 * @return the Postlayers
	 */
	public Layer[] getPostlayers()
	{
		return postlayers;
	}
	
	/**
	 * Sets the Postlayers.
	 * @param Postlayers the Postlayers
	 */
	public void setPostlayers(Layer[] postlayers)
	{
		this.postlayers = postlayers;
	}
	
	/**
	 * Gets the drawable combiner object for the object marker
	 * @return the marker drawable
	 */
	public DrawableCombiner getMarkerDrawCombiner()
	{
		return marker;
	}
	
	/**
	 * Sets the drawable combiner object for the object marker
	 * @param marker the marker drawable
	 */
	public void setMarkerDrawCombiner(DrawableCombiner marker)
	{
		this.marker = marker;
	}
	
	/**
	 * Gets the view of the perspective.
	 * @return the view
	 */
	public Component getView()
	{
		if(viewport == null)
		{
			if(marker == null)
			{
				marker = new DrawableCombiner();
				Primitive markerPrimitive = new TexturedRectangle(getClass().getPackage().getName().replaceAll("perspective", "").concat("images.").replaceAll("\\.", "/").concat("selection_marker.png"));
				marker.addPrimitive(markerPrimitive, Integer.MAX_VALUE);
			}
//			System.out.println("Persp: "+name+" opengl="+tryopengl);
			ClassLoader	cl	= obscenter.getClassLoader();
			viewport = createViewport(this, cl, bgColor, tryopengl);
			AbstractEnvironmentSpace space = obscenter.getSpace();
			if(space instanceof Space2D)
			{
				viewport.setAreaSize(((Space2D)space).getAreaSize().copy());
			}
			viewport.addViewportListener(selectioncontroller);
			viewport.setZoomLimit(zoomlimit);
		}
		return viewport.getCanvas();
	}
	
	/**
	 * Gets the viewport
	 * @return the viewport
	 */
	public IViewport getViewport()
	{
		if(viewport == null)
			getView();
		return viewport;
	}
	
	/**
	 * Gets whether to try to use OpenGL.
	 * @return true, if attempt should be made to use OpenGL
	 */
	public boolean getOpenGl()
	{
		return tryopengl;
	}
	
	/**
	 * Sets whether to try to use OpenGL.
	 * @param opengl true, if attempt should be made to use OpenGL
	 */
	public boolean setOpenGl(boolean opengl)
	{
		this.tryopengl = opengl;
		if (viewport != null)
		{
			viewport = null;
			getView();
			try
			{
				if (!Class.forName("jadex.extension.envsupport.observer.graphics.opengl.ViewportJOGL",
					true, Thread.currentThread().getContextClassLoader()).isInstance(viewport))
					return false;
			}
			catch (ClassNotFoundException e)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets x-axis inversion.
	 * @return true, if the x-axis should be inverted.
	 */
	public boolean getInvertXAxis()
	{
		return invertxaxis;
	}
	
	/**
	 * Sets x-axis inversion.
	 * @param invert true, if the x-axis should be inverted.
	 */
	public void setInvertXAxis(boolean invert)
	{
		invertxaxis = invert;
	}
	
	/**
	 * Gets y-axis inversion.
	 * @return true, if the y-axis should be inverted.
	 */
	public boolean getInvertYAxis()
	{
		return invertyaxis;
	}
	
	/**
	 * Sets y-axis inversion.
	 * @param invert true, if the y-axis should be inverted.
	 */
	public void setInvertYAxis(boolean invert)
	{
		invertyaxis = invert;
	}
	
	/**
	 * Sets the background color.
	 * @param bgColor the background color
	 */
	public void setBackground(Color bgColor)
	{
		if (bgColor == null)
		{
			bgColor = UIManager.getColor("Panel.background");
			if (bgColor == null)
				bgColor = (new JPanel()).getBackground();
			if (bgColor == null)
				bgColor = Color.BLACK;
		}
			
		this.bgColor = bgColor;
		if (viewport != null)
			setBackground(bgColor);
	}
	
	/** 
	 * Sets the maximum distance for selecting objects.
	 * 
	 * @param maxDist selections distance
	 */
	public synchronized void setSelectorDistance(IVector1 maxDist)
	{
		selectorDistance = maxDist;
	}
	
	/** 
	 * Gets the maximum distance for selecting objects.
	 * 
	 * @return selections distance
	 */
	public synchronized IVector1 getSelectorDistance()
	{
		return selectorDistance;
	}
	
	/**
	 * Gets the display order.
	 * @return the display order
	 */
	public synchronized Comparator getDisplayOrder()
	{
		return displayorder;
	}
	
	/**
	 * Sets the display order.
	 * @param order the display order
	 */
	public synchronized void setDisplayOrder(Comparator order)
	{
		displayorder = order;
	}
	
	/**
	 * Gets the object shift.
	 * @return the object shift
	 */
	public synchronized IVector2 getObjectShift()
	{
		return objectShift.copy();
	}
	
	/**
	 * Sets the object shift.
	 * @param shift the object shift
	 */
	public synchronized void setObjectShift(IVector2 shift)
	{
		objectShift = shift.copy();
	}
	
	/**
	 * Gets the maximum zoom.
	 * @return the zoom limit
	 */
	public double getZoomLimit()
	{
		return zoomlimit;
	}
	
	/**
	 * Sets the maximum zoom.
	 * @param zoomlimit the zoom limit
	 */
	public void setZoomLimit(double zoomlimit)
	{
		this.zoomlimit = zoomlimit;
		if (viewport != null)
			viewport.setZoomLimit(zoomlimit);
	}
	
	/** Returns the current zoom stepping
	 *  @return the zoom stepping
	 */
	public double getZoomStepping()
	{
		return viewport.getAreaSize().copy().divide(viewport.getSize().copy().multiply(0.9)).getMean().getAsDouble() - getZoom();
	}
	
	/** Returns the zoom factor
	 *  @return the zoom factor
	 */
	public double getZoom()
	{
		return viewport.getAreaSize().copy().divide(viewport.getSize()).getMean().getAsDouble();
	}
	
	/** Sets a new zoom factor
	 *  @param zoom new zoom factor
	 */
	public void setZoom(double zoom)
	{
		IVector2 newSize = viewport.getAreaSize().copy().divide(new Vector2Double(zoom));
		IVector2 oldSize = viewport.getSize().copy();
		IVector2 pos = viewport.getPosition();
		viewport.setSize(newSize);
		pos.add(oldSize.subtract(newSize).multiply(0.5));
		viewport.setPosition(pos);
	}
	
	/**
	 * Shifts the viewport position.
	 * @param shift relative (to current zoom/size) shift vector
	 */
	public void shiftPosition(IVector2 shift)
	{
		if (invertxaxis)
			shift.negateX();
		if (invertyaxis)
			shift.negateY();
		IVector2 pos = viewport.getPosition().copy();
		pos.add(viewport.getSize().copy().multiply(shift));
		viewport.setPosition(pos);
	}
	
	/**
	 * Resets position and flushes render info
	 */
	public void reset()
	{
		flushRenderInfo();
		resetZoomAndPosition();
	}
	
	/**
	 * Resets the zoom and position.
	 */
	public void resetZoomAndPosition()
	{
//		viewport.setAreaSize(obscenter.getAreaSize());
		AbstractEnvironmentSpace space = obscenter.getSpace();
		if(space instanceof Space2D)
		{
			IVector2 tmpsize = ((Space2D)space).getAreaSize();
			viewport.setAreaSize(tmpsize);
		}
	}
	
	/**
	 *  Flushes the render information.
	 */
	public void flushRenderInfo()
	{
		synchronized(visuals)
		{
			for (Iterator it = visuals.values().iterator(); it.hasNext(); )
				((DrawableCombiner) it.next()).flushRenderInfo();
		}
		
		for (int i = 0; i < prelayers.length; ++i)
			prelayers[i].flushRenderInfo();
		
		for (int i = 0; i < postlayers.length; ++i)
			postlayers[i].flushRenderInfo();
	}
	
	/**
	 * Refreshes the perspective.
	 */
	public void refresh()
	{
		if(!rendering)
		{
			rendering	= true;
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					rendering	= false;
					IDataView dataview = obscenter.getSelectedDataView();
					if (dataview == null)
					{
						return;
					}

					viewport.setInvertX(invertxaxis);
					viewport.setInvertY(invertyaxis);
					viewport.setObjectShift(objectShift);
					
					// Set pre- and postlayers
					viewport.setPreLayers(prelayers);
					viewport.setPostLayers(postlayers);
					
					Object[] objects = dataview.getObjects();
	
					List objectList = null;
					objectList = new ArrayList(objects.length + 1);
					for (int j = 0; j < objects.length; ++j )
					{
						Object obj = objects[j];
						DrawableCombiner d = (DrawableCombiner) visuals.get(SObjectInspector.getType(obj));
						if (d == null)
						{
							continue;
						}
						Object[] viewObj = new Object[2];
						viewObj[0] = obj;
						viewObj[1] = d;
						objectList.add(viewObj);
					}
	
					if (selectedobject != null)
					{
						DrawableCombiner dc =(DrawableCombiner)visuals.get(SObjectInspector.getType(selectedobject));
						IVector2 size = (IVector2)dc.getBoundValue(selectedobject, dc.getSize(), viewport);
						Object[] viewObj = new Object[2];
						marker.setSize((IVector2) size);
						viewObj[0] = selectedobject;
						viewObj[1] = marker;
						objectList.add(viewObj);
					}
					else
					{
						setSelectedObject(null);
					}
	
					if (displayorder != null)
					{
						Collections.sort(objectList, displayorder);
					}
	
					viewport.setObjectList(objectList);
					viewport.refresh();
				}
			});
		}
	}
	
	private static final IViewport createViewport(IPerspective persp, ClassLoader cl, Color bgColor, boolean tryopengl)
	{
//		System.out.println("create viewport: "+Thread.currentThread());
		final JFrame frame = new JFrame("");
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.pack();
		frame.setSize(1, 1);
		
		if (tryopengl && OPENGL)
		{
			// Try OpenGL...
			try
			{
				Constructor con = Class.forName("jadex.extension.envsupport.observer.graphics.opengl.ViewportJOGL",
												true,
												Thread.currentThread().getContextClassLoader())
													.getConstructor(new Class[] {IPerspective.class, ClassLoader.class});
				IViewport vp =  (IViewport) con.newInstance(new Object[] {persp, cl});
				//new ViewportJOGL(persp, libService);
				frame.add(vp.getCanvas());
				frame.setVisible(true);
				if (Boolean.FALSE.equals(vp.getClass().getMethod("isValid", new Class[] {}).invoke(vp, new Object[0])))
				{
					System.err.println("OpenGL support insufficient, using Java2D fallback...");
					tryopengl = false;
				}
			}
			catch (ClassNotFoundException e)
			{
				tryopengl = false;
			}
			catch (NoSuchMethodException e)
			{
				tryopengl = false;
			}
			catch (InvocationTargetException e)
			{
				tryopengl = false;
			}
			catch (IllegalAccessException e)
			{
				tryopengl = false;
			}
			catch (InstantiationException e)
			{
				tryopengl = false;
			}
			catch (RuntimeException e)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				System.err.println(e);
				tryopengl = false;
			}
			catch (Error e)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				System.err.println(e);
				tryopengl = false;
			}
			finally
			{
//				System.out.println("dispose viewport frame: "+Thread.currentThread());
				frame.dispose();
			}
		}
		else
		{
//			System.out.println("dispose viewport frame2: "+Thread.currentThread());
			frame.dispose();			
		}
		
		IViewport viewport = null;
		if (tryopengl && OPENGL)
		{
			try
			{
				Constructor con = Class.forName("jadex.extension.envsupport.observer.graphics.opengl.ViewportJOGL",
					true,Thread.currentThread().getContextClassLoader())
						.getConstructor(new Class[] {IPerspective.class, ClassLoader.class});
				viewport = (IViewport) con.newInstance(new Object[] {persp, cl});
			}
			catch (Exception e0)
			{
			}
		}
		
		if (viewport == null)
		{
			viewport = new ViewportJ2D(persp, cl);
		}
		
		viewport.setBackground(bgColor);
		return viewport;
	}
	
	class SelectionController implements IViewportListener
	{
		public void leftClicked(IVector2 position)
		{
			IDataView dataview = obscenter.getSelectedDataView();
			if (dataview == null)
				return;
			position = position.copy().subtract(objectShift);
			
			IVector1 minDist = null;
			List closest = new LinkedList();
			Object[] objects = dataview.getObjects();
			for (int i = 0; i < objects.length; ++i)
			{
				DrawableCombiner dc = (DrawableCombiner)visuals.get(SObjectInspector.getType(objects[i]));
				if (dc == null)
					continue;
				IVector2 objPos = (IVector2)dc.getBoundValue(objects[i], dc.getPosition(), viewport); 
				if (objPos == null)
				{
					continue;
				}
				if((closest.isEmpty()) || (position.getDistance(objPos).less(minDist)))
				{
					closest.clear();
					closest.add(objects[i]);
					minDist = position.getDistance(objPos);
				}
				else if ((minDist != null) && (position.getDistance(objPos).equals(minDist)))
				{
					closest.add(objects[i]);
				}
			}
			
			if ((!closest.isEmpty()) && (minDist.less(selectorDistance)))
			{
				++selectCycle;
				selectCycle %= closest.size();
				setSelectedObject(closest.get(selectCycle));
			}
		}
	}
}
