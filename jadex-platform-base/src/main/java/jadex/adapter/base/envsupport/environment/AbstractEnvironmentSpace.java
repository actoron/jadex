package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.contextservice.IContext;
import jadex.adapter.base.envsupport.environment.view.IView;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  
 */
public abstract class AbstractEnvironmentSpace extends PropertyHolder 
											   implements IEnvironmentSpace
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The context. */
	protected IContext context;
	
	/** Available space actions. */
	protected Map spaceactions;
	
	/** Available views */
	protected Map views;
	
	/** Available agent actions. */
	protected Map agentactions;
	
	/** The environment processes. */
	protected Map processes;
	
	/** The percept generators. */
	protected Map perceptgenerators;

	/** Long/ObjectIDs (keys) and environment objects (values). */
	protected Map spaceobjects;
	
	/** Types of EnvironmentObjects and lists of EnvironmentObjects of that type (typed view). */
	protected Map spaceobjectsbytype;
	
	/** Space object by owner, owner can null (owner view). */
	protected Map spaceobjectsbyowner;
	
	/** Object id counter for new ids. */
	protected AtomicCounter objectidcounter;
	
	/** The environment listeners. */
	protected List listeners;
	
	/** The action executor. */
	protected ActionProcessor actionexecutor;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 *  @param spaceexecutor executor for the space
	 *  @param actionexecutor executor for agent actions
	 */
	public AbstractEnvironmentSpace()
	{
		this.monitor = new Object();
		this.views = new HashMap();
		this.spaceactions = new HashMap();
		this.agentactions = new HashMap();
		this.processes = new HashMap();
		this.perceptgenerators = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		this.objectidcounter = new AtomicCounter();
		this.actionexecutor = new ActionProcessor(monitor);
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
			process.start(this);
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
			ISpaceProcess process = (ISpaceProcess)processes.remove(id);
			if(process!=null)
				process.shutdown(this);
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
		ISpaceObject ret;
		
		synchronized(monitor)
		{
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			ret = new SpaceObject(id, type, owner, properties, tasks, listeners, monitor);
			spaceobjects.put(id, ret);
			List typeobjects = (List)spaceobjectsbytype.get(ret.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(ret.getType(), typeobjects);
			}
			typeobjects.add(ret);
			
			if(owner!=null)
			{
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
		}
		
		if(listeners!=null)
		{
			EnvironmentEvent event = new EnvironmentEvent(EnvironmentEvent.OBJECT_CREATED, this, ret, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(event);
			}
		}
		
		return ret;
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
		// hmm? what about calling destroy on object? could it do sth. else than throwing event?
		ObjectEvent event = new ObjectEvent(ObjectEvent.OBJECT_REMOVED);
		event.setParameter("space_name", getName());
		obj.fireObjectEvent(event);
		
		if(listeners!=null)
		{
			EnvironmentEvent ev = new EnvironmentEvent(EnvironmentEvent.OBJECT_DESTROYED, this, obj, null);
			for(int i=0; i<listeners.size(); i++)
			{
				IEnvironmentListener lis = (IEnvironmentListener)listeners.get(i);
				lis.dispatchEnvironmentEvent(ev);
			}
		}
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
			ISpaceObject ret = (ISpaceObject)spaceobjects.get(id);
			if(ret==null)
				throw new RuntimeException("Space object not found: "+id);
			return ret;
		}
	}
	
	/**
	 * Returns an object in this space.
	 * @param id the object's ID
	 * @return the object in this space
	 */
	public ISpaceObject getSpaceObject0(Object id)
	{
		synchronized(monitor)
		{
			return (ISpaceObject)spaceobjects.get(id);
		}
	}
	
	/**
	 * Get all space object of a specific type.
	 * @param type The space object type.
	 * @return The space objects of the desired type.
	 */
	public ISpaceObject[] getSpaceObjectsByType(Object type)
	{
		List obs = (List)spaceobjectsbytype.get(type);
		return obs==null? new ISpaceObject[0]: (ISpaceObject[])obs.toArray(new ISpaceObject[obs.size()]); 
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
	public void performAgentAction(final Object id, final Map parameters, final IResultListener listener)
	{
		actionexecutor.invokeLater(new Runnable()
		{
			public void run()
			{
				IAgentAction action = (IAgentAction)agentactions.get(id);
				Object ret = action.perform(parameters==null? Collections.EMPTY_MAP: parameters, AbstractEnvironmentSpace.this);
				listener.resultAvailable(ret);
			}
		}); // todo: what about metainfo
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
	
	/**
	 * Adds a view to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addView(String name, IView view)
	{
		synchronized (monitor)
		{
			views.put(name, view);
		}
	}
	
	/**
	 * Removes a view from the space.
	 * @param name name of the view
	 */
	public void removeView(String name)
	{
		synchronized (monitor)
		{
			views.remove(name);
		}
	}
	
	/**
	 * Gets a specific view.
	 * @param name name of the view
	 * @return the view
	 */
	public IView getView(String name)
	{
		synchronized (monitor)
		{
			return (IView) views.get(name);
		}
	}
	
	/**
	 * Get all available views in this space.
	 * @return list of view names
	 */
	public List getViewNames()
	{
		synchronized (monitor)
		{
			return new ArrayList(views.keySet());
		}
	}
	
	/**
	 *  Add an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void addEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			if(listeners==null)
				listeners = new ArrayList();
			listeners.add(listener);
		}
	}
	
	/**
	 *  Remove an environment listener.
	 *  @param listener The environment listener. 
	 */
	public void removeEnvironmentListener(IEnvironmentListener listener)
	{
		synchronized(monitor)
		{
			listeners.remove(listener);
			if(listeners.size()==0)
				listeners = null;
		}
	}
	
	/**
	 * Adds a percept generator.
	 * @param id The percept generator id.
	 * @param gen The percept generator.
	 */
	public void addPerceptGenerator(Object id, IPerceptGenerator gen)
	{
		synchronized(monitor)
		{
			addEnvironmentListener(gen);
			perceptgenerators.put(id, gen);
		}
	}
	
	/**
	 * Remove a percept generator.
	 * @param id The percept generator id.
	 */
	public void removePerceptGenerator(Object id)
	{
		synchronized(monitor)
		{
			removeEnvironmentListener((IEnvironmentListener)perceptgenerators.remove(id));
		}
	}

	
	//-------- ISpace methods --------
	
	/**
	 *  Called when an agent was added. 
	 */
	public void agentAdded(IAgentIdentifier aid)
	{
		synchronized(monitor)
		{
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.agentAdded(aid, this);
				}
			}
		}
	}
	
	/**
	 *  Called when an agent was removed.
	 */
	public void agentRemoved(IAgentIdentifier aid)
	{
		synchronized(monitor)
		{
			if(perceptgenerators!=null)
			{
				for(Iterator it=perceptgenerators.keySet().iterator(); it.hasNext(); )
				{
					IPerceptGenerator gen = (IPerceptGenerator)perceptgenerators.get(it.next());
					gen.agentRemoved(aid, this);
				}
			}
		}
		
		// Remove the owned object too?
	}
	
	
	/**
	 *  Get the context.
	 *  @return The context.
	 */
	public IContext getContext()
	{
		return context;
	}
	
	/**
	 *  Set the context.
	 *  @param context The context.
	 */
	public void setContext(IContext context)
	{
		this.context = context;
	}
	
	/** 
	 * Steps the space. May be non-functional in spaces that do not have
	 * a concept of steps.
	 * @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 */
	public void step(IVector1 progress)
	{
		synchronized(monitor)
		{
			// Update the environment objects.
			for(Iterator it = spaceobjects.values().iterator(); it.hasNext(); )
			{
				SpaceObject obj = (SpaceObject)it.next();
				obj.updateObject(progress);
			}
			
			// Execute the scheduled agent actions.
			actionexecutor.executeEntries(null); // todo: where to get filter
			
			// Execute the processes.
			Object[] procs = processes.values().toArray();
			for(int i = 0; i < procs.length; ++i)
			{
				ISpaceProcess process = (ISpaceProcess) procs[i];
				process.execute(progress, this);
			}
			
			// Update the views.
			for (Iterator it = views.values().iterator(); it.hasNext(); )
			{
				IView view = (IView) it.next();
				view.update(this);
			}
		}
	}
	
	/**
	 *  Fire an environment event.
	 *  @param event The event.
	 */
	protected void fireEnvironmentEvent(EnvironmentEvent event)
	{
		synchronized(monitor)
		{
			if(listeners!=null)
			{
				for(int i=0; i<listeners.size(); i++)
				{
					((IEnvironmentListener)listeners.get(i)).dispatchEnvironmentEvent(event);
				}
			}
		}
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
