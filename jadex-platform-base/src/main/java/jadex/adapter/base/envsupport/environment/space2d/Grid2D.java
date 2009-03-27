package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  2D grid environment.
 */
public class Grid2D extends Space2D
{
	//-------- constants --------

	/** The default ID for this space */
	public static final String DEFAULT_NAME = Grid2D.class.getName();
	
	//todo: rename in boder behaviour
	/** Euclidean plane behavior */
	public static final int AREA_BEHAVIOR_EUCLID = 0;

	/** Torus behavior */
	public static final int AREA_BEHAVIOR_TORUS = 1;

	//-------- attributes --------
	
	/** The behavior of the world */
	public int area_behavior_ = AREA_BEHAVIOR_TORUS;
	
	/** All simobject id's accessible per position. */
	protected MultiCollection spaceObjectsByGridPosition_;
	
	/** Last known discrete position of a simobject */
	protected Map gridPositionBySpaceObjectId_;
	
	//-------- constructors --------
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 */
	public Grid2D()
	{
		this(null, null, null);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 */
	public Grid2D(IClockService clockService, IVector1 timeCoefficient, IVector2 areaSize)
	{
		this(clockService, timeCoefficient, areaSize, DEFAULT_NAME);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * 
	 * @param clockService the clock service
	 * @param timeCoefficient the time coefficient for time differences.
	 * @param areaSize the size of the 2D area
	 * @param spaceName the name of this space
	 */
	public Grid2D(IClockService clockService, IVector1 timeCoefficient, IVector2 areaSize, Object spaceName)
	{
		super(clockService, timeCoefficient, areaSize);
		spaceProperties_.put("name", spaceName);
		this.spaceObjectsByGridPosition_ = new MultiCollection();
		this.gridPositionBySpaceObjectId_ = new HashMap();
	}
	
	//-------- grid specific methods --------
	
	/**
	 * Get all SimObjects at a specific grid position
	 */
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position)
	{
		return getSpaceObjectsByGridPosition(position, null);
	}

	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		ISpaceObject[] ret = null;
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				synchronized(spaceObjectsByGridPosition_)
				{
					Collection simobjs = spaceObjectsByGridPosition_.getCollection(position);
					if (null == type)
					{
						ret = (ISpaceObject[])simobjs.toArray(new ISpaceObject[simobjs.size()]);
					}
					else
					{
						List l = new ArrayList();
						for (Iterator objs = simobjs.iterator(); objs.hasNext();)
						{
							ISpaceObject currentObj = (ISpaceObject)objs.next();
//							synchronized(currentObj)
//							{
								if (type.equals(currentObj.getType()))
								{
									l.add(currentObj);
								}
//							}
							
						}
						ret = (ISpaceObject[])l.toArray(new ISpaceObject[l.size()]);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return {@link SimObject}[] 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 distance)
	{
		Collection ret = new ArrayList();
		
		int sizex = areaSize_.getXAsInteger();
		int sizey = areaSize_.getYAsInteger();

		int x = position.getXAsInteger();
		int y = position.getYAsInteger();
		
		int range = distance.getAsInteger();

		synchronized(spaceObjectsByGridPosition_)
		{
			switch(area_behavior_)
			{
				case AREA_BEHAVIOR_TORUS:
				{
					for (int i = x - range; i <= x + range; i++)
					{
						for (int j = y - range; j <= y + range; j++)
						{
							Collection tmp = spaceObjectsByGridPosition_.getCollection(
									new Vector2Int((i + sizex) % sizex, (j + sizey) % sizey));
							if (tmp != null)
								ret.addAll(tmp);
						}
					}
				}
					break;

				case AREA_BEHAVIOR_EUCLID:
				{
					int minx = (x - range >= 0 ? x - range : 0);
					int maxx = (x + range <= sizex ? x + range : sizex);

					int miny = (y - range >= 0 ? y - range : 0);
					int maxy = (y + range <= sizey ? y + range : sizey);

					for (int i = minx; i <= maxx; i++)
					{
						for (int j = miny; j <= maxy; j++)
						{
							Collection tmp = spaceObjectsByGridPosition_.getCollection(
									new Vector2Int((i + sizex) % sizex, (j + sizey) % sizey));
							if (tmp != null)
								ret.addAll(tmp);
						}
					}
				}
					break;

				default:
					// can't happen
					break;
			}
		}

		return(ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
	}
	
	/**
	 *  Get an empty position in the grid.
	 *  @return Empty {@link IVector2} position.
	 */
	public IVector2 getEmptyGridPosition()
	{
		IVector2 ret = null;
		
		synchronized(spaceObjectsByGridPosition_)
		{
			while (ret == null)
			{
				ret = new Vector2Int(getRandomPosition(Vector2Int.ZERO));
				if(spaceObjectsByGridPosition_.containsKey(ret))
				{
					ret = null;
				}
			}
		}
		
		return ret;
	}
	
	//-------- overridings --------
	
	/**
	 *  Set the position of an object.
	 *  @param id The object id.
	 *  @param pos The object position.
	 */
	public void setPosition(Object id, IVector2 pos)
	{
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				synchronized(spaceObjectsByGridPosition_)
				{
					synchronized(gridPositionBySpaceObjectId_)
					{
						ISpaceObject obj = getSpaceObject(id);
						IVector2 oldpos = (IVector2)obj.getProperty(POSITION);
						if(oldpos!=null)
							spaceObjectsByGridPosition_.remove(oldpos, obj);
						
						spaceObjectsByGridPosition_.put(pos, obj);
						gridPositionBySpaceObjectId_.put(id, pos);
					}
				}
			}
		}
		super.setPosition(id, pos);
	}
	
	/** 
	 * Creates an object in this space.
	 * 
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public Object createSpaceObject(Object type, Map properties, List tasks, List listeners)
	{
		Object id;
		
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				synchronized(spaceObjectsByGridPosition_)
				{
					synchronized(gridPositionBySpaceObjectId_)
					{
						// TODO: maybe only assign position to discretePosition vector?
						
						id = super.createSpaceObject(type, properties, tasks, listeners);
						
						if(properties!=null)
						{
							IVector2 pos = (IVector2)properties.get(POSITION);
							if(pos!=null)
							{
								spaceObjectsByGridPosition_.put(pos, getSpaceObject(id));
								gridPositionBySpaceObjectId_.put(id, pos);
							}
						}
					}
				}
			}
		}
		return id;
	}
	
	/** 
	 * Destroys an object in this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Object id)
	{
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				synchronized (spaceObjectsByGridPosition_)
				{
					synchronized (gridPositionBySpaceObjectId_)
					{
						// remove the object from grid
						IVector2 pos = (IVector2)gridPositionBySpaceObjectId_.remove(id);
						if(pos!=null)
							spaceObjectsByGridPosition_.remove(pos, spaceObjects_.get(id));
						
						super.destroySpaceObject(id);
					}
				}
			}
		}
	}
}
