package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

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
	//-------- attributes --------
	
	/** Available actions in the space. */
	protected Map actions;
	
	/** The environment processes. */
	protected Map processes;

	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space properties. */
	protected Map spaceproperties;
	
	/** Object id counter for new ids. */
	protected AtomicCounter objectidcounter;
	
	/** The synchronization object. */
	protected SynchronizedObject syncobject;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		this.actions = new HashMap();
		this.processes = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceproperties = new HashMap();
		this.objectidcounter = new AtomicCounter();
		this.syncobject = new SynchronizedObject();
	}
	
	//-------- methods --------
	
	/**
	 * Adds a space process.
	 * @param process new space process
	 */
	public void addSpaceProcess(final ISpaceProcess process)
	{
		synchronized(syncobject.getMonitor())
		{
			processes.put(process.getId(), process);
		}
	}

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id )
	{
		synchronized(syncobject.getMonitor())
		{
			return (ISpaceProcess)processes.get(id);
		}
	}

	/**
	 * Removes a space process.
	 * @param id ID of the space process
	 */
	public void removeSpaceProcess(final Object id)
	{
		synchronized(syncobject.getMonitor())
		{
			processes.remove(id);
		}
	}
	
	/** 
	 * Creates an object in this space.
	 * @param type the object's type
	 * @param properties initial properties (may be null)
	 * @param tasks initial task list (may be null)
	 * @param listeners initial listeners (may be null)
	 * @return the object's ID
	 */
	public Object createSpaceObject(Object type, Map properties, List tasks, List listeners)
	{
		synchronized(syncobject.getMonitor())
		{
			Object id = null;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			ISpaceObject obj = new SpaceObject(id, type, properties, tasks, listeners, syncobject.getMonitor());
			spaceobjects.put(id, obj);
			List typeObjects = (List) spaceobjectsbytype.get(obj.getType());
			if (typeObjects == null)
			{
				typeObjects = Collections.synchronizedList(new ArrayList());
				spaceobjectsbytype.put(obj.getType(), typeObjects);
			}
			typeObjects.add(obj);
			return id;
		}
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(final Object id)
	{
		ISpaceObject obj;
		synchronized(syncobject.getMonitor())
		{
			obj = (ISpaceObject)spaceobjects.get(id);
			// shutdown and jettison tasks
			obj.clearTasks();

			// remove object
			spaceobjects.remove(id);
			((List) spaceobjectsbytype.get(obj.getType())).remove(obj);
		}
		
		// signal removal
		ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
		event.setParameter("space_name", getName());
		obj.fireObjectEvent(event);
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject(Object id)
	{
		synchronized(syncobject.getMonitor())
		{
			return (ISpaceObject)spaceobjects.get(id);
		}
	}
	
	/**
	 * Gets a space property
	 * @param id the property's ID
	 * @return the property
	 */
	public Object getSpaceProperty(Object id)
	{
		synchronized(syncobject.getMonitor())
		{
			return spaceproperties.get(id);
		}
	}
	
	/**
	 * Sets a space property
	 * @param id the property's ID
	 * @param property the property
	 */
	public void setSpaceProperty(final Object id, final Object property)
	{
		synchronized(syncobject.getMonitor())
		{
			spaceproperties.put(id, property);
		}
	}
	
	/**
	 * Adds an environment action.
	 * @param action the action
	 */
	public void addSpaceAction(final ISpaceAction action)
	{
		synchronized(syncobject.getMonitor())
		{
			actions.put(action.getId(), action);
		}
	}
	
	/**
	 * Removes an environment action.
	 * @param id the action ID
	 */
	public void removeAction(final Object id)
	{
		synchronized(syncobject.getMonitor())
		{	
			actions.remove(id);
		}
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName()
	{
		synchronized(syncobject.getMonitor())
		{
			return (String)spaceproperties.get("name");
		}
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public void setName(final String name)
	{
		synchronized(syncobject.getMonitor())
		{
			spaceproperties.put("name", name);
		}
	}
	
	/**
	 * Performs an environment action.
	 * @param id ID of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public void performAction(final Object id, final Map parameters, final IResultListener listener)
	{
		syncobject.invokeLater(new Runnable()
		{
			public void run()
			{
				ISpaceAction action = (ISpaceAction)actions.get(id);
				Object ret = action.perform(new HashMap(parameters), AbstractEnvironmentSpace.this);
				listener.resultAvailable(ret);
			}
		});
		
//		ISpaceAction action = (ISpaceAction) actions.get(actionId);
//		assert action != null;
//		return action.perform(new HashMap(parameters), this);
	}
	
	/**
	 *  Get the synchronized object.
	 *  @return The sync object.
	 */
	public SynchronizedObject getSynchronizedObject()
	{
		synchronized(syncobject.getMonitor())
		{
			return syncobject;
		}
	}
	
	//-------- ISpace methods --------
	
	/**
	 *  Called when an agent was added. 
	 */
	public void agentAdded(IAgentIdentifier aid)
	{
	}
	
	/**
	 *  Called when an agent was removed.
	 */
	public void agentRemoved(IAgentIdentifier aid)
	{
	}
	
	/**
	 *  Synchronized counter class
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
