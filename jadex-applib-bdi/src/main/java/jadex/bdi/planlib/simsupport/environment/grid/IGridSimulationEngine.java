package jadex.bdi.planlib.simsupport.environment.grid;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public interface IGridSimulationEngine extends ISimulationEngine
{

	// ------ constants --------
	
	/** Euclidean plane behavior */
	public static final int WORLD_BEHAVIOR_EUCLID = 0;
	/** Torus behavior */
	public static final int WORLD_BEHAVIOR_TORUS = 1;

	// ------ grid specific methods --------
	
	/** 
	 * Retrieve the world behavior
	 */
	public abstract int getWorldBehavior();
	
	
	/**
	 * Get all SimObjects at a specific grid position
	 */
	public abstract SimObject[] getSimulationObjectsByGridPosition(
			IVector2 position);

	/**
	 * Get all SimObjects from a specific type at a specific grid position
	 */
	public abstract SimObject[] getSimulationObjectsByGridPosition(
			IVector2 position, String type);

	/**
	 * Get the last known grid position for a simobject id
	 * @param objectId
	 * @return IVector2 position
	 */
	public abstract IVector2 getSimulationObjectGridPosition(Integer objectId);

	/**
	 * Retrieve all objects in the distance for a position
	 * @param position
	 * @param distance
	 * @return {@link SimObject}[] 
	 */
	public abstract SimObject[] getNearObjects(IVector2 position,
			IVector1 distance);

	/**
	 *  Get an empty position in the grid.
	 *  @return Empty {@link IVector2} position.
	 */
	public abstract IVector2 getEmptyGridPosition();

	/**
	 *  Get an empty position in the grid.
	 *  @param distance the minimum edge distance
	 *  @return Empty {@link IVector2} position.
	 */
	public abstract IVector2 getEmptyGridPosition(IVector2 distance);

}
