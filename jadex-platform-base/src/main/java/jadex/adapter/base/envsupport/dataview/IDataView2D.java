package jadex.adapter.base.envsupport.dataview;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;

/** 
 * 2D-specific extension of a dataview.
 */
public interface IDataView2D extends IDataView
{
	/**
	 * Returns the ID of the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxDist maximum distance from the position, use null for unlimited distance
	 * @return the ID of nearest object or null if none is found
	 */
	public Object getNearestObjectId(IVector2 position, IVector1 maxDist);
}
