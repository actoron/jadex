package jadex.base.service.cms;

import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.SearchConstraints;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IModelInfo;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.execution.IExecutionService;
import jadex.commons.service.library.ILibraryService;

import java.util.ArrayList;
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
public abstract class ComponentManagementService extends BasicService implements IComponentManagementService
{
	//-------- constants --------

	/** The component counter. Used for generating unique component ids. */
	public static int compcnt = 0;

	//-------- attributes --------

	/** The service provider. */
	protected IServiceProvider provider;

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
	
	/** The exception of a component during execution (if any). */
	protected Map exceptions;
	
	/** The execution service (cached to avoid using futures). */
	protected IExecutionService	exeservice;
	
	/** The message service (cached to avoid using futures). */
	protected IMessageService	msgservice;
	
	/** The root component. */
	protected IComponentAdapter root;
	
//	/** The map of initing futures of components (created but not yet visible). */
//	protected Map initfutures;
	
	/** The init adapters and descriptions, i.e. adapters and desc of initing components, 
	 *  are only visible for child components in their init. */
	protected Map initinfos;
	
	/** Number of non-daemon children for each autoshutdown component (cid->Integer). */
	protected Map childcounts;
	
    //-------- constructors --------

	 /**
     *  Create a new component execution service.
     *  @param provider	The service provider.
     */
    public ComponentManagementService(IServiceProvider provider)
	{
    	this(provider, null);
	}
	
    /**
     *  Create a new component execution service.
     *  @param provider	The service provider.
     */
    public ComponentManagementService(IServiceProvider provider, IComponentAdapter root)
	{
		super(provider.getId(), IComponentManagementService.class, null);

		this.provider = provider;
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.descs = Collections.synchronizedMap(SCollection.createLinkedHashMap());
		this.ccs = SCollection.createLinkedHashMap();
//		this.children	= SCollection.createMultiCollection();
		this.logger = Logger.getLogger(provider.getId()+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.killresultlisteners = Collections.synchronizedMap(SCollection.createHashMap());
//		this.initfutures = Collections.synchronizedMap(SCollection.createHashMap());
		this.initinfos = Collections.synchronizedMap(SCollection.createHashMap());
		this.childcounts = SCollection.createHashMap();
		
		this.root = root;
    }
    
	/**
	 *  Get the component instance from an adapter.
	 */
	public abstract IComponentInstance getComponentInstance(IComponentAdapter adapter);

	/**
	 *  Get the component adapter factory.
	 */
	public abstract IComponentAdapterFactory getComponentAdapterFactory();
	
	/**
	 *  Invoke kill on adapter.
	 */
	public abstract IFuture killComponent(IComponentAdapter adapter);
	
	/**
	 *  Cancel the execution.
	 */
	public abstract IFuture cancel(IComponentAdapter adapter);

	/**
	 *  Do a step.
	 */
	public abstract IFuture doStep(IComponentAdapter adapter);
	
	/**
	 *  Get the component description.
	 */
	public abstract IComponentDescription getDescription(IComponentAdapter adapter);
	
    
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
		final Future inited = new Future();
		
		final CreationInfo cinfo = info!=null? info: new CreationInfo();	// Dummy default info, if null.
		
//		System.out.println("create start1: "+model+" "+cinfo.getParent());
		
		if(name!=null && name.indexOf('@')!=-1)
		{
			inited.setException(new RuntimeException("No '@' allowed in component name."));
			return inited;
		}

		// Load the model with fitting factory.
		SServiceProvider.getService(provider, ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(provider, new ComponentFactorySelector(model, cinfo.getImports(), ls.getClassLoader()))
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
//						System.out.println("create start2: "+model+" "+cinfo.getParent());
						
						final IComponentFactory factory = (IComponentFactory)result;
						if(factory==null)
						{
//							throw new RuntimeException("No factory found for component: "+model);
							inited.setException(new RuntimeException("No factory found for component: "+model));
							return;
						}
						final IModelInfo lmodel = factory.loadModel(model, cinfo.getImports(), ls.getClassLoader());
						final String type = factory.getComponentType(model, cinfo.getImports(), ls.getClassLoader());
		
						// Create id and adapter.
						
						final ComponentIdentifier cid;
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
									cid = new ComponentIdentifier(name+"@"+((IComponentIdentifier)provider.getId()).getPlatformName()); // Hack?!
									if(adapters.containsKey(cid) || initinfos.containsKey(cid))
									{
										inited.setException(new RuntimeException("Component name already exists on platform: "+cid));
										return;
									}
									// todo: hmm adresses may be set too late? use cached message service?
									SServiceProvider.getService(provider, IMessageService.class).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											IMessageService	ms	= (IMessageService)result;
											if(ms!=null)
												cid.setAddresses(ms.getAddresses());
										}
									});
								}
