package jadex.adapter.standalone.service.componentexecution;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.standalone.StandaloneComponentAdapter;
import jadex.adapter.standalone.fipaimpl.AMSAgentDescription;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.adapter.standalone.fipaimpl.SearchConstraints;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.execution.IExecutionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Standalone implementation of component execution service.
 */
public class ComponentExecutionService implements IComponentExecutionService
{
	//-------- constants --------

	/** The component counter. Used for generating unique component ids. */
	public static int compcnt = 0;

	//-------- attributes --------

	/** The service container. */
	protected IServiceContainer	container;

	/** The components (id->component adapter). */
	protected Map adapters;
	
	/** The component descriptions (id -> component description). */
	protected Map descs;
	
	/** The logger. */
	protected Logger logger;

	/** The listeners. */
	protected List listeners;
	
    //-------- constructors --------

    /**
     *  Create a new component execution service.#
     *  @param container	The service container.
     */
    public ComponentExecutionService(IServiceContainer container)
	{
		this.container = container;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.descs = Collections.synchronizedMap(SCollection.createHashMap());
		this.logger = Logger.getLogger(container.getName()+".cms");
		this.listeners = SCollection.createArrayList();
    }
    
    //-------- IComponentExecutionService interface --------

    /**
	 *  Create a new component on the platform.
	 *  The component will not run before the {@link startComponent()}
	 *  method is called.
	 *  @param name The component name (null for auto creation).
	 *  @param component The component instance.
	 *  @param listener The result listener (if any). Will receive the id of the component as result.
	 *  @param creator The creator (if any).
	 */
	public void	createComponent(String name, String model, String config, Map args, IResultListener listener, Object creator)
	{
		/*
		if(container.isShuttingDown())
		{
			listener.exceptionOccurred(new RuntimeException("No new agents may be created when platform is shutting down."));
			return;
			//throw new RuntimeException("No new agents may be created when platform is shutting down.");
		}
		*/
			
		// Load the model with fitting factory.
		
		IComponentFactory factory = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
			{
				IComponentFactory	cf	= (IComponentFactory)it.next();
				if(cf.isLoadable(model))
				{
					factory	= cf;
				}
			}
		}
		ILoadableComponentModel lmodel = factory.loadModel(model);

		// Create id and adapter.
		
		AgentIdentifier cid;
		StandaloneComponentAdapter adapter;
		AMSAgentDescription	ad;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				if(name==null)
				{
					cid = generateComponentIdentifier(lmodel.getName());
				}
				else
				{
					cid = new AgentIdentifier(name+"@"+container.getName()); // Hack?!
					if(adapters.containsKey(cid))
					{
						listener.exceptionOccurred(new RuntimeException("Agent name already exists on agent platform."));
						return;
					}
					IMessageService	ms	= (IMessageService)container.getService(IMessageService.class);
					if(ms!=null)
						cid.setAddresses(ms.getAddresses());
				}
		
				// Arguments must be isolated between agent instances.
				adapter = new StandaloneComponentAdapter(container, cid);
				adapters.put(cid, adapter);
				
				ad	= new AMSAgentDescription(cid);
				ad.setState(IComponentDescription.STATE_INITIATED);
				adapter.setState(IComponentDescription.STATE_INITIATED);
				descs.put(cid, ad);
			}
		}
		
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}
		
		// Create the agent instance (interpreter and state).
		
		IComponentInstance instance = factory.createComponentInstance(adapter, lmodel, config, args);
		adapter.setComponent(instance);
		
