package jadex.extension.envsupport.environment.space2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.IFilter;
import jadex.commons.SimplePropertyObject;
import jadex.commons.meta.IPropertyMetaDataSet;
import jadex.extension.envsupport.MObjectType;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;


/**
 *  General 2D space.
 */
public abstract class Space2D extends AbstractEnvironmentSpace
{
	//-------- constants --------
	
	/** The constant for the position property. */
	public static final String PROPERTY_POSITION = "position";
	
	/** The constant for the border property. */
	public static final String PROPERTY_BORDER = "border";
	
	/** Border strict mode. */
	public static final String BORDER_STRICT = "strict";

	/** Border relaxed mode. */
	public static final String BORDER_RELAXED = "relaxed";

	/** Border torus behavior. */
	public static final String BORDER_TORUS = "torus";

	//-------- attributes --------
	
	/** Area size. */
	protected IVector2 areasize;
	
	/** KD-Trees. */
	protected Map<String, KdTree> kdTrees;	
	
	//-------- constructors --------
	
	/**
	 * Initializes the 2D-Space.
	 * @param spaceexecutor executor for the space
	 * @param actionexecutor executor for component actions
	 * @param areasize the size of the 2D area
	 */
	protected Space2D(IVector2 areasize)
	{		
		this.areasize = areasize;
		this.kdTrees = new HashMap<String, KdTree>();
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
		return getPropertyNames().contains(PROPERTY_BORDER)
		? (String)getProperty(PROPERTY_BORDER)
		: BORDER_TORUS;
	}

	/** 
	 * Init an object in this space.
	 */
	public void initSpaceObject(ISpaceObject ret)
	{
		super.initSpaceObject(ret);
		
		IVector2 pos = ret.getPropertyNames().contains(PROPERTY_POSITION)? 
			(IVector2)ret.getProperty(PROPERTY_POSITION): getRandomPosition(Vector2Int.ZERO);

		if(pos!=null)
		{
			ret.setProperty(PROPERTY_POSITION, null);
			setPosition(ret.getId(), pos);
		}
		
		KdTree kdTree = kdTrees.get(ret.getType());
		if (kdTree != null)
			kdTree.addObject(ret);
	}
	
