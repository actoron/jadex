package jadex.application.runtime.impl;

import jadex.application.model.ApplicationModel;
import jadex.application.model.MAgentInstance;
import jadex.application.model.MAgentType;
import jadex.application.model.MApplicationInstance;
import jadex.application.model.MApplicationType;
import jadex.application.model.MSpaceInstance;
import jadex.application.runtime.IApplication;
import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.runtime.ISpace;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  The application context provides a closed environment for agents.
 *  If agents spawn other agents, these will automatically be added to
 *  the context.
 *  When the context is deleted all agents will be destroyed.
 *  An agent must only be in one application context.
 */
public class Application	implements IApplication, IComponentInstance
{
	//-------- attributes --------
	
	/** The application configuration. */
	protected MApplicationInstance	config;
	
	/** The contained spaces. */
	protected Map spaces;
	
	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The application type. */
	protected ApplicationModel model;
	
	/** The parent component. */
	protected IExternalAccess parent;
	
	/** Flag to indicate that the application is already inited. */
	protected boolean	inited;
	
	/** Flag to indicate that the context is about to be deleted
	 * (no more agents can be added). */
	protected boolean	terminating;
	
	/** Component type mapping (cid -> logical type name). */
	protected Map	ctypes;
	
	/** The arguments. */
	protected Map arguments;
	
	/** The arguments. */
	protected Map results;

	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public Application(ApplicationModel model, MApplicationInstance config, IComponentAdapter adapter, IExternalAccess parent, Map arguments)
	{
		this.config	= config;
		this.adapter = adapter;
		this.model = model;
		this.parent = parent;
		this.arguments = arguments==null ? new HashMap() : arguments;
		this.results = new HashMap();
		
		// Init the arguments with default values.
		String configname = config!=null? config.getName(): null;
		IArgument[] args = getModel().getArguments();
		for(int i=0; i<args.length; i++)
		{
			if(args[i].getDefaultValue(configname)!=null)
			{
				if(this.arguments.get(args[i].getName())==null)
				{
					this.arguments.put(args[i].getName(), args[i].getDefaultValue(configname));
				}
			}
		}
		
		// Init the results with default values.
		IArgument[] res = model.getResults();
		for(int i=0; i<res.length; i++)
		{
			if(res[i].getDefaultValue(configname)!=null)
			{
				this.results.put(res[i].getName(), res[i].getDefaultValue(configname));
			}
		}
	}

	//-------- space handling --------
		
	/**
	 *  Add a space to the context.
	 *  @param space The space.
	 */
	public synchronized void addSpace(String name, ISpace space)
	{
		if(spaces==null)
			spaces = new HashMap();
		
		spaces.put(name, space);
		
		// Todo: Add spaces dynamically (i.e. add existing components to space).
	}
	
	/**
	 *  Add a space to the context.
	 *  @param name The space name.
	 */
	public synchronized void removeSpace(String name)
	{
		if(spaces!=null)
		{
			spaces.remove(name);
			if(spaces.isEmpty())
			{
				spaces = null;
			}
		}

//		System.out.println("Removed space: "+name);
	}
	
	/**
	 *  Get a space by name.
	 *  @param name The name.
	 *  @return The space.
	 */
	public synchronized ISpace getSpace(String name)
	{
		return spaces==null? null: (ISpace)spaces.get(name);
	}
	
	//-------- template methods --------

