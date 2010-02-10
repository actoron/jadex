package jadex.adapter.standalone.service.componentexecution;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.standalone.StandaloneComponentAdapter;
import jadex.adapter.standalone.fipaimpl.CESComponentDescription;
import jadex.adapter.standalone.fipaimpl.ComponentIdentifier;
import jadex.adapter.standalone.fipaimpl.SearchConstraints;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.execution.IExecutionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Standalone implementation of component execution service.
 */
public class ComponentExecutionService implements IComponentExecutionService, IService
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
	
	/** The cleanup commands for the components (component id -> cleanup command). */
	protected Map ccs;
	
	/** The children of a component (component id -> children ids). */
	protected MultiCollection	children;
	
	/** The logger. */
	protected Logger logger;

	/** The listeners. */
	protected MultiCollection listeners;
	
	/** The result (kill listeners). */
	protected Map killresultlisteners;
	
    //-------- constructors --------

    /**
     *  Create a new component execution service.#
     *  @param container	The service container.
     */
    public ComponentExecutionService(IServiceContainer container)
	{
		this.container = container;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.descs = Collections.synchronizedMap(SCollection.createLinkedHashMap());
		this.ccs = SCollection.createLinkedHashMap();
		this.children	= SCollection.createMultiCollection();
		this.logger = Logger.getLogger(container.getName()+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.killresultlisteners = Collections.synchronizedMap(SCollection.createHashMap());
    }
    
    //-------- IComponentExecutionService interface --------

	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param config The configuration to use for initializing the component (null for default).
	 *  @param args The arguments for the component (if any).
	 *  @param suspend Create the component in suspended mode (i.e. do not run until resume() is called).
	 *  @param listener The result listener (if any). Will receive the id of the component as result.
	 *  @param parent The parent (if any).
	 */
	public void	createComponent(String name, String model, final String config, final Map args, final boolean suspend, 
		IResultListener listener, IComponentIdentifier parent, final IResultListener resultlistener, boolean master)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			listener.exceptionOccurred(this, new RuntimeException("No '@' allowed in agent name."));
			return;
			//throw new RuntimeException("No '@' allowed in agent name.");
		}

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
		String	type	= null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
			{
				IComponentFactory	cf	= (IComponentFactory)it.next();
				if(cf.isLoadable(model))
				{
					factory	= cf;
					type	= factory.getComponentType(model);
				}
			}
		}
		if(factory==null)
			throw new RuntimeException("No factory found for component: "+model);
		final ILoadableComponentModel lmodel = factory.loadModel(model);

		// Create id and adapter.
		
		final ComponentIdentifier cid;
		final StandaloneComponentAdapter adapter;
		final CESComponentDescription ad;
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
					cid = new ComponentIdentifier(name+"@"+container.getName()); // Hack?!
					if(adapters.containsKey(cid))
					{
						listener.exceptionOccurred(this, new RuntimeException("Agent name already exists on agent platform: "+cid));
						return;
					}
					IMessageService	ms	= (IMessageService)container.getService(IMessageService.class);
					if(ms!=null)
						cid.setAddresses(ms.getAddresses());
				}
		
				IComponentDescription padesc = parent!=null? (IComponentDescription)descs.get(parent): null;
				ad	= new CESComponentDescription(cid, type, parent, master);
				// Suspend when set to suspend or when parent is also suspended.
				if(suspend || (padesc!=null && IComponentDescription.STATE_SUSPENDED.equals(padesc.getState())))
				{
					ad.setState(IComponentDescription.STATE_SUSPENDED);
				}
				else
				{
					ad.setState(IComponentDescription.STATE_ACTIVE);
				}
				descs.put(cid, ad);
				if(parent!=null)
				{
					children.put(parent, cid);
				}
			}

			adapter = new StandaloneComponentAdapter(container, ad);
			adapters.put(cid, adapter);

			if(parent!=null)
			{
				final IResultListener	rl	= listener;
				final IComponentFactory	cf	= factory;
				final StandaloneComponentAdapter pad	= (StandaloneComponentAdapter)adapters.get(parent);
				pad.getComponentInstance().getExternalAccess(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						createComponentInstance(config, args, suspend, rl,
							resultlistener, cf, lmodel, cid, adapter, pad, ad, (IExternalAccess)result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						rl.exceptionOccurred(source, exception);
					}
				});
			}
			else
			{
				createComponentInstance(config, args, suspend, listener,
					resultlistener, factory, lmodel, cid, adapter, null, ad, null);
			}
		}				
	}

	/**
	 *  Create an instance of a component (step 2 of creation process).
	 */
	protected void createComponentInstance(String config, Map args,
			boolean suspend, IResultListener listener,
			final IResultListener resultlistener, IComponentFactory factory,
			ILoadableComponentModel lmodel, final ComponentIdentifier cid,
			StandaloneComponentAdapter adapter, StandaloneComponentAdapter pad,
			CESComponentDescription ad, IExternalAccess parent)
	{
		// Create the component instance.
		IComponentInstance instance = factory.createComponentInstance(adapter, lmodel, config, args, parent);
		adapter.setComponent(instance, lmodel);
		
//		System.out.println("added: "+agentdescs.size()+", "+aid);
		
		// Register component at parent.
		if(pad!=null)
		{
			pad.getComponentInstance().componentCreated(ad, lmodel);
		}

		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(cid));
			alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].componentAdded(ad);
		}
		
		if(resultlistener!=null)
			killresultlisteners.put(cid, resultlistener);
		
		listener.resultAvailable(this, cid.clone());
		
		if(!suspend)
		{
			adapter.wakeup();			
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public void destroyComponent(IComponentIdentifier cid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		CESComponentDescription	desc;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Kill subcomponents
				for(Iterator it=children.getCollection(cid).iterator(); it.hasNext(); )
				{
					destroyComponent((IComponentIdentifier)it.next(), null);	// todo: cascading delete with wait.
				}
				
//				System.out.println("killing: "+cid);
				
				StandaloneComponentAdapter agent = (StandaloneComponentAdapter)adapters.get(cid);
				if(agent==null)
				{
					listener.exceptionOccurred(this, new RuntimeException("Component "+cid+" does not exist."));
					return;

					//System.out.println(agentdescs);
					//throw new RuntimeException("Agent "+aid+" does not exist.");
				}
				
				// todo: does not work always!!! A search could be issued before agents had enough time to kill itself!
				// todo: killAgent should only be called once for each agent?
				desc	= (CESComponentDescription)descs.get(cid);
				if(desc!=null)
				{
					if(!ccs.containsKey(cid))
					{
						CleanupCommand	cc	= new CleanupCommand(cid);
						ccs.put(cid, cc);
						if(listener!=null)
							cc.addKillListener(listener);
						agent.killComponent(cc);						
					}
					else
					{
						if(listener!=null)
						{
							CleanupCommand	cc	= (CleanupCommand)ccs.get(cid);
							if(cc==null)
								listener.exceptionOccurred(this, new RuntimeException("No cleanup command for component "+cid+": "+desc.getState()));
							cc.addKillListener(listener);
						}
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
		
		CESComponentDescription ad;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Suspend subcomponents
				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
				{
					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
					if(IComponentDescription.STATE_ACTIVE.equals(((IComponentDescription)descs.get(child)).getState()))
					{
						suspendComponent((IComponentIdentifier)it.next(), null);	// todo: cascading resume with wait.
					}
				}

				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				ad = (CESComponentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(this, new RuntimeException("Component identifier not registered: "+componentid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IComponentDescription.STATE_ACTIVE.equals(ad.getState())
					/*&& !IComponentDescription.STATE_TERMINATING.equals(ad.getState())*/)
				{
					listener.exceptionOccurred(this, new RuntimeException("Only active components can be suspended: "+componentid+" "+ad.getState()));
					//throw new RuntimeException("Only active agents can be suspended: "+aid+" "+ad.getState());
				}
				
				ad.setState(IComponentDescription.STATE_SUSPENDED);
				IExecutionService exe = (IExecutionService)container.getService(IExecutionService.class);
				exe.cancel(adapter, listener);
			}
		}
		
		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(componentid));
			alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].componentChanged(ad);
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
		
		CESComponentDescription ad;
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Resume subcomponents
				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
				{
					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
					if(IComponentDescription.STATE_SUSPENDED.equals(((IComponentDescription)descs.get(child)).getState()))
					{
						resumeComponent((IComponentIdentifier)it.next(), null);	// todo: cascading resume with wait.
					}
				}

				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				ad = (CESComponentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
					listener.exceptionOccurred(this, new RuntimeException("Component identifier not registered: "+componentid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IComponentDescription.STATE_SUSPENDED.equals(ad.getState()))
					listener.exceptionOccurred(this, new RuntimeException("Only suspended components can be resumed: "+componentid+" "+ad.getState()));
					//throw new RuntimeException("Only suspended agents can be resumed: "+aid+" "+ad.getState());
				
				ad.setState(IComponentDescription.STATE_ACTIVE);
				adapter.wakeup();
			}
		}
		
		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(componentid));
			alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].componentChanged(ad);
		}
	
		listener.resultAvailable(this, ad);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public void stepComponent(IComponentIdentifier componentid, IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				IComponentDescription cd = (IComponentDescription)descs.get(componentid);
				if(adapter==null || cd==null)
					listener.exceptionOccurred(this, new RuntimeException("Component identifier not registered: "+componentid));
					//throw new RuntimeException("Agent Identifier not registered in AMS: "+aid);
				if(!IComponentDescription.STATE_SUSPENDED.equals(cd.getState()))
					listener.exceptionOccurred(this, new RuntimeException("Only suspended components can be stepped: "+componentid+" "+cd.getState()));
					//throw new RuntimeException("Only suspended agents can be resumed: "+aid+" "+ad.getState());
				
				adapter.doStep(listener);
				IExecutionService exe = (IExecutionService)container.getService(IExecutionService.class);
				exe.execute(adapter);
			}
		}
	}

	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public void setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints)
	{
		CESComponentDescription ad;
		synchronized(descs)
		{
			ad = (CESComponentDescription)descs.get(componentid);
			ad.setBreakpoints(breakpoints);
		}
		
		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(componentid));
			alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			alisteners[i].componentChanged(ad);
		}
	}

	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentIdentifier comp, IComponentListener listener)
    {
		synchronized(listeners)
		{
			listeners.put(comp, listener);
		}
    }
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public void removeComponentListener(IComponentIdentifier comp, IComponentListener listener)
    {
		synchronized(listeners)
		{
			listeners.remove(comp, listener);
		}
    }
    
    //-------- helper classes --------

	/**
	 *  Command that is executed on component cleanup.
	 */
	class CleanupCommand implements IResultListener
	{
		protected IComponentIdentifier cid;
		protected List killlisteners;
		
		public CleanupCommand(IComponentIdentifier cid)
		{
//			System.out.println("CleanupCommand created");
			this.cid = cid;
		}
		
		public void resultAvailable(Object source, Object result)
		{
//			System.out.println("CleanupCommand: "+result);
			IComponentDescription ad = (IComponentDescription)descs.get(cid);
			Map results = null;
			StandaloneComponentAdapter	adapter;
			synchronized(adapters)
			{
				synchronized(descs)
				{
//					System.out.println("CleanupCommand remove called for: "+cid);
					adapter = (StandaloneComponentAdapter)adapters.remove(cid);
					if(adapter==null)
						throw new RuntimeException("Component Identifier not registered: "+cid);
					
					results = adapter.getComponentInstance().getResults();
					
					CESComponentDescription	desc	= (CESComponentDescription)descs.get(cid);
					desc.setState(IComponentDescription.STATE_TERMINATED);
					descs.remove(cid);
					ccs.remove(cid);
					
					// Stop execution of agent.
					((IExecutionService)container.getService(IExecutionService.class)).cancel(adapter, null);
					
					// Deregister destroyed component at parent.
					if(desc.getParent()!=null)
					{
						children.remove(desc.getParent(), desc.getName());

						StandaloneComponentAdapter	pad	= (StandaloneComponentAdapter)adapters.get(desc.getParent());
						if(pad!=null)
						{
							pad.getComponentInstance().componentDestroyed(desc);
						}
						// else parent has just been killed.
					}
				}
			}
			
//			// Deregister killed agent at contexts.
//			IContextService	cs	= (IContextService)container.getService(IContextService.class);
//			if(cs!=null)
//			{
//				IContext[]	contexts	= cs.getContexts(cid);
//				for(int i=0; contexts!=null && i<contexts.length; i++)
//				{
//					((BaseContext)contexts[i]).agentDestroyed(cid);
//				}
//			}

			IComponentListener[] alisteners;
			synchronized(listeners)
			{
				Set	slisteners	= new HashSet(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
			}
			
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				try
				{
					alisteners[i].componentRemoved(ad, results);
				}
				catch(Exception e)
				{
					System.out.println("WARNING: Exception when removing agent: "+ad+", "+e);
				}
			}
			
			IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
			if(reslis!=null)
			{
//				System.out.println("result: "+cid+" "+results);
				reslis.resultAvailable(cid, results);
			}
			
//			System.out.println("CleanupCommand end.");
			
			if(killlisteners!=null)
			{
				for(int i=0; i<killlisteners.size(); i++)
				{
					((IResultListener)killlisteners.get(i)).resultAvailable(source, result);
				}
			}
		}
		
		public void exceptionOccurred(Object source, Exception exception)
		{
			resultAvailable(source, cid);
		}
		
		/**
		 *  Add a listener to be informed, when the component has terminated.
		 * @param listener
		 */
		public void	addKillListener(IResultListener listener)
		{
			if(killlisteners==null)
				killlisteners	= new ArrayList();
			killlisteners.add(listener);
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
		
		listener.resultAvailable(this, adapters.get(cid));
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
			listener.exceptionOccurred(this, new RuntimeException("No local component found for component identifier: "+cid));
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
		return new ComponentIdentifier(name, addresses, null);		
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
	 * Create a component description.
	 * @param id The component identifier.
	 * @param state The state.
	 * @param ownership The ownership.
	 * @param type The component type.
	 * @param parent The parent.
	 * @return The component description.
	 */
	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, String ownership, String type, IComponentIdentifier parent)
	{
		CESComponentDescription	ret	= new CESComponentDescription(id, type, parent, false);
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
		
		IComponentDescription	ret = (IComponentDescription)descs.get(cid);	// Todo: synchronize?
//			if(ret!=null)
//			{
//				// Todo: addresses required for communication across platforms.
////				ret.setName(refreshComponentIdentifier(aid));
//				ret	= (IComponentDescription)ret.clone();
//			}
		
		listener.resultAvailable(this, ret);
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public void getComponentDescriptions(IResultListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Result listener required.");
		
		listener.resultAvailable(this, descs.values().toArray(new IComponentDescription[0]));	// Todo: synchronize?
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
//				ret[i] = refreshComponentIdentifier(ret[i]); // Hack!
		}
		
		listener.resultAvailable(this, ret);
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
		CESComponentDescription[] ret;

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			CESComponentDescription ad = (CESComponentDescription)descs.get(adesc.getName());
			if(ad!=null && ad.getName().equals(adesc.getName()))
			{
				// Todo: addresses reuqired for interplatform comm.
//				ad.setName(refreshComponentIdentifier(ad.getName()));
				CESComponentDescription	desc	= (CESComponentDescription)ad.clone();
				ret = new CESComponentDescription[]{desc};
			}
			else
			{
				ret	= new CESComponentDescription[0];
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
					CESComponentDescription	test	= (CESComponentDescription)it.next();
					if(adesc==null ||
						(adesc.getOwnership()==null || adesc.getOwnership().equals(test.getOwnership()))
						&& (adesc.getParent()==null || adesc.getParent().equals(test.getParent()))
						&& (adesc.getType()==null || adesc.getType().equals(test.getType()))
						&& (adesc.getState()==null || adesc.getState().equals(test.getState())))					
					{
						tmp.add(test);
					}
				}
			}
			ret	= (CESComponentDescription[])tmp.toArray(new CESComponentDescription[tmp.size()]);
		}

		//System.out.println("searched: "+ret);
		listener.resultAvailable(this, ret);
	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	protected ComponentIdentifier generateComponentIdentifier(String name)
	{
		ComponentIdentifier ret = null;

		synchronized(adapters)
		{
			do
			{
				ret = new ComponentIdentifier(name+(compcnt++)+"@"+container.getName()); // Hack?!
			}
			while(adapters.containsKey(ret));
		}
		
		IMessageService	ms	= (IMessageService)container.getService(IMessageService.class);
		if(ms!=null)
			ret.setAddresses(ms.getAddresses());

		return ret;
	}
	
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	public void	setComponentState(IComponentIdentifier comp, String state)
	{
		assert IComponentDescription.STATE_SUSPENDED.equals(state)
			|| IComponentDescription.STATE_WAITING.equals(state) : "wrong state: "+comp+", "+state;
		
		CESComponentDescription	desc	= null;
		synchronized(descs)
		{
			desc	= (CESComponentDescription)descs.get(comp);
			desc.setState(state);			
		}
		
		IComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(comp));
			alisteners	= (IComponentListener[])slisteners.toArray(new IComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		for(int i=0; i<alisteners.length; i++)
		{
			try
			{
				alisteners[i].componentChanged(desc);
			}
			catch(Exception e)
			{
				System.out.println("WARNING: Exception when changing component state: "+desc+", "+e);
			}
		}
	}
	
	//-------- IService interface --------
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
		
	}

	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
}
