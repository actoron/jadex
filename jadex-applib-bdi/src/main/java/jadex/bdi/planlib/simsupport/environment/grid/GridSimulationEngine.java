package jadex.bdi.planlib.simsupport.environment.grid;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GridSimulationEngine extends EuclideanSimulationEngine implements IGridSimulationEngine
{
	// ------ fields --------
	
	/** The behavior of the world */
	public int area_behavior_ = AREA_BEHAVIOR_TORUS;
	
	/** All simobject id's accessible per position. */
	protected MultiCollection simObjectsByGridPosition_;
	
	/** Last known discrete position of a simobject */
	protected Map gridPositionBySimObjectId_;
	
	// ------ constructors --------

	/**
	 * Default constructor.
	 * Grid default is a discrete torus world
	 */
	public GridSimulationEngine(String title, IVector2 areaSize)
	{
		this(title, areaSize, AREA_BEHAVIOR_TORUS);
	}
	
	/**
	 * Constructor to create a GridSimulationEngine.
	 */
	public GridSimulationEngine(String title, IVector2 areaSize, int world_behavior)
	{
		super(title, areaSize);
		this.area_behavior_ = world_behavior;
		this.simObjectsByGridPosition_ = new MultiCollection();
		this.gridPositionBySimObjectId_ = Collections.synchronizedMap(new HashMap());
	}
	
	// ------ grid methods --------
	
	/** 
	 * Retrieve the world behavior
	 */
	public int getAreaBehavior()
	{
		return area_behavior_;
	}
	
	/**
	 * Get all SimObjects at a specific grid position
	 */
	public SimObject[] getSimulationObjectsByGridPosition(GridPosition position)
	{
		return getSimulationObjectsByGridPosition(position, null);
	}
	
	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public SimObject[] getSimulationObjectsByGridPosition(GridPosition position, String type)
	{
		SimObject[] ret = null;
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				synchronized (simObjectsByGridPosition_)
				{
					Collection simobjs = simObjectsByGridPosition_.getCollection(position);
					if (null == type)
					{
						ret = (SimObject[]) simobjs.toArray(new SimObject[simobjs.size()]);
					}
					else
					{
						List l = new ArrayList();
						for (Iterator objs = simobjs.iterator(); objs.hasNext();)
						{
							SimObject currentObj = (SimObject) objs.next();
//							synchronized(currentObj)
//							{
								if (type.equals(currentObj.getType()))
								{
									l.add(currentObj);
								}
//							}
							
						}
						ret = (SimObject[]) l.toArray(new SimObject[l.size()]);
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the last known grid position for a simobject id
	 * @param objectId
	 * @return IVector2 position
	 */
	public GridPosition getSimulationObjectGridPosition(Integer objectId)
	{
		SimObject object = getSimulationObject(objectId);
		if(object == null)
		{
			return null;
		}
		return (GridPosition) gridPositionBySimObjectId_.get(objectId);
	}
	
	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return {@link SimObject}[] 
	 */
	public SimObject[] getNearObjects(GridPosition position, IVector1 distance)
	{
		Collection ret = new ArrayList();
		
		int sizex = areaSize_.getXAsInteger();
		int sizey = areaSize_.getYAsInteger();

		int x = position.getXAsInteger();
		int y = position.getYAsInteger();
		
		int range = distance.getAsInteger();

		synchronized (simObjectsByGridPosition_)
		{
			switch (area_behavior_)
			{
				case AREA_BEHAVIOR_TORUS:
				{
					for (int i = x - range; i <= x + range; i++)
					{
						for (int j = y - range; j <= y + range; j++)
						{
							Collection tmp = simObjectsByGridPosition_.getCollection(
									new GridPosition((i + sizex) % sizex, (j + sizey) % sizey));
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
							Collection tmp = simObjectsByGridPosition_.getCollection(
									new GridPosition((i + sizex) % sizex, (j + sizey) % sizey));
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

		return (SimObject[]) ret.toArray(new SimObject[ret.size()]);
	}
	
	/**
	 *  Get an empty position in the grid.
	 *  @return Empty {@link IVector2} position.
	 */
	public GridPosition getEmptyGridPosition()
	{
		return getEmptyGridPosition(new Vector2Int(0));
	}
	
	/**
	 *  Get an empty position in the grid.
	 *  @param distance the minimum edge distance
	 *  @return Empty {@link IVector2} position.
	 */
	public GridPosition getEmptyGridPosition(IVector2 distance)
	{
		GridPosition ret = null;
		
		synchronized (simObjectsByGridPosition_)
		{
			while (ret == null)
			{
				ret = new GridPosition(getRandomPosition(distance));
				if (simObjectsByGridPosition_.containsKey(ret))
				{
					ret = null;
				}
			}
		}
		
		return ret;

	}
	
	// ------ override methods --------
	
	/**
	 * @see EuclideanSimulationEngine#createSimObject(String, Map, List, IVector2, boolean, ISimulationEventListener)
	 * 
	 * Also adds the simulation object to the discrete grid.
	 */
	public Integer createSimObject(String type, Map properties, List tasks,
			IVector2 position, boolean signalDestruction,
			ISimulationEventListener listener)
	{
		Integer newSimobjectId = null;
		
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				synchronized (simObjectsByGridPosition_)
				{
					synchronized (gridPositionBySimObjectId_)
					{
						// TODO: maybe only assign position to discretePosition vector?
						
						newSimobjectId = super.createSimObject(type, properties, tasks, position, signalDestruction, listener);
						
						// ensure discrete position in maps
						GridPosition gridPosition = new GridPosition(position);
						simObjectsByGridPosition_.put(gridPosition, getSimulationObject(newSimobjectId));
						gridPositionBySimObjectId_.put(newSimobjectId, gridPosition);
					}
				}
			}
		}
		return newSimobjectId;
	}

	/**
	 * @see EuclideanSimulationEngine#destroySimObject(Integer)
	 * 
	 * Also removes the simulation object from the discrete grid.
	 */
	public void destroySimObject(Integer objectId)
	{
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				synchronized (simObjectsByGridPosition_)
				{
					synchronized (gridPositionBySimObjectId_)
					{
						// remove the object from grid
						GridPosition lastGridPosition = (GridPosition) gridPositionBySimObjectId_.remove(objectId);
						if (null != lastGridPosition)
						{
							simObjectsByGridPosition_.remove(lastGridPosition, simObjects_.get(objectId));
						}
						
						super.destroySimObject(objectId);
					}
				}
			}
		}
	}
	
	/**
	 * @see EuclideanSimulationEngine#performAction(String, Integer, Integer, List)
	 */
	public boolean performAction(String actionName, Integer actorId,
			Integer objectId, List parameters)
	{
		// TODO: implement grid position update?
		return super.performAction(actionName, actorId, objectId, parameters);
	}
	
	/**
	 * @see EuclideanSimulationEngine#simulateStep(IVector1)
	 */
	public void simulateStep(IVector1 deltaT)
	{
		super.simulateStep(deltaT);
		updateObjects(deltaT);
	}
	
	/**
	 * Updates the positions of objects.
	 * Updates the discrete grid positions as well.
	 * 
	 * @param deltaT time difference since the last step
	 */
	private void updateObjects(IVector1 deltaT)
	{
		synchronized(simObjects_)
		{
			synchronized (simObjectsByType_)
			{
				synchronized (simObjectsByGridPosition_)
				{
					synchronized (gridPositionBySimObjectId_)
					{
						for(Iterator it = simObjects_.values().iterator(); it.hasNext();)
						{
							SimObject simObject = (SimObject)it.next();
							simObject.updateObject(deltaT);
							
							// TODO: maybe only assign position to GridPosition?
							
							// update position map when needed
							
							GridPosition gridPosition = (GridPosition) gridPositionBySimObjectId_.get(simObject.getId());
							
							// TODO: round simObject.getPosition() vector? 
							// 19->18 switches immediately, 18->19 on the last simulation step.
							if (! gridPosition.equals(simObject.getPosition())) 
							{
								simObjectsByGridPosition_.remove(gridPosition, simObject);
								// ensure new discrete position
								gridPosition = new GridPosition(simObject.getPosition());
								simObjectsByGridPosition_.put(gridPosition, simObject);
								gridPositionBySimObjectId_.put(simObject.getId(), gridPosition);
							}
						}
					}
					
				}
			}
			
		}
	}

}
