package jadex.bdi.planlib.simsupport.environment;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

import java.util.Map;


/**
 * The simulation engine interface. 
 * 
 * NOTE: The *Access()-methods provide fast,
 * direct access to internal data structures of the engine. Therefore returned
 * objects must be locked appropriately before use (i.e. synchronized() blocks
 * during iteration, methods are already synchronized). If multiple *Access()
 * objects are required at the same time, the following lock-order must be used
 * to prevent deadlocks: 0. getSimObjectAccess() (also locks the engine) 1.
 * getTypedSimObjectAccess()
 */
public interface ISimulationEngine extends IExternalEngineAccess
{
	/**
	 * Retrieves a simulation object.
	 * 
	 * @param objectId the simulation object ID
	 * @return current the simulated object
	 */
	public SimObject getSimulationObject(Integer objectId);

	/**
	 * Returns the nearest object to the given position.
	 * 
	 * @param position position the object should be nearest to
	 * @return nearest object
	 */
	public SimObject getNearestObject(IVector2 position);

	/**
	 * Returns the nearest object of a specific type to the given position.
	 * 
	 * @param type type of the object
	 * @param position position the object should be nearest to
	 * @return nearest object of a specific type
	 */
	public SimObject getNearestObject(String type, IVector2 position);

	/**
	 * Returns direct access to the simulation objects.
	 * 
	 * @return direct access to simulation objects
	 */
	public Map getSimObjectAccess();

	/**
	 * Returns direct access to the typed simulation object view.\
	 * 
	 * @return direct access to typed simulation object view
	 */
	public Map getTypedSimObjectAccess();

	/**
	 * Progresses the simulation.
	 * 
	 * @param deltaT time difference since the last step in seconds
	 */
	public void simulateStep(IVector1 deltaT);
}
