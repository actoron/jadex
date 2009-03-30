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
	
	/** Border strict mode. */
	public static final int BORDER_STRICT = 0;

	/** Border torus behavior. */
	public static final int BORDER_TORUS = 1;

	//-------- attributes --------
	
	/** The behavior of the world */
	public int border_mode;
	
	/** All simobject id's accessible per position. */
	protected MultiCollection objectsygridpos;
	
	/** Last known discrete position of a simobject */
	protected Map gridposbyobject;
	
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
		this.spaceproperties.put("name", spaceName);
		this.objectsygridpos = new MultiCollection();
		this.gridposbyobject = new HashMap();
		this.border_mode = BORDER_TORUS;
	}
	
	//-------- grid specific methods --------
	
	/**
	 * Get all SimObjects at a specific grid position
	 * /
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			return getSpaceObjectsByGridPosition(position, null);
		}
	}*/

	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 * /
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			ISpaceObject[] ret = null;
			Collection simobjs = objectsygridpos.getCollection(position);
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
					if (type.equals(currentObj.getType()))
					{
						l.add(currentObj);
					}
				}
				ret = (ISpaceObject[])l.toArray(new ISpaceObject[l.size()]);
			}
			return ret;
		}
	}*/
	
	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			Collection ret = null;
			Collection simobjs = objectsygridpos.getCollection(position);
			if(null == type)
			{
				ret = simobjs;
			}
			else
			{
				ret = new ArrayList();
				for (Iterator objs = simobjs.iterator(); objs.hasNext();)
				{
					ISpaceObject currentObj = (ISpaceObject)objs.next();
					if (type.equals(currentObj.getType()))
					{
						ret.add(currentObj);
					}
				}
			}
			return ret;
		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return {@link SimObject}[] 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 distance)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			Collection ret = new ArrayList();
			
			int sizex = areasize.getXAsInteger();
			int sizey = areasize.getYAsInteger();
	
			int x = position.getXAsInteger();
			int y = position.getYAsInteger();
			
			int range = distance.getAsInteger();
	
			if(border_mode==BORDER_TORUS)
			{
				for (int i = x - range; i <= x + range; i++)
				{
					for (int j = y - range; j <= y + range; j++)
					{
						Collection tmp = objectsygridpos.getCollection(
								new Vector2Int((i + sizex) % sizex, (j + sizey) % sizey));
						if (tmp != null)
							ret.addAll(tmp);
					}
				}
			}
			else if(border_mode==BORDER_STRICT)
			{
				int minx = (x - range >= 0 ? x - range : 0);
				int maxx = (x + range <= sizex ? x + range : sizex);

				int miny = (y - range >= 0 ? y - range : 0);
				int maxy = (y + range <= sizey ? y + range : sizey);

				for (int i = minx; i <= maxx; i++)
				{
					for (int j = miny; j <= maxy; j++)
					{
						Collection tmp = objectsygridpos.getCollection(
								new Vector2Int((i + sizex) % sizex, (j + sizey) % sizey));
						if (tmp != null)
							ret.addAll(tmp);
					}
				}
			}
			
			return(ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
		}
	}
	
	/**
	 *  Get an empty position in the grid.
	 *  @return Empty {@link IVector2} position.
	 */
	public IVector2 getEmptyGridPosition()
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			IVector2 ret = null;
			while (ret == null)
			{
				ret = new Vector2Int(getRandomPosition(Vector2Int.ZERO));
				if(objectsygridpos.containsKey(ret))
				{
					ret = null;
				}
			}
			return ret;
		}
	}
	
	//-------- overridings --------
	
	/**
	 *  Set the position of an object.
	 *  @param id The object id.
	 *  @param pos The object position.
	 */
	public void setPosition(Object id, IVector2 pos)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			ISpaceObject obj = getSpaceObject(id);
			IVector2 oldpos = (IVector2)obj.getProperty(POSITION);
			if(oldpos!=null)
				objectsygridpos.remove(oldpos, obj);
			
			objectsygridpos.put(pos, obj);
			gridposbyobject.put(id, pos);
			super.setPosition(id, pos);
		}
	}
	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public Object createSpaceObject(Object type, Map properties, List tasks, List listeners)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			// TODO: maybe only assign position to discretePosition vector?
			
			Object id = super.createSpaceObject(type, properties, tasks, listeners);
			
			if(properties!=null)
			{
				IVector2 pos = (IVector2)properties.get(POSITION);
				if(pos!=null)
				{
					objectsygridpos.put(pos, getSpaceObject(id));
					gridposbyobject.put(id, pos);
				}
			}
			return id;
		}
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Object id)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			// remove the object from grid
			IVector2 pos = (IVector2)gridposbyobject.remove(id);
			if(pos!=null)
				objectsygridpos.remove(pos, spaceobjects.get(id));
			
			super.destroySpaceObject(id);
		}
	}
}
