package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IAgentIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public abstract class AbstractEnvironmentSpace implements IEnvironmentSpace
{
	/** Available actions in the space. */
	protected Map			actions_;
	
	/** The environment processes. */
	protected Map			processes_;

	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map			spaceObjects_;
	
	/** Types of EnvironmentObjects and Lists of EnvironmentObjects of that type (typed view). */
	protected Map			spaceObjectsByType_;
	
	/** Space properties. */
	protected Map			spaceProperties_;
	
	/** Object ID counter for new IDs. */
	private AtomicCounter	objectIdCounter_;
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		actions_ = Collections.synchronizedMap(new HashMap());
		processes_ = Collections.synchronizedMap(new HashMap());
		spaceObjects_ = Collections.synchronizedMap(new HashMap());
		spaceObjectsByType_ = Collections.synchronizedMap(new HashMap());
		spaceProperties_ = Collections.synchronizedMap(new HashMap());
		objectIdCounter_ = new AtomicCounter();
	}
	
	/**
	 * Adds a space process.
	 * 
	 * @param process new space process
	 */
	public void addSpaceProcess(ISpaceProcess process)
	{
		processes_.put(process.getId(), process);
	}

	/**
	 * Returns a space process.
	 * 
	 * @param processId ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object processId)
	{
		return (ISpaceProcess) processes_.get(processId);
	}

	/**
	 * Removes a space process.
	 * 
	 * @param processId ID of the space process
	 */
	public void removeSpaceProcess(Object processId)
	{
		processes_.remove(processId);
	}
	
	/** 
	 * Creates an object in this space.
	 * 
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public Object createSpaceObject(Object type, Map properties, List tasks, List listeners)
	{
		Long objectId = null;
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				do
				{
					objectId = objectIdCounter_.getNext();
				}
				while (spaceObjects_.containsKey(objectId));
				
				ISpaceObject obj = new EnvironmentObject(objectId, type, properties, tasks, listeners);
				spaceObjects_.put(objectId, obj);
				List typeObjects = (List) spaceObjectsByType_.get(obj.getType());
				if (typeObjects == null)
				{
					typeObjects = Collections.synchronizedList(new ArrayList());
					spaceObjectsByType_.put(obj.getType(), typeObjects);
				}
				typeObjects.add(obj);
			}
		}
		return objectId;
	}
	
	/** 
	 * Destroys an object in this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void destroySpaceObject(Object objectId)
	{
		synchronized(spaceObjects_)
		{
			synchronized(spaceObjectsByType_)
			{
				ISpaceObject obj = (ISpaceObject) spaceObjects_.get(objectId);
				// shutdown and jettison tasks
				obj.clearTasks();

				// remove object
				spaceObjects_.remove(objectId);
				((List) spaceObjectsByType_.get(obj.getType())).remove(obj);

				// signal removal
				ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
				event.setParameter("space_name", getName());
				obj.fireObjectEvent(event);
			}
		}
	}
	
	/**
	 * Returns an object in this space.
	 * 
	 * @param objectId the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object objectId)
	{
		return (ISpaceObject) spaceObjects_.get(objectId);
	}
	
	/**
	 * Gets a space property
	 * 
	 * @param id the property's ID
	 * @return the property
	 */
	public Object getSpaceProperty(Object id)
	{
		return spaceProperties_.get(id);
	}
	
	/**
	 * Sets a space property
	 * 
	 * @param id the property's ID
	 * @param property the property
	 */
	public void setSpaceProperty(Object id, Object property)
	{
		spaceProperties_.put(id, property);
	}
	
	/**
	 * Adds an environment action.
	 * 
	 * @param action the action
	 */
	public void addSpaceAction(ISpaceAction action)
	{
		actions_.put(action.getId(), action);
	}
	
	/**
	 * Removes an environment action.
	 * 
	 * @param actionId the action ID
	 */
	public void removeAction(Object actionId)
	{
		actions_.remove(actionId);
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName()
	{
		return (String)spaceProperties_.get("name");
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public void setName(String name)
	{
		spaceProperties_.put("name", name);
	}
	
	/**
	 * Performs an environment action.
	 * 
	 * @param actionId ID of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performAction(Object actionId, Map parameters)
	{
		ISpaceAction action = (ISpaceAction) actions_.get(actionId);
		
		assert action != null;
		
		return action.perform(new HashMap(parameters), this);
	}
	
	public void agentAdded(IAgentIdentifier aid)
	{
	}
	
	public void agentRemoved(IAgentIdentifier aid)
	{
	}
	
	/**
	 * Synchronized counter class
	 */
	private class AtomicCounter
	{
		long count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Long getNext()
		{
			return new Long(count_++);
		}
	}
}
