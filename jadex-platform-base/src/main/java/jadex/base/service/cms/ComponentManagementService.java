package jadex.base.service.cms;

import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.SearchConstraints;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.CreationInfo;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.ServiceNotFoundException;
import jadex.bridge.service.component.ComponentFactorySelector;
import jadex.bridge.service.execution.IExecutionService;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.RemoteDelegationResultListener;
import jadex.xml.annotation.XMLClassname;

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
 *  Abstract default implementation of component management service.
 */
public abstract class ComponentManagementService extends BasicService implements IComponentManagementService
{
	//-------- constants --------

	/** The component counter. Used for generating unique component ids. */
	public static int compcnt = 0;

	//-------- attributes --------

	/** The service provider. */
	protected IExternalAccess exta;

	/** The components (id->component adapter). */
	protected Map adapters;
	
	/** The cleanup commands for the components (component id -> cleanup command). */
	protected Map ccs;
	
	/** The cleanup futures for the components (component id -> cleanup future). */
	protected Map cfs;
	
	/** The children of a component (component id -> children ids). */
//	protected MultiCollection	children;
	
	/** The logger. */
	protected Logger logger;

	/** The listeners. */
	protected MultiCollection listeners;
	
	/** The result (kill listeners). */
	protected Map killresultlisteners;
	
//	/** The exception of a component during execution (if any). */
//	protected Map exceptions;
	
	/** The execution service (cached to avoid using futures). */
	protected IExecutionService	exeservice;
	
	/** The message service (cached to avoid using futures). */
	protected IMessageService	msgservice;
	
	/** The root component. */
	protected IComponentAdapter root;
	
//	/** The map of initing futures of components (created but not yet visible). */
//	protected Map initfutures;
	
	/** The init adapters and descriptions, i.e. adapters and desc of initing components, 
	 *  are only visible for the component and child components in their init. */
	protected Map initinfos;
	
	/** Number of non-daemon children for each autoshutdown component (cid->Integer). */
	protected Map childcounts;
	
	/**	The local filename cache (tuple(parent filename, child filename) -> local typename)*/
	protected Map	localtypes;
	
	/** The cached factories. */
	protected Collection factories;
	
	/** The bootstrap component factory. */
	protected IComponentFactory componentfactory;
	
	/** The default copy parameters flag for service calls. */
	protected boolean copy;
	
//	/** The classloader cache. */
//	protected Map classloadercache;
	
    //-------- constructors --------

