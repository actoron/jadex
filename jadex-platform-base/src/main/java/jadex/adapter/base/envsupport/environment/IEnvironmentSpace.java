
package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.contextservice.ISpace;

import java.util.List;
import java.util.Map;

public interface IEnvironmentSpace extends ISpace
{
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName();
	
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
	public Object createSpaceObject(Object type, Map properties, List tasks, List listeners);
	
	/** 
	 * Destroys an object in this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Object objectId);
	
	/**
	 * Returns an object in this space.
	 * 
	 * @param objectId the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object objectId);
	
	/**
	 * Gets a space property
	 * 
	 * @param id the property's ID
	 * @return the property
	 */
	public Object getSpaceProperty(Object id);
	
	/**
	 * Sets a space property
	 * 
	 * @param id the property's ID
	 * @param property the property
	 */
	public void setSpaceProperty(Object id, Object property);
	
	/**
	 * Adds an environment action.
	 * 
	 * @param action the action
	 */
	public void addSpaceAction(ISpaceAction action);
	
	/**
	 * Removes an environment action.
	 * 
	 * @param actionId the action ID
	 */
	public void removeAction(Object actionIs);
	
	/**
	 * Performs an environment action.
	 * 
	 * @param actionId ID of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performAction(Object actionId, Map parameters);
	
}
