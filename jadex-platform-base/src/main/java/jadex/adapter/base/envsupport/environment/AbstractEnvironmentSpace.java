package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.envsupport.IObjectCreator;
import jadex.adapter.base.envsupport.MEnvSpaceInstance;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IContext;
import jadex.commons.collection.MultiCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Abstract base class for environment space. 
 */
public abstract class AbstractEnvironmentSpace extends SynchronizedPropertyObject implements IEnvironmentSpace
{
	//-------- attributes --------
	
	/** The space name. */
	protected String name;
	
	/** The context. */
	protected IContext context;
	
	/** The space object types. */
	protected Map objecttypes;
	
	/** The object task types. */
	protected Map tasktypes;
	
	/** The space process types. */
	protected Map processtypes;
	
	/** The percepttypes. */
	protected Map percepttypes;
	
	/** Available agent actions. */
	protected Map actions;
	
	/** The percept generators. */
	protected Map perceptgenerators;

	/** The percept processors. */
	protected MultiCollection perceptprocessors;
	
	/** Avatar mappings. */
	protected MultiCollection avatarmappings;

	/** Initial avatar settings (aid -> [type, props]). */
	protected Map initialavatars;

	/** Data view mappings. */
	protected MultiCollection	dataviewmappings;
	
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
	
	/** Task id counter for new ids. */
	protected AtomicCounter taskidcounter;
	
	/** The list of scheduled agent actions. */
	protected AgentActionList actionlist;
	
	/** The list of scheduled percepts. */
	protected PerceptList perceptlist;
		
	/** Available views */
	protected Map views;

	/** The environment listeners. */
	protected List listeners;
	
	/** The fetcher. */
	protected SimpleValueFetcher fetcher;

	//-------- constructors --------
	
