package jadex.bdi.planlib.envsupport.environment;

import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bridge.IClock;

import java.util.List;
import java.util.Map;

public interface IEnvironmentSpace
{
	/**
	 * Adds a space process.
	 * 
	 * @param process new space process
	 */
	public void addSpaceProcess(ISpaceProcess process);

	/**
	 * Returns a space process.
	 * 
	 * @param processId ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object processId);

	/**
	 * Removes a space process.
	 * 
	 * @param processId ID of the space process
	 */
	public void removeSpaceProcess(Object processId);
	
	/** 
	 * Creates an object in this space.
	 * 
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public Long createSpaceObject(Object type, Map properties, List tasks, List listeners);
	
	/** 
	 * Destroys an object in this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Long objectId);
	
	/**
	 * Returns an object in this space.
	 * 
	 * @param objectId the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Long objectId);
	
	/**
	 * Returns the space's ID.
	 * 
	 * @return the space's ID.
	 */
	public Object getId();
}
