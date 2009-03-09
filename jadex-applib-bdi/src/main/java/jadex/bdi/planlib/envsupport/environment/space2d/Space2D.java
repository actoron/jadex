package jadex.bdi.planlib.envsupport.environment.space2d;

import jadex.bdi.planlib.envsupport.environment.EnvironmentSpaceTime;
import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bdi.planlib.envsupport.math.IVector2;


public abstract class Space2D extends EnvironmentSpaceTime
{
	/** Area size. */
	protected IVector2		areaSize_;
	
	/**
	 * Initializes the TimeSpace.
	 * @param startTime the start time
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 */
	protected Space2D(long startTime, IVector1 timeCoefficient, IVector2 areaSize)
	{
		super(startTime, timeCoefficient);
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
}
