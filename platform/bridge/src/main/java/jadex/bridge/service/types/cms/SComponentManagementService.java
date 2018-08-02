package jadex.bridge.service.types.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.component.impl.IInternalSubcomponentsFeature;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSCreatedEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSIntermediateResultEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSTerminatedEvent;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.ResourceInfo;
import jadex.commons.SFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.commons.future.Tuple2Future;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Static CMS methods.
 *
 */
public class SComponentManagementService
{
	/**
	 *  Gets the external access of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return External Access.
	 */
	public static final IExternalAccess getLocalExternalAccess(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getComponents(platform).get(cid);
		if (comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getExternalAccess();
	}
	
	/**
	 *  Gets the classloader of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return ClassLoader.
	 */
	public static final ClassLoader getLocalClassLoader(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getComponents(platform).get(cid);
		if (comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getClassLoader();
	}
	
//	/**
//	 *  Gets a local component map.
//	 *  
//	 *  @param platform Platform ID.
//	 *  @return The component map.
//	 */
//	public static final Map<IComponentIdentifier, IPlatformComponentAccess> getComponentMap(IComponentIdentifier platform)
//	{
//		@SuppressWarnings("unchecked")
//		Map<IComponentIdentifier, IPlatformComponentAccess> ret = (Map<IComponentIdentifier, IPlatformComponentAccess>) Starter.getPlatformValue(platform, Starter.DATA_COMPONENTMAP);
//		if (ret == null)
//			throw new IllegalArgumentException("Platform not found: " + platform);
//		return ret;
//	}
	
	public static Map<IComponentIdentifier, IPlatformComponentAccess> getComponents(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, IPlatformComponentAccess>)Starter.getPlatformValue(cid, Starter.DATA_COMPONENTMAP);
	}
	
	public static Map<IComponentIdentifier, InitInfo> getInitInfos(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, InitInfo>)Starter.getPlatformValue(cid, Starter.DATA_INITINFOS);
	}
	
	public static Map<IComponentIdentifier, Integer> getChildCounts(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, Integer>)Starter.getPlatformValue(cid, Starter.DATA_CHILDCOUNTS);
	}
	
	public static Map<IComponentIdentifier, CleanupCommand> getCleanupCommands(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, CleanupCommand>)Starter.getPlatformValue(cid, Starter.DATA_CLEANUPCOMMANDS);
	}
	
	public static Map<IComponentIdentifier, IFuture<Map<String, Object>>> getCleanupFutures(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, IFuture<Map<String, Object>>>)Starter.getPlatformValue(cid, Starter.DATA_CLEANUPFUTURES);
	}
	
	public static  Map<Tuple, String> getLocalTypes(IComponentIdentifier cid)
	{
		/**	The local filename cache (tuple(parent filename, child filename) -> local typename)*/
		return (Map<Tuple, String>)Starter.getPlatformValue(cid, Starter.DATA_LOCALTYPES);
	}
	
	public static  Map<String, Integer> getCidCounts(IComponentIdentifier cid)
	{
		return (Map<String, Integer>)Starter.getPlatformValue(cid, Starter.DATA_CIDCOUNTS);
	}
	
	public static Map<IComponentIdentifier, LockEntry> getLockEntries(IComponentIdentifier cid)
	{
		/** The locked components (component are locked till init is finished,
	    i.e. if destroy is called during init it wait till lock is away). */
		return (Map<IComponentIdentifier, LockEntry>)Starter.getPlatformValue(cid, Starter.DATA_LOCKENTRIES);
	}
	
	public static Map<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> getListeners(IComponentIdentifier cid)
	{
		return (Map<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>>)Starter.getPlatformValue(cid, Starter.DATA_CMSLISTENERS);
	}
	
	public static IComponentFactory getComponentFactory(IComponentIdentifier cid)
	{
		return (IComponentFactory)Starter.getPlatformValue(cid, Starter.DATA_BOOTSTRAPFACTORY);
	}
	
	public static void removeComponentFactory(IComponentIdentifier cid)
	{
		Starter.putPlatformValue(cid, Starter.DATA_BOOTSTRAPFACTORY, null);
	}
	
	/**
	 *  Get the description for a component (if any).
	 */
	public static IComponentDescription	getDescription(IComponentIdentifier cid)
	{
		if(cid==null)
			return null;
		
		IPlatformComponentAccess	component	= getComponents(cid).get(cid);
		// Hack? Allows components to get description in init phase
		if(component==null)
		{
			InitInfo ii = getInitInfos(cid).get(cid);
			if(ii!=null)
				component = ii.getComponent();
		}
		return component!=null ? component.getInternalAccess().getDescription() : null;
	}
	
	/**
	 *  Exit the destroy method by setting description state and resetting maps.
	 *  @return True, when somebody was notified.
	 */
	public static void	exitDestroy(IComponentIdentifier cid, IComponentDescription desc, Exception ex, Map<String, Object> results)
	{
//		Thread.dumpStack();
		Future<Map<String, Object>>	ret;
		getCleanupCommands(cid).remove(cid);
		ret	= (Future<Map<String, Object>>)getCleanupFutures(cid).remove(cid);

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
     *  Add a component listener for all components.
     *  The listener is registered for component changes.
     */
    public static ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll(IInternalAccess agent)
    {
    	return listenToComponent(null, agent);
    }
	
	/**
     *  Add a component listener for a specific component.
     *  The listener is registered for component changes.
     *  @param cid	The component to be listened.
     */
    public static ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(final IComponentIdentifier cid, IInternalAccess agent)
    {
    	final SubscriptionIntermediateFuture<CMSStatusEvent>	ret	= new SubscriptionIntermediateFuture<IComponentManagementService.CMSStatusEvent>();
    	SFuture.avoidCallTimeouts(ret, agent);
    	
//    	if(getListeners()==null)
//    		listeners	= new MultiCollection<IComponentIdentifier, SubscriptionIntermediateFuture<CMSStatusEvent>>();
    	// todo: make this transactional (with features this could be broken)
    	Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> col = getListeners(cid).get(cid);
    	if(col==null)
    	{
    		col = new ArrayList<SubscriptionIntermediateFuture<CMSStatusEvent>>();
    		getListeners(cid).put(cid, col);
    	}
    	col.add(ret);
    	
    	ret.setTerminationCommand(new TerminationCommand()
    	{
    		@Override
    		public void terminated(Exception reason)
    		{
    			Map<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> lis = getListeners(cid);
    			Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> col = lis.get(cid);
    			if(col!=null)
    				col.remove(ret);
    		}
    	});
    	
    	return ret;    	
    }
    
    /**
	 *  Notify the cms listeners of an addition.
	 */
	public static void notifyListenersAdded(IComponentDescription desc)
	{
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	slisteners = getListeners(desc.getName()).get(desc.getName());
		
		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: SUtil.notNull(slisteners))
		{
			sub.addIntermediateResult(new CMSCreatedEvent(desc));
		}
	}
    
    /**
	 *  Notify the cms listeners of a change.
	 */
	public static void notifyListenersChanged(IComponentDescription desc)
	{
		Map<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> lis = getListeners(desc.getName());
		for(Map.Entry<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> entry: lis.entrySet())
		{
			for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: SUtil.notNull(entry.getValue()))
			{
				sub.addIntermediateResult(new CMSStatusEvent(desc));
			}
		}
