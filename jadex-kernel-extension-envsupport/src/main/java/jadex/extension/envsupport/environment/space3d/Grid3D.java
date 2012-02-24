package jadex.extension.envsupport.environment.space3d;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector3Int;
import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  2D grid environment.
 */
public class Grid3D extends Space3D
{
	//-------- constants --------

	/** The default ID for this space. */
	public static final String DEFAULT_NAME = Grid3D.class.getName();
	
	/** The moore neighborhood. */
	public static final String NEIGHBORHOOD_MOORE = "moore";
	
	/** The von neumann neighborhood. */
	public static final String NEIGHBORHOOD_VON_NEUMANN = "von_neumann";

	/** The neighborhood property. */
	public static final String PROPERTY_NEIGHBORHOOD = "neighborhood";
	
	//-------- attributes --------
	
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
	public Grid3D()
	{
		this(null);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param actionexecutor executor for component actions
	 * @param areasize the size of the 2D area
	 */
	public Grid3D(IVector3 areasize)
	{
		this(DEFAULT_NAME, areasize);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * @param name the name of this space
	 * @param areasize the size of the 2D area
	 * @param actionexecutor executor for component actions
	 */
	public Grid3D(Object name, IVector3 areasize)
	{
		super(areasize==null? null: new Vector3Int(areasize.getXAsInteger(), areasize.getYAsInteger(), areasize.getZAsInteger()));
		this.setProperty("name", name);	
		this.objectsygridpos = new MultiCollection();
	}
	
	//-------- grid specific methods --------
		
	/**
	 *  Set the area size.
	 *  @param areasize The area size.
	 */
	public void setAreaSize(IVector3 areasize)
	{
		synchronized(monitor)
		{
			this.areasize = areasize==null? null: new Vector3Int(areasize.getXAsInteger(), areasize.getYAsInteger(), areasize.getZAsInteger());
		}
	}
	
	/**
	 *  Get the neighborhood.
	 *  @return Set the neighborhood.
	 */
	public String getNeighborhood()
	{
		return getPropertyNames().contains(PROPERTY_NEIGHBORHOOD)
			? (String)getProperty(PROPERTY_NEIGHBORHOOD)
			: NEIGHBORHOOD_VON_NEUMANN;
	}

	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public Collection getSpaceObjectsByGridPosition(IVector3 position, Object type)
	{
		Collection ret = null;
		synchronized(monitor)
		{
			if(position!=null)
			{
				position = adjustPosition(position);
				IVector3 fieldpos = new Vector3Int(position.getXAsInteger(), position.getYAsInteger(), position.getZAsInteger());
			
				Collection simobjs = objectsygridpos.getCollection(fieldpos);
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
			}
//			System.out.println("getSpaceObs: "+position+" "+type+" "+ret+" "+simobjs);
			
		}
		return ret;
	}
		
	/**
	 *  Get an empty position in the grid.
	 *  @return Empty {@link IVector2} position.
	 */
	public IVector3 getEmptyGridPosition()
	{
		synchronized(monitor)
		{
			IVector3 ret = null;

			// Problem of efficiently finding an empty field
			// Enumerating all empty fields would cause big
			// memory consumption on big grids
			
			// first try n times random value 
			int n = 5;
			for(int i=0; i<n && ret==null; i++)
			{
				ret = new Vector3Int(getRandomPosition(Vector3Int.ZERO));
				if(objectsygridpos.containsKey(ret))
					ret = null;
			}
			
			// Just find first empty field
			if(ret==null)
			{
				for(int y=0; ret==null && y<areasize.getYAsInteger(); y++)
				{
					for(int x=0; ret==null && x<areasize.getXAsInteger(); x++)
					{
						for(int z=0; ret==null && x<areasize.getZAsInteger(); z++)
						{
						ret = new Vector3Int(x, y, z);
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
	public void setPosition(Object id, IVector3 pos)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id);
			
			IVector3 oldpos = (IVector3)obj.getProperty(PROPERTY_POSITION);
			if(oldpos!=null)
			{
				oldpos	= new Vector3Int(oldpos.getXAsInteger(), oldpos.getYAsInteger(), oldpos.getZAsInteger());
				if(objectsygridpos.containsKey(oldpos))
				{
	//				System.out.println("remove: "+oldpos+" "+obj);
					objectsygridpos.remove(oldpos, obj);
				}
			}
			
			IVector3 newpos = adjustPosition(pos);
			if(newpos!=null)
			{
				objectsygridpos.put(new Vector3Int(newpos.getXAsInteger(), newpos.getYAsInteger(), newpos.getZAsInteger()), obj);
//				System.out.println("add: "+newpos+" "+obj);
			}
			
			super.setPosition(id, newpos);
		}
	}
		
	/** 
	 * Init an object in this space.
	 */
	public void initSpaceObject(ISpaceObject ret)
	{
		synchronized(monitor)
		{
			// TODO: maybe only assign position to discretePosition vector?
			if(!ret.getPropertyNames().contains(PROPERTY_POSITION))
			{
				IVector3 pos = getEmptyGridPosition();
				if(pos!=null)
					ret.setProperty(Space3D.PROPERTY_POSITION, pos);
			}
			
			super.initSpaceObject(ret);
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
				IVector3 pos = (IVector3)getSpaceObject(id).getProperty(Space3D.PROPERTY_POSITION);
				if(pos!=null)
				{
					IVector3 fieldpos = new Vector3Int(pos.getXAsInteger(), pos.getYAsInteger(), pos.getZAsInteger());
					objectsygridpos.remove(fieldpos, spaceobjects.get(id));
				}
				super.destroySpaceObject(id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 *  Retrieve all objects in the distance for a position.
	 *  Uses position->object mapping, for fast operation.
	 *  @param position	The position.
	 *  @param distance	The distance.
	 *  @param type	The type (or null for all objects).
	 */
	public Set getNearObjects(IVector3 position, IVector1 distance, String type)
	{
		synchronized(monitor)
		{
			Set ret = new HashSet();
			
			int sizex = areasize.getXAsInteger();
			int sizey = areasize.getYAsInteger();
			int sizez = areasize.getZAsInteger();
	
			int x = position.getXAsInteger();
			int y = position.getYAsInteger();
			int z = position.getYAsInteger();
			IVector3 pos = new Vector3Int(x, y, z);
			
			int range = distance.getAsInteger();
			
			int minx = x - range >= 0 || getBorderMode().equals(BORDER_TORUS) ? x - range : 0;
			int maxx = x + range <= sizex || getBorderMode().equals(BORDER_TORUS) ? x + range : sizex;

			int miny = y - range >= 0 || getBorderMode().equals(BORDER_TORUS) ? y - range : 0;
			int maxy = y + range <= sizey || getBorderMode().equals(BORDER_TORUS) ? y + range : sizey;
			
			int minz = z - range >= 0 || getBorderMode().equals(BORDER_TORUS) ? z - range : 0;
			int maxz = z + range <= sizez || getBorderMode().equals(BORDER_TORUS) ? z + range : sizez;

			for(int i = minx; i <= maxx; i++)
			{
				for(int j = miny; j <= maxy; j++)
				{
					for(int k = minz; k <= maxz; j++)
					{
					Vector3Int testpos = new Vector3Int((i + sizex) % sizex, (j + sizey) % sizey, (k + sizez) % sizez);
					if(!getDistance(testpos, pos).greater(distance))
					{
						Collection tmp = objectsygridpos.getCollection(testpos);
						if(tmp != null)
						{
							if(type==null)
							{
								ret.addAll(tmp);
							}
							else
							{
								for(Iterator it=tmp.iterator(); it.hasNext(); )
								{
									ISpaceObject obj = (ISpaceObject)it.next();
									if(obj.getType().equals(type))
										ret.add(obj);
								}
								
							}
						}
					}
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
	 * /
	public ISpaceObject[] getNearObjects(IVector2 position, IVector2 maxdist, String type)
	{
		synchronized(monitor)
		{
			Collection ret = new ArrayList();
			
			int sizex = areasize.getXAsInteger();
			int sizey = areasize.getYAsInteger();
	
			int x = position.getXAsInteger();
			int y = position.getYAsInteger();
			
			int rangex = maxdist.getXAsInteger();
			int rangey = maxdist.getYAsInteger();
	
			int minx = x - rangex >= 0 || getBorderMode().equals(BORDER_TORUS) ? x - rangex : 0;
			int maxx = x + rangex <= sizex || getBorderMode().equals(BORDER_TORUS) ? x + rangex : sizex;

			int miny = y - rangey >= 0 || getBorderMode().equals(BORDER_TORUS) ? y - rangey : 0;
			int maxy = y + rangey <= sizey || getBorderMode().equals(BORDER_TORUS) ? y + rangey : sizey;

			for (int i = minx; i <= maxx; i++)
			{
				for (int j = miny; j <= maxy; j++)
				{
					Collection tmp = objectsygridpos.getCollection(
							new Vector2Int((i + sizex) % sizex, (j + sizey) % sizey));
					if(tmp != null)
					{
						if(type==null)
						{
							ret.addAll(tmp);
						}
						else
						{
							for(Iterator it=tmp.iterator(); it.hasNext(); )
							{
								ISpaceObject	obj	= (ISpaceObject)it.next();
								if(obj.getType().equals(type))
									ret.add(obj);
							}
						}
					}
				}
			}
			
			return(ISpaceObject[])ret.toArray(new ISpaceObject[ret.size()]);
		}
	}*/
	
	/**
	 * Returns the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param maxdist maximum distance from the position, use null for unlimited distance
	 * @return nearest object's ID or null if none is found
	 * /
	// Todo: doesn't work and needs termination condition.
	public ISpaceObject getNearestObject(IVector2 position, IVector1 maxdist, String type)
	{
		
		synchronized(monitor)
		{
			int	cnt	= 0;
			ISpaceObject	ret = null;
			IVector1	retdist	= null;
			IVector2	testpos	= position.copy();
			IVector1	testdist	= getDistance(position, testpos);
			IVector1	mindist	= testdist;			
			
			System.out.println("Nearest object: "+testpos);
			if((maxdist==null || !testdist.greater(maxdist)) && (ret==null || testdist.less(retdist)))
			{
				Collection	tmp	= getSpaceObjectsByGridPosition(testpos, type);
				if(tmp!=null && !tmp.isEmpty())
				{
					ret	= (ISpaceObject)tmp.iterator().next();
					retdist	= testdist;
					System.out.println("Nearest object found "+cnt+" steps: "+retdist+", "+ret);
				}
			}
			cnt++;
			
			for(int i=1; (ret==null || retdist.greater(mindist)) && (maxdist==null || !mindist.greater(maxdist)); i++)
			{
				// Move left / right
				for(int j=0; j<i; j++)
				{
					testpos	= adjustPosition(i%2==0 ? testpos.add(LEFT) : testpos.add(RIGHT));
					testdist	= getDistance(position, testpos);
					mindist	= j==0 ? testdist : testdist.less(mindist) ? testdist : mindist;
					System.out.println("Nearest object: "+testpos);
					if((maxdist==null || !testdist.greater(maxdist)) && (ret==null || testdist.less(retdist)))
					{
						Collection	tmp	= getSpaceObjectsByGridPosition(testpos, type);
						if(tmp!=null && !tmp.isEmpty())
						{
							ret	= (ISpaceObject)tmp.iterator().next();
							retdist	= testdist;
							System.out.println("Nearest object found "+cnt+" steps: "+retdist+", "+ret);
						}
					}
					cnt++;
				}
				
				// Todo: join both inner loops!?
				if(!(ret==null || retdist.greater(mindist)) && (maxdist==null || !mindist.greater(maxdist)))
					break;
				
				// Move up / down
				for(int j=0; j<i; j++)
				{
					testpos	= adjustPosition(i%2==0 ? testpos.add(UP) : testpos.add(DOWN));
					testdist	= getDistance(position, testpos);
					mindist	= j==0 ? testdist : testdist.less(mindist) ? testdist : mindist;
					System.out.println("Nearest object: "+testpos);
					if((maxdist==null || !testdist.greater(maxdist)) && (ret==null || testdist.less(retdist)))
					{
						Collection	tmp	= getSpaceObjectsByGridPosition(testpos, type);
						if(tmp!=null && !tmp.isEmpty())
						{
							ret	= (ISpaceObject)tmp.iterator().next();
							retdist	= testdist;
							System.out.println("Nearest object found "+cnt+" steps: "+retdist+", "+ret);
						}
					}
					cnt++;
				}
			}
			
			System.out.println("Nearest object took "+cnt+" steps: "+retdist+", "+ret);
			
			return ret;
		}
	}*/

	/**
	 *  Calculate the distance in the space.
	 *  @param dx The distance in x.
	 *  @param dy The distance in y.
	 *  @return The distance according to the distance metrics of the space.
	 */
	public IVector1 calculateDistance(IVector1 dx, IVector1 dy)
	{
		if(NEIGHBORHOOD_MOORE.equals(getNeighborhood()))
		{
			return dx.greater(dy)? dx: dy;
		}
		else if(NEIGHBORHOOD_VON_NEUMANN.equals(getNeighborhood()))
		{
			if(dx.less(Vector1Double.ZERO))
				dx	= dx.copy().negate();
			if(dy.less(Vector1Double.ZERO))
				dy	= dy.copy().negate();
		}
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
