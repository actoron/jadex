package jadex.adapter.jade;

import jade.wrapper.ControllerException;
import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.execution.IExecutionService;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.jade.fipaimpl.AMSAgentDescription;
import jadex.adapter.jade.fipaimpl.AgentIdentifier;
import jadex.adapter.jade.fipaimpl.SearchConstraints;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IAgentModel;
import jadex.bridge.IMessageService;
import jadex.bridge.IPlatformService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;

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
public class AMS implements IAMS, IPlatformService
{
	//-------- constants --------

	/** The agent counter. */
	public static int agentcnt = 0;

	//-------- attributes --------

	/** The agent platform. */
	protected Platform platform;

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
    public AMS(Platform platform)
    {
		this.platform = platform;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.agentdescs = Collections.synchronizedMap(SCollection.createHashMap());
		this.logger = Logger.getLogger(platform.getName()+".ams");
		this.listeners = SCollection.createArrayList();
    }

    //-------- IMAS interface methods --------
    
	/**
	 *  Create a new agent on the platform.
	 *  Ensures (in non error case) that the aid of
	 *  the new agent is added to the AMS when call returns.
	 *  @param name The agent name (null for auto creation)
	 *  @param model The model name.
	 *  @param confi The configuration.
	 *  @param args The arguments map (name->value).
	 */
	public void	createAgent(String name, String model, String config, Map args, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}
		
//		if(platform.isShuttingDown())
//		{
//			listener.exceptionOccurred(new RuntimeException("No new agents may be created when platform is shutting down."));
//			return;
//			//throw new RuntimeException("No new agents may be created when platform is shutting down.");
//		}

		AgentIdentifier aid;
		JadeAgentAdapter agent;
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

				List argus = args==null? new ArrayList(): new ArrayList(args.values());
				argus.add(0, model);
				argus.add(1, config);
				try
				{
					platform.getPlatformController().createNewAgent(getShortName(model), "jadex.adapter.jade.JadeAgentAdapter", argus.toArray());
				}
				catch(ControllerException e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
//				agent = new JadeAgentAdapter(platform, aid, model, config, args==null? null: new HashMap(args));

				// todo:!!!
//				adapters.put(aid, agent);
//				ad	= new AMSAgentDescription(aid);
//				ad.setState(IAMSAgentDescription.STATE_INITIATED);
//				agent.setState(IAMSAgentDescription.STATE_INITIATED);
//				agentdescs.put(aid, ad);
			}
		}
