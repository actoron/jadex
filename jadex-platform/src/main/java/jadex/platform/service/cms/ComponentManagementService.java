package jadex.platform.service.cms;

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

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.Cause;
import jadex.bridge.ComponentCreationException;
import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.FactoryFilter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.IInternalArgumentsResultsFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.component.impl.IInternalSubcomponentsFeature;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
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
import jadex.commons.future.FutureHelper;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.Tuple2Future;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.kernelbase.IBootstrapFactory;
import jadex.micro.annotation.Binding;

/**
 *  Abstract default implementation of component management service.
 */
@Service
public class ComponentManagementService implements IComponentManagementService
{
	//-------- attributes --------
	
	/** Flag to avoid double initialization. */
	protected boolean	running;
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The platform access. */
	protected IPlatformComponentAccess	access;
	
	/** The logger. */
	protected Logger logger;

	/** The components (id->component). */
	protected Map<IComponentIdentifier, IPlatformComponentAccess> components;
	
	/** The cleanup commands for the components (component id -> cleanup command). */
	protected Map<IComponentIdentifier, CleanupCommand> ccs;
	
	/** The cleanup futures for the components (component id -> cleanup future). */
	protected Map<IComponentIdentifier, IFuture<Map<String, Object>>> cfs;
	
	/** The listeners. */
	protected MultiCollection<IComponentIdentifier, ICMSComponentListener> listeners;
	
//	/** The execution service (cached to avoid using futures). */
//	protected IExecutionService	exeservice;
	
//	/** The message service (cached to avoid using futures). */
//	protected IMessageService	msgservice;
	
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
	
	/** The locked components (component are locked till init is finished,
	    i.e. if destroy is called during init it wait till lock is away). */
	protected Map<IComponentIdentifier, LockEntry> lockentries;
	
//	/** The time service. */
//	protected IClockService clockservice;
	
	/** Flag to enable unique id generation. */
	// Todo: move to platform data ?
	protected boolean uniqueids;
	
	/** The cid count. */
	protected Map<String, Integer> cidcounts;
	
    //-------- constructors --------

    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public ComponentManagementService(IPlatformComponentAccess access, IBootstrapFactory componentfactory, boolean uniqueids)
	{
    	this.access	= access;
		this.componentfactory = componentfactory;
		this.uniqueids = uniqueids;
		
//		this.components = SCollection.createHashMap();
		components = Collections.synchronizedMap(new HashMap<IComponentIdentifier, IPlatformComponentAccess>());
		PlatformConfiguration.putPlatformValue(access.getInternalAccess().getComponentIdentifier(), PlatformConfiguration.DATA_COMPONENTMAP, components);
		this.ccs = SCollection.createLinkedHashMap();
		this.cfs = SCollection.createLinkedHashMap();
//		this.logger = Logger.getLogger(AbstractComponentAdapter.getLoggerName(exta.getComponentIdentifier())+".cms");
		this.listeners = SCollection.createMultiCollection();
		this.initinfos = SCollection.createHashMap();
		this.childcounts = SCollection.createHashMap();
		this.localtypes	= new LRU<Tuple, String>(100);
		this.lockentries = SCollection.createHashMap();
		this.cidcounts = new HashMap<String, Integer>();
		
		putInitInfo(access.getInternalAccess().getComponentIdentifier(), new InitInfo(access, null, null));
	}
    
