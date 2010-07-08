package jadex.standalone.service;

import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.ComponentIdentifier;
import jadex.base.fipa.SearchConstraints;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;
import jadex.service.execution.IExecutionService;
import jadex.service.library.ILibraryService;
import jadex.standalone.StandaloneComponentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Standalone implementation of component execution service.
 */
public class ComponentManagementService implements IComponentManagementService, IService
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
//	protected MultiCollection	children;
	
	/** The logger. */
	protected Logger logger;

	/** The listeners. */
	protected MultiCollection listeners;
	
	/** The result (kill listeners). */
	protected Map killresultlisteners;
	
	/** The daemon counter. */
	protected int daemons;
	
	/** The autoshutdown flag. */
	protected boolean autoshutdown;

	/** The exception of a component during execution (if any). */
	protected Map exceptions;
	
	/** The execution service (cached to avoid using futures). */
	protected IExecutionService	exeservice;
	
	/** The message service (cached to avoid using futures). */
	protected IMessageService	msgservice;
	
	
	protected IComponentAdapter root;
	
    //-------- constructors --------

	 /**
     *  Create a new component execution service.#
     *  @param container	The service container.
     */
    public ComponentManagementService(IServiceContainer container, boolean autoshutdown)
	{
    	this(container, autoshutdown, null);
	}
	
    /**
     *  Create a new component execution service.#
     *  @param container	The service container.
     */
    public ComponentManagementService(IServiceContainer container, boolean autoshutdown, IComponentAdapter root)
	{
		this.container = container;
		this.autoshutdown = autoshutdown;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.descs = Collections.synchronizedMap(SCollection.createLinkedHashMap());
		this.ccs = SCollection.createLinkedHashMap();
//		this.children	= SCollection.createMultiCollection();
		this.logger = Logger.getLogger(container.getName()+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.killresultlisteners = Collections.synchronizedMap(SCollection.createHashMap());
		
		this.root = root;
    }
    
    //-------- IComponentManagementService interface --------

	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param killlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture createComponent(final String name, final String model, CreationInfo info, final IResultListener killlistener)
	{
		final Future ret = new Future();
		
		final CreationInfo cinfo = info!=null? info: new CreationInfo();	// Dummy default info, if null.
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			ret.setException(new RuntimeException("No '@' allowed in component name."));
			return ret;
		}

		/*
		if(container.isShuttingDown())
		{
			listener.exceptionOccurred(new RuntimeException("No new components may be created when platform is shutting down."));
			return;
			//throw new RuntimeException("No new components may be created when platform is shutting down.");
		}
		*/
			
		// Load the model with fitting factory.
		container.getService(ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				container.getServices(IComponentFactory.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						Collection facts = (Collection)result;
						IComponentFactory factory = null;
						String	type	= null;
						if(facts!=null)
						{
							for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
							{
								IComponentFactory	cf	= (IComponentFactory)it.next();
								if(cf.isLoadable(model, cinfo.getImports(), ls.getClassLoader()))
								{
									factory	= cf;
									type	= factory.getComponentType(model, cinfo.getImports(), ls.getClassLoader());
								}
							}
						}
						
						if(factory==null)
							throw new RuntimeException("No factory found for component: "+model);
						final ILoadableComponentModel lmodel = factory.loadModel(model, cinfo.getImports(), ls.getClassLoader());
		
						// Create id and adapter.
						
						final ComponentIdentifier cid;
						final StandaloneComponentAdapter adapter;
						final CMSComponentDescription ad;
						StandaloneComponentAdapter pad	= null;
						synchronized(adapters)
						{
							synchronized(descs)
							{
								if(name==null)
								{
									cid = (ComponentIdentifier)generateComponentIdentifier(lmodel.getName());
								}
								else
								{
									cid = new ComponentIdentifier(name+"@"+container.getName()); // Hack?!
									if(adapters.containsKey(cid))
									{
										ret.setException(new RuntimeException("Component name already exists on platform: "+cid));
										return;
									}
									// todo: hmm adresses may be set too late? use cached message service?
									container.getService(IMessageService.class).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											IMessageService	ms	= (IMessageService)result;
											if(ms!=null)
												cid.setAddresses(ms.getAddresses());
										}
									});
								}
						
								ad	= new CMSComponentDescription(cid, type, getParent(cinfo), cinfo.isMaster(), cinfo.isDaemon());
//								System.out.println("created: "+ad);
								
								// Increase daemon cnt
								if(cinfo.isDaemon())
									daemons++;
								
								CMSComponentDescription padesc = (CMSComponentDescription)descs.get(getParent(cinfo));
								
								// Suspend when set to suspend or when parent is also suspended or when specified in model.
								Object	debugging 	= lmodel.getProperties().get("debugging");
								if(cinfo.isSuspend() || (padesc!=null && (IComponentDescription.STATE_SUSPENDED.equals(padesc.getState()) || IComponentDescription.STATE_WAITING.equals(padesc.getState())))
									|| debugging instanceof Boolean && ((Boolean)debugging).booleanValue())
								{
									ad.setState(IComponentDescription.STATE_SUSPENDED);
								}
								else
								{
									ad.setState(IComponentDescription.STATE_ACTIVE);
								}
								descs.put(cid, ad);
//								if(getParent(cinfo)!=null)
								{
		//							children.put(parent, cid);
									padesc.addChild(cid);
								}
							}
		
							adapter = new StandaloneComponentAdapter(ad);
							adapters.put(cid, adapter);
		
//							if(cinfo.getParent()!=null)
							{
								pad	= (StandaloneComponentAdapter)adapters.get(getParent(cinfo));
							}
						}
		
						if(pad!=null)
						{
		//					final IResultListener	rl	= listener;
							final IComponentFactory	cf	= factory;
							final StandaloneComponentAdapter	fpad	= pad;
							pad.getComponentInstance().getExternalAccess().addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									createComponentInstance(cinfo.getConfiguration(), cinfo.getArguments(), cinfo.isSuspend(), ret,
										killlistener, cf, lmodel, cid, adapter, fpad, ad, (IExternalAccess)result);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									ret.setException(exception);
								}
							});
						}
						else
						{
							createComponentInstance(cinfo.getConfiguration(), cinfo.getArguments(), cinfo.isSuspend(), ret,
								killlistener, factory, lmodel, cid, adapter, null, ad, null);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Create an instance of a component (step 2 of creation process).
	 */
	protected void createComponentInstance(String config, Map args,
		boolean suspend, Future ret,
		final IResultListener killlistener, IComponentFactory factory,
		ILoadableComponentModel lmodel, final ComponentIdentifier cid,
		StandaloneComponentAdapter adapter, StandaloneComponentAdapter pad,
		CMSComponentDescription ad, IExternalAccess parent)
	{
		// Create the component instance.
		try
		{
			IComponentInstance instance = factory.createComponentInstance(adapter, lmodel, config, args, parent);
			adapter.setComponent(instance, lmodel);
			
	//		System.out.println("added: "+descs.size()+", "+aid);
			
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
			
			if(killlistener!=null)
				killresultlisteners.put(cid, killlistener);
			
			ret.setResult(cid.clone());
			
			if(!suspend)
			{
				adapter.wakeup();			
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public IFuture destroyComponent(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Kill subcomponents
				final CMSComponentDescription	desc = (CMSComponentDescription)descs.get(cid);
				if(desc==null)
				{
					ret.setException(new RuntimeException("Component "+cid+" does not exist."));
					return ret;
				}
				IComponentIdentifier[] achildren = desc.getChildren();
				
				destroyComponentLoop(cid, achildren, 0).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						System.out.println("killing: "+cid);
						
						StandaloneComponentAdapter component = (StandaloneComponentAdapter)adapters.get(cid);
						if(component==null)
						{
							ret.setException(new RuntimeException("Component "+cid+" does not exist."));
							return;
						}
						
						// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
						// todo: killcomponent should only be called once for each component?
						if(desc!=null)
						{
							if(!ccs.containsKey(cid))
							{
								CleanupCommand	cc	= new CleanupCommand(cid);
								ccs.put(cid, cc);
								cc.addKillFuture(ret);
								component.killComponent(cc);						
							}
							else
							{
								CleanupCommand	cc	= (CleanupCommand)ccs.get(cid);
								if(cc==null)
									ret.setException(new RuntimeException("No cleanup command for component "+cid+": "+desc.getState()));
								cc.addKillFuture(ret);
							}
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 *  Loop for destroying subcomponents.
	 */
	protected IFuture destroyComponentLoop(final IComponentIdentifier cid, final IComponentIdentifier[] achildren, final int i)
	{
		final Future ret = new Future();
		
		if(achildren.length>0)
		{
			destroyComponent(achildren[i]).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(i+1<achildren.length)
					{
						destroyComponentLoop(cid, achildren, i+1).addResultListener(new DelegationResultListener(ret));
					}
					else
					{
						ret.setResult(null);
					}
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture suspendComponent(IComponentIdentifier componentid)
	{
		final Future ret = new Future();
		
		CMSComponentDescription ad;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Suspend subcomponents
				CMSComponentDescription desc = (CMSComponentDescription)descs.get(componentid);
				IComponentIdentifier[] achildren = desc.getChildren();
//				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
				for(int i=0; i<achildren.length; i++)
				{
//					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
					if(IComponentDescription.STATE_ACTIVE.equals(((IComponentDescription)descs.get(achildren[i])).getState()))
					{
						suspendComponent(achildren[i]);	// todo: cascading resume with wait.
					}
				}

				final StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				ad = (CMSComponentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+componentid));
					return ret;
				}
				if(!IComponentDescription.STATE_ACTIVE.equals(ad.getState())
					/*&& !IComponentDescription.STATE_TERMINATING.equals(ad.getState())*/)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+componentid));
					return ret;
				}
				
				ad.setState(IComponentDescription.STATE_SUSPENDED);
				exeservice.cancel(adapter, new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
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
		
		return ret;
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture resumeComponent(IComponentIdentifier componentid)
	{
		Future ret = new Future();
		
		CMSComponentDescription ad;
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				// Resume subcomponents
				CMSComponentDescription desc = (CMSComponentDescription)descs.get(componentid);
				IComponentIdentifier[] achildren = desc.getChildren();
//				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
				for(int i=0; i<achildren.length; i++)
				{
//					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
					if(IComponentDescription.STATE_SUSPENDED.equals(((IComponentDescription)descs.get(achildren[i])).getState())
						|| IComponentDescription.STATE_WAITING.equals(((IComponentDescription)descs.get(achildren[i])).getState()))
					{
						resumeComponent(achildren[i]);	// todo: cascading resume with wait.
					}
				}

				StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				ad = (CMSComponentDescription)descs.get(componentid);
				if(adapter==null || ad==null)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+componentid));
					return ret;
				}
				if(!IComponentDescription.STATE_SUSPENDED.equals(ad.getState())
					&& !IComponentDescription.STATE_WAITING.equals(ad.getState()))
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+componentid));
					return ret;
				}
				
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
	
		ret.setResult(ad);
		return ret;
