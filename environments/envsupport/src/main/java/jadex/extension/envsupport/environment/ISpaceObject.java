package jadex.extension.envsupport.environment;

import jadex.commons.meta.ITypedPropertyObject;

/**
 *  Interface for a space object.
 */
public interface ISpaceObject extends ITypedPropertyObject
{
	//-------- constants --------
	
	/** The owner property. */
	public static final String	PROPERTY_OWNER	= "owner";
	
	//-------- methods --------
	
	/**
	 *  Get the objects id.
	 *  @return The object id.
	 */
	public Object getId();
	
	/**
	 *  Returns the type of the object.
	 *  @return the type
	 */
	public String getType();
	
	/**
	 *  Returns an object's property.
	 *  @param name The name of the property.
	 *  @return The property.
	 */
//	public Object getProperty(String name);

	/**
	 *  Sets an object's property.
	 *  @param name name of the property
	 *  @param value the property
	 */
//	public void setProperty(String name, Object value);

	/**
	 *  Returns a copy of all of the object's properties.
	 *  @return the properties
	 */
//	public Map getProperties();

	/**
	 *  Adds a new task for the object.
	 *  @param task new task
	 */
//	public void addTask(IObjectTask task);

	/**
	 *  Returns a task by its id.
	 *  @param id The id of the task.
	 *  @return The task.
	 * /
	public IObjectTask getTask(Object id);*/

	/**
	 *  Removes a task from the object.
	 *  @param task	The task.
	 */
//	public void removeTask(IObjectTask task);
	
	/**
	 *  Removes all tasks from the object.
	 */
//	public void clearTasks();
	
	/**
	 *  Updates the object to the current time.
	 *  @param time the current time
	 *  @param deltaT the time difference that has passed
	 */
	// Internal method (not for user)
//	public void updateObject(long time, IVector1 deltaT);
}