    //-------- IComponentManagementService interface --------
    
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	public IFuture<IModelInfo> loadComponentModel(final String filename, final IResourceIdentifier rid)
	{
//		if(filename!=null && filename.indexOf("Remote")!=-1)
//			System.out.println("cache miss: "+filename);
		
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		if(filename==null)
		{
			ret.setException(new IllegalArgumentException("Filename must not null"));
		}
		else
		{
			SServiceProvider.getService(agent, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<ILibraryService, IModelInfo>(ret)
			{
				public void customResultAvailable(final ILibraryService ls)
				{
					IFuture<IComponentFactory> fut = SServiceProvider.getService(agent, IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(filename, null, rid));
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
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String model, CreationInfo info)
	{
		return createComponent(null, model, info);
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String name, final String model, CreationInfo info)
	{
//		final Tuple2Future<IComponentIdentifier, Map<String, Object>> ret = new Tuple2Future<IComponentIdentifier, Map<String,Object>>();
		final Tuple2Future<IComponentIdentifier, Map<String, Object>> ret = (Tuple2Future<IComponentIdentifier, Map<String,Object>>)SFuture.getNoTimeoutFuture(Tuple2Future.class, agent);
		createComponent(name, model, info, new IResultListener<Collection<Tuple2<String,Object>>>()
		{
			public void resultAvailable(Collection<jadex.commons.Tuple2<String,Object>> result) 
			{
//				if(model.toString().indexOf("Feature")!=-1)
//					System.err.println("createComponent.resultAvailable: "+model+", "+result);
				ret.setSecondResultIfUndone(Argument.convertArguments(result));
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				ret.setExceptionIfUndone(exception);
			}
		}).addResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier result)
			{
				ret.setFirstResultIfUndone(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Create a new component on the platform.
	 *  This method allows for retrieving intermediate results of the component via
	 *  status events.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The status events of the components. Consists of CMSCreatedEvent, (CMSIntermediateResultEvent)*, CMSTerminatedEvent
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponent(CreationInfo info, String name, String model)
	{		
		final SubscriptionIntermediateFuture<CMSStatusEvent> ret = (SubscriptionIntermediateFuture)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		final IComponentIdentifier[] mycid = new IComponentIdentifier[1];
		final boolean[] terminate = new boolean[1];
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				if(mycid[0]!=null)
				{
					destroyComponent(mycid[0]);
				}
				else
				{
					terminate[0]	= true;
				}
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});

		createComponent(name, model, info, new IIntermediateResultListener<Tuple2<String, Object>>()
		{
			Map<String, Object> results = new HashMap<String, Object>();
			
			public void intermediateResultAvailable(Tuple2<String, Object> result)
			{
				ret.addIntermediateResultIfUndone(new CMSIntermediateResultEvent(mycid[0], result.getFirstEntity(), result.getSecondEntity()));
				results.put(result.getFirstEntity(), result.getSecondEntity());
			}
			
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
				if(result!=null)
				{
					for(Tuple2<String, Object> res: result)
					{
						intermediateResultAvailable(res);
					}
				}
				finished();
			}
			
			public void finished()
			{
				ret.addIntermediateResultIfUndone(new CMSTerminatedEvent(mycid[0], results));
				ret.setFinishedIfUndone();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		}).addResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier cid)
			{
				mycid[0] = cid;
				ret.addIntermediateResultIfUndone(new CMSCreatedEvent(cid));
				if(terminate[0])
				{
					destroyComponent(cid);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		
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
			return new Future<IComponentIdentifier>(new IllegalArgumentException("Error creating component: " + name + " : Modelname must not be null."));

//		System.out.println("create: "+name+" "+modelname+" "+agent.getComponentIdentifier());
		
//		if(name!=null && name.toLowerCase().indexOf("provider")!=-1)
//			System.out.println("create compo: "+modelname+" "+info);
		
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		final IComponentIdentifier creator = sc==null? null: sc.getCaller();
		final Cause curcause = sc==null? agent.getComponentDescription().getCause(): sc.getCause();
		
//		if(modelname.indexOf("jadex.platform.service.message.transport.ssltcpmtp.ProviderAgent")!=-1)
//			System.out.println("create: "+modelname);//+" "+info!=null? info.getResourceIdentifier(): "norid");
		
//		final DebugException	de	= new DebugException();
	
//		if(modelname.indexOf("Provider")!=-1)
//			System.out.println("create component: "+modelname+" "+name);
		
		final Future<IComponentIdentifier> inited = new Future<IComponentIdentifier>();
		final Future<Void> resfut = new Future<Void>();
		
		final CreationInfo cinfo = new CreationInfo(info);	// Dummy default info, if null. Must be cloned as localtype is set on info later.
		
		if(cinfo.getParent()!=null)
		{
			// Check if parent is killing itself -> no new child component, exception
			if(cfs.containsKey(cinfo.getParent()))
				return new Future<IComponentIdentifier>(new ComponentTerminatedException(cinfo.getParent() ,"Parent is killing itself. Child component creation no allowed."));
		}
		
		if(cinfo.getParent()!=null && isRemoteComponent(cinfo.getParent()))
		{				
			getRemoteCMS(cinfo.getParent()).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(inited)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					// todo: problem, the call will get a wrong caller due to IComponentIdentidier.LOCAL.get()
					// will deliver the platform (as this second call is performed by the cms itself)
					
					// Map remote subscription events to local result listener (avoids the need for listener as plain remote object)
					rcms.createComponent(cinfo, name, modelname).addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
					{
						Collection<Tuple2<String, Object>>	results;
						
						@Override
						public void intermediateResultAvailable(CMSStatusEvent result)
						{
							if(result instanceof CMSCreatedEvent)
							{
								inited.setResult(result.getComponentIdentifier());
							}
							else if(result instanceof CMSIntermediateResultEvent && resultlistener!=null)
							{
								CMSIntermediateResultEvent	ire	= (CMSIntermediateResultEvent)result;
								Tuple2<String, Object>	res	= new Tuple2<String, Object>(ire.getName(), ire.getValue());
								
								if(resultlistener instanceof IIntermediateResultListener)
								{
									IIntermediateResultListener<Tuple2<String, Object>>	reslis	= (IIntermediateResultListener<Tuple2<String, Object>>)resultlistener;
									reslis.intermediateResultAvailable(res);
								}
								else
								{
									if(results==null)
									{
										results	= new HashSet<Tuple2<String,Object>>();
									}
									
									results.add(res);
								}
							}
						}
						
						@Override
						public void resultAvailable(Collection<CMSStatusEvent> result)
						{
							assert false: "Should not happen"; 
						}
						
						@Override
						public void finished()
						{
							if(resultlistener!=null)
							{
								if(resultlistener instanceof IIntermediateResultListener)
								{
									((IIntermediateResultListener<Tuple2<String, Object>>)resultlistener).finished();
								}
								else
								{
									if(results==null)
									{
										results	= Collections.emptySet();
									}
									resultlistener.resultAvailable(results);
								}								
							}
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							if(!inited.setExceptionIfUndone(exception) && resultlistener!=null)
							{
								resultlistener.exceptionOccurred(exception);
							}
						}
					});
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
//				getAddresses().addResultListener(createResultListener(new ExceptionDelegationResultListener<String[], IComponentIdentifier>(inited)
//				{
//					public void customResultAvailable(final String[] addresses)
//					{
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
										getComponentFactory(model, cinfo, rid, false, false)
											.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentFactory, IComponentIdentifier>(inited)
										{
											public void customResultAvailable(final IComponentFactory factory)
											{
//												System.out.println("load: "+model+" "+rid);
												factory.loadModel(model, cinfo.getImports(), rid)
													.addResultListener(createResultListener(new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier>(inited)
												{
													public void customResultAvailable(final IModelInfo lmodel)
													{
														if(lmodel.getReport()!=null)
														{
															inited.setException(new ComponentCreationException("Errors loading model: "+model+"\n"+lmodel.getReport().getErrorText(), 
																ComponentCreationException.REASON_MODEL_ERROR));
														}
														else
														{
															factory.getComponentFeatures(lmodel)
																.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFeatureFactory>, IComponentIdentifier>(inited)
															{
																public void customResultAvailable(Collection<IComponentFeatureFactory> features)
																{
																	// Create id and adapter.
																	
																	final BasicComponentIdentifier cid;
																	
																	// check if system component is located in system tree
																	Map<String, Object> props = lmodel.getProperties();
																	
																	IComponentIdentifier pacid = getParentIdentifier(cinfo);
																	
																	boolean systemcomponent = "system".equals(name) && pacid.getParent()==null;
																	if(props.containsKey("system") && !"system".equals(name))
																	{
																		UnparsedExpression uexp = (UnparsedExpression)props.get("system");
																		IParsedExpression exp = SJavaParser.parseExpression(uexp, lmodel.getAllImports(), null); // todo: classloader
																		SimpleValueFetcher fet = new SimpleValueFetcher()
																		{
																			public Object fetchValue(String name) 
																			{
																				Object ret = null;
																				if("$config".equals(name) || "$configuration".equals(name))
																				{
																					if(cinfo.getConfiguration()!=null)
																					{
																						ret = cinfo.getConfiguration();
																					}
																					else
																					{
																						String[] cs = lmodel.getConfigurationNames();
																						if(cs!=null && cs.length>0)
																						{
																							ret = cs[0];
																						}
																					}
																				}
																				else if("$model".equals(name))
																				{
																					ret = lmodel;
																				}
																				return ret;
																			}
																		};
																		Boolean bool = (Boolean)exp.getValue(fet);
																		if(bool!=null && bool.booleanValue())// || (props.get("system").toString().indexOf("true")!=-1))
																		{
																			systemcomponent = true;
																			
																			// is system component, now check whether parent is ok
//																			boolean insystem = false;
//																			
//																			while(pacid.getParent()!=null && !insystem)
//																			{
//																				insystem = pacid.getLocalName().equals("system");
//																				pacid = pacid.getParent();
//																			}
//																			
//																			if(!insystem)
//																			{
////																				System.out.println("Relocating system component: "+name+" - "+modelname);
//																				logger.info("Relocating system component: "+name+" - "+modelname);
//																				ComponentIdentifier npa = new ComponentIdentifier("system", agent.getComponentIdentifier());
//																				cinfo.setParent(npa);
//																			}
																		}
																	}
																	// Check if system is used in service (one declared system service is enough for component being systemcomponent)
																	if(!systemcomponent)
																	{
//																		if(lmodel.getName().indexOf("Remote")!=-1)
//																			System.out.println("sdfsdfsd");
																		ProvidedServiceInfo[] psis = lmodel.getProvidedServices();
																		if(psis!=null)
																		{
																			for(ProvidedServiceInfo psi: psis)
																			{
																				// Hack cast
																				Class<?> iftype = psi.getType().getType(((ModelInfo)lmodel).getClassLoader());
																				systemcomponent = jadex.bridge.service.ServiceIdentifier.isSystemService(iftype);
																				if(systemcomponent)
																					break;
																			}
																		}
																	}
																	
																	// Lock the parent while creating
																	final String lockkey = SUtil.createUniqueId("lock");
																	LockEntry kt = lockentries.get(cinfo.getParent());
																	if(kt==null)
																	{
																		kt = new LockEntry(cinfo.getParent());
																		lockentries.put(cinfo.getParent(), kt);
																	}
																	kt.addLocker(lockkey);
																	inited.addResultListener(createResultListener(new IResultListener<IComponentIdentifier>()
																	{
																		public void resultAvailable(IComponentIdentifier result)
																		{
																			LockEntry kt = lockentries.get(cinfo.getParent());
																			if(kt!=null)
																			{
																				kt.removeLocker(lockkey);
																				if(kt.getLockerCount()==0)
																				{
																					lockentries.remove(cinfo.getParent());
																				}
																			}
																		}
																		public void exceptionOccurred(Exception exception)
																		{
																			LockEntry kt = lockentries.get(cinfo.getParent());
																			if(kt!=null)
																			{
																				kt.removeLocker(lockkey);
																				if(kt.getLockerCount()==0)
																				{
																					lockentries.remove(cinfo.getParent());
																				}
																			}
																		}
																	}));

																	final IInternalAccess pad = getParentComponent(cinfo);
																	IExternalAccess parent = pad.getExternalAccess();
																	pacid = parent.getComponentIdentifier();

																	String paname = pacid.getName().replace('@', '.');
																	
																	cid = (BasicComponentIdentifier)generateComponentIdentifier(name!=null? name: lmodel.getName(), paname);//, addresses);
																	
																	// Defer component services being found from registry
																	ServiceRegistry.getRegistry(access.getInternalAccess()).addExcludedComponent(cid);
																	
																	Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
																	Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
																	Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
																	Boolean sync = cinfo.getSynchronous()!=null? cinfo.getSynchronous(): lmodel.getSynchronous(cinfo.getConfiguration());
																	Boolean persistable = cinfo.getPersistable()!=null? cinfo.getPersistable(): lmodel.getPersistable(cinfo.getConfiguration());
																	PublishEventLevel moni = cinfo.getMonitoring()!=null? cinfo.getMonitoring(): lmodel.getMonitoring(cinfo.getConfiguration());
																	// Inherit monitoring from parent if null
																	if(moni==null && cinfo.getParent()!=null)
																	{
																		CMSComponentDescription desc = (CMSComponentDescription)getDescription(cinfo.getParent());
																		moni = desc.getMonitoring();
																	}
																	
//																	Cause cause = new Cause(curcause, cid.getName());
																	Cause cause = curcause;
																	// todo: how to do platform init so that clock is always available?
																	IClockService cs = getClockService0();
																	final CMSComponentDescription ad = new CMSComponentDescription(cid, lmodel.getType(), master!=null ? master.booleanValue() : false,
																		daemon!=null ? daemon.booleanValue() : false, autosd!=null ? autosd.booleanValue() : false, sync!=null ? sync.booleanValue() : false,
																		persistable!=null ? persistable.booleanValue() : false, moni,
																		lmodel.getFullName(), cinfo.getLocalType(), lmodel.getResourceIdentifier(), cs!=null? cs.getTime(): System.currentTimeMillis(), creator, cause, systemcomponent);
																	
//																	System.out.println("moni: "+moni+" "+lmodel.getFullName());
																	
																	// Use first configuration if no config specified.
																	String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
																		: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
																	final IPlatformComponentAccess	component	= new PlatformComponent();
																	ComponentCreationInfo	cci	= new ComponentCreationInfo(lmodel, config, cinfo.getArguments(), ad, cinfo.getProvidedServiceInfos(), cinfo.getRequiredServiceBindings());
																	component.create(cci, features);
																	if(resultlistener!=null)
																	{
																		IArgumentsResultsFeature	af	= component.getInternalAccess().getComponentFeature0(IArgumentsResultsFeature.class);
																		IResultListener<Collection<Tuple2<String, Object>>>	rl	= new IIntermediateResultListener<Tuple2<String, Object>>()
																		{
																			public void exceptionOccurred(final Exception exception)
																			{
																				// Wait for cleanup finished before posting results
																				cfs.get(cid).addResultListener(new IResultListener<Map<String,Object>>()
																				{
																					public void resultAvailable(java.util.Map<String,Object> result)
																					{
																						resultlistener.exceptionOccurred(exception);
																					}
																					
																					public void exceptionOccurred(Exception exception)
																					{
																						resultlistener.exceptionOccurred(exception);																						
																					}
																				});
																			}
																			
																			public void finished()
																			{
																				// Wait for cleanup finished before posting results
																				cfs.get(cid).addResultListener(new IResultListener<Map<String,Object>>()
																				{
																					public void resultAvailable(java.util.Map<String,Object> result)
																					{
																						Collection<Tuple2<String, Object>>	results	= new ArrayList<Tuple2<String,Object>>();
																						for(Map.Entry<String, Object> entry: result.entrySet())
																						{
																							results.add(new Tuple2<String, Object>(entry.getKey(), entry.getValue()));
																						}
																						resultlistener.resultAvailable(results);
																					}
																					
																					public void exceptionOccurred(Exception exception)
																					{
																						resultlistener.exceptionOccurred(exception);																																												
																					}
																				});
																			}
																			
																			public void intermediateResultAvailable(Tuple2<String, Object> result)
																			{
																				if(resultlistener instanceof IIntermediateResultListener)
																				{
																					((IIntermediateResultListener)resultlistener).intermediateResultAvailable(result);
																				}
																			}
																			
																			public void resultAvailable(Collection<Tuple2<String, Object>> result)
																			{
																				// shouldn't happen...
																				Thread.dumpStack();
																			}
																		}; 
																		if(af!=null)
																		{
																			af.subscribeToResults().addResultListener(rl);
																		}
																	}
																	
																	putInitInfo(cid, new InitInfo(component, cinfo, resfut));
																	
																	// Start regular execution of inited component
																	// when this component is the outermost component, i.e. with no parent
																	// or the parent is already running
																	final boolean	doinit	= cinfo.getParent()==null || getInitInfo(cinfo.getParent())==null;
																	
																	logger.info("Starting component: "+cid.getName());
							//										System.err.println("Pre-Init: "+cid);
																	
																	resfut.addResultListener(createResultListener(new IResultListener<Void>()
																	{
																		public void resultAvailable(Void result)
																		{
																			logger.info("Started component: "+cid.getName());

																			ServiceRegistry.getRegistry(access.getInternalAccess()).removeExcludedComponent(cid);
																			
		//																	System.out.println("created: "+ad);
																			
																			// Init successfully finished. Add description and adapter.
																			InitInfo	info	= getInitInfo(cid);
																			
																			// Init finished. Set to suspended until parent registration is finished.
																			// not set to suspend to allow other initing sibling components invoking services
				//															ad.setState(IComponentDescription.STATE_SUSPENDED);
																			
//																			System.out.println("adding cid: "+cid);
																			components.put(cid, info.getComponent());
																			// Removed in resumeComponent()
				//																initinfos.remove(cid);
																			
																			// Register component at parent.
																			addSubcomponent(pad, ad, lmodel)
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
																					
//																					if(modelname.indexOf("jadex.platform.service.message.transport.ssltcpmtp.ProviderAgent")!=-1)
//																						System.out.println("inited, return: "+cid);//+" "+info!=null? info.getResourceIdentifier(): "norid");
																					
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
																							removeInitInfo(cids.get(i));
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
																						
																						// Kill component if destroy called during init.
																						destroyComponent(cid, killfut);
																					}
																					else
																					{
																						if(doinit)
																						{
//																							System.out.println("start: "+cid);
																							resumeComponent(cid, true);//.addResultListener(listener)
																						}
																					}
																				}
																				
																				public void exceptionOccurred(Exception exception)
																				{
																					// Exception in parent during startup of subcomponent
																					// --> complete init, so parent can terminate.
																					if(exception instanceof ComponentTerminatedException)
																					{
																						notifyListenersAdded(cid, ad);
																						inited.setResult(cid);
																					}
																					else
																					{
																						logger.info("Starting component failed: "+cid+", "+exception);
																					}																					
																				}
																			}));								
																		}
																		
																		public void exceptionOccurred(final Exception exception)
																		{
																			logger.info("Starting component failed: "+cid+", "+exception);
																			
																			ServiceRegistry.getRegistry(access.getInternalAccess()).removeExcludedComponent(cid);
																			
//																			System.err.println("Starting component failed: "+cid+", "+exception);
//																			exception.printStackTrace();
			//																System.out.println("Ex: "+cid+" "+exception);
																			final Runnable	cleanup	= new Runnable()
																			{
																				public void run()
																				{
																					IPlatformComponentAccess	comp	= components.get(cid);
																					if(comp==null)
																					{
																						InitInfo	ii	= getInitInfo(cid);
																						assert ii!=null: "Should be either in 'components' or 'initinfos'.";
																						comp	= ii.getComponent();
																					}
																					
																					comp.getInternalAccess().getExternalAccess().killComponent(exception)
																						.addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, IComponentIdentifier>(inited)
																					{
																						@Override
																						public void customResultAvailable(Map<String, Object> result)
																						{
																							// Shouldn't happen.
																							Thread.dumpStack();
																						}																							
																					});
//																							new IResultListener<Map<String,Object>>()
//																					{
//																						
//																						@Override
//																						public void exceptionOccurred(Exception exception)
//																						{
//																							components.remove(cid);
//																							removeInitInfo(cid);
//
//																							if(resultlistener!=null)
//																							{
//																								resultlistener.exceptionOccurred(exception);
//																							}
//
//																							inited.setException(exception);
//																						}
//																					});
//																					exitDestroy(cid, ad, exception, null);
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
																	
																	// Create component and init.
																	component.init().addResultListener(createResultListener(new DelegationResultListener<Void>(resfut)));
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
//					}
//				}));
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
		
		try
		{
			// Hack for platform init
			ILibraryService libservice = SServiceProvider.getLocalService(agent, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
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
						IInternalAccess pad = getParentComponent(cinfo);
						IExternalAccess parent = pad.getExternalAccess();
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
		catch(ServiceNotFoundException s)
		{
			// Hack for platform init
			String	filename	= modelname;
			
			if(cinfo.getParent()!=null)
			{
				// Try to find file for local type.
				String	localtype	= modelname!=null ? modelname : cinfo.getLocalType();
				filename	= null;
				IInternalAccess pad = getParentComponent(cinfo);
				IExternalAccess parent = pad.getExternalAccess();
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
			}
			
			ret.setResult(filename);
		}
		
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
	 *  @param searched	True, when a search has already been done.
	 *  @return The component factory.
	 */
	protected IFuture<IComponentFactory> getComponentFactory(final String model, final CreationInfo cinfo, final IResourceIdentifier rid, final boolean searched, final boolean cachemiss)
	{
		final Future<IComponentFactory> ret = new Future<IComponentFactory>();

		// Search, if no cache available or not found in cache
		if((factories==null || cachemiss) && !searched)
		{
//			System.out.println("searching factories");
			
			IFuture<Collection<IComponentFactory>> fut = SServiceProvider.getServices(agent, IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM);
			fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, IComponentFactory>(ret)
			{
				public void customResultAvailable(Collection<IComponentFactory> facts)
				{
					if(!facts.isEmpty())
					{						
						// Reorder factories to assure that delegating multi loaders are last (if present).
						if(facts.size()>1)
						{
							List<IComponentFactory>	singles	= new ArrayList<IComponentFactory>();
							List<IComponentFactory>	multies	= new ArrayList<IComponentFactory>();
							for(IComponentFactory fac: facts)
							{
								if(fac.toString().toLowerCase().indexOf("multi")!=-1)
								{
									multies.add(fac);
								}
								else
								{
									singles.add(fac);
									// Remove fallback factory when first real factory is found.
									componentfactory = null;
								}
							}
							facts	= singles;
							facts.addAll(multies);
						}
					}
					factories = facts.isEmpty() ? null : facts;
					
					// Invoke again, now with up-to-date cache.
					getComponentFactory(model, cinfo, rid, true, false).addResultListener(new DelegationResultListener<IComponentFactory>(ret));
				}
			}));
		}
		
		// Cache available or recently searched.
		else
		{
//			System.out.println("create start2: "+model+" "+cinfo.getParent());
						
			selectComponentFactory(factories==null? null: factories.iterator(), model, cinfo, rid)
				.addResultListener(createResultListener(new DelegationResultListener<IComponentFactory>(ret)
			{
				public void exceptionOccurred(Exception exception)
				{
					// Todo: sometimes nullpointerexception is caused below (because agent is null???).
					if(!(exception instanceof ComponentCreationException))
					{
						System.out.println("factory ex: "+exception+", "+agent);
					}

					// If not found in cache but not yet searched, invoke again to start fresh search due to cache miss.
					if(!searched)
					{
						getComponentFactory(model, cinfo, rid, false, true)
							.addResultListener(createResultListener(new DelegationResultListener<IComponentFactory>(ret)));
					}
					
					// Otherwise give up.
					else
					{
						super.exceptionOccurred(exception);
					}
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
	 *  @param cinfo The creation info.
	 *  @param cl The classloader.
	 *  @return The component factory.
	 */
	protected IFuture<IComponentFactory> selectComponentFactory(final Iterator<IComponentFactory> factories, 
		final String model, final CreationInfo cinfo, final IResourceIdentifier rid)
	{
//		System.out.println("select factory: "+model+", "+SUtil.arrayToString(factories));
		
//		if(model.indexOf("boken")!=-1)
//			System.out.println("sdfsdf");
		
		final Future<IComponentFactory> ret = new Future<IComponentFactory>();
		
		if(factories!=null && factories.hasNext())
		{
			final IComponentFactory	factory	= factories.next();
			factory.isLoadable(model, cinfo.getImports(), rid)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<Boolean, IComponentFactory>(ret)
			{
				public void customResultAvailable(Boolean res)
				{
					if(res.booleanValue())
					{
						// If multi factory, clear cache and invoke again to obtain real factory.
						if(factory.toString().toLowerCase().indexOf("multi")!=-1)
						{
							ComponentManagementService.this.factories	= null;
							getComponentFactory(model, cinfo, rid, false, false)
								.addResultListener(new DelegationResultListener<IComponentFactory>(ret));
						}
						else
						{
							ret.setResult(factory);
						}
					}
					else if(factories.hasNext())
					{
						selectComponentFactory(factories, model, cinfo, rid)
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
//		System.out.println("fallback: "+model);
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
	 *  Get the parent component.
	 */	
	protected IInternalAccess getParentComponent(CreationInfo cinfo)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo);
		IPlatformComponentAccess component	= components.get(paid);
		if(component==null)
		{
			InitInfo	pinfo = getParentInfo(cinfo);
			
			// Hack!!! happens when parent is killed while trying to create subcomponent (todo: integrate locking for destroy and create of component structure)
			if(pinfo==null)
			{
				throw new ComponentTerminatedException(paid);
			}
			
			component = pinfo.getComponent();
		}
		
		return component.getInternalAccess();
	}
	
//	/**
//	 *  Get the desc of the parent component.
//	 */
//	protected CMSComponentDescription getParentDescription(CreationInfo cinfo)
//	{
//		final IComponentIdentifier paid = getParentIdentifier(cinfo);
//		CMSComponentDescription desc = adapters.containsKey(paid)
//			? (CMSComponentDescription)((IComponentAdapter)adapters.get(paid)).getDescription()
//			: (CMSComponentDescription)getParentInfo(cinfo).getDescription();
//		return desc;
//	}
		
	/**
	 *  Test if a component identifier is a remote component.
	 */
	protected boolean isRemoteComponent(IComponentIdentifier cid)
	{
		return !cid.getPlatformName().equals(agent.getComponentIdentifier().getName());
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
//		if(cid.getParent()==null)
//			System.out.println("---- !!!! ----- Killing platform ---- !!!! ----- "+cid.getName());
//		System.out.println("Terminating component: "+cid.getName());
		
//		ServiceCall sc = ServiceCall.getCurrentInvocation();
//		System.out.println("kill compo: "+cid);//+" "+(sc!=null? sc.getCaller(): "null"));
		
//		if(cid.toString().indexOf("MegaParallel")!=-1)
//			System.out.println("destroy: "+cid.getName());
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
//		System.out.println("destroy0: "+cid+" "+cfs.containsKey(cid)+" "+tmp.isDone());
//		Thread.currentThread().dumpStack();
		
		// If destroyComponent has not been called before
		if(!contains)
		{
			cfs.put(cid, tmp);
//			if(cid.getParent()==null)
//			{
//				tmp.addResultListener(new IResultListener<Map<String,Object>>()
//				{
//					public void resultAvailable(Map<String, Object> result)
//					{
//						System.out.println("res: "+result);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						System.out.println("ex: "+exception);
//					}
//				});
//			}
//			((CMSComponentDescription)getDescription(cid)).setState(IComponentDescription.STATE_TERMINATING);
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
//		else //if("Application".equals(getDescription(cid).getType()))
//			System.out.println("no destroy: "+contains+", "+locked);
		
//		if(cid.getParent()==null)
//		{
//			ret.addResultListener(new IResultListener<Map<String,Object>>()
//			{
//				public void resultAvailable(Map<String, Object> result)
//				{
//					System.out.println("kill platorm finished: "+cid.getName());
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("kill platform exception: "+cid.getName()+" "+exception);
//				}
//			});
//		}

		return ret;
	}

	/**
	 *  Internal destroy method that performs the actual work.
	 *	@param cid The component to destroy.
	 *  @param ret The future to be informed.
	 */
	protected void destroyComponent(final IComponentIdentifier cid,	final Future<Map<String, Object>> ret)
	{
//		System.out.println("kill: "+cid);
		
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
			InitInfo infos	= getInitInfo(cid);
//			IComponentAdapter adapter	= infos!=null ? infos.getAdapter() : (IComponentAdapter)adapters.get(cid);
			IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : components.get(cid);
			
//			if(adapter!=null && adapter.getDescription().getModelName().indexOf("Pool")==-1)
//				System.out.println("Terminating component: "+cid.getName());
			
			// Terminate component that is shut down during init.
			
			// todo:
			if(infos!=null && !infos.getInitFuture().isDone())
			{
				// Propagate failed component init.
//				if(comp!=null && comp.getException()!=null)
//				{
//					infos.getInitFuture().setException(adapter.getException());
//				}
				
				// Component terminated from outside: wait for init to complete, will be removed as cleanup future is registered (cfs).
//				else
				{
//					if(cid.toString().indexOf("Mandelbrot")!=-1)
//						System.out.println("Queued component termination during init: "+cid.getName());
					logger.info("Queued component termination during init: "+cid.getName());
				}
			}
			
			// Terminate normally inited component.
			else if(comp!=null)
			{				
				// Kill subcomponents
				logger.info("Terminating component structure: "+cid.getName());
				final CMSComponentDescription	desc = (CMSComponentDescription)((PlatformComponent)comp).getComponentDescription();
				final IComponentIdentifier[] achildren = desc.getChildren();
				
//				if(achildren.length>0)
//					System.out.println("kill childs start: "+cid+" "+achildren.length+" "+SUtil.arrayToString(achildren));
				
				destroyComponentLoop(cid, achildren, achildren.length-1).addResultListener(createResultListener(new IResultListener<List<Exception>>()
				{
					public void resultAvailable(List<Exception> result)
					{
//						if(achildren.length>0)
//							System.out.println("kill childs end: "+cid);
						
//						if(cid.toString().startsWith("Initiator"))
//							System.out.println("Terminated component structure: "+cid.getName());
						
						logger.info("Terminated component structure: "+cid.getName());
						CleanupCommand	cc	= null;
						IFuture<Void>	fut	= null;
						
						// Fetch comp again as component may be already killed (e.g. when autoshutdown).
						InitInfo infos	= getInitInfo(cid);
						IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : components.get(cid);
						if(comp!=null)
						{
//							if(cid.toString().indexOf("AutoTerminate")!=-1)
//								System.out.println("destroy1: "+cid.getName());
//										
							// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
							// todo: killcomponent should only be called once for each component?
							if(!ccs.containsKey(cid))
							{
//								if(cid.toString().indexOf("AutoTerminate")!=-1)
//									System.out.println("killing a: "+cid);
								
								cc	= new CleanupCommand(cid);
								ccs.put(cid, cc);
								logger.info("Terminating component: "+cid.getName());
								fut = comp.shutdown();								
							}
							else
							{
//								if(cid.toString().indexOf("AutoTerminate")!=-1)
//									System.out.println("killing b: "+cid);
								
								cc = (CleanupCommand)ccs.get(cid);
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
			else
			{
				cfs.remove(cid);
				ret.setException(new ComponentTerminatedException(cid, "Cannot kill, no such component."));
			}
		}
	}
	
	/**
	 *  Exit the destroy method by setting description state and resetting maps.
	 *  @return True, when somebody was notified.
	 */
	protected void	exitDestroy(IComponentIdentifier cid, IComponentDescription desc, Exception ex, Map<String, Object> results)
	{
//		Thread.dumpStack();
		Future<Map<String, Object>>	ret;
		ccs.remove(cid);
		ret	= (Future<Map<String, Object>>)cfs.remove(cid);

		if(desc instanceof CMSComponentDescription)
		{
			((CMSComponentDescription)desc).setState(IComponentDescription.STATE_TERMINATED);
		}
		if(ret!=null)
		{
			if(ex!=null)
			{
				ret.setExceptionIfUndone(ex);
			}
			else
			{
				ret.setResultIfUndone(results);
			}
		}
	}
	
	/**
	 *  Loop for destroying subcomponents.
	 */
	protected IFuture<List<Exception>> destroyComponentLoop(final IComponentIdentifier cid, final IComponentIdentifier[] achildren, final int i)
	{
		final Future<List<Exception>> ret = new Future<List<Exception>>();
		
//		System.out.println("destroy loop: "+cid+" "+i+"/"+achildren.length);
		
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
		IFuture<Void>	ret;
		
		if(isRemoteComponent(cid))
		{
			final Future<Void> fut = new Future<Void>();
			ret	= fut;
			getRemoteCMS(cid).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.suspendComponent(cid).addResultListener(createResultListener(new DelegationResultListener<Void>(fut)));
				}
			}));
			return ret;
		}
		else
		{
			final IPlatformComponentAccess comp = components.get(cid);
			if(comp!=null)
			{
				CMSComponentDescription desc = (CMSComponentDescription)((PlatformComponent)comp).getComponentDescription();
				if(IComponentDescription.STATE_ACTIVE.equals(desc.getState()))
				{
					// Suspend subcomponents
					IComponentIdentifier[] achildren = desc.getChildren();
					for(int i=0; i<achildren.length; i++)
					{
						IComponentDescription	cdesc	= getDescription(achildren[i]);
						if(IComponentDescription.STATE_ACTIVE.equals(cdesc.getState()))
						{
							suspendComponent(achildren[i]);	// todo: cascading suspend with wait.
						}
					}					
					desc.setState(IComponentDescription.STATE_SUSPENDED);
					notifyListenersChanged(cid, desc);
					
					ret	= IFuture.DONE;
				}
				else
				{
					ret	= new Future<Void>(new IllegalStateException("Component not active: "+cid));
				}
			}
			else
			{
				ret	= new Future<Void>(new ComponentNotFoundException("Component identifier not registered: "+cid));
			}
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
						final IPlatformComponentAccess adapter = components.get(cid);
						boolean	changed	= false;
						if(adapter==null && !initresume)	// Might be killed after init but before init resume
						{
							ret.setException(new ComponentNotFoundException("Component identifier not registered: "+cid));
						}
						else if(adapter!=null)
						{
							// Hack for startup.
							if(initresume)
							{
								// Not killed during init.
								if(!cfs.containsKey(cid))
								{
									InitInfo ii = removeInitInfo(cid);
		//							System.out.println("removed: "+cid+" "+ii);
									if(ii!=null)
									{
										boolean	suspend = isInitSuspend(ii.getInfo(), adapter.getInternalAccess().getModel());
										
										if(suspend)
										{
											desc.setState(IComponentDescription.STATE_SUSPENDED);
											changed	= true;
										}
									}
									
									adapter.body().addResultListener(adapter.getInternalAccess().getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											// Nop. keepalive is handled by component internally
										}
										
										public void exceptionOccurred(Exception exception)
										{
											if(!(exception instanceof ComponentTerminatedException)
												|| !((ComponentTerminatedException)exception).getComponentIdentifier().equals(adapter.getInternalAccess().getComponentIdentifier()))
											{
												adapter.getInternalAccess().killComponent(exception);
											}
										}
									}));
								}
									
								// Killed after init but before init resume -> execute queued destroy.
								else if(getInitInfo(cid)!=null)
								{
									removeInitInfo(cid);
									cfs.remove(cid);
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
									((IInternalExecutionFeature)adapter.getInternalAccess().getComponentFeature(IExecutionFeature.class)).wakeup();
								}
							}
							
							if(changed)
								notifyListenersChanged(cid, desc);
						
							ret.setResult(null);
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
	 *  Add a new component to its parent.
	 */
	protected IFuture<Void>	addSubcomponent(IInternalAccess pad, IComponentDescription ad, IModelInfo lmodel)
	{
		CMSComponentDescription padesc	= (CMSComponentDescription)pad.getComponentDescription();
		padesc.addChild(ad.getName());
		
//		if(padesc.isAutoShutdown() && !ad.isDaemon())
//		if(pas!=null && pas.booleanValue() && (dae==null || !dae.booleanValue()))
		// cannot check parent shutdown state because could be still uninited
		if(!ad.isDaemon())
		{
			Integer	childcount	= (Integer)childcounts.get(padesc.getName());
			int cc = childcount!=null ? childcount.intValue()+1 : 1;
			childcounts.put(padesc.getName(), Integer.valueOf(cc));
		}
		
		// Register component at parent.
		return ((IInternalSubcomponentsFeature)pad.getComponentFeature(ISubcomponentsFeature.class)).componentCreated(ad);//, lmodel);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> stepComponent(final IComponentIdentifier cid, final String stepinfo)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid))
		{
			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.stepComponent(cid, stepinfo).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			final PlatformComponent adapter = (PlatformComponent)components.get(cid);
			if(adapter!=null)
			{
//				if(stepinfo!=null)
//				{
//					CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
//					if(desc!=null)
//					{
//						desc.setStepInfo(stepinfo);
//					}
//				}
				
				((IInternalExecutionFeature)adapter.getComponentFeature(IExecutionFeature.class)).doStep(stepinfo)
					.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
			}
			else
			{
				ret.setException(new ComponentNotFoundException("Component identifier not registered: "+cid));
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
		listeners.add(comp, listener);
		return IFuture.DONE;
    }
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeComponentListener(IComponentIdentifier comp, ICMSComponentListener listener)
    {
		listeners.removeObject(comp, listener);
		return IFuture.DONE;
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
//			try
//			{
			boolean	killparent	= false;
//			IComponentAdapter adapter = null;
//			IComponentAdapter pad = null;
			CMSComponentDescription desc;
			Map<String, Object> results = null;
			logger.info("Terminated component: "+cid.getName());
//			if(cid.getParent()==null)
//				System.out.println("CleanupCommand: "+cid);
//			boolean shutdown = false;

//			System.out.println("CleanupCommand remove called for: "+cid);
			IPlatformComponentAccess	comp	= components.remove(cid);
			if(comp==null)
			{
				InitInfo	ii	= removeInitInfo(cid);
				assert ii!=null: "Should be either in 'components' or 'initinfos'.";
				comp	= ii.getComponent();
			}
			PlatformComponent pad = null;
			if(comp==null)
				throw new ComponentNotFoundException("Component Identifier not registered: "+cid);
			
//				if(cid.getName().indexOf("Peer")==-1)
//					System.out.println("removed adapter: "+adapter.getComponentIdentifier().getLocalName()+" "+cid+" "+adapters);
			
			desc	= (CMSComponentDescription)comp.getInternalAccess().getComponentDescription();
			
			IArgumentsResultsFeature	af	= comp.getInternalAccess().getComponentFeature0(IArgumentsResultsFeature.class); 
			results = af!=null ? af.getResults() : null;

//				desc.setState(IComponentDescription.STATE_TERMINATED);
//				ccs.remove(cid);
//				cfs.remove(cid);
			
			// Deregister destroyed component at parent.
			if(desc.getName().getParent()!=null)
			{
				// Stop execution of component. When root component services are already shutdowned.
//				cancel(comp);
//				exeservice.cancel(adapter);
				
				killparent = desc.isMaster();
				CMSComponentDescription padesc = (CMSComponentDescription)getDescription(desc.getName().getParent());
				if(padesc!=null)
				{
					padesc.removeChild(desc.getName());
//					if(pas!=null && pas.booleanValue() && (dae==null || !dae.booleanValue()))
					if(!desc.isDaemon())
//					if(padesc.isAutoShutdown() && !desc.isDaemon())
					{
						Integer	childcount	= (Integer)childcounts.get(padesc.getName());
//						assert childcount!=null && childcount.intValue()>0;
						if(childcount!=null)
						{
							int cc = childcount.intValue()-1;
							if(cc>0)
								childcounts.put(padesc.getName(), Integer.valueOf(cc));
							else
								childcounts.remove(padesc.getName());
//							System.out.println("childcount-: "+padesc.getName()+" "+cc);
						}
						// todo: could fail when parent is still in init phase. 
						// Should test for init phase and remember that it has to be killed.
						killparent = killparent || (padesc.isAutoShutdown() 
							&& (childcount==null || childcount.intValue()<=1));
					}
				}
				pad	= (PlatformComponent)components.get(desc.getName().getParent());
				
				// todo: wait for result?!
				if(pad!=null)
					((IInternalSubcomponentsFeature)pad.getComponentFeature(ISubcomponentsFeature.class)).componentRemoved(desc);
			}

			// Must be executed out of sync block due to deadlocks
			// agent->cleanupcommand->space.componentRemoved (holds adapter mon -> needs space mone)
			// space executor->general loop->distributed percepts->(holds space mon -> needs adapter mon for getting external access)
			
			// todo:
//			if(pad!=null)
//			{
//				try
//				{
//					pad.componentDestroyed(desc);
//				}
//				catch(ComponentTerminatedException cte)
//				{
//					// Parent just killed: ignore.
//				}
//			}
			// else parent has just been killed.
			
			// Use adapter exception before cleanup exception as it probably happened first.
			Exception	ex	= comp.getInternalAccess().getException()!=null ? comp.getInternalAccess().getException() : exception;
			
			exitDestroy(cid, desc, ex, results);

			notifyListenersRemoved(cid, desc, results);
			
			if(ex!=null)
			{
				// Unhandled component exception
				if(af!=null && ((IInternalArgumentsResultsFeature)af).hasListener())
				{
					// Delegated exception to some listener, only print info.
					comp.getInternalAccess().getLogger().info("Fatal error, component '"+cid+"' will be removed due to "+ex);
				}
				else
				{
					// No listener -> print exception.
					comp.getInternalAccess().getLogger().severe("Fatal error, component '"+cid+"' will be removed.\n"+SUtil.getExceptionStacktrace(ex));
				}
			}
			
//			System.out.println("CleanupCommand end.");
			
			// Terminate rescue threads when platform is killed (hack, starter should use kill listener, but listener isn't registered in cms)
			if(cid.getParent()==null)
			{
				// In case of stack compaction it has to be ensured that listeners are
				// notified before the rescue thread is terminated
				FutureHelper.notifyStackedListeners();
				Starter.shutdownRescueThread(cid);
			}
			
			// Kill parent is autoshutdown or child was master.
			else if(pad!=null && killparent)
			{
//				System.out.println("killparent: "+pad.getComponentIdentifier());
				destroyComponent(pad.getComponentIdentifier());
			}
			
			if(cid.getRoot().equals(cid))
			{
//				System.out.println("removed: "+cid);
				PlatformConfiguration.removePlatformMemory(cid);
			}
//		}
//		catch(Throwable t)
//		{
//			t.printStackTrace();
//		}
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
//		System.out.println("getExta: "+cid);
		
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
//		ret.addResultListener(new IResultListener<IExternalAccess>()
//		{
//			public void resultAvailable(IExternalAccess result)
//			{
//				if(result==null)
//				{
//					System.err.println("ea is null in cms!!! "+cid);
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//			}
//		});
		
		if(cid==null)
		{
			ret.setException(new IllegalArgumentException("Identifier is null."));
			return ret;
		}
		
		if(isRemoteComponent(cid))
		{
//			ServiceCall	sc	= ServiceCall.getCurrentInvocation();
//			System.out.println("getExternalAccess: remote "+sc);
			agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
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
			IPlatformComponentAccess component = null;
//				System.out.println("getExternalAccess: adapters");
			boolean delayed = false;
			component = components.get(cid);
			
			if(component==null)
			{
				// Hack? Allows components to getExternalAccess in init phase from parent but also from component itself
				InitInfo ii = getInitInfo(cid);
				if(ii!=null)
				{
//					if(!internal && (ii.getAdapter()==null || ii.getAdapter().isExternalThread())) // cannot work because of decoupling
					IComponentIdentifier caller = ServiceCall.getCurrentInvocation()!=null ? ServiceCall.getCurrentInvocation().getCaller() : null;
					if(internal || (caller!=null && (cid.equals(caller.getParent()) || cid.equals(caller))))
					{
//						System.out.println("getExternalAccess: not delayed");
						component = ii.getComponent();
					}
					else
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
									ret.setResult(components.get(cid).getInternalAccess().getExternalAccess());
								}
								catch(Exception e)
								{
									ret.setException(e);
								}
							}
						}));
					}
				}
			}
			
			if(component!=null)
			{
				try
				{
					ret.setResult(component.getInternalAccess().getExternalAccess());
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
			else if(!delayed)
			{
				ret.setException(new ComponentNotFoundException("No local component found for component identifier: "+cid));
			}
			
		}
		
//		ret.addResultListener(new IResultListener<IExternalAccess>()
//		{
//			public void resultAvailable(IExternalAccess result)
//			{
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.err.println("getExternalAccess() failed: "+agent+", "+exception);//SUtil.getExceptionStacktrace(exception));
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Find the class loader for a new (local) component.
	 *  Use parent component class loader for local parents
	 *  and current platform class loader for remote or no parents.
	 *  @param platform	The component id.
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
			getExternalAccess(ci.getParent()==null? agent.getComponentIdentifier(): ci.getParent(), true)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExternalAccess, IResourceIdentifier>(ret)
			{
				public void customResultAvailable(IExternalAccess ea)
				{
//					System.err.println("Model class loader: "+ea.getModel().getName()+", "+ea.getModel().getClassLoader());
//						classloadercache.put(ci.getParent(), ea.getModel().getClassLoader());
					ret.setResult(ea.getModel().getResourceIdentifier());
				}
				public void exceptionOccurred(Exception exception)
				{
					// External access of parent not found, because already terminated (hack!!! fix creation/destroy structure lock)
					if(ci.getParent()!=null)
					{
						exception 	= new ComponentTerminatedException(ci.getParent());
					}
					super.exceptionOccurred(exception);
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

//	/**
//	 *  Get the component adapter for a component identifier.
//	 *  @param aid The component identifier.
//	 *  @param listener The result listener.
//	 */
//    // Todo: Hack!!! remove
//	@Excluded
//	public IFuture<IComponentAdapter> getComponentAdapter(IComponentIdentifier cid)
//	{
//		return new Future<IComponentAdapter>(internalGetComponentAdapter(cid));
//	}
	
//	/**
//	 *  Get the component adapter for a component identifier.
//	 *  @param aid The component identifier.
//	 *  @param listener The result listener.
//	 */
//    // Todo: Hack!!! remove
//	@Excluded
//	public IComponentAdapter internalGetComponentAdapter(IComponentIdentifier cid)
//	{
//		IComponentAdapter ret;
//		ret = (IComponentAdapter)adapters.get(cid);
//		// Hack, to retrieve description from component itself in init phase
//		if(ret==null)
//		{
//			InitInfo ii= getInitInfo(cid);
//			if(ii!=null)
//				ret	= ii.getAdapter();
//		}
//		// Hack, to retrieve root adapter in bootstrapping phase.
//		if(cid.equals(root.getComponentIdentifier()))
//		{
//			ret	= root;
//		}
//		return ret;
//	}

	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param platform The component identifier.
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParentIdentifier(CreationInfo ci)
	{
		IComponentIdentifier ret = ci!=null && ci.getParent()!=null ? ci.getParent() : agent.getComponentIdentifier(); 
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
//		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
		
//		if(isRemoteComponent(cid))
//		{
//			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService rcms)
//				{
//					rcms.getParent(cid).addResultListener(createResultListener(new DelegationResultListener<IComponentIdentifier>(ret)));
//				}
//			}));
//		}
//		else
//		{
//			CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
//			ret.setResult(desc!=null? desc.getName().getParent(): null);
//		}
		
//		return ret;
	
		return new Future<IComponentIdentifier>(cid.getParent());
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
//			if(cid.getParent()==null)
//				System.out.println("getChildren: "+cid);
			
			IComponentIdentifier[] tmp = internalGetChildren(cid);
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
	 *  Get the children of a component.
	 */
	protected IComponentIdentifier[] internalGetChildren(final IComponentIdentifier cid)
	{
		IComponentIdentifier[] tmp;
		CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
//			System.out.println("desc: "+desc.getName()+" "+desc.hashCode());
		tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
			IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
//			System.out.println(getServiceIdentifier()+" "+desc.getName()+" "+SUtil.arrayToString(tmp));
		return tmp;
	}
	
//	/**
//	 *  Get the children components of a component.
//	 *  @param cid The component identifier.
//	 *  @return The children component identifiers.
//	 */
//	public IIntermediateFuture<IComponentIdentifier> getChildren(final IComponentIdentifier cid)
//	{
//		final IntermediateFuture<IComponentIdentifier>	ret	= new IntermediateFuture<IComponentIdentifier>();
//		
//		if(isRemoteComponent(cid))
//		{
//			getRemoteCMS(cid).addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<IComponentIdentifier>>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService rcms)
//				{
//					rcms.getChildren(cid).addResultListener(createResultListener(new IntermediateDelegationResultListener<IComponentIdentifier>(ret)));
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					super.exceptionOccurred(exception);
//				}
//			}));
//		}
//		else
//		{
//	//		System.out.println("getChildren: "+this+" "+isValid());
//			IComponentIdentifier[] tmp;
//			CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
////			System.out.println("desc: "+desc.getName()+" "+desc.hashCode());
//			tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
//			IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
////			System.out.println(getServiceIdentifier()+" "+desc.getName()+" "+SUtil.arrayToString(tmp));
//			ret.setResult(tmp);
//			
//			// Nice style to check for valid?
//	//		checkValid().addResultListener(new IResultListener()
//	//		{
//	//			public void resultAvailable(Object source, Object result)
//	//			{
//	//				CMSComponentDescription desc = (CMSComponentDescription)descs.get(cid);
//	//				IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
//	//					IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
//	//				ret.setResult(tmp);
//	//			}
//	//			
//	//			public void exceptionOccurred(Object source, Exception exception)
//	//			{
//	//				ret.setException(exception);
//	//			}
//	//		});
//		}
//		
//		return ret;
//	}

	
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
		
//		ret.addResultListener(new IResultListener<IComponentDescription[]>()
//		{
//			public void resultAvailable(IComponentDescription[] result)
//			{
//				System.out.println("found childs: "+cid+" "+SUtil.arrayToString(result));
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ClassCastException)
//					exception.printStackTrace();
//				System.out.println("exe: "+exception);
//			}
//		});
		
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
			CMSComponentDescription desc = (CMSComponentDescription)getDescription(cid);
			// Hack, to retrieve description from component itself in init phase
			if(desc==null)
			{
				InitInfo ii= getInitInfo(cid);
				if(ii!=null)
				{
					desc = (CMSComponentDescription)ii.getComponent().getInternalAccess().getComponentDescription();
				}
			}
						
			if(desc!=null)
			{
//				IMessageService ms = getMessageService0();
//				
//				if(ms==null)
//				{
					ret.setResult(desc);
//				}
//				else
//				{
//					final CMSComponentDescription	fdesc	= desc;
//					ms.updateComponentIdentifier(cid).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, IComponentDescription>(ret)
//					{
//						public void customResultAvailable(IComponentIdentifier rcid)
//						{
//							// addresses required for communication across platforms.
//							// ret.setName(refreshComponentIdentifier(aid));
//							fdesc.setName(rcid);
//							ret.setResult(fdesc);
//						}
//					});
//				}
			}
			else
			{
				ret.setException(new ComponentNotFoundException("No description available for: "+cid));
			}
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
		
		IComponentDescription[] ret = new IComponentDescription[components.size()];
		int i=0;
		for(Iterator<IPlatformComponentAccess> it=components.values().iterator(); i<ret.length; i++)
		{
			ret[i] = (IComponentDescription)((CMSComponentDescription)it.next().getInternalAccess().getComponentDescription()).clone();
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
		return new Future<IComponentIdentifier[]>((IComponentIdentifier[])components.keySet().toArray(new IComponentIdentifier[components.size()]));
		
//		final Future<IComponentIdentifier[]> fut = new Future<IComponentIdentifier[]>();
//		
//		getAddresses().addResultListener(createResultListener(
//			new ExceptionDelegationResultListener<String[], IComponentIdentifier[]>(fut)
//		{
//			public void customResultAvailable(String[] addresses)
//			{
//				IComponentIdentifier[] ret;
//				
//				ret = (IComponentIdentifier[])components.keySet().toArray(new IComponentIdentifier[components.size()]);
//				
//				if(ret.length>0)
//				{
//					if(!Arrays.equals(ret[0].getAddresses(), addresses))
//					{
//						// addresses required for inter-platform comm.
//						for(int i=0; i<ret.length; i++)
//							ret[i] = new ComponentIdentifier(ret[i].getName(), addresses);
////							ret[i] = refreshComponentIdentifier(ret[i]); // Hack!
//					}
//				}
//				
//				fut.setResult(ret);
//			}
//		}));
//		
//		return fut;
	}
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier()
	{
		return new Future<IComponentIdentifier>(agent.getComponentIdentifier());
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
			for(Iterator<IPlatformComponentAccess> it=components.values().iterator(); it.hasNext(); )
			{
				CMSComponentDescription	test = (CMSComponentDescription)it.next().getInternalAccess().getComponentDescription();
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
			IFuture<Collection<IComponentManagementService>> futi = SServiceProvider.getServices(agent, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL);
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
	
//	/**
//	 *  Create a component identifier that is allowed on the platform.
//	 *  @param name The base name.
//	 *  @return The component identifier.
//	 */
//	public IComponentIdentifier generateComponentIdentifier(String localname, String platformname, String[] addresses)
//	{
//		ComponentIdentifier ret = null;
//
//		if(platformname==null)
//			platformname = agent.getComponentIdentifier().getName();
//		ret = new ComponentIdentifier(localname+"@"+platformname, addresses);
//		
//		if(uniqueids || components.containsKey(ret) || initinfos.containsKey(ret))
//		{
//			String key = localname+"@"+platformname;
//			
//			do
//			{
//				Integer cnt = cidcounts.get(key);
//				if(cnt==null)
//				{
//					cidcounts.put(key, Integer.valueOf(1));
//					ret = new ComponentIdentifier(localname+"@"+platformname, addresses);
//				}
//				else
//				{
//					cidcounts.put(key, Integer.valueOf(cnt.intValue()+1));
//					ret = new ComponentIdentifier(localname+cnt+"@"+platformname, addresses); // Hack?!
//				}
//			}
//			while(components.containsKey(ret) || initinfos.containsKey(ret) || cfs.containsKey(ret));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier generateComponentIdentifier(String localname, String platformname)
	{
		BasicComponentIdentifier ret = null;

		if(platformname==null)
			platformname = agent.getComponentIdentifier().getName();
		ret = new BasicComponentIdentifier(localname+"@"+platformname);
		
		if(uniqueids || components.containsKey(ret) || getInitInfo(ret)!=null)
		{
			String key = localname+"@"+platformname;
			
			do
			{
				Integer cnt = cidcounts.get(key);
				if(cnt==null)
				{
					cidcounts.put(key, Integer.valueOf(1));
					ret = new BasicComponentIdentifier(localname+"@"+platformname);
				}
				else
				{
					cidcounts.put(key, Integer.valueOf(cnt.intValue()+1));
					ret = new BasicComponentIdentifier(localname+cnt+"@"+platformname); // Hack?!
				}
			}
			while(components.containsKey(ret) || getInitInfo(ret)!=null || cfs.containsKey(ret));
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

		if(!running)
		{
			// Avoid double initialization from persistence service.
			running	= true;
		
			logger = agent.getLogger();
			componentfactory.startService(agent, sid.getResourceIdentifier()).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					removeInitInfo(agent.getComponentIdentifier());
					components.put(agent.getComponentIdentifier(), access);
					
					ret.setResult(null);
//					SServiceProvider.getService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(createResultListener(new IResultListener<IMessageService>()
//					{
//						public void resultAvailable(IMessageService result)
//						{
//							msgservice	= result;
//							cont();
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							cont();
//						}
//						
//						protected void cont()
//						{
//							SServiceProvider.getService(agent, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//								.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
//							{
//								public void customResultAvailable(IClockService result)
//								{
//									clockservice	= result;
							
									// add root adapter and register root component
//									getAddresses().addResultListener(createResultListener(new ExceptionDelegationResultListener<String[], Void>(ret)
//									{
//										public void customResultAvailable(String[] addresses)
//										{
//											((ComponentIdentifier)agent.getComponentIdentifier()).setAddresses(addresses);
//											initinfos.remove(agent.getComponentIdentifier());
//											components.put(agent.getComponentIdentifier(), access);
//											ret.setResult(null);
//										}
//									}));
//								}
//							});	
//						}
//					}));
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
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
//		System.out.println("shutdown cms: "+agent.getComponentIdentifier()+" "+adapters);
		
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
		this.access	= null;
		this.componentfactory	= null;
//		this.exeservice	= null;
		this.agent	= null;
		this.factories	= null;
		this.localtypes	= null;
//		this.marshalservice	= null;
//		this.msgservice	= null;
		
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
		
//		Starter.removePlatformMemory(sid.getProviderId());
		
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
		if(painfo!=null && painfo.getComponent().getInternalAccess().getModel()!=null)
		{
			pasuspend	= isInitSuspend(painfo.getInfo(), painfo.getComponent().getInternalAccess().getModel());
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

//	/**
//	 *  Get the msgservice.
//	 *  @return the msgservice.
//	 */
//	public IMessageService getMessageService()
//	{
//		return msgservice;
//	}

//	/**
//	 *  Get the exeservice.
//	 *  @return the exeservice.
//	 */
//	public IExecutionService getExecutionService()
//	{
//		return exeservice;
//	}
	
	/**
	 *  Get the description for a component (if any).
	 */
	protected IComponentDescription	getDescription(IComponentIdentifier cid)
	{
		IPlatformComponentAccess	component	= components.get(cid);
		// Hack? Allows components to get description in init phase
		if(component==null)
		{
			InitInfo ii = getInitInfo(cid);
			if(ii!=null)
				component = ii.getComponent();
		}
		return component!=null ? component.getInternalAccess().getComponentDescription() : null;
	}
	
	//-------- service handling --------
	
	/**
	 *  Notify the cms listeners of a change.
	 */
	protected void notifyListenersChanged(final IComponentIdentifier cid, final IComponentDescription origdesc)
	{
//		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new DefaultResultListener<IComponentDescription>()
//		{
//			public void resultAvailable(IComponentDescription newdesc)
//			{
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
//					lis.componentChanged(newdesc).addResultListener(createResultListener(new IResultListener<Void>()
					lis.componentChanged(origdesc).addResultListener(createResultListener(new IResultListener<Void>()
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
//			}
//		}));
	}
	
	/**
	 *  Notify the cms listeners of a removal.
	 */
	protected void notifyListenersRemoved(final IComponentIdentifier cid, final IComponentDescription origdesc, final Map results)
	{
//		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new IResultListener<IComponentDescription>()
//		{
//			public void resultAvailable(IComponentDescription newdesc)
//			{
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
					try
					{
//						lis.componentRemoved(newdesc, results).addResultListener(createResultListener(new IResultListener<Void>()
						lis.componentRemoved(origdesc, results).addResultListener(createResultListener(new IResultListener<Void>()
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
					catch(Exception e)
					{
						removeComponentListener(cid, lis);
					}
				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				resultAvailable(origdesc);
//			}
//		}));
	}
	
	/**
	 *  Create result listener that tolerates when agent is null at shutdown.
	 */
	protected <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return agent==null? listener: agent.getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	/**
	 *  Notify the cms listeners of an addition.
	 */
	protected void notifyListenersAdded(final IComponentIdentifier cid, final IComponentDescription origdesc)
	{
//		updateComponentDescription((CMSComponentDescription)origdesc).addResultListener(createResultListener(new DefaultResultListener<IComponentDescription>()
//		{
//			public void resultAvailable(IComponentDescription newdesc)
//			{
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
//					lis.componentAdded(newdesc).addResultListener(createResultListener(new IResultListener<Void>()
					lis.componentAdded(origdesc).addResultListener(createResultListener(new IResultListener<Void>()
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
//			}
//		}));
	}
	
//	/**
//	 *  Update a component description according to another one.
//	 */
//	protected IFuture<IComponentDescription> updateComponentDescription(final CMSComponentDescription origdesc)
//	{
//		final Future<IComponentDescription> ret = new Future<IComponentDescription>();
//		
//		IMessageService ms = getMessageService0();
//		
//		if(ms==null)
//		{
//			ret.setResult(origdesc);
//		}
//		else
//		{
//			ms.updateComponentIdentifier(origdesc.getName())
//				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, IComponentDescription>(ret)
//			{
//				public void customResultAvailable(IComponentIdentifier newcid)
//				{
//					CMSComponentDescription newdesc = (CMSComponentDescription)((CMSComponentDescription)origdesc).clone();
//					newdesc.setName(newcid);
//					ret.setResult(newdesc);
//				}
//			}));
//		}
//			
//		return ret;
//	}
	
	/**
	 *  Get the remote component management system for a specific component id.
	 */
	protected IFuture<IComponentManagementService>	getRemoteCMS(final IComponentIdentifier cid)
	{
		final Future<IComponentManagementService>	ret	= new Future<IComponentManagementService>();
		
		ServiceQuery<IComponentManagementService> sq = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, Binding.SCOPE_GLOBAL, null, agent.getComponentIdentifier(), null);		
		sq.setPlatform(cid.getRoot());
		return ServiceRegistry.getRegistry(agent).searchServiceAsync(sq);
		
//		SServiceProvider.getService(agent, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IRemoteServiceManagementService, IComponentManagementService>(ret)
//		{
//			public void customResultAvailable(IRemoteServiceManagementService rms)
//			{
//				IFuture<IComponentManagementService> fut = rms.getServiceProxy(agent.getComponentIdentifier(), cid, new ClassInfo(IComponentManagementService.class), RequiredServiceInfo.SCOPE_PLATFORM, null);
//				fut.addResultListener(createResultListener(new DelegationResultListener<IComponentManagementService>(ret)));
//			}
//		}));
		
//		ret.addResultListener(new IResultListener<IComponentManagementService>() 
//		{
//			public void resultAvailable(IComponentManagementService result)
//			{
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
//				exception.printStackTrace();
//			}
//		});
		
//		return ret;
	}
	
//	/**
//	 *  Get the addresses.
//	 */
//	protected IFuture<String[]> getAddresses()
//	{
//		IMessageService ms = getMessageService0();
//		if(ms!=null)
//		{
//			return ms.getAddresses();
//		}
//		else
//		{
//			return new Future<String[]>((String[])null);
//		}
//	}
	
	/**
	 *  Get the clock service without exception if not found.
	 */
	protected IClockService getClockService0()
	{
		IClockService ret = null;
		
		try
		{
			ret = SServiceProvider.getLocalService(agent, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		}
		catch(ServiceNotFoundException e)
		{
		}
		
		return ret;
	}
	
//	/**
//	 *  Get the message service without exception if not found.
//	 */
//	protected IMessageService getMessageService0()
//	{
//		IMessageService ret = null;
//		
//		try
//		{
//			ret = SServiceProvider.getLocalService(agent, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//		}
//		catch(ServiceNotFoundException e)
//		{
//		}
//		
//		return ret;
//	}
	
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
		
		/** The component. */
		protected IPlatformComponentAccess component;
		
		/** The creation info. */
		protected CreationInfo info;
		
		/** The init future. */
		protected Future<Void> initfuture;
		
		//-------- constructors --------
		
		/**
		 *  Create a new init info.
		 */
		public InitInfo(IPlatformComponentAccess component, CreationInfo info, Future<Void> initfuture)
		{
			this.component = component;
			this.info = info;
			this.initfuture = initfuture;
		}

		//-------- methods --------
		
		/**
		 *  Get the adapter.
		 *  @return The adapter.
		 */
		public IPlatformComponentAccess getComponent()
		{
			return component;
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
		 *  Get the initfuture.
		 *  @return The initfuture.
		 */
		public Future<Void> getInitFuture()
		{
			return initfuture;
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
