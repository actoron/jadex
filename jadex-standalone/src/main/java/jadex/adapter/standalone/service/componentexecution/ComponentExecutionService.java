package jadex.adapter.standalone.service.componentexecution;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.contextservice.BaseContext;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.standalone.AbstractPlatform;
import jadex.adapter.standalone.StandaloneComponentAdapter;
import jadex.adapter.standalone.fipaimpl.AMSAgentDescription;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.IMessageService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

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
	public void	registerComponent(String name, IComponentInstance component, IResultListener listener, Object creator)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}
		
		if(container.isShuttingDown())
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
			synchronized(descs)
			{
				if(name==null)
				{
					aid = generateAgentIdentifier(getShortName(component));
				}
				else
				{
					aid = new AgentIdentifier(name+"@"+container.getName()); // Hack?!
					if(adapters.containsKey(aid))
					{
						listener.exceptionOccurred(new RuntimeException("Agent name already exists on agent platform."));
						return;
					}
					IMessageService	ms	= (IMessageService)container.getService(IMessageService.class);
					if(ms!=null)
						aid.setAddresses(ms.getAddresses());
				}
		
				// Arguments must be isolated between agent instances.
				agent = new StandaloneComponentAdapter(container, aid, component);
				adapters.put(aid, agent);
				
				ad	= new AMSAgentDescription(aid);
				ad.setState(IAMSAgentDescription.STATE_INITIATED);
				agent.setState(IAMSAgentDescription.STATE_INITIATED);
				descs.put(aid, ad);
			}
		}
//		System.out.println("added: "+agentdescs.size()+", "+aid);
		
		// Register new agent at contexts.
		IContextService	cs	= (IContextService)container.getService(IContextService.class);
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
	 *  Start a previously created component on the platform.
	 *  @param componentid The id of the previously created component.
	 */
	public void	startComponent(IComponentIdentifier componentid, IResultListener listener)
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
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public void destroyComponent(IComponentIdentifier componentid, IResultListener listener);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void suspendComponent(IComponentIdentifier componentid, IResultListener listener);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public void resumeComponent(IComponentIdentifier componentid, IResultListener listener);
	
	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentListener listener);
    
    /**
     *  Remove a listener.
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentListener listener);
}
