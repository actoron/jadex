package jadex.adapter.base.envsupport.environment.space2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.adapter.base.envsupport.environment.EnvironmentSpaceTime;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.bridge.IClockService;

/**
 * 
 */
public abstract class Space2D extends EnvironmentSpaceTime
{
	//-------- constants --------
	
	/** The constant for the position property. */
	public static final String POSITION = "position";
	
	/** Area size. */
	protected IVector2 areaSize_;
	
	//-------- constructors --------
	
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
	
	//-------- methods --------
	
	/**
	 * Retrieves a random position within the simulation area with a minimum
	 * distance from the edge.
	 * 
	 * @param distance minimum distance from the edge, null or zero for no distance
	 */
//	public abstract IVector2 getRandomPosition(IVector2 distance);
	
	/**
	 * Returns the ID of the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxDist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 */
//	public abstract ISpaceObject getNearestObject(IVector2 position, IVector1 maxDist);
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
//	public abstract ISpaceObject[] getNearObjects(IVector2 position, IVector1 distance);
	
	/**
	 * Returns the size of the simulated area.
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
		ISpaceObject obj = getSpaceObject(id); 
		if(obj==null)
			throw new RuntimeException("Space object not found: "+id);
		return (IVector2)obj.getProperty(POSITION);
	}
	
	/**
	 *  Set the position of an object.
	 *  @param id The object id.
	 *  @param pos The object position.
	 */
	public void setPosition(Object id, IVector2 pos)
	{
		ISpaceObject obj = getSpaceObject(id); 
		if(obj==null)
			throw new RuntimeException("Space object not found: "+id);
		obj.setProperty(POSITION, pos);
	}
	
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
	public ISpaceObject getNearestObject(IVector2 position, IVector1 maxDist)
	{
		ISpaceObject nearest = null;
		IVector1 distance = null;
		synchronized(spaceObjects_)
		{
			Set objects = spaceObjects_.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject currentObj = (ISpaceObject) entry.getValue();
				IVector1 objDist = ((IVector2) currentObj.getProperty(Space2D.POSITION)).getDistance(position); 
				if ((nearest == null) || (objDist.less(distance)))
				{
					nearest = currentObj;
					distance = objDist;
				}
			}
		}
		if((maxDist != null) && (distance != null) && (maxDist.less(distance)))
		{
			return null;
		}
		
		return nearest;
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 maxdist)
	{
		List ret = new ArrayList();
		
		synchronized(spaceObjects_)
		{
			Set objects = spaceObjects_.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject obj = (ISpaceObject)entry.getValue();
				IVector1 dist = ((IVector2)obj.getProperty(Space2D.POSITION)).getDistance(position); 
				if(dist.less(maxdist) || dist.equals(maxdist))
				{
					ret.add(obj);
				}
			}
		}
		
		return (ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
	}
}