	 /**
     *  Create a new component execution service.
     *  @param provider	The service provider.
     */
    public ComponentManagementService(IExternalAccess provider)
	{
    	this(provider, null);
	}
	
    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public ComponentManagementService(IExternalAccess exta, IComponentAdapter root)
	{
    	this(exta, root, null, true);
    }
    
    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public ComponentManagementService(IExternalAccess exta, IComponentAdapter root, 
    	IComponentFactory componentfactory, boolean copy)
	{
		super(exta.getServiceProvider().getId(), IComponentManagementService.class, null);

		this.exta = exta;
		this.root = root;
		this.componentfactory = componentfactory;
		this.copy = copy;
		
		this.adapters = Collections.synchronizedMap(SCollection.createHashMap());
		this.ccs = SCollection.createLinkedHashMap();
		this.cfs = SCollection.createLinkedHashMap();
//		this.children	= SCollection.createMultiCollection();
		this.logger = Logger.getLogger(exta.getModel().getFullName()+"."+exta.getServiceProvider().getId()+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.killresultlisteners = Collections.synchronizedMap(SCollection.createHashMap());
//		this.initfutures = Collections.synchronizedMap(SCollection.createHashMap());
		this.initinfos = Collections.synchronizedMap(SCollection.createHashMap());
		this.childcounts = SCollection.createHashMap();
		this.localtypes	= Collections.synchronizedMap(new LRU(100));
//		this.classloadercache = Collections.synchronizedMap(new LRU(1000));
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
	
    //-------- IComponentManagementService interface --------
    
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	public IFuture loadComponentModel(final String filename)
	{
		final Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("loadModel")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				
				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(filename, null, ls.getClassLoader()))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.loadModel(filename, null, ls.getClassLoader())
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
									ret.setResult(null);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}
				}));
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param killlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture createComponent(final String name, final String modelname, CreationInfo info, final IResultListener killlistener)
	{	
//		System.out.println("create component: "+modelname+" "+name);
		final Future inited = new Future();
		
		final CreationInfo cinfo = info!=null? info: new CreationInfo();	// Dummy default info, if null.
		
		if(cinfo.getParent()!=null && isRemoteComponent(cinfo.getParent()))
		{
			final IResultListener	rkilllis;
			if(killlistener!=null && !SServiceProvider.isRemoteReference(killlistener))//(killlistener instanceof IRemotable))
			{
				Future	kill	= new Future();
				rkilllis	= new RemoteDelegationResultListener(kill);
				kill.addResultListener(killlistener);
			}
			else
			{
				rkilllis	= killlistener;
			}
			
			getRemoteCMS(cinfo.getParent()).addResultListener(new DelegationResultListener(inited)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.createComponent(name, modelname, cinfo, rkilllis).addResultListener(new DelegationResultListener(inited));
				}
			});
		}
		else
		{
	//		System.out.println("create start1: "+model+" "+cinfo.getParent());
			
			if(name!=null && name.indexOf('@')!=-1)
			{
				inited.setException(new RuntimeException("No '@' allowed in component name."));
			}
			else
			{
				// Load the model with fitting factory.
				getClassLoader(cinfo).addResultListener(new DelegationResultListener(inited)
				{
					public void customResultAvailable(Object result)
					{
						final ClassLoader	cl = (ClassLoader)result;
						
						final String	model	= resolveFilename(modelname, cinfo, cl);
						
						getComponentFactory(model, cinfo, cl)
							.addResultListener(new DelegationResultListener(inited)
						{
							public void exceptionOccurred(Exception exception)
							{
								super.exceptionOccurred(exception);
							}
								
							public void customResultAvailable(Object result)
							{
								final IComponentFactory factory = (IComponentFactory)result;
							
								factory.loadModel(model, cinfo.getImports(), cl)
									.addResultListener(new DelegationResultListener(inited)
								{
									public void customResultAvailable(Object result)
									{
										final IModelInfo lmodel = (IModelInfo)result;
										if(lmodel.getReport()!=null)
										{
											inited.setException(new RuntimeException("Errors loading model: "+model+"\n"+lmodel.getReport().getErrorText()));
										}
										else
										{
											factory.getComponentType(model, cinfo.getImports(), cl)
												.addResultListener(new DelegationResultListener(inited)
											{
												public void customResultAvailable(Object result)
												{
													final String type = (String)result;
													
													// Create id and adapter.
													
													final ComponentIdentifier cid;
													
													final IComponentAdapter pad = getParentAdapter(cinfo);
													IExternalAccess parent = getComponentInstance(pad).getExternalAccess();
	
													synchronized(adapters)
													{
														IComponentIdentifier pacid = parent.getComponentIdentifier();
														String paname = pacid.getName().replace('@', '.');
														if(name!=null)
														{
															cid = new ComponentIdentifier(name+"@"+paname);
															if(adapters.containsKey(cid) || initinfos.containsKey(cid))
															{
																throw new RuntimeException("Component "+cid+" already exists.");
															}
															if(msgservice!=null)
															{
																cid.setAddresses(msgservice.getAddresses());
															}
														}
														else
														{
															cid = (ComponentIdentifier)generateComponentIdentifier(lmodel.getName(), paname);
														}
														initinfos.put(cid, new Object[0]);
													}
													
													Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
													Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
													Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
													final CMSComponentDescription ad = new CMSComponentDescription(cid, type, master, daemon, autosd, lmodel.getFullName(), cinfo.getLocalType());
													
													logger.info("Starting component: "+cid.getName());
			//										System.err.println("Pre-Init: "+cid);
													
													final Future future = new Future();
													future.addResultListener(new IResultListener()
													{
														public void resultAvailable(Object result)
														{
															logger.info("Started component: "+cid.getName());
			//												System.err.println("Post-Init: "+cid);
			
															// Create the component instance.
															final IComponentAdapter adapter;
															
															synchronized(adapters)
															{
//																System.out.println("created: "+ad);
																
																// Init successfully finished. Add description and adapter.
																adapter = (IComponentAdapter)((Object[])result)[1];
																
																// Init finished. Set to suspended until parent registration is finished.
																// not set to suspend to allow other initing sibling components invoking services
	//															ad.setState(IComponentDescription.STATE_SUSPENDED);
																
		//														System.out.println("adding cid: "+cid+" "+ad.getMaster()+" "+ad.getDaemon()+" "+ad.getAutoShutdown());
																adapters.put(cid, adapter);
																// Removed in resumeComponent()
	//																initinfos.remove(cid);
																
																CMSComponentDescription padesc;
																Object[] painfo = getParentInfo(cinfo);
																if(painfo!=null)
																{
																	padesc = (CMSComponentDescription)painfo[0];
																}
																else
																{
																	padesc = (CMSComponentDescription)getDescription(getParentIdentifier(cinfo));
																}
																padesc.addChild(cid);
																
																Boolean dae = ad.getDaemon();
		//														if(padesc.isAutoShutdown() && !ad.isDaemon())
		//														if(pas!=null && pas.booleanValue() && (dae==null || !dae.booleanValue()))
																// cannot check parent shutdown state because could be still uninited
																if(dae==null || !dae.booleanValue())
																{
																	Integer	childcount	= (Integer)childcounts.get(padesc.getName());
																	int cc = childcount!=null ? childcount.intValue()+1 : 1;
																	childcounts.put(padesc.getName(), new Integer(cc));
		//															System.out.println("childcount+:"+padesc.getName()+" "+cc);
																}
															}
															
															// Register component at parent.
															getComponentInstance(pad).componentCreated(ad, lmodel)
																.addResultListener(new IResultListener()
															{
																public void resultAvailable(Object result)
																{
			//														System.err.println("Registered at parent: "+cid);
																	
																	// Registration finished -> reactivate component.
	//																// Note: Must be set to suspended because otherwise
																	// any call to wakeup would immediately start executing the component.
	//																if(isInitSuspend(cinfo, lmodel))
	//																{
																		// not set to suspend to allow other initing sibling components invoking services
	//																	ad.setState(CMSComponentDescription.STATE_SUSPENDED);
	//																}
	//																else
	//																{
	//																	ad.setState(CMSComponentDescription.STATE_ACTIVE);
	//																}
																	
																	// todo: can be called after listener has (concurrently) deregistered
																	// notify listeners without holding locks
																	notifyListenersAdded(cid, ad);
																			
			//														System.out.println("created: "+cid.getLocalName()+" "+(parent!=null?parent.getComponentIdentifier().getLocalName():"null"));
			//														System.out.println("added: "+descs.size()+", "+aid);
																	
																	if(killlistener!=null)
																		killresultlisteners.put(cid, killlistener);
																	
																	inited.setResult(cid);
																	
																	Future	killfut;
																	synchronized(adapters)
																	{
																		killfut	= (Future)cfs.get(cid);
																		if(killfut!=null)
																		{
																			// Remove init infos otherwise done in resume()
																			List	cids	= new ArrayList();
																			cids.add(cid);
																			for(int i=0; i<cids.size(); i++)
																			{
																				initinfos.remove(cids.get(i));
																				CMSComponentDescription	desc	= (CMSComponentDescription)getDescription((IComponentIdentifier)cids.get(i));
																				if(desc!=null)
																				{
																					IComponentIdentifier[]	achildren	= desc.getChildren();
																					for(int j=0; j<achildren.length; j++)
																					{
																						cids.add(achildren[j]);
																					}
																				}
																			}
																		}
																	}
																	
																	if(killfut!=null)
																	{
																		destroyComponent(cid, killfut);
																	}
																	else
																	{
																		// Start regular execution of inited component
																		// when this component is the outermost component, i.e. with no parent
																		// or the parent is already running
																		if(cinfo.getParent()==null || initinfos.get(cinfo.getParent())==null)
																		{
		//																	System.err.println("start: "+cid);
																			resumeComponent(cid, true);
																		}
																	}
																}
																
																public void exceptionOccurred(Exception exception)
																{
																	exception.printStackTrace();
																}
															});								
														}
														
														public void exceptionOccurred(final Exception exception)
														{
															logger.info("Starting component failed: "+cid+", "+exception);
	//														exception.printStackTrace();
			//												System.out.println("Ex: "+cid+" "+exception);
															final Runnable	cleanup	= new Runnable()
															{
																public void run()
																{
																	synchronized(adapters)
																	{
																		adapters.remove(cid);
																		initinfos.remove(cid);		
																	}
																	
																	IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
																	if(reslis!=null)
																	{
																		reslis.exceptionOccurred(exception);
																	}
																	
																	exitDestroy(cid, ad, exception, null);
																	
																	inited.setException(exception);
																}
															};
															
															IComponentIdentifier[]	children	= ad.getChildren();
															if(children.length>0)
															{
																CounterResultListener	crl	= new CounterResultListener(children.length, true,
																	new IResultListener()
																	{
																		public void resultAvailable(Object result)
																		{
																			cleanup.run();
																		}
																		
																		public void exceptionOccurred(Exception exception)
																		{
																			cleanup.run();
																		}
																	}
																);
																
																for(int i=0; i<children.length; i++)
																{
																	destroyComponent(children[i]).addResultListener(crl);
																}
															}
															else
															{
																cleanup.run();									
															}
														}
													});
													
													// Create component and wakeup for init.
													// Use first configuration if no config specified.
													String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
														: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
													factory.createComponentInstance(ad, getComponentAdapterFactory(), lmodel, 
														config, cinfo.getArguments(), parent, cinfo.getRequiredServiceBindings(), copy, future).addResultListener(new DefaultResultListener()
													{
														public void resultAvailable(Object result)
														{
															Object[] comp = (Object[]) result;
															// Store (invalid) desc, adapter and info for children
															synchronized(adapters)
															{
																// 0: description, 1: adapter, 2: creation info, 3: model, 4: initfuture, 5: component instance
	//															System.out.println("infos: "+ad.getName());
																initinfos.put(cid, new Object[]{ad, comp[1], cinfo, lmodel, future, comp[0]});
															}
															
															// Start the init procedure by waking up the adapter.
															try
															{
																getComponentAdapterFactory().initialWakeup((IComponentAdapter)comp[1]);
															}
															catch(Exception e)
															{
																inited.setException(e);
															}
														}
													});
												}
											});
										}
									}
								});
							}
						});
					}
				});
			}
		}
		
		return inited;
	}
	
	/**
	 *  Find the file name and local component type name
	 *  for a component to be started.
	 */
	protected String	resolveFilename(String modelname, final CreationInfo cinfo, ClassLoader cl)
	{
		String	filename	= modelname;
		
		if(cinfo.getParent()!=null)
		{
			// Try to find file for local type.
			String	localtype	= modelname!=null ? modelname : cinfo.getLocalType();
			filename	= null;
			IComponentAdapter pad = getParentAdapter(cinfo);
			IExternalAccess parent = getComponentInstance(pad).getExternalAccess();
			final SubcomponentTypeInfo[] subcomps = parent.getModel().getSubcomponentTypes();
			for(int i=0; filename==null && i<subcomps.length; i++)
			{
				if(subcomps[i].getName().equals(localtype))
				{
					filename = subcomps[i].getFilename();
					cinfo.setLocalType(localtype);
				}
			}
			if(filename==null)
			{
				filename	= modelname;
			}
			
			// Try to find local type for file
			if(cinfo.getLocalType()==null && subcomps.length>0)
			{
				Tuple	key	= new Tuple(parent.getModel().getFullName(), filename);
				if(localtypes.containsKey(key))
				{
					cinfo.setLocalType((String)localtypes.get(key));
				}
				else
				{
					ResourceInfo	info	= SUtil.getResourceInfo0(filename, cl);
					if(info!=null)
					{
						for(int i=0; cinfo.getLocalType()==null && i<subcomps.length; i++)
						{
							ResourceInfo	info1	= SUtil.getResourceInfo0(subcomps[i].getFilename(), cl);
							if(info1!=null)
							{
								if(info.getFilename().equals(info1.getFilename()))
								{
									cinfo.setLocalType(subcomps[i].getName());
								}
								info1.cleanup();
							}
						}
						info.cleanup();
					}
					localtypes.put(key, cinfo.getLocalType());
	//				System.out.println("Local type: "+cinfo.getLocalType()+", "+pad.getComponentIdentifier());
				}
			}
		}
		
		return filename;
	}
	
	/**
	 *  Get a fitting component factory for a specific model.
	 *  Searches the cached factories for the one that fits
	 *  the model and returns it. Possibly reevaluates the
	 *  cache when no factory was found.
	 *  @param model The model file name.
	 *  @param cinfo The creaion info.
	 *  @param cl The classloader.
	 *  @return The component factory.
	 */
	protected IFuture getComponentFactory(final String model, 
		final CreationInfo cinfo, final ClassLoader cl)
	{
		final Future ret = new Future();
		
		boolean nofac = false;
		if(factories==null)
		{
			nofac = true;
		}
		else if(factories.size()==0)
		{
			factories = null;
		}
		
		if(nofac)
		{
			SServiceProvider.getServices(exta.getServiceProvider(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					factories = (Collection)result;
					getComponentFactory(model, cinfo, cl).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
//			System.out.println("create start2: "+model+" "+cinfo.getParent());
						
			selectComponentFactory(factories==null? null: (IComponentFactory[])factories.toArray(new IComponentFactory[factories.size()]), model, cinfo, cl, 0)
				.addResultListener(new DelegationResultListener(ret)
			{
//				public void customResultAvailable(Object result)
//				{
//					System.out.println("res: "+result);
//					super.customResultAvailable(result);
//				}
					
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("factory ex: "+exception);
					SServiceProvider.getServices(exta.getServiceProvider(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{	
							factories = (Collection)result;
							selectComponentFactory((IComponentFactory[])factories.toArray(new IComponentFactory[factories.size()]), model, cinfo, cl, 0)
								.addResultListener(new DelegationResultListener(ret));
						}
					});
				}
			});
		}
		return ret;
	}
	
	/**
	 *  Selects a component factory from a collection of factories.
	 *  Uses the isLoadable factory method to determine if the
	 *  model can be loaded.
	 *  @param factories The collection of factories.
	 *  @param model The model file name.
	 *  @param cinfo The creaion info.
	 *  @param cl The classloader.
	 *  @return The component factory.
	 */
	protected IFuture selectComponentFactory(final IComponentFactory[] factories, 
		final String model, final CreationInfo cinfo, final ClassLoader cl, final int idx)
	{
		final Future ret = new Future();
		
		if(factories!=null && factories.length>0)
		{
			factories[idx].isLoadable(model, cinfo.getImports(), cl)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
						ret.setResult(factories[idx]);
					}
					else if(idx+1<factories.length)
					{
						selectComponentFactory(factories, model, cinfo, cl, idx+1)
							.addResultListener(new DelegationResultListener(ret));
					}
					else
					{
						selectFallbackFactory(model, cinfo, cl).addResultListener(new DelegationResultListener(ret));
					}
				}		
			});
		}
		else
		{
			selectFallbackFactory(model, cinfo, cl).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture selectFallbackFactory(final String model, final CreationInfo cinfo, final ClassLoader cl)
	{
		final Future ret = new Future();
		
		if(componentfactory!=null)
		{
			componentfactory.isLoadable(model, cinfo.getImports(), cl)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
						ret.setResult(componentfactory);
					}
					else
					{
						ret.setException(new RuntimeException("No factory found for: "+model));
					}
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("No factory found for: "+model));
		}
		
		return ret;
	}

	/**
	 *  Get the info of the parent component.
	 */
	protected Object[] getParentInfo(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		Object[] ret;
		synchronized(adapters)
		{
			ret = getInitInfo(paid);
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
			adapter = (IComponentAdapter)adapters.get(paid);
			if(adapter==null)
			{
				adapter = (IComponentAdapter)getParentInfo(cinfo)[1];
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
			desc = adapters.containsKey(paid)
				? (CMSComponentDescription)((IComponentAdapter)adapters.get(paid)).getDescription()
				: (CMSComponentDescription)getParentInfo(cinfo)[0];
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
//		System.out.println("destroy component: "+cid);
		boolean contains = false;
		Future tmp;
		synchronized(adapters)
		{
			contains = cfs.containsKey(cid);
			tmp = contains? (Future)cfs.get(cid): new Future();
//			System.out.println("destroy0: "+cid+" "+cfs.containsKey(cid));
//			Thread.currentThread().dumpStack();
			
			if(!contains)
			{
				cfs.put(cid, tmp);
			}
		}
		final Future ret = tmp;
		
		if(!contains)
		{
			destroyComponent(cid, ret);
		}
		
		return ret;
	}

	/**
	 *  Internal destroy method that performs the actual work.
	 *	@param cid The component to destroy.
	 *  @param ret The future to be informed.
	 */
	protected void destroyComponent(final IComponentIdentifier cid,	final Future ret)
	{
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.destroyComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			IComponentAdapter ad;
			Object[] infos;
			synchronized(adapters)
			{
				ad = (IComponentAdapter)adapters.get(cid);
				infos = (Object[])initinfos.get(cid);
			}
			// Terminate component that is shut down during init.
			if(infos!=null && infos.length>0)
			{
				logger.info("Queued component termination during init: "+cid.getName());
			}
			// Terminate normally inited component.
			else 
			{
				final IComponentAdapter adapter = ad;
				
				// Kill subcomponents
				if(adapter==null)
				{
					// Todo: need to kill children!? How to reproduce this case!?
					logger.info("Terminating component structure adapter is null: "+cid.getName());
					exitDestroy(cid, null, new RuntimeException("Component "+cid+" does not exist."), null);
				}
				else
				{
					logger.info("Terminating component structure: "+cid.getName());
					final CMSComponentDescription	desc;
					IComponentIdentifier[] achildren;
					synchronized(adapters)
					{
						desc	= (CMSComponentDescription)adapter.getDescription();
						achildren = desc.getChildren();
					}
					
//						System.out.println("kill childs: "+cid+" "+SUtil.arrayToString(achildren));
					
					destroyComponentLoop(cid, achildren, achildren.length-1).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							logger.info("Terminated component structure: "+cid.getName());
							boolean	exit	= false;
							synchronized(adapters)
							{
								IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
								// Component may be already killed (e.g. when autoshutdown).
								if(adapter!=null)
								{
//											System.out.println("destroy1: "+cid);//+" "+component.getParent().getComponentIdentifier().getLocalName());
									
									// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
									// todo: killcomponent should only be called once for each component?
									if(!ccs.containsKey(cid))
									{
//										System.out.println("killing a: "+cid);
										
										CleanupCommand	cc	= new CleanupCommand(cid);
										ccs.put(cid, cc);
										logger.info("Terminating component: "+cid.getName());
										killComponent(adapter).addResultListener(cc);
	//									component.killComponent(cc);	
									}
									else
									{
//										System.out.println("killing b: "+cid);
										
										CleanupCommand cc = (CleanupCommand)ccs.get(cid);
										if(cc==null)
										{
											// Todo: what is this case?
											exit	= true;
										}
									}
								}
							}
							
							if(exit)
							{
								exitDestroy(cid, desc, new RuntimeException("No cleanup command for component "+cid+": "+desc.getState()), null);
							}
							else
							{
								// Resume component to be killed in case it is currently suspended.
								resumeComponent(cid);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("ex: "+exception);
							exitDestroy(cid, desc, exception, null);
						}
					});
				}
			}
		}
	}
	
	/**
	 *  Exit the destroy method by setting description state and resetting maps.
	 */
	protected void exitDestroy(IComponentIdentifier cid, IComponentDescription desc, Exception ex, Map results)
	{
//		Thread.dumpStack();
		Future	ret;
		synchronized(adapters)
		{
			if(desc instanceof CMSComponentDescription)
			{
				((CMSComponentDescription)desc).setState(IComponentDescription.STATE_TERMINATED);
			}
			ccs.remove(cid);
			ret	= (Future)cfs.remove(cid);
		}
		if(ret!=null)
		{
			if(ex!=null)
			{
				ret.setException(ex);
			}
			else
			{
				ret.setResult(results);
			}
		}
	}
	
	/**
	 *  Loop for destroying subcomponents.
	 */
	protected IFuture destroyComponentLoop(final IComponentIdentifier cid, final IComponentIdentifier[] achildren, final int i)
	{
		final Future ret = new Future();
		
		if(achildren.length>0)
		{
			final List exceptions = new ArrayList();
			destroyComponent(achildren[i]).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					if(i>0)
					{
						destroyComponentLoop(cid, achildren, i-1).addResultListener(new DelegationResultListener(ret));
					}
					else
					{
						ret.setResult(exceptions);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exceptions.add(exception);
					resultAvailable(null);
//					ret.setException(exception);
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
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.suspendComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			CMSComponentDescription desc;
			synchronized(adapters)
			{
				final IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
				if(adapter==null)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+cid));
					return ret;
				}
				
				// Suspend subcomponents
				desc = (CMSComponentDescription)adapter.getDescription();
				IComponentIdentifier[] achildren = desc.getChildren();
//				for(Iterator it=children.getCollection(componentid).iterator(); it.hasNext(); )
				for(int i=0; i<achildren.length; i++)
				{
//					IComponentIdentifier	child	= (IComponentIdentifier)it.next();
					IComponentDescription	cdesc	= getDescription(achildren[i]);
					if(IComponentDescription.STATE_ACTIVE.equals(cdesc.getState()))
					{
						suspendComponent(achildren[i]);	// todo: cascading suspend with wait.
					}
				}

				if(!IComponentDescription.STATE_ACTIVE.equals(desc.getState())
					/*&& !IComponentDescription.STATE_TERMINATING.equals(ad.getState())*/)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+cid));
					return ret;
				}
				
				desc.setState(IComponentDescription.STATE_SUSPENDED);
				cancel(adapter).addResultListener(new DelegationResultListener(ret));
