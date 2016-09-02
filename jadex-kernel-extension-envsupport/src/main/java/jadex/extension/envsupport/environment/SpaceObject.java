package jadex.extension.envsupport.environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.MObjectType;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Default implementation of a space object. 
 */
@Reference
public class SpaceObject extends SynchronizedPropertyObject implements ISpaceObject
{
	//-------- attributes --------
	
	/** The object's ID. */
	protected Object id;

	/** The object's typename. */
	protected String typename;
	
	/** The object's tasks. */
	protected Map tasks;

	/** The task listeners. */
	protected MultiCollection<Object, IResultListener<?>> tasklisteners;

	/** The fetcher. */
	protected SimpleValueFetcher fetcher;
	
	/** The space. */
	protected AbstractEnvironmentSpace	space;
	
	//-------- constructors --------
	
	/**
	 * Creates a new EnvironmentObject.
	 * 
	 * @param objectId the object's ID
	 * @param typename the object's type
	 * @param properties initial properties (may be null)
	 * @param propertiesMeta the meta data of the properties
	 * @param tasks initial task list (may be null)
	 */
	public SpaceObject(Object id, MObjectType type, Map properties, List tasks, Object monitor, AbstractEnvironmentSpace space)
	{
		super(type, monitor);
		
		this.id = id;
		this.typename = type.getName();
		this.properties = properties;
		this.space = space;
		
		this.tasks = new LinkedHashMap();
		if(tasks != null)
		{
			for(Iterator it = tasks.iterator(); it.hasNext(); )
			{
				IObjectTask task = (IObjectTask)it.next();
				this.tasks.put(task.getProperty(IObjectTask.PROPERTY_ID), task);
			}
		}
		this.fetcher = new SimpleValueFetcher(space.getFetcher());
		fetcher.setValue("$object", this);
	}
	
	// HACK constructor!!!
	// For what purpose space objects must be sent via service calls?
	// Makes it easy to create 'wrong' space objects
//	/**
//	 *  Bean constructor.
//	 */
//	public SpaceObject()
//	{
//		super(null, new Object());
//	}
	
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
				ret = ((IParsedExpression)ret).getValue(fetcher);
			}
			
			return ret;
		}
	}
	
	/**
	 *  Only for debugging.
	 */
	public void setProperty(String name, Object value)
	{
//		if(getType().equals("waste") && name.equals(Space2D.PROPERTY_POSITION))
//			System.out.println("Setting: "+name+" "+value);
		
		Object	oldvalue;
//		synchronized(getMonitor())
//		{
		oldvalue	= super.getProperty(name); 
		super.setProperty(name, value);
//		}
		
		space.fireObjectEvent(this, name, oldvalue);
	}
	
	/**
	 *  Get the objects id.
	 *  @return The object id.
	 */
	public Object getId()
	{
//		synchronized(monitor)
//		{
			return id;
//		}
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
//		synchronized(monitor)
//		{
			return typename;
//		}
	}
	
	/**
	 * Set the type of the object.
	 */
	// Bean setter.
	public void	setType(String type)
	{
		synchronized(monitor)
		{
			typename = type;
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
			if(tasks.containsKey(task.getProperty(IObjectTask.PROPERTY_ID)))
				throw new RuntimeException("Task already exists: "+this+", "+task);
			task.start(this);
			tasks.put(task.getProperty(IObjectTask.PROPERTY_ID), task);
		}
	}

	/**
	 * Removes a task from the object.
	 * @param task	The task.
	 */
	public void removeTask(Object taskid, Exception e)
	{
		synchronized(monitor)
		{
			if(tasklisteners!=null && tasklisteners.containsKey(taskid))
			{
				Collection<IResultListener<?>>	listeners	= tasklisteners.getCollection(taskid);
				for(Iterator it=listeners.iterator(); it.hasNext(); )
				{
					if(e==null)
					{
						((IResultListener)it.next()).resultAvailable(taskid);
					}
					else
					{
						((IResultListener)it.next()).exceptionOccurred(e);
					}
				}
				tasklisteners.remove(taskid);
				
				if(tasklisteners.isEmpty())
					tasklisteners	= null;
			}

			IObjectTask task = getTask(taskid);
			if(task!=null)
			{
				try
				{
					task.shutdown(this);
				}
				catch (Exception e2)
				{
					// Todo: logger.
					e2.printStackTrace();
				}
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
				removeTask(atasks[i], null);
			}
		}
	}
	
	/**
	 *  Add a result listener to a task.
	 *  The result will be the task id.
	 *  If the task is already finished, the listener will be notified.
	 */
	public void addTaskListener(Object taskid, IResultListener listener)
	{
		synchronized(monitor)
		{
			if(tasks.containsKey(taskid))
			{				
				if(tasklisteners==null)
					tasklisteners	= new MultiCollection<Object, IResultListener<?>>();
				
				tasklisteners.add(taskid, listener);
			}
			else
			{
				listener.resultAvailable(taskid);
			}
		}
	}
	
	/**
	 *  Remove a result listener from a task.
	 */
	public void removeTaskListener(Object taskid, IResultListener listener)
	{
		synchronized(monitor)
		{
			if(tasklisteners!=null)
			{
				tasklisteners.removeObject(taskid, listener);
				
				if(tasklisteners.isEmpty())
					tasklisteners	= null;
			}			
		}
	}

	/**
	 * Updates the object to the current time.
	 * time the current time	
	 *  @param progress	The time that has passed according to the environment executor.
	 *  @param clock	The clock service.
	 */
	public void updateObject(IEnvironmentSpace space, long progress, IClockService clock)
	{
		synchronized(monitor)
		{
			IObjectTask[] atasks = (IObjectTask[])tasks.values().toArray(new IObjectTask[tasks.size()]);
			for(int i = 0; i < atasks.length; ++i)
			{
				try
				{
					if(!atasks[i].isFinished(space, this))
					{
						atasks[i].execute(space, this, progress, clock);
					}
					else
					{
						removeTask(atasks[i].getProperty(IObjectTask.PROPERTY_ID), null);
					}
				}
				catch(Exception e)
				{
					// Todo: logger.
//					e.printStackTrace();
					
					if(tasklisteners!=null && tasklisteners.containsKey(atasks[i].getProperty(IObjectTask.PROPERTY_ID)))
					{
						Collection<IResultListener<?>>	listeners	= tasklisteners.getCollection(atasks[i].getProperty(IObjectTask.PROPERTY_ID));
						for(Iterator<IResultListener<?>> it=listeners.iterator(); it.hasNext(); )
						{
							it.next().exceptionOccurred(e);
						}
						tasklisteners.remove(atasks[i].getProperty(IObjectTask.PROPERTY_ID));				
						if(tasklisteners.isEmpty())
							tasklisteners	= null;

					}
					
					removeTask(atasks[i].getProperty(IObjectTask.PROPERTY_ID), e);
				}
			}
		}
	}
	
	/**
	 *  Bean accessor.
	 *  For serializing a space object,
	 *  replace dynamic expressions with current values.
	 */
	public Map getProperties()
	{
		Map	ret	= new HashMap();
		for(Iterator it=getPropertyNames().iterator(); it.hasNext(); )
		{
			String	prop	= (String)it.next();
			ret.put(prop, getProperty(prop));
		}
		return ret;
	}
	
	/**
	 *  Test if has a property.
	 */
	public boolean hasProperty(String name) 
	{
		boolean ret = false;
		synchronized(monitor) 
		{
			MObjectType type = space.getSpaceObjectType(typename);
			if(type!=null)
			{
				ret = type.getProperty(name)!=null;
			}
		}
		return ret;
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