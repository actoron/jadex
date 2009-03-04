package jadex.bdi.planlib.envsupport.environment.space;

import jadex.bdi.planlib.envsupport.environment.Environment;
import jadex.bdi.planlib.envsupport.environment.IEnvironmentObject;
import jadex.bdi.planlib.envsupport.environment.IObjectListener;
import jadex.bdi.planlib.envsupport.environment.ObjectEvent;
import jadex.bdi.planlib.envsupport.environment.task.IObjectTask;
import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bridge.IClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractSpace implements ISpace
{
	/** The environment */
	protected Environment	environment_;
	
	/** Available actions in the space. */
	protected Map			actions_;
	
	/** The environment processes. */
	protected Map			processes_;

	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map			envObjects_;
	
	/** Types of EnvironmentObjects and Lists of EnvironmentObjects of that type (typed view). */
	protected Map			envObjectsByType_;
	
	/** Space properties. */
	protected Map			spaceProperties_;
	
	/** Object properties. */
	private Map				objectProperties_;
	
	/** Object tasks. */
	private Map				objectTasks_;
	
	/** Object listeners. */
	private Map				objectListeners_;
	
	public AbstractSpace()
	{
		actions_ = Collections.synchronizedMap(new HashMap());
		processes_ = Collections.synchronizedMap(new HashMap());
		envObjects_ = Collections.synchronizedMap(new HashMap());
		envObjectsByType_ = Collections.synchronizedMap(new HashMap());
		spaceProperties_ = Collections.synchronizedMap(new HashMap());
		objectProperties_ = Collections.synchronizedMap(new HashMap());
		objectTasks_ = Collections.synchronizedMap(new HashMap());
		objectListeners_ = Collections.synchronizedMap(new HashMap());
	}
	
	/**
	 * This method gets executed when the space is added to an environment.
	 * 
	 * @param engine the environment engine
	 */
	public void start(Environment environment)
	{
		environment_ = environment;
	}
	
	/**
	 * This method gets executed when the space is removed from an environment.
	 */
	public void shutdown()
	{
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
	 * Adds an object to this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void addEnvironmentObject(Long objectId, Map properties, List tasks, List listeners)
	{
		if (properties == null)
		{
			properties = new HashMap();
		}
		if (tasks == null)
		{
			tasks = new ArrayList();
		}
		if (listeners == null)
		{
			listeners = new ArrayList();
		}
		
		synchronized(envObjects_)
		{
			synchronized(envObjectsByType_)
			{
				synchronized(objectProperties_)
				{
					synchronized(objectTasks_)
					{
						synchronized(objectListeners_)
						{
							IEnvironmentObject obj = new EnvironmentObjectAccess(objectId);
							envObjects_.put(objectId, obj);
							List typeObjects = (List) envObjectsByType_.get(obj.getType());
							if (typeObjects == null)
							{
								typeObjects = Collections.synchronizedList(new ArrayList());
								envObjectsByType_.put(obj.getType(), typeObjects);
							}
							typeObjects.add(obj);
							
							Map taskMap = Collections.synchronizedMap(new HashMap());
							for (Iterator it = tasks.iterator(); it.hasNext(); )
							{
								IObjectTask task = (IObjectTask) it.next();
								taskMap.put(task.getId(), task);
							}
							objectTasks_.put(objectId, taskMap);
							
							objectListeners_.put(objectId, Collections.synchronizedList(new ArrayList(listeners)));
							
							objectProperties_.put(objectId, Collections.synchronizedMap(new HashMap(properties)));
						}
					}
				}
			}
		}
	}
	
	/** 
	 * Removes an object from this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void removeEnvironmentObject(Long objectId)
	{
		synchronized(envObjects_)
		{
			synchronized(envObjectsByType_)
			{
				synchronized(objectProperties_)
				{
					synchronized(objectTasks_)
					{
						synchronized(objectListeners_)
						{
							IEnvironmentObject obj = (IEnvironmentObject) envObjects_.get(objectId);

							// avoid NullPointerException for invalid simId
							if (obj != null) 
							{

								// shutdown and jettison tasks
								obj.clearTasks();
								objectTasks_.remove(objectId);

								// jettison properties
								objectProperties_.remove(objectId);

								// remove object
								envObjects_.remove(objectId);
								((List) envObjectsByType_.get(obj.getType())).remove(obj);
								
								// signal removal
								ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
								event.setParameter("space_id", getId());
								obj.fireObjectEvent(event);
								
								// jettison listeners
								objectListeners_.remove(objectId);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns an environment object in this space.
	 * 
	 * @param objectId the object's ID
	 * @return the object in this space
	 */
	public IEnvironmentObject getEnvironmentObject(Long objectId)
	{
		return (IEnvironmentObject) envObjects_.get(objectId);
	}
	
	private class EnvironmentObjectAccess implements IEnvironmentObject
	{
		/** The object's ID */
		private Long objectId_;
		
		/**
		 * New Object Access
		 * @param objectId the object's ID
		 */
		public EnvironmentObjectAccess(Long objectId)
		{
			objectId_ = objectId;
		}
		
		/**
		 * Returns the type of the object.
		 * 
		 * @return the type
		 */
		public Object getType()
		{
			return environment_.getObjectType(objectId_);
		}
		
		/**
		 * Returns an object's property.
		 * 
		 * @param name name of the property
		 * @return the property
		 */
		public Object getProperty(String name)
		{
			Map props = (Map) objectProperties_.get(objectId_);
			return props.get(name);
		}

		/**
		 * Sets an object's property.
		 * 
		 * @param name name of the property
		 * @param property the property
		 */
		public void setProperty(String name, Object property)
		{
			Map props = (Map) objectProperties_.get(objectId_);
			props.put(name, property);
		}

		/**
		 * Returns a copy of all of the object's properties.
		 * 
		 * @return the properties
		 */
		public Map getProperties()
		{
			Map props = (Map) objectProperties_.get(objectId_);
			synchronized(props)
			{
				return new HashMap(props);
			}
		}

		/**
		 * Adds a new task for the object.
		 * 
		 * @param task new task
		 */
		public void addTask(IObjectTask task)
		{
			task.start(this);
			Map tasks = (Map) objectTasks_.get(objectId_);
			tasks.put(task.getId(), task);
		}

		/**
		 * Returns a task by its ID.
		 * 
		 * @param taskId ID of the task
		 * @return the task
		 */
		public IObjectTask getTask(Object taskId)
		{
			Map tasks = (Map) objectTasks_.get(objectId_);
			return (IObjectTask) tasks.get(taskId);
		}

		/**
		 * Removes a task from the object.
		 * 
		 * @param taskId ID of the task
		 */
		public void removeTask(Object taskId)
		{
			Map tasks = (Map) objectTasks_.get(objectId_);
			IObjectTask task = (IObjectTask) tasks.remove(taskId);
			task.shutdown(this);
		}
		
		public void clearTasks()
		{
			Map tasks = (Map) objectTasks_.get(objectId_);
			synchronized(tasks)
			{
				HashSet taskIds = new HashSet(tasks.keySet());
				for (Iterator it = taskIds.iterator(); it.hasNext(); )
				{
					Object taskId = it.next();
					removeTask(taskId);
				}
			}
		}
		
		/**
		 * Updates the object to the current time.
		 * 
		 * @param clock the clock	
		 * @param deltaT the time difference that has passed
		 */
		public void updateObject(IClock clock, IVector1 deltaT)
		{
			Object[] tasks = ((Map) objectTasks_.get(objectId_)).values().toArray();
			for(int i = 0; i < tasks.length; ++i)
			{
				IObjectTask task = (IObjectTask) tasks[i];
				task.execute(clock, deltaT, this);
			}
		}
		
		/**
		 * Fires an ObjectEvent.
		 * 
		 * @param event the ObjectEvent
		 */
		public void fireObjectEvent(ObjectEvent event)
		{
			if (event.getParameter("object_id") == null)
			{
				event.setParameter("object_id", objectId_);
			}
			
			List listeners = (List) objectListeners_.get(objectId_);
			synchronized(listeners)
			{
				for (Iterator it = listeners.iterator(); it.hasNext(); )
				{
					IObjectListener listener = (IObjectListener) it.next();
					listener.dispatchObjectEvent(event);
				}
			}
			environment_.fireObjectEvent(objectId_, event);
		}
	}
}