//		Set<SubscriptionIntermediateFuture<CMSStatusEvent>>	slisteners	= new LinkedHashSet<SubscriptionIntermediateFuture<CMSStatusEvent>>().getColl(null));
//		slisteners.addAll(getListeners(desc.getName()).getCollection(desc.getName()));
//		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: slisteners)
//		{
//			sub.addIntermediateResult(new CMSStatusEvent(desc));
//		}
	}
	
	/**
	 *  Notify the cms listeners of a removal.
	 */
	public static void notifyListenersRemoved(IComponentDescription desc, Map<String, Object> results)
	{
		Map<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> lis = getListeners(desc.getName());
		for(Map.Entry<IComponentIdentifier, Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>> entry: lis.entrySet())
		{
			for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: SUtil.notNull(entry.getValue()))
			{
				sub.addIntermediateResult(new CMSTerminatedEvent(desc, results));
			}
		}
		
		// remove the listeners of the terminated component
		getListeners(desc.getName()).remove(desc.getName());	// remove(!) subscriptions for termination event
	}
	
	
	/**
	 *  Find the file name and local component type name
	 *  for a component to be started.
	 */
	protected static IFuture<Tuple2<String, ClassLoader>> resolveFilename(final String modelname, final CreationInfo cinfo, final IResourceIdentifier rid, IInternalAccess agent)
	{
		final Future<Tuple2<String, ClassLoader>> ret = new Future<Tuple2<String, ClassLoader>>();
		
		ILibraryService libservice = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		// Hack!!! May be null on platform init
		if(libservice!=null)
		{
			libservice.getClassLoader(rid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<ClassLoader, Tuple2<String, ClassLoader>>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					String filename = modelname;
					
					if(cinfo.getParent()!=null)
					{
						// Try to find file for local type.
						String localtype = modelname!=null ? modelname : cinfo.getLocalType();
						filename = null;
						IInternalAccess pad = getParentComponent(cinfo, agent);
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
							if(SComponentManagementService.getLocalTypes(agent.getId()).containsKey(key))
							{
								cinfo.setLocalType((String)SComponentManagementService.getLocalTypes(agent.getId()).get(key));
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
								SComponentManagementService.getLocalTypes(agent.getId()).put(key, cinfo.getLocalType());
				//				System.out.println("Local type: "+cinfo.getLocalType()+", "+pad.getComponentIdentifier());
							}
						}
					}
					
					ret.setResult(new Tuple2<String, ClassLoader>(filename, cl));
				}
			}));
		}
		else
		{
			// Hack for platform init
			String	filename	= modelname;
			
			if(cinfo.getParent()!=null)
			{
				// Try to find file for local type.
				String	localtype	= modelname!=null ? modelname : cinfo.getLocalType();
				filename = null;
				IInternalAccess pad = getParentComponent(cinfo, agent);
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
					filename = modelname;
				}
			}
			
			ret.setResult(new Tuple2<String, ClassLoader>(filename, null));
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
	protected static IFuture<IComponentFactory> getComponentFactory(final String model, final CreationInfo cinfo, final IResourceIdentifier rid, final boolean searched, final boolean cachemiss, IInternalAccess agent)
	{
		Future<IComponentFactory> ret = new Future<>();
		
		FactoryFilter ff = new FactoryFilter(model, cinfo==null? null: cinfo.getImports(), rid);
		SComponentFactory.getFactory(ff, agent).addResultListener(new DelegationResultListener<IComponentFactory>(ret)
		{
			@Override
			public void customResultAvailable(IComponentFactory result)
			{
//				System.out.println("found: "+model+" "+result);
				// do not use fallback as soon as first real can be found
//				if(!result.equals(componentfactory))
//				{
					removeComponentFactory(agent.getId());
//					System.out.println("deleting fallback factory: "+model);
//				}
				super.customResultAvailable(result);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				if(getComponentFactory(agent.getId())!=null)
				{
					ff.filter(getComponentFactory(agent.getId())).addResultListener(new IResultListener<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							if(result.booleanValue())
								ret.setResult(getComponentFactory(agent.getId()));
							else
								ret.setException(new ServiceNotFoundException("No component factory found for: "+model));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(new ServiceNotFoundException("No component factory found for: "+model));
						}
					});
				}
				else
				{
					ret.setException(new ServiceNotFoundException("No component factory found for: "+model));
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test if factory is a multi factory.
	 */
	protected static boolean isMultiFactory(IComponentFactory fac)
	{
//		if(fac.toString().toLowerCase().indexOf("multi")!=-1)
		return ((IService)fac).getId().getProviderId().getLocalName().indexOf("multi")!=-1;
	}
	
	/**
	 *  Get the info of the parent component.
	 */
	protected static InitInfo getParentInfo(CreationInfo cinfo, IInternalAccess agent)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo, agent);
		return getInitInfo(paid);
	}
	
	/**
	 *  Get the parent component.
	 */	
	protected static IInternalAccess getParentComponent(CreationInfo cinfo, IInternalAccess agent)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo, agent);
		IPlatformComponentAccess component	= SComponentManagementService.getComponents(agent.getId()).get(paid);
		if(component==null)
		{
			InitInfo	pinfo = getParentInfo(cinfo, agent);
			
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
	public static boolean isRemoteComponent(IComponentIdentifier cid, IInternalAccess agent)
	{
		return !cid.getPlatformName().equals(agent.getId().getName());
	}
	
	/**
	 *  Get the init info for a component identifier.
	 */
	public static InitInfo getInitInfo(IComponentIdentifier cid)
	{
		return (InitInfo)SComponentManagementService.getInitInfos(cid).get(cid);
	}
	
	/**
	 *  Put an init info.
	 */
	public static void putInitInfo(IComponentIdentifier cid, InitInfo info)
	{
		SComponentManagementService.getInitInfos(cid).put(cid, info);
	}
	
	/**
	 *  Remove an init info.
	 */
	public static InitInfo removeInitInfo(IComponentIdentifier cid)
	{
		return (InitInfo)SComponentManagementService.getInitInfos(cid).remove(cid);
	}
	
	/**
	 *  Create result listener that tolerates when agent is null at shutdown.
	 */
	protected static <T> IResultListener<T> createResultListener(IInternalAccess agent, IResultListener<T> listener)
	{
		return agent==null? listener: agent.getFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	/**
	 *  Get the remote component management system for a specific component id.
	 */
	protected static IFuture<IComponentManagementService> getRemoteCMS(IInternalAccess agent, final IComponentIdentifier cid)
	{
//		final Future<IComponentManagementService>	ret	= new Future<IComponentManagementService>();
//		ServiceQuery<IComponentManagementService> query = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, Binding.SCOPE_PLATFORM, null, cid, null);
//		 ((IInternalRemoteExecutionFeature)agent.getComponentFeature(IRemoteExecutionFeature.class))
//			.executeRemoteSearch(cid, query).addResultListener(new ExceptionDelegationResultListener<Collection<IComponentManagementService>, IComponentManagementService>(ret)
//		{
//			@Override
//			public void customResultAvailable(Collection<IComponentManagementService> result) throws Exception
//			{
//				assert result!=null && !result.isEmpty();
//				ret.setResult(result.iterator().next());
//			}
//		});
//		return ret;

		ServiceQuery<IComponentManagementService> sq = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		sq.setPlatform(cid.getRoot());
		return agent.getFeature(IRequiredServicesFeature.class).searchService(sq);
	}
	
	/**
	 *  Test if a component should be suspended after init is done.
	 *  @param cinfo	The creation info.
	 *  @param lmodel	The model of the component.
	 *  @return	True, if the component should be suspended
	 */
	protected static boolean isInitSuspend(CreationInfo cinfo, IModelInfo lmodel, IInternalAccess agent)
	{
		boolean pasuspend = false;
		InitInfo painfo = getParentInfo(cinfo, agent);
		
		// Parent also still in init.
		if(painfo!=null && painfo.getComponent().getInternalAccess().getModel()!=null)
		{
			pasuspend	= isInitSuspend(painfo.getInfo(), painfo.getComponent().getInternalAccess().getModel(), agent);
		}
		
		// Parent already running.
		else
		{
			CMSComponentDescription	padesc = (CMSComponentDescription)SComponentManagementService.getDescription(getParentIdentifier(cinfo, agent));
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
	 *  Get the children of a component.
	 */
	protected static IComponentIdentifier[] internalGetChildren(final IComponentIdentifier cid)
	{
		IComponentIdentifier[] tmp;
		CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
//			System.out.println("desc: "+desc.getName()+" "+desc.hashCode());
		tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
			IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
//			System.out.println(getId()+" "+desc.getName()+" "+SUtil.arrayToString(tmp));
		return tmp;
	}
	
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	public void	setComponentState(IComponentIdentifier comp, String state)
	{
		assert IComponentDescription.STATE_SUSPENDED.equals(state) : "wrong state: "+comp+", "+state;
		
		CMSComponentDescription	desc = null;
		desc	= (CMSComponentDescription)SComponentManagementService.getDescription(comp);
		desc.setState(state);			
		
		SComponentManagementService.notifyListenersChanged(desc);
	}
	
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public static IComponentIdentifier generateComponentIdentifier(String localname, String platformname, IInternalAccess agent, boolean uniqueids)
	{
		BasicComponentIdentifier ret = null;

		if(platformname==null)
			platformname = agent.getId().getName();
		ret = new BasicComponentIdentifier(localname+"@"+platformname);
		
		if(uniqueids || SComponentManagementService.getComponents(agent.getId()).containsKey(ret) || getInitInfo(ret)!=null)
		{
			String key = localname+"@"+platformname;
			
			do
			{
				Integer cnt = SComponentManagementService.getCidCounts(agent.getId()).get(key);
				if(cnt==null)
				{
					SComponentManagementService.getCidCounts(agent.getId()).put(key, Integer.valueOf(1));
					ret = new BasicComponentIdentifier(localname+"@"+platformname);
				}
				else
				{
					SComponentManagementService.getCidCounts(agent.getId()).put(key, Integer.valueOf(cnt.intValue()+1));
					ret = new BasicComponentIdentifier(localname+cnt+"@"+platformname); // Hack?!
				}
			}
			while(SComponentManagementService.getComponents(agent.getId()).containsKey(ret) || getInitInfo(ret)!=null || SComponentManagementService.getCleanupFutures(agent.getId()).containsKey(ret));
		}
		
		return ret;
	}
	
	
	
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public static IFuture<IComponentDescription[]> searchComponents(final IComponentDescription adesc, final ISearchConstraints con, boolean remote, IInternalAccess agent, IServiceIdentifier sid)
	{
		final Future<IComponentDescription[]> fut = new Future<IComponentDescription[]>();
		
//		System.out.println("search: "+components);
		final List<IComponentDescription> ret = new ArrayList<IComponentDescription>();

		// If name is supplied, just lookup description.
		if(adesc!=null && adesc.getName()!=null)
		{
			CMSComponentDescription ad = (CMSComponentDescription)SComponentManagementService.getDescription(agent.getId());
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
			for(Iterator<IPlatformComponentAccess> it=SComponentManagementService.getComponents(agent.getId()).values().iterator(); it.hasNext(); )
			{
				CMSComponentDescription	test = (CMSComponentDescription)it.next().getInternalAccess().getDescription();
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
			IFuture<Collection<IComponentManagementService>> futi = agent.getFeature(IRequiredServicesFeature.class).searchServices((new ServiceQuery<>(IComponentManagementService.class).setScope(RequiredServiceInfo.SCOPE_GLOBAL)));
			futi.addResultListener(createResultListener(agent, new IResultListener<Collection<IComponentManagementService>>()
			{
				public void resultAvailable(Collection<IComponentManagementService> result)
				{
//					System.out.println("cms: "+coll);
					// Ignore search failures of remote dfs
					IResultListener<IComponentDescription[]> lis = createResultListener(agent, new CollectionResultListener<IComponentDescription[]>(result.size(), true, 
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
//						if(remotecms!=ComponentManagementService.this)
						if(!((IService)remotecms).getId().equals(sid))
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
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large. <- TODO and the reason is...?
	 */
	public static IFuture<IComponentIdentifier[]> getComponentIdentifiers(IInternalAccess agent)
	{
		return new Future<IComponentIdentifier[]>((IComponentIdentifier[])SComponentManagementService.getComponents(agent.getId()).keySet().toArray(new IComponentIdentifier[SComponentManagementService.getComponents(agent.getId()).size()]));
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public static IFuture<IComponentDescription[]> getComponentDescriptions(IInternalAccess agent)
	{
		Future<IComponentDescription[]> fut = new Future<IComponentDescription[]>();
		
		IComponentDescription[] ret = new IComponentDescription[SComponentManagementService.getComponents(agent.getId()).size()];
		int i=0;
		for(Iterator<IPlatformComponentAccess> it=SComponentManagementService.getComponents(agent.getId()).values().iterator(); i<ret.length; i++)
		{
			ret[i] = (IComponentDescription)((CMSComponentDescription)it.next().getInternalAccess().getDescription()).clone();
		}
		
		fut.setResult(ret);
		return fut;
	}
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public static IFuture<IComponentDescription> getComponentDescription(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentDescription> ret = new Future<IComponentDescription>();
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getComponentDescription(cid).addResultListener(createResultListener(agent, new DelegationResultListener<IComponentDescription>(ret)));
				}
			}));
		}
		else
		{
			CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			// Hack, to retrieve description from component itself in init phase
			if(desc==null)
			{
				InitInfo ii= getInitInfo(cid);
				if(ii!=null)
				{
					desc = (CMSComponentDescription)ii.getComponent().getInternalAccess().getDescription();
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
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public static IFuture<IComponentDescription[]> getChildrenDescriptions(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentDescription[]>	ret	= new Future<IComponentDescription[]>();
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent,
				new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription[]>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getChildrenDescriptions(cid).addResultListener(createResultListener(agent, new DelegationResultListener<IComponentDescription[]>(ret)));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			}));
		}
		else
		{
			CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
				IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
			IComponentDescription[]	descs	= new IComponentDescription[tmp.length];
			for(int i=0; i<descs.length; i++)
			{
				descs[i]	= (IComponentDescription)SComponentManagementService.getDescription(tmp[i]);
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
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public static IFuture<IComponentIdentifier[]> getChildren(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentIdentifier[]>	ret	= new Future<IComponentIdentifier[]>();
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier[]>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getChildren(cid).addResultListener(createResultListener(agent, new DelegationResultListener<IComponentIdentifier[]>(ret)));
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			}));
		}
		else
		{
			IComponentIdentifier[] tmp = internalGetChildren(cid);
			ret.setResult(tmp);
		}
		
		return ret;
	}
	
	/**
	 *  Get the parent component of a component.
	 *  @param platform The component identifier.
	 *  @return The parent component identifier.
	 */
	public static IComponentIdentifier getParentIdentifier(CreationInfo ci, IInternalAccess agent)
	{
		IComponentIdentifier ret = ci!=null && ci.getParent()!=null ? ci.getParent() : agent.getId(); 
//		System.out.println("parent id: "+ret);
		return ret;
	}
	
	/**
	 *  Find the class loader for a new (local) component.
	 *  Use parent component class loader for local parents
	 *  and current platform class loader for remote or no parents.
	 *  @param platform	The component id.
	 *  @return	The class loader.
	 */
	protected static IFuture<IResourceIdentifier> getResourceIdentifier(final CreationInfo ci, IInternalAccess agent)
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
			&& (ci.getParent()==null || !isRemoteComponent(ci.getParent(), agent))
//			&& !initinfos.containsKey(ci.getParent())	// does not work during init as external access is not available!?
//			&& !Boolean.TRUE.equals(ci.getPlatformloader()))
			)
		{
			getExternalAccess(ci.getParent()==null? agent.getId(): ci.getParent(), true, agent)
				.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IExternalAccess, IResourceIdentifier>(ret)
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

	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public static IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid, boolean internal, IInternalAccess agent)
	{
//		System.out.println("getExta: "+cid);
		
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
//		ret.addResultListener(new IResultListener<IExternalAccess>()
//		{
//			public void resultAvailable(IExternalAccess result)
//			{
//				if(cid.toString().indexOf("Killer")!=-1)
//				{
//					System.out.println("getExternalAccess: "+cid+", "+result+", "+agent);
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				if(cid.toString().indexOf("Killer")!=-1)
//				{
//					System.out.println("getExternalAccess: "+cid+", "+exception+", "+agent);
//				}
//			}
//		});
		
		if(cid==null)
		{
			ret.setException(new IllegalArgumentException("Identifier is null."));
			return ret;
		}
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.getExternalAccess(cid).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
				}
			});
		}
		else
		{
//			System.out.println("getExternalAccess: local");
			IPlatformComponentAccess component = null;
//				System.out.println("getExternalAccess: adapters");
			boolean delayed = false;
			component = SComponentManagementService.getComponents(agent.getId()).get(cid);
			
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
						fut.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Void, IExternalAccess>(ret)
						{
							public void customResultAvailable(Void result)
							{
								try
								{
									ret.setResult(SComponentManagementService.getComponents(agent.getId()).get(cid).getInternalAccess().getExternalAccess());
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
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener.
	 */
	public static IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier cid, IInternalAccess agent)
	{
		return getExternalAccess(cid, false, agent);
	}
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param cid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public static IFuture<Void> setComponentBreakpoints(final IComponentIdentifier cid, final String[] breakpoints, IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.setComponentBreakpoints(cid, breakpoints).addResultListener(
						createResultListener(agent, new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			CMSComponentDescription ad = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			ad.setBreakpoints(breakpoints);
			
			SComponentManagementService.notifyListenersChanged(ad);
			
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public static IFuture<Void> stepComponent(final IComponentIdentifier cid, final String stepinfo, IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.stepComponent(cid, stepinfo).addResultListener(createResultListener(agent, new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			final IPlatformComponentAccess adapter = SComponentManagementService.getComponents(agent.getId()).get(cid);
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
				
				((IInternalExecutionFeature)adapter.getInternalAccess().getFeature(IExecutionFeature.class)).doStep(stepinfo)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
			}
			else
			{
				ret.setException(new ComponentNotFoundException("Component identifier not registered: "+cid));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a new component to its parent.
	 */
	protected static IFuture<Void>	addSubcomponent(IInternalAccess pad, IComponentDescription ad, IModelInfo lmodel, IInternalAccess agent)
	{
		CMSComponentDescription padesc	= (CMSComponentDescription)pad.getDescription();
		padesc.addChild(ad.getName());
		
//		if(padesc.isAutoShutdown() && !ad.isDaemon())
//		if(pas!=null && pas.booleanValue() && (dae==null || !dae.booleanValue()))
		// cannot check parent shutdown state because could be still uninited
		if(!ad.isDaemon())
		{
			Integer	childcount	= (Integer)SComponentManagementService.getChildCounts(agent.getId()).get(padesc.getName());
			int cc = childcount!=null ? childcount.intValue()+1 : 1;
			SComponentManagementService.getChildCounts(agent.getId()).put(padesc.getName(), Integer.valueOf(cc));
		}
		
		// Register component at parent.
		return ((IInternalSubcomponentsFeature)pad.getFeature(ISubcomponentsFeature.class)).componentCreated(ad);//, lmodel);
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public static IFuture<Void> resumeComponent(final IComponentIdentifier cid, final boolean initresume, IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(isRemoteComponent(cid, agent))
		{
			assert !initresume;
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.resumeComponent(cid).addResultListener(createResultListener(agent, new DelegationResultListener<Void>(ret)));
				}
			}));
		}
		else
		{
			// Resume subcomponents
			final CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			IComponentIdentifier[] achildren = desc!=null ? desc.getChildren() : null;

			if(desc!=null)
			{
				IResultListener<Void> lis = createResultListener(agent, new CounterResultListener<Void>(achildren.length, true, new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						final IPlatformComponentAccess adapter = SComponentManagementService.getComponents(agent.getId()).get(cid);
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
								if(!SComponentManagementService.getCleanupFutures(agent.getId()).containsKey(cid))
								{
									InitInfo ii = removeInitInfo(cid);
		//							System.out.println("removed: "+cid+" "+ii);
									if(ii!=null)
									{
										boolean	suspend = isInitSuspend(ii.getInfo(), adapter.getInternalAccess().getModel(), agent);
										
										if(suspend)
										{
											desc.setState(IComponentDescription.STATE_SUSPENDED);
											changed	= true;
										}
									}
									
									adapter.body().addResultListener(adapter.getInternalAccess().getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											// Nop. keepalive is handled by component internally
										}
										
										public void exceptionOccurred(Exception exception)
										{
											if(!(exception instanceof ComponentTerminatedException)
												|| !((ComponentTerminatedException)exception).getComponentIdentifier().equals(adapter.getInternalAccess().getId()))
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
									SComponentManagementService.getCleanupFutures(agent.getId()).remove(cid);
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
									((IInternalExecutionFeature)adapter.getInternalAccess().getFeature(IExecutionFeature.class)).wakeup();
								}
							}
							
							if(changed)
								SComponentManagementService.notifyListenersChanged(desc);
						
							ret.setResult(null);
						}
					}
				}));
				
				for(int i=0; i<achildren.length; i++)
				{
					resumeComponent(achildren[i], initresume, agent).addResultListener(lis);
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
	 *  Suspend the execution of an component.
	 *  @param cid The component identifier.
	 */
	public static IFuture<Void> suspendComponent(final IComponentIdentifier cid, IInternalAccess agent)
	{
		IFuture<Void>	ret;
		
		if(isRemoteComponent(cid, agent))
		{
			final Future<Void> fut = new Future<Void>();
			ret	= fut;
			getRemoteCMS(agent, cid).addResultListener(createResultListener(agent,
				new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					rcms.suspendComponent(cid).addResultListener(createResultListener(agent, new DelegationResultListener<Void>(fut)));
				}
			}));
			return ret;
		}
		else
		{
			final IPlatformComponentAccess comp = SComponentManagementService.getComponents(agent.getId()).get(cid);
			if(comp!=null)
			{
				CMSComponentDescription desc = (CMSComponentDescription)comp.getInternalAccess().getDescription();
				if(IComponentDescription.STATE_ACTIVE.equals(desc.getState()))
				{
					// Suspend subcomponents
					IComponentIdentifier[] achildren = desc.getChildren();
					for(int i=0; i<achildren.length; i++)
					{
						IComponentDescription	cdesc	= SComponentManagementService.getDescription(achildren[i]);
						if(IComponentDescription.STATE_ACTIVE.equals(cdesc.getState()))
						{
							suspendComponent(achildren[i], agent);	// todo: cascading suspend with wait.
						}
					}					
					desc.setState(IComponentDescription.STATE_SUSPENDED);
					SComponentManagementService.notifyListenersChanged(desc);
					
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
	 *  Loop for destroying subcomponents.
	 */
	protected static IFuture<List<Exception>> destroyComponentLoop(final IComponentIdentifier cid, final IComponentIdentifier[] achildren, final int i, IInternalAccess agent)
	{
		final Future<List<Exception>> ret = new Future<List<Exception>>();
		
//		System.out.println("destroy loop: "+cid+" "+i+"/"+achildren.length);
		
		if(achildren.length>0)
		{
			final List<Exception> exceptions = new ArrayList<Exception>();
			destroyComponent(achildren[i], agent).addResultListener(createResultListener(agent, new IResultListener<Map<String, Object>>()
			{
				public void resultAvailable(Map<String, Object> result)
				{
					if(i>0)
					{
						destroyComponentLoop(cid, achildren, i-1, agent).addResultListener(
							createResultListener(agent, new DelegationResultListener<List<Exception>>(ret)));
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
	 *  Internal destroy method that performs the actual work.
	 *	@param cid The component to destroy.
	 *  @param ret The future to be informed.
	 */
	protected static void destroyComponent(final IComponentIdentifier cid,	final Future<Map<String, Object>> ret, IInternalAccess agent)
	{
//		System.out.println("kill: "+cid);
		
		if(isRemoteComponent(cid, agent))
		{
			getRemoteCMS(agent, cid.getParent()).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
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
			IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : SComponentManagementService.getComponents(agent.getId()).get(cid);
			
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
					agent.getLogger().info("Queued component termination during init: "+cid.getName());
				}
			}
			
			// Terminate normally inited component.
			else if(comp!=null)
			{				
				// Kill subcomponents
				agent.getLogger().info("Terminating component structure: "+cid.getName());
				final CMSComponentDescription	desc = (CMSComponentDescription)comp.getInternalAccess().getDescription();
				final IComponentIdentifier[] achildren = desc.getChildren();
				
//				if(achildren.length>0)
//					System.out.println("kill childs start: "+cid+" "+achildren.length+" "+SUtil.arrayToString(achildren));
				
				destroyComponentLoop(cid, achildren, achildren.length-1, agent).addResultListener(createResultListener(agent, new IResultListener<List<Exception>>()
				{
					public void resultAvailable(List<Exception> result)
					{
//						if(achildren.length>0)
//							System.out.println("kill childs end: "+cid);
						
//						if(cid.toString().startsWith("Initiator"))
//							System.out.println("Terminated component structure: "+cid.getName());
						
						agent.getLogger().info("Terminated component structure: "+cid.getName());
						CleanupCommand	cc	= null;
						IFuture<Void>	fut	= null;
						
						// Fetch comp again as component may be already killed (e.g. when autoshutdown).
						InitInfo infos	= getInitInfo(cid);
						IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : SComponentManagementService.getComponents(agent.getId()).get(cid);
						if(comp!=null)
						{
//							if(cid.toString().indexOf("AutoTerminate")!=-1)
//								System.out.println("destroy1: "+cid.getName());
//										
							// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
							// todo: killcomponent should only be called once for each component?
							if(!SComponentManagementService.getCleanupCommands(agent.getId()).containsKey(cid))
							{
//								if(cid.toString().indexOf("AutoTerminate")!=-1)
//									System.out.println("killing a: "+cid);
								
								cc	= new CleanupCommand(cid);
								SComponentManagementService.getCleanupCommands(agent.getId()).put(cid, cc);
								agent.getLogger().info("Terminating component: "+cid.getName());
								fut = comp.shutdown();								
							}
							else
							{
//								if(cid.toString().indexOf("AutoTerminate")!=-1)
//									System.out.println("killing b: "+cid);
								
								cc = (CleanupCommand)SComponentManagementService.getCleanupCommands(agent.getId()).get(cid);
							}
						}
						
						// Add listener outside synchronized block to avoid deadlocks
						if(fut!=null && cc!=null)
						{
							// Cannot use invoke later during platform shutdown
							IResultListener lis = cid.getParent()==null? cc: createResultListener(agent, cc);
							fut.addResultListener(lis);
						}
						
						if(cc==null)
						{
							// Todo: what is this case?
							SComponentManagementService.exitDestroy(cid, desc, new RuntimeException("No cleanup command for component "+cid+": "+desc.getState()), null);
						}
						else
						{
							// Resume component to be killed in case it is currently suspended.
							resumeComponent(cid, false, agent);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
//							System.out.println("ex: "+exception);
						SComponentManagementService.exitDestroy(cid, desc, exception, null);
					}
				}));
			}			
			else
			{
				SComponentManagementService.getCleanupFutures(cid).remove(cid);
				ret.setException(new ComponentTerminatedException(cid, "Cannot kill, no such component."));
			}
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param cid	The component to destroy.
	 */
	public static IFuture<Map<String, Object>> destroyComponent(final IComponentIdentifier cid, IInternalAccess agent)
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
		
		contains = SComponentManagementService.getCleanupFutures(agent.getId()).containsKey(cid);
		tmp = contains? (Future<Map<String, Object>>)SComponentManagementService.getCleanupFutures(agent.getId()).get(cid): new Future<Map<String, Object>>();
//		System.out.println("destroy0: "+cid+" "+cfs.containsKey(cid)+" "+tmp.isDone());
//		Thread.currentThread().dumpStack();
		
		// If destroyComponent has not been called before
		if(!contains)
		{
			SComponentManagementService.getCleanupFutures(agent.getId()).put(cid, tmp);
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
		LockEntry kt = SComponentManagementService.getLockEntries(agent.getId()).get(cid);
		if(kt!=null && kt.getLockerCount()>0)
		{
			kt.setKillFuture(tmp);
			locked = true;
		}

		final Future<Map<String, Object>> ret = tmp;
		
		if(!contains && !locked)
		{
			destroyComponent(cid, ret, agent);
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
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param resultlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	public static IFuture<IComponentIdentifier> createComponent(final String oname, final String modelname, CreationInfo info, 
		final IResultListener<Collection<Tuple2<String, Object>>> resultlistener, IInternalAccess agent)
	{			
		if(modelname==null)
			return new Future<IComponentIdentifier>(new IllegalArgumentException("Error creating component: " + oname + " : Modelname must not be null."));

//		System.out.println("create: "+name+" "+modelname+" "+agent.getComponentIdentifier());
		
//		if(oname!=null && oname.toLowerCase().indexOf("Provider")!=-1)
//			System.out.println("create compo: "+modelname+" "+info);
		
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		final IComponentIdentifier creator = sc==null? null: sc.getCaller();
		final Cause curcause = sc==null? agent.getDescription().getCause(): sc.getCause();
		
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
			if(SComponentManagementService.getCleanupFutures(agent.getId()).containsKey(cinfo.getParent()))
				return new Future<IComponentIdentifier>(new ComponentTerminatedException(cinfo.getParent() ,"Parent is killing itself. Child component creation no allowed."));
		}
		
		if(cinfo.getParent()!=null && isRemoteComponent(cinfo.getParent(), agent))
		{				
			getRemoteCMS(agent, cinfo.getParent()).addResultListener(createResultListener(agent, 
				new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(inited)
			{
				public void customResultAvailable(IComponentManagementService rcms)
				{
					// todo: problem, the call will get a wrong caller due to IComponentIdentidier.LOCAL.get()
					// will deliver the platform (as this second call is performed by the cms itself)
					
					// Map remote subscription events to local result listener (avoids the need for listener as plain remote object)
					rcms.createComponent(cinfo, oname, modelname).addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
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
			
			if(oname!=null && oname.indexOf('@')!=-1)
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
						getResourceIdentifier(cinfo, agent).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IResourceIdentifier, IComponentIdentifier>(inited)
						{
							public void customResultAvailable(final IResourceIdentifier rid)
							{
//								System.out.println("loading: "+modelname+" "+rid);

								resolveFilename(modelname, cinfo, rid, agent).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Tuple2<String, ClassLoader>, IComponentIdentifier>(inited)
								{
									public void customResultAvailable(final Tuple2<String, ClassLoader> tup)
									{
										final String model = tup.getFirstEntity();
										getComponentFactory(model, cinfo, rid, false, false, agent)
											.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentFactory, IComponentIdentifier>(inited)
										{
											public void customResultAvailable(final IComponentFactory factory)
											{
//												System.out.println("load: "+model+" "+rid);
												factory.loadModel(model, cinfo.getImports(), rid)
													.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier>(inited)
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
															final String name = (String)SJavaParser.evaluateExpressionPotentially(oname, lmodel.getAllImports(), null, tup.getSecondEntity());
															
//															System.out.print("<"+lmodel.getName()+" "+factory.getClass());
															IFuture<Collection<IComponentFeatureFactory>> fut = factory.getComponentFeatures(lmodel);
															fut.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Collection<IComponentFeatureFactory>, IComponentIdentifier>(inited)
															{
																public void customResultAvailable(Collection<IComponentFeatureFactory> features)
																{
//															fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Object, IComponentIdentifier>(inited)
//															{
//																public void customResultAvailable(Object o)
//																{
//																	if(!(o instanceof Collection))
//																		System.out.println("ppp: "+o);
																	
//																	Collection<IComponentFeatureFactory> features = (Collection<IComponentFeatureFactory>)o;
//																	System.out.println(">");
																	// Create id and adapter.
																	
																	final BasicComponentIdentifier cid;
																	
																	// check if system component is located in system tree
																	Map<String, Object> props = lmodel.getProperties();
																	
																	IComponentIdentifier pacid = getParentIdentifier(cinfo, agent);
																	
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
																	LockEntry kt = SComponentManagementService.getLockEntries(agent.getId()).get(cinfo.getParent());
																	if(kt==null)
																	{
																		kt = new LockEntry(cinfo.getParent());
																		SComponentManagementService.getLockEntries(agent.getId()).put(cinfo.getParent(), kt);
																	}
																	kt.addLocker(lockkey);
																	inited.addResultListener(createResultListener(agent, new IResultListener<IComponentIdentifier>()
																	{
																		public void resultAvailable(IComponentIdentifier result)
																		{
																			LockEntry kt = SComponentManagementService.getLockEntries(agent.getId()).get(cinfo.getParent());
																			if(kt!=null)
																			{
																				kt.removeLocker(lockkey);
																				if(kt.getLockerCount()==0)
																				{
																					SComponentManagementService.getLockEntries(agent.getId()).remove(cinfo.getParent());
																				}
																			}
																		}
																		public void exceptionOccurred(Exception exception)
																		{
																			LockEntry kt = SComponentManagementService.getLockEntries(agent.getId()).get(cinfo.getParent());
																			if(kt!=null)
																			{
																				if(kt.removeLocker(lockkey))
																					destroyComponent(kt.getLocked(), kt.getKillFuture(), agent);
																				
																				if(kt.getLockerCount()==0)
																				{
																					SComponentManagementService.getLockEntries(agent.getId()).remove(cinfo.getParent());
																				}
																			}
																		}
																	}));

																	final IInternalAccess pad = getParentComponent(cinfo, agent);
																	IExternalAccess parent = pad.getExternalAccess();
																	pacid = parent.getId();

																	String paname = pacid.getName().replace('@', ':');
																	
																	// TODO!!!!! use unique setting
																	
																	cid = (BasicComponentIdentifier)generateComponentIdentifier(name!=null? name: lmodel.getNameHint()!=null? lmodel.getNameHint(): lmodel.getName(), paname, agent, true);//, addresses);
																	
																	// Defer component services being found from registry
																	ServiceRegistry.getRegistry(agent).addExcludedComponent(cid);
																	
																	Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
																	Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
																	Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
																	Boolean sync = cinfo.getSynchronous()!=null? cinfo.getSynchronous(): lmodel.getSynchronous(cinfo.getConfiguration());
																	Boolean persistable = cinfo.getPersistable()!=null? cinfo.getPersistable(): lmodel.getPersistable(cinfo.getConfiguration());
																	PublishEventLevel moni = cinfo.getMonitoring()!=null? cinfo.getMonitoring(): lmodel.getMonitoring(cinfo.getConfiguration());
																	// Inherit monitoring from parent if null
																	if(moni==null && cinfo.getParent()!=null)
																	{
																		CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cinfo.getParent());
																		moni = desc.getMonitoring();
																	}
																	
//																	Cause cause = new Cause(curcause, cid.getName());
																	Cause cause = curcause;
																	// todo: how to do platform init so that clock is always available?
																	IClockService cs = getClockService0(agent);
																	final CMSComponentDescription ad = new CMSComponentDescription(cid, lmodel.getType(), master!=null ? master.booleanValue() : false,
																		daemon!=null ? daemon.booleanValue() : false, autosd!=null ? autosd.booleanValue() : false, sync!=null ? sync.booleanValue() : false,
																		persistable!=null ? persistable.booleanValue() : false, moni,
																		lmodel.getFullName(), cinfo.getLocalType(), lmodel.getResourceIdentifier(), cs!=null? cs.getTime(): System.currentTimeMillis(), creator, cause, systemcomponent);
																	
//																	System.out.println("moni: "+moni+" "+lmodel.getFullName());
																	
																	// Use first configuration if no config specified.
																	String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
																		: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
																	final IPlatformComponentAccess component = new PlatformComponent();
																	ComponentCreationInfo	cci	= new ComponentCreationInfo(lmodel, config, cinfo.getArguments(), ad, cinfo.getProvidedServiceInfos(), cinfo.getRequiredServiceBindings());
																	component.create(cci, features);
																	if(resultlistener!=null)
																	{
																		IArgumentsResultsFeature	af	= component.getInternalAccess().getFeature0(IArgumentsResultsFeature.class);
																		IResultListener<Collection<Tuple2<String, Object>>>	rl	= new IIntermediateResultListener<Tuple2<String, Object>>()
																		{
																			public void exceptionOccurred(final Exception exception)
																			{
																				// Wait for cleanup finished before posting results
																				SComponentManagementService.getCleanupFutures(agent.getId()).get(cid).addResultListener(new IResultListener<Map<String,Object>>()
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
																				IFuture<Map<String, Object>>	fut	= SComponentManagementService.getCleanupFutures(agent.getId()).get(cid);
																				if(fut!=null)	// TODO: why null (seldom in RemoteBlockingTestAgent during gradle build)
																				{
																					fut.addResultListener(new IResultListener<Map<String,Object>>()
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
																				else
																				{
																					resultlistener.exceptionOccurred(new NullPointerException("No cleanup future!?"));
																				}
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
																	
																	agent.getLogger().info("Starting component: "+cid.getName());
							//										System.err.println("Pre-Init: "+cid);
																	
																	resfut.addResultListener(createResultListener(agent, new IResultListener<Void>()
																	{
																		public void resultAvailable(Void result)
																		{
																			agent.getLogger().info("Started component: "+cid.getName());

																			ServiceRegistry.getRegistry(agent).removeExcludedComponent(cid);
																			
		//																	System.out.println("created: "+ad);
																			
																			// Init successfully finished. Add description and adapter.
																			InitInfo	info	= getInitInfo(cid);
																			
																			// Init finished. Set to suspended until parent registration is finished.
																			// not set to suspend to allow other initing sibling components invoking services
				//															ad.setState(IComponentDescription.STATE_SUSPENDED);
																			
//																			System.out.println("adding cid: "+cid);
																			SComponentManagementService.getComponents(agent.getId()).put(cid, info.getComponent());
																			// Removed in resumeComponent()
				//																initinfos.remove(cid);
																			
																			// Register component at parent.
																			addSubcomponent(pad, ad, lmodel, agent)
																				.addResultListener(createResultListener(agent, new IResultListener<Void>()
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
																					SComponentManagementService.notifyListenersAdded(ad);
																							
			//																		System.out.println("created: "+cid.getLocalName());//+" "+(parent!=null?parent.getComponentIdentifier().getLocalName():"null"));
							//														System.out.println("added: "+descs.size()+", "+aid);
																					
//																					if(modelname.indexOf("jadex.platform.service.message.transport.ssltcpmtp.ProviderAgent")!=-1)
//																						System.out.println("inited, return: "+cid);//+" "+info!=null? info.getResourceIdentifier(): "norid");
																					
																					inited.setResult(cid);
																					
																					Future<Map<String, Object>>	killfut;
																					killfut	= (Future<Map<String, Object>>)SComponentManagementService.getCleanupFutures(agent.getId()).get(cid);
																					if(killfut!=null)
																					{
																						// Remove init infos otherwise done in resume()
																						List<IComponentIdentifier>	cids	= new ArrayList<IComponentIdentifier>();
																						cids.add(cid);
																						for(int i=0; i<cids.size(); i++)
																						{
																							removeInitInfo(cids.get(i));
																							CMSComponentDescription	desc	= (CMSComponentDescription)SComponentManagementService.getDescription((IComponentIdentifier)cids.get(i));
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
																						destroyComponent(cid, killfut, agent);
																					}
																					else
																					{
																						if(doinit)
																						{
//																							System.out.println("start: "+cid);
																							resumeComponent(cid, true, agent);//.addResultListener(listener)
																						}
																					}
																				}
																				
																				public void exceptionOccurred(Exception exception)
																				{
																					// Exception in parent during startup of subcomponent
																					// --> complete init, so parent can terminate.
																					if(exception instanceof ComponentTerminatedException)
																					{
																						SComponentManagementService.notifyListenersAdded(ad);
																						inited.setResult(cid);
																					}
																					else
																					{
																						agent.getLogger().info("Starting component failed: "+cid+", "+exception);
																					}																					
																				}
																			}));								
																		}
																		
																		public void exceptionOccurred(final Exception exception)
																		{
																			agent.getLogger().info("Starting component failed: "+cid+", "+exception);
																			
																			IServiceRegistry reg = agent!=null ? ServiceRegistry.getRegistry(agent) : null;
																			if(reg!=null)	// TODO: why null? -> platform shutdown, superpeer fail?
																				reg.removeExcludedComponent(cid);
																			
//																			System.err.println("Starting component failed: "+cid+", "+exception);
//																			exception.printStackTrace();
			//																System.out.println("Ex: "+cid+" "+exception);
																			final Runnable	cleanup	= new Runnable()
																			{
																				public void run()
																				{
																					IPlatformComponentAccess	comp	= SComponentManagementService.getComponents(agent.getId()).get(cid);
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
																					createResultListener(agent, new IResultListener<Void>()
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
																					destroyComponent(children[i], agent).addResultListener(crl);
																				}
																			}
																			else
																			{
																				cleanup.run();									
																			}
																		}
																	}));
																	
																	// Create component and init.
																	component.init().addResultListener(createResultListener(agent, new DelegationResultListener<Void>(resfut)));
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
	 *  Create a new component on the platform.
	 *  This method allows for retrieving intermediate results of the component via
	 *  status events.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The status events of the components. Consists of CMSCreatedEvent, (CMSIntermediateResultEvent)*, CMSTerminatedEvent
	 */
	public static ISubscriptionIntermediateFuture<CMSStatusEvent> createComponent(CreationInfo info, String name, String model, IInternalAccess agent)
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
					destroyComponent(mycid[0], agent);
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
				ret.addIntermediateResultIfUndone(new CMSIntermediateResultEvent(SComponentManagementService.getDescription(mycid[0]), result.getFirstEntity(), result.getSecondEntity()));
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
				ret.addIntermediateResultIfUndone(new CMSTerminatedEvent(SComponentManagementService.getDescription(mycid[0]), results));
				ret.setFinishedIfUndone();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		}, agent).addResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier cid)
			{
				mycid[0] = cid;
				ret.addIntermediateResultIfUndone(new CMSCreatedEvent(SComponentManagementService.getDescription(cid)));
				if(terminate[0])
				{
					destroyComponent(cid, agent);
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
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public static ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String name, final String model, CreationInfo info, IInternalAccess agent)
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
		}, agent).addResultListener(new IResultListener<IComponentIdentifier>()
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
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	@SuppressWarnings("unchecked")
	public static IFuture<IModelInfo> loadComponentModel(final String filename, final IResourceIdentifier rid, IInternalAccess agent)
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
			ILibraryService	ls	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ILibraryService.class));
			Collection<IComponentFactory> facs = agent.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>( IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM));
			FactoryFilter facfilter = new FactoryFilter(filename, null, rid);
			
			SFilter.applyFilter(facs, facfilter).addResultListener(new IResultListener<Collection<IComponentFactory>>()
			{
				public void resultAvailable(Collection<IComponentFactory> result)
				{
					if (result != null && result.size() > 0)
					{
						result.iterator().next().loadModel(filename, null, rid)
							.addResultListener(new DelegationResultListener<IModelInfo>(ret));
					}
					else
					{
						ret.setResult(null);
					}
				}
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		return ret;
	}
	
	/**
	 *  Get the clock service without exception if not found.
	 */
	protected static IClockService getClockService0(IInternalAccess agent)
	{
		return agent.getFeature(IRequiredServicesFeature.class).searchLocalService(
			new ServiceQuery<>(IClockService.class).setMultiplicity(Multiplicity.ZERO_ONE));
	}
}