//		System.out.println("added: "+agentdescs.size()+", "+aid);

		IAMSListener[]	alisteners;
		synchronized(listeners)
		{
			alisteners	= (IAMSListener[])listeners.toArray(new IAMSListener[listeners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
//			alisteners[i].agentAdded(ad);
		}
		
		listener.resultAvailable(aid.clone());
	}

	/**
	 *  Start a previously created agent on the platform.
	 *  @param agent The id of the previously created agent.
	 */
	public void	startAgent(IAgentIdentifier agent, IResultListener listener)
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
					JadeAgentAdapter	adapter	= (JadeAgentAdapter)adapters.get(agent);
					if(adapter!=null)
					{
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
		
		listener.resultAvailable(null);
	}
	
	/**
	 *  Destroy (forcefully terminate) an agent on the platform.
	 *  @param aid	The agent to destroy.
	 */
	public void destroyAgent(final IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				//System.out.println("killing: "+aid);
				
				JadeAgentAdapter	agent	= (JadeAgentAdapter)adapters.get(aid);
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
						resumeAgent(aid, null);
					
					if(IAMSAgentDescription.STATE_ACTIVE.equals(desc.getState()))
					//if(!AMSAgentDescription.STATE_TERMINATING.equals(desc.getState()))
					{
						agent.setState(IAMSAgentDescription.STATE_TERMINATING);
						desc.setState(IAMSAgentDescription.STATE_TERMINATING);
//						if(listeners.get(aid)!=null)
//							throw new RuntimeException("Multiple result listeners for agent: "+aid);
//						listeners.put(aid, listener);
						agent.killAgent(new CleanupCommand(aid, listener));
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
	public void suspendAgent(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				JadeAgentAdapter adapter = (JadeAgentAdapter)adapters.get(aid);
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
	public void resumeAgent(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(agentdescs)
			{
				JadeAgentAdapter adapter = (JadeAgentAdapter)adapters.get(aid);
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
	public void containsAgent(IAgentIdentifier aid, IResultListener listener)
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
	public void getAgentDescription(IAgentIdentifier aid, IResultListener listener)
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
		
		IAgentIdentifier[] ret;
		
		synchronized(adapters)
		{
			ret = (IAgentIdentifier[])adapters.keySet().toArray(new IAgentIdentifier[adapters.size()]);
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
	public void getAgentAdapter(IAgentIdentifier aid, IResultListener listener)
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
	public void getExternalAccess(IAgentIdentifier aid, IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		JadeAgentAdapter adapter = (JadeAgentAdapter)adapters.get(aid);
		if(adapter==null)
			listener.exceptionOccurred(new RuntimeException("No local agent found for agent identifier: "+aid));
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
	public IAgentIdentifier createAgentIdentifier(String name, boolean local)
	{
		if(local)
			name = name + "@" + platform.getName();
		return new AgentIdentifier(name);
	}
	
	/**
	 *  Create an agent identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 */
	public IAgentIdentifier createAgentIdentifier(String name, boolean local, String[] addresses)
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
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent)
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
	public IAMSAgentDescription createAMSAgentDescription(IAgentIdentifier agent, String state, String ownership)
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
		// todo
//		platform.shutdown(listener);
	}

	//-------- Helper methods --------
		
	/**
	 *  Get the agent adapters.
	 *  @return The agent adapters.
	 */
	public IAgentAdapter[] getAgentAdapters()
	{
		synchronized(adapters)
		{
			return (IAgentAdapter[])adapters.values().toArray(new IAgentAdapter[adapters.size()]);
		}
	}
	
	/**
	 *  Copy and refresh local agent identifier.
	 *  @param aid The agent identifier.
	 *  @return The refreshed copy of the aid.
	 */
	public IAgentIdentifier refreshAgentIdentifier(IAgentIdentifier aid)
	{
		IAgentIdentifier	ret	= (IAgentIdentifier)((AgentIdentifier)aid).clone();
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
    public void addAMSListener(IAMSListener listener)
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
    public void removeAMSListener(IAMSListener listener)
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
		IAgentModel	model	= platform.getAgentFactory().loadModel(filename);
		return model.getName();
	}

	/**
	 *  Command that is executed on agent cleanup.
	 */
	class CleanupCommand implements IResultListener
	{
		protected IAgentIdentifier aid;
		protected IResultListener listener;
		
		public CleanupCommand(IAgentIdentifier aid, IResultListener listener)
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
					JadeAgentAdapter	adapter	= (JadeAgentAdapter)adapters.remove(aid);
					if(adapter==null)
						throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
					adapter.setState(IAMSAgentDescription.STATE_TERMINATED);
					agentdescs.remove(aid);
					
					// Stop execution of agent.
					((IExecutionService)platform.getService(IExecutionService.class)).cancel(adapter, null);
				}
			}
			
			IAMSListener[]	alisteners;
			synchronized(listeners)
			{
				alisteners	= (IAMSListener[])listeners.toArray(new IAMSListener[listeners.size()]);
			}
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].agentRemoved(ad);
			}
			
			if(listener!=null)
				listener.resultAvailable(null);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			resultAvailable(null);
		}
	}
	
}

/**
 *
 * /
public class AMS implements IAMS, IPlatformService
{
	/* * The platform. * /
	protected Platform platform;
	
	/**
	 * 
	 * /
	public AMS(Platform platform)
	{
		this.platform = platform;
	}
	
	/**
	 *  Create a new agent on the platform.
	 *  The agent will not run before the {@link startAgent(AgentIdentifier)}
	 *  method is called.
	 *  Ensures (in non error case) that the aid of
	 *  the new agent is added to the AMS when call returns.
	 *  @param name The agent name (null for auto creation)
	 *  @param model The model name.
	 *  @param confi The configuration.
	 *  @param args The arguments map (name->value).
	 * /
	public void	createAgent(String name, String model, String config, Map args, IResultListener listener)
	{
		try
		{
			AgentController ac = platform.getContainer().createNewAgent(name, model, args.values().toArray());
			listener.resultAvailable(new AID(name, AID.ISLOCALNAME));
		}
		catch(StaleProxyException e)
		{
			listener.exceptionOccurred(e);
		}
	}
	

}*/
