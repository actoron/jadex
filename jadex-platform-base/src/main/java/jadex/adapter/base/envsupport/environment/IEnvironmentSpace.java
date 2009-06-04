
package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
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
	 *  Add a space percept type.
	 *  @param typename The percept name.
	 *  @param objecttypes The objecttypes.
	 *  @param agenttypes The agenttypes.
	 */
	public void addPerceptType(PerceptType percepttype);
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removePerceptType(String typename);
	
	/**
	 *  Get a space percept type.
	 *  @param percepttype The name of the percept type.
	 *  @return The percept type. 
	 */
	public PerceptType getPerceptType(String percepttype);
	
	/**
	 *  Add a space process type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceProcessType(String typename, Class clazz, Map properties);
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removeSpaceProcessType(String typename);
	
	/**
	 * Creates a space process.
	 * @param id ID of the space process
	 * @param type The process type.
	 */
	public void createSpaceProcess(String type, Map props);

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
	 * @param typename the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @return the object.
	 */
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks);
	
	/**
	 *  Add a space object type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceObjectType(String typename, Map properties);
	
	/**
	 *  Remove a space object type.
	 *  @param typename The type name.
	 */
	public void removeSpaceObjectType(String typename);
	
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
	public ISpaceObject[] getSpaceObjectsByType(String type);
			
	/**
	 * Adds an space action.
	 * @param name the action ID
	 * @param action the action
	 */
	public void addSpaceAction(String name, ISpaceAction action);
	
	/**
	 * Removes an space action.
	 * @param name the action ID
	 */
	public void removeSpaceAction(String name);
	
	/**
	 * Perform an space action. It will be executed
	 * according to the space execution policy (e.g. at the end of a round). 
	 * @param name Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void performSpaceAction(String name, Map parameters, IResultListener listener);

	/**
	 * Performs a space action.
	 * @param name Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(String name, Map parameters);
	
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
	 *  @param owner The object owner.
	 */
	public void setOwner(Object id, IAgentIdentifier owner);
	
	/**
	 *  Get the owned objects.
	 *  @return The owned objects. 
	 */
	public ISpaceObject[] getOwnedObjects(IAgentIdentifier owner);
	
	/**
	 * Adds a dataview to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addDataView(String name, IDataView view);
	
	/**
	 * Removes a dataview from the space.
	 * @param name name of the dataview
	 */
	public void removeDataView(String name);
	
	/**
	 * Gets a specific dataview.
	 * @param name name of the dataview
	 * @return the dataview
	 */
	public IDataView getDataView(String name);
	
	/**
	 * Get all available dataviews in this space.
	 * @return all available dataviews
	 */
	public Map getDataViews();
	
	/** 
	 * Steps the space. May be non-functional in spaces that do not have
	 * a concept of steps.
	 * @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 * /
	public void step(IVector1 progress);*/
	
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