//					exeservice.cancel(adapter).addResultListener(new DelegationResultListener(ret));
			}
			
			notifyListenersChanged(cid, desc);
		}
		
		return ret;
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture resumeComponent(IComponentIdentifier cid)
	{
		return resumeComponent(cid, false);
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture resumeComponent(final IComponentIdentifier cid, final boolean initresume)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			assert !initresume;
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.resumeComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			// Resume subcomponents
			final CMSComponentDescription desc;
			IComponentIdentifier[] achildren;
			synchronized(adapters)
			{
				desc = (CMSComponentDescription)getDescription(cid);
				achildren = desc!=null ? desc.getChildren() : null;
			}
			if(desc!=null)
			{
				CounterResultListener lis = new CounterResultListener(achildren.length, true, new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
						boolean	changed	= false;
						if(adapter==null && !initresume)	// Might be killed after init but before init resume
						{
							ret.setException(new RuntimeException("Component identifier not registered: "+cid));
						}
						else if(adapter!=null)
						{
							// Hack for startup.
							if(initresume)
							{
								boolean	wakeup	= false;
								IComponentInstance instance	= null;
								Future	destroy	= null;
								synchronized(adapters)
								{
									// Not killed during init.
									if(!cfs.containsKey(cid))
									{
										Object[] ii = (Object[])initinfos.remove(cid);
			//							System.out.println("removed: "+cid+" "+ii);
										if(ii!=null && ii.length>=6)
										{
											CreationInfo cinfo = (CreationInfo)ii[2];
											IModelInfo lmodel = (IModelInfo)ii[3];
											boolean	suspend = isInitSuspend(cinfo, lmodel);
											instance = (IComponentInstance)ii[5];
											if(suspend)
											{
												desc.setState(IComponentDescription.STATE_SUSPENDED);
												changed	= true;
											}
											wakeup	= !suspend;
										}
									}
									
									// Killed after init but before init resume -> execute queued destroy.
									else if(initinfos.containsKey(cid))
									{
										initinfos.remove(cid);
										destroy	= (Future)cfs.remove(cid);
									}									
								}
								
								if(instance!=null)
								{
									try
									{
										final IComponentInstance	ci	= instance;
										instance.getExternalAccess().scheduleImmediate(new IComponentStep()
										{
											public Object execute(IInternalAccess ia)
											{
												ci.startBehavior();
												return null;
											}
										});
									}
									catch(ComponentTerminatedException e)
									{
										// Ignore when killed in mean time.
									}
								}
								if(wakeup)
								{
									try
									{
										adapter.wakeup();
									}
									catch(ComponentTerminatedException e)
									{
										// Ignore when killed in mean time.
									}
								}
								if(destroy!=null)
								{
									destroyComponent(cid, destroy);
								}
							}
							else
							{
								boolean	wakeup	= false;
								synchronized(adapters)
								{
									if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
									{
										wakeup	= true;
										desc.setState(IComponentDescription.STATE_ACTIVE);
										changed	= true;
									}
								}
								if(wakeup)
								{
									adapter.wakeup();
								}
							}
							
							if(changed)
								notifyListenersChanged(cid, desc);
						
							ret.setResult(desc);
						}
					}
				});
				
				for(int i=0; i<achildren.length; i++)
				{
					resumeComponent(achildren[i], initresume).addResultListener(lis);
				}
			}
			else
			{
				ret.setResult(null);
			}
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
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.stepComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			synchronized(adapters)
			{
				final IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
				if(adapter==null)
				{
					ret.setException(new RuntimeException("Component identifier not registered: "+cid));
					return ret;
				}
				if(!IComponentDescription.STATE_SUSPENDED.equals(adapter.getDescription().getState()))
				{
					ret.setException(new RuntimeException("Only suspended components can be stepped: "+cid+" "+adapter.getDescription().getState()));
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
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.setComponentBreakpoints(cid, breakpoints).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			CMSComponentDescription ad;
			synchronized(adapters)
			{
				ad = (CMSComponentDescription)getDescription(cid);
				ad.setBreakpoints(breakpoints);
			}
			
			notifyListenersChanged(cid, ad);
			
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
		
		public CleanupCommand(IComponentIdentifier cid)
		{
//			System.out.println("CleanupCommand created");
			this.cid = cid;
		}
		
		public void resultAvailable(Object result)
		{
			doCleanup(null);
		}
		
		
		public void exceptionOccurred(Exception exception)
		{
			doCleanup(exception);
		}

		
		protected void doCleanup(Exception exception)
		{
			boolean	killparent	= false;
			IComponentAdapter adapter = null;
			IComponentAdapter pad = null;
			CMSComponentDescription desc;
			Map results = null;
			synchronized(adapters)
			{
				logger.info("Terminated component: "+cid.getName());
//					System.out.println("CleanupCommand: "+cid);
	//			boolean shutdown = false;
	
//					System.out.println("CleanupCommand remove called for: "+cid);
				adapter = (IComponentAdapter)adapters.remove(cid);
				if(adapter==null)
					throw new RuntimeException("Component Identifier not registered: "+cid);
				
//				if(cid.getName().indexOf("Peer")==-1)
//					System.out.println("removed adapter: "+adapter.getComponentIdentifier().getLocalName()+" "+cid+" "+adapters);
				
				desc	= (CMSComponentDescription)adapter.getDescription();
				results = getComponentInstance(adapter).getResults();
				
//				desc.setState(IComponentDescription.STATE_TERMINATED);
//				ccs.remove(cid);
//				cfs.remove(cid);
				
				// Deregister destroyed component at parent.
				if(desc.getName().getParent()!=null)
				{
					// Stop execution of component. When root component services are already shutdowned.
					cancel(adapter);
//						exeservice.cancel(adapter);
					
					killparent = desc.getMaster()!=null && desc.getMaster().booleanValue();
					CMSComponentDescription padesc = (CMSComponentDescription)getDescription(desc.getName().getParent());
					if(padesc!=null)
					{
						padesc.removeChild(desc.getName());
						Boolean pas = padesc.getAutoShutdown();
						Boolean dae = desc.getDaemon();
//							if(pas!=null && pas.booleanValue() && (dae==null || !dae.booleanValue()))
						if(dae==null || !dae.booleanValue())
//							if(padesc.isAutoShutdown() && !desc.isDaemon())
						{
							Integer	childcount	= (Integer)childcounts.get(padesc.getName());
//								assert childcount!=null && childcount.intValue()>0;
							if(childcount!=null)
							{
								int cc = childcount.intValue()-1;
								if(cc>0)
									childcounts.put(padesc.getName(), new Integer(cc));
								else
									childcounts.remove(padesc.getName());
//									System.out.println("childcount-: "+padesc.getName()+" "+cc);
							}
							// todo: could fail when parent is still in init phase. 
							// Should test for init phase and remember that it has to be killed.
							killparent = killparent || (pas!=null && pas.booleanValue() 
								&& (childcount==null || childcount.intValue()<=1));
						}
					}
					pad	= (IComponentAdapter)adapters.get(desc.getName().getParent());
				}
			}
			
			// Must be executed out of sync block due to deadlocks
			// agent->cleanupcommand->space.componentRemoved (holds adapter mon -> needs space mone)
			// space executor->general loop->distributed percepts->(holds space mon -> needs adapter mon for getting external access)
			if(pad!=null)
			{
				try
				{
					getComponentInstance(pad).componentDestroyed(desc);
				}
				catch(ComponentTerminatedException cte)
				{
					// Parent just killed: ignore.
				}
			}
			// else parent has just been killed.
			
			exitDestroy(cid, desc, exception, results);

			notifyListenersRemoved(cid, desc, results);
			
			Exception	ex	= adapter.getException();
//			if(exceptions!=null && exceptions.containsKey(cid))
//			{
//				ex	= (Exception)exceptions.get(cid);
//				exceptions.remove(cid);
//			}
			IResultListener reslis = (IResultListener)killresultlisteners.remove(cid);
			if(reslis!=null)
			{
//				System.out.println("kill lis: "+cid+" "+results+" "+ex);
				if(ex!=null)
				{
					reslis.exceptionOccurred(ex);
				}
				else
				{
					reslis.resultAvailable(results);
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
			
			// Kill parent is autoshutdown or child was master.
			if(pad!=null && killparent)
			{
//				System.out.println("killparent: "+pad.getComponentIdentifier());
				destroyComponent(pad.getComponentIdentifier());
			}
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
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			IComponentAdapter adapter = null;
			synchronized(adapters)
			{
				adapter = (IComponentAdapter)adapters.get(cid);
				if(adapter==null)
				{
					// Hack? Allows components to getExternalAccess in init phase
					Object[] ii = getInitInfo(cid);
					if(ii!=null)
						adapter = (IComponentAdapter)ii[1];
				}
			}
			
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
	
	/**
	 *  Find the class loader for a new (local) component.
	 *  Use parent component class loader for local parents
	 *  and current platform class loader for remote or no parents.
	 *  @param cid	The component id.
	 *  @return	The class loader.
	 */
	protected IFuture getClassLoader(final CreationInfo ci)
	{
		final Future	ret	= new Future();
		
		// Local parent but not platform (does not work during init as external access is not available).
		if(ci!=null && ci.getParent()!=null
			&& !ci.getParent().equals(root.getComponentIdentifier())
			&& !isRemoteComponent(ci.getParent())
			&& !initinfos.containsKey(ci.getParent())
			&& !Boolean.TRUE.equals(ci.getPlatformloader()))
		{
//			ClassLoader cl = (ClassLoader)classloadercache.get(ci.getParent());
//			if(cl!=null)
//			{
//				ret.setResult(cl);
//			}
//			else
//			{
				SServiceProvider.getService(exta.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IComponentManagementService	cms	= (IComponentManagementService)result;
						cms.getExternalAccess(ci.getParent()).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess	ea	= (IExternalAccess)result;
	//							System.err.println("Model class loader: "+ea.getModel().getName()+", "+ea.getModel().getClassLoader());
//								classloadercache.put(ci.getParent(), ea.getModel().getClassLoader());
								ret.setResult(ea.getModel().getClassLoader());
							}
						});
					}
				});
//			}
		}
		
		// Remote or no parent or platform as parent
		else
		{
			SServiceProvider.getService(exta.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					ILibraryService	ls	= (ILibraryService)result;
//					System.err.println("Libservice class loader: "+ls.getClassLoader());
					ret.setResult(ls.getClassLoader());
				}
			});
		}
		return ret;
	}

	
	/**
	 *  Get the component adapter for a component identifier.
	 *  @param aid The component identifier.
	 *  @param listener The result listener.
	 */
    // Todo: Hack!!! remove?
	public IComponentAdapter getComponentAdapter(IComponentIdentifier cid)
	{
		IComponentAdapter ret;
		synchronized(adapters)
		{
			ret = (IComponentAdapter)adapters.get(cid);
			// Hack, to retrieve description from component itself in init phase
			if(ret==null)
			{
				Object[] ii= getInitInfo(cid);
				if(ii!=null && ii.length>0)
					ret	= (IComponentAdapter)ii[1];
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
	public IFuture getParent(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.getParent(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
			ret.setResult(desc!=null? desc.getName().getParent(): null);
		}
		return ret;
	}
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture getChildren(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.getChildren(cid).addResultListener(new DelegationResultListener(ret));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			});
		}
		else
		{
	//		System.out.println("getChildren: "+this+" "+isValid());
			synchronized(adapters)
			{
				CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
//				System.out.println("desc: "+desc.getName()+" "+desc.hashCode());
				IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
					IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
				ret.setResult(tmp);
//				System.out.println(getServiceIdentifier()+" "+desc.getName()+" "+SUtil.arrayToString(tmp));
			}
			
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
		}
		
		return ret;
	}

	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture getChildrenDescriptions(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.getChildrenDescriptions(cid).addResultListener(new DelegationResultListener(ret));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			});
		}
		else
		{
			synchronized(adapters)
			{
				CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
				IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
					IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
				IComponentDescription[]	descs	= new IComponentDescription[tmp.length];
				for(int i=0; i<descs.length; i++)
				{
					descs[i]	= (IComponentDescription)getDescription(tmp[i]);
					assert descs[i]!=null;
				}
				ret.setResult(descs);
			}
		}
		
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
			name = name + "@" + ((IComponentIdentifier)exta.getServiceProvider().getId()).getPlatformName(); // Hack?!
		return new ComponentIdentifier(name, addresses);		
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, IComponentIdentifier parent, String[] addresses)
	{
		String paname = parent.getName().replace('@', '.');
		return new ComponentIdentifier(name+"@"+paname, addresses);
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
	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, String ownership, String type, String modelname, String localtype)
	{
		CMSComponentDescription	ret	= new CMSComponentDescription(id, type, null, null, null, modelname, localtype);
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
	public IFuture getComponentDescription(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.getComponentDescription(cid).addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			IComponentDescription desc;
			synchronized(adapters)
			{
				desc = (IComponentDescription)getDescription(cid);

				// Hack, to retrieve description from component itself in init phase
				if(desc==null)
				{
					Object[] ii= getInitInfo(cid);
					if(ii!=null)
						desc	= (IComponentDescription) ii[0];
				}
				
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
		synchronized(adapters)
		{
			ret = new IComponentDescription[adapters.size()];
			int i=0;
			for(Iterator it=adapters.values().iterator(); i<ret.length; i++)
			{
				ret[i] = (IComponentDescription)((CMSComponentDescription)((IComponentAdapter)it.next()).getDescription()).clone();
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
			CMSComponentDescription ad = (CMSComponentDescription)getDescription(adesc.getName());
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
			synchronized(adapters)
			{
				for(Iterator it=adapters.values().iterator(); it.hasNext(); )
				{
					CMSComponentDescription	test	= (CMSComponentDescription)((IComponentAdapter)it.next()).getDescription();
					if(adesc==null ||
						(adesc.getOwnership()==null || adesc.getOwnership().equals(test.getOwnership()))
//						&& (adesc.getName().getParent()==null || adesc.getName().getParent().equals(test.getParent()))
						&& (adesc.getType()==null || adesc.getType().equals(test.getType()))
						&& (adesc.getState()==null || adesc.getState().equals(test.getState()))
//						&& (adesc.getProcessingState()==null || adesc.getProcessingState().equals(test.getProcessingState()))
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
			SServiceProvider.getServices(exta.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					Collection coll = (Collection)result;
//					System.out.println("cms: "+coll);
					// Ignore search failures of remote dfs
					CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new IResultListener()
					{
						public void resultAvailable(Object result)
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
						
						public void exceptionOccurred(Exception exception)
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
							lis.resultAvailable(null);
						}
					}
				}
				
				public void exceptionOccurred(Exception exception)
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
	public IComponentIdentifier generateComponentIdentifier(String localname, String platformname)
	{
		ComponentIdentifier ret = null;

		if(platformname==null)
			platformname = ((IComponentIdentifier)exta.getServiceProvider().getId()).getName();
		synchronized(adapters)
		{
			ret = new ComponentIdentifier(localname+"@"+platformname);
			if(adapters.containsKey(ret) || initinfos.containsKey(ret))
			{
				do
				{
					ret = new ComponentIdentifier(localname+(compcnt++)+"@"+platformname); // Hack?!
				}
				while(adapters.containsKey(ret) || initinfos.containsKey(ret));
			}
		}
		
		if(msgservice!=null)
		{
			ret.setAddresses(msgservice.getAddresses());
		}
//		else
//		{
//			SServiceProvider.getService(exta.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new DefaultResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
//					IMessageService	ms	= (IMessageService)result;
//					if(ms!=null)
//						ret.setAddresses(ms.getAddresses());
//				}
//			});
//		}

		return ret;
	}
	
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	/*public void	setProcessingState(IComponentIdentifier comp, String state)
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
//			notifyListeners(comp, desc);
//			ICMSComponentListener[]	alisteners;
//			synchronized(listeners)
//			{
//				Set	slisteners	= new HashSet(listeners.getCollection(null));
//				slisteners.addAll(listeners.getCollection(comp));
//				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
//			}
//			// todo: can be called after listener has (concurrently) deregistered
//			for(int i=0; i<alisteners.length; i++)
//			{
//				try
//				{
//					alisteners[i].componentChanged(desc);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//					System.out.println("WARNING: Exception when changing component state: "+desc+", "+e);
//				}
//			}
		}
	}*/
	
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	public void	setComponentState(IComponentIdentifier comp, String state)
	{
		assert IComponentDescription.STATE_SUSPENDED.equals(state) : "wrong state: "+comp+", "+state;
		
		CMSComponentDescription	desc	= null;
		synchronized(adapters)
		{
			desc	= (CMSComponentDescription)getDescription(comp);
			desc.setState(state);			
		}
		
		notifyListenersChanged(comp, desc);
	}

	//-------- IService interface --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		
		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final boolean[]	services = new boolean[2];
				
				SServiceProvider.getService(exta.getServiceProvider(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						exeservice	= (IExecutionService)result;
						boolean	setresult;
						synchronized(services)
						{
							services[0]	= true;
							setresult	= services[0] && services[1];
						}
						if(setresult)
							ret.setResult(getServiceIdentifier());
					}
				});
				
				SServiceProvider.getService(exta.getServiceProvider(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
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
//								msgservice.signalStarted().addResultListener(new IResultListener()
//								{
//									public void resultAvailable(Object result)
//									{
										synchronized(adapters)
										{
											// Hack?! Need to set transport addresses on root id.
											((ComponentIdentifier)root.getComponentIdentifier()).setAddresses(msgservice.getAddresses());
//											System.out.println("root: "+SUtil.arrayToString(msgservice.getAddresses())+" "+root.getComponentIdentifier().hashCode());
											adapters.put(root.getComponentIdentifier(), root);
										}
//									}
									
//									public void exceptionOccurred(Exception exception)
//									{
//									}
//								});
							}
							else
							{
								synchronized(adapters)
								{
									adapters.put(root.getComponentIdentifier(), root);
								}
							}
						}
						
						if(setresult)
							ret.setResult(getServiceIdentifier());
					}
				});
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
//		System.out.println(": "+this);
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
			CMSComponentDescription	padesc = (CMSComponentDescription)getDescription(getParentIdentifier(cinfo));
			pasuspend = IComponentDescription.STATE_SUSPENDED.equals(padesc.getState());
		}
		// Suspend when set to suspend or when parent is also suspended or when specified in model.
		boolean	debugging = lmodel.getProperty("debugging")==null? false: ((Boolean)lmodel.getProperty("debugging")).booleanValue();
		debugging = debugging || lmodel.getSuspend(cinfo.getConfiguration())==null? false: lmodel.getSuspend(cinfo.getConfiguration()).booleanValue();
		boolean sus = cinfo.getSuspend()==null? false: cinfo.getSuspend().booleanValue();
		boolean	suspend	= sus || pasuspend || debugging;
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
	 *  Get the description for a component (if any).
	 */
	protected IComponentDescription	getDescription(IComponentIdentifier cid)
	{
		synchronized(adapters)
		{
			IComponentAdapter	adapter	= (IComponentAdapter)adapters.get(cid);
			// Hack? Allows components to getExternalAccess in init phase
			if(adapter==null)
			{
				Object[] ii = getInitInfo(cid);
				if(ii!=null)
					adapter = (IComponentAdapter)ii[1];
			}
			return adapter!=null ? adapter.getDescription() : null;
		}
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
	
	/**
	 *  Notify the cms listeners of a change.
	 */
	protected void notifyListenersChanged(final IComponentIdentifier cid, IComponentDescription desc)
	{
		ICMSComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(cid));
			alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		
//		System.out.println("comp changed: "+desc+" "+listeners);
//		logger.info("Component changed: "+desc+" "+listeners);
		
		for(int i=0; i<alisteners.length; i++)
		{
			final ICMSComponentListener lis = alisteners[i];
			lis.componentChanged(desc).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("prob: "+exception);
					removeComponentListener(cid, lis);
				}
			});
		}
	}
	
	/**
	 *  Notify the cms listeners of a removal.
	 */
	protected void notifyListenersRemoved(final IComponentIdentifier cid, IComponentDescription desc, Map results)
	{
		ICMSComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(cid));
			alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		