//		listener.resultAvailable(this, ad);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public IFuture stepComponent(IComponentIdentifier componentid)
	{
		final Future ret = new Future();
		
		synchronized(adapters)
		{
			synchronized(descs)
			{
				final StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(componentid);
				IComponentDescription cd = (IComponentDescription)descs.get(componentid);
				if(adapter==null || cd==null)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+componentid));
					return ret;
				}
				if(!IComponentDescription.STATE_SUSPENDED.equals(cd.getState()))
				{
					ret.setException(new RuntimeException("Only suspended components can be stepped: "+componentid+" "+cd.getState()));
					return ret;
				}
				
				adapter.doStep(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
				exeservice.execute(adapter);
			}
		}
		
		return ret;
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
		CMSComponentDescription ad;
		synchronized(descs)
		{
			ad = (CMSComponentDescription)descs.get(componentid);
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
		protected List killfutures;
		
		public CleanupCommand(IComponentIdentifier cid)
		{
//			System.out.println("CleanupCommand created");
			this.cid = cid;
		}
		
		public void resultAvailable(Object source, Object result)
		{
			System.out.println("CleanupCommand: "+result);
			IComponentDescription ad = (IComponentDescription)descs.get(cid);
			Map results = null;
			StandaloneComponentAdapter adapter;
			StandaloneComponentAdapter pad = null;
			CMSComponentDescription desc;
			boolean shutdown = false;
			synchronized(adapters)
			{
				synchronized(descs)
				{
//					System.out.println("CleanupCommand remove called for: "+cid);
					adapter = (StandaloneComponentAdapter)adapters.remove(cid);
					if(adapter==null)
						throw new RuntimeException("Component Identifier not registered: "+cid);
					
					results = adapter.getComponentInstance().getResults();
					
					desc = (CMSComponentDescription)descs.remove(cid);
					desc.setState(IComponentDescription.STATE_TERMINATED);
					if(desc.isDaemon())
						daemons--;
					if((autoshutdown && adapters.size()-daemons==0) || desc.isMaster())
						shutdown = true;
					
					ccs.remove(cid);
					
					// Deregister destroyed component at parent.
					if(desc.getParent()!=null)
					{
						// Stop execution of component. When root component services are already shutdowned.
						exeservice.cancel(adapter, null);
						
//						children.remove(desc.getParent(), desc.getName());
						CMSComponentDescription padesc = (CMSComponentDescription)descs.get(desc.getParent());
						if(padesc!=null)
							padesc.removeChild(desc.getName());
						pad	= (StandaloneComponentAdapter)adapters.get(desc.getParent());
					}
				}
			}
			
			// Must be executed out of sync block due to deadlocks
			// agent->cleanupcommand->space.componentRemoved (holds adapter mon -> needs space mone)
			// space executor->general loop->distributed percepts->(holds space mon -> needs adapter mon for getting external access)
			if(pad!=null)
			{
				pad.getComponentInstance().componentDestroyed(desc);
			}
			// else parent has just been killed.
			
//			// Deregister killed component at contexts.
//			IContextService	cs	= (IContextService)container.getService(IContextService.class);
//			if(cs!=null)
//			{
//				IContext[]	contexts	= cs.getContexts(cid);
//				for(int i=0; contexts!=null && i<contexts.length; i++)
//				{
//					((BaseContext)contexts[i]).componentDestroyed(cid);
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
					System.out.println("WARNING: Exception when removing component: "+ad+", "+e);
				}
			}
			
			IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
			if(reslis!=null)
			{
//				System.out.println("result: "+cid+" "+results);
				if(exceptions!=null && exceptions.containsKey(cid))
				{
					reslis.exceptionOccurred(cid, (Exception)exceptions.get(cid));
					exceptions.remove(cid);
				}
				else
				{
					reslis.resultAvailable(cid, results);
				}
			}
			
