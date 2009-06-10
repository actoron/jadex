package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.adapter.base.envsupport.math.Vector2Int;
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
		this(null, null, BORDER_TORUS);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param actionexecutor executor for agent actions
	 * @param areasize the size of the 2D area
	 */
	public Grid2D(IVector2 areasize)
	{
		this(DEFAULT_NAME, areasize, BORDER_TORUS);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * @param name the name of this space
	 * @param areasize the size of the 2D area
	 * @param actionexecutor executor for agent actions
	 */
	public Grid2D(Object name, IVector2 areasize, String bordermode)
	{
		super(areasize==null? null: new Vector2Int(areasize.getXAsInteger(), areasize.getYAsInteger()), bordermode);
		this.setProperty("name", name);
		this.objectsygridpos = new MultiCollection();
	}
	
	//-------- grid specific methods --------
		
	/**
	 *  Set the area size.
	 *  @param areasize The area size.
	 */
	public void setAreaSize(IVector2 areasize)
	{
		synchronized(monitor)
		{
			this.areasize = areasize==null? null: new Vector2Int(areasize.getXAsInteger(), areasize.getYAsInteger());
		}
	}
	
	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public Collection getSpaceObjectsByGridPosition(IVector2 position, Object type)
	{
		synchronized(monitor)
		{
			if(position!=null)
				position	= adjustPosition(position);
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
			
			IVector2 oldpos = (IVector2)obj.getProperty(PROPERTY_POSITION);
			if(objectsygridpos.containsKey(oldpos) && oldpos!=null)
			{
//				System.out.println("remove: "+oldpos+" "+obj);
				objectsygridpos.remove(oldpos, obj);
			}
			
			IVector2 newpos = adjustPosition(pos);
			objectsygridpos.put(newpos, obj);
//			System.out.println("add: "+newpos+" "+obj);
			
			super.setPosition(id, newpos);
		}
	}
		
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @return the object's ID
	 */
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks)
	{
		synchronized(monitor)
		{
			// TODO: maybe only assign position to discretePosition vector?
			if(properties==null)
			{
				properties	= new HashMap();
			}
			if(!properties.containsKey(Space2D.PROPERTY_POSITION))
			{
				// Todo: Fail, when no empty position.
				properties.put(Space2D.PROPERTY_POSITION, getEmptyGridPosition());
			}
			
			ISpaceObject obj = super.createSpaceObject(typename, properties, tasks);
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
				IVector2 pos = (IVector2)getSpaceObject(id).getProperty(Space2D.PROPERTY_POSITION);
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
	
	/**
	 *  Calculate the distance in the space.
	 *  @param dx The distance in x.
	 *  @param dy The distance in y.
	 *  @return The distance according to the distance metrics of the space.
	 */
	public IVector1 calculateDistance(IVector1 dx, IVector1 dy)
	{
		if(dx.less(Vector1Double.ZERO))
			dx	= dx.copy().negate();
		if(dy.less(Vector1Double.ZERO))
			dy	= dy.copy().negate();
		
		return dx.add(dy);
	}
	
	/**
	 *  Get the shortest (direct) direction between two coordinates.
	 *  @param pos1 The first position.
	 *  @param pos2 The second position.
	 *  @param isx The flag indicating if x or y).
	 *  @return -1: left/down, +1: right/up, 0: no move
	 */
	public IVector1 getShortestDirection(IVector1 pos1, IVector1 pos2, boolean isx)
	{
		IVector1 ret = Vector1Int.ZERO;
		
		if(getBorderMode().equals(BORDER_TORUS))
		{
			IVector1 size = isx? areasize.getX(): areasize.getY();
			
			if(pos1.less(pos2))
			{
				IVector1 d1 = pos2.copy().subtract(pos1);
				IVector1 d2 = pos1.copy().add(size).subtract(pos2);
				
				if(d1.less(d2))
					ret = new Vector1Int(1);
				else 
					ret = new Vector1Int(-1);
			}
			else if(pos1.greater(pos2))
			{
				IVector1 d1 = pos1.copy().subtract(pos2);
				IVector1 d2 = pos2.copy().add(size).subtract(pos1);
				
				if(d1.less(d2))
					ret = new Vector1Int(-1);
				else
					ret = new Vector1Int(1);
			}
		}
		else
		{
			if(pos1.less(pos2))
				ret = new Vector1Int(1);
			else if(pos1.greater(pos2))
				ret = new Vector1Int(-1);
		}
		
		return ret;
	}
}
