package jadex.bdi.planlib.simsupport.environment;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;

import java.util.List;
import java.util.Map;


/**
 * The external simulation engine interface.
 */
public interface IExternalEngineAccess
{
	/**
	 * Declares a type of object.
	 * 
	 * @param type object type
	 */
	public void declareObjectType(String type);

	/**
	 * Adds a new SimObject to the simulation.
	 * 
	 * @param type type of the object
	 * @param properties properties of the object (may be null)
	 * @param tasks tasks of the object (may be null)
	 * @param position position of the object
	 * @param listener default object listener, may be null if none is required
	 * @return the simulation object ID
	 */
	public Integer createSimObject(String type, Map properties, List tasks,
			IVector2 position, boolean signalDestruction,
			ISimulationEventListener listener);

	/**
	 * Removes a SimObject from the simulation.
	 * 
	 * @param objectId the simulation object ID
	 */
	public void destroySimObject(Integer objectId);

	/**
	 * Adds an environment process.
	 * 
	 * @param process new environment process
	 */
	public void addEnvironmentProcess(IEnvironmentProcess process);

	/**
	 * Returns an environment process.
	 * 
	 * @param processName name of the environment process
	 * @return the environment process or null if not found
	 */
	public IEnvironmentProcess getEnvironmentProcess(String processName);

	/**
	 * Removes an environment process.
	 * 
	 * @param processName name of the environment process
	 */
	public void removeEnvironmentProcess(String processName);

	/**
	 * Returns an environment property.
	 * 
	 * @param name name of the property
	 * @return the property
	 */
	public Object getEnvironmentProperty(String name);

	/**
	 * Sets an environment property.
	 * 
	 * @param name name of the property
	 * @param property the property
	 */
	public void setEnvironmentProperty(String name, Object property);

	/**
	 * Returns the position of an object.
	 * 
	 * @param objectId the object ID
	 * @return the position of the object
	 */
	public IVector2 getObjectPosition(Integer objectId);

	/**
	 * Returns the type of an object.
	 * 
	 * @param objectId the object ID
	 * @return the type of the object
	 */
	public String getObjectType(Integer objectId);

	/**
	 * Returns the properties of an object.
	 * 
	 * @param objectId the object ID
	 * @return the properties
	 */
	public Map getObjectProperties(Integer objectId);

	/**
	 * Returns the ID of the nearest object to the given position.
	 * 
	 * @param position position the object should be nearest to
	 * @return nearest object's ID
	 */
	public Integer getNearestObjectId(IVector2 position);

	/**
	 * Returns the ID of the nearest object to the given position within a
	 * maximum distance from the position.
	 * 
	 * @param position position the object should be nearest to
	 * @param distance maximum distance from the position
	 * @return nearest object's ID or null if none is found
	 */
	public Integer getNearestObjectId(IVector2 position, IVector1 distance);

	/**
	 * Adds a new executable action to the environment.
	 * 
	 * @param action the new action
	 */
	public void addAction(ISimAction action);

	/**
	 * Removes an action from the environment.
	 * 
	 * @param actionName name of the action
	 */
	public void removeAction(String actionName);

	/**
	 * Performs an action.
	 * 
	 * @param actionName name of the action
	 * @param actorId ID of the actor performing the action
	 * @param objectId ID of the object acted upon (may be null)
	 * @param parameters parameters for the action (may be null)
	 * @return true if the action was successful, false otherwise
	 */
	public boolean performAction(String actionName, Integer actorId,
			Integer objectId, List parameters);

	/**
	 * Returns the size of the simulated area.
	 * 
	 * @return size of the simulated area
	 */
	public IVector2 getAreaSize();

	/**
	 * Retrieves a random position within the simulation area with a minimum
	 * distance from the edge.
	 * 
	 * @param distance minimum distance from the edge
	 */
	public IVector2 getRandomPosition(IVector2 distance);
}
