package jadex.adapter.base.envsupport.observer.gui.presentation;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.IViewport;
import jadex.adapter.base.envsupport.observer.graphics.IViewportListener;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.graphics.YOrder;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.theme.Theme2D;
import jadex.bridge.ILibraryService;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

public class Presentation2D implements IPresentation
{
	private static final Set SUPPORTED_THEME_TYPES = new HashSet();
	static
	{
		SUPPORTED_THEME_TYPES.add(Theme2D.class);
	}
	
	/** Name of the presentation */
	protected String name;
	
	/** The ObserverCenter */
	protected ObserverCenter obscenter;
	
	/** The viewport */
	protected IViewport viewport;
	
	/** Selection controller
	 */
	private SelectionController selectioncontroller;
	
	/** The current theme */
	protected Theme2D theme;
	
	/** The selected object */
	protected Object selectedobject;
	
	/** Order in which objects are displayed */
	private Comparator displayorder;
	
	/** The object shift */
	private IVector2 objectShift;
	
	/** Maximum selection distance */
	private IVector1 selectorDistance;
	
	/** Flag if the x-axis should be inverted */
	private boolean invertxaxis;
	
	/** Flag if the y-axis should be inverted */
	private boolean invertyaxis;
	
	/** Try OpenGL if true */
	private boolean tryopengl;
	
	/**
	 * Creates a 2D-Presentation.
	 */
	public Presentation2D()
	{
		this.objectShift = new Vector2Double();
		this.selectorDistance = new Vector1Double(1.0);
		this.tryopengl = true;
		
		this.displayorder = new YOrder();
		
		this.theme = new Theme2D();
		this.name = getClass().getName();
		viewport = null;
		selectioncontroller = new SelectionController();
	}
	
	/**
	 * Returns the name of the presentation
	 * @return name of the presentation
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the presentation
	 * @param name name of the presentation
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Returns supported theme types.
	 * 
	 * @return supported theme types
	 */
	public Set getSupportedThemeTypes()
	{
		return SUPPORTED_THEME_TYPES;
	}
	
	/**
	 * Sets the current theme.
	 * @param theme the new theme 
	 */
	public void setTheme(Object theme)
	{
		if ((!SUPPORTED_THEME_TYPES.contains(theme.getClass())) ||
			(theme == null))
		{
			theme = new Theme2D();
		}
		this.theme = (Theme2D) theme;
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
	}
	
	/**
	 * Sets the ObserverCenter.
	 * @param obscenter the ObserverCenter
	 */
	public void setObserverCenter(ObserverCenter obscenter)
	{
		this.obscenter = obscenter;
	}
	
	/**
	 * Gets the view of the presentation.
	 * @return the view
	 */
	public Component getView()
	{
		if (viewport == null)
		{
			viewport = createViewport(obscenter.getLibraryService(), tryopengl);
			viewport.setSize(((Space2D)(obscenter.getSpace())).getAreaSize().copy());
			viewport.addViewportListener(selectioncontroller);
		}
		return viewport.getCanvas();
	}
	
	/**
	 * Sets whether to try to use OpenGL.
	 * @param opengl true, if attempt should be made to use OpenGL
	 */
	public void setOpenGl(boolean opengl)
	{
		this.tryopengl = opengl;
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
	 * @returns selections distance
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
		return objectShift;
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
	 * Refreshes the presentation.
	 */
	public void refresh()
	{
		if (obscenter.getSpace() == null)
		{
			return;
		}
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				viewport.setInvertX(invertxaxis);
				viewport.setInvertY(invertyaxis);
				viewport.setObjectShift(objectShift);
				
				// Set pre- and postlayers
				IEnvironmentSpace space = obscenter.getSpace();
				ILayer[] preLayers = theme.getPrelayers();
				ILayer[] postLayers = theme.getPostlayers();
				viewport.setPreLayers(preLayers);
				viewport.setPostLayers(postLayers);
				
				List viewnames = space.getViewNames();
				if(viewnames!=null)
				{
					for(int i=0; i<viewnames.size(); i++)
					{
						Object[] objects = space.getView((String)viewnames.get(i)).getObjects();
						
						List objectList = null;
						objectList = new ArrayList(objects.length + 1);
						for (int j = 0; j < objects.length; ++j )
						{
							ISpaceObject obj = (ISpaceObject) objects[j];
							DrawableCombiner d = theme.getDrawableCombiner(obj.getType());
							if (d == null)
							{
								continue;
							}
							IVector2 position = (IVector2) obj.getProperty("position");
							if (position == null)
							{
								continue;
							}
							Object[] viewObj = new Object[3];
							viewObj[0] = position.copy();
							IVector2 vel = ((IVector2) obj.getProperty("velocity"));
							if (vel != null)
							{
								viewObj[1] = vel.copy();
							}
							viewObj[2] = d;
							objectList.add(viewObj);
						}
						
						ISpaceObject mObj = null;
						if (selectedobject != null)
						{
							mObj = (ISpaceObject) space.getSpaceObject(selectedobject);
						}
						if (mObj != null)
						{
							IVector2 size = theme.getDrawableCombiner(mObj.getType()).getSize().copy();
							size.multiply(2.0);
							Object[] viewObj = new Object[3];
							DrawableCombiner marker = theme.getMarkerDrawCombiner();
							marker.setDrawableSizes(size);
							viewObj[0] = mObj.getProperty("position");
							viewObj[2] = marker;
							objectList.add(viewObj);
						}
						else
						{
							selectedobject = null;
						}
						
						if (displayorder != null)
						{
							Collections.sort(objectList, displayorder);
						}
						
						viewport.setObjectList(objectList);
						viewport.refresh();
					}
				}
			}
		});
	}
	
	private IViewport createViewport(ILibraryService libService, boolean tryopengl)
	{
		final JFrame frame = new JFrame("");
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.pack();
		frame.setSize(1, 1);
		
		if (tryopengl)
		{
			// Try OpenGL...
			try
			{
				ViewportJOGL vp = new ViewportJOGL(libService);
				frame.add(vp.getCanvas());
				frame.setVisible(true);
				if (!vp.isValid())
				{
					System.err.println("OpenGL support insufficient, using Java2D fallback...");
					tryopengl = false;
				}
			}
			catch (RuntimeException e0)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				System.err.println(e0);
				tryopengl = false;
			}
			catch (Error e1)
			{
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
				System.err.println(e1);
				tryopengl = false;
			}
		}
		
		IViewport viewport = null;
		if (tryopengl)
		{
			viewport = new ViewportJOGL(libService);
		}
		else
		{
			viewport = new ViewportJ2D(libService);
		}
		return viewport;
	}
	
	private class SelectionController implements IViewportListener
	{
		public void leftClicked(IVector2 position)
		{
			position = position.copy().subtract(objectShift);
			((Space2D) obscenter.getSpace()).getNearestObject(position, selectorDistance);
			ISpaceObject obj = ((Space2D) obscenter.getSpace()).getNearestObject(position, selectorDistance);
			selectedobject = obj.getId();
		}
		
		public void rightClicked(IVector2 position)
		{
		}
	}
}
