package jadex.base.service.cms;

import jadex.bridge.ComponentCreationException;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.ComponentFactorySelector;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.kernelbase.IBootstrapFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Abstract default implementation of component management service.
 */
@Service
public abstract class DecoupledComponentManagementService implements IComponentManagementService
{
	//-------- constants --------

	/** The component counter. Used for generating unique component ids. */
	public static int compcnt = 0;

	//-------- attributes --------

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The logger. */
	protected Logger logger;

	/** The components (id->component adapter). */
	protected Map<IComponentIdentifier, IComponentAdapter> adapters;
	
	/** The cleanup commands for the components (component id -> cleanup command). */
	protected Map<IComponentIdentifier, CleanupCommand> ccs;
	
	/** The cleanup futures for the components (component id -> cleanup future). */
	protected Map<IComponentIdentifier, IFuture<Map<String, Object>>> cfs;
	
	/** The listeners. */
	protected MultiCollection listeners;
	
	/** The result (kill listeners). */
	protected Map<IComponentIdentifier, IntermediateResultListener> resultlisteners;
	
	/** The execution service (cached to avoid using futures). */
	protected IExecutionService	exeservice;
	
	/** The message service (cached to avoid using futures). */
	protected IMessageService	msgservice;
	
//	/** The marshal service (cached to avoid using futures). */
//	protected IMarshalService	marshalservice;
	
	/** The root component. */
	protected IComponentAdapter root;
	
	/** The init adapters and descriptions, i.e. adapters and desc of initing components, 
	 *  are only visible for the component and child components in their init. */
	protected Map<IComponentIdentifier, InitInfo> initinfos;
	
	/** Number of non-daemon children for each autoshutdown component (cid->Integer). */
	protected Map<IComponentIdentifier, Integer> childcounts;
	
	/**	The local filename cache (tuple(parent filename, child filename) -> local typename)*/
	protected Map<Tuple, String> localtypes;
	
	/** The cached factories. */
	protected Collection<IComponentFactory> factories;
	
	/** The bootstrap component factory. */
	protected IBootstrapFactory componentfactory;
	
	/** The default copy parameters flag for service calls. */
	protected boolean copy;
	
	/** The realtime timeout flag. */
	protected boolean realtime;
	
	/** The locked components. */
	protected Map<IComponentIdentifier, LockEntry> lockentries;
	
	/** The time service. */
	protected IClockService clockservice;
	
