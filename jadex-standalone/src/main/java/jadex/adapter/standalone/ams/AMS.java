package jadex.adapter.standalone.ams;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.SComponentFactory;
import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.standalone.AbstractPlatform;
import jadex.adapter.standalone.StandaloneComponentAdapter;
import jadex.adapter.standalone.fipaimpl.AMSAgentDescription;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.adapter.standalone.fipaimpl.SearchConstraints;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.execution.IExecutionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Built-in standalone agent platform, with only basic features.
 *  // todo: what about this property change support? where used?
 */
public class AMS implements IAMS, IService
{
	//-------- constants --------

	/** The agent counter. */
	public static int agentcnt = 0;

	//-------- attributes --------

	/** The agent platform. */
	protected AbstractPlatform platform;

	/** The agents (aid->adapter agent). */
	protected Map adapters;
	
	/** The ams agent descriptions (aid -> ams agent description). */
	protected Map agentdescs;
	
	/** The logger. */
	protected Logger logger;

	/** The ams listeners. */
	protected List listeners;
	
    //-------- constructors --------

    /**
     *  Create a new AMS.
     */
    public AMS(AbstractPlatform platform)
    {
		this.platform = platform;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.agentdescs = Collections.synchronizedMap(SCollection.createHashMap());
		this.logger = Logger.getLogger(platform.getName()+".ams");
		this.listeners = SCollection.createArrayList();
    }

    //-------- IAMS interface methods --------
    
    /**
	 *  Test if the execution service can handle the element (or model).
	 *  @param element The element (or its filename).
	 * /
	public boolean isResponsible(Object element)
	{
		boolean ret = element instanceof IAgentIdentifier;
		
		if(!ret && element instanceof String)
		{
			ret = IComponentFactory.ELEMENT_TYPE_AGENT.equals(
				SComponentFactory.getElementType(platform, (String)element));
		}
		
		return ret;
	}*/
    
	/**
	 *  Create a new agent on the platform.
	 *  Ensures (in non error case) that the aid of
	 *  the new agent is added to the AMS when call returns.
	 *  @param name The agent name (null for auto creation)
	 *  @param model The model name.
	 *  @param confi The configuration.
	 *  @param args The arguments map (name->value).
	 */
	public void	createElement(String name, String model, String config, Map args, IResultListener listener, Object creator)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}
		
		if(platform.isShuttingDown())
		{
			listener.exceptionOccurred(new RuntimeException("No new agents may be created when platform is shutting down."));
			return;
			//throw new RuntimeException("No new agents may be created when platform is shutting down.");
		}

		AgentIdentifier aid;
		StandaloneComponentAdapter agent;
		AMSAgentDescription	ad;
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				if(name==null)
				{
					aid = generateAgentIdentifier(getShortName(model));
				}
				else
				{
					aid = new AgentIdentifier(name+"@"+platform.getName()); // Hack?!
					if(adapters.containsKey(aid))
					{
						listener.exceptionOccurred(new RuntimeException("Agent name already exists on agent platform."));
						return;
					}
					IMessageService	ms	= (IMessageService)platform.getService(IMessageService.class);
					if(ms!=null)
						aid.setAddresses(ms.getAddresses());
				}
		
				// Arguments must be isolated between agent instances.
				agent = new StandaloneComponentAdapter(platform, aid, model, config, args==null? null: new HashMap(args));
				adapters.put(aid, agent);
				
				ad	= new AMSAgentDescription(aid);
				ad.setState(IAMSAgentDescription.STATE_INITIATED);
				agent.setState(IAMSAgentDescription.STATE_INITIATED);
				agentdescs.put(aid, ad);
			}
		}
