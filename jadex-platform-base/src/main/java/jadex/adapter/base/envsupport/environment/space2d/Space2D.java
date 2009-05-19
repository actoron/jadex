package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 *  General 2D space.
 */
public abstract class Space2D extends AbstractEnvironmentSpace
{
	//-------- constants --------
	
	/** The constant for the position property. */
	public static final String POSITION = "position";
	
	/** Area size. */
	protected IVector2 areasize;
	
	//-------- constructors --------
	
	/**
	 * Initializes the 2D-Space.
	 * @param spaceexecutor executor for the space
	 * @param actionexecutor executor for agent actions
	 * @param areasize the size of the 2D area
	 */
	protected Space2D(IVector2 areasize)
	{
		this.areasize = areasize;
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
		synchronized(monitor)
		{
			return areasize.copy();
		}
	}
	
	/**
	 *  Set the area size.
	 *  @param areaSize The area size.
	 */
	public void setAreaSize(IVector2 areaSize)
	{
		synchronized(monitor)
		{
			areasize = areaSize;
		}
	}
	
	/**
	 *  Get the position of an object.
	 *  @param id The id.
	 *  @return The position.
	 * /
	public IVector2 getPosition(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return (IVector2)obj.getProperty(POSITION);
		}
	}*/
	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks, List listeners)
	{
		IVector2 pos = properties!=null ? (IVector2)properties.remove(Space2D.POSITION) : null;
		if(pos==null)	
			pos = getRandomPosition(Vector2Int.ZERO);
		
		ISpaceObject	ret	= super.createSpaceObject(typename, properties, tasks, listeners);
		
		if(pos!=null)
		{
			setPosition(ret.getId(), pos);
		}
		
		return ret;
		
		
	}

	/**
	 *  Set the position of an object.
	 *  @param id The object id.
	 *  @param pos The object position.
	 */
	public void setPosition(Object id, IVector2 pos)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			Object oldpos = obj.getProperty(POSITION);
			obj.setProperty(POSITION, pos);
			
			fireEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.OBJECT_POSITION_CHANGED, this, obj, oldpos));
		}
	}

	/**
	 *  Get the distance between two positions.
	 *  @param pos1	The first position.
	 *  @param pos2	The second position.
	 */
	// Todo: border properties (e.g. torus) for generic 2D space?
	public IVector1	getDistance(IVector2 pos1, IVector2 pos2)
	{
		synchronized(monitor)
		{
			return pos1.getDistance(pos2);
		}
	}
	
	/**
	 * Retrieves a random position within the simulation area with a minimum
	 * distance from the edge.
	 * @param distance minimum distance from the edge, null or zero for no distance
	 */
	public IVector2 getRandomPosition(IVector2 distance)
	{
		synchronized(monitor)
		{
			if (distance == null)
			{
				distance = Vector2Double.ZERO;
			}
			IVector2 position = areasize.copy();
			position.subtract(distance);
			position.randomX(distance.getX(), position.getX());
			position.randomY(distance.getY(), position.getY());
			return position;
		}
	}
	
	/**
	 * Returns the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxDist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 */
	public ISpaceObject getNearestObject(IVector2 position, IVector1 maxDist)
	{
		synchronized(monitor)
		{
			ISpaceObject nearest = null;
			IVector1 distance = null;
			synchronized(spaceobjects)
			{
				Set objects = spaceobjects.entrySet();
				for(Iterator it = objects.iterator(); it.hasNext();)
				{
					Map.Entry entry = (Entry)it.next();
					ISpaceObject curobj = (ISpaceObject)entry.getValue();
					if(curobj.getProperty(Space2D.POSITION)!=null)
					{
						IVector1 objdist = ((IVector2)curobj.getProperty(Space2D.POSITION)).getDistance(position); 
						if ((nearest == null) || (objdist.less(distance)))
						{
							nearest = curobj;
							distance = objdist;
						}
					}
				}
			}
			if((maxDist != null) && (distance != null) && (maxDist.less(distance)))
			{
				return null;
			}
			
			return nearest;
		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 maxdist)
	{
		synchronized(monitor)
		{
			List ret = new ArrayList();
		
			Set objects = spaceobjects.entrySet();
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
		
			return (ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
		}
	}
	
	public Object[] getSpaceObjects()
	{
		synchronized(monitor)
		{
			return spaceobjects.values().toArray();
		}
	}
}