    //-------- constructors --------

    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public DecoupledComponentManagementService(IComponentAdapter root)
	{
    	this(root, null, true, true);
    }
    
    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public DecoupledComponentManagementService(IComponentAdapter root, 
    	IBootstrapFactory componentfactory, boolean copy, boolean realtime)
	{
		this.root = root;
		this.componentfactory = componentfactory;
		this.copy = copy;
		this.realtime = realtime;
		
		// Todo: why some collections synchronized? single thread access!?
		this.adapters = SCollection.createHashMap();
//		this.adapters = Collections.synchronizedMap(adapters);
		this.ccs = SCollection.createLinkedHashMap();
		this.cfs = SCollection.createLinkedHashMap();
//		this.children	= SCollection.createMultiCollection();
//		this.logger = Logger.getLogger(AbstractComponentAdapter.getLoggerName(exta.getComponentIdentifier())+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.resultlisteners = SCollection.createHashMap();
//		this.resultlisteners = Collections.synchronizedMap(resultlisteners);
//		this.initfutures = Collections.synchronizedMap(SCollection.createHashMap());
		this.initinfos = SCollection.createHashMap();
//		this.initinfos = Collections.synchronizedMap(initinfos);
		this.childcounts = SCollection.createHashMap();
		this.localtypes	= new LRU<Tuple, String>(100);
		this.lockentries = SCollection.createHashMap();
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
	public abstract IFuture<Void> killComponent(IComponentAdapter adapter);
	
	/**
	 *  Cancel the execution.
	 */
	public abstract IFuture<Void> cancel(IComponentAdapter adapter);

	/**
	 *  Do a step.
	 */
	public abstract IFuture<Void> doStep(IComponentAdapter adapter);
	
    //-------- IComponentManagementService interface --------
    
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	public IFuture<IModelInfo> loadComponentModel(final String filename, final IResourceIdentifier rid)
	{
//		if(filename!=null && filename.indexOf("RemoteServiceManagementAgent")!=-1)
//			System.out.println("cache miss: "+filename);
		
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		if(filename==null)
		{
			ret.setException(new IllegalArgumentException("Filename must not null"));
		}
		else
		{
			SServiceProvider.getService(agent.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<ILibraryService, IModelInfo>(ret)
			{
				public void customResultAvailable(final ILibraryService ls)
				{
					IFuture<IComponentFactory> fut = SServiceProvider.getService(agent.getServiceContainer(), new ComponentFactorySelector(filename, null, rid));
					fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentFactory, IModelInfo>(ret)
					{
						public void customResultAvailable(IComponentFactory factory)
						{
							factory.loadModel(filename, null, rid)
								.addResultListener(new DelegationResultListener<IModelInfo>(ret));
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
		}
		return ret;
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param resultlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture<IComponentIdentifier> createComponent(final String name, final String modelname, CreationInfo info, 
		final IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{			
		if(modelname==null)
			return new Future<IComponentIdentifier>(new IllegalArgumentException("Modelname must not null."));
		
//		if(modelname.indexOf("RemoteServiceManagementAgent")!=-1 || modelname.indexOf("rms")!=-1)
//			System.out.println("cache miss: "+modelname);
		
//		final DebugException	de	= new DebugException();
	
//		System.out.println("create component: "+modelname+" "+name);
		
		final Future<IComponentIdentifier> inited = new Future<IComponentIdentifier>();
		final Future<Void> resfut = new Future<Void>();
		
		final CreationInfo cinfo = new CreationInfo(info);	// Dummy default info, if null. Must be cloned as localtype is set on info later.
		
		final IntermediateResultListener reslis = new IntermediateResultListener(resultlistener);
		
		if(cinfo.getParent()!=null)
		{
			// Lock the parent while creating
			final String lockkey = SUtil.createUniqueId("lock");
			LockEntry kt = lockentries.get(cinfo.getParent());
			if(kt==null)
			{
				kt= new LockEntry(cinfo.getParent());
				lockentries.put(cinfo.getParent(), kt);
			}
			kt.addLocker(lockkey);
			inited.addResultListener(createResultListener(new IResultListener<IComponentIdentifier>()
			{
				public void resultAvailable(IComponentIdentifier result)
				{
					LockEntry kt = lockentries.get(cinfo.getParent());
					kt.removeLocker(lockkey);
					if(kt.getLockerCount()==0)
						lockentries.remove(cinfo.getParent());
				}
				public void exceptionOccurred(Exception exception)
				{
					LockEntry kt = lockentries.get(cinfo.getParent());
					kt.removeLocker(lockkey);
					if(kt.getLockerCount()==0)
						lockentries.remove(cinfo.getParent());
				}
			}));
		}
		
		if(cinfo.getParent()!=null && isRemoteComponent(cinfo.getParent()))
		{				
			getRemoteCMS(cinfo.getParent()).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(inited)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.createComponent(name, modelname, cinfo, resultlistener).addResultListener(new DelegationResultListener<IComponentIdentifier>(inited));
				}
			}));
		}
		else
		{
	//		System.out.println("create start1: "+model+" "+cinfo.getParent());
			
			if(name!=null && name.indexOf('@')!=-1)
			{
				inited.setException(new ComponentCreationException("No '@' allowed in component name.", ComponentCreationException.REASON_WRONG_ID));
			}
			else
			{
				msgservice.getAddresses().addResultListener(createResultListener(new ExceptionDelegationResultListener<String[], IComponentIdentifier>(inited)
				{
					public void customResultAvailable(final String[] addresses)
					{
						// Load the model with fitting factory.
						getResourceIdentifier(cinfo).addResultListener(createResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, IComponentIdentifier>(inited)
						{
							public void customResultAvailable(final IResourceIdentifier rid)
							{
//								System.out.println("loading: "+modelname+" "+rid);
								resolveFilename(modelname, cinfo, rid).addResultListener(createResultListener(new ExceptionDelegationResultListener<String, IComponentIdentifier>(inited)
								{
									public void customResultAvailable(final String model)
									{
										getComponentFactory(model, cinfo, rid)
											.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentFactory, IComponentIdentifier>(inited)
										{
											public void customResultAvailable(final IComponentFactory factory)
											{
												factory.loadModel(model, cinfo.getImports(), rid)
													.addResultListener(createResultListener(new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier>(inited)
												{
													public void exceptionOccurred(Exception exception)
													{
														super.exceptionOccurred(exception);
													}
														
													public void customResultAvailable(final IModelInfo lmodel)
													{
														if(lmodel.getReport()!=null)
														{
															inited.setException(new ComponentCreationException("Errors loading model: "+model+"\n"+lmodel.getReport().getErrorText(), 
																ComponentCreationException.REASON_MODEL_ERROR));
														}
														else
														{
															factory.getComponentType(model, cinfo.getImports(), rid)
																.addResultListener(createResultListener(new ExceptionDelegationResultListener<String, IComponentIdentifier>(inited)
															{
																public void customResultAvailable(final String type)
																{
																	// Create id and adapter.
																	
																	final ComponentIdentifier cid;
																	
																	final IComponentAdapter pad = getParentAdapter(cinfo);
																	IExternalAccess parent = getComponentInstance(pad).getExternalAccess();
					
																	IComponentIdentifier pacid = parent.getComponentIdentifier();
																	String paname = pacid.getName().replace('@', '.');
																	if(name!=null)
																	{
																		cid = new ComponentIdentifier(name+"@"+paname, addresses);
																		if(adapters.containsKey(cid) || initinfos.containsKey(cid))
																		{
		//																	de.printStackTrace();
																			inited.setException(new ComponentCreationException("Component "+cid+" already exists.", ComponentCreationException.REASON_COMPONENT_EXISTS, cid));
																			return;
		//																	throw new RuntimeException("Component "+cid+" already exists.");
																		}
//																		if(msgservice!=null)
//																		{
//																			cid.setAddresses(msgservice.getAddresses());
//																		}
																	}
																	else
																	{
																		cid = (ComponentIdentifier)generateComponentIdentifier(lmodel.getName(), paname, addresses);
																	}
																	initinfos.put(cid, new InitInfo(null, null, cinfo, null, resfut, null));
																	
																	Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
																	Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
																	Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
																	final CMSComponentDescription ad = new CMSComponentDescription(cid, type, master, daemon, autosd, 
																		lmodel.getFullName(), cinfo.getLocalType(), lmodel.getResourceIdentifier(), clockservice.getTime());
																	
																	logger.info("Starting component: "+cid.getName());
							//										System.err.println("Pre-Init: "+cid);
																	
																	resfut.addResultListener(createResultListener(new IResultListener<Void>()
																	{
																		public void resultAvailable(Void result)
																		{
																			logger.info("Started component: "+cid.getName());
							//												System.err.println("Post-Init: "+cid);
							
																			// Create the component instance.
																			final IComponentAdapter adapter;
																			
		//																	System.out.println("created: "+ad);
																			
																			// Init successfully finished. Add description and adapter.
																			InitInfo	info	= initinfos.get(cid);
																			adapter = info.getAdapter();
																			
																			// Init finished. Set to suspended until parent registration is finished.
																			// not set to suspend to allow other initing sibling components invoking services
				//															ad.setState(IComponentDescription.STATE_SUSPENDED);
																			
					//														System.out.println("adding cid: "+cid+" "+ad.getMaster()+" "+ad.getDaemon()+" "+ad.getAutoShutdown());
																			adapters.put(cid, adapter);
																			// Removed in resumeComponent()
				//																initinfos.remove(cid);
																			
																			CMSComponentDescription padesc	= (CMSComponentDescription)pad.getDescription();
//																			InitInfo painfo = getParentInfo(cinfo);
//																			if(painfo!=null && painfo.getDescription()!=null)
//																			{
//																				padesc = (CMSComponentDescription)painfo.getDescription();
//																			}
//																			else
//																			{
//																				padesc = (CMSComponentDescription)getDescription(getParentIdentifier(cinfo));
//																			}
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
																			
																			// Register component at parent.
																			getComponentInstance(pad).componentCreated(ad, lmodel)
																				.addResultListener(createResultListener(new IResultListener<Void>()
																			{
																				public void resultAvailable(Void result)
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
																							
			//																		System.out.println("created: "+cid.getLocalName());//+" "+(parent!=null?parent.getComponentIdentifier().getLocalName():"null"));
							//														System.out.println("added: "+descs.size()+", "+aid);
																					
																					resultlisteners.put(cid, reslis);
//																								if(resultlistener!=null)
//																									resultlisteners.put(cid, resultlistener);
																					
																					inited.setResult(cid);
																					
																					Future<Map<String, Object>>	killfut;
																					killfut	= (Future<Map<String, Object>>)cfs.get(cid);
																					if(killfut!=null)
																					{
																						// Remove init infos otherwise done in resume()
																						List<IComponentIdentifier>	cids	= new ArrayList<IComponentIdentifier>();
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
																					
																					if(killfut!=null)
																					{
																						// Kill component if destroy called during init.
																						destroyComponent(cid, killfut);
																					}
																					else
																					{
																						// Start regular execution of inited component
																						// when this component is the outermost component, i.e. with no parent
																						// or the parent is already running
																						if(cinfo.getParent()==null || initinfos.get(cinfo.getParent())==null)
																						{
			//																				System.out.println("start: "+cid);
																							resumeComponent(cid, true);
																						}
																					}
																				}
																				
																				public void exceptionOccurred(Exception exception)
																				{
																					// Todo: use some logger for user error?
																					if(!(exception instanceof ComponentTerminatedException))
																						exception.printStackTrace();
																				}
																			}));								
																		}
																		
																		public void exceptionOccurred(final Exception exception)
																		{
																			logger.info("Starting component failed: "+cid+", "+exception);
//																			System.err.println("Starting component failed: "+cid+", "+exception);
//																			exception.printStackTrace();
			//																System.out.println("Ex: "+cid+" "+exception);
																			final Runnable	cleanup	= new Runnable()
																			{
																				public void run()
																				{
																					adapters.remove(cid);
																					removeInitInfo(cid);
																					
																					IntermediateResultListener reslis = resultlisteners.remove(cid);
																					if(reslis!=null)
																						reslis.exceptionOccurred(exception);
																					
																					exitDestroy(cid, ad, exception, null);
																					
																					inited.setException(exception);
																				}
																			};
																			
																			IComponentIdentifier[]	children	= ad.getChildren();
																			if(children.length>0)
																			{
																				CounterResultListener<Map<String, Object>>	crl	= new CounterResultListener<Map<String, Object>>(children.length, true,
																					createResultListener(new IResultListener<Void>()
																					{
																						public void resultAvailable(Void result)
																						{
																							cleanup.run();
																						}
																						
																						public void exceptionOccurred(Exception exception)
																						{
																							cleanup.run();
																						}
																					}
																				));
																				
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
																	}));
																	
																	// Create component and wakeup for init.
																	// Use first configuration if no config specified.
																	String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
																		: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
																						
																	factory.createComponentInstance(ad, getComponentAdapterFactory(), lmodel, 
																		config, cinfo.getArguments(), parent, cinfo.getRequiredServiceBindings(), copy, realtime, reslis, resfut)
																		.addResultListener(createResultListener(new IResultListener<Tuple2<IComponentInstance, IComponentAdapter>>()
																	{
																		public void resultAvailable(Tuple2<IComponentInstance, IComponentAdapter> comp)
																		{
																			// Store (invalid) desc, adapter and info for children
																			// 0: description, 1: adapter, 2: creation info, 3: model, 4: initfuture, 5: component instance
				//															System.out.println("infos: "+ad.getName());
																			InitInfo ii = getInitInfo(cid);
																			ii.setDescription(ad);
																			ii.setInfo(cinfo);
																			ii.setInstance(comp.getFirstEntity());
																			ii.setAdapter(comp.getSecondEntity());
																			ii.setModel(lmodel);
		//																	initinfos.put(cid, new Object[]{ad, comp.getSecondEntity(), cinfo, lmodel, resfut, comp.getFirstEntity()});
																			
																			try
																			{
																				// Start the init procedure by waking up the adapter.
																				getComponentAdapterFactory().initialWakeup(comp.getSecondEntity());
																			}
																			catch(RuntimeException e)
																			{
																				exceptionOccurred(e);
																			}
																		}
																		
																		public void exceptionOccurred(Exception exception)
																		{
																			// Init problem might be notified already in other future.
																			if(!resfut.isDone())
																			{
																				inited.setExceptionIfUndone(exception);
																			}
																		}
																	}));
																}
															}));
														}
													}
												}));
											}
										}));
									}
								}));
							}
						}));
					}
				}));
			}
		}
		return inited;
	}
	
	/**
	 *  Find the file name and local component type name
	 *  for a component to be started.
	 */
	protected IFuture<String>	resolveFilename(final String modelname, final CreationInfo cinfo, final IResourceIdentifier rid)
	{
		final Future<String>	ret	= new Future<String>();
		SServiceProvider.getService(agent.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<ILibraryService, String>(ret)
		{
			public void customResultAvailable(ILibraryService libservice)
			{
				libservice.getClassLoader(rid).addResultListener(createResultListener(new ExceptionDelegationResultListener<ClassLoader, String>(ret)
				{
					public void customResultAvailable(ClassLoader cl)
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
						
						ret.setResult(filename);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get a fitting component factory for a specific model.
	 *  Searches the cached factories for the one that fits
	 *  the model and returns it. Possibly reevaluates the
	 *  cache when no factory was found.
	 *  @param model The model file name.
	 *  @param cinfo The creaion info.
	 *  @param rid The resource identifier.
	 *  @return The component factory.
	 */
	protected IFuture<IComponentFactory> getComponentFactory(final String model, final CreationInfo cinfo, final IResourceIdentifier rid)
	{
		final Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
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
			IFuture<Collection<IComponentFactory>> fut = SServiceProvider.getServices(agent.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM);
			fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, IComponentFactory>(ret)
			{
				public void customResultAvailable(Collection<IComponentFactory> facts)
				{
					factories = facts;//(Collection)result;
					getComponentFactory(model, cinfo, rid).addResultListener(new DelegationResultListener<IComponentFactory>(ret));
				}
			}));
		}
		else
		{
//			System.out.println("create start2: "+model+" "+cinfo.getParent());
						
			selectComponentFactory(factories==null? null: (IComponentFactory[])factories.toArray(new IComponentFactory[factories.size()]), model, cinfo, rid, 0)
				.addResultListener(createResultListener(new DelegationResultListener<IComponentFactory>(ret)
			{
//				public void customResultAvailable(Object result)
//				{
//					System.out.println("res: "+result);
//					super.customResultAvailable(result);
//				}
					
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("factory ex: "+exception);
					IFuture<Collection<IComponentFactory>> fut = SServiceProvider.getServices(agent.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM);
					fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, IComponentFactory>(ret)
					{
						public void customResultAvailable(Collection<IComponentFactory> facts)
						{	
							factories = facts;
							for(Iterator it=factories.iterator(); it.hasNext(); )
							{
								Object o = it.next();
//								System.out.println("is: "+o+" "+(o instanceof IComponentFactory));
							}
							selectComponentFactory((IComponentFactory[])factories.toArray(new IComponentFactory[factories.size()]), model, cinfo, rid, 0)
								.addResultListener(new DelegationResultListener<IComponentFactory>(ret));
						}
					}));
				}
			}));
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
	protected IFuture<IComponentFactory> selectComponentFactory(final IComponentFactory[] factories, 
		final String model, final CreationInfo cinfo, final IResourceIdentifier rid, final int idx)
	{
		final Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		if(factories!=null && factories.length>0)
		{
			factories[idx].isLoadable(model, cinfo.getImports(), rid)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<Boolean, IComponentFactory>(ret)
			{
				public void customResultAvailable(Boolean res)
				{
					if(res.booleanValue())
					{
						ret.setResult(factories[idx]);
					}
					else if(idx+1<factories.length)
					{
						selectComponentFactory(factories, model, cinfo, rid, idx+1)
							.addResultListener(new DelegationResultListener<IComponentFactory>(ret));
					}
					else
					{
						selectFallbackFactory(model, cinfo, rid).addResultListener(createResultListener(new DelegationResultListener<IComponentFactory>(ret)));
					}
				}		
			}));
		}
		else
		{
			selectFallbackFactory(model, cinfo, rid).addResultListener(createResultListener(new DelegationResultListener<IComponentFactory>(ret)));
		}
		
		return ret;
	}
	
	/**
	 *  Select the fallback factory.
	 */
	protected IFuture<IComponentFactory> selectFallbackFactory(final String model, final CreationInfo cinfo, final IResourceIdentifier rid)
	{
		final Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		if(componentfactory!=null)
		{
			componentfactory.isLoadable(model, cinfo.getImports(), rid)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<Boolean, IComponentFactory>(ret)
			{
				public void customResultAvailable(Boolean res)
				{
					if(res.booleanValue())
					{
						ret.setResult(componentfactory);
					}
					else
					{
						ret.setException(new ComponentCreationException("No factory found for: "+model, ComponentCreationException.REASON_NO_COMPONENT_FACTORY));
					}
				}
			}));
		}
		else
		{
			ret.setException(new ComponentCreationException("No factory found for: "+model, ComponentCreationException.REASON_NO_COMPONENT_FACTORY));
		}
		
		return ret;
	}

	/**
	 *  Get the info of the parent component.
	 */
	protected InitInfo getParentInfo(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		return getInitInfo(paid);
	}
	
	/**
	 *  Get the adapter of the parent component.
	 */
	protected IComponentAdapter getParentAdapter(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		IComponentAdapter adapter;
		adapter = (IComponentAdapter)adapters.get(paid);
		if(adapter==null)
			adapter = getParentInfo(cinfo).getAdapter();
		return adapter;
	}
	
	/**
	 *  Get the desc of the parent component.
	 */
	protected CMSComponentDescription getParentDescription(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		CMSComponentDescription desc = adapters.containsKey(paid)
			? (CMSComponentDescription)((IComponentAdapter)adapters.get(paid)).getDescription()
			: (CMSComponentDescription)getParentInfo(cinfo).getDescription();
		return desc;
	}
		
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
	public IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		if(cid.toString().indexOf("Mandelbrot")!=-1)
			System.out.println("destroy: "+cid.getName());
//		else if(getDescription(cid)==null) 
//			System.out.println("destroy: null");
//		if(cid.getParent()==null)
//		{
//			System.out.println("Platform kill called:_"+cid.getName());
//			Thread.dumpStack();
//		}
		
		boolean contains = false;
		boolean locked = false;
		Future<Map<String, Object>> tmp;
		
		contains = cfs.containsKey(cid);
		tmp = contains? (Future<Map<String, Object>>)cfs.get(cid): new Future<Map<String, Object>>();
//			System.out.println("destroy0: "+cid+" "+cfs.containsKey(cid));
//			Thread.currentThread().dumpStack();
		
		// If destroyComponent has not called before
		if(!contains)
		{
			cfs.put(cid, tmp);
		}
		
		// Is the component locked?
		LockEntry kt = lockentries.get(cid);
		if(kt!=null && kt.getLockerCount()>0)
		{
			kt.setKillFuture(tmp);
			locked = true;
		}

		final Future<Map<String, Object>> ret = tmp;
		
		if(!contains && !locked)
		{
			destroyComponent(cid, ret);
		}
//		else if("Application".equals(getDescription(cid).getType()))
//			System.out.println("no destroy: "+contains+", "+locked);
		
		return ret;
	}

	/**
	 *  Internal destroy method that performs the actual work.
	 *	@param cid The component to destroy.
	 *  @param ret The future to be informed.
	 */
	protected void destroyComponent(final IComponentIdentifier cid,	final Future<Map<String, Object>> ret)
	{
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
//					final IComponentManagementService rcms = (IComponentManagementService)result;
					rcms.destroyComponent(cid).addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
				}
			});
		}
		else
		{
			IComponentAdapter ad;
			InitInfo infos;
			ad = (IComponentAdapter)adapters.get(cid);
			infos = getInitInfo(cid);
			// Terminate component that is shut down during init.
//			if(infos!=null && infos.length>0 && !((IFuture)infos[4]).isDone())
			if(infos!=null && !infos.getInitFuture().isDone())
			{
				// Propagate failed component init.
				ad	= infos.getAdapter();
				if(ad==null || ad.getException()!=null)
				{
					if(cid.toString().indexOf("Mandelbrot")!=-1)
						System.out.println("init future exception: "+cid.getName());
					infos.getInitFuture().setException(ad.getException());
				}
				
				// Component terminated from outside: wait for init to complete, will be removed as cleanup future is registered (cfs).
				else
				{
					if(cid.toString().indexOf("Mandelbrot")!=-1)
						System.out.println("Queued component termination during init: "+cid.getName());
					logger.info("Queued component termination during init: "+cid.getName());
				}
			}
			// Terminate normally inited component.
			else 
			{
				final IComponentAdapter adapter = ad;
				
				// Kill subcomponents
				if(adapter==null)
				{
					if(cid.toString().indexOf("Mandelbrot")!=-1)
						System.out.println("Terminating component structure adapter is null: "+cid.getName());
//					
					// Todo: need to kill children!? How to reproduce this case!?
					logger.info("Terminating component structure adapter is null: "+cid.getName());
					exitDestroy(cid, null, new ComponentTerminatedException(cid, "Component does not exist."), null);
				}
				else
				{
					if(cid.toString().indexOf("Mandelbrot")!=-1)
						System.out.println("Terminating component structure: "+cid.getName());
//					
					logger.info("Terminating component structure: "+cid.getName());
					final CMSComponentDescription	desc = (CMSComponentDescription)adapter.getDescription();
					IComponentIdentifier[] achildren = desc.getChildren();
					
//					System.out.println("kill childs: "+cid+" "+SUtil.arrayToString(achildren));
					
					destroyComponentLoop(cid, achildren, achildren.length-1).addResultListener(createResultListener(new IResultListener<List<Exception>>()
					{
						public void resultAvailable(List<Exception> result)
						{
							if(cid.toString().indexOf("Mandelbrot")!=-1)
								System.out.println("Terminated component structure: "+cid.getName());
							
							logger.info("Terminated component structure: "+cid.getName());
							CleanupCommand	cc	= null;
							IFuture<Void>	fut	= null;
							synchronized(adapters)
							{
								try
								{
									IComponentAdapter adapter = (IComponentAdapter)adapters.get(cid);
									// Component may be already killed (e.g. when autoshutdown).
									if(adapter!=null)
									{
										if(cid.toString().indexOf("Mandelbrot")!=-1)
											System.out.println("destroy1: "+cid.getName());
										
										// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
										// todo: killcomponent should only be called once for each component?
										if(!ccs.containsKey(cid))
										{
											if(cid.toString().indexOf("Mandelbrot")!=-1)
												System.out.println("killing a: "+cid);
											
											cc	= new CleanupCommand(cid);
											ccs.put(cid, cc);
											logger.info("Terminating component: "+cid.getName());
											fut	= killComponent(adapter);
		//									component.killComponent(cc);	
										}
										else
										{
											if(cid.toString().indexOf("Mandelbrot")!=-1)
												System.out.println("killing b: "+cid);
											
											cc = (CleanupCommand)ccs.get(cid);
										}
									}
								}
								catch(Throwable e)
								{
									e.printStackTrace();
								}
							}
							
							// Add listener outside synchronized block to avoid deadlocks
							if(fut!=null && cc!=null)
							{
								// Cannot use invoke later during platform shutdown
								IResultListener lis = cid.getParent()==null? cc: createResultListener(cc);
								fut.addResultListener(lis);
							}
							
							if(cc==null)
							{
								// Todo: what is this case?
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
					}));
				}
			}
		}
	}
	
	/**
	 *  Exit the destroy method by setting description state and resetting maps.
	 */
	protected void exitDestroy(IComponentIdentifier cid, IComponentDescription desc, Exception ex, Map<String, Object> results)
	{
//		Thread.dumpStack();
		Future<Map<String, Object>>	ret;
		if(desc instanceof CMSComponentDescription)
		{
			((CMSComponentDescription)desc).setState(IComponentDescription.STATE_TERMINATED);
		}
		ccs.remove(cid);
		ret	= (Future<Map<String, Object>>)cfs.remove(cid);
		
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
	protected IFuture<List<Exception>> destroyComponentLoop(final IComponentIdentifier cid, final IComponentIdentifier[] achildren, final int i)
	{
		final Future<List<Exception>> ret = new Future<List<Exception>>();
		
		if(achildren.length>0)
		{
			final List<Exception> exceptions = new ArrayList<Exception>();
			destroyComponent(achildren[i]).addResultListener(createResultListener(new IResultListener<Map<String, Object>>()
			{
				public void resultAvailable(Map<String, Object> result)
				{
					if(i>0)
					{
						destroyComponentLoop(cid, achildren, i-1).addResultListener(
							createResultListener(new DelegationResultListener<List<Exception>>(ret)));
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
			}));
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
	public IFuture<Void> suspendComponent(final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.suspendComponent(cid).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			CMSComponentDescription desc;
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
			cancel(adapter).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
//					exeservice.cancel(adapter).addResultListener(new DelegationResultListener(ret));
			
			notifyListenersChanged(cid, desc);
		}
		
		return ret;
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier cid)
	{
		return resumeComponent(cid, false);
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(final IComponentIdentifier cid, final boolean initresume)
	{
//		System.out.println("resume: "+cid);
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid))
		{
			assert !initresume;
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.resumeComponent(cid).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			// Resume subcomponents
			final CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
			IComponentIdentifier[] achildren = desc!=null ? desc.getChildren() : null;

			if(desc!=null)
			{
				IResultListener<Void> lis = createResultListener(new CounterResultListener<Void>(achildren.length, true, new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
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
								Future<Map<String, Object>>	destroy	= null;
								// Not killed during init.
								if(!cfs.containsKey(cid))
								{
									InitInfo ii = removeInitInfo(cid);
		//							System.out.println("removed: "+cid+" "+ii);
									if(ii!=null && ii.getInstance()!=null)
									{
										instance = ii.getInstance();
										boolean	suspend = isInitSuspend(ii.getInfo(), ii.getModel());
										
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
									removeInitInfo(cid);
									destroy	= (Future<Map<String, Object>>)cfs.remove(cid);
								}									
								
								if(instance!=null)
								{
									try
									{
										final IComponentInstance	ci	= instance;
										instance.getExternalAccess().scheduleImmediate(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												ci.startBehavior();
												return IFuture.DONE;
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
								if(IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
								{
									wakeup	= true;
									desc.setState(IComponentDescription.STATE_ACTIVE);
									changed	= true;
								}
								if(wakeup)
								{
									adapter.wakeup();
								}
							}
							
							if(changed)
								notifyListenersChanged(cid, desc);
						
							ret.setResult(null);
//							ret.setResult(desc);
						}
					}
				}));
				
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
	public IFuture<Void> stepComponent(final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.stepComponent(cid).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
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
			
			doStep(adapter).addResultListener(new DelegationResultListener<Void>(ret));
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
	public IFuture<Void> setComponentBreakpoints(final IComponentIdentifier cid, final String[] breakpoints)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.setComponentBreakpoints(cid, breakpoints).addResultListener(
						createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			CMSComponentDescription ad = (CMSComponentDescription)getDescription(cid);
			ad.setBreakpoints(breakpoints);
			
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
    public IFuture<Void> addComponentListener(IComponentIdentifier comp, ICMSComponentListener listener)
    {
		listeners.put(comp, listener);
		return IFuture.DONE;
    }
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeComponentListener(IComponentIdentifier comp, ICMSComponentListener listener)
    {
		listeners.remove(comp, listener);
		return IFuture.DONE;
    }
    
    /**
	 *  Add a result listener. Also intermediate result listeners can be
	 *  added. In this case results are immediately fed back when set.
	 *  @param listener The result (or intermediate) result listener.
	 */
	public IFuture<Void> addComponentResultListener(IResultListener<Collection<Tuple2<String, Object>>> listener, IComponentIdentifier cid)
	{
		Future<Void> ret = new Future<Void>();
		IntermediateResultListener lis = (IntermediateResultListener)resultlisteners.get(cid);
		if(lis!=null)
		{
			lis.addListener(listener);
			ret.setResult(null);
		}
		else
		{
			ret.setException(new RuntimeException("Component has no registered listener: "+cid));
		}
		return ret;
	}
	
	/**
	 *  Add a previously added result listener. 
	 *  @param listener The result (or intermediate) result listener.
	 */
	public IFuture<Void> removeComponentResultListener(IResultListener<Collection<Tuple2<String, Object>>> listener, IComponentIdentifier cid)
	{
		Future<Void> ret = new Future<Void>();
		IntermediateResultListener lis = (IntermediateResultListener)resultlisteners.get(cid);
		if(lis!=null)
		{
			lis.removeListener(listener);
			ret.setResult(null);
		}
		else
		{
			ret.setException(new RuntimeException("Component has no registered listener: "+cid));
		}
		return ret;
	}

    
    //-------- helper classes --------

	/**
	 *  Command that is executed on component cleanup.
	 */
	class CleanupCommand implements IResultListener<Void>
	{
		protected IComponentIdentifier cid;
		
		public CleanupCommand(IComponentIdentifier cid)
		{
//			System.out.println("CleanupCommand created");
			this.cid = cid;
		}
		
		public void resultAvailable(Void result)
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
			Map<String, Object> results = null;
			logger.info("Terminated component: "+cid.getName());
//			System.out.println("CleanupCommand: "+cid);
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
			
			// Use adapter exception before cleanup exception as it probably happened first.
			Exception	ex	= adapter.getException()!=null ? adapter.getException() : exception;
//			if(exceptions!=null && exceptions.containsKey(cid))
//			{
//				ex	= (Exception)exceptions.get(cid);
//				exceptions.remove(cid);
//			}
			IntermediateResultListener reslis = resultlisteners.remove(cid);
//			System.out.println("kill lis: "+cid+" "+reslis+" "+results+" "+ex);
			if(reslis!=null)	// null for platform.
			{
				if(ex!=null)
				{
					reslis.exceptionOccurred(ex);
				}
				else
				{
					reslis.finished();
	//					reslis.resultAvailable(results);
				}
			
				if(ex!=null && !reslis.isInitial())
				{
					// Unhandled component exception
					// Todo: delegate printing to parent component (if any).
					adapter.getLogger().severe("Fatal error, component '"+cid+"' will be removed.");
					ex.printStackTrace();
				}
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
	public IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid)
	{
		return getExternalAccess(cid, false);
	}
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	protected IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid, boolean internal)
	{
//		System.out.println("getExternalAccess: "+this+", "+cid);
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		if(cid==null)
		{
			ret.setException(new IllegalArgumentException("Identifier is null."));
			return ret;
		}
		
		if(isRemoteComponent(cid))
		{
//			System.out.println("getExternalAccess: remote");
			agent.getServiceContainer().searchService(IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IRemoteServiceManagementService, IExternalAccess>(ret)
			{
				public void customResultAvailable(IRemoteServiceManagementService rms)
				{
					rms.getExternalAccessProxy(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
				}
			});
	
	//		getRemoteCMS(cid).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
	//		{
	//			public void customResultAvailable(IComponentManagementService rcms)
	//			{
	//				rcms.getExternalAccess(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
	//			}
	//		});
		}
		else
		{
//			System.out.println("getExternalAccess: local");
			IComponentAdapter adapter = null;
//				System.out.println("getExternalAccess: adapters");
			boolean delayed = false;
			adapter = (IComponentAdapter)adapters.get(cid);
			
			if(adapter==null)
			{
				// Hack? Allows components to getExternalAccess in init phase
				InitInfo ii = getInitInfo(cid);
				if(ii!=null)
				{
					if(!internal && (ii.getAdapter()==null || ii.getAdapter().isExternalThread()))
					{
//							System.out.println("getExternalAccess: delayed");
						delayed = true;
						IFuture<Void> fut = ii.getInitFuture();
						fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
						{
							public void customResultAvailable(Void result)
							{
								try
								{
									ret.setResult(getComponentInstance(getComponentAdapter(cid)).getExternalAccess());
								}
								catch(Exception e)
								{
									ret.setException(e);
								}
							}
						}));
					}
					else
					{
//							System.out.println("getExternalAccess: not delayed");
						adapter = ii.getAdapter();
					}
				}
			}
			
			if(adapter!=null)
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
			else if(!delayed)
			{
				ret.setException(new RuntimeException("No local component found for component identifier: "+cid));
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
	protected IFuture<IResourceIdentifier>	getResourceIdentifier(final CreationInfo ci)
	{
		final Future<IResourceIdentifier>	ret	= new Future<IResourceIdentifier>();
		
		// User supplied resource identifier.
		if(ci!=null && ci.getResourceIdentifier()!=null)
		{
			ret.setResult(ci.getResourceIdentifier());
		}
		
		// Local parent //(but not platform -> platform now has valid rid).
		else if(ci!=null 
//			&& !ci.getParent().equals(root.getComponentIdentifier())
			&& (ci.getParent()==null || !isRemoteComponent(ci.getParent()))
//			&& !initinfos.containsKey(ci.getParent())	// does not work during init as external access is not available!?
//			&& !Boolean.TRUE.equals(ci.getPlatformloader()))
			)
		{
			getExternalAccess(ci.getParent()==null? root.getComponentIdentifier(): ci.getParent(), true)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExternalAccess, IResourceIdentifier>(ret)
			{
				public void customResultAvailable(IExternalAccess ea)
				{
//					System.err.println("Model class loader: "+ea.getModel().getName()+", "+ea.getModel().getClassLoader());
//						classloadercache.put(ci.getParent(), ea.getModel().getClassLoader());
					ret.setResult(ea.getModel().getResourceIdentifier());
				}
			}));
		}
		
		// Remote or no parent or platform as parent
		else
		{
			// null resource identifier for searching in all current libservice resources.
			ret.setResult(null);
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
		ret = (IComponentAdapter)adapters.get(cid);
		// Hack, to retrieve description from component itself in init phase
		if(ret==null)
		{
			InitInfo ii= getInitInfo(cid);
			if(ii!=null)
				ret	= ii.getAdapter();
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
	public IFuture<IComponentIdentifier> getParent(final IComponentIdentifier cid)
	{
		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getParent(cid).addResultListener(createResultListener(new DelegationResultListener<IComponentIdentifier>(ret)));
				}
			}));
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
	public IFuture<IComponentIdentifier[]> getChildren(final IComponentIdentifier cid)
	{
		final Future<IComponentIdentifier[]>	ret	= new Future<IComponentIdentifier[]>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier[]>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getChildren(cid).addResultListener(createResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret)));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			}));
		}
		else
		{
	//		System.out.println("getChildren: "+this+" "+isValid());
			IComponentIdentifier[] tmp;
			CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
//			System.out.println("desc: "+desc.getName()+" "+desc.hashCode());
			tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
			IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
//			System.out.println(getServiceIdentifier()+" "+desc.getName()+" "+SUtil.arrayToString(tmp));
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
		}
		
		return ret;
	}

	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildrenDescriptions(final IComponentIdentifier cid)
	{
		final Future<IComponentDescription[]>	ret	= new Future<IComponentDescription[]>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription[]>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getChildrenDescriptions(cid).addResultListener(createResultListener(new DelegationResultListener<IComponentDescription[]>(ret)));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			}));
		}
		else
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
		
		return ret;
	}
	
	//--------- information methods --------
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture<IComponentDescription> getComponentDescription(final IComponentIdentifier cid)
	{
		final Future<IComponentDescription> ret = new Future<IComponentDescription>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getComponentDescription(cid).addResultListener(createResultListener(new DelegationResultListener<IComponentDescription>(ret)));
				}
			}));
		}
		else
		{
			msgservice.updateComponentIdentifier(cid).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, IComponentDescription>(ret)
			{
				public void customResultAvailable(IComponentIdentifier rcid)
				{
//					System.out.println("desc: "+SUtil.arrayToString(rcid.getAddresses()));
					CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);

					// Hack, to retrieve description from component itself in init phase
					if(desc==null)
					{
						InitInfo ii= getInitInfo(cid);
						if(ii!=null)
							desc = (CMSComponentDescription)ii.getDescription();
					}
					
					if(desc!=null)
					{
						// addresses required for communication across platforms.
						// ret.setName(refreshComponentIdentifier(aid));
						desc.setName(rcid);
						desc = (CMSComponentDescription)((CMSComponentDescription)desc).clone();
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
			});
		}			
		
		return ret;
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions()
	{
		Future<IComponentDescription[]> fut = new Future<IComponentDescription[]>();
		
		IComponentDescription[] ret = new IComponentDescription[adapters.size()];
		int i=0;
		for(Iterator<IComponentAdapter> it=adapters.values().iterator(); i<ret.length; i++)
		{
			ret[i] = (IComponentDescription)((CMSComponentDescription)((IComponentAdapter)it.next()).getDescription()).clone();
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
	public IFuture<IComponentIdentifier[]> getComponentIdentifiers()
	{
		final Future<IComponentIdentifier[]> fut = new Future<IComponentIdentifier[]>();
		
		msgservice.getAddresses().addResultListener(createResultListener(
			new ExceptionDelegationResultListener<String[], IComponentIdentifier[]>(fut)
		{
			public void customResultAvailable(String[] addresses)
			{
				IComponentIdentifier[] ret;
				
				ret = (IComponentIdentifier[])adapters.keySet().toArray(new IComponentIdentifier[adapters.size()]);
				
				if(ret.length>0)
				{
					if(!Arrays.equals(ret[0].getAddresses(), addresses))
					{
						// addresses required for inter-platform comm.
						for(int i=0; i<ret.length; i++)
							ret[i] = new ComponentIdentifier(ret[i].getName(), addresses);
//							ret[i] = refreshComponentIdentifier(ret[i]); // Hack!
					}
				}
				
				fut.setResult(ret);
			}
		}));
		
		return fut;
	}
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier()
	{
		return new Future<IComponentIdentifier>(root.getComponentIdentifier());
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)
	{
		return searchComponents(adesc, con, false);
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(final IComponentDescription adesc, final ISearchConstraints con, boolean remote)
	{
		final Future<IComponentDescription[]> fut = new Future<IComponentDescription[]>();
		
//		System.out.println("search: "+components);
		final List<IComponentDescription> ret = new ArrayList<IComponentDescription>();

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
			for(Iterator<IComponentAdapter> it=adapters.values().iterator(); it.hasNext(); )
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

		//System.out.println("searched: "+ret);
		
//		System.out.println("Started search: "+ret);
//		open.add(fut);
		if(remote)
		{
			IFuture<Collection<IComponentManagementService>> futi = SServiceProvider.getServices(agent.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL);
			futi.addResultListener(createResultListener(new IResultListener<Collection<IComponentManagementService>>()
			{
				public void resultAvailable(Collection<IComponentManagementService> result)
				{
//					System.out.println("cms: "+coll);
					// Ignore search failures of remote dfs
					IResultListener<IComponentDescription[]> lis = createResultListener(new CollectionResultListener<IComponentDescription[]>(result.size(), true, 
						new IResultListener<Collection<IComponentDescription[]>>()
					{
						public void resultAvailable(Collection<IComponentDescription[]> result)
						{
							// Add all services of all remote dfs
							for(Iterator<IComponentDescription[]> it=result.iterator(); it.hasNext(); )
							{
								IComponentDescription[] res = it.next();
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
							fut.setResult((CMSComponentDescription[])ret.toArray(new CMSComponentDescription[ret.size()]));
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							open.remove(fut);
							fut.setException(exception);
//							fut.setResult(ret.toArray(new DFComponentDescription[ret.size()]));
						}
					}));
					for(Iterator<IComponentManagementService> it=result.iterator(); it.hasNext(); )
					{
						IComponentManagementService remotecms = it.next();
						if(remotecms!=DecoupledComponentManagementService.this)
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
					fut.setResult((CMSComponentDescription[])ret.toArray(new CMSComponentDescription[ret.size()]));
				}
			}));
		}
		else
		{
//			open.remove(fut);
//			System.out.println("Local search: "+ret+" "+open);
			fut.setResult((CMSComponentDescription[])ret.toArray(new CMSComponentDescription[ret.size()]));
		}
		
		return fut;
	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier generateComponentIdentifier(String localname, String platformname, String[] addresses)
	{
		ComponentIdentifier ret = null;

		if(platformname==null)
			platformname = ((IComponentIdentifier)agent.getServiceContainer().getId()).getName();
		ret = new ComponentIdentifier(localname+"@"+platformname, addresses);
		if(adapters.containsKey(ret) || initinfos.containsKey(ret))
		{
			do
			{
				ret = new ComponentIdentifier(localname+(compcnt++)+"@"+platformname); // Hack?!
			}
			while(adapters.containsKey(ret) || initinfos.containsKey(ret));
		}
		
		return ret;
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
		desc	= (CMSComponentDescription)getDescription(comp);
		desc.setState(state);			
		
		notifyListenersChanged(comp, desc);
	}

	//-------- IService interface --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		logger = agent.getLogger();
		componentfactory.startService(agent, sid.getResourceIdentifier()).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				SServiceProvider.getService(agent.getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExecutionService, Void>(ret)
				{
					public void customResultAvailable(IExecutionService result)
					{
						exeservice	= result;
						
//						SServiceProvider.getService(agent.getServiceContainer(), IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//							.addResultListener(createResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
//						{
//							public void customResultAvailable(IMarshalService result)
//							{
//								marshalservice	= result;
						
								SServiceProvider.getService(agent.getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(createResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
								{
									public void customResultAvailable(IMessageService result)
									{
										msgservice	= result;
										
										SServiceProvider.getService(agent.getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
											.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
										{
											public void customResultAvailable(IClockService result)
											{
												clockservice	= result;
										
												// add root adapter and register root component
												if(root!=null)
												{
													msgservice.getAddresses().addResultListener(createResultListener(new ExceptionDelegationResultListener<String[], Void>(ret)
													{
														public void customResultAvailable(String[] addresses)
														{
															((ComponentIdentifier)root.getComponentIdentifier()).setAddresses(addresses);
															adapters.put(root.getComponentIdentifier(), root);
															ret.setResult(null);
														}
													}));
												}
											}
										});	
									}
								}));
//							}
//						}));
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
//		System.out.println(": "+this);
//		this.adapters	= null;	// required for final cleanup command
//		this.ccs	= null;	// required for final cleanup command
//		this.cfs	= null;	// required for final cleanup command
//		this.logger	= null;	// required for final cleanup command
//		this.listeners	= null;	// required for final cleanup command
//		this.killresultlisteners	= null;	// required for final cleanup command
//		this.root	= null;	// required for final cleanup command
//		this.initinfos	= null;	// required for final cleanup command
		
		this.childcounts	= null;
		this.componentfactory	= null;
		this.exeservice	= null;
		this.agent	= null;
		this.factories	= null;
		this.localtypes	= null;
//		this.marshalservice	= null;
		this.msgservice	= null;
		
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
		return IFuture.DONE;
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
		InitInfo painfo = getParentInfo(cinfo);
		
		// Parent also still in init.
		if(painfo!=null && painfo.getModel()!=null)
		{
			pasuspend	= isInitSuspend(painfo.getInfo(), painfo.getModel());
		}
		
		// Parent already running.
		else
		{
			CMSComponentDescription	padesc = (CMSComponentDescription)getDescription(getParentIdentifier(cinfo));
			pasuspend = IComponentDescription.STATE_SUSPENDED.equals(padesc.getState());
		}
		// Suspend when set to suspend or when parent is also suspended or when specified in model.
//		boolean	debugging = lmodel.getProperty("debugging")==null? false: ((Boolean)lmodel.getProperty("debugging")).booleanValue();
		boolean	debugging = lmodel.getSuspend(cinfo.getConfiguration())==null? false: lmodel.getSuspend(cinfo.getConfiguration()).booleanValue();
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
		IComponentAdapter	adapter	= (IComponentAdapter)adapters.get(cid);
		// Hack? Allows components to getExternalAccess in init phase
		if(adapter==null)
		{
			InitInfo ii = getInitInfo(cid);
			if(ii!=null)
				adapter = ii.getAdapter();
		}
		return adapter!=null ? adapter.getDescription() : null;
	}
	
	//-------- service handling --------
	
	/**
	 *  Notify the cms listeners of a change.
	 */
	protected void notifyListenersChanged(final IComponentIdentifier cid, final IComponentDescription origdesc)
	{
		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new DefaultResultListener<IComponentDescription>()
		{
			public void resultAvailable(IComponentDescription newdesc)
			{
				ICMSComponentListener[]	alisteners;
				Set<ICMSComponentListener>	slisteners	= new HashSet<ICMSComponentListener>(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
				// todo: can be called after listener has (concurrently) deregistered
				
//				System.out.println("comp changed: "+desc+" "+listeners);
//				logger.info("Component changed: "+desc+" "+listeners);
				
				for(int i=0; i<alisteners.length; i++)
				{
					final ICMSComponentListener lis = alisteners[i];
					lis.componentChanged(newdesc).addResultListener(createResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("prob: "+exception);
							removeComponentListener(cid, lis);
						}
					}));
				}
			}
		}));
	}
	
	/**
	 *  Notify the cms listeners of a removal.
	 */
	protected void notifyListenersRemoved(final IComponentIdentifier cid, final IComponentDescription origdesc, final Map results)
	{
		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new IResultListener<IComponentDescription>()
		{
			public void resultAvailable(IComponentDescription newdesc)
			{
				ICMSComponentListener[]	alisteners;
				
				Set<ICMSComponentListener>	slisteners	= new HashSet<ICMSComponentListener>(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
				// todo: can be called after listener has (concurrently) deregistered
				
		//		System.out.println("comp changed: "+desc+" "+listeners);
		//		logger.info("Component changed: "+desc+" "+listeners);
				
				for(int i=0; i<alisteners.length; i++)
				{
					final ICMSComponentListener lis = alisteners[i];
					lis.componentRemoved(newdesc, results).addResultListener(createResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
		//					System.out.println("prob: "+exception);
							removeComponentListener(cid, lis);
						}
					}));
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				resultAvailable(origdesc);
			}
		}));
	}
	
	/**
	 *  Create result listener that tolerates when agent is null at shutdown.
	 */
	protected IResultListener createResultListener(IResultListener listener)
	{
		return agent==null? listener: agent.createResultListener(listener);
	}
	
	/**
	 *  Notify the cms listeners of an addition.
	 */
	protected void notifyListenersAdded(final IComponentIdentifier cid, final IComponentDescription origdesc)
	{
		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new DefaultResultListener<IComponentDescription>()
		{
			public void resultAvailable(IComponentDescription newdesc)
			{
				ICMSComponentListener[]	alisteners;
				Set<ICMSComponentListener>	slisteners	= new HashSet<ICMSComponentListener>(listeners.getCollection(null));
				slisteners.addAll(listeners.getCollection(cid));
				alisteners	= (ICMSComponentListener[])slisteners.toArray(new ICMSComponentListener[slisteners.size()]);
				// todo: can be called after listener has (concurrently) deregistered
				
		//		System.out.println("comp changed: "+desc+" "+listeners);
		//		logger.info("Component changed: "+desc+" "+listeners);
				
				for(int i=0; i<alisteners.length; i++)
				{
					final ICMSComponentListener lis = alisteners[i];
					lis.componentAdded(newdesc).addResultListener(createResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
		//					System.out.println("prob: "+exception);
							removeComponentListener(cid, lis);
						}
					}));
				}
			}
		}));
	}
	
	/**
	 *  Update a component description according to another one.
	 */
	protected IFuture<IComponentDescription> updateComponentDescription(final CMSComponentDescription origdesc)
	{
		final Future<IComponentDescription> ret = new Future<IComponentDescription>();
		
		if(msgservice==null)
		{
			ret.setResult(origdesc);
		}
		else
		{
			msgservice.updateComponentIdentifier(origdesc.getName())
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, IComponentDescription>(ret)
			{
				public void customResultAvailable(IComponentIdentifier newcid)
				{
					CMSComponentDescription newdesc = (CMSComponentDescription)((CMSComponentDescription)origdesc).clone();
					newdesc.setName(newcid);
					ret.setResult(newdesc);
				}
			}));
		}
			
		return ret;
	}
	
	/**
	 *  Get the remote component management system for a specific component id.
	 */
	protected IFuture<IComponentManagementService>	getRemoteCMS(final IComponentIdentifier cid)
	{
		final Future<IComponentManagementService>	ret	= new Future<IComponentManagementService>();
		SServiceProvider.getService(agent.getServiceContainer(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IRemoteServiceManagementService, IComponentManagementService>(ret)
		{
			public void customResultAvailable(IRemoteServiceManagementService rms)
			{
				rms.getServiceProxy(cid, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
	
	/**
	 *  Get the init info for a component identifier.
	 */
	protected InitInfo getInitInfo(IComponentIdentifier cid)
	{
		return (InitInfo)initinfos.get(cid);
	}
	
	/**
	 *  Put an init info.
	 */
	protected void putInitInfo(IComponentIdentifier cid, InitInfo info)
	{
		initinfos.put(cid, info);
	}
	
	/**
	 *  Remove an init info.
	 */
	protected InitInfo removeInitInfo(IComponentIdentifier cid)
	{
		return (InitInfo)initinfos.remove(cid);
	}
	
	/**
	 *  Struct that stores information about initing components.
	 */
	static class InitInfo
	{
		//-------- attributes --------
		
		// 0: description, 1: adapter, 2: creation info, 3: model, 4: initfuture, 5: component instance
		
		/** The component description. */
		protected IComponentDescription description;
		
		/** The adapter. */
		protected IComponentAdapter adapter;
		
		/** The creation info. */
		protected CreationInfo info;
		
		/** The model. */
		protected IModelInfo model;
		
		/** The init future. */
		protected Future<Void> initfuture;
		
		/** The component instance. */
		protected IComponentInstance instance;

		//-------- constructors --------
		
		/**
		 *  Create a new init info.
		 */
		public InitInfo(IComponentDescription description,
			IComponentAdapter adapter, CreationInfo info, IModelInfo model,
			Future<Void> initfuture, IComponentInstance instance)
		{
			this.description = description;
			this.adapter = adapter;
			this.info = info;
			this.model = model;
			this.initfuture = initfuture;
			this.instance = instance;
		}

		//-------- methods --------
		
		/**
		 *  Get the description.
		 *  @return The description.
		 */
		public IComponentDescription getDescription()
		{
			return description;
		}

		/**
		 *  Set the description.
		 *  @param description The description to set.
		 */
		public void setDescription(IComponentDescription description)
		{
			this.description = description;
		}

		/**
		 *  Get the adapter.
		 *  @return The adapter.
		 */
		public IComponentAdapter getAdapter()
		{
			return adapter;
		}

		/**
		 *  Set the adapter.
		 *  @param adapter The adapter to set.
		 */
		public void setAdapter(IComponentAdapter adapter)
		{
			this.adapter = adapter;
		}

		/**
		 *  Get the info.
		 *  @return The info.
		 */
		public CreationInfo getInfo()
		{
			return info;
		}

		/**
		 *  Set the info.
		 *  @param info The info to set.
		 */
		public void setInfo(CreationInfo info)
		{
			this.info = info;
		}

		/**
		 *  Get the model.
		 *  @return The model.
		 */
		public IModelInfo getModel()
		{
			return model;
		}

		/**
		 *  Set the model.
		 *  @param model The model to set.
		 */
		public void setModel(IModelInfo model)
		{
			this.model = model;
		}

		/**
		 *  Get the initfuture.
		 *  @return The initfuture.
		 */
		public Future<Void> getInitFuture()
		{
			return initfuture;
		}

		/**
		 *  Set the initfuture.
		 *  @param initfuture The initfuture to set.
		 */
		public void setInitFuture(Future<Void> initfuture)
		{
			this.initfuture = initfuture;
		}

		/**
		 *  Get the instance.
		 *  @return The instance.
		 */
		public IComponentInstance getInstance()
		{
			return instance;
		}

		/**
		 *  Set the instance.
		 *  @param instance The instance to set.
		 */
		public void setInstance(IComponentInstance instance)
		{
			this.instance = instance;
		}
	}
	
	/**
	 *  Entry that represents a lock for a component.
	 *  Is used to lock the parent while a child is created.
	 */
	class LockEntry
	{
		//-------- attributes --------
		
		/** The locked component. */
		protected IComponentIdentifier locked;
		
		/** The components that have a lock. */
		protected Set<String> lockers;
		
		/** The kill flag. */
		protected Future<Map<String, Object>> killfuture;
		
		//-------- constructors --------
		
		/**
		 *  Create a new lock entry.
		 */
		public LockEntry(IComponentIdentifier locked)
		{
			this.locked = locked;
		}
		
		//-------- methods --------
		
		/**
		 *  Add a locker id.
		 *  @param locker The locker id.
		 */
		public void addLocker(String locker)
		{
			if(lockers==null)
				lockers = new HashSet<String>();
			lockers.add(locker);
		}
		
		/**
		 *  Remove a locker id.
		 *  @param locker The locker id.
		 */
		public void removeLocker(String locker)
		{
			lockers.remove(locker);
			if(lockers.isEmpty() && killfuture!=null)
				destroyComponent(locked, killfuture);
		}
		
		/**
		 *  Get the locker count.
		 *  @return The number of lockers.
		 */
		public int getLockerCount()
		{
			return lockers==null? 0: lockers.size();
		}

		/**
		 *  Get the killfuture.
		 *  @return the killfuture.
		 */
		public Future<Map<String, Object>> getKillFuture()
		{
			return killfuture;
		}

		/**
		 *  Set the killfuture.
		 *  @param killfuture The killfuture to set.
		 */
		public void setKillFuture(Future<Map<String, Object>> killfuture)
		{
			this.killfuture = killfuture;
		}
	}

}