//			System.out.println("CleanupCommand end.");
			
//			if(killlisteners!=null)
//			{
//				for(int i=0; i<killlisteners.size(); i++)
//				{
//					((IResultListener)killlisteners.get(i)).resultAvailable(source, result);
//				}
//			}
			
			if(killfutures!=null)
			{
				for(int i=0; i<killfutures.size(); i++)
				{
					((Future)killfutures.get(i)).setResult(result);
				}
			}
			
			// Shudown platform when last (non-daemon) component was destroyed
//			if(shutdown)
//				container.shutdown();
		}
		
		public void exceptionOccurred(Object source, Exception exception)
		{
			resultAvailable(source, cid);
		}
		
		/**
		 *  Add a listener to be informed, when the component has terminated.
		 * @param listener
		 * /
		public void	addKillListener(IResultListener listener)
		{
			if(killlisteners==null)
				killlisteners	= new ArrayList();
			killlisteners.add(listener);
		}*/
		
		/**
		 *  Add a listener to be informed, when the component has terminated.
		 * @param listener
		 */
		public void	addKillFuture(Future killfuture)
		{
			if(killfutures==null)
				killfutures = new ArrayList();
			killfutures.add(killfuture);
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
	public IFuture getExternalAccess(IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		StandaloneComponentAdapter adapter = (StandaloneComponentAdapter)adapters.get(cid);
		if(adapter==null)
		{
			ret.setException(new RuntimeException("No local component found for component identifier: "+cid));
		}
		else
		{
			adapter.getComponentInstance().getExternalAccess().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					ret.setResult(result);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParent(CreationInfo ci)
	{
		IComponentIdentifier rt = root.getComponentIdentifier();
		return ci==null? rt: ci.getParent()==null? rt: ci.getParent(); 
	}
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParent(IComponentIdentifier cid)
	{
		CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
		return desc!=null? desc.getParent(): null;
	}
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IComponentIdentifier[] getChildren(IComponentIdentifier cid)
	{
		CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
		return desc!=null? desc.getChildren(): null;
	}

	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name)
	{
		return createComponentIdentifier(name, true, null);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
	{
		return createComponentIdentifier(name, local, null);
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
		CMSComponentDescription	ret	= new CMSComponentDescription(id, type, parent, false, false);
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
	public Future getComponentDescription(IComponentIdentifier cid)
	{
		Future ret = new Future();
		
		IComponentDescription desc = (IComponentDescription)descs.get(cid);
		
		// Todo: addresses required for communication across platforms.
//		ret.setName(refreshComponentIdentifier(aid));
		if(desc!=null)
		{
			desc = (IComponentDescription)((CMSComponentDescription)desc).clone();	// Todo: synchronize?
			ret.setResult(desc);
		}
		else
		{
			ret.setException(new RuntimeException("No description available for: "+cid));
		}
		
		return ret;
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public Future getComponentDescriptions()
	{
		Future fut = new Future();
		
		IComponentDescription[] ret;
		synchronized(descs)
		{
			ret = new IComponentDescription[descs.size()];
			int i=0;
			for(Iterator it=descs.values().iterator(); i<ret.length; i++)
			{
				ret[i] = (IComponentDescription)((CMSComponentDescription)it.next()).clone();
			}
		}
		
		fut.setResult(ret);
		return fut;
	}
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large. <- TODO and the reason is...?
	 */
	public Future getComponentIdentifiers()
	{
		Future fut = new Future();
		
		IComponentIdentifier[] ret;
		
		synchronized(adapters)
		{
			ret = (IComponentIdentifier[])adapters.keySet().toArray(new IComponentIdentifier[adapters.size()]);
			// Todo: addresses required for inter-platform comm.
//			for(int i=0; i<ret.length; i++)
//				ret[i] = refreshComponentIdentifier(ret[i]); // Hack!
		}
		
		fut.setResult(ret);
		return fut;
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public Future searchComponents(IComponentDescription adesc, ISearchConstraints con)
	{
		Future fut = new Future();
		
//		System.out.println("search: "+components);
		CMSComponentDescription[] ret;

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			CMSComponentDescription ad = (CMSComponentDescription)descs.get(adesc.getName());
			if(ad!=null && ad.getName().equals(adesc.getName()))
			{
				// Todo: addresses reuqired for interplatform comm.
//				ad.setName(refreshComponentIdentifier(ad.getName()));
				CMSComponentDescription	desc	= (CMSComponentDescription)ad.clone();
				ret = new CMSComponentDescription[]{desc};
			}
			else
			{
				ret	= new CMSComponentDescription[0];
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
					CMSComponentDescription	test	= (CMSComponentDescription)it.next();
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
			ret	= (CMSComponentDescription[])tmp.toArray(new CMSComponentDescription[tmp.size()]);
		}

		//System.out.println("searched: "+ret);
		
		fut.setResult(ret);
		return fut;
	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier generateComponentIdentifier(String name)
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
		
		if(msgservice!=null)
			ret.setAddresses(msgservice.getAddresses());

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
		
		CMSComponentDescription	desc	= null;
		synchronized(descs)
		{
			desc	= (CMSComponentDescription)descs.get(comp);
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

	/**
	 *  Set the exception of a component.
	 *  @param comp	The component.
	 *  @param e	The exception.
	 */
	public void setComponentException(IComponentIdentifier comp, Exception e)
	{
		synchronized(descs)
		{
			if(exceptions==null)
				exceptions	= new HashMap();
			
			exceptions.put(comp, e);
		}
	}
	
	//-------- IService interface --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		
		// add root adapter and register root component
		if(root!=null)
		{
			synchronized(adapters)
			{
				synchronized(descs)
				{
					adapters.put(root.getComponentIdentifier(), root);
					IComponentDescription desc = createComponentDescription(root.getComponentIdentifier(), null, null, null, null);
					descs.put(root.getComponentIdentifier(), desc);
				}
			}
		}
		
		final boolean[]	services	= new boolean[2];
		container.getService(IExecutionService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				exeservice	= (IExecutionService)result;
				boolean	setresult;
				synchronized(services)
				{
					services[0]	= true;
					setresult	= services[0] && services[1];
				}
				if(setresult)
					ret.setResult(null);
			}
		});
		container.getService(IMessageService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				msgservice	= (IMessageService)result;
				boolean	setresult;
				synchronized(services)
				{
					services[1]	= true;
					setresult	= services[0] && services[1];
				}
				if(setresult)
					ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService()
	{
		return new Future(null);

		/*final Future ret = new Future();
		final  long shutdowntime = 10000; // todo: shutdowntime and MAX_SHUTDOWM_TIME
		
		// Step 1: Find existing components.
		getComponentDescriptions().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				// Step 2: Kill existing components excepts daemons.
				final List comps = new ArrayList(Arrays.asList((IComponentDescription[])result));
				for(int i=comps.size()-1; i>-1; i--)
				{
					if(((CMSComponentDescription)comps.get(i)).isDaemon())
						comps.remove(i);
				}
				
				killComponents(comps, shutdowntime, new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Step 3: Find remaining components.
						getComponentDescriptions().addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// Step 4: Kill remaining components.
								killComponents(Arrays.asList((IComponentDescription[])result), shutdowntime, new DelegationResultListener(ret));
							}

							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});		
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}

			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;*/
	}
	
	/**
	 *  Kill the given components within the specified timeout.
	 *  @param comps	The component ids.
	 *  @param timeout	The time after which to inform the listener anyways.
	 *  @param listener	The result listener.
	 */
	protected void killComponents(final List comps, final long timeout, final IResultListener listener)
	{
		if(comps.isEmpty())
			listener.resultAvailable(this, null);
		System.out.println("killcomps: "+comps);
		
		container.getService(IClockService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				// Timer entry to notify lister after timeout.
				final	boolean	notified[]	= new boolean[1];
				final ITimer killtimer	= ((IClockService)result).createTimer(timeout, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						boolean	notify	= false;
						synchronized(notified)
						{
							if(!notified[0])
							{
								notify	= true;
								notified[0]	= true;
							}
						}
						if(notify)
						{
							listener.resultAvailable(this, null);
						}
					}
				});
				
				// Kill the given components.
				final IResultListener	rl	= new IResultListener()
				{
					int cnt	= 0;
					public void resultAvailable(Object source, Object result)
					{
						testFinished();
					}
					public void exceptionOccurred(Object source, Exception exception)
					{
						testFinished();
					}
					protected synchronized void testFinished()
					{
						cnt++;
//						System.out.println("here: "+cnt+" "+comps.size());
						if(cnt==comps.size())
						{
							killtimer.cancel();
							boolean	notify	= false;
							synchronized(notified)
							{
								if(!notified[0])
								{
									notify	= true;
									notified[0]	= true;
								}
							}
							if(notify)
							{
								listener.resultAvailable(this, null);
							}
						}
					}
				};
				
				container.getService(IComponentManagementService.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentManagementService	ces	= (IComponentManagementService)result;
						for(int i=0; i < comps.size(); i++)
						{
							System.out.println("Killing component: "+comps.get(i));
							CMSComponentDescription desc = (CMSComponentDescription)comps.get(i);
							ces.destroyComponent(desc.getName()).addResultListener(rl);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						listener.exceptionOccurred(source, exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				listener.exceptionOccurred(source, exception);
			}
		});
	}
	
	//-------- service handling --------
	
	/**
	 *  Get a component service of a specific type.
	 *  @param type The type.
	 *  @return The service object. 
	 * /
	public IFuture getComponentService(Class type)
	{
		Future ret = new Future();
		Object service = services.get(type);
		
		ret.setResult(service);
		return ret;
	}*/
}
