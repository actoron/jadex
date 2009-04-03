package jadex.adapter.base.envsupport.environment.view;

import java.util.List;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;

/**
 * A general 2D view that presents all Objects in a Space2D to the observer.
 *
 */
public class GeneralView2D implements IView
{
	/** The space the view is connected to */
	protected Space2D space;
	
	/** The current objects */
	protected Object[] objects;
	
	/** Flag whether the view needs updating */
	protected boolean dirty;
	
	/** Internal lock */
	protected Object monitor;
	
	/**
	 * Creates a general 2D view that presents all Objects in a Space2D to the observer.
	 */
	public GeneralView2D()
	{
		monitor = new Object();
		objects = new Object[0];
		dirty = false;
	}
	
	/**
	 * Returns the type of the view.
	 * @return type of the view
	 */
	public String getType()
	{
		return IView.SIMPLE_VIEW_2D;
	}
	
	/**
	 * Returns a list of objects in this view
	 * @return list of objects
	 */
	public Object[] getObjects()
	{
		synchronized (monitor)
		{
			if (dirty)
			{
				Space2D spc = (Space2D) space;
				objects = spc.getSpaceObjects();
				dirty = false;
			}
			return objects;
		}
	}
	
	/**
	 *  Updates the view.
	 *  
	 *  @param space the space of the view
	 */
	public void update(IEnvironmentSpace space)
	{
		synchronized(monitor)
		{
			dirty = true;
		}
	}
}
