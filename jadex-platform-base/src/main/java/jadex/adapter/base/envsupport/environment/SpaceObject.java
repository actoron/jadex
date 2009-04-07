package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.SReflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Default implementation of a space object. 
 */
public class SpaceObject extends PropertyHolder implements ISpaceObject
{
	//-------- attributes --------
	
	/** The object's ID. */
	protected Object id;

	/** The object's type. */
	protected Object type;
	
	/** The object's owner. */
	protected Object owner;

	/** The object's tasks (task names -> tasks). */
	protected Map tasks;

	/** Event listeners. */
	protected List listeners;
	
	/** The monitor. */
	//protected Object monitor;
	
	//-------- constructors --------
	
	/**
	 * Creates a new EnvironmentObject.
	 * 
	 * @param objectId the object's ID
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 */
	public SpaceObject(Object id, Object type, Object owner, Map properties, List tasks, List listeners, Object monitor)
	{
		this.id = id;
		this.type = type;
		this.owner = owner;
		this.properties = properties;
//		this.tasks = tasks;
		this.listeners = listeners;
		setMonitor(monitor);
		
//		this.properties = new HashMap();
//		if(properties != null)
//		{
//			properties.putAll(properties);
//		}
//		
		this.tasks = new HashMap();
		if(tasks != null)
		{
			for (Iterator it = tasks.iterator(); it.hasNext(); )
			{
				IObjectTask task = (IObjectTask) it.next();
				this.tasks.put(task.getId(), task);
			}
		}
//		
//		this.listeners = new ArrayList();
//		if (listeners != null)
//		{
//			listeners.addAll(listeners);
//		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the objects id.
	 *  @return The object id.
	 */
	public Object getId()
	{
		synchronized(monitor)
		{
			return id;
		}
	}
	
	/**
	 * Returns the type of the object.
	 * @return the type
	 */
	public Object getType()
	{
		synchronized(monitor)
		{
			return type;
		}
	}
	
	/**
	 * Returns the owner of the object.
	 * @return The owner.
	 */
	public Object getOwner()
	{
		synchronized(monitor)
		{
			return owner;
		}
	}

	/**
<<<<<<< .mine
	 * Sets an object's property.
	 * @param name name of the property
	 * @param value the property
	 */
	public void setProperty(String name, Object value)
	{
		Object oldval;
		synchronized(monitor)
		{
			if(properties==null)
				properties= new HashMap();
//			if(name.indexOf("pos")!=-1)
//				System.out.println("here");
			oldval = properties.get(name);
			properties.put(name, value);
		}
		pcs.firePropertyChange(name, oldval, value);
	}

	/**
	 * Returns a copy of all of the object's properties.
	 * @return the properties
	 */
	public Map getProperties()
	{
		synchronized(monitor)
		{
			return properties==null? Collections.EMPTY_MAP: properties;
		}
	}

	/**
	 * Adds a new task for the object.
	 * @param task new task
	 */
	public void addTask(IObjectTask task)
	{
		synchronized(monitor)
		{
			task.start(this);
			tasks.put(task.getId(), task);
		}
	}

	/**
	 * Returns a task by its ID.
	 * @param taskId ID of the task
	 * @return the task
	 */
	public IObjectTask getTask(Object taskId)
	{
		synchronized(monitor)
		{
			return (IObjectTask) tasks.get(taskId);
		}
	}

	/**
	 * Removes a task from the object.
	 * @param taskId ID of the task
	 */
	public void removeTask(Object taskId)
	{
		synchronized(monitor)
		{
			IObjectTask task = (IObjectTask) tasks.remove(taskId);
			if(task != null)
			{
				task.shutdown(this);
			}
		}
	}
	
	/**
	 * Removes all tasks from the object.
	 */
	public void clearTasks()
	{
		synchronized(monitor)
		{
			Object[] taskIds = tasks.keySet().toArray();
			for (int i = 0; i < taskIds.length; ++i)
			{
				removeTask(taskIds[i]);
			}
		}
	}
	
	/**
	 * Updates the object to the current time.
	 * time the current time	
	 * @param deltaT the time difference that has passed
	 */
	public void updateObject(long time, IVector1 deltaT)
	{
		synchronized(monitor)
		{
			Object[] tasks = this.tasks.values().toArray();
			for(int i = 0; i < tasks.length; ++i)
			{
				IObjectTask task = (IObjectTask) tasks[i];
				task.execute(time, deltaT, this);
			}
		}
	}
	
	/**
	 * Fires an ObjectEvent.
	 * @param event the ObjectEvent
	 */
	public void fireObjectEvent(ObjectEvent event)
	{
		if(event.getParameter("object_id") == null)
		{
			event.setParameter("object_id", id);
		}
		
		if(listeners!=null)
		{
			for(Iterator it = listeners.iterator(); it.hasNext(); )
			{
				IObjectListener listener = (IObjectListener)it.next();
				listener.dispatchObjectEvent(event);
			}
		}
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(id=");
		buf.append(getId());
		buf.append(", type=");
		buf.append(getType());
		buf.append(", properties=");
		buf.append(getProperties());
		buf.append(", tasks=");
		buf.append(tasks);
		buf.append(")");
		return buf.toString();
	}
}