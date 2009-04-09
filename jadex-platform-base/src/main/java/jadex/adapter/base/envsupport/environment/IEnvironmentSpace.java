
package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.agentaction.IAgentAction;
import jadex.adapter.base.envsupport.environment.view.IView;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.IPropertyObject;
import jadex.commons.concurrent.IResultListener;

import java.util.List;
import java.util.Map;

/**
 *  Main interface for an environment space.
 */
public interface IEnvironmentSpace extends ISpace, IPropertyObject
{
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName();
	
	/**
	 * Adds a space process.
	 * @param id ID of the space process
	 * @param process new space process
	 */
	public void addSpaceProcess(Object id, ISpaceProcess process);

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id);

	/**
	 * Removes a space process.
	 * @param id ID of the space process
	 */
	public void removeSpaceProcess(Object id);
	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object.
	 */
	public ISpaceObject createSpaceObject(Object type, Object owner, Map properties, List tasks, List listeners);
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(Object id);
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object id);
	
	/**
	 * Get all space object of a specific type.
	 * @param type The space object type.
	 * @return The space objects of the desired type.
	 */
	public ISpaceObject[] getSpaceObjectsByType(Object type);
	
	/**
	 * Adds a space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addSpaceAction(Object id, ISpaceAction action);
	
	/**
	 * Removes a space action.
	 * @param actionId the action ID
	 */
	public void removeSpaceAction(Object id);
	
	/**
	 * Performs a space action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(Object id, Map parameters);
	
	/**
	 * Adds an agent action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addAgentAction(Object id, IAgentAction action);
	
	/**
	 * Removes an agent action.
	 * @param actionId the action ID
	 */
	public void removeAgentAction(Object id);
	
	/**
	 * Perform an agent action. It will be executed
	 * according to the space execution policy (e.g. at the end of a round). 
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void performAgentAction(Object id, Map parameters, IResultListener listener);
	
	/**
	 *  Get the owner of an object.
	 *  @param id The id.
	 *  @return The owner.
	 */
//	public Object getOwner(Object id);
	
	/**
	 * Adds a percept generator.
	 * @param id The percept generator id.
	 * @param gen The percept generator.
	 */
	public void addPerceptGenerator(Object id, IPerceptGenerator gen);
	
	/**
	 * Remove a percept generator.
	 * @param id The percept generator id.
	 */
	public void removePerceptGenerator(Object id);
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, Object owner);
	
	/**
	 *  Get the owned objects.
	 *  @return The owned objects. 
	 */
	public ISpaceObject[] getOwnedObjects(Object owner);
	
	/**
	 * Adds a view to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addView(String name, IView view);
	
	/**
	 * Removes a view from the space.
	 * @param name name of the view
	 */
	public void removeView(String name);
	
	/**
	 * Gets a specific view.
	 * @param name name of the view
	 * @return the view
	 */
	public IView getView(String name);
	
	/**
	 * Get all available views in this space.
	 * @return list of view names
	 */
	public List getViewNames();
	
	/** Sets the space executor that executes the space.
	 *  @param the space executor
	 */
	public void setSpaceExecutor(ISpaceExecutor executor);
	
	/** 
	 * Steps the space. May be non-functional in spaces that do not have
	 * a concept of steps.
	 * @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 */
	public void step(IVector1 progress);
	
	/**
	 *  Add an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void addEnvironmentListener(IEnvironmentListener listener);
	
	/**
	 *  Remove an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void removeEnvironmentListener(IEnvironmentListener listener);
}
