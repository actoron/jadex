package jadex.adapter.base.envsupport.environment.space2d;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
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
	public static final String PROPERTY_POSITION = "position";
	
	/** Border strict mode. */
	public static final String BORDER_STRICT = "strict";

	/** Border torus behavior. */
	public static final String BORDER_TORUS = "torus";

	//-------- attributes --------
	
	/** Area size. */
	protected IVector2 areasize;
	
	/** The behavior of the world */
	public String bordermode;
	
	//-------- constructors --------
	
	/**
	 * Initializes the 2D-Space.
	 * @param spaceexecutor executor for the space
	 * @param actionexecutor executor for agent actions
	 * @param areasize the size of the 2D area
	 */
	protected Space2D(IVector2 areasize, String bordermode)
	{		
		this.areasize = areasize;
		setBorderMode(bordermode);
	}
	
	//-------- methods --------
		
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
	 *  @param areasize The area size.
	 */
	public void setAreaSize(IVector2 areasize)
	{
		synchronized(monitor)
		{
			this.areasize = areasize;
		}
	}
	
	/**
	 *  Get the border mode.
	 *  @return the border_mode
	 */
	public String getBorderMode()
	{
		return this.bordermode;
	}

	/**
	 *  Set the border mode.
	 *  @param border_mode The border mode to set.
	 */
	public void setBorderMode(String bordermode)
	{
//		System.out.println("bordemode: "+bordermode);
		if(!BORDER_STRICT.equals(bordermode) && !BORDER_TORUS.equals(bordermode))
			throw new RuntimeException("Unknown border mode: "+bordermode);
		this.bordermode = bordermode;
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
		ISpaceObject ret = super.createSpaceObject(typename, properties, tasks);
		
		IVector2 pos = ret.getPropertyNames().contains(PROPERTY_POSITION)? 
			(IVector2)ret.getProperty(PROPERTY_POSITION): getRandomPosition(Vector2Int.ZERO);

		if(pos!=null)
		{
//			System.out.println("setting pos to: "+ret+" "+pos);
			ret.setProperty(PROPERTY_POSITION, null);
			setPosition(ret.getId(), pos);
		}
//		else
//		{
//			System.out.println("setting no pos to: "+ret);
//		}
		
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
			
			IVector2 oldpos = (IVector2)obj.getProperty(PROPERTY_POSITION);
			IVector2 newpos = adjustPosition(pos);
			obj.setProperty(PROPERTY_POSITION, newpos);
			fireEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.OBJECT_POSITION_CHANGED, this, obj, oldpos));
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
			try
			{
				IVector1 dx = getDistance(pos1.getX(), pos2.getX(), true);
				IVector1 dy = getDistance(pos1.getY(), pos2.getY(), false);
			
				return calculateDistance(dx, dy);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	
	/**
	 *  Get the distance between two coordinates (x or y).
	 *  @param pos1	The first position.
	 *  @param pos2	The second position.
	 */
	public IVector1	getDistance(IVector1 pos1, IVector1 pos2, boolean isx)
	{
		synchronized(monitor)
		{
			IVector1 ret = null;
			
			if(getBorderMode()==BORDER_TORUS)
			{
				IVector1 size = isx? areasize.getX(): areasize.getY();
				
				if(pos1.greater(pos2))
				{
					IVector1 tmp = pos1;
					pos1 = pos2;
					pos2 = tmp;
				}
				IVector1 d1 = pos2.copy().subtract(pos1);
				IVector1 d2 = pos1.copy().add(size).subtract(pos2);
				ret = d1.less(d2) ? d1 : d2;
			}
			else
			{
				ret = pos1.getDistance(pos2);
			}
			
			return ret;
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
		IVector1 x2 = dx.copy().multiply(dx);
		IVector1 y2 = dy.copy().multiply(dy);
		return x2.add(y2).sqrt();
	}
	
	/**
	 *  Calculate a position according to the space borders.
	 */
	public IVector2 adjustPosition(IVector2 pos)
	{
		IVector2 ret = null;
		
		if(pos!=null)
		{
			if(BORDER_TORUS.equals(getBorderMode()))
			{
				IVector1 sizex = areasize.getX();
				IVector1 sizey = areasize.getY();
				
				IVector1 x = pos.getX().copy();
				IVector1 y = pos.getY().copy();
				
				while(x.less(Vector1Double.ZERO))
					x.add(sizex);
				while(y.less(Vector1Double.ZERO))
					y.add(sizey);
				
				x = x.copy().mod(sizex);
				y = y.copy().mod(sizey);
				
				ret = x.createVector2(y);
			}
			else if(BORDER_STRICT.equals(getBorderMode()))
			{
				IVector1 sizex = areasize.getX();
				IVector1 sizey = areasize.getY();
				
				if(pos.getX().greater(sizex) || pos.getX().less(Vector1Double.ZERO)
					|| pos.getY().greater(sizey) || pos.getY().less(Vector1Double.ZERO))
				{
					throw new RuntimeException("Position out of areasize: "+pos+" "+areasize);
				}
				ret = pos;
			}
			else
			{
				throw new RuntimeException("Unknown bordermode: "+bordermode);
			}
		}
		
		return ret;
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
			if(distance == null)
				distance = Vector2Double.ZERO;
			IVector2 position = areasize.copy();
			position.subtract(distance);
			position.randomX(distance.getX(), position.getX());
			position.randomY(distance.getY(), position.getY());
			
//			System.out.println("position: "+position);
			return position;
		}
	}
	
	/**
	 * Returns the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxdist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 */
	public ISpaceObject getNearestObject(IVector2 position, IVector1 maxdist, String type)
	{
		ISpaceObject ret = null;
		
		synchronized(monitor)
		{
			ISpaceObject nearest = null;
			IVector1 distance = null;
			ISpaceObject[] objects = type!=null ? getSpaceObjectsByType(type) : (ISpaceObject[])getSpaceObjects();
			for(int i=0; objects!=null && i<objects.length; i++)
			{
				IVector2	curpos	= (IVector2)objects[i].getProperty(Space2D.PROPERTY_POSITION);
				if(curpos!=null)
				{
					IVector1 objdist = getDistance(curpos, position); 
					if(nearest==null || objdist.less(distance))
					{
						nearest = objects[i];
						distance = objdist;
					}
				}
			}
			
			if(maxdist==null || distance!=null && !maxdist.less(distance))
				ret = nearest;
			
			return ret;
		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector1 maxdist, String type)
	{
		synchronized(monitor)
		{
			List ret = new ArrayList();
		
			Set objects = spaceobjects.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject obj = (ISpaceObject)entry.getValue();
				IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
				
				if(pos!=null && (type==null || type.equals(obj.getType())))
				{
					IVector1 dist = getDistance(pos, position);
					if(maxdist==null || !maxdist.less(dist))
					{
						ret.add(obj);
					}
				}
			}
		
			return (ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public ISpaceObject[] getNearObjects(IVector2 position, IVector2 maxdist, String type)
	{
		synchronized(monitor)
		{
			List ret = new ArrayList();
		
			Set objects = spaceobjects.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject obj = (ISpaceObject)entry.getValue();
				IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
				
				if(pos!=null && (type==null || type.equals(obj.getType())))
				{
					IVector1 dx = getDistance(pos.getX(), position.getX(), true);
					IVector1 dy = getDistance(pos.getY(), position.getY(), false);
	
					if(dx.less(maxdist.getX()) || dx.equals(maxdist.getX())
						&& dy.less(maxdist.getY()) || dy.equals(maxdist.getY()))
					{
						ret.add(obj);
					}
				}
			}
		
			return (ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
		}
	}
	
	/**
	 *  Get all space objects.
	 *  @return All space objects.
	 */
	public Object[] getSpaceObjects()
	{
		synchronized(monitor)
		{
			return spaceobjects.values().toArray();
		}
	}
}