//		System.out.println("comp changed: "+desc+" "+listeners);
//		logger.info("Component changed: "+desc+" "+listeners);
		
		for(int i=0; i<alisteners.length; i++)
		{
			final ICMSComponentListener lis = alisteners[i];
			lis.componentRemoved(desc, results).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("prob: "+exception);
					removeComponentListener(cid, lis);
				}
			});
		}
	}
	
	/**
	 *  Notify the cms listeners of an addition.
	 */
	protected void notifyListenersAdded(final IComponentIdentifier cid, IComponentDescription desc)
	{
		ICMSComponentListener[]	alisteners;
		synchronized(listeners)
		{
			Set	slisteners	= new HashSet(listeners.getCollection(null));
			slisteners.addAll(listeners.getCollection(cid));
			alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
		}
		// todo: can be called after listener has (concurrently) deregistered
		
//		System.out.println("comp changed: "+desc+" "+listeners);
//		logger.info("Component changed: "+desc+" "+listeners);
		
		for(int i=0; i<alisteners.length; i++)
		{
			final ICMSComponentListener lis = alisteners[i];
			lis.componentAdded(desc).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("prob: "+exception);
					removeComponentListener(cid, lis);
				}
			});
		}
	}
	
	/**
	 *  Get the remote component management system for a specific component id.
	 */
	protected IFuture	getRemoteCMS(final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		SServiceProvider.getService(exta.getServiceProvider(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				rms.getServiceProxy(cid, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Get the init info for a component identifier.
	 */
	protected Object[] getInitInfo(IComponentIdentifier cid)
	{
		Object[] ret = (Object[])initinfos.get(cid);
		if(ret!=null && ret.length==0)
			ret = null;
		return ret;
	}
}
