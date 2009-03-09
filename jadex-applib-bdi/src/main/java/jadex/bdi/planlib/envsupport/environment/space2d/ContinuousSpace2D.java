package jadex.bdi.planlib.envsupport.environment.space2d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.bdi.planlib.envsupport.environment.ISpaceObject;
import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bdi.planlib.envsupport.math.IVector2;
import jadex.bdi.planlib.envsupport.math.SynchronizedVector2Wrapper;
import jadex.bdi.planlib.envsupport.math.Vector2Double;

public class ContinuousSpace2D extends Space2D
{
	/** The default ID for this space */
	public static final Object DEFAULT_ID = ContinuousSpace2D.class;
	
	/** The space's ID */
	private Object id_;
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default ID.
	 * 
	 * @param startTime the start time
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 */
	public ContinuousSpace2D(long startTime, IVector1 timeCoefficient, IVector2 areaSize)
	{
		this(startTime, timeCoefficient, areaSize, DEFAULT_ID);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * 
	 * @param startTime the start time
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 * @param spaceId the ID of this space
	 */
	public ContinuousSpace2D(long startTime,
							 IVector1 timeCoefficient,
							 IVector2 areaSize,
							 Object spaceId)
	{
		super(startTime, timeCoefficient, areaSize);
		id_ = spaceId;
	}
	
	// FIXME
	/** 
	 * Adds an object to this space.
	 * 
	 * @param objectId the object's ID
	 */
	/*public void addEnvironmentObject(Long objectId, Map properties, List tasks, List listeners)
	{
		if (properties == null)
		{
			properties = new HashMap();
		}
		
		if (!properties.containsKey("position"))
		{
			properties.put("position", new SynchronizedVector2Wrapper(getRandomPosition(Vector2Double.ZERO)));
		}
		else
		{
			IVector2 position = (IVector2) properties.get("position");
			if (!(position instanceof SynchronizedVector2Wrapper))
			{
				properties.put("position", new SynchronizedVector2Wrapper(position));
			}
		}
		
		super.addEnvironmentObject(objectId, properties, tasks, listeners);
	}*/
	
	/**
	 * Retrieves a random position within the simulation area with a minimum
	 * distance from the edge.
	 * 
	 * @param distance minimum distance from the edge, null or zero for no distance
	 */
	public IVector2 getRandomPosition(IVector2 distance)
	{
		if (distance == null)
		{
			distance = Vector2Double.ZERO;
		}
		IVector2 position = areaSize_.copy();
		position.subtract(distance);
		position.randomX(distance.getX(), position.getX());
		position.randomY(distance.getY(), position.getY());
		return position;
	}
	
	/**
	 * Returns the ID of the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxDist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 */
	public Long getNearestObjectId(IVector2 position, IVector1 maxDist)
	{
		Long nearest = null;
		IVector1 distance = null;
		synchronized(spaceObjects_)
		{
			Set objects = spaceObjects_.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject currentObj = (ISpaceObject) entry.getValue();
				IVector1 objDist = ((IVector2) currentObj.getProperty("position")).getDistance(position); 
				if ((nearest == null) || (objDist.less(distance)))
				{
					nearest = (Long) entry.getKey();
					distance = objDist;
				}
			}
		}
		if ((maxDist != null) && (distance != null) && (maxDist.less(distance)))
		{
			return null;
		}
		
		return nearest;
	}
	
	/**
	 * Returns the space's ID.
	 * 
	 * @return the space's ID.
	 */
	public Object getId()
	{
		return id_;
	}
}