	/**
	 *  Delete a context. Called from context service before a context is
	 *  removed from the platform. Default context behavior is to do nothing.
	 *  @param context	The context to be deleted.
	 *  @param listener	The listener to be notified when deletion is finished (if any).
	 */
	public void	deleteContext(final IResultListener listener)
	{
		this.setTerminating(true);
//		final IComponentIdentifier[]	agents	= getAgents();
//		if(agents!=null && agents.length>0)
//		{
//			// Create AMS result listener (l2), when listener is used.
//			// -> notifies listener, when last agent is killed.
//			IResultListener	l2	= listener!=null ? new IResultListener()
//			{
//				int tokill	= agents.length;
//				Exception	exception;
//				
//				public void resultAvailable(Object result)
//				{
//					result();
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					if(this.exception==null)	// Only return first exception.
//						this.exception	= exception;
//					result();
//				}
//				
//				/**
//				 *  Called for each killed agent.
//				 *  Decrease counter and notify listener, when last agent is killed.
//				 */
//				protected void	result()
//				{
//					tokill--;
//					if(tokill==0)
//					{
//						for(Iterator it=spaces.values().iterator(); it.hasNext(); )
//						{
//							ISpace space = (ISpace)it.next();
//							space.terminate();
//						}
//						
//						if(exception!=null)
//							listener.exceptionOccurred(exception);
//						else
//							listener.resultAvailable(Application.this);
//					}
//				}
//			} : null;
//			
//			// Kill all agents in the context. 
////			IAMS ams = (IAMS) platform.getService(IAMS.class);
//			for(int i=0; i<agents.length; i++)
//			{
//				IComponentExecutionService ces = (IComponentExecutionService)container.getService(IComponentExecutionService.class);
//				ces.destroyComponent(agents[i], l2);
////				ams.destroyAgent(agents[i], l2);
//			}
//		}
//		else
		{
			for(Iterator it=spaces.values().iterator(); it.hasNext(); )
			{
				ISpace space = (ISpace)it.next();
				space.terminate();
			}
			
			if(listener!=null)
				listener.resultAvailable(this, this);
		}
	}

	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public void	componentCreated(IComponentDescription desc, ILoadableComponentModel model)
	{
		IComponentIdentifier comp = desc.getName();
		List	atypes	= this.model.getApplicationType().getMAgentTypes();
		boolean	found	= false;
		String	type	= null;
		for(int i=0; !found	&& i<atypes.size(); i++)
		{
			MAgentType	atype	= (MAgentType)atypes.get(i);
			
			// Hack!!! simplify lookup!?
			IComponentFactory factory = null;
			Collection facts = adapter.getServiceContainer().getServices(IComponentFactory.class);
			if(facts!=null)
			{
				for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
				{
					IComponentFactory	cf	= (IComponentFactory)it.next();
					if(cf.isLoadable(atype.getFilename()))
					{
						factory	= cf;
					}
				}
			}
			ILoadableComponentModel amodel = factory.loadModel(atype.getFilename());
			
			if(amodel.getPackage().equals(model.getPackage()) && amodel.getName().equals(model.getName()))
//			if(amodel.getFilename().equals(model.getFilename()))
			{
				synchronized(this)
				{
					if(ctypes==null)
						ctypes	= new HashMap();
					ctypes.put(comp, atype.getName());
				}
				type	= atype.getName();
				found	= true;
			}
		}
		if(!found)
		{
			throw new RuntimeException("Unsupported component for application: "+model.getFilename());
		}
		
		ISpace[]	aspaces	= null;
		synchronized(this)
		{
			if(spaces!=null)
			{
				aspaces	= (ISpace[])spaces.values().toArray(new ISpace[spaces.size()]);
			}
		}

		if(aspaces!=null)
		{
			for(int i=0; i<aspaces.length; i++)
			{
				aspaces[i].componentAdded(comp, type);
			}
		}
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public void	componentDestroyed(IComponentDescription desc)
	{
		IComponentIdentifier comp = desc.getName();
		ISpace[]	aspaces	= null;
		synchronized(this)
		{
			if(spaces!=null)
			{
				aspaces	= (ISpace[])spaces.values().toArray(new ISpace[spaces.size()]);
			}
		}

		if(aspaces!=null)
		{
			for(int i=0; i<aspaces.length; i++)
			{
				aspaces[i].componentRemoved(comp);
			}
		}
		
		synchronized(this)
		{
			if(ctypes!=null)
			{
				ctypes.remove(comp);
			}
		}
		
		if(desc.isMaster())
			killApplication();
	}
	
	/**
	 *  Kill the application.
	 */
	public void killApplication()
	{
		((IComponentExecutionService)getComponentAdapter().getServiceContainer()
			.getService(IComponentExecutionService.class))
			.destroyComponent(getComponentAdapter().getComponentIdentifier(), null);
	}
	
	
	/**
	 *  Add an agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @param prop The property.
	 * /
	public synchronized void addProperty(IComponentIdentifier agent, String key, Object prop)
	{
		if(!containsAgent(agent))
			throw new RuntimeException("Agent not contained in context: "+agent+" "+this);
			
		Map agentprops = (Map)properties.get(agent);
		if(agentprops==null)
		{
			agentprops = new HashMap();
			properties.put(agent, agentprops);
		}
		
		agentprops.put(key, prop);
	}
	
	/**
	 *  Get agent property. 
	 *  @param agent The agent.
	 *  @param key The key.
	 *  @return The property. 
	 * /
	public synchronized Object getProperty(IComponentIdentifier agent, String key)
	{
		Object ret = null;
		
		if(!containsAgent(agent))
			throw new RuntimeException("Agent not contained in context: "+agent+" "+this);
			
		Map agentprops = (Map)properties.get(agent);
		if(agentprops!=null)
			ret = agentprops.get(key);
		
		return ret;
	}*/
	
	//-------- methods --------

	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter getComponentAdapter() 
	{
		return adapter;
	}

	/**
	 *  Get the name.
	 */
	// todo: remove.
	public String	getName()
	{
		return adapter.getComponentIdentifier().getLocalName();		
	}
	
	/**
	 *  Get a string representation of the context.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(SReflect.getInnerClassName(getClass()));
		ret.append("(name=");
		ret.append(adapter.getComponentIdentifier().getLocalName());
//		ret.append(", parent=");
//		ret.append(getParentContext());
//		IComponentIdentifier[]	aids	= getAgents(); 
//		if(aids!=null)
//		{
//			ret.append(", agents=");
//			ret.append(SUtil.arrayToString(aids));
//		}
//		IContext[]	subcs	= getSubContexts(); 
//		if(subcs!=null)
//		{
//			ret.append(", subcontexts=");
//			ret.append(SUtil.arrayToString(subcs));
//		}
		ret.append(")");
		return ret.toString();
	}
		
	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public ApplicationModel getModel()
	{
		return this.model;
	}
	
	/**
	 *  Get the application type.
	 */
	public MApplicationType	getApplicationType()
	{
		return model.getApplicationType();
	}
	
	/**
	 *  Get the service container.
	 */
	public IServiceContainer	getServiceContainer()
	{
		return adapter.getServiceContainer();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
	/**
	 *  Create an agent in the context.
	 *  @param name	The name of the newly created agent.
	 *  @param type	The agent type as defined in the application type.
	 *  @param configuration	The agent configuration.
	 *  @param arguments	Arguments for the new agent.
	 *  @param start	Should the new agent be started?
	 *  
	 *  @param istener	A listener to be notified, when the agent is created (if any).
	 *  @param creator	The agent that wants to create a new agent (if any).	
	 * /
	public void createAgent(String name, final String type, String configuration,
			Map arguments, final boolean start, final boolean master, 
			final IResultListener listener)
	{
		MAgentType	at	= model.getApplicationType().getMAgentType(type);
		if(at==null)
			throw new RuntimeException("Unknown agent type '"+type+"' in application: "+model);
//		final IAMS	ams	= (IAMS) platform.getService(IAMS.class);
		IComponentExecutionService ces = (IComponentExecutionService)container.getService(IComponentExecutionService.class);

		
		ces.createComponent(name, at.getFilename(), configuration, arguments, true, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				if(listener!=null)
					listener.exceptionOccurred(exception);
			}
			public void resultAvailable(Object result)
			{
				IComponentIdentifier aid = (IComponentIdentifier)result;
				synchronized(Application.this)
				{
					if(agenttypes==null)
						agenttypes	= new HashMap();
					agenttypes.put(aid, type);
				}
				
				if(!containsAgent(aid))
					addAgent(aid);	// Hack??? agentCreated() may be called from AMS.
				
				if(master)
				{
					addProperty(aid, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
				}
				
				if(start)
				{
					IComponentExecutionService ces = (IComponentExecutionService)container.getService(IComponentExecutionService.class);
					ces.resumeComponent(aid, listener);
				}
				else
				{
					if(listener!=null)
						listener.resultAvailable(result);
				}
			}
		}, creator);
	}*/
	
	/**
	 *  Remove an agent from a context.
	 * /
	// Cannot be synchronized due to deadlock with space (uses context.getAgentType()).
	public void	removeAgent(IComponentIdentifier agent)
	{
		boolean master = isAgentMaster(agent);
			
		super.removeAgent(agent);
		
		if(master)
			((IContextService)container.getService(IContextService.class)).deleteContext(this, null);
	}*/

	/**
	 *  Get the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	public boolean	isTerminating()
	{
		return this.terminating;
	}

	/**
	 *  Set the flag indicating if the context is about to be deleted
	 *  (no more agents can be added).
	 */
	protected void setTerminating(boolean terminating)
	{
		if(!terminating || this.terminating)
			throw new RuntimeException("Cannot terminate; illegal state: "+this.terminating+", "+terminating);
			
		this.terminating	= terminating;
	}

	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @param master The master.
	 * /
	public void setAgentMaster(IComponentIdentifier agent, boolean master)
	{
		addProperty(agent, PROPERTY_AGENT_MASTER, master? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Set an agent as master (causes context to be terminated on its deletion).
	 *  @param agent The agent.
	 *  @return True, if agent is master.
	 * /
	public boolean isAgentMaster(IComponentIdentifier agent)
	{
		Boolean ret = (Boolean)getProperty(agent, PROPERTY_AGENT_MASTER);
		return ret==null? false: ret.booleanValue();
	}*/
	
	/**
	 *  Get the agent type for an agent id.
	 *  @param aid	The agent id.
	 *  @return The agent type name.
	 * /
	public synchronized String	getAgentType(IComponentIdentifier aid)
	{
		return agenttypes!=null ? (String)agenttypes.get(aid) : null;
	}*/
	
	/**
	 *  Get the agent types.
	 *  @return The agent types.
	 * /
	public String[] getAgentTypes()
	{
		List atypes = model.getApplicationType().getMAgentTypes();
		String[] ret = atypes!=null? new String[atypes.size()]: SUtil.EMPTY_STRING;
		
		for(int i=0; i<ret.length; i++)
		{
			MAgentType at = (MAgentType)atypes.get(i);
			ret[i] = at.getName();
		}
		
		return ret;
	}
	
	/**
	 *  Get the agent type for an agent filename.
	 *  @param aid	The agent filename.
	 *  @return The agent type name.
	 * /
	public String getAgentType(String filename)
	{
		String ret = null;
		filename = filename.replace('\\', '/');
		
		List agenttypes = model.getApplicationType().getMAgentTypes();
		for(Iterator it=agenttypes.iterator(); it.hasNext(); )
		{
			MAgentType agenttype = (MAgentType)it.next();
			if(filename.endsWith(agenttype.getFilename()))
				ret = agenttype.getName();
		}
		
		return ret;
	}*/
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		return model.getApplicationType().getAllImports();
	}
	
	//-------- methods to be called by adapter --------

	/**
	 *  Can be called on the agent thread only.
	 * 
	 *  Main method to perform agent execution.
	 *  Whenever this method is called, the agent performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for agents
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public boolean executeStep()
	{
		if(!inited)
		{
			// Create spaces for context.
			List spaces = config.getMSpaceInstances();
			if(spaces!=null)
			{
				for(int i=0; i<spaces.size(); i++)
				{
					MSpaceInstance si = (MSpaceInstance)spaces.get(i);
					try
					{
						ISpace space = (ISpace)si.getClazz().newInstance();
						this.addSpace(si.getName(), space);
						space.initSpace(this, si);
					}
					catch(Exception e)
					{
						System.out.println("Exception while creating space: "+si.getName());
					}
				}
			}
			
			List agents = config.getMAgentInstances();
			ClassLoader cl = ((ILibraryService)adapter.getServiceContainer().getService(ILibraryService.class)).getClassLoader();
			for(int i=0; i<agents.size(); i++)
			{
				final MAgentInstance agent = (MAgentInstance)agents.get(i);
				
	//			System.out.println("Create: "+agent.getName()+" "+agent.getTypeName()+" "+agent.getConfiguration());
				int num = agent.getNumber(this, cl);
				for(int j=0; j<num; j++)
				{
					IComponentExecutionService	ces	= (IComponentExecutionService)adapter.getServiceContainer().getService(IComponentExecutionService.class);
					ces.createComponent(agent.getName(), agent.getType(model.getApplicationType()).getFilename(), agent.getConfiguration(), 
						agent.getArguments(this, cl), agent.isSuspended(), null, adapter.getComponentIdentifier(), null, agent.isMaster());					
	//				context.createAgent(agent.getName(), agent.getTypeName(),
	//					agent.getConfiguration(), agent.getArguments(container, apptype, cl), agent.isStart(), agent.isMaster(),
	//					DefaultResultListener.getInstance(), null);	
				}
			}
			
			inited	= true;
		}
		// todo: is this necessary? can we ensure that this is not called?
//		else
//		{
//			throw new UnsupportedOperationException();
//		}
		return false;
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *   
	 *  Request agent to kill itself.
	 *  The agent might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param listener	When cleanup of the agent is finished, the listener must be notified.
	 */
	public void killComponent(IResultListener listener)
	{
		// Todo: application cleanup?
		if(listener!=null)
			listener.resultAvailable(this, listener);
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this agent.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public void getExternalAccess(IResultListener listener)
	{
		listener.resultAvailable(this, new IApplicationExternalAccess()
		{
			public ILoadableComponentModel getModel()
			{
				return model;
			}
			
			public IComponentIdentifier getComponentIdentifier()
			{
				return adapter.getComponentIdentifier();
			}
			
			public Object getSpace(String name)
			{
				return Application.this.getSpace(name);
			}
		});
	}

	/**
	 *  Get the class loader of the agent.
	 *  The agent class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while agents are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The agent class loader. 
	 */
	public ClassLoader getClassLoader()
	{
		return model.getClassLoader();
	}

	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		// Todo: application breakpoints!?
		return false;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{	
		results.put(name, value);
	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map getResults()
	{
		return results;
	}

	/**
	 *  Get the logical component type for a given component id.
	 */
	public String getComponentType(IComponentIdentifier cid)
	{
		return (String)ctypes.get(cid);
	}

	/**
	 *  Get the file name for a logical type name of a subcomponent of this application.
	 */
	public String	getComponentFilename(String type)
	{
		return model.getApplicationType().getMAgentType(type).getFilename();
	}
}