	public void addSpaceObjectType(String typename,
			IPropertyMetaDataSet mobjecttype)
	{
		super.addSpaceObjectType(typename, mobjecttype);
		if (((MObjectType) mobjecttype).isKdTree())
			enableKdTree(typename);
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
			
			IVector2 newpos = adjustPosition(pos);
			obj.setProperty(PROPERTY_POSITION, newpos);
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
//			try
//			{
				IVector1 dx = getDistance(pos1.getX(), pos2.getX(), true);
				IVector1 dy = getDistance(pos1.getY(), pos2.getY(), false);
			
				IVector1 ret = calculateDistance(dx, dy);
//				System.out.println(ret);
				return ret;
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				throw new RuntimeException();
//			}
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
			
			if(BORDER_TORUS.equals(getBorderMode()))
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
			else if(BORDER_RELAXED.equals(getBorderMode()))
			{
				ret = pos;
			}
			else
			{
				throw new RuntimeException("Unknown bordermode: "+getBorderMode());
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
	 * Enables kd-tree NN-Search optimization for a specific object type.
	 * This will increase space overhead but massively decreases restricted
	 * nearest-neighborsearches in cases of large amounts 
	 * of (evenly distributed) objects.
	 * 
	 * @param String type The type of object being optimized.
	 */
	public void enableKdTree(String type)
	{
		synchronized(monitor)
		{
			KdTree tree = new KdTree();
			ISpaceObject[] objects = (ISpaceObject[]) getSpaceObjectsByType(type);
			for (int i = 0; i < objects.length; ++i)
				tree.addObject(objects[i]);
			
			tree.rebuild();
			
			kdTrees.put(type, tree);
			
			ISpaceProcess process = new KdTreeProcess(tree);
			process.setProperty(ISpaceProcess.ID, tree);
			processes.put(tree, process);
		}
	}
	
	/**
	 * Disables kd-tree NN-Search optimization for a specific object type.
	 * 
	 * @param String type The type of object for which the kd-tree is disabled.
	 */
	public void disableKdTree(String type)
	{
		synchronized (monitor)
		{
			KdTree tree = kdTrees.remove(type);
			processes.remove(tree);
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
			KdTree kdTree = kdTrees.get(type);
			if (kdTree != null)
				return kdTree.getNearestObject(position, maxdist.getAsDouble());
			
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
	public Set<ISpaceObject> getNearObjects(IVector2 position, IVector1 maxdist)
	{
		return getNearObjects(position, maxdist, (IFilter)null);
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public Set<ISpaceObject> getNearObjects(IVector2 position, IVector1 maxdist, final String type)
	{
		synchronized(monitor)
		{
			KdTree kdTree = kdTrees.get(type);
			if (kdTree != null)
				return new HashSet(kdTree.getNearestObjects(position, maxdist.getAsDouble()));
		}
		
		return getNearObjects(position, maxdist, new IFilter()
		{
			public boolean filter(Object obj)
			{
				return type.equals(((ISpaceObject)obj).getType());
			}
		});
//		synchronized(monitor)
//		{
//			Set ret = new HashSet();
//		
//			Set objects = spaceobjects.entrySet();
//			for(Iterator it = objects.iterator(); it.hasNext();)
//			{
//				Map.Entry entry = (Entry)it.next();
//				ISpaceObject obj = (ISpaceObject)entry.getValue();
//				IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
//				
//				if(pos!=null && (type==null || type.equals(obj.getType())))
//				{
//					IVector1 dist = getDistance(pos, position);
//					if(maxdist==null || !maxdist.less(dist))
//					{
//						ret.add(obj);
//					}
//				}
//			}
//		
//			return ret;
//		}
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 */
	public Set<ISpaceObject> getNearObjects(IVector2 position, IVector1 maxdist, IFilter filter)
	{
		synchronized(monitor)
		{
			Set ret = new HashSet();
			
			Set objects = spaceobjects.entrySet();
			for(Iterator it = objects.iterator(); it.hasNext();)
			{
				Map.Entry entry = (Entry)it.next();
				ISpaceObject obj = (ISpaceObject)entry.getValue();
				IVector2 pos = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
				
				if(pos!=null && (filter==null || filter.filter(obj)))
				{
					IVector1 dist = getDistance(pos, position);
					if(maxdist==null || !maxdist.less(dist))
					{
						ret.add(obj);
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
	 * @return The near objects. 
	 */
	public Set<ISpaceObject> getNearObjects(IVector2 position, IVector1 maxdist, final String type, final IFilter filter)
	{
		synchronized(monitor)
		{
			KdTree kdTree = kdTrees.get(type);
			if (kdTree != null)
				return new HashSet(kdTree.getNearestObjects(position, maxdist.getAsDouble(), filter));
		}
		
		return getNearObjects(position, maxdist, new IFilter()
		{
			public boolean filter(Object obj)
			{
				return (((ISpaceObject) obj).getType().equals(type) && filter.filter(obj));
			}
		});
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return The near objects. 
	 * /
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
	}*/
	
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
	
	protected class KdTreeProcess extends SimplePropertyObject implements ISpaceProcess
	{
		/** The kd-tree */
		protected KdTree kdtree;
		
		/** Creates a new update process for a kd-tree.
		 * 
		 * @param kdTree The kd-tree.
		 */
		public KdTreeProcess(KdTree kdTree)
		{
			this.kdtree = kdTree;
		}
		
		public void execute(IClockService clock, IEnvironmentSpace space)
		{
			kdtree.rebuild();
		}
		
		public void shutdown(IEnvironmentSpace space)
		{
		}
		
		public void start(IClockService clock, IEnvironmentSpace space)
		{
		}
	}
}
