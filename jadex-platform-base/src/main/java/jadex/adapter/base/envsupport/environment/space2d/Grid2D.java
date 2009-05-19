package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.Collection;
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
		this(null, null);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param actionexecutor executor for agent actions
	 * @param areaSize the size of the 2D area
	 */
	public Grid2D(IVector2 areaSize)
	{
		this(areaSize, DEFAULT_NAME);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * 
	 * @param actionexecutor executor for agent actions
	 * @param areaSize the size of the 2D area
	 * @param spaceName the name of this space
	 */
	public Grid2D(IVector2 areaSize, Object spaceName)
	{
		super(areaSize);
		this.setProperty("name", spaceName);
		this.objectsygridpos = new MultiCollection();
		this.border_mode = BORDER_TORUS;
	}
	
	//-------- grid specific methods --------
	
	/**
	 * Get all SimObjects at a specific grid position
	 * /
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position)
	{
		synchronized(monitor)
		{
			return getSpaceObjectsByGridPosition(position, null);
		}
	}*/

	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 * /
	public ISpaceObject[] getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		synchronized(monitor)
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
	 *  Get a position on the grid.
	 *  Applies torus or strict border settings as necessary.
	 *  @param position	The position.
	 *  @return	The position converted to a correct grid position (if necessary).
	 */
	public IVector2	getGridPosition(IVector2 position)
	{
		IVector2 ret	= position;
		if(border_mode==BORDER_TORUS)
		{
			int sizex = areasize.getXAsInteger();
			int sizey = areasize.getYAsInteger();
			ret	= new Vector2Int((position.getXAsInteger()%sizex+sizex)%sizex,
				(position.getYAsInteger()%sizey+sizey)%sizey);
		}
		
		return ret;
	}
	
	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		synchronized(monitor)
		{
			if(position!=null)
				position	= getGridPosition(position);
			Collection ret = null;
			Collection simobjs = objectsygridpos.getCollection(position);
			if(null == type)
			{
				ret = simobjs;
			}
			else
			{
				ArrayList tmp = new ArrayList();
				for(Iterator objs = simobjs.iterator(); objs.hasNext();)
				{
					ISpaceObject curobj = (ISpaceObject)objs.next();
					if(type.equals(curobj.getType()))
					{
						tmp.add(curobj);
					}
				}
				if(tmp.size()>0)
					ret = tmp;
			}
			
//			System.out.println("getSpaceObs: "+position+" "+type+" "+ret+" "+simobjs);
			
			return ret;
		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return {@link ISpaceObject}[] 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 distance)
	{
		synchronized(monitor)
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
		synchronized(monitor)
		{
			IVector2 ret = null;

			// Problem of efficiently finding an empty field
			// Enumerating all empty fields would cause big
			// memory consumption on big grids
			
			if(objectsygridpos.keySet().size()<areasize.getYAsInteger()*areasize.getXAsInteger())
			{
				// first try n times random value 
				int n = 5;
				for(int i=0; i<n && ret==null; i++)
				{
					ret = new Vector2Int(getRandomPosition(Vector2Int.ZERO));
					if(objectsygridpos.containsKey(ret))
						ret = null;
				}
				
				// Just find first empty field
				if(ret==null)
				{
					for(int y=0; y<areasize.getYAsInteger(); y++)
					{
						for(int x=0; x<areasize.getXAsInteger(); x++)
						{
							ret = new Vector2Int(x, y);
							if(objectsygridpos.containsKey(ret))
								ret = null;
						}
					}
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
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id);
			
			IVector2 oldpos = (IVector2)obj.getProperty(POSITION);
			if(objectsygridpos.containsKey(oldpos))
				objectsygridpos.remove(oldpos, obj);
			
			if(pos!=null)
			{
				if(BORDER_TORUS==border_mode)
				{
					int sizex = areasize.getXAsInteger();
					int sizey = areasize.getYAsInteger();
					
					int x = pos.getXAsInteger();
					int y = pos.getYAsInteger();
					
					while(x<0)
						x += sizex;
					while(y<0)
						y += sizey;
					
					x = x % sizex;
					y = y % sizey;
					
					pos = new Vector2Int(x, y);
				}
			
				objectsygridpos.put(pos, obj);
//				gridposbyobject.put(id, pos);
			}
			
			super.setPosition(id, pos);
		}
	}
	
	/**
	 *  Get the distance between two positions.
	 *  @param pos1	The first position.
	 *  @param pos2	The second position.
	 */
	public IVector1	getDistance(IVector2 pos1, IVector2 pos2)
	{
		synchronized(monitor)
		{
			IVector1	ret;
			
			if(BORDER_TORUS==border_mode)
			{
				int	x1	= pos1.getXAsInteger();
				int	y1	= pos1.getYAsInteger();
				int	x2	= pos2.getXAsInteger();
				int	y2	= pos2.getYAsInteger();
				int sizex	= areasize.getXAsInteger();
				int sizey	= areasize.getYAsInteger();
				
				int dx	= x1<x2	? Math.min(x2-x1, x1+sizex-x2) : Math.min(x1-x2, x2+sizex-x1);
				int dy	= y1<y2	? Math.min(y2-y1, y1+sizey-y2) : Math.min(y1-y2, y2+sizey-y1);
				
				ret	= new Vector1Double(Math.sqrt((dx * dx) + (dy * dy)));
			}
			else
			{
				ret	= pos1.getDistance(pos2);
			}
			
			return ret;
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
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks, List listeners)
	{
		synchronized(monitor)
		{
			// TODO: maybe only assign position to discretePosition vector?
			
			ISpaceObject obj = super.createSpaceObject(typename, properties, tasks, listeners);

			IVector2 pos = properties==null || !properties.containsKey(Space2D.POSITION)
				? getEmptyGridPosition(): (IVector2)properties.get(Space2D.POSITION);
			if(pos==null)	// No empty position. todo: fail!?
				pos = getRandomPosition(Vector2Int.ZERO);
			
			// Hack! todo: remove this
			if(properties!=null)
				properties.remove(Space2D.POSITION);
			setPosition(obj.getId(), pos);

			return obj;
		}
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Object id)
	{
		try
		{
			synchronized(monitor)
			{
				// remove the object from grid
				IVector2 pos = (IVector2)getSpaceObject(id).getProperty(Space2D.POSITION);
				if(pos!=null)
					objectsygridpos.remove(pos, spaceobjects.get(id));
				
				super.destroySpaceObject(id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