//								System.out.println("create start3: "+model+" "+cinfo.getParent());
							}		
						}
						
						final IComponentAdapter pad = getParentAdapter(cinfo);
						IExternalAccess parent = getComponentInstance(pad).getExternalAccess();
						final CMSComponentDescription ad = new CMSComponentDescription(cid, type, 
							getParentIdentifier(cinfo), cinfo.isMaster(), cinfo.isDaemon(), cinfo.isAutoShutdown(), lmodel.getFullName());
						
						Future future = new Future();
						future.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// Create the component instance.
								IComponentAdapter adapter;
								ICMSComponentListener[] alisteners;
								
								synchronized(adapters)
								{
									synchronized(descs)
									{
//										System.out.println("created: "+ad);
										
										if(isInitSuspend(cinfo, lmodel))
										{
											ad.setState(IComponentDescription.STATE_SUSPENDED);
										}
										else
										{
											ad.setState(IComponentDescription.STATE_ACTIVE);
										}
										
										// Init successfully finished. Add description and adapter.
										adapter = (IComponentAdapter)((Object[])result)[1];
										descs.put(cid, ad);
//										System.out.println("adding cid: "+cid);
										adapters.put(cid, adapter);
										initinfos.remove(cid);
										
										CMSComponentDescription padesc;
										Object[] painfo = getParentInfo(cinfo);
										if(painfo!=null)
										{
											padesc = (CMSComponentDescription)painfo[0];
										}
										else
										{
											padesc = (CMSComponentDescription)descs.get(getParentIdentifier(cinfo));
										}
										padesc.addChild(cid);
										
										if(padesc.isAutoShutdown() && !ad.isDaemon())
										{
											Integer	childcount	= (Integer)childcounts.get(padesc.getName());
											childcounts.put(padesc.getName(), new Integer(childcount!=null ? childcount.intValue()+1 : 1));
										}
									}
								}
								getComponentInstance(pad).componentCreated(ad, lmodel);
								
								// Register component at parent.
								
								// todo: can be called after listener has (concurrently) deregistered
								// notify listeners without holding locks
								synchronized(listeners)
								{
									Set	slisteners	= new HashSet(listeners.getCollection(null));
									slisteners.addAll(listeners.getCollection(cid));
									alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
								}
								for(int i=0; i<alisteners.length; i++)
								{
									try
									{
										alisteners[i].componentAdded(ad);
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
										
//										System.out.println("created: "+cid.getLocalName()+" "+(parent!=null?parent.getComponentIdentifier().getLocalName():"null"));
//										System.out.println("added: "+descs.size()+", "+aid);
								
								if(killlistener!=null)
									killresultlisteners.put(cid, killlistener);
								
								inited.setResult(cid);
								
								// Start regular execution of inited component.
								if(!cinfo.isSuspend())
								{
									try
									{
//										System.out.println("cid wakeup: "+cid);
										adapter.wakeup();
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
//								e.printStackTrace();
//								System.out.println("Ex: "+cid+" "+exception);
								
								CleanupCommand	cc	= null;
								synchronized(adapters)
								{
									synchronized(descs)
									{
										adapters.remove(cid);
										descs.remove(cid);
										initinfos.remove(cid);		
										if(exceptions!=null)
											exceptions.remove(cid);
										cc	= (CleanupCommand)ccs.remove(cid);										
									}
								}
								
								IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
								if(reslis!=null)
								{
									reslis.exceptionOccurred(cid, exception);
								}
								
								if(cc!=null && cc.killfutures!=null)
								{
									for(int i=0; i<cc.killfutures.size(); i++)
									{
										((Future)cc.killfutures.get(i)).setException(exception);
									}
								}
								
								inited.setException(exception);
							}
						});
						
						// Create component and wakeup for init.
						// Use first configuration if no config specified.
						String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
							: lmodel.getConfigurations().length>0 ? lmodel.getConfigurations()[0] : null;
						Object[] comp = factory.createComponentInstance(ad, getComponentAdapterFactory(), lmodel, 
							config, cinfo.getArguments(), parent, future);
						
						// Store (invalid) desc, adapter and info for children
						synchronized(adapters)
						{
							synchronized(descs)
							{
								// 0: description, 1: adapter, 2: creation info, 3: model, 4: initfuture
								initinfos.put(cid, new Object[]{ad, comp[1], cinfo, lmodel, future});
							}
						}
						
						// Start the init procedure by waking up the adapter.
						try
						{
							((IComponentAdapter)comp[1]).wakeup();
						}
						catch(Exception e)
						{
							inited.setException(e);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						inited.setException(exception);
					}
				});
			}
		});
		
		return inited;
	}

	/**
	 *  Get the adapter of the parent component.
	 */
	protected Object[] getParentInfo(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		Object[] ret;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				ret = (Object[])initinfos.get(paid);
			}
		}
		return ret;
	}
	
	/**
	 *  Get the adapter of the parent component.
	 */
	protected IComponentAdapter getParentAdapter(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		IComponentAdapter adapter;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				adapter = (IComponentAdapter)adapters.get(paid);
				if(adapter==null)
				{
					adapter = (IComponentAdapter)getParentInfo(cinfo)[1];
				}
			}
		}
		return adapter;
	}
	
	/**
	 *  Get the desc of the parent component.
	 */
	protected CMSComponentDescription getParentDescription(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		CMSComponentDescription desc;
		synchronized(adapters)
		{
			synchronized(descs)
			{
				desc = (CMSComponentDescription)descs.get(paid);
				if(desc==null)
				{
					desc = (CMSComponentDescription)getParentInfo(cinfo)[0];
				}
			}
		}
		return desc;
	}
	
	/**
	 *  Get the adapter of the parent component.
	 * /
	protected Future getParentAdapter(CreationInfo cinfo)
	{
		final Future ret = new Future();
		
		StandaloneComponentAdapter adapter;
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		synchronized(adapters)
		{
			synchronized(descs)
			{
				adapter = (StandaloneComponentAdapter)adapters.get(paid);
			}
		}
		if(adapter!=null)
		{
			ret.setResult(adapter);
		}
		else
		{
			synchronized(adapters)
			{
				synchronized(descs)
				{
					Future inited = (Future)initfutures.get(paid);
					inited.addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							StandaloneComponentAdapter adapter;
							synchronized(adapters)
							{
								synchronized(descs)
								{
									adapter = (StandaloneComponentAdapter)adapters.get(paid);
								}
							}
							ret.setResult(adapter);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Test if a component identifier is a remote component.
	 */
	protected boolean isRemoteComponent(IComponentIdentifier cid)
	{
		return !cid.getPlatformName().equals(root.getComponentIdentifier().getName());
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public IFuture destroyComponent(final IComponentIdentifier cid)
	{
//		System.out.println("destroy: "+cid.getName());
		
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IComponentManagementService rcms = (IComponentManagementService)result;
							rcms.destroyComponent(cid).addResultListener(new DelegationResultListener(ret));
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
		else
		{
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
							IComponentAdapter component = (IComponentAdapter)adapters.get(cid);
							// Component may be already killed (e.g. when autoshutdown).
							if(component!=null)
							{
		//						System.out.println("killing: "+cid+" "+component.getParent().getComponentIdentifier().getLocalName());
								
								// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
								// todo: killcomponent should only be called once for each component?
								if(!ccs.containsKey(cid))
								{
		//								System.out.println("killing a: "+cid);
									
									CleanupCommand	cc	= new CleanupCommand(cid);
									ccs.put(cid, cc);
									cc.addKillFuture(ret);
									killComponent(component).addResultListener(cc);
//									component.killComponent(cc);	
								}
								else
								{
		//								System.out.println("killing b: "+cid);
									
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
	 *  @param cid The component identifier.
	 */
	public IFuture suspendComponent(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IComponentManagementService rcms = (IComponentManagementService)result;
							rcms.suspendComponent(cid).addResultListener(new DelegationResultListener(ret));
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
		else
		{
			CMSComponentDescription ad;
			synchronized(adapters)
			{
				synchronized(descs)
				{
					// Suspend subcomponents
					CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
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
	
					final IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
					ad = (CMSComponentDescription)descs.get(cid);
					if(adapter==null || ad==null)
					{
						ret.setException(new RuntimeException("Component identifier not registered: "+cid));
						return ret;
					}
					if(!IComponentDescription.STATE_ACTIVE.equals(ad.getState())
						/*&& !IComponentDescription.STATE_TERMINATING.equals(ad.getState())*/)
					{
						ret.setException(new RuntimeException("Component identifier not registered: "+cid));
						return ret;
					}
					
					ad.setState(IComponentDescription.STATE_SUSPENDED);
					cancel(adapter).addResultListener(new DelegationResultListener(ret));
//					exeservice.cancel(adapter).addResultListener(new DelegationResultListener(ret));
				}
			}
			
			ICMSComponentListener[]	alisteners;
			synchronized(listeners)
			{
				Set	slisteners	= new HashSet(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
			}
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].componentChanged(ad);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture resumeComponent(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IComponentManagementService rcms = (IComponentManagementService)result;
							rcms.resumeComponent(cid).addResultListener(new DelegationResultListener(ret));
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
		else
		{
			CMSComponentDescription ad;
			boolean	changed	= false;
			synchronized(adapters)
			{
				synchronized(descs)
				{
					// Resume subcomponents
					CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
					IComponentIdentifier[] achildren = desc.getChildren();
	//				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
					for(int i=0; i<achildren.length; i++)
					{
	//					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
//						if(IComponentDescription.STATE_SUSPENDED.equals(((IComponentDescription)descs.get(achildren[i])).getState()))
//						{
							resumeComponent(achildren[i]);	// todo: cascading resume with wait.
//						}
					}
	
					IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
					ad = (CMSComponentDescription)descs.get(cid);
					if(adapter==null || ad==null)
					{
						ret.setException(new RuntimeException("Component identifier not registered: "+cid));
						return ret;
					}

					if(IComponentDescription.STATE_SUSPENDED.equals(ad.getState()))
					{
						ad.setState(IComponentDescription.STATE_ACTIVE);						
						adapter.wakeup();
						changed	= true;
					}
				}
			}
			
			if(changed)
			{
				ICMSComponentListener[]	alisteners;
				synchronized(listeners)
				{
					Set	slisteners	= new HashSet(listeners.getCollection(null));
					slisteners.addAll(listeners.getCollection(cid));
					alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
				}
				// todo: can be called after listener has (concurrently) deregistered
				for(int i=0; i<alisteners.length; i++)
				{
					alisteners[i].componentChanged(ad);
				}
			}
		
			ret.setResult(ad);
		}
		
		return ret;
//		listener.resultAvailable(this, ad);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public IFuture stepComponent(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IComponentManagementService rcms = (IComponentManagementService)result;
							rcms.stepComponent(cid).addResultListener(new DelegationResultListener(ret));
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
		else
		{
			synchronized(adapters)
			{
				synchronized(descs)
				{
					final IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
					IComponentDescription cd = (IComponentDescription)descs.get(cid);
					if(adapter==null || cd==null)
					{
						ret.setException(new RuntimeException("Component identifier not registered: "+cid));
						return ret;
					}
					if(!IComponentDescription.STATE_SUSPENDED.equals(cd.getState()))
					{
						ret.setException(new RuntimeException("Only suspended components can be stepped: "+cid+" "+cd.getState()));
						return ret;
					}
					
					doStep(adapter).addResultListener(new DelegationResultListener(ret));
//					adapter.doStep(new IResultListener()
//					{
//						public void resultAvailable(Object source, Object result)
//						{
//							ret.setResult(result);
//						}
//						
//						public void exceptionOccurred(Object source, Exception exception)
//						{
//							ret.setException(exception);
//						}
//					});
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param cid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture setComponentBreakpoints(final IComponentIdentifier cid, final String[] breakpoints)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							final IComponentManagementService rcms = (IComponentManagementService)result;
							rcms.setComponentBreakpoints(cid, breakpoints).addResultListener(new DelegationResultListener(ret));
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
		else
		{
			CMSComponentDescription ad;
			synchronized(descs)
			{
				ad = (CMSComponentDescription)descs.get(cid);
				ad.setBreakpoints(breakpoints);
			}
			
			ICMSComponentListener[]	alisteners;
			synchronized(listeners)
			{
				Set	slisteners	= new HashSet(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
			}
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].componentChanged(ad);
			}
			
			ret.setResult(null);
		}
		
		return ret;
	}

	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be added.
     */
    public void addComponentListener(IComponentIdentifier comp, ICMSComponentListener listener)
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
    public void removeComponentListener(IComponentIdentifier comp, ICMSComponentListener listener)
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
			boolean	killparent	= false;
			IComponentAdapter adapter = null;
			IComponentAdapter pad = null;
			CMSComponentDescription desc;
			Map results = null;
			synchronized(adapters)
			{
				synchronized(descs)
				{
//					System.out.println("CleanupCommand: "+result);
		//			boolean shutdown = false;
		
//					System.out.println("CleanupCommand remove called for: "+cid);
					adapter = (IComponentAdapter)adapters.remove(cid);
					if(adapter==null)
						throw new RuntimeException("Component Identifier not registered: "+cid);
//					System.out.println("removed adapter: "+adapter.getComponentIdentifier().getLocalName()+" "+cid+" "+adapters);
					
					results = getComponentInstance(adapter).getResults();
					
					desc = (CMSComponentDescription)descs.remove(cid);
					desc.setState(IComponentDescription.STATE_TERMINATED);
					
					ccs.remove(cid);
					
					// Deregister destroyed component at parent.
					if(desc.getParent()!=null)
					{
						// Stop execution of component. When root component services are already shutdowned.
						cancel(adapter);
//						exeservice.cancel(adapter);
						
						killparent	= desc.isMaster();
						CMSComponentDescription padesc = (CMSComponentDescription)descs.get(desc.getParent());
						if(padesc!=null)
						{
							padesc.removeChild(desc.getName());
							if(padesc.isAutoShutdown() && !desc.isDaemon())
							{
								Integer	childcount	= (Integer)childcounts.get(padesc.getName());
								assert childcount!=null && childcount.intValue()>0;
								killparent	= childcount==null || childcount.intValue()<=1;
								if(!killparent)
								{
									childcounts.put(padesc.getName(), new Integer(childcount.intValue()-1));
								}
							}
						}
						pad	= (IComponentAdapter)adapters.get(desc.getParent());
					}
				}
			}
			
			// Must be executed out of sync block due to deadlocks
			// agent->cleanupcommand->space.componentRemoved (holds adapter mon -> needs space mone)
			// space executor->general loop->distributed percepts->(holds space mon -> needs adapter mon for getting external access)
			if(pad!=null)
			{
				getComponentInstance(pad).componentDestroyed(desc);
			}
			// else parent has just been killed.
			
			ICMSComponentListener[] alisteners;
			synchronized(listeners)
			{
				Set	slisteners	= new HashSet(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
			}
			
			// todo: can be called after listener has (concurrently) deregistered
			for(int i=0; i<alisteners.length; i++)
			{
				try
				{
					alisteners[i].componentRemoved(desc, results);
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					System.out.println("WARNING: Exception when removing component: "+desc+", "+e);
				}
			}
			
			Exception	ex	= null;
			if(exceptions!=null && exceptions.containsKey(cid))
			{
				ex	= (Exception)exceptions.get(cid);
				exceptions.remove(cid);
			}
			IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
			if(reslis!=null)
			{
//				System.out.println("kill lis: "+cid+" "+results+" "+ex);
				if(ex!=null)
				{
					reslis.exceptionOccurred(cid, ex);
				}
				else
				{
					reslis.resultAvailable(cid, results);
				}
			}
			else if(ex!=null)
			{
				// Unhandled component exception
				// Todo: delegate printing to parent component (if any).
				adapter.getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
				ex.printStackTrace();
			}
			
//			System.out.println("CleanupCommand end.");
			
			if(killfutures!=null)
			{
				for(int i=0; i<killfutures.size(); i++)
				{
					((Future)killfutures.get(i)).setResult(result);
				}
			}
			
			// Kill parent is autoshutdown or child was master.
			if(pad!=null && killparent)
			{
				destroyComponent(pad.getComponentIdentifier());
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
		public void	addKillFuture(Future killfuture)
		{
			if(killfutures==null)
				killfutures = new ArrayList();
			killfutures.add(killfuture);
		}
	}
	
	//-------- internal methods --------
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public IFuture getExternalAccess(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		if(cid==null)
		{
			ret.setException(new IllegalArgumentException("Identifier is null."));
			return ret;
		}
		
		if(isRemoteComponent(cid))
		{
			SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
					
					rms.getExternalAccessProxy(cid).addResultListener(new IResultListener()
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
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		else
		{
			IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
			if(adapter==null)
			{
				ret.setException(new RuntimeException("No local component found for component identifier: "+cid));
			}
			else
			{
				try
				{
					ret.setResult(getComponentInstance(adapter).getExternalAccess());
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
		}
		
		return ret;
	}
	
	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParentIdentifier(CreationInfo ci)
	{
		IComponentIdentifier rt = root.getComponentIdentifier();
		IComponentIdentifier ret = ci==null? rt: ci.getParent()==null? rt: ci.getParent(); 
//		System.out.println("parent id: "+ret);
		return ret;
	}
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 */
	public IFuture getParent(IComponentIdentifier cid)
	{
		CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
		return new Future(desc!=null? desc.getParent(): null);
	}
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture getChildren(final IComponentIdentifier cid)
	{
//		System.out.println("getChildren: "+this+" "+isValid());
		final Future ret = new Future();
		CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
		IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
			IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
		ret.setResult(tmp);
		
		// Nice style to check for valid?
//		checkValid().addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
//				IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
//					IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
//				ret.setResult(tmp);
//			}
//			
//			public void exceptionOccurred(Object source, Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
		
		return ret;
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
		return createComponentIdentifier(name, true);
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
		return createComponentIdentifier(name, local, msgservice!=null ? msgservice.getAddresses() : null);		
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
			name = name + "@" + ((IComponentIdentifier)provider.getId()).getPlatformName(); // Hack?!
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
	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, String ownership, String type, IComponentIdentifier parent, String modelname)
	{
		CMSComponentDescription	ret	= new CMSComponentDescription(id, type, parent, false, false, false, modelname);
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
	public IFuture getComponentDescription(IComponentIdentifier cid)
	{
		Future ret = new Future();
		
		IComponentDescription desc;
		synchronized(descs)
		{
			desc = (IComponentDescription)descs.get(cid);
			
			// Todo: addresses required for communication across platforms.
	//		ret.setName(refreshComponentIdentifier(aid));
			if(desc!=null)
			{
				desc = (IComponentDescription)((CMSComponentDescription)desc).clone();
			}
		}
		
		if(desc!=null)
		{
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
	public IFuture getComponentDescriptions()
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
	public IFuture getComponentIdentifiers()
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
	public IFuture searchComponents(IComponentDescription adesc, ISearchConstraints con)
	{
		return searchComponents(adesc, con, false);
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture searchComponents(final IComponentDescription adesc, final ISearchConstraints con, boolean remote)
	{
		final Future fut = new Future();
		
//		System.out.println("search: "+components);
		final List ret = new ArrayList();

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			CMSComponentDescription ad = (CMSComponentDescription)descs.get(adesc.getName());
			if(ad!=null && ad.getName().equals(adesc.getName()))
			{
				// Todo: addresses reuqired for interplatform comm.
//				ad.setName(refreshComponentIdentifier(ad.getName()));
				CMSComponentDescription	desc	= (CMSComponentDescription)ad.clone();
				ret.add(desc);
			}
		}

		// Otherwise search for matching descriptions.
		else
		{
			synchronized(descs)
			{
				for(Iterator it=descs.values().iterator(); it.hasNext(); )
				{
					CMSComponentDescription	test	= (CMSComponentDescription)it.next();
					if(adesc==null ||
						(adesc.getOwnership()==null || adesc.getOwnership().equals(test.getOwnership()))
						&& (adesc.getParent()==null || adesc.getParent().equals(test.getParent()))
						&& (adesc.getType()==null || adesc.getType().equals(test.getType()))
						&& (adesc.getState()==null || adesc.getState().equals(test.getState()))
						&& (adesc.getProcessingState()==null || adesc.getProcessingState().equals(test.getProcessingState()))
						&& (adesc.getModelName()==null || adesc.getModelName().equals(test.getModelName())))					
					{
						ret.add(test);
					}
				}
			}
		}

		//System.out.println("searched: "+ret);
		
//		System.out.println("Started search: "+ret);
//		open.add(fut);
		if(remote)
		{
			SServiceProvider.getServices(provider, IComponentManagementService.class, true, true).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					Collection coll = (Collection)result;
//					System.out.println("cms: "+coll);
					// Ignore search failures of remote dfs
					CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							// Add all services of all remote dfs
							for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
							{
								IComponentDescription[] res = (IComponentDescription[])it.next();
								if(res!=null)
								{
									for(int i=0; i<res.length; i++)
									{
										ret.add(res[i]);
									}
								}
							}
//							open.remove(fut);
//							System.out.println("Federated search: "+ret);//+" "+open);
							fut.setResult(ret.toArray(new CMSComponentDescription[ret.size()]));
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
//							open.remove(fut);
							fut.setException(exception);
//								fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
						}
					});
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						IComponentManagementService remotecms = (IComponentManagementService)it.next();
						if(remotecms!=ComponentManagementService.this)
						{
							remotecms.searchComponents(adesc, con, false).addResultListener(lis);
						}
						else
						{
							lis.resultAvailable(null, null);
						}
					}
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
//					open.remove(fut);
					fut.setResult(ret.toArray(new CMSComponentDescription[ret.size()]));
				}
			});
		}
		else
		{
//			open.remove(fut);
//			System.out.println("Local search: "+ret+" "+open);
			fut.setResult(ret.toArray(new CMSComponentDescription[ret.size()]));
		}
		
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
				ret = new ComponentIdentifier(name+(compcnt++)+"@"+((IComponentIdentifier)provider.getId()).getPlatformName()); // Hack?!
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
	public void	setProcessingState(IComponentIdentifier comp, String state)
	{
		CMSComponentDescription	desc	= null;
		synchronized(descs)
		{
			desc	= (CMSComponentDescription)descs.get(comp);
			if(desc!=null)	// May be null during platform init. hack!!!
				desc.setProcessingState(state);			
		}
		
		if(desc!=null)
		{
			ICMSComponentListener[]	alisteners;
			synchronized(listeners)
			{
				Set	slisteners	= new HashSet(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(comp));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
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
					e.printStackTrace();
					System.out.println("WARNING: Exception when changing component state: "+desc+", "+e);
				}
			}
		}
	}
	
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	public void	setComponentState(IComponentIdentifier comp, String state)
	{
		assert IComponentDescription.STATE_SUSPENDED.equals(state) : "wrong state: "+comp+", "+state;
		
		CMSComponentDescription	desc	= null;
		synchronized(descs)
		{
			desc	= (CMSComponentDescription)descs.get(comp);
			desc.setState(state);			
		}
		
		ICMSComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(comp));
			alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
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

			Object[]	infos = (Object[])initinfos.get(comp);
			if(infos!=null)
			{
				((Future)infos[4]).setException(e);
			}
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
		
		super.startService().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final boolean[]	services = new boolean[2];
				
				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DefaultResultListener()
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
							ret.setResult(ComponentManagementService.this);
					}
				});
				
				SServiceProvider.getService(provider, IMessageService.class).addResultListener(new DefaultResultListener()
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
						
						// add root adapter and register root component
						if(root!=null)
						{
							if(msgservice!=null)
							{
								msgservice.signalStarted().addResultListener(new IResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										synchronized(adapters)
										{
											synchronized(descs)
											{
												// Hack?! Need to set transport addresses on root id.
												((ComponentIdentifier)root.getComponentIdentifier()).setAddresses(msgservice.getAddresses());
	//											System.out.println("root: "+SUtil.arrayToString(msgservice.getAddresses())+" "+root.getComponentIdentifier().hashCode());
												adapters.put(root.getComponentIdentifier(), root);
												
												IComponentDescription desc = getDescription(root);
	//											IComponentDescription desc = ((IComponentAdapter)root).getDescription(); 
												descs.put(root.getComponentIdentifier(), desc);
											}
										}
									}
									
									public void exceptionOccurred(Object source, Exception exception)
									{
									}
								});
							}
							else
							{
								synchronized(adapters)
								{
									synchronized(descs)
									{
										adapters.put(root.getComponentIdentifier(), root);
										IComponentDescription desc = getDescription(root);
										descs.put(root.getComponentIdentifier(), desc);
									}
								}
							}
						}
						
						if(setresult)
							ret.setResult(ComponentManagementService.this);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
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
//		System.out.println("shutdown: "+this);
		return super.shutdownService();

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
	 *  Test if a component should be suspended after init is done.
	 *  @param cinfo	The creation info.
	 *  @param lmodel	The model of the component.
	 *  @return	True, if the component should be suspended
	 */
	protected boolean isInitSuspend(CreationInfo cinfo, IModelInfo lmodel)
	{
		boolean pasuspend = false;
		Object[] painfo = getParentInfo(cinfo);
		
		// Parent also still in init.
		if(painfo!=null)
		{
			pasuspend	= isInitSuspend((CreationInfo)painfo[2], (IModelInfo)painfo[3]);
		}
		
		// Parent already running.
		else
		{
			CMSComponentDescription	padesc = (CMSComponentDescription)descs.get(getParentIdentifier(cinfo));
			pasuspend = IComponentDescription.STATE_SUSPENDED.equals(padesc.getState());
		}
		// Suspend when set to suspend or when parent is also suspended or when specified in model.
		Object	debugging 	= lmodel.getProperties().get("debugging");
		boolean	suspend	= cinfo.isSuspend() || pasuspend || debugging instanceof Boolean 
			&& ((Boolean)debugging).booleanValue();
		return suspend;
	}

	/**
	 *  Get the msgservice.
	 *  @return the msgservice.
	 */
	public IMessageService getMessageService()
	{
		return msgservice;
	}

	/**
	 *  Get the exeservice.
	 *  @return the exeservice.
	 */
	public IExecutionService getExecutionService()
	{
		return exeservice;
	}
	
	/**
	 *  Kill the given components within the specified timeout.
	 *  @param comps	The component ids.
	 *  @param timeout	The time after which to inform the listener anyways.
	 *  @param listener	The result listener.
	 * /
	protected void killComponents(final List comps, final long timeout, final IResultListener listener)
	{
		if(comps.isEmpty())
			listener.resultAvailable(this, null);
		System.out.println("killcomps: "+comps);
		
		SServiceProvider.getService(container, IClockService.class).addResultListener(new IResultListener()
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
				

				for(int i=0; i < comps.size(); i++)
				{
					System.out.println("Killing component: "+comps.get(i));
					CMSComponentDescription desc = (CMSComponentDescription)comps.get(i);
					destroyComponent(desc.getName()).addResultListener(rl);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				listener.exceptionOccurred(source, exception);
			}
		});
	}*/
	
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