//		System.out.println("added: "+agentdescs.size()+", "+aid);
		
		// Register new agent at contexts.
		IContextService	cs	= (IContextService)platform.getService(IContextService.class);
		if(cs!=null)
		{
			IContext[]	contexts	= cs.getContexts((IComponentIdentifier)creator);
			for(int i=0; contexts!=null && i<contexts.length; i++)
			{
				((BaseContext)contexts[i]).agentCreated((IComponentIdentifier)creator, aid);
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
		
		listener.resultAvailable(aid.clone());
	}

	/**
	 *  Start a previously created agent on the platform.
	 *  @param agent The id of the previously created agent.
	 */
	public void	startElement(Object agent, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				AMSAgentDescription	desc	= (AMSAgentDescription)agentdescs.get(agent);
				if(desc!=null && IAMSAgentDescription.STATE_INITIATED.equals(desc.getState()))
				{
					StandaloneComponentAdapter	adapter	= (StandaloneComponentAdapter)adapters.get(agent);
					if(adapter!=null)
					{
						// Todo: use result listener and set active state after agent has inited.
						desc.setState(IAMSAgentDescription.STATE_ACTIVE);
						adapter.setState(IAMSAgentDescription.STATE_ACTIVE);
						adapter.wakeup();
					}
					else
					{
						// Shouldn't happen?
						listener.exceptionOccurred(new RuntimeException("Cannot start unknown agent: "+agent));
						return;
					}
				}
				else if(desc!=null)
				{
					listener.exceptionOccurred(new RuntimeException("Cannot start agent "+agent+" in state: "+desc.getState()));
					return;
				}
				else
				{
					listener.exceptionOccurred(new RuntimeException("Cannot start unknown agent: "+agent));
					return;
				}
			}
		}
		
		listener.resultAvailable(agent);
	}
	
	/**
	 *  Destroy (forcefully terminate) an agent on the platform.
	 *  @param aid	The agent to destroy.
	 */
	public void destroyElement(final Object aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				//System.out.println("killing: "+aid);
				
				StandaloneComponentAdapter agent = (StandaloneComponentAdapter)adapters.get(aid);
				if(agent==null)
				{
					listener.exceptionOccurred(new RuntimeException("Agent "+aid+" does not exist."));
					return;

					//System.out.println(agentdescs);
					//throw new RuntimeException("Agent "+aid+" does not exist.");
				}
				
				// todo: does not work always!!! A search could be issued before agents had enough time to kill itself!
				// todo: killAgent should only be called once for each agent?
				AMSAgentDescription	desc	= (AMSAgentDescription)agentdescs.get(aid);
				if(desc!=null)
				{
					// Resume a suspended agent before killing it.
					if(IAMSAgentDescription.STATE_SUSPENDED.equals(desc.getState()))
						resumeComponent(aid, null);
					
					if(IAMSAgentDescription.STATE_ACTIVE.equals(desc.getState()))
					//if(!AMSAgentDescription.STATE_TERMINATING.equals(desc.getState()))
					{
						agent.setState(IAMSAgentDescription.STATE_TERMINATING);
						desc.setState(IAMSAgentDescription.STATE_TERMINATING);
//						if(listeners.get(aid)!=null)
//							throw new RuntimeException("Multiple result listeners for agent: "+aid);
//						listeners.put(aid, listener);
						agent.killAgent(new CleanupCommand((IComponentIdentifier)aid, listener));
					}
					else
					{
						listener.exceptionOccurred(new RuntimeException("Cannot kill "+aid+" agent: "+desc.getState()));
						//throw new RuntimeException("Cannot kill "+aid+" agent: "+desc.getState());
					}
				}
			}
		}
	}
	
	/**
	 *  Suspend the execution of an agent.
	 *  @param aid The agent identifier.
	 *  // todo: make sure that agent is really suspended an does not execute
	 *  an action currently.
	 */
	public void suspendElement(Object aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(aid);
				AMSAgentDescription ad = (AMSAgentDescription)agentdescs.get(aid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(new RuntimeException("Agent Identifier not registered in AMS: "+aid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IAMSAgentDescription.STATE_ACTIVE.equals(ad.getState()))
					listener.exceptionOccurred(new RuntimeException("Only active agents can be suspended: "+aid+" "+ad.getState()));
					//throw new RuntimeException("Only active agents can be suspended: "+aid+" "+ad.getState());
				
				// todo: call listener when suspension has finished!!!
				
				ad.setState(IAMSAgentDescription.STATE_SUSPENDED);
				adapter.setState(IAMSAgentDescription.STATE_SUSPENDED);
				IExecutionService exe = (IExecutionService)platform.getService(IExecutionService.class);
				exe.cancel(adapter, listener);
			}
		}
//		pcs.firePropertyChange("agents", null, adapters);
		
//		listener.resultAvailable(null);
	}
	
	/**
	 *  Resume the execution of an agent.
	 *  @param aid The agent identifier.
	 */
	public void resumeElement(Object aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(aid);
				AMSAgentDescription ad = (AMSAgentDescription)agentdescs.get(aid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(new RuntimeException("Agent Identifier not registered in AMS: "+aid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IAMSAgentDescription.STATE_SUSPENDED.equals(ad.getState()))
					listener.exceptionOccurred(new RuntimeException("Only active agents can be suspended: "+aid+" "+ad.getState()));
					//throw new RuntimeException("Only suspended agents can be resumed: "+aid+" "+ad.getState());
				
				ad.setState(IAMSAgentDescription.STATE_ACTIVE);
				adapter.setState(IAMSAgentDescription.STATE_ACTIVE);
				IExecutionService exe = (IExecutionService)platform.getService(IExecutionService.class);
				exe.execute(adapter);
			}
		}
//		pcs.firePropertyChange("agents", null, adapters);
	
		listener.resultAvailable(null);
	}

	/**
	 *  Search for agents matching the given description.
	 *  @return An array of matching agent descriptions.
	 */
	public void	searchAgents(IAMSAgentDescription adesc, ISearchConstraints con, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
//		System.out.println("search: "+agents);
		AMSAgentDescription[] ret;

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			AMSAgentDescription ad = (AMSAgentDescription)agentdescs.get(adesc.getName());
			if(ad!=null && ad.getName().equals(adesc.getName()))
			{
				ad.setName(refreshAgentIdentifier(ad.getName()));
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
			synchronized(agentdescs)
			{
				for(Iterator it=agentdescs.values().iterator(); it.hasNext(); )
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
	 *  Test if an agent is currently living on the platform.
	 *  @param aid The agent identifier.
	 *  @return True, if agent is hosted on platform.
	 */
	public void containsAgent(IComponentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(adapters.containsKey(aid)? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the agent description of a single agent.
	 *  @param aid The agent identifier.
	 *  @return The agent description of this agent.
	 */
	public void getAgentDescription(IComponentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		AMSAgentDescription ret = (AMSAgentDescription)agentdescs.get(aid); // Hack!
		if(ret!=null)
		{
			ret.setName(refreshAgentIdentifier(aid));
			ret	= (AMSAgentDescription)ret.clone();
		}
		
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Get the agent descriptions.
	 *  @return The agent descriptions.
	 */
	public void getAgentDescriptions(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(agentdescs.values().toArray(new AMSAgentDescription[0]));
	}
	
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public void getAgentIdentifiers(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		IComponentIdentifier[] ret;
		
		synchronized(adapters)
		{
			ret = (IComponentIdentifier[])adapters.keySet().toArray(new IComponentIdentifier[adapters.size()]);
			for(int i=0; i<ret.length; i++)
				ret[i] = refreshAgentIdentifier(ret[i]); // Hack!
		}
		
		listener.resultAvailable(ret);
	}
	
	/**
	 *  Get the number of active agents.
	 *  @return The number of active agents.
	 */
	public void getAgentCount(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(new Integer(adapters.size()));
	}
	
	/**
	 *  Get the agent adapter for an agent identifier.
	 *  @param aid The agent identifier.
	 *  @return The agent adapter.
	 */
	public void getAgentAdapter(IComponentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(adapters.get(aid));
	}
	
	/**
	 *  Get the external access of an agent.
	 *  @param aid The agent identifier.
	 *  @param listener The result listener.
	 */
	public void getExternalAccess(IComponentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(aid);
		if(adapter==null)
			listener.exceptionOccurred(new RuntimeException("No local agent found for agent identifier: "+aid));
//		else if(!IAMSAgentDescription.STATE_ACTIVE.equals(adapter.getState()))
//			listener.exceptionOccurred(new RuntimeException("Agent not (yet?) active: "+aid));
		else
			adapter.getKernelAgent().getExternalAccess(listener);
	}

	
	//-------- IPlatformService interface methods --------
	 
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// nothing to do.
	}
	
	/**
	 *  Called when the platform shuts down.
	 *  At this time all agents already had time to kill themselves
	 *  (as specified in the platform shutdown time).
	 *  Remaining agents should be discarded.
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		listener.resultAvailable(null);
	}
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @return The new agent identifier.
	 */
	public IComponentIdentifier createAgentIdentifier(String name, boolean local)
	{
		if(local)
		{
			if(name.indexOf("@")!=-1)
				throw new RuntimeException("Agent local name must not contain '@' sign: "+name);
			name = name + "@" + platform.getName();
		}
		return new AgentIdentifier(name);
	}
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 */
	public IComponentIdentifier createAgentIdentifier(String name, boolean local, String[] addresses)
	{
		if(local)
			name = name + "@" + platform.getName();
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
	public IAMSAgentDescription createAMSAgentDescription(IComponentIdentifier agent)
	{
		return new AMSAgentDescription(agent);
	}

	/**
	 *  Create a ams agent description.
	 *  @param agent The agent.
	 *  @param state The state.
	 *  @param ownership The ownership.
	 *  @return The ams agent description.
	 */
	public IAMSAgentDescription createAMSAgentDescription(IComponentIdentifier agent, String state, String ownership)
	{
		AMSAgentDescription	ret	= new AMSAgentDescription(agent);
		ret.setState(state);
		ret.setOwnership(ownership);
		return ret;
	}

	/**
	 *  Shutdown the platform.
	 *  @param listener The listener.
	 */
	public void shutdownPlatform(IResultListener listener)
	{
		platform.shutdown(listener);
	}

	//-------- Helper methods --------
		
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public IComponentAdapter[] getAgentAdapters()
	{
		synchronized(adapters)
		{
			return (IComponentAdapter[])adapters.values().toArray(new IComponentAdapter[adapters.size()]);
		}
	}
	
	/**
	 *  Copy and refresh local agent identifier.
	 *  @param aid The agent identifier.
	 *  @return The refreshed copy of the aid.
	 */
	public IComponentIdentifier refreshAgentIdentifier(IComponentIdentifier aid)
	{
		IComponentIdentifier	ret	= (IComponentIdentifier)((AgentIdentifier)aid).clone();
		if(adapters.containsKey(aid))
		{
			IMessageService	ms	= (IMessageService)platform.getService(IMessageService.class);
			if(ms!=null)
				((AgentIdentifier)ret).setAddresses(ms.getAddresses());
		}
		return ret;
	}
	
	/**
	 *  Create an agent identifier that is allowed on the platform.
	 *  @param typename The type name.
	 *  @return The agent identifier.
	 */
	protected AgentIdentifier generateAgentIdentifier(String typename)
	{
		AgentIdentifier ret = null;

		synchronized(adapters)
		{
			do
			{
				ret = new AgentIdentifier(typename+(agentcnt++)+"@"+platform.getName()); // Hack?!
			}
			while(adapters.containsKey(ret));
		}
		
		IMessageService	ms	= (IMessageService)platform.getService(IMessageService.class);
		if(ms!=null)
			ret.setAddresses(ms.getAddresses());

		return ret;
	}
	
	//-------- listener methods --------
	
	/**
     *  Add an ams listener.
     *  The listener is registered for ams changes.
     *  @param listener  The listener to be added.
     */
    public void addElementListener(IComponentListener listener)
	{
		synchronized(listeners)
		{
			listeners.add(listener);
		}
    }
    
    /**
     *  Remove an ams listener.
     *  @param listener  The listener to be removed.
     */
    public void removeElementListener(IComponentListener listener)
	{
		synchronized(listeners)
		{
			listeners.remove(listener);
		}
    }

	/**
	 *  Get the short type name from a model filename.
	 *  @param filename The filename.
	 *  @return The short type name.
	 */
	public String getShortName(String filename)
	{
//		ILoadableElementModel	model	= platform.getAgentFactory().loadModel(filename);
		ILoadableComponentModel	model	= SComponentFactory.loadModel(platform, filename);
		return model.getName();
	}

	/**
	 *  Command that is executed on agent cleanup.
	 */
	class CleanupCommand implements IResultListener
	{
		protected IComponentIdentifier aid;
		protected IResultListener listener;
		
		public CleanupCommand(IComponentIdentifier aid, IResultListener listener)
		{
			this.aid = aid;
			this.listener = listener;
		}
		
		public void resultAvailable(Object result)
		{
			IAMSAgentDescription ad = (IAMSAgentDescription)agentdescs.get(aid);
			synchronized(adapters)
			{
				synchronized(agentdescs)
				{
//					System.out.println("remove called for: "+aid);
					StandaloneComponentAdapter	adapter	= (StandaloneComponentAdapter)adapters.remove(aid);
					if(adapter==null)
						throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
					adapter.setState(IAMSAgentDescription.STATE_TERMINATED);
					agentdescs.remove(aid);
					
					// Stop execution of agent.
					((IExecutionService)platform.getService(IExecutionService.class)).cancel(adapter, null);
				}
			}
			
			// Deregister killed agent at contexts.
			IContextService	cs	= (IContextService)platform.getService(IContextService.class);
			if(cs!=null)
			{
				IContext[]	contexts	= cs.getContexts(aid);
				for(int i=0; contexts!=null && i<contexts.length; i++)
				{
					((BaseContext)contexts[i]).agentDestroyed(aid);
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
			
			if(listener!=null)
				listener.resultAvailable(result);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			resultAvailable(aid);
		}
	}
	
}
