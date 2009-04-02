package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.environment.agentaction.IActionExecutor;
import jadex.adapter.base.envsupport.environment.agentaction.IAgentAction;
import jadex.adapter.base.envsupport.math.IVector2;
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
public abstract class AbstractEnvironmentSpace extends PropertyHolder 
											   implements IEnvironmentSpace
{
	//-------- constants --------
	
//	public static final String NO_OWNER = new String("no_owner"); 
	
	//-------- attributes --------
	
	/** Available space actions. */
	protected Map spaceactions;
	
	/** Available agent actions. */
	protected Map agentactions;
	
	/** The environment processes. */
	protected Map processes;

	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space object by owner, owner can null (owner view). */
	protected Map spaceobjectsbyowner;
	
	/** Object id counter for new ids. */
	protected AtomicCounter objectidcounter;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		this.spaceactions = new HashMap();
		this.agentactions = new HashMap();
		this.processes = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		this.objectidcounter = new AtomicCounter();
	}
	
	//-------- methods --------
	
	/**
	 * Adds a space process.
	 * @param id ID of the space process
	 * @param process new space process
	 */
	public void addSpaceProcess(Object id, ISpaceProcess process)
	{
		synchronized(monitor)
		{
			processes.put(id, process);
		}
	}

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id )
	{
		synchronized(monitor)
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
		synchronized(monitor)
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
	public ISpaceObject createSpaceObject(Object type, Object owner, Map properties, List tasks, List listeners)
	{
		synchronized(monitor)
		{
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			ISpaceObject obj = new SpaceObject(id, type, owner, properties, tasks, listeners, monitor);
			spaceobjects.put(id, obj);
			List typeobjects = (List)spaceobjectsbytype.get(obj.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(obj.getType(), typeobjects);
			}
			typeobjects.add(obj);
			if(owner!=null)
			{
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(obj);
			}
			return obj;
		}
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(final Object id)
	{
		ISpaceObject obj;
		synchronized(monitor)
		{
			obj = (ISpaceObject)spaceobjects.get(id);
			// shutdown and jettison tasks
			obj.clearTasks();

			// remove object
			spaceobjects.remove(id);
			List typeobjs = (List)spaceobjectsbytype.get(obj.getType());
			typeobjs.remove(obj);
			if(typeobjs.size()==0)
				spaceobjectsbytype.remove(obj.getType());
			
			if(obj.getProperty(ISpaceObject.OWNER)!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(obj.getProperty(ISpaceObject.OWNER));
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(obj.getProperty(ISpaceObject.OWNER));
			}
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
		synchronized(monitor)
		{
			return (ISpaceObject)spaceobjects.get(id);
		}
	}
	
	/**
	 * Adds a space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addSpaceAction(Object id, ISpaceAction action)
	{
		synchronized(monitor)
		{
			spaceactions.put(id, action);
		}
	}
	
	/**
	 * Removes a space action.
	 * @param id the action ID
	 */
	public void removeSpaceAction(final Object id)
	{
		synchronized(monitor)
		{	
			spaceactions.remove(id);
		}
	}
	
	/**
	 * Performs an environment action.
	 * @param id ID of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(final Object id, final Map parameters)
	{
		synchronized(monitor)
		{
			ISpaceAction action = (ISpaceAction) spaceactions.get(id);
			assert action != null;
			return action.perform(parameters, this);
		}
	}
	
	/**
	 * Adds an agent action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addAgentAction(Object id, IAgentAction action)
	{
		synchronized(monitor)
		{
			agentactions.put(id, action);
		}
	}
	
	/**
	 * Removes an agent action.
	 * @param actionId the action ID
	 */
	public void removeAgentAction(Object id)
	{
		synchronized(monitor)
		{	
			agentactions.remove(id);
		}
	}
	
	/**
	 * Schedules an agent action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void scheduleAgentAction(final Object id, final Map parameters, final IResultListener listener)
	{
		synchronized(monitor)
		{
			IActionExecutor executor = 
				(IActionExecutor) processes.get(IActionExecutor.DEFAULT_EXECUTOR_NAME);
			executor.getSynchronizer().invokeLater(new Runnable()
			{
				public void run()
				{
					IAgentAction action = (IAgentAction) agentactions.get(id);
					Object ret = action.execute(new HashMap(parameters), AbstractEnvironmentSpace.this);
					listener.resultAvailable(ret);
				}
			});
		}
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public String getName()
	{
		synchronized(monitor)
		{
			return (String)getProperty("name");
		}
	}
	
	/**
	 * Returns the space's name.
	 * @return the space's name.
	 */
	public void setName(final String name)
	{
		synchronized(monitor)
		{
			setProperty("name", name);
		}
	}
	
	/**
	 *  Get the owner of an object.
	 *  @param id The id.
	 *  @return The owner.
	 * /
	public Object getOwner(Object id)
	{
		synchronized(getSynchronizedObject().getMonitor())
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return obj.getProperty(ISpaceObject.OWNER);
		}
	}*/
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, Object owner)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			Object oldowner = obj.getProperty(ISpaceObject.OWNER);
			if(oldowner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(oldowner);
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(oldowner);
			}
			if(owner!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(owner);
				if(ownedobjs==null)
				{
					ownedobjs = new ArrayList();
					spaceobjectsbyowner.put(owner, ownedobjs);
				}
				ownedobjs.add(obj);
			}
			obj.setProperty(ISpaceObject.OWNER, owner);
		}
	}
	
	/**
	 *  Get the owned objects.
	 *  @return The owned objects. 
	 */
	public ISpaceObject[] getOwnedObjects(Object owner)
	{
		synchronized(monitor)
		{
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			return ownedobjs==null? new ISpaceObject[0]: (ISpaceObject[])ownedobjs.toArray(new ISpaceObject[ownedobjs.size()]);
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
		// Remove the owned object too?
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
