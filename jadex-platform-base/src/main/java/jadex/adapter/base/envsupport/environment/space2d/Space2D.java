package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.EnvironmentSpaceTime;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IClockService;


public abstract class Space2D extends EnvironmentSpaceTime
{
	/** Area size. */
	protected IVector2		areaSize_;
	
	/**
	 * Initializes the TimeSpace.
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 */
	protected Space2D(IClockService clockService, IVector1 timeCoefficient, IVector2 areaSize)
	{
		super(clockService, timeCoefficient);
		areaSize_ = areaSize;
	}
	
	/**
	 * Retrieves a random position within the simulation area with a minimum
	 * distance from the edge.
	 * 
	 * @param distance minimum distance from the edge, null or zero for no distance
	 */
	public abstract IVector2 getRandomPosition(IVector2 distance);
	
	/**
	 * Returns the ID of the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxDist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 */
	public abstract Long getNearestObjectId(IVector2 position, IVector1 maxDist);
	
	/**
	 * Returns the size of the simulated area.
	 * 
	 * @return size of the simulated area
	 */
	public IVector2 getAreaSize()
	{
		return areaSize_.copy();
	}
	
	/**
	 *  Set the area size.
	 *  @param areaSize The area size.
	 */
	public void setAreaSize(IVector2 areaSize)
	{
		areaSize_ = areaSize;
	}
	
	/**
	 *  Get the position of an object.
	 *  @param id The id.
	 *  @return The position.
	 */
	public IVector2 getPosition(Object id)
	{
		IVector2 ret = null;
		ISpaceObject obj = getSpaceObject((Long)id);  // Hack change id to object?!
		if(obj!=null)
			ret = (IVector2)obj.getProperty("position");
		return ret;
	}
}