//		System.out.println("added: "+agentdescs.size()+", "+aid);
		
		// Register new agent at contexts.
		IContextService	cs	= (IContextService)container.getService(IContextService.class);
		if(cs!=null)
		{
			IContext[]	contexts	= cs.getContexts((IComponentIdentifier)creator);
			for(int i=0; contexts!=null && i<contexts.length; i++)
			{
				((BaseContext)contexts[i]).agentCreated((IComponentIdentifier)creator, cid);
			}
		}

		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			alisteners	= (IComponentListener[])listeners.toArray(new IComponentListener[listeners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].componentAdded(ad);
		}
		
		listener.resultAvailable(cid.clone());
	}
    
	/**
	 *  Start a previously created component on the platform.
	 *  @param componentid The id of the previously created component.
	 */
	public void	startComponent(IComponentIdentifier componentid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				AMSAgentDescription	desc	= (AMSAgentDescription)descs.get(componentid);
				if(desc!=null && IComponentDescription.STATE_INITIATED.equals(desc.getState()))
				{
					StandaloneComponentAdapter	adapter	= (StandaloneComponentAdapter)adapters.get(componentid);
					if(adapter!=null)
					{
						// Todo: use result listener and set active state after agent has inited.
						desc.setState(IComponentDescription.STATE_ACTIVE);
						adapter.setState(IComponentDescription.STATE_ACTIVE);
						adapter.wakeup();
					}
					else
					{
						// Shouldn't happen?
						listener.exceptionOccurred(new RuntimeException("Cannot start unknown component: "+componentid));
						return;
					}
				}
				else if(desc!=null)
				{
					listener.exceptionOccurred(new RuntimeException("Cannot start component "+componentid+" in state: "+desc.getState()));
					return;
				}
				else
				{
					listener.exceptionOccurred(new RuntimeException("Cannot start unknown component: "+componentid));
					return;
				}
			}
		}
		
		listener.resultAvailable(componentid);		
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public void destroyComponent(IComponentIdentifier cid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
//				System.out.println("killing: "+cid);
				
				StandaloneComponentAdapter agent = (StandaloneComponentAdapter)adapters.get(cid);
				if(agent==null)
				{
					listener.exceptionOccurred(new RuntimeException("Component "+cid+" does not exist."));
					return;

					//System.out.println(agentdescs);
					//throw new RuntimeException("Agent "+aid+" does not exist.");
				}
				
				// todo: does not work always!!! A search could be issued before agents had enough time to kill itself!
				// todo: killAgent should only be called once for each agent?
				AMSAgentDescription	desc	= (AMSAgentDescription)descs.get(cid);
				if(desc!=null)
				{
					// Resume a suspended agent before killing it.
					if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
						resumeComponent(cid, null);
					
					if(IComponentDescription.STATE_ACTIVE.equals(desc.getState()))
					//if(!AMSAgentDescription.STATE_TERMINATING.equals(desc.getState()))
					{
						agent.setState(IComponentDescription.STATE_TERMINATING);
						desc.setState(IComponentDescription.STATE_TERMINATING);
//						if(listeners.get(aid)!=null)
//							throw new RuntimeException("Multiple result listeners for agent: "+aid);
//						listeners.put(aid, listener);
						agent.killComponent(new CleanupCommand(cid, listener));
					}
					else
					{
						listener.exceptionOccurred(new RuntimeException("Cannot kill "+cid+" component: "+desc.getState()));
						//throw new RuntimeException("Cannot kill "+aid+" agent: "+desc.getState());
					}
				}
			}
		}
	}

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void suspendComponent(IComponentIdentifier componentid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				AMSAgentDescription ad = (AMSAgentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(new RuntimeException("Component identifier not registered: "+componentid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IComponentDescription.STATE_ACTIVE.equals(ad.getState()))
					listener.exceptionOccurred(new RuntimeException("Only active components can be suspended: "+componentid+" "+ad.getState()));
					//throw new RuntimeException("Only active agents can be suspended: "+aid+" "+ad.getState());
				
				// todo: call listener when suspension has finished!!!
				
				ad.setState(IComponentDescription.STATE_SUSPENDED);
				adapter.setState(IComponentDescription.STATE_SUSPENDED);
				IExecutionService exe = (IExecutionService)container.getService(IExecutionService.class);
				exe.cancel(adapter, listener);
			}
		}
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void resumeComponent(IComponentIdentifier componentid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				AMSAgentDescription ad = (AMSAgentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(new RuntimeException("Component identifier not registered: "+componentid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IComponentDescription.STATE_SUSPENDED.equals(ad.getState()))
					listener.exceptionOccurred(new RuntimeException("Only active components can be suspended: "+componentid+" "+ad.getState()));
					//throw new RuntimeException("Only suspended agents can be resumed: "+aid+" "+ad.getState());
				
				ad.setState(IComponentDescription.STATE_ACTIVE);
				adapter.setState(IComponentDescription.STATE_ACTIVE);
				IExecutionService exe = (IExecutionService)container.getService(IExecutionService.class);
				exe.execute(adapter);
			}
		}
//		pcs.firePropertyChange("agents", null, adapters);
	
		listener.resultAvailable(null);
	}
	
	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentListener listener)
    {
		synchronized(listeners)
		{
			listeners.add(listener);
		}
    }
    
    /**
     *  Remove a listener.
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentListener listener)
    {
		synchronized(listeners)
		{
			listeners.remove(listener);
		}
    }
    
    //-------- helper classes --------

	/**
	 *  Command that is executed on agent cleanup.
	 */
	class CleanupCommand implements IResultListener
	{
		protected IComponentIdentifier cid;
		protected IResultListener listener;
		
		public CleanupCommand(IComponentIdentifier cid, IResultListener listener)
		{
//			System.out.println("CleanupCommand created");
			this.cid = cid;
			this.listener = listener;
		}
		
		public void resultAvailable(Object result)
		{
//			System.out.println("CleanupCommand: "+result);
			IComponentDescription ad = (IComponentDescription)descs.get(cid);
			synchronized(adapters)
			{
				synchronized(descs)
				{
//					System.out.println("remove called for: "+cid);
					StandaloneComponentAdapter	adapter	= (StandaloneComponentAdapter)adapters.remove(cid);
					if(adapter==null)
						throw new RuntimeException("Component Identifier not registered: "+cid);
					adapter.setState(IComponentDescription.STATE_TERMINATED);
					descs.remove(cid);
					
					// Stop execution of agent.
					((IExecutionService)container.getService(IExecutionService.class)).cancel(adapter, null);
				}
			}
			
			// Deregister killed agent at contexts.
			IContextService	cs	= (IContextService)container.getService(IContextService.class);
			if(cs!=null)
			{
				IContext[]	contexts	= cs.getContexts(cid);
				for(int i=0; contexts!=null && i<contexts.length; i++)
				{
					((BaseContext)contexts[i]).agentDestroyed(cid);
				}
			}

			IComponentListener[]	alisteners;
			synchronized(listeners)
			{
				alisteners	= (IComponentListener[])listeners.toArray(new IComponentListener[listeners.size()]);
			}
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				try
				{
					alisteners[i].componentRemoved(ad);
				}
				catch(Exception e)
				{
					System.out.println("WARNING: Exception when removing agent: "+ad+", "+e);
				}
			}
			
//			System.out.println("CleanupCommand end.");
			
			if(listener!=null)
				listener.resultAvailable(result);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			resultAvailable(cid);
		}
	}
	
	//-------- internal methods --------
    
	/**
	 *  Get the component adapter for a component identifier.
	 *  @param aid The component identifier.
	 *  @param listener The result listener.
	 */
    // Todo: Hack!!! remove?
	public void getComponentAdapter(IComponentIdentifier cid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(adapters.get(cid));
	}
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public void getExternalAccess(IComponentIdentifier cid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
	
		StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(cid);
		if(adapter==null)
			listener.exceptionOccurred(new RuntimeException("No local component found for component identifier: "+cid));
		else
			adapter.getComponentInstance().getExternalAccess(listener);
	}

	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses)
	{
		if(local)
			name = name + "@" + container.getName();
		return new AgentIdentifier(name, addresses, null);		
	}

	/**
	 *  Create a search constraints object.
	 *  @param maxresults The maximum number of results.
	 *  @param maxdepth The maximal search depth.
	 *  @return The search constraints.
	 */
	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth)
	{
		SearchConstraints	ret	= new SearchConstraints();
		ret.setMaxResults(maxresults);
		ret.setMaxDepth(maxdepth);
		return ret;
	}
	
	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @return The ams agent description.
	 */
	public IComponentDescription createComponentDescription(IComponentIdentifier agent, String state, String ownership)
	{
		AMSAgentDescription	ret	= new AMSAgentDescription(agent);
		ret.setState(state);
		ret.setOwnership(ownership);
		return ret;
	}
	
	//--------- information methods --------
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public void getComponentDescription(IComponentIdentifier cid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		AMSAgentDescription ret = (AMSAgentDescription)descs.get(cid); // Hack!
		if(ret!=null)
		{
			// Todo: addresses required for communication across platforms.
//			ret.setName(refreshAgentIdentifier(aid));
			ret	= (AMSAgentDescription)ret.clone();
		}
		
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public void getComponentDescriptions(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(descs.values().toArray(new AMSAgentDescription[0]));
	}
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 */
	public void getComponentIdentifiers(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		IComponentIdentifier[] ret;
		
		synchronized(adapters)
		{
			ret = (IComponentIdentifier[])adapters.keySet().toArray(new IComponentIdentifier[adapters.size()]);
			// Todo: addresses required for inter-platform comm.
//			for(int i=0; i<ret.length; i++)
//				ret[i] = refreshAgentIdentifier(ret[i]); // Hack!
		}
		
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions.
	 */
	public void	searchComponents(IComponentDescription adesc, ISearchConstraints con, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
//		System.out.println("search: "+agents);
		AMSAgentDescription[] ret;

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			AMSAgentDescription ad = (AMSAgentDescription)descs.get(adesc.getName());
			if(ad!=null && ad.getName().equals(adesc.getName()))
			{
				// Todo: addresses reuqired for interplatform comm.
//				ad.setName(refreshAgentIdentifier(ad.getName()));
				AMSAgentDescription	desc	= (AMSAgentDescription)ad.clone();
				ret = new AMSAgentDescription[]{desc};
			}
			else
			{
				ret	= new AMSAgentDescription[0];
			}
		}

		// Otherwise search for matching descriptions.
		else
		{
			List	tmp	= new ArrayList();
			synchronized(descs)
			{
				for(Iterator it=descs.values().iterator(); it.hasNext(); )
				{
					AMSAgentDescription	test	= (AMSAgentDescription)it.next();
					if(adesc==null ||
						(adesc.getOwnership()==null || adesc.getOwnership().equals(test.getOwnership()))
						&& (adesc.getState()==null || adesc.getState().equals(test.getState())))
					{
						tmp.add(test.clone());
					}
				}
			}
			ret	= (AMSAgentDescription[])tmp.toArray(new AMSAgentDescription[tmp.size()]);
		}

		//System.out.println("searched: "+ret);
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	protected AgentIdentifier generateComponentIdentifier(String name)
	{
		AgentIdentifier ret = null;

		synchronized(adapters)
		{
			do
			{
				ret = new AgentIdentifier(name+(compcnt++)+"@"+container.getName()); // Hack?!
			}
			while(adapters.containsKey(ret));
		}
		
		IMessageService	ms	= (IMessageService)container.getService(IMessageService.class);
		if(ms!=null)
			ret.setAddresses(ms.getAddresses());

		return ret;
	}
	
	//-------- IService interface --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		
	}

	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(null);
	}
}
