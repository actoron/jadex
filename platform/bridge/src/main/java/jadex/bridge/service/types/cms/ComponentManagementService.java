package jadex.bridge.service.types.cms;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

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
//	protected Logger logger;

	/** The components (id->component). */
//	protected Map<IComponentIdentifier, IPlatformComponentAccess> components;
	
	/** The cleanup commands for the components (component id -> cleanup command). */
//	protected Map<IComponentIdentifier, CleanupCommand> ccs;
	
	/** The cleanup futures for the components (component id -> cleanup future). */
//	protected Map<IComponentIdentifier, IFuture<Map<String, Object>>> cfs;
	
	/** The listeners. */
//	protected MultiCollection<IComponentIdentifier, SubscriptionIntermediateFuture<CMSStatusEvent>> listeners;
	
//	/** The execution service (cached to avoid using futures). */
//	protected IExecutionService	exeservice;
	
//	/** The message service (cached to avoid using futures). */
//	protected IMessageService	msgservice;
	
	/** The init adapters and descriptions, i.e. adapters and desc of initing components, 
	 *  are only visible for the component and child components in their init. */
//	protected Map<IComponentIdentifier, InitInfo> initinfos;
	
	/** Number of non-daemon children for each autoshutdown component (cid->Integer). */
//	protected Map<IComponentIdentifier, Integer> childcounts;
	
	/**	The local filename cache (tuple(parent filename, child filename) -> local typename)*/
//	protected Map<Tuple, String> localtypes;
	
//	/** The cached factories. */
//	protected Collection<IComponentFactory> factories;
	
//	/** The cid count. */
//	protected Map<String, Integer> cidcounts;

	
	/** The bootstrap component factory. */
//	protected IBootstrapFactory componentfactory;
	
	/** The locked components (component are locked till init is finished,
	    i.e. if destroy is called during init it wait till lock is away). */
//	protected Map<IComponentIdentifier, LockEntry> lockentries;
	
//	/** The time service. */
//	protected IClockService clockservice;
	
	/** Flag to enable unique id generation. */
	// Todo: move to platform data ?
	protected boolean uniqueids;
		
    //-------- constructors --------

    /**
     *  Create a new component execution service.
     *  @param exta	The service provider.
     */
    public ComponentManagementService(IPlatformComponentAccess access, IBootstrapFactory componentfactory, boolean uniqueids)
	{
    	this.access	= access;
//		this.componentfactory = componentfactory;
		this.uniqueids = uniqueids;
		
//		this.components = SCollection.createHashMap();
//		components = (Map<IComponentIdentifier, IPlatformComponentAccess>)Starter.getPlatformValue(access.getInternalAccess().getId(), Starter.DATA_COMPONENTMAP);
		
//		Starter.putPlatformValue(access.getInternalAccess().getId(), Starter.DATA_COMPONENTMAP, components);
		
//		this.ccs = SCollection.createLinkedHashMap();
//		this.cfs = SCollection.createLinkedHashMap();
//		this.logger = Logger.getLogger(AbstractComponentAdapter.getLoggerName(exta.getComponentIdentifier())+".cms");
//		this.listeners = SCollection.createMultiCollection();
//		this.initinfos = SCollection.createHashMap();
//		this.childcounts = SCollection.createHashMap();
//		this.localtypes	= new LRU<Tuple, String>(100);
//		this.lockentries = SCollection.createHashMap();
//		this.cidcounts = new HashMap<String, Integer>();
		
		SComponentManagementService.putInitInfo(access.getInternalAccess().getId(), new InitInfo(access, null, null));
	}
    
    //-------- IComponentManagementService interface --------
    
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	@SuppressWarnings("unchecked")
	public IFuture<IModelInfo> loadComponentModel(final String filename, final IResourceIdentifier rid)
	{
		return SComponentManagementService.loadComponentModel(filename, rid, agent);
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
		return SComponentManagementService.createComponent(name, model, info, agent);
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
		return SComponentManagementService.createComponent(info, name, model, agent);
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param resultlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public IFuture<IComponentIdentifier> createComponent(final String oname, final String modelname, CreationInfo info, 
		final IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{			
		return SComponentManagementService.createComponent(oname, modelname, info, resultlistener, agent);
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid)
	{
		return SComponentManagementService.destroyComponent(cid, agent);
	}
	
	/**
	 *  Suspend the execution of an component.
	 *  @param cid The component identifier.
	 */
	public IFuture<Void> suspendComponent(final IComponentIdentifier cid)
	{
		return SComponentManagementService.suspendComponent(cid, agent);
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
		return SComponentManagementService.resumeComponent(cid, initresume, agent);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> stepComponent(final IComponentIdentifier cid, final String stepinfo)
	{
		return SComponentManagementService.stepComponent(cid, stepinfo, agent);
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
		return SComponentManagementService.setComponentBreakpoints(cid, breakpoints, agent);
	}

	//-------- internal methods --------
	
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getExternalAccess(cid, false, agent);
	}
	
	//-------- parent/child component accessors --------
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getChildren(cid, agent);
	}
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildrenDescriptions(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getChildrenDescriptions(cid, agent);
	}
	
	//--------- information methods --------
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture<IComponentDescription> getComponentDescription(final IComponentIdentifier cid)
	{
		return SComponentManagementService.getComponentDescription(cid, agent);
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions()
	{
		return SComponentManagementService.getComponentDescriptions(agent);
	}
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large. <- TODO and the reason is...?
	 */
	public IFuture<IComponentIdentifier[]> getComponentIdentifiers()
	{
		return SComponentManagementService.getComponentIdentifiers(agent);
	}
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier()
	{
		return new Future<IComponentIdentifier>(agent.getId());
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
		return SComponentManagementService.searchComponents(adesc, con, remote, agent, sid);
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
		
//			logger = agent.getLogger();
			((IBootstrapFactory)SComponentManagementService.getComponentFactory(agent.getId())).startService(agent, sid.getResourceIdentifier()).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					SComponentManagementService.removeInitInfo(agent.getId());
					SComponentManagementService.getComponents(agent.getId()).put(agent.getId(), access);
					
					ret.setResult(null);
//					agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
//							agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
		
//		this.childcounts	= null;
		this.access	= null;
//		this.componentfactory	= null;
//		this.exeservice	= null;
		this.agent	= null;
//		this.factories	= null;
//		this.localtypes	= null;
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
     *  Add a component listener for a specific component.
     *  The listener is registered for component changes.
     *  @param cid	The component to be listened.
     */
    public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(final IComponentIdentifier cid)
    {
    	return SComponentManagementService.listenToComponent(cid, agent);
    }
	
	/**
     *  Add a component listener for all components.
     *  The listener is registered for component changes.
     */
    public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll()
    {
    	return SComponentManagementService.listenToComponent(null, agent);
    }
	
}
