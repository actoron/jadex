package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.SReflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Default implementation of a space object. 
 */
public class SpaceObject extends PropertyHolder implements ISpaceObject
{
	//-------- attributes --------
	
	/** The object's ID. */
	protected Object id;

	/** The object's type. */
	protected String typename;
	
	/** The object's tasks. */
	protected Set	tasks;

	/** Event listeners. */
	protected List listeners;
	
	/** The monitor. */
	//protected Object monitor;
	
	//-------- constructors --------
	
	/**
	 * Creates a new EnvironmentObject.
	 * 
	 * @param objectId the object's ID
	 * @param typename the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 */
	public SpaceObject(Object id, String typename, Map properties, List tasks, List listeners, Object monitor)
	{
		super(monitor);
		
		this.id = id;
		this.typename = typename;
		this.properties = properties;
//		this.tasks = tasks;
		this.listeners = listeners;
		
//		this.properties = new HashMap();
//		if(properties != null)
//		{
//			properties.putAll(properties);
//		}
//		
		this.tasks = new LinkedHashSet();
		if(tasks != null)
		{
			for (Iterator it = tasks.iterator(); it.hasNext(); )
			{
				IObjectTask task = (IObjectTask) it.next();
				this.tasks.add(task);
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
	public String getType()
	{
		synchronized(monitor)
		{
			return typename;
		}
	}

	/**
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
			if(tasks.contains(task))
				throw new RuntimeException("Task already exists: "+this+", "+task);
			task.start(this);
			tasks.add(task);
		}
	}

	/**
	 * Removes a task from the object.
	 * @param task	The task.
	 */
	public void removeTask(IObjectTask task)
	{
		synchronized(monitor)
		{
			if(!tasks.contains(task))
				throw new RuntimeException("Task does not exist: "+this+", "+task);
			task.shutdown(this);
			tasks.remove(task);
		}
	}
	
	/**
	 * Removes all tasks from the object.
	 */
	public void clearTasks()
	{
		synchronized(monitor)
		{
			IObjectTask[] atasks = (IObjectTask[])tasks.toArray(new IObjectTask[tasks.size()]);
			for (int i = 0; i < atasks.length; ++i)
			{
				removeTask(atasks[i]);
			}
		}
	}
	
	/**
	 * Updates the object to the current time.
	 * time the current time	
	 * @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 */
	public void updateObject(IEnvironmentSpace space, IVector1 progress)
	{
		synchronized(monitor)
		{
			IObjectTask[] atasks = (IObjectTask[])tasks.toArray(new IObjectTask[tasks.size()]);
			for(int i = 0; i < atasks.length; ++i)
			{
				atasks[i].execute(space, this, progress);
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