	/**
	 *  Create an environment space
	 */
	public AbstractEnvironmentSpace()
	{
		super(new Object());
		this.views = new HashMap();
		this.avatarmappings = new MultiCollection();
		this.dataviewmappings = new MultiCollection();
		this.actions = new HashMap();
		this.processtypes = new HashMap();
		this.tasktypes = new HashMap();
		this.processes = new HashMap();
		this.percepttypes = new HashMap();
		this.perceptgenerators = new HashMap();
		this.perceptprocessors = new MultiCollection();
		this.objecttypes = new HashMap();
		this.spaceobjects = new HashMap();
		this.spaceobjectsbytype = new HashMap();
		this.spaceobjectsbyowner = new HashMap();
		
		this.objectidcounter = new AtomicCounter();
		this.taskidcounter = new AtomicCounter();
		this.actionlist	= new AgentActionList(this);
		this.perceptlist = new PerceptList(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Add a space type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceObjectType(String typename, List properties)
	{
		synchronized(monitor)
		{
			objecttypes.put(typename, properties);
		}
	}
	
	/**
	 *  Remove a space object type.
	 *  @param typename The type name.
	 */
	public void removeSpaceObjectType(String typename)
	{
		synchronized(monitor)
		{
			objecttypes.remove(typename);
			// Kill running process instances also?
		}
	}
	
	/**
	 *  Add a space process type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addSpaceProcessType(String typename, Class clazz, List properties)
	{
		synchronized(monitor)
		{
			processtypes.put(typename, new Object[]{clazz, properties});
		}
	}
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removeSpaceProcessType(String typename)
	{
		synchronized(monitor)
		{
			processtypes.remove(typename);
		}
	}
	
	/**
	 *  Creates a space process.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void createSpaceProcess(String typename, Map properties)
	{
		synchronized(monitor)
		{
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			Object[] procinfo = (Object[])processtypes.get(typename);
			if(procinfo==null)
				throw new RuntimeException("Unknown space process: "+typename);
			
			try
			{
				ISpaceProcess process = (ISpaceProcess)((Class)procinfo[0]).newInstance();
				properties	= mergeProperties((List)procinfo[1], properties);
				if(properties!=null)
				{
					for(Iterator it = properties.keySet().iterator(); it.hasNext(); )
					{
						String propname = (String)it.next();
						process.setProperty(propname, properties.get(propname)); 
					}
				}
				
				processes.put(id, process);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Could not create space process: "+typename, e);
			}
//			process.start(this);	// Done by executor.
		}
	}
	
	/**
	 *  Add a object task type.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void addObjectTaskType(String typename, Class clazz, List properties)
	{
		synchronized(monitor)
		{
			tasktypes.put(typename, new Object[]{clazz, properties});
		}
	}
	
	/**
	 *  Remove an object task type.
	 *  @param typename The type name.
	 */
	public void removeObjectTaskType(String typename)
	{
		synchronized(monitor)
		{
			tasktypes.remove(typename);
		}
	}
	
	/**
	 *  Creates an object task.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 *  @return The task id.
	 */
	public Object createObjectTask(String typename, Map properties, Object objectid)
	{
		synchronized(monitor)
		{
//			System.out.println("add task: "+typename+" "+objectid+" "+properties);

			// Prepare properties (runtime props override type props).
			Object[] taskinfo = (Object[])tasktypes.get(typename);
			if(taskinfo==null)
				throw new RuntimeException("Unknown space task: "+typename);
			
			try
			{
				IObjectTask task = (IObjectTask)((Class)taskinfo[0]).newInstance();
				// todo: ensure uniqueness?!
				Object id = taskidcounter.getNext();
				task.setProperty(IObjectTask.PROPERTY_ID, id);
				
				properties	= mergeProperties((List) taskinfo[1], properties);
				if(properties!=null)
				{
					for(Iterator it = properties.keySet().iterator(); it.hasNext(); )
					{
						String propname = (String)it.next();
						task.setProperty(propname, properties.get(propname)); 
					}
				}
				
				SpaceObject object = (SpaceObject)getSpaceObject(objectid);
				object.addTask(task);
				return id;
			}
			catch(Exception e)
			{
				throw new RuntimeException("Could not create space task: "+typename, e);
			}
		}
	}
	
	/**
	 *  Remove an object task.
	 *  @param typename The type name.
	 *  @param properties The properties.
	 */
	public void removeObjectTask(Object taskid, Object objectid)
	{
		synchronized(monitor)
		{
//			System.out.println("remove task: "+taskid+" "+objectid);
			SpaceObject so = (SpaceObject)getSpaceObject(objectid);
			so.removeTask(taskid);
		}
	}
	
	/**
	 *  Add a result listener to an object task.
	 *  The listener result will be the task id.
	 *  If the task is already finished, the listener will be notified.
	 */
	public void addTaskListener(Object taskid, Object objectid, IResultListener listener)
	{
		SpaceObject so = (SpaceObject)getSpaceObject(objectid);
		so.addTaskListener(taskid, listener);
	}
	
	/**
	 *  Remove a result listener from an object task.
	 */
	public void removeTaskListener(Object taskid, Object objectid, IResultListener listener)
	{
		SpaceObject so = (SpaceObject)getSpaceObject(objectid);
		so.removeTaskListener(taskid, listener);
	}
	
	/**
	 * Returns then names of the space processes.
	 * @return the names of the space processes
	 */
	public Set getSpaceProcessNames()
	{
		synchronized(monitor)
		{
			return new HashSet(processes.keySet());
		}
	}

	/**
	 * Returns a space process.
	 * @param id ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object id)
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
	public void removeSpaceProcess(Object id)
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
	public ISpaceObject createSpaceObject(String typename, Map properties, List tasks)
	{
		if(!objecttypes.containsKey(typename))
			throw new RuntimeException("Unknown space object type: "+typename);
			
		ISpaceObject ret;
		
		synchronized(monitor)
		{
			// Generate id.
			Object id;
			do
			{
				id = objectidcounter.getNext();
			}
			while(spaceobjects.containsKey(id));
			
			// Prepare properties (runtime props override type props).
			properties = mergeProperties((List)objecttypes.get(typename), properties);
			
			// Create the object.
			ret = new SpaceObject(id, typename, properties, tasks, null, monitor, this);
			spaceobjects.put(id, ret);

			// Store in type objects.
			List typeobjects = (List)spaceobjectsbytype.get(ret.getType());
			if(typeobjects == null)
			{
				typeobjects = new ArrayList();
				spaceobjectsbytype.put(ret.getType(), typeobjects);
			}
			typeobjects.add(ret);
			
			// Store in owner objects.
			if(properties!=null && properties.get(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				IAgentIdentifier	owner	= (IAgentIdentifier)properties.get(ISpaceObject.PROPERTY_OWNER);
				List ownerobjects = (List)spaceobjectsbyowner.get(owner);
				if(ownerobjects == null)
				{
					ownerobjects = new ArrayList();
					spaceobjectsbyowner.put(owner, ownerobjects);
				}
				ownerobjects.add(ret);
			}
			
			// Create view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.getCollection(typename)!=null)
			{
				for(Iterator it=dataviewmappings.getCollection(typename).iterator(); it.hasNext(); )
				{
					try
					{
						Map	sourceview	= (Map)it.next();
						Map viewargs = new HashMap();
						viewargs.put("sourceview", sourceview);
						viewargs.put("space", this);
						viewargs.put("$object", ret);
						
						IDataView	view	= (IDataView)((IObjectCreator)MEnvSpaceInstance.getProperty(sourceview, "creator")).createObject(viewargs);
						addDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name")+"_"+id, view);
					}
					catch(Exception e)
					{
						if(e instanceof RuntimeException)
							throw (RuntimeException)e;
						throw new RuntimeException(e);
					}
				}
			}
			
			// Possibly create agent.
			for(Iterator it=avatarmappings.keySet().iterator(); it.hasNext(); )
			{
				String agenttype = (String)it.next();
				AvatarMapping mapping = getAvatarMapping(agenttype, typename);
				if(mapping!=null && mapping.isCreateAgent())
				{
					final Object	fid	= id;
					// todo: what about arguments etc.?
					((ApplicationContext)getContext()).createAgent(null, agenttype, null, null, false, false, new IResultListener() {
						
						public void resultAvailable(Object result)
						{
							IAgentIdentifier	agent	= (IAgentIdentifier)result;
							
							setOwner(fid, agent);
							
							((IAMS)((ApplicationContext)getContext()).getPlatform().getService(IAMS.class))
								.startAgent(agent, null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
						}
					}, null);
				}
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
	 *  Add type properties to runtime properties.
	 *  Runtime properties have precedence if specified.
	 *  @param typeprops	The type properties (if any).
	 *  @param properties	The runtime properties or null.
	 *  @return	The merged runtime properties.
	 */
	protected Map mergeProperties(List typeprops, Map properties)
	{
		if(typeprops!=null)
		{
			if(properties==null)
				properties = new HashMap();
			for(Iterator it=typeprops.iterator(); it.hasNext(); )
			{
				Map	prop	= (Map) it.next();
				String propname = (String)prop.get("name");
				if(!properties.containsKey(propname))
				{
					IParsedExpression exp = (IParsedExpression)prop.get("value");
					boolean dyn = ((Boolean)prop.get("dynamic")).booleanValue();
					if(dyn)
						properties.put(propname, exp);
					else
						properties.put(propname, exp==null? null: exp.getValue(fetcher));
				}
			}
		}
		return properties;
	}
	
	/** 
	 * Destroys an object in this space.
	 * @param id the object's ID
	 */
	public void destroySpaceObject(final Object id)
	{
		SpaceObject obj;
		synchronized(monitor)
		{
			obj = (SpaceObject)spaceobjects.get(id);
			if(obj==null)
				throw new RuntimeException("No object found for id: "+id);
			String	objecttype	= obj.getType();
			
			// Possibly kill agent.
			IAgentIdentifier agent = (IAgentIdentifier)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
			if(agent!=null)
			{
				String	agenttype = ((ApplicationContext)getContext()).getAgentType(agent);
				AvatarMapping mapping = getAvatarMapping(agenttype, objecttype);
				if(mapping.isKillAgent())
				{
					IAMS ams = (IAMS)((ApplicationContext)getContext()).getPlatform().getService(IAMS.class);
					ams.destroyAgent(agent, null);
				}
			}
			
			// shutdown and jettison tasks
			obj.clearTasks();

			// remove object
			spaceobjects.remove(id);
			List typeobjs = (List)spaceobjectsbytype.get(objecttype);
			typeobjs.remove(obj);
			if(typeobjs.size()==0)
				spaceobjectsbytype.remove(obj.getType());
			
			if(obj.getProperty(ISpaceObject.PROPERTY_OWNER)!=null)
			{
				List ownedobjs = (List)spaceobjectsbyowner.get(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
				ownedobjs.remove(obj);
				if(ownedobjs.size()==0)
					spaceobjectsbyowner.remove(obj.getProperty(ISpaceObject.PROPERTY_OWNER));
			}

			// Remove view(s) for the object if any.
			if(dataviewmappings!=null && dataviewmappings.getCollection(objecttype)!=null)
			{
				for(Iterator it=dataviewmappings.getCollection(objecttype).iterator(); it.hasNext(); )
				{
					Map	sourceview	= (Map)it.next();
					removeDataView((String)MEnvSpaceInstance.getProperty(sourceview, "name")+"_"+id);
				}
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
	public ISpaceObject[] getSpaceObjectsByType(String type)
	{
		List obs = (List)spaceobjectsbytype.get(type);
		return obs==null? new ISpaceObject[0]: (ISpaceObject[])obs.toArray(new ISpaceObject[obs.size()]); 
	}
	
	/**
	 * Adds an avatar mapping.
	 * @param agenttype The agent type.
	 * @param objecttype The object type to represent the agent.
	 */
	public void addAvatarMappings(AvatarMapping mapping)
	{
		synchronized(monitor)
		{
			this.avatarmappings.put(mapping.getAgentType(), mapping);			
		}
	}

	/**
	 * Remove an avatar mapping.
	 * @param agenttype The agent type.
	 * @param objecttype The object type to represent the agent.
	 */
	public void removeAvatarMappings(AvatarMapping mapping)
	{
		synchronized(monitor)
		{
			this.avatarmappings.remove(mapping.getAgentType(), mapping);			
		}
	}
	
	/**
	 * Adds an space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public void addSpaceAction(String id, ISpaceAction action)
	{
		synchronized(monitor)
		{
			actions.put(id, action);
		}
	}
	
	/**
	 * Adds an space action.
	 * @param actionId the action ID
	 * @param action the action
	 */
	public ISpaceAction	getSpaceAction(String id)
	{
		ISpaceAction	ret	= (ISpaceAction)actions.get(id);
		if(ret==null)
		{
			throw new RuntimeException("No such space action: "+id);
		}
		return ret;
	}

	/**
	 * Removes an space action.
	 * @param actionId the action ID
	 */
	public void removeSpaceAction(String id)
	{
		synchronized(monitor)
		{	
			actions.remove(id);
		}
	}
	
	/**
	 * Schedules an space action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @param listener the result listener
	 */
	public void performSpaceAction(String id, Map parameters, IResultListener listener)
	{
		synchronized(monitor)
		{
			actionlist.scheduleAgentAction(getSpaceAction(id), parameters, listener);
		}
	}
	
	/**
	 * Performs a space action.
	 * @param id Id of the action
	 * @param parameters parameters for the action (may be null)
	 * @return return value of the action
	 */
	public Object performSpaceAction(String id, Map parameters)
	{
		synchronized(monitor)
		{
			ISpaceAction action = (ISpaceAction)actions.get(id);
			if(action==null)
				throw new RuntimeException("Action not found: "+id);
			return action.perform(parameters, this);
		}
	}
	
	/**
	 *  Create a percept for the given agent.
	 *  @param typename The percept type.
	 *  @param data	The content of the percept (if any).
	 *  @param agent The agent that should receive the percept.
	 */
	public void createPercept(String typename, Object data, IAgentIdentifier agent, ISpaceObject avatar)
	{
		synchronized(monitor)
		{
//			if(!percepttypes.containsKey(typename))
//				throw new RuntimeException("Unknown percept type: "+typename);
			
//			System.out.println("New percept: "+typename+", "+data+", "+agent);
			
			String	agenttype = ((ApplicationContext)getContext()).getAgentType(agent);
			List procs	= (List)perceptprocessors.get(agenttype);
			IPerceptProcessor proc = null;
			if(procs!=null)
			{
				for(int i=0; i<procs.size() && proc==null; i++)
				{
					Object[] tmp = (Object[])procs.get(i);
					if(tmp[0]==null || ((Collection)tmp[0]).contains(typename))
						proc = (IPerceptProcessor)tmp[1];
				}
			}
			
			if(proc!=null)
				perceptlist.schedulePercept(typename, data, agent, avatar, proc);
			else
				System.out.println("Warning: No processor for percept: "+typename+", "+data+", "+agent+", "+avatar);
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
	 */
	public IAgentIdentifier	getOwner(Object id)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			return (IAgentIdentifier)obj.getProperty(ISpaceObject.PROPERTY_OWNER);
		}
	}
	
	/**
	 *  Set the owner of an object.
	 *  @param id The object id.
	 *  @param pos The object owner.
	 */
	public void setOwner(Object id, IAgentIdentifier owner)
	{
		synchronized(monitor)
		{
			ISpaceObject obj = getSpaceObject(id); 
			if(obj==null)
				throw new RuntimeException("Space object not found: "+id);
			Object oldowner = obj.getProperty(ISpaceObject.PROPERTY_OWNER);
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
			obj.setProperty(ISpaceObject.PROPERTY_OWNER, owner);
		}
	}
	
	/**
	 *  Get the avatar objects.
	 *  @return The avatar objects. 
	 */
	public ISpaceObject[] getAvatars(IAgentIdentifier owner)
	{
		synchronized(monitor)
		{
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			return ownedobjs==null? new ISpaceObject[0]: (ISpaceObject[])ownedobjs.toArray(new ISpaceObject[ownedobjs.size()]);
		}
	}
	
	/**
	 *  Get the avatar objects.
	 *  @return The avatar objects. 
	 */
	public IAgentIdentifier[] getAgents()
	{
		synchronized(monitor)
		{
			return (IAgentIdentifier[])spaceobjectsbyowner.keySet().toArray(new IAgentIdentifier[spaceobjectsbyowner.keySet().size()]);
		}
	}
	
	/**
	 *  Get the avatar object.
	 *  @return The avatar object. 
	 */
	public ISpaceObject getAvatar(IAgentIdentifier owner)
	{
		synchronized(monitor)
		{
			ISpaceObject ret = null;
			List ownedobjs = (List)spaceobjectsbyowner.get(owner);
			if(ownedobjs!=null)
			{
				if(ownedobjs.size()>1)
					throw new RuntimeException("More than one avatar for agent: "+owner);
				else if(ownedobjs.size()==1)
					ret = (ISpaceObject)ownedobjs.get(0);
			}
			return ret;
		}
	}
	
	/**
	 * Adds a view to the space.
	 * @param name name of the view
	 * @param view the view
	 */
	public void addDataView(String name, IDataView view)
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
	public void removeDataView(String name)
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
	public IDataView getDataView(String name)
	{
		synchronized (monitor)
		{
			return (IDataView) views.get(name);
		}
	}
	
	/**
	 * Get all available dataviews in this space.
	 * @return all available dataviews
	 */
	public Map getDataViews()
	{
		synchronized (monitor)
		{
			return new HashMap(views);
		}
	}
	
	/**
	 *  Add a mapping from object type to data view
	 *  @param objecttype	The object type.
	 *  @param view	Settings for view creation.
	 */
	public void addDataViewMapping(String objecttype, Map view)
	{
		synchronized(monitor)
		{
			dataviewmappings.put(objecttype, view);
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

	/**
	 *  Add a percept processor.
	 *  @param	agenttype	The agent type.
	 *  @param	proc	The percept processor.
	 */
	public void addPerceptProcessor(String agenttype, Set percepttypes, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			perceptprocessors.put(agenttype, new Object[]{percepttypes, proc});
		}
	}
	
	/**
	 *  remove a percept processor.
	 *  @param	agenttype	The agent type.
	 *  @param	proc	The percept processor.
	 */
	public void removePerceptProcessor(String agenttype, IPerceptProcessor proc)
	{
		synchronized(monitor)
		{
			List procs = (List)perceptprocessors.get(agenttype);
			for(int i=0; i<procs.size(); i++)
			{
				Object[] tmp = (Object[])procs.get(i);
				if(proc.equals(tmp[1]))
				{
					perceptprocessors.remove(agenttype, tmp);
					break;
				}
			}
		}
	}
	
	/**
	 *  Add a space percept type.
	 *  @param typename The percept name.
	 *  @param objecttypes The objecttypes.
	 *  @param agenttypes The agenttypes.
	 */
	public void addPerceptType(PerceptType percepttype)
	{
		synchronized(monitor)
		{
			percepttypes.put(percepttype.getName(), percepttype);
		}
	}
	
	/**
	 *  Remove a space process type.
	 *  @param typename The type name.
	 */
	public void removePerceptType(String typename)
	{
		synchronized(monitor)
		{
			percepttypes.remove(typename);
		}
	}
	
	/**
	 *  Get a space percept type.
	 *  @param percepttype The name of the percept type.
	 *  @return The percept type. 
	 */
	public PerceptType getPerceptType(String percepttype)
	{
		synchronized(monitor)
		{
			return (PerceptType)percepttypes.get(percepttype);
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
			// Possibly add or create avatar(s) if any.
			if(initialavatars!=null && initialavatars.containsKey(aid))
			{
				Object[]	ia	= (Object[])initialavatars.get(aid);
				String	objecttype	=	(String)ia[0];
				Map	props	=	(Map)ia[1];
				if(props==null)
					props	= new HashMap();
				props.put(ISpaceObject.PROPERTY_OWNER, aid);
				createSpaceObject(objecttype, props, null);
			}
			else
			{
				String	agenttype	= ((ApplicationContext)getContext()).getAgentType(aid);
				if(agenttype!=null && avatarmappings.getCollection(agenttype)!=null)
				{
					for(Iterator it=avatarmappings.getCollection(agenttype).iterator(); it.hasNext(); )
					{
						AvatarMapping mapping = (AvatarMapping)it.next();
						if(mapping.isCreateAvatar())
						{
							Map	props	= new HashMap();
							props.put(ISpaceObject.PROPERTY_OWNER, aid);
							createSpaceObject(mapping.getObjectType(), props, null);
						}
					}
				}
			}
			
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
			String	agenttype	= ((ApplicationContext)getContext()).getAgentType(aid);
			
			// Possibly kill avatars of that agent.
			if(agenttype!=null && avatarmappings.getCollection(agenttype)!=null)
			{
				ISpaceObject[] avatars = getAvatars(aid);
				if(avatars!=null)
				{
					for(int i=0; i<avatars.length; i++)
					{
						String avatartype = avatars[i].getType();
						AvatarMapping mapping = getAvatarMapping(agenttype, avatartype);
						
						if(mapping!=null && mapping.isKillAvatar())
						{
							destroySpaceObject(avatars[i].getId());
						}
					}
				}
			}
			
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
	 * Returns a property.
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
				ret = ((IParsedExpression) ret).getValue(getFetcher());
			}
			
			return ret;
		}
	}
	
	/**
	 *  Get the value fetcher.
	 *  @return The fetcher.
	 */
	public SimpleValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			this.fetcher = new SimpleValueFetcher();
			fetcher.setValue("$space", this);
		}
		return this.fetcher;
	}

	/**
	 *  Set the fetcher.
	 *  @param fetcher The fetcher to set.
	 */
	public void setFetcher(SimpleValueFetcher fetcher)
	{
		this.fetcher = fetcher;
	}

	/**
	 *  Get the space objects.
	 */
	// Hack!!! getSpaceObjecs() implemented in Space2D???
	protected Collection	getSpaceObjectsCollection()
	{
		return spaceobjects.values();
	}
	
	/**
	 *  Get the processes.
	 */
	protected Collection	getProcesses()
	{
		return processes.values();
	}
	
	/**
	 *  Get the list of scheduled agent actions
	 */
	public AgentActionList	getAgentActionList()
	{
		return actionlist;
	}
	
	/**
	 *  Get the list of scheduled percepts.
	 */
	protected PerceptList	getPerceptList()
	{
		return perceptlist;
	}
	
	/**
	 *  Get the views.
	 */
	protected Collection	getViews()
	{
		return views.values();
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

	/**
	 *  Initial settings for the avatar of a specific agent.
	 *  @param ownerid	The agent id.
	 *  @param type	The object type.
	 *  @param props	The properties for the object (if any).
	 */
	public void addInitialAvatar(IAgentIdentifier ownerid, String type,	Map props)
	{
		synchronized(monitor)
		{
			if(initialavatars==null)
				initialavatars	= new HashMap();

			initialavatars.put(ownerid, new Object[]{type, props});
		}
	}
	
	/**
	 *  Get the avatar mapping for an agent avatar combination.
	 */
	protected AvatarMapping getAvatarMapping(String agenttype, String avatartype)
	{
		AvatarMapping mapping = null;
		for(Iterator it=avatarmappings.getCollection(agenttype).iterator(); mapping==null && it.hasNext(); )
		{
			AvatarMapping	test = (AvatarMapping)it.next();
			if(avatartype.equals(test.getObjectType()))
				mapping	= test;
		}
		return mapping;
	}
}
