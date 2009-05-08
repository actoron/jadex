package jadex.adapter.base.envsupport.dataview;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;

/**
 *  A view showing only objects in a local range.
 */
public class LocalDataView2D implements IDataView
{
	//-------- attributes --------
	
	/** The space. */
	protected Space2D	space;
	
	/** The space object, which is the center of the view. */
	protected ISpaceObject	object;
	
	/** The range of the view. */
	protected IVector1	range;
	
	//-------- IDataView interface --------
	
	/**
	 * Returns the type of the view.
	 * @return type of the view
	 */
	public String getType()
	{
		return SIMPLE_VIEW_2D;
	}
	
	/**
	 * Returns a list of objects in this view
	 * @return list of objects
	 */
	public Object[] getObjects()
	{
		synchronized(space.getMonitor())
		{
			IVector2	pos	= (IVector2)object.getProperty(Space2D.POSITION);
			return space.getNearObjects(pos, range);
		}
	}
	
	/**
	 *  Updates the view.
	 *  
	 *  @param space the space of the view
	 */
	public void update(IEnvironmentSpace space)
	{
		this.space	= (Space2D)space;
	}

	/**
	 *  Set the space of this view.
	 */
	public void init(IEnvironmentSpace space, Map props)
	{
		this.space	= (Space2D)space;
		object	= (ISpaceObject)props.get("object");
		range	= new Vector1Int(((Number)props.get("range")).intValue());
	}
}
