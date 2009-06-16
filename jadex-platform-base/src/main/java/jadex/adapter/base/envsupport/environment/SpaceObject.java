package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Default implementation of a space object. 
 */
public class SpaceObject extends SynchronizedPropertyObject implements ISpaceObject
{
	//-------- attributes --------
	
	/** The object's ID. */
	protected Object id;

	/** The object's type. */
	protected String typename;
	
	/** The object's tasks. */
	protected Map tasks;

	/** Event listeners. */
	protected List listeners;
	
	/** The fetcher. */
	protected SimpleValueFetcher fetcher;
	
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
	public SpaceObject(Object id, String typename, Map properties, List tasks, List listeners, Object monitor, IEnvironmentSpace space)
	{
		super(monitor);
		
		this.id = id;
		this.typename = typename;
		this.properties = properties;
		this.listeners = listeners;
		
		this.tasks = new LinkedHashMap();
		if(tasks != null)
		{
			for(Iterator it = tasks.iterator(); it.hasNext(); )
			{
				IObjectTask task = (IObjectTask)it.next();
				this.tasks.put(task.getId(), task);
			}
		}
		this.fetcher = new SimpleValueFetcher();
		fetcher.setValue("$object", this);
		fetcher.setValue("$space", space);
	}
	
	/**
	 *  Bean constructor.
	 */
	public SpaceObject()
	{
		super(new Object());
	}
	
	//-------- methods --------
	
	/**
	 * Returns a property.
	 * 
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		synchronized(monitor)
		{
			Object ret = super.getProperty(name);
			
			if(ret instanceof IParsedExpression)
			{
				ret = ((IParsedExpression) ret).getValue(fetcher);
			}
			
			return ret;
		}
	}
	
	/**
	 *  Only for debugging.
	 * /
	public void setProperty(String name, Object value)
	{
		if(getType().equals("cleaner") && name.equals(Space2D.PROPERTY_POSITION))
			System.out.println("Setting: "+name+" "+value);
		
		super.setProperty(name, value);
	}*/
	
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
	 * Set the id of the object.
	 */
	// Bean setter.
	public void	setId(Object id)
	{
		synchronized(monitor)
		{
			this.id	= id;
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
	 * Set the type of the object.
	 */
	// Bean setter.
	public void	setType(String type)
	{
		synchronized(monitor)
		{
			this.typename	= type;
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
			if(tasks.containsKey(task.getId()))
				throw new RuntimeException("Task already exists: "+this+", "+task);
			task.start(this);
			tasks.put(task.getId(), task);
		}
	}

	/**
	 * Removes a task from the object.
	 * @param task	The task.
	 */
	public void removeTask(Object taskid)
	{
		synchronized(monitor)
		{
			IObjectTask task = getTask(taskid);
			if(task!=null)
			{
				task.shutdown(this);
				tasks.remove(taskid);
			}
		}
	}
	
	/**
	 *  Returns all tasks of the object for introspection.
	 *  @return all tasks of the object
	 */
	public Collection getTasks()
	{
		synchronized(monitor)
		{
			return tasks.values();
		}
	}
	
	/**
	 *  Get a specific task.
	 *  @param id The task id.
	 *  @return The task.
	 */
	public IObjectTask getTask(Object id)
	{
		synchronized(monitor)
		{
			return (IObjectTask)tasks.get(id);
		}
	}
	
	/**
	 * Removes all tasks from the object.
	 */
	public void clearTasks()
	{
		synchronized(monitor)
		{
			IObjectTask[] atasks = (IObjectTask[])tasks.values().toArray(new IObjectTask[tasks.size()]);
			for(int i = 0; i < atasks.length; ++i)
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
			IObjectTask[] atasks = (IObjectTask[])tasks.values().toArray(new IObjectTask[tasks.size()]);
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
		buf.append(properties);
		buf.append(", tasks=");
		buf.append(tasks);
		buf.append(")");
		return buf.toString();
	}
}