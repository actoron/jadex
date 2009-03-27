package jadex.adapter.base.envsupport.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.bridge.IClock;

/**
 *  Default implementation of a space object. 
 */
public class EnvironmentObject implements ISpaceObject
{
	/** The object's ID. */
	private Long		objectId_;

	/** The object's type. */
	private Object		type_;

	/** The object's properties. */
	private Map			properties_;

	/** The object's tasks (task names -> tasks). */
	private Map			tasks_;

	/** Event listeners. */
	private List		listeners_;
	
	/**
	 * Creates a new EnvironmentObject.
	 * 
	 * @param objectId the object's ID
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 */
	public EnvironmentObject(Long objectId, Object type, Map properties, List tasks, List listeners)
	{
		objectId_ = objectId;
		type_ = type;
		
		properties_ = new HashMap();
		if (properties != null)
		{
			properties_.putAll(properties);
		}
		
		tasks_ = new HashMap();
		if (tasks != null)
		{
			for (Iterator it = tasks.iterator(); it.hasNext(); )
			{
				IObjectTask task = (IObjectTask) it.next();
				tasks_.put(task.getId(), task);
			}
		}
		
		listeners_ = new ArrayList();
		if (listeners != null)
		{
			listeners_.addAll(listeners);
		}
	}
	
	/**
	 * Returns the type of the object.
	 * 
	 * @return the type
	 */
	public synchronized Object getType()
	{
		return type_;
	}
	
	/**
	 * Returns an object's property.
	 * 
	 * @param name name of the property
	 * @return the property
	 */
	public synchronized Object getProperty(String name)
	{
		return properties_.get(name);
	}

	/**
	 * Sets an object's property.
	 * 
	 * @param name name of the property
	 * @param property the property
	 */
	public synchronized void setProperty(String name, Object property)
	{
		properties_.put(name, property);
	}

	/**
	 * Returns a copy of all of the object's properties.
	 * 
	 * @return the properties
	 */
	public synchronized Map getProperties()
	{
		return new HashMap(properties_);
	}

	/**
	 * Adds a new task for the object.
	 * 
	 * @param task new task
	 */
	public synchronized void addTask(IObjectTask task)
	{
		task.start(this);
		tasks_.put(task.getId(), task);
	}

	/**
	 * Returns a task by its ID.
	 * 
	 * @param taskId ID of the task
	 * @return the task
	 */
	public synchronized IObjectTask getTask(Object taskId)
	{
		return (IObjectTask) tasks_.get(taskId);
	}

	/**
	 * Removes a task from the object.
	 * 
	 * @param taskId ID of the task
	 */
	public synchronized void removeTask(Object taskId)
	{
		IObjectTask task = (IObjectTask) tasks_.remove(taskId);
		if(task != null)
		{
			task.shutdown(this);
		}
	}
	
	/**
	 * Removes all tasks from the object.
	 * 
	 */
	public synchronized void clearTasks()
	{
		Object[] taskIds = tasks_.keySet().toArray();
		for (int i = 0; i < taskIds.length; ++i)
		{
			removeTask(taskIds[i]);
		}
	}
	
	/**
	 * Updates the object to the current time.
	 * 
	 * time the current time	
	 * @param deltaT the time difference that has passed
	 */
	public synchronized void updateObject(long time, IVector1 deltaT)
	{
		Object[] tasks = tasks_.values().toArray();
		for(int i = 0; i < tasks.length; ++i)
		{
			IObjectTask task = (IObjectTask) tasks[i];
			task.execute(time, deltaT, this);
		}
	}
	
	/**
	 * Fires an ObjectEvent.
	 * 
	 * @param event the ObjectEvent
	 */
	public synchronized void fireObjectEvent(ObjectEvent event)
	{
		if (event.getParameter("object_id") == null)
		{
			event.setParameter("object_id", objectId_);
		}
		
		for (Iterator it = listeners_.iterator(); it.hasNext(); )
		{
			IObjectListener listener = (IObjectListener) it.next();
			listener.dispatchObjectEvent(event);
		}
	}
	
}