package jadex.bridge.service.types.cms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ComponentCreationException;
import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.FactoryFilter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.ProxyFactory;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.StepAbortedException;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.IInternalArgumentsResultsFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.component.impl.IInternalSubcomponentsFeature;
import jadex.bridge.component.impl.remotecommands.IMethodReplacement;
import jadex.bridge.component.impl.remotecommands.ProxyInfo;
import jadex.bridge.component.impl.remotecommands.ProxyReference;
import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.IService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSCreatedEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSIntermediateResultEvent;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSTerminatedEvent;
import jadex.bridge.service.types.cms.CmsState.CmsComponentState;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.MethodInfo;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.IAutoLock;
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
import jadex.commons.future.TerminationCommand;
import jadex.commons.future.Tuple2Future;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  Static CMS methods.
 */
public class SComponentManagementService
{
	/**
	 *  Gets the external access of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return External Access.
	 */
	// r: components
	// w: -
	public static final IExternalAccess getLocalExternalAccess(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getState(platform).getAccess(cid);
		if(comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getExternalAccess();
	}
	
	/**
	 *  Gets the classloader of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return ClassLoader.
	 */
	// r: components
	// w: -
	public static final ClassLoader getLocalClassLoader(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getState(platform).getAccess(cid);
		if(comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getClassLoader();
	}
	
	/**
	 *  Get the CMS state for the platform.
	 *  @param cid The platform id.
	 *  @return The CMS state.
	 */
	public static final CmsState getState(IComponentIdentifier cid)
	{
		return (CmsState)Starter.getPlatformValue(cid, Starter.DATA_CMSSTATE);
	}
	
	/**
	 *  Get the bootstrap factory
	 *  @param cid The platform id.
	 *  @return The bootstrap factory.
	 */
	public static IComponentFactory getComponentFactory(IComponentIdentifier cid)
	{
		return (IComponentFactory)Starter.getPlatformValue(cid, Starter.DATA_BOOTSTRAPFACTORY);
	}
	
	/**
	 *  Remove the bootstrap factory
	 *  @param cid The platform id.
	 *  @return The bootstrap factory.
	 */
	public static void removeComponentFactory(IComponentIdentifier cid)
	{
		Starter.putPlatformValue(cid, Starter.DATA_BOOTSTRAPFACTORY, null);
	}
	
	/**
	 *  Get the description for a component (if any).
	 */
	// r: components
	// w: -
	public static IComponentDescription	getDescription(IComponentIdentifier cid)
	{
		if(cid==null)
			return null;
		IPlatformComponentAccess component = getComponent(cid);
		return component!=null ? component.getInternalAccess().getDescription() : null;
	}
	
	/**
	 *  Exit the destroy method by setting description state and resetting maps.
	 *  @return True, when somebody was notified.
	 */
	// r: -
	// w: cleanup futures
	public static void exitDestroy(IComponentIdentifier cid, IComponentDescription desc, Exception ex, Map<String, Object> results)
	{
//		Thread.dumpStack();
		Future<Map<String, Object>>	ret;
		//getCleanupCommands(cid).remove(cid);
//		System.out.println("exit destoy remove cleanup future: "+cid);
		ret = (Future<Map<String, Object>>)getState(cid).getCleanupFuture(cid);
//		ret	= (Future<Map<String, Object>>)getCleanupFutures(cid).remove(cid);

		if(desc instanceof CMSComponentDescription)
		{
			((CMSComponentDescription)desc).setState(IComponentDescription.STATE_TERMINATED);
		}
		if(ret!=null)
		{
			// StepAbortedException is normal for steps during kill
			if(ex!=null && !(ex instanceof StepAbortedException))
			{
				ret.setExceptionIfUndone(ex);
			}
			else
			{
				ret.setResultIfUndone(results);
			}
		}
	}
	
	// r: listeners
	// w: listeners
	/**
     *  Add a component listener for all components.
     *  The listener is registered for component changes.
     */
    public static ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll(IInternalAccess agent)
    {
    	return listenToComponent(null, agent);
    }
	
	// r: listeners
	// w: listeners
	/**
     *  Add a component listener for a specific component.
     *  The listener is registered for component changes.
     *  @param cid	The component to be listened.
     */
    public static ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(final IComponentIdentifier cid, IInternalAccess agent)
    {
    	final SubscriptionIntermediateFuture<CMSStatusEvent> ret = new SubscriptionIntermediateFuture<CMSStatusEvent>();
    	SFuture.avoidCallTimeouts(ret, agent);
    	
//    	if(getListeners()==null)
//    		listeners	= new MultiCollection<IComponentIdentifier, SubscriptionIntermediateFuture<CMSStatusEvent>>();
    	// todo: make this transactional (with features this could be broken)
    	
    	CmsState cmsstate = getState(agent.getId());
    	try(IAutoLock l = cmsstate.writeLock())
    	{
    		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> col = null;
    		if (cid != null)
    		{
	    		CmsComponentState compstate = cmsstate.getComponent(cid);
	    		if (compstate == null)
	    		{
	    			ret.setException(new IllegalStateException("Component not found: " + cid));
	    			return ret;
	    		}
	    		compstate.getCmsListeners();
	    		if(col==null)
		    	{
		    		col = new ArrayList<SubscriptionIntermediateFuture<CMSStatusEvent>>();
		    		compstate.setCmsListeners(col);
		    	}
    		}
    		else
    		{
    			col = cmsstate.getAllListeners();
    		}
	    	col.add(ret);
    	}
    	
    	ret.setTerminationCommand(new TerminationCommand()
    	{
    		@Override
    		public void terminated(Exception reason)
    		{
    			try(IAutoLock l = cmsstate.writeLock())
    			{
	    			Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> col = getState(agent.getId()).getCmsListeners(cid);
	    			if(col!=null)
	    				col.remove(ret);
    			}
    		}
    	});
    	
    	return ret;    	
    }
    
    // r: listeners
 	// w: -
    /**
	 *  Notify the cms listeners of an addition.
	 */
	public static void notifyListenersAdded(IComponentDescription desc)
	{
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	slis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(desc.getName())));
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	alis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(null)));
		slis.addAll(alis);
		
		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: slis)
		{
			sub.addIntermediateResultIfUndone(new CMSCreatedEvent(desc));
		}
	}
    
	// r: listeners
 	// w: -
    /**
	 *  Notify the cms listeners of a change.
	 */
	public static void notifyListenersChanged(IComponentDescription desc)
	{
		// listeners are copied to be threadsafe

		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	slis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(desc.getName())));
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	alis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(null)));
		slis.addAll(alis);
		
		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: slis)
		{
			sub.addIntermediateResultIfUndone(new CMSStatusEvent(desc));
		}
	}
	
	// r: listeners
 	// w: -
	/**
	 *  Notify the cms listeners of a removal.
	 */
	public static void notifyListenersRemoved(IComponentDescription desc, Exception ex, Map<String, Object> results)
	{
		// listeners are copied to be threadsafe
		
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	slis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(desc.getName())));
		Collection<SubscriptionIntermediateFuture<CMSStatusEvent>>	alis = new ArrayList<>(SUtil.notNull(getState(desc.getName()).getCmsListeners(null)));
		
		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: alis)
		{
			sub.addIntermediateResultIfUndone(new CMSTerminatedEvent(desc, results, ex));
		}
		
		for(SubscriptionIntermediateFuture<CMSStatusEvent> sub: slis)
		{
			sub.addIntermediateResultIfUndone(new CMSTerminatedEvent(desc, results, ex));
			sub.setFinished();
		}
		
		// remove the listeners of the terminated component
//		getListeners(desc.getName()).remove(desc.getName());	// remove(!) subscriptions for termination event
	}
	
	// r: components, local component types
	// w: local component types
	/**
	 *  Find the file name and local component type name
	 *  for a component to be started.
	 */
	protected static IFuture<Tuple2<String, ClassLoader>> resolveFilename(final String modelname, final CreationInfo cinfo, final IResourceIdentifier rid, IInternalAccess agent)
	{
		final Future<Tuple2<String, ClassLoader>> ret = new Future<Tuple2<String, ClassLoader>>();
		
		ILibraryService libservice = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class).setMultiplicity(Multiplicity.ZERO_ONE));
		// Hack!!! May be null on platform init
		
		final IInternalAccess pad = getParentComponent(cinfo, agent);
//		final IExternalAccess parent = pad.getExternalAccess();
		
		IComponentIdentifier parent = agent.getId();
		IModelInfo model = pad.getModel();
//		parent.getModelAsync().addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IModelInfo, Tuple2<String, ClassLoader>>(ret)
//		{
//			public void customResultAvailable(IModelInfo model)
//			{
				if(libservice!=null)
				{
					getClassLoader(libservice, rid).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<ClassLoader, Tuple2<String, ClassLoader>>(ret)
					{
						public void customResultAvailable(ClassLoader cl)
						{
//							final IInternalAccess pad = getParentComponent(cinfo, agent);
//							final IExternalAccess parent = pad.getExternalAccess();
							
							String filename = modelname;
							
//							if(cinfo.getParent()!=null) // FIXME
							if(parent!=null)
							{
								// Try to find file for local type.
								String localtype = modelname!=null ? modelname : cinfo.getLocalType();
								filename = null;
								final SubcomponentTypeInfo[] subcomps = model.getSubcomponentTypes();
								for(int i=0; filename==null && i<subcomps.length; i++)
								{
									if(subcomps[i].getName().equals(localtype))
									{
										filename = subcomps[i].getFilename();
										cinfo.setLocalType(localtype);
									}
								}
								if(filename==null)
									filename	= modelname;
								
								// Try to find local type for file
								if(cinfo.getLocalType()==null && subcomps.length>0)
								{
									Tuple key = new Tuple(model.getFullName(), filename);
									// as no one removes local types this is threadsafe in two steps
									if(getState(agent.getId()).getLocalTypes().containsKey(key))
									{
										cinfo.setLocalType(getState(agent.getId()).getLocalTypes().get(key));
									}
									else
									{
										ResourceInfo info = SUtil.getResourceInfo0(filename, cl);
										if(info!=null)
										{
											for(int i=0; cinfo.getLocalType()==null && i<subcomps.length; i++)
											{
												ResourceInfo info1 = SUtil.getResourceInfo0(subcomps[i].getFilename(), cl);
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
										getState(agent.getId()).getLocalTypes().put(key, cinfo.getLocalType());
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
					
//					if(cinfo.getParent()!=null) //FIXME
					if(parent!=null)
					{
						// Try to find file for local type.
						String	localtype = modelname!=null ? modelname : cinfo.getLocalType();
						filename = null;
//						IInternalAccess pad = getParentComponent(cinfo, agent);
//						IExternalAccess parent = pad.getExternalAccess();
						final SubcomponentTypeInfo[] subcomps = model.getSubcomponentTypes();
						
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
					
//					ret.setResult(new Tuple2<String, ClassLoader>(filename, null));
					ret.setResult(new Tuple2<String, ClassLoader>(filename, SComponentManagementService.class.getClassLoader()));
				}
//			}
//		}));
		
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
	// r: factory
	// w: factory (deletes as sideeffect)
	protected static IFuture<IComponentFactory> getComponentFactory(final String model, final CreationInfo cinfo, final IResourceIdentifier rid, final boolean searched, final boolean cachemiss, IInternalAccess agent)
	{
		Future<IComponentFactory> ret = new Future<>();
		
//		if(model.indexOf("KernelMicro")!=-1)
//			System.out.println("getCompFac: "+model);
		
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
//				System.out.println("found factory for: "+model);
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
							{
//								System.out.println("found fallback factory for: "+model);
								ret.setResult(getComponentFactory(agent.getId()));
							}
							else
							{
								ret.setException(new ServiceNotFoundException("No component factory found for: "+model));
							}
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
		return ((IService)fac).getServiceId().getProviderId().getLocalName().indexOf("multi")!=-1;
	}
	
	// r: init infos
	// w: - 
	/**
	 *  Get the info of the parent component.
	 */
	protected static InitInfo getParentInfo(CreationInfo cinfo, IInternalAccess agent)
	{
		final IComponentIdentifier paid = getParentIdentifier(cinfo, agent);
		return getState(paid).getInitInfo(paid);
	}
	
	// r: components, init infos
	// w: -
	/**
	 *  Get the parent component.
	 */	
	protected static IInternalAccess getParentComponent(CreationInfo cinfo, IInternalAccess agent)
	{
		// access to both maps could fail if not in a transaction
		
		
		IPlatformComponentAccess component = null;
		CmsState cmsstate = getState(agent.getId());
		try(IAutoLock l = cmsstate.readLock())
		{
			IComponentIdentifier paid = getParentIdentifier(cinfo, agent);
			component = cmsstate.getAccess(paid);
			if(component==null)
			{
				InitInfo pinfo = getParentInfo(cinfo, agent);
				
				// Hack!!! happens when parent is killed while trying to create subcomponent (todo: integrate locking for destroy and create of component structure)
				if(pinfo==null)
					throw new ComponentTerminatedException(paid);
				
				component = pinfo.getComponent();
			}
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		
		return component.getInternalAccess();
	}
		
	/**
	 *  Test if a component identifier is a remote component.
	 */
	public static boolean isRemoteComponent(IComponentIdentifier cid, IInternalAccess agent)
	{
		return !cid.getPlatformName().equals(agent.getId().getRoot().getName());
	}
	
	/**
	 *  Create result listener that tolerates when agent is null at shutdown.
	 */
	protected static <T> IResultListener<T> createResultListener(IInternalAccess agent, IResultListener<T> listener)
	{
		return agent==null? listener: agent.getFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	// r: initinfos, components
	// w: 
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
			pasuspend = isInitSuspend(painfo.getInfo(), painfo.getComponent().getInternalAccess().getModel(), agent);
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
	
	// r: components
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
	
	// w: desc
	// r: listeners, components
	/**
	 *  Set the state of a component (i.e. update the component description).
	 *  Currently only switching between suspended/waiting is allowed.
	 */
	// hack???
	public void	setComponentState(IComponentIdentifier comp, String state)
	{
		assert IComponentDescription.STATE_SUSPENDED.equals(state) : "wrong state: "+comp+", "+state;
		
		CMSComponentDescription	desc = null;
		desc = (CMSComponentDescription)SComponentManagementService.getDescription(comp);
		desc.setState(state);			
		
		SComponentManagementService.notifyListenersChanged(desc);
	}
	
	// reads/writes cidcounts
	/**
	 *  Create a component identifier that is allowed on the platform.
	 *  @param name The base name.
	 *  @return The component identifier.
	 */
	public static IComponentIdentifier generateComponentIdentifier(String localname, String platformname, IInternalAccess agent, boolean uniqueids)
	{
		// checks components and init infos
		
		BasicComponentIdentifier ret = null;
		
		CmsState cmsstate = getState(agent.getId());
		if(platformname==null)
			platformname = agent.getId().getName();
		ret = new BasicComponentIdentifier(localname+"@"+platformname);
		
		if(uniqueids || cmsstate.getComponent(ret) != null)
		{
			String key = localname+"@"+platformname;
			
			do
			{
				Integer cnt = cmsstate.getCidCounts().get(key);
				if(cnt==null)
				{
					cmsstate.getCidCounts().put(key, Integer.valueOf(1));
					ret = new BasicComponentIdentifier(localname+"@"+platformname);
				}
				else
				{
					cmsstate.getCidCounts().put(key, Integer.valueOf(cnt.intValue()+1));
					ret = new BasicComponentIdentifier(localname+cnt+"@"+platformname); // Hack?!
				}
			}
			while(cmsstate.getComponent(ret) != null);
		}
		
		return ret;
	}
	
	// r: components
	// w: -
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	// todo: support remote? 
	public static IFuture<IComponentDescription[]> searchComponents(final IComponentDescription adesc, final ISearchConstraints con, IInternalAccess agent)//, IServiceIdentifier sid)
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
				CMSComponentDescription	desc = (CMSComponentDescription)ad.clone();
				ret.add(desc);
			}
		}

		// Otherwise search for matching descriptions.
		else
		{
			CmsState cmsstate = getState(agent.getId());
			try(IAutoLock l = cmsstate.readLock())
			{
				for(Iterator<CmsComponentState> it=cmsstate.getComponentMap().values().iterator(); it.hasNext(); )
				{
					CMSComponentDescription	test = (CMSComponentDescription)it.next().getAccess().getInternalAccess().getDescription();
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
		
		fut.setResult((CMSComponentDescription[])ret.toArray(new CMSComponentDescription[ret.size()]));

		return fut;
	}
	
	// r: components
	// w: -
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  
	 *  This method should be used with caution when the agent population is large. <- TODO and the reason is...?
	 */
	public static IFuture<IComponentIdentifier[]> getComponentIdentifiers(IInternalAccess agent)
	{
		CmsState cmsstate = getState(agent.getId());
		try(IAutoLock l = cmsstate.readLock())
		{
			Set<IComponentIdentifier> cids = cmsstate.getComponentMap().keySet();
			return new Future<IComponentIdentifier[]>((IComponentIdentifier[])cids.toArray(new IComponentIdentifier[cids.size()]));
		}
	}
	
	// r: components
	// w: 
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public static IFuture<IComponentDescription[]> getComponentDescriptions(IInternalAccess agent)
	{
		Future<IComponentDescription[]> fut = new Future<IComponentDescription[]>();
		
		IComponentDescription[] ret = null;
		
		CmsState cmsstate = getState(agent.getId());
		try(IAutoLock l = cmsstate.readLock())
		{
			ret = new IComponentDescription[cmsstate.getComponentMap().size()];
			int i=0;
			for(Iterator<CmsComponentState> it=cmsstate.getComponentMap().values().iterator(); i<ret.length; i++)
			{
				ret[i] = (IComponentDescription)((CMSComponentDescription)it.next().getAccess().getInternalAccess().getDescription()).clone();
			}
		}
		
		fut.setResult(ret);
		return fut;
	}
	
	// r: components, initinfos
	// w: -
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public static IFuture<IComponentDescription> getComponentDescription(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentDescription> ret = new Future<IComponentDescription>();
		
		CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
		// Hack, to retrieve description from component itself in init phase
		if(desc==null)
		{
			CmsState state = getState(agent.getId());
			if(state!=null)
			{
				InitInfo ii = state.getInitInfo(cid);
				if(ii!=null)
					desc = (CMSComponentDescription)ii.getComponent().getInternalAccess().getDescription();
			}
		}
					
		if(desc!=null)
		{
			ret.setResult(desc);
		}
		else
		{
			ret.setException(new ComponentNotFoundException("No description available for: "+cid));
		}		
		
		return ret;
	}
	
	// r: components
	// w: -
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public static IFuture<IComponentDescription[]> getChildrenDescriptions(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentDescription[]>	ret	= new Future<IComponentDescription[]>();
		
		try(IAutoLock l = getState(agent.getId()).readLock())
		{
			CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
				IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
			IComponentDescription[]	descs	= new IComponentDescription[tmp.length];
			for(int i=0; i<descs.length; i++)
			{
				descs[i] = (IComponentDescription)SComponentManagementService.getDescription(tmp[i]);
				assert descs[i]!=null;
			}
			ret.setResult(descs);
		}
		
		return ret;
	}
	
	// r: components
	// w: -
	/**
	 *  Get the children count.
	 *  @param cid The component identifier.
	 *  @return The child count
	 */
	public static IFuture<Integer> getChildCount(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<Integer> ret = new Future<Integer>();
		
		try(IAutoLock lock = getState(agent.getId()).readLock())
		{
			CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
			IComponentIdentifier[] tmp = desc!=null? desc.getChildren()!=null? desc.getChildren(): 
				IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS: IComponentIdentifier.EMPTY_COMPONENTIDENTIFIERS;
			
			ret.setResult(tmp.length);
		}
		
		return ret;
	}
	
	// r: components
	// w: -
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public static IFuture<IComponentIdentifier[]> getChildren(final IComponentIdentifier cid, IInternalAccess agent)
	{
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		
		IComponentIdentifier[] tmp = internalGetChildren(cid);
//		System.out.println("children: "+cid+" "+Arrays.toString(tmp));
		ret.setResult(tmp);
		
		return ret;
	}
	
	/**
	 *  Get the parent component of a component.
	 *  @param platform The component identifier.
	 *  @return The parent component identifier.
	 */
	public static IComponentIdentifier getParentIdentifier(CreationInfo ci, IInternalAccess agent)
	{
//		IComponentIdentifier ret = ci!=null && ci.getParent()!=null ? ci.getParent() : agent.getId().getRoot(); //FIXME 
		IComponentIdentifier ret = agent.getId();
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
//			&& (ci.getParent()==null || !isRemoteComponent(ci.getParent(), agent)) //FIXME: PARENT
//			&& !initinfos.containsKey(ci.getParent())	// does not work during init as external access is not available!?
//			&& !Boolean.TRUE.equals(ci.getPlatformloader()))
			)
		{
			//IExternalAccess ea = getExternalAccess(ci.getParent()==null? agent.getId(): ci.getParent(), agent); //FIXME: PARENT
			IExternalAccess ea = getExternalAccess(agent.getId(), agent);
			ea.getModelAsync().addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IModelInfo, IResourceIdentifier>(ret)
			{
				public void customResultAvailable(IModelInfo model)
				{
					ret.setResult(model.getResourceIdentifier());
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
	public static IExternalAccess getExternalAccess(final IComponentIdentifier cid, IInternalAccess agent)
	{
		IExternalAccess ret = null;
		
		if(cid==null)
			throw new IllegalArgumentException("Identifier is null.");
		
		if(isRemoteComponent(cid, agent))
		{
//			System.out.println("getExta remote: "+cid);
			try
			{
				Class<?>[] interfaces = new Class[]{IExternalAccess.class};
				ProxyInfo pi = new ProxyInfo(interfaces);
				pi.addMethodReplacement(new MethodInfo("equals", new Class[]{Object.class}), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return Boolean.valueOf(args[0]!=null && ProxyFactory.isProxyClass(args[0].getClass())
							&& ProxyFactory.getInvocationHandler(obj).equals(ProxyFactory.getInvocationHandler(args[0])));
					}
				});
				pi.addMethodReplacement(new MethodInfo("hashCode", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return Integer.valueOf(ProxyFactory.getInvocationHandler(obj).hashCode());
					}
				});
				pi.addMethodReplacement(new MethodInfo("toString", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return "Fake proxy for external access("+cid+")";
					}
				});
				pi.addMethodReplacement(new MethodInfo("getId", new Class[0]), new IMethodReplacement()
				{
					public Object invoke(Object obj, Object[] args)
					{
						return cid;
					}
				});
				Method getclass = SReflect.getMethod(Object.class, "getClass", new Class[0]);
				pi.addExcludedMethod(new MethodInfo(getclass));
				
				RemoteReference rr = new RemoteReference(cid, cid);
				ProxyReference pr = new ProxyReference(pi, rr);
				InvocationHandler handler = new RemoteMethodInvocationHandler(agent, pr);
				IExternalAccess ea = (IExternalAccess)ProxyFactory.newProxyInstance(agent.getClassLoader(), 
					interfaces, handler);
				ret = ea;
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
		else
		{
			CmsState state = getState(agent.getId());
			try(IAutoLock l = state.readLock())
			{
	//			System.out.println("getExternalAccess: local");
				IPlatformComponentAccess component = null;
	//			System.out.println("getExternalAccess: adapters");
				component = state.getAccess(cid);
				
				if(component==null)
				{
					InitInfo ii = state.getInitInfo(cid);
					if(ii!=null)
						component = ii.getComponent();
				}
				
				if(component!=null)
				{
					ret = component.getInternalAccess().getExternalAccess();
				}
				else
				{
					throw new ComponentNotFoundException("No local component found for component identifier: "+cid);
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
	public static IFuture<Void> setComponentBreakpoints(final IComponentIdentifier cid, final String[] breakpoints, IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		CMSComponentDescription ad = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
		ad.setBreakpoints(breakpoints);
		
		SComponentManagementService.notifyListenersChanged(ad);
		
		ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 */
	public static IFuture<Void> stepComponent(final IComponentIdentifier cid, final String stepinfo, IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		final IPlatformComponentAccess adapter = getState(agent.getId()).getAccess(cid);
		if(adapter!=null)
		{
			((IInternalExecutionFeature)adapter.getInternalAccess().getFeature(IExecutionFeature.class)).doStep(stepinfo)
				.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
		}
		else
		{
			ret.setException(new ComponentNotFoundException("Component identifier not registered: "+cid));
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
		
		// Resume subcomponents
		final CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cid);
		IComponentIdentifier[] achildren = desc!=null ? desc.getChildren() : null;

		if(desc!=null)
		{
			IResultListener<Void> lis = createResultListener(agent, new CounterResultListener<Void>(achildren.length, true, new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					final CmsState state = getState(agent.getId());
					final IPlatformComponentAccess adapter = state.getAccess(cid);
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
							CmsComponentState compstate = state.getComponent(cid);
							// Not killed during init.
							if(compstate.getCleanupFuture() == null)
							{
								try(IAutoLock l = state.writeLock())
								{
									InitInfo ii = compstate.getInitInfo();
									compstate.setInitInfo(null);
		//							System.out.println("removed: "+cid+" "+ii);
									if(ii!=null)
									{
										boolean	suspend = isInitSuspend(ii.getInfo(), adapter.getInternalAccess().getModel(), agent);
										
//										System.out.println("cid: "+cid+" "+suspend);
										if(suspend)
										{
											desc.setState(IComponentDescription.STATE_SUSPENDED);
											changed	= true;
										}
									}
								}
								
								// release before outbound call body()
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
											IComponentIdentifier pid = adapter.getInternalAccess().getId().getParent();
											IPlatformComponentAccess pacom = getComponent(pid);
											// hmm wait for call to finish?!
											pacom.childTerminated(adapter.getInternalAccess().getDescription(), exception); 
											adapter.getInternalAccess().killComponent(exception);
										}
									}
								}));
							}
								
							// Killed after init but before init resume -> execute queued destroy.
							else if(state.getInitInfo(cid)!=null)
							{
								try(IAutoLock l = state.writeLock())
								{
									compstate.setInitInfo(null);
									
									System.out.println("init resume remove cleanup future: "+cid);
									compstate.setCleanupFuture(null);
								}
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
		
		CMSComponentDescription desc = null;
		boolean notifylisteners = false;
		CmsState state = getState(agent.getId());
		try(IAutoLock l = state.writeLock())
		{
			final IPlatformComponentAccess comp = state.getAccess(cid);
			if(comp!=null)
			{
				desc = (CMSComponentDescription)comp.getInternalAccess().getDescription();
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
					notifylisteners = true;
					
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
		
		if (notifylisteners)
			SComponentManagementService.notifyListenersChanged(desc);
		
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
	 *  This method is guaranteed to be called exactly ONCE and represents
	 *  the internal destroy method that performs the actual cleanup work.
	 *	@param cid The component to destroy.
	 *  @param ret The future to be informed.
	 */
	protected static void destroyComponent(final IComponentIdentifier cid,	final Future<Map<String, Object>> ret, IInternalAccess agent)
	{
		CmsState state = getState(cid);
		InitInfo infos = state.getInitInfo(cid);
		IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : state.getAccess(cid);
		
		// Terminate component that is shut down during init.
		
		// todo:
		if(infos!=null && !infos.getInitFuture().isDone())
		{
			// Propagate failed component init.
			if(comp!=null && comp.getInternalAccess().getException()!=null)
			{
				infos.getInitFuture().setException(comp.getInternalAccess().getException());
			}
			
			// Component terminated from outside: wait for init to complete, will be removed as cleanup future is registered (cfs).
			else
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
//				else
//					System.out.println("no children: "+cid);
			
//				System.out.println("Killing: " + agent + " " + Arrays.toString(achildren));
//				resumeComponent(cid, false, agent);
//				System.out.println("Resumed: " + agent);
			
			Runnable finishkill = new Runnable()
			{
				public void run()
				{
//						System.out.println("DONE killing: " + agent);
					InitInfo infos	= state.getInitInfo(cid);
					IPlatformComponentAccess comp = infos!=null ? infos.getComponent() : state.getAccess(cid);
					if(comp!=null)
					{
//							// todo: does not work always!!! A search could be issued before components had enough time to kill itself!
//							// todo: killcomponent should only be called once for each component?
						
						agent.getLogger().info("Terminating component: "+cid.getName());
						IResultListener<Void> cc = new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
//									System.out.println("Killed: " + cid);
								cleanup(cid, null);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								cleanup(cid, exception);
							}
						};
						IResultListener<Void> lis = cid.getParent()==null? cc: createResultListener(agent, cc);
						comp.shutdown().addResultListener(lis);
					}
				}
			};
			
			if (achildren != null && achildren.length > 0)
			{
				agent.getFeature(ISubcomponentsFeature.class).killComponents(achildren).addResultListener(new IResultListener<Collection<Tuple2<IComponentIdentifier, Map<String,Object>>>>()
				{
					public void resultAvailable(Collection<Tuple2<IComponentIdentifier, Map<String, Object>>> result)
					{
						finishkill.run();
					}
					public void exceptionOccurred(Exception exception)
					{
//							exception.printStackTrace();
						finishkill.run();
						SComponentManagementService.exitDestroy(cid, desc, exception, null);
					}
				});
			}
			else
			{
				finishkill.run();
			}
		}			
		else
		{
//			state.getComponent(cid).setCleanupFuture(null);
			ret.setException(new ComponentTerminatedException(cid, "Cannot kill, no such component."));
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
		
//		if(cid.toString().indexOf("StreamPro")!=-1)
//			System.out.println("kill: "+cid);
		
		boolean contains = false;
		boolean locked = false;
		boolean inited = false;
		Future<Map<String, Object>> tmp;
		
		CmsState state = getState(agent.getId());
		try(IAutoLock l = state.writeLock())
		{
			CmsComponentState compstate = state.getComponent(cid);
			contains = compstate.getCleanupFuture() != null;
			tmp = contains? (Future<Map<String, Object>>)compstate.getCleanupFuture(): new Future<Map<String, Object>>();
	//		System.out.println("destroy0: "+cid+" "+cfs.containsKey(cid)+" "+tmp.isDone());
	//		Thread.currentThread().dumpStack();
			
			// If destroyComponent has not been called before
			if(!contains)
				compstate.setCleanupFuture(tmp);
			
			// Is the component inited, i.e. init info already remved or init failed with exception and waiting for removal.
			inited = compstate.getInitInfo()==null || compstate.getInitInfo().getInitFuture().isDone();
			
			// Is the component locked?
			LockEntry kt = compstate.getLock();
			if(kt!=null && kt.getLockerCount()>0)
			{
				kt.setKillFuture(tmp);
				locked = true;
			}
		}
		
		final Future<Map<String, Object>> ret = tmp;
		
		if(!contains && !locked && inited)
			destroyComponent(cid, ret, agent);

		return ret;
	}
	
	/**
	 *  Load a model with the following steps:
	 *  - get the resource identifier for the model
	 *  - resolve the filename (local types of subcomponents -> filenames)
	 *  - get a suitable component factory
	 *  - load the model with that factory
	 *  - check if the model has errors and convert to exception
	 */
	public static IFuture<Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>> loadModel(String modelname, CreationInfo cinfo, IInternalAccess agent)
	{
		Future<Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>> ret = new Future<>();
		
		getResourceIdentifier(cinfo, agent).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IResourceIdentifier, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>>(ret)
		{
			public void customResultAvailable(final IResourceIdentifier rid)
			{
				resolveFilename(modelname, cinfo, rid, agent).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Tuple2<String, ClassLoader>, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>>(ret)
				{
					public void customResultAvailable(final Tuple2<String, ClassLoader> tup)
					{
						@SuppressWarnings("unchecked")
						final Map<Tuple2<String, ClassLoader>, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>> modelcache = getState(agent.getId().getRoot()).getModelCache();
						Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>> cacheres = modelcache.get(tup);
						if (cacheres != null)
						{
							ret.setResult(cacheres);
							return;
						}
						
						final String model = tup.getFirstEntity();
						getComponentFactory(model, cinfo, rid, false, false, agent)
							.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IComponentFactory, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>>(ret)
						{
							public void customResultAvailable(final IComponentFactory factory)
							{
//								System.out.println("load: "+model+" "+rid);
								factory.loadModel(model, cinfo.getImports(), rid)
									.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<IModelInfo, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>>(ret)
								{
									@Override
									public void customResultAvailable(IModelInfo result)
									{
										if(result.getReport()!=null)
										{
											ret.setException(new ComponentCreationException("Errors loading model: "+model+"\n"+result.getReport().getErrorText(), 
												ComponentCreationException.REASON_MODEL_ERROR));
										}
										else
										{
											IFuture<Collection<IComponentFeatureFactory>> fut = factory.getComponentFeatures(result);
											fut.addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Collection<IComponentFeatureFactory>, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>>(ret)
											{
												public void customResultAvailable(Collection<IComponentFeatureFactory> features)
												{
													Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>> res = new Tuple3<>(result, tup.getSecondEntity(), features);
													modelcache.put(tup, res);
													ret.setResult(res);
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
		
		return ret;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected static boolean isSystemComponent(String name, IModelInfo lmodel, CreationInfo cinfo, IInternalAccess agent)
	{
		// check if system component is located in system tree
		Map<String, Object> props = lmodel.getProperties();
		
		IComponentIdentifier pacid = getParentIdentifier(cinfo, agent);
		
		boolean ret = "system".equals(name) && pacid.getParent()==null;
		
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
				ret = true;
		}
		
		// Check if system is used in service (one declared system service is enough for component being systemcomponent)
		if(!ret)
		{
			ProvidedServiceInfo[] psis = lmodel.getProvidedServices();
			if(psis!=null)
			{
				for(ProvidedServiceInfo psi: psis)
				{
					// Hack cast
					Class<?> iftype = psi.getType().getType(((ModelInfo)lmodel).getClassLoader());
					ret = jadex.bridge.service.ServiceIdentifier.isSystemService(iftype);
					if(ret)
						break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add the locking of the parent while subcomponent is created.
	 */
	protected static void addParentLocking(CreationInfo cinfo, IInternalAccess agent, Future<IComponentIdentifier> inited)
	{
		IComponentIdentifier parent = agent.getId(); //FIXME: PARENT
		// Lock the parent while creating child
		final String lockkey = SUtil.createUniqueId("lock");
		final CmsState state = getState(agent.getId());
		CmsComponentState parstate = state.getComponent(parent);
		LockEntry kt = parstate.getLock();
		if(kt==null)
		{
			kt = new LockEntry(parent);
			parstate.setLock(kt);
		}
		kt.addLocker(lockkey);
		inited.addResultListener(createResultListener(agent, new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier result)
			{
				// Lock is lost on future callback? get it back???!?
				try(IAutoLock l = state.writeLock())
				{
					LockEntry kt = parstate.getLock();
					if(kt!=null)
					{
						if(kt.removeLocker(lockkey))
							destroyComponent(kt.getLocked(), kt.getKillFuture(), agent);
						if(kt.getLockerCount()==0)
						{
							parstate.setLock(null);
						}
					}
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				// Lock is lost on future callback? get it back???!?
				try(IAutoLock l = state.writeLock())
				{
					LockEntry kt = parstate.getLock();
					if(kt!=null)
					{
						if(kt.removeLocker(lockkey))
							destroyComponent(kt.getLocked(), kt.getKillFuture(), agent);
						if(kt.getLockerCount()==0)
						{
							parstate.setLock(null);
						}
					}
				}
			}
		}));
	}
	
	/**
	 * 
	 */
	public static void linkResults(IResultListener<Collection<Tuple2<String, Object>>> resultlistener, IPlatformComponentAccess component, IInternalAccess agent)
	{
		if(resultlistener!=null)
		{
			IComponentIdentifier cid = component.getInternalAccess().getId();
			
			IArgumentsResultsFeature af = component.getInternalAccess().getFeature0(IArgumentsResultsFeature.class);
			IResultListener<Collection<Tuple2<String, Object>>>	rl = new IIntermediateResultListener<Tuple2<String, Object>>()
			{
				public void exceptionOccurred(final Exception exception)
				{
					// Wait for cleanup finished before posting results
					getState(agent.getId()).getCleanupFuture(cid).addResultListener(new IResultListener<Map<String,Object>>()
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
					IFuture<Map<String, Object>> fut = getState(agent.getId()).getCleanupFuture(cid);
					if(fut!=null) // TODO: why null (seldom in RemoteBlockingTestAgent during gradle build)
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
						// Although order of kill is
						// a) component.sutdown(), argumentsfeature notifies listener via setFinished(), listener copies values but needs future
						// b) cleanup in cms (remove cleanup future)
						// order is nondeterministic
						
						// todo: use values from future
						Collection<Tuple2<String, Object>>	results	= new ArrayList<Tuple2<String,Object>>();
						Map<String, Object> result = af.getResults();
						for(Map.Entry<String, Object> entry: result.entrySet())
						{
							results.add(new Tuple2<String, Object>(entry.getKey(), entry.getValue()));
						}
						resultlistener.resultAvailable(results);
//						System.out.println("No cleanup future for: "+cid);
//						resultlistener.exceptionOccurred(new NullPointerException("No cleanup future!? "+cid));
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
				af.subscribeToResults().addResultListener(rl);
		}
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info	The creation info, if any.
	 *  @param listener The result listener (if any). Will receive the id of the component as result, when the component has been created.
	 *  @param resultlistener The kill listener (if any). Will receive the results of the component execution, after the component has terminated.
	 */
	// rw: cleanup futures, (loadModel), lock entries, init infos, components
	public static IFuture<IComponentIdentifier> createComponent(final String oname, final String modelname, CreationInfo info, 
		final IResultListener<Collection<Tuple2<String, Object>>> resultlistener, IInternalAccess agent)
	{
//		IComponentIdentifier ciparent = agent.getId(); // FIXME: PARENT
		if(modelname==null)
			return new Future<IComponentIdentifier>(new IllegalArgumentException("Error creating component: " + oname + " : Modelname must not be null."));

//		if(modelname.indexOf("Super")!=-1)
//			System.out.println("create: "+oname+" "+modelname+" on "+agent.getId());
		
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		final IComponentIdentifier creator = sc==null? null: sc.getCaller();
//		final Cause curcause = sc==null? agent.getDescription().getCause(): sc.getCause();
		
		final Future<IComponentIdentifier> inited = new Future<IComponentIdentifier>();
		final Future<Void> resfut = new Future<Void>();
		
		final CreationInfo cinfo = new CreationInfo(info);	// Dummy default info, if null. Must be cloned as localtype is set on info later.
		
		// Check if parent is killing itself -> no new child component, exception
		if(getState(agent.getId()).getCleanupFuture(agent.getId()) != null)
			return new Future<IComponentIdentifier>(new ComponentTerminatedException(agent.getId() ,"Parent is killing itself. Child component creation no allowed."));
		
		if(isRemoteComponent(agent.getId(), agent))
		{
			inited.setException(new IllegalArgumentException("Cannot locally create component for remote component" + agent));
		}
		else if(oname!=null && oname.indexOf('@')!=-1)
		{
			inited.setException(new ComponentCreationException("No '@' allowed in component name.", ComponentCreationException.REASON_WRONG_ID));
		}
		else
		{
//			System.out.println("create start1: "+model+" "+cinfo.getParent());
			
			// Load the model and get also classloader and component features
			loadModel(modelname, cinfo, agent).addResultListener(createResultListener(agent, new ExceptionDelegationResultListener<Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>, IComponentIdentifier>(inited)
			{
				public void customResultAvailable(final Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>> t)
				{
					IPlatformComponentAccess component = null;
					ComponentCreationInfo cci = null;
					Collection<IComponentFeatureFactory> features = null;
					BasicComponentIdentifier tmpcid = null;
					CMSComponentDescription tmpad = null;
					IInternalAccess tmppad = null;
					IModelInfo tmplmodel = null;
					
					CmsState state = getState(agent.getId());
					try(IAutoLock l = state.writeLock())
					{
						tmplmodel = t.getFirstEntity();
						ClassLoader cl = t.getSecondEntity();
						features = t.getThirdEntity(); 
						final String name = (String)SJavaParser.evaluateExpressionPotentially(oname, tmplmodel.getAllImports(), null, cl);
						
						// Create id and adapter.
						
						boolean systemcomponent = isSystemComponent(modelname, tmplmodel, cinfo, agent);
						
						addParentLocking(cinfo, agent, inited);
		
						tmppad = getParentComponent(cinfo, agent);
						IExternalAccess parent = tmppad.getExternalAccess();
						IComponentIdentifier pacid = parent.getId();
		
						String paname = pacid.getName().replace('@', ':');
						
						// TODO!!!!! use unique setting
						
						// The name is generated using a) the defined name else b) the name hint c) the model name as basis
						tmpcid = (BasicComponentIdentifier)generateComponentIdentifier(name!=null? name: tmplmodel.getNameHint()!=null? tmplmodel.getNameHint(): tmplmodel.getName(), paname, agent, true);//, addresses);
						
						// Defer component services being found from registry
						ServiceRegistry.getRegistry(agent).addExcludedComponent(tmpcid);
						
	//					Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
	//					Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
	//					Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
						Boolean sync = cinfo.getSynchronous()!=null? cinfo.getSynchronous(): tmplmodel.getSynchronous(cinfo.getConfiguration());
	//					Boolean persistable = cinfo.getPersistable()!=null? cinfo.getPersistable(): lmodel.getPersistable(cinfo.getConfiguration());
						PublishEventLevel moni = cinfo.getMonitoring()!=null? cinfo.getMonitoring(): tmplmodel.getMonitoring(cinfo.getConfiguration());
						// Inherit monitoring from parent if null
						if(moni==null)
						{
							CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(agent.getId());
							moni = desc.getMonitoring();
						}
						
	//					Cause cause = curcause;
						// todo: how to do platform init so that clock is always available?
						IClockService cs = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(
							new ServiceQuery<>(IClockService.class).setMultiplicity(Multiplicity.ZERO_ONE));
	//					final CMSComponentDescription ad = new CMSComponentDescription(cid, lmodel.getType(), master!=null ? master.booleanValue() : false,
	//						daemon!=null ? daemon.booleanValue() : false, autosd!=null ? autosd.booleanValue() : false, sync!=null ? sync.booleanValue() : false,
	//						persistable!=null ? persistable.booleanValue() : false, moni,
	//						lmodel.getFullName(), cinfo.getLocalType(), lmodel.getResourceIdentifier(), cs!=null? cs.getTime(): System.currentTimeMillis(), creator, systemcomponent);
						tmpad = new CMSComponentDescription(tmpcid).setType(tmplmodel.getType()).setModelName(tmplmodel.getFullName()).setLocalType(cinfo.getLocalType())
							.setResourceIdentifier(tmplmodel.getResourceIdentifier()).setCreator(creator).setSystemComponent(systemcomponent).setCreationTime(cs!=null? cs.getTime(): System.currentTimeMillis())
							.setSynchronous(sync!=null ? sync.booleanValue() : false).setFilename(tmplmodel.getFilename());
						
						// Use first configuration if no config specified.
						String config	= cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
							: tmplmodel.getConfigurationNames().length>0 ? tmplmodel.getConfigurationNames()[0] : null;
						
						component = createPlatformComponent(cl);
						
						cci = new ComponentCreationInfo(tmplmodel, config, cinfo.getArguments(), tmpad, cinfo.getProvidedServiceInfos(), cinfo.getRequiredServiceBindings());
					}
					
					final CMSComponentDescription ad = tmpad;
					final IInternalAccess pad = tmppad;
					final IModelInfo lmodel = tmplmodel;
					final BasicComponentIdentifier cid = tmpcid;
					
					// Invoke create on platform component
					component.create(cci, features);
					
					boolean tmpdoinit = false;
					try(IAutoLock l = state.writeLock())
					{
						linkResults(resultlistener, component, agent);
						
						CmsComponentState compstate = new CmsComponentState();
						state.getComponentMap().put(cid, compstate);
						compstate.setInitInfo(new InitInfo(component, cinfo, resfut));
						
						// Start regular execution of inited component
						// when this component is the outermost component, i.e. with no parent
						// or the parent is already running
						tmpdoinit = state.getInitInfo(agent.getId())==null;
						
						agent.getLogger().info("Starting component: "+cid.getName());
		//				System.err.println("Pre-Init: "+cid);
						
					}
					final boolean doinit = tmpdoinit;
					
					resfut.addResultListener(createResultListener(agent, new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							try(IAutoLock l = state.writeLock())
							{
								agent.getLogger().info("Started component: "+cid.getName());
		
								ServiceRegistry.getRegistry(agent).removeExcludedComponent(cid);
								
								// Init successfully finished. Add description and adapter.
								CmsComponentState compstate = state.getComponent(cid);
								InitInfo info = compstate.getInitInfo();
								
								// Init finished. Set to suspended until parent registration is finished.
								// not set to suspend to allow other initing sibling components invoking services
								
								compstate.setAccess(info.getComponent());
							}
							
							// Register component at parent.
							addSubcomponent(pad, ad, lmodel, agent)
								.addResultListener(createResultListener(agent, new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									// Registration finished -> reactivate component.
									
									// todo: can be called after listener has (concurrently) deregistered
									// notify listeners without holding locks
									SComponentManagementService.notifyListenersAdded(ad);
											
									inited.setResult(cid);
									
									Future<Map<String, Object>>	killfut;
									CmsState state = getState(agent.getId());
									killfut	= (Future<Map<String, Object>>)state.getCleanupFuture(cid);
									if(killfut!=null)
									{
										try(IAutoLock l = state.writeLock())
										{
											// Remove init infos otherwise done in resume()
											List<IComponentIdentifier>	cids = new ArrayList<IComponentIdentifier>();
											cids.add(cid);
											for(int i=0; i<cids.size(); i++)
											{
												state.getComponent(cids.get(i)).setInitInfo(null);
												CMSComponentDescription	desc = (CMSComponentDescription)SComponentManagementService.getDescription((IComponentIdentifier)cids.get(i));
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
										
										// Kill component if destroy called during init.
										destroyComponent(cid, killfut, agent);
									}
									else
									{
										if(doinit)
										{
	//										System.out.println("start: "+cid);
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
							
	//						System.err.println("Starting component failed: "+cid+", "+exception);
	//						exception.printStackTrace();
	//						System.out.println("Ex: "+cid+" "+exception);
							final Runnable cleanup = new Runnable()
							{
								public void run()
								{
									IPlatformComponentAccess comp = getComponent(cid);
									
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
		return inited;
	}
	
	/**
	 *  Create a platform component.
	 *  It creates a proxy around the platform component to autoimplement the feature methods of internal access.
	 */
	public static IPlatformComponentAccess createPlatformComponent(ClassLoader classloader)
	{
		final PlatformComponent comp = new PlatformComponent();
		
		IPlatformComponentAccess ret = (IPlatformComponentAccess)ProxyFactory.newProxyInstance(classloader, new Class[]{IInternalAccess.class, IPlatformComponentAccess.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				Object ret = null;
				try
				{
					Class<?> iface = method.getDeclaringClass();
					String name = SReflect.getClassName(iface);
					String intname = SUtil.replaceLast(name, "External", "");
//					System.out.println(name+" "+intname);
					
					ClassLoader cl = comp.getClassLoader()!=null? comp.getClassLoader(): classloader;
					Class<?> clazz = SReflect.findClass0(intname, null, cl);
					Object feat = clazz!=null? comp.getFeature0(clazz): null;
					
					if(feat==null)
					{
						String mname = method.getName();
//						int idx = mname.lastIndexOf("Async");
//						if(idx>0)
//							mname = mname.substring(0, idx);
						Method m = PlatformComponent.class.getMethod(mname, method.getParameterTypes());
						ret = m.invoke(comp, args);
					}
					else
					{
						ret = method.invoke(feat, args);
					}
				}
				catch(Exception e)
				{
					if(SReflect.isSupertype(IFuture.class, method.getReturnType()))
					{
						ret = SFuture.getFuture(method.getReturnType());
						((Future)ret).setException(e);
					}
					else
					{
						throw e;
					}
				}
				return ret;
			}
		});
		
		// Hack to combine proxy with component
		comp.setInternalAccess((IInternalAccess)ret);
		
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
				ret.addIntermediateResultIfUndone(new CMSTerminatedEvent(SComponentManagementService.getDescription(mycid[0]), results, null));
				ret.setFinishedIfUndone();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.addIntermediateResultIfUndone(new CMSTerminatedEvent(SComponentManagementService.getDescription(mycid[0]), results, exception));
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
		// No timeout is issued since this is not a service call. NoTimeoutFuture causes 100% CPU in
		// simulation mode if the platform is idle.
		final Tuple2Future<IComponentIdentifier, Map<String, Object>> ret = new Tuple2Future<IComponentIdentifier, Map<String,Object>>();
//		final Tuple2Future<IComponentIdentifier, Map<String, Object>> ret = (Tuple2Future<IComponentIdentifier, Map<String,Object>>)SFuture.getNoTimeoutFuture(Tuple2Future.class, agent);
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
	 * 
	 */
	// r: components
	// w: initinfos 
	protected static void cleanup(IComponentIdentifier cid, Exception exception)
	{
		IPlatformComponentAccess comp = null;
		IPlatformComponentAccess pad = null;
		boolean	killparent	= false;
		boolean notifylis = false;
		Exception ex = null;
		CMSComponentDescription desc;
		Map<String, Object> results = null;
		
		CmsState state = getState(cid);
		try(IAutoLock l = state.writeLock())
		{
	//		System.out.println("doCleanup: "+cid);
	//		logger.info("Terminated component: "+cid.getName());
			
	//		System.out.println("CleanupCommand remove called for: "+cid);
//			assert compstate!=null: "Should be available.";
			CmsComponentState compstate = state.getComponentMap().get(cid);
			if (compstate != null)
			{
				comp = compstate.getAccess();
				if (comp == null)
				{
					InitInfo ii = compstate.getInitInfo();
					assert ii!=null: "Should be either in 'components' or 'initinfos'.";
					comp = ii.getComponent();
					compstate.setInitInfo(null);
				}
				else
				{
					compstate.setAccess(null);
				}
			}
			else
			{
				throw new ComponentNotFoundException("Component Identifier not registered: "+cid);
			}
//			comp = SComponentManagementService.getComponents(cid).remove(cid);
//			if(comp==null)
//			{
//				InitInfo	ii	= SComponentManagementService.getInitInfos(cid).remove(cid);
//				assert ii!=null: "Should be either in 'components' or 'initinfos'.";
//				comp = ii.getComponent();
//			}
			
			comp.getInternalAccess().getLogger().info("Terminated component: "+cid.getName());
			
			desc = (CMSComponentDescription)comp.getInternalAccess().getDescription();
			
			IArgumentsResultsFeature af = comp.getInternalAccess().getFeature0(IArgumentsResultsFeature.class); 
			results = af!=null ? af.getResults() : null;
	
			// Deregister destroyed component at parent.
			if(desc.getName().getParent()!=null)
			{
				// Stop execution of component. When root component services are already shutdowned.
				
	//			killparent = desc.isMaster();
				CMSComponentDescription padesc = (CMSComponentDescription)SComponentManagementService.getDescription(desc.getName().getParent());
				if(padesc!=null)
				{
					padesc.removeChild(desc.getName());
	//				if(!desc.isDaemon())
	//				{
	//					pad	= getComponent(padesc.getName());
	//					
	//					int cc = -1;
	//					if(pad!=null)
	//						cc = pad.getInternalAccess().getFeature(ISubcomponentsFeature.class).decChildcount();
	//					killparent = killparent || (padesc.isAutoShutdown() && cc<=0);
	//				}
				}
				pad	= state.getAccess(desc.getName().getParent());
				
				// todo: wait for result?!
				if(pad!=null)
					((IInternalSubcomponentsFeature)pad.getInternalAccess().getFeature(ISubcomponentsFeature.class)).componentRemoved(desc);
			}
	
			// Must be executed out of sync block due to deadlocks
			// agent->cleanupcommand->space.componentRemoved (holds adapter mon -> needs space mone)
			// space executor->general loop->distributed percepts->(holds space mon -> needs adapter mon for getting external access)
			
			// todo:
	//		if(pad!=null)
	//		{
	//			try
	//			{
	//				pad.componentDestroyed(desc);
	//			}
	//			catch(ComponentTerminatedException cte)
	//			{
	//				// Parent just killed: ignore.
	//			}
	//		}
			// else parent has just been killed.
			
			// Use adapter exception before cleanup exception as it probably happened first.
			ex = comp.getInternalAccess().getException()!=null ? comp.getInternalAccess().getException() : exception;
			
			SComponentManagementService.exitDestroy(cid, desc, ex, results);
			
			notifylis = true;
			
			if(ex!=null && !(ex instanceof StepAbortedException))
			{
				// Unhandled component exception
				if(af!=null && ((IInternalArgumentsResultsFeature)af).exceptionNotified())
				{
					// Delegated exception to some listener, only print info.
	//					comp.getInternalAccess().getLogger().info("Fatal error, component '"+cid+"' will be removed due to "+ex);
					System.out.println("Fatal error, component '"+cid+"' will be removed due to "+ex);
				}
				else
				{
					// No listener -> print exception.
	//					comp.getInternalAccess().getLogger().severe("Fatal error, component '"+cid+"' will be removed.\n"+SUtil.getExceptionStacktrace(ex));
					System.out.println("Fatal error, component '"+cid+"' will be removed.\n"+SUtil.getExceptionStacktrace(ex));
				}
			}
			
	//		System.out.println("CleanupCommand end.");
		}
		
		// Release before outbound call destroyComponent()
		
		if (notifylis)
			SComponentManagementService.notifyListenersRemoved(desc, ex, results);
		
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
//			System.out.println("killparent: "+pad.getComponentIdentifier());
			SComponentManagementService.destroyComponent(pad.getInternalAccess().getId(), (IInternalAccess)comp);
		}
		
		try (IAutoLock l = state.writeLock())
		{
			state.getComponentMap().remove(cid);
		}
		
		if(cid.getRoot().equals(cid))
		{
//			System.out.println("removed: "+cid);
			Starter.removePlatformMemory(cid);
		}
	}
	
	/**
	 *  Helper to get a component from components or init infos.
	 *  @param cid The component id.
	 *  @return The component.
	 */
	protected static IPlatformComponentAccess getComponent(IComponentIdentifier cid)
	{
		CmsState state = getState(cid);
		IPlatformComponentAccess ret = null;
		if(state != null)
		{
			try
			{
				try(IAutoLock l = state.readLock())
				{
					CmsComponentState compstate = state.getComponent(cid);
					if (compstate != null)
					{
						ret = compstate.getAccess();
						if(ret==null)
						{
							InitInfo ii = compstate.getInitInfo();
							if(ii!=null)
								ret	= ii.getComponent();
						}
					}
				}
			}
			finally
			{
			}
		}
		
		return ret;
	}
	
	/** Gets the classloader from libservice. */
	protected static final IFuture<ClassLoader> getClassLoader(ILibraryService libser, IResourceIdentifier rid)
	{
		IComponentIdentifier plat = ((IService) libser).getServiceId().getProviderId().getRoot();
		CmsState state = getState(plat);
		ClassLoader cl = state.getClassLoaders().get(rid);
		if (cl != null)
			return new Future<>(cl);
		
		final Future<ClassLoader> ret = new Future<>();
		libser.getClassLoader(rid).addResultListener(new IResultListener<ClassLoader>()
		{
			public void resultAvailable(ClassLoader result)
			{
				state.getClassLoaders().put(rid, result);
				ret.setResult(result);
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Schedule a step without getting external access.
	 *  @param cid The component id.
	 *  @param step The step.
	 *  @return result of the step or runtime exception when component not found.
	 */
	public static <T> IFuture<T> scheduleStep(IComponentIdentifier cid, IComponentStep<T> step)
	{
		IPlatformComponentAccess comp = getComponent(cid);
		if(comp!=null)
		{
			return comp.getInternalAccess().scheduleStep(step);
		}
		else
		{
			return new Future<T>(new RuntimeException("Component not found to schedule: "+cid));
		}
	}
}
