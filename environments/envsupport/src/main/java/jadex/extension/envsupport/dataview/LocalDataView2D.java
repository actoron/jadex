package jadex.extension.envsupport.dataview;

import java.util.Map;

import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

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
	protected Object range;
	
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
			ISpaceObject[] ret;
			IVector2 pos = (IVector2)object.getProperty(Space2D.PROPERTY_POSITION);
			if(range instanceof IVector1)
				ret = (ISpaceObject[])space.getNearObjects(pos, (IVector1)range).toArray(new ISpaceObject[0]);
//			else if(range instanceof IVector2)
//				ret = space.getNearObjects(pos, (IVector2)range, null);
			else
				throw new RuntimeException("Range must be vector1: "+range);
			return ret;
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
		range	= props.get("range");
		if(range instanceof Number)
			range	= new Vector1Double(((Number)props.get("range")).doubleValue());
		
	}
}
