package jadex.bridge.service.types.cms;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import jadex.base.Starter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.ProxyFactory;
import jadex.bridge.SFuture;
import jadex.bridge.StepAbortedException;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.FeatureNotAvailableException;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IExternalComponentFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.IPropertiesFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.Tuple2Future;

/**
 *  Standalone platform component implementation.
 */
public class PlatformComponent implements IPlatformComponentAccess //, IInternalAccess
{	
	//-------- constants --------
	
	/** Property name for timeout after which component long running cleanup is forcefully aborted. */
	public static final String	PROPERTY_TERMINATION_TIMEOUT	= "termination_timeout";
	
	//-------- attributes --------
	
	/** The internal access. */
	protected IInternalAccess ia;
	
	/** The creation info. */
	protected ComponentCreationInfo	info;
	
	/** The features. */
	protected Map<Class<?>, IComponentFeature>	features;
	
	/** The feature instances as list (for reverse execution, cached for speed). */
	protected List<IComponentFeature>	lfeatures;
	
	/** The inited feature instances as list (for shutdown after failed init). */
	protected List<IComponentFeature>	ifeatures;
	
	/** The logger. */
	protected Logger logger;
	
	/** The failure reason (if any). */
	protected Exception	exception;
	
	/** The combined value fetcher (cached for speed). */
	protected IValueFetcher	fetcher;
	
	/** The shutdown flag (set on start of shutdown). */
	protected boolean shutdown;
	
	//-------- IPlatformComponentAccess interface --------
	
	/**
	 *  Create the component, i.e. instantiate its features.
	 *  This is the first method that is called by the platform.
	 *  
	 *  @param info The component creation info.
	 *  @param platformdata The shared objects for all components of the same platform (registry etc.). See starter for available data.
	 *  @param facs The factories for component features to be instantiated for this component.
	 */
	public void	create(ComponentCreationInfo info, Collection<IComponentFeatureFactory> facs)
	{
//		state = ComponentLifecycleState.CREATE;
		
		this.info	= info;
		this.features	= new LinkedHashMap<Class<?>, IComponentFeature>();
		this.lfeatures	= new ArrayList<IComponentFeature>();

		for(IComponentFeatureFactory fac: facs)
		{
//			System.out.println("feature: "+fac);
			IComponentFeature	instance	= fac.createInstance(getInternalAccess(), info);
			features.put((Class<?>)fac.getType(), instance);
			for(Class<?> ltype: fac.getLookupTypes())
				features.put(ltype, instance);
			lfeatures.add(instance);
		}
	}
	
	/**
	 *  Set the internal access (proxy).
	 *  @param ia The internal access.
	 */
	protected void setInternalAccess(IInternalAccess ia)
	{
		this.ia = ia;
	}
	
	/**
	 *  Perform the initialization of the component.
	 *  Tries to switch to a separate thread for the component as soon as possible.
	 *  
	 *  @return A future to indicate when the initialization is done.
	 */
	public IFuture<Void>	init()
	{
//		state = ComponentLifecycleState.INIT;
		
//		if(getComponentIdentifier().getName().indexOf("VSIS")!=-1)
//			System.out.println("init of: "+getComponentIdentifier());
		
		// Run init on component thread (hack!!! requires that execution feature works before its init)
		IExecutionFeature exe	= getFeature(IExecutionFeature.class);
		return exe.scheduleStep(new ImmediateComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ifeatures	= new ArrayList<IComponentFeature>();
				return executeInitOnFeatures(lfeatures.iterator());
			}
		});
	}
	
	/**
	 *  Perform the main execution of the component (if any).
	 *  
	 *  @return A future to indicate when the body is done.
	 */
	public IFuture<Void>	body()
	{
//		state = ComponentLifecycleState.BODY;
		
		IExecutionFeature exe	= getFeature(IExecutionFeature.class);

		return exe.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return executeBodyOnFeatures(lfeatures.iterator());
			}
			
			@Override
			public String toString()
			{
				return "PlatformComponent.body("+getId()+")";
			}
		});
	}
	
	/**
	 *  Perform the shutdown of the component (if any).
	 *  
	 *  @return A future to indicate when the shutdown is done.
	 */
	public IFuture<Void>	shutdown()
	{
		shutdown	= true;
//		state = ComponentLifecycleState.END;
		
//		System.out.println("shutdown component features start: "+getComponentIdentifier());
		IExecutionFeature exe	= getFeature(IExecutionFeature.class);
		return exe.scheduleStep(new ImmediateComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
				
//				if(getComponentIdentifier().getName().indexOf("Leaker")!=-1)
//					System.out.println("shutdown component features start: "+getComponentIdentifier()+", "+ifeatures +", "+ lfeatures);
				executeShutdownOnFeatures(ifeatures!=null ? ifeatures : lfeatures)
					.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						proceed(null);
					}

					public void exceptionOccurred(Exception exception)
					{
						proceed(exception);
					}
					
					public void proceed(final Exception ex)
					{
//						if(getComponentIdentifier().getName().indexOf("Leaker")!=-1)
//							System.out.println("shutdown component features end: "+getComponentIdentifier()+", "+ex);
						if(getFeature0(IMonitoringComponentFeature.class)!=null 
							&& getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
						{
//							if(getComponentIdentifier().getName().indexOf("Feature")!=-1)
//								System.out.println("shutdown component features end1: "+getComponentIdentifier()+", "+ex);
							MonitoringEvent event = new MonitoringEvent(getDescription().getName(), getDescription().getCreationTime(),
//								IMonitoringEvent.TYPE_COMPONENT_DISPOSED, getDescription().getCause(), System.currentTimeMillis(), PublishEventLevel.COARSE);
								IMonitoringEvent.TYPE_COMPONENT_DISPOSED, System.currentTimeMillis(), PublishEventLevel.COARSE);
							event.setProperty("details", getDescription());
							getFeature(IMonitoringComponentFeature.class).publishEvent(event, PublishTarget.TOALL);
//								.addResultListener(new IResultListener<Void>()
//							{
//								public void resultAvailable(Void result)
//								{
////									if(getComponentIdentifier().getName().indexOf("Feature")!=-1)
////										System.out.println("shutdown component features end2: "+getComponentIdentifier()+", "+ex);
//									if(ex!=null)
//										ret.setExceptionIfUndone(ex);
//									else
//										ret.setResultIfUndone(null);
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
////									if(getComponentIdentifier().getName().indexOf("Feature")!=-1)
////										System.out.println("shutdown component features end3: "+getComponentIdentifier()+", "+ex);
//									ret.setExceptionIfUndone(exception);
//								}
//							});
						}
						
						// Do not wait for monitoring event but directly terminate to avoid having all return steps being scheduled immediately (see DecouplingReturnInterceptor) 
//						else
						{
//							if(getComponentIdentifier().getName().indexOf("Feature")!=-1)
//								System.out.println("shutdown component features end4: "+getComponentIdentifier()+", "+ex);
							if(ex!=null)
								ret.setExceptionIfUndone(ex);
							else
								ret.setResultIfUndone(null);
						}
					}
				});
				
				// Add timeout in case cleanup takes too long.
				Number	ntimeout	= (Number)getModel().getProperty(PROPERTY_TERMINATION_TIMEOUT, getClassLoader());
				long	timeout	= ntimeout!=null ? ntimeout.longValue() : Starter.getDefaultTimeout(getId());
				if(timeout!=Timeout.NONE)
				{
					if(getFeature0(IExecutionFeature.class)!=null)
					{
						getFeature(IExecutionFeature.class).waitForDelay(timeout, true)
							.addResultListener(new IResultListener<Void>()
						{
							@Override
							public void resultAvailable(Void result)
							{
//								System.out.println("shutdown component features timeout: "+getComponentIdentifier());
								executeKillOnFeatures(ifeatures!=null ? ifeatures : lfeatures);
								ret.setExceptionIfUndone(new TimeoutException("Timeout during component cleanup: "+timeout));
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// ignore (e.g. ComponentTerminatedException when cleanup successful)
							}
						});
					}
					else
					{
						System.err.println("No execution feature for timeout: "+getId());
					}
				}

				return ret;
			}
		});
	}
	
	/**
	 *  Recursively init the features.
	 */
	protected IFuture<Void>	executeInitOnFeatures(final Iterator<IComponentFeature> features)
	{
		// Try synchronous init.
		IFuture<Void>	ret	= IFuture.DONE;
		while(ret.isDone() && ret.getException()==null && features.hasNext())
		{
			IComponentFeature	cf	= features.next();
//			if(getComponentIdentifier().getName().indexOf("Custom")!=-1)
//				System.out.println("Initing "+cf+" of "+getComponentIdentifier());
			ifeatures.add(cf);
			ret	= cf.init();
		}
		
		// Wait for future but also check features, as future might have become done since check in loop.
		if(!ret.isDone() || features.hasNext())
		{
			// Recurse for asynchronous feature.
			final Future<Void>	fut	= new Future<Void>();
			ret.addResultListener(getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(fut)
			{
				public void customResultAvailable(Void result)
				{
					executeInitOnFeatures(features).addResultListener(new DelegationResultListener<Void>(fut));
				}
			}));
			ret	= fut;
		}
		
		// Init complete
		else
		{
			if(ret.getException()!=null)
			{
//				System.out.println("Initing of "+getComponentIdentifier()+" failed due to "+fut.getException());
				
				// Init failed: remove failed feature.
				IComponentFeature	feature	= ifeatures.remove(ifeatures.size()-1);
				feature.kill();	// Kill failed feature. Other features will be shutdowned.
			}
			else
			{
//				System.out.println("Initing of "+getComponentIdentifier()+" done.");
				
				// Init succeeded: list no longer needed.
				ifeatures	= null;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Execute feature bodies in parallel.
	 */
	protected IFuture<Void>	executeBodyOnFeatures(final Iterator<IComponentFeature> features)
	{
		List<IFuture<Void>>	undones	= new ArrayList<IFuture<Void>>();
		IFuture<Void>	ret	= IFuture.DONE;
		while(!shutdown && ret.getException()==null && features.hasNext())
		{
			final IComponentFeature	cf	= features.next();
//			if(getComponentIdentifier().getName().indexOf("Custom")!=-1)
//				System.out.println("Body "+cf+" of "+getComponentIdentifier());
			
			// Execute user body on separate step to allow blocking get() and still execute the other bodies.
			if(cf.hasUserBody())
			{
				ret	= getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					@Override
					public IFuture<Void> execute(IInternalAccess ia)
					{
						return cf.body();
					}
					
					@Override
					public String toString()
					{
						return "PlatformComponent.executeBodyOnFeatures() "+cf;
					}
				});
			}
			else
			{
				ret	= cf.body();
			}
			
			if(!ret.isDone())
			{
				undones.add(ret);
			}
		}
		
		// Check if need to kill due to no keepalive.
		if(!shutdown && ret.getException()==null)
		{
			// Body already finished -> kill if not keep alive
			if(undones.isEmpty())
			{
				Boolean	keepalive	= getModel().getKeepalive(getConfiguration());
				if(keepalive!=null && !keepalive.booleanValue())
				{
					killComponent();
				}
			}
			
			
			// Wait for body and then kill.
			else
			{
				final Future<Void>	fut	= new Future<Void>();
				IResultListener<Void>	crl	= new CounterResultListener<Void>(undones.size(), new DelegationResultListener<Void>(fut)
				{
					public void customResultAvailable(Void result)
					{
						Boolean	keepalive	= getModel().getKeepalive(getConfiguration());
						if(keepalive!=null && !keepalive.booleanValue())
						{
							killComponent();
						}
						fut.setResult(null);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						// Ignore step aborted due to component shutdown (hack?)
						if(exception instanceof StepAbortedException)
						{
							customResultAvailable(null);
						}
						else
						{
							super.exceptionOccurred(exception);
						}
					}
				});
				
				for(IFuture<Void> undone: undones)
				{
					undone.addResultListener(crl);
				}
				
				ret	= fut;
			}
		}
		
		// else body failed -> return fut.

		return ret;
	}
	
	/**
	 *  Recursively shutdown the features in inverse order.
	 */
	protected IFuture<Void>	executeShutdownOnFeatures(final List<IComponentFeature> features)
	{
		// Try synchronous shutdown.
		IFuture<Void>	fut	= IFuture.DONE;
		boolean sync	= true;
		while(sync && !features.isEmpty())
		{
			// On exception -> print but continue shutdown with next feature
			if(fut.getException()!=null)
			{
				StringWriter	sw	= new StringWriter();
				fut.getException().printStackTrace(new PrintWriter(sw));
				getLogger().warning("Exception during component cleanup of "+getId()+": "+fut.getException());
				getLogger().info(sw.toString());
			}
//			if(getComponentIdentifier().getName().indexOf("Leaker")!=-1)
//				System.out.println("feature shutdown start: "+getComponentIdentifier()+" "+features);
			
			fut	= features.get(features.size()-1).shutdown();
			sync	= fut.isDone();
			if(sync)
			{
				features.remove(features.size()-1);
			}
		}
		
		// Recurse once for current async feature
		if(!sync)
		{
			final Future<Void>	ret	= new Future<Void>();
			fut.addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					proceed();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					StringWriter	sw	= new StringWriter();
					exception.printStackTrace(new PrintWriter(sw));
					getLogger().warning("Exception during component cleanup of "+getId()+": "+exception);
					getLogger().info(sw.toString());

					proceed();
				}
				
				protected void proceed()
				{
					if(!features.isEmpty())	// Happens, when killed due to termination timeput
					{
						features.remove(features.size()-1);
						executeShutdownOnFeatures(features).addResultListener(new DelegationResultListener<Void>(ret));
					}
				}
			});
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}

	/**
	 *  Kill the features in inverse order.
	 *  Kill is invoked, when shutdown does not return due to timeout.
	 */
	protected void	executeKillOnFeatures(List<IComponentFeature> features)
	{
		while(!features.isEmpty())
		{
			features.remove(features.size()-1).kill();
		}
	}

	/**
	 *  Get the user view of this platform component.
	 *  
	 *  @return An internal access exposing user operations of the component.
	 */
	public IInternalAccess	getInternalAccess()
	{
		return ia;
	}
	
	/**
	 *  Get the exception, if any.
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception	getException()
	{
		return exception;
	}
	
//	/**
//	 *  Get the shared platform data.
//	 *  
//	 *  @return The objects shared by all components of the same platform (registry etc.). See starter for available data.
//	 */
//	public Map<String, Object>	getPlatformData()
//	{
//		return platformdata;
//	}
	
	//-------- IInternalAccess interface --------
	
	/**
	 *  @deprecated From 3.0. Use getComponentFeature(IArgumentsResultsFeature.class).getArguments()
	 *  Get an argument value per name.
	 *  @param name The argument name.
	 *  @return The argument value.
	 */
	public Object getArgument(String name)
	{
		return getFeature(IArgumentsResultsFeature.class).getArguments().get(name);
	}
	
//	/**
//	 *  @deprecated From version 3.0 - Use getComponentFeature(IRequiredServicesFeatures.class).getService()
//	 *  Get a required service of a given name.
//	 *  @param name The service name.
//	 *  @return The service.
//	 */
//	public <T> IFuture<T> getService(String name)
//	{
//		return getFeature(IRequiredServicesFeature.class).getService(name);
//	}
//	
//	/**
//	 *  @deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).scheduleStep()
//	 *  Execute a component step.
//	 */
//	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
//	{
//		return getFeature(IExecutionFeature.class).scheduleStep(step);
//	}
//	
//	/**
//	 * 	@deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).waitForDelay()
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
//	{
//		return getFeature(IExecutionFeature.class).waitForDelay(delay, step);
//	}
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return info!=null? info.getModel(): null;
	}
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IFuture<IModelInfo> getModelAsync()
	{
		return new Future<>(getModel());
	}

	/**
	 *  Get the start configuration or the default configuration if any.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		String	ret	= info.getConfiguration();
//		if(ret==null)
//		{
//			ConfigurationInfo[]	configs	= getModel().getConfigurations();
//			if(configs.length>0)
//			{
//				ret	= configs[0].getName();
//			}
//		}
		return ret;
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getId()
	{
		return info!=null && info.getComponentDescription()!=null? info.getComponentDescription().getName(): null;
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription	getDescription()
	{
		return info.getComponentDescription();
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription> getDescription(IComponentIdentifier cid)
	{
		return SComponentManagementService.getComponentDescription(cid, getInternalAccess());
	}

	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T	getFeature(Class<? extends T> type)
	{
		if(!features.containsKey(type))
		{
			throw new FeatureNotAvailableException("No such feature: "+type);
		}
		else
		{
			return type.cast(features.get(type));
		}
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature0(Class<? extends T> type)
	{
		return features!=null? type.cast(features.get(type)): null;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return killComponent((Exception)null);
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(Exception e)
	{
		// Only remember first exception.
		if(exception==null && e!=null)
			this.exception	= e;
//		IComponentManagementService cms = this.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		IFuture<Map<String, Object>> ret = this.killComponent(getId());
		return ret;
//		if(getComponentIdentifier().getParent()==null)
//		{
//			ret.addResultListener(new IResultListener<Map<String,Object>>()
//			{
//				public void resultAvailable(Map<String, Object> result)
//				{
//					System.out.println("ia: "+result);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("ia: "+exception);
//				}
//			});
//		}
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid)
	{
		return SComponentManagementService.destroyComponent(cid, getInternalAccess());
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		// Todo: shadow access and invalidation
//		return new ExternalAccess(this);
		
//		final ExternalAccess ea = new ExternalAccess(this);
//		
//		return (IExternalAccess)ProxyFactory.newProxyInstance(getClassLoader(), new Class[]{IExternalAccess.class}, new InvocationHandler()
//		{
//			@Override
//			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
//			{
//				System.out.println(method.getName()+" "+method.getReturnType()+" "+Arrays.toString(args));
//				
//				return method.invoke(ea, args);
//			}
//		});
		
		return (IExternalAccess)ProxyFactory.newProxyInstance(getClassLoader(), new Class[]{IExternalAccess.class}, new InvocationHandler()
		{
			/** The component identifier. */
			protected IComponentIdentifier	cid;
			
			/** The toString value. */
			protected String tostring;
			
			{
				this.cid	= getId();
				this.tostring = cid.getLocalName();
			}
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
//				System.out.println(method.getName()+" "+method.getReturnType()+" "+Arrays.toString(args));
				
				if("getId".equals(method.getName()))
				{
					return cid;
//					return getId();
				}
				else if("toString".equals(method.getName()))
				{
					return tostring;
//					return getId().getLocalName();
				}
				else if("equals".equals(method.getName()))
				{
					boolean ret = false;
					
					if(args[0] instanceof IExternalAccess)
						ret = ((IExternalAccess)args[0]).getId().equals(getId());
					
					return ret;
				}
				else if("hashCode".equals(method.getName()))
				{
					return cid == null? 0 : cid.hashCode();
//					return getId() == null? 0 : getId().hashCode();
				}
				else if("getExternalFeature".equals(method.getName()))
				{
					Class<?> iface = (Class<?>)args[0];
					return getExternalFeature(iface, getClassLoader(), getInternalAccess());
				}
				else
				{
					if(!getFeature(IExecutionFeature.class).isComponentThread())
					{
//						System.out.println("scheduleStep: "+method.getName());
						final Future<Object> ret = (Future<Object>)SFuture.getFuture(method.getReturnType());
						
						getInternalAccess().scheduleStep(new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								boolean intermediate = SReflect.isSupertype(IIntermediateFuture.class, method.getReturnType());
								if(!intermediate)
									doInvoke(ia, method, args).addResultListener(new DelegationResultListener<>(ret));
								else
									doInvoke(ia, method, args).addResultListener(new IntermediateDelegationResultListener<>((IntermediateFuture)ret));
								return IFuture.DONE;
							}
						});
						
						return ret;
					}
					else
					{
						return doInvoke(getInternalAccess(), method, args);
//						System.out.println("res2: "+res.getClass());
					}
				}
			}
			
			public IFuture<Object> doInvoke(IInternalAccess ia, Method method, Object[] args)
			{
				Future<Object> ret = new Future<>();
				
				try
				{
					Class<?> iface = method.getDeclaringClass();
					String name = SReflect.getClassName(iface);
					String intname = SUtil.replaceLast(name, "External", "");
//					System.out.println(name+" "+intname);
					
					Class<?> clazz = SReflect.findClass0(intname, null, ia.getClassLoader());
					Object feat = clazz!=null? ia.getFeature0(clazz): null;
					
					Object res;
					if(feat==null)
					{
						String mname = method.getName();
//						int idx = mname.lastIndexOf("Async");
//						if(idx>0)
//							mname = mname.substring(0, idx);
						Method m = IInternalAccess.class.getMethod(mname, method.getParameterTypes());
						res = m.invoke(ia, args);
					}
					else
					{
						res = method.invoke(feat, args);
					}
					
					if(res instanceof IFuture)
					{
						return (IFuture<Object>)res;
					}
					else
					{
						ret.setResult(res);
					}
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
				
				return ret;
			}
		});
	}
	
	/**
	 *  Get external feature wrapper.
	 *  @param iface
	 *  @param args
	 *  @return The proxy.
	 */
	public static <T> T getExternalFeature(Class<T> iface, ClassLoader cl, Object original)
	{
		if(!SReflect.isSupertype(IExternalComponentFeature.class, iface))
			throw new IllegalArgumentException("Must be external feature interface (extend IExternalComponentFeature)");
		
		return (T)ProxyFactory.newProxyInstance(cl, new Class[]{iface, IExternalAccess.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return method.invoke(original, args);
			}
		});
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		if(logger==null)
		{
			// todo: problem: loggers can cause memory leaks
			// http://bugs.sun.com/view_bug.do;jsessionid=bbdb212815ddc52fcd1384b468b?bug_id=4811930
			String name = getLoggerName(getId());
			logger = LogManager.getLogManager().getLogger(name);
			
			// if logger does not already exist, create it
			if(logger==null)
			{
				// Hack!!! Might throw exception in applet / webstart.
				try
				{
					logger = Logger.getLogger(name);
					initLogger(logger);
					logger = new LoggerWrapper(logger, null);	// Todo: clock
					//System.out.println(logger.getParent().getLevel());
				}
				catch(SecurityException e)
				{
					// Hack!!! For applets / webstart use anonymous logger.
					logger = Logger.getAnonymousLogger();
					initLogger(logger);
					logger = new LoggerWrapper(logger, null);	// Todo: clock
				}
			}
		}
		
		return logger;
	}

	/**
	 *  Get the logger name.
	 *  @param cid The component identifier.
	 *  @return The name.
	 */
	public static String getLoggerName(IComponentIdentifier cid)
	{
		// Prepend parent names for nested loggers.
		String	name	= null;
		for(; cid!=null; cid=cid.getParent())
		{
			name	= name==null ? cid.getLocalName() : cid.getLocalName() + "." +name;
		}
		return name;
	}
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected void initLogger(Logger logger)
	{
		IPropertiesFeature pf = getFeature0(IPropertiesFeature.class);
		
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		Object prop = pf!=null? pf.getProperty("logging.level"): null;
		Level level = prop!=null? (Level)prop : logger.getParent()!=null && logger.getParent().getLevel()!=null ? logger.getParent().getLevel() : Level.SEVERE;
		logger.setLevel(level);
		
		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = pf!=null? pf.getProperty("logging.useParentHandlers"): null;
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = pf!=null? pf.getProperty("logging.addConsoleHandler"): null;
		if(prop!=null)
		{
			Handler console;
			/*if[android]
			console = new jadex.commons.android.AndroidHandler();
			 else[android]*/
			console = new ConsoleHandler();
			/* end[android]*/
			
            console.setLevel(Level.parse(prop.toString()));
            logger.addHandler(console);
        }
		
		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
		// if logger should add a filehandler to capture log data in a file. 
		// The user specifies the directory to contain the log file.
		// $scope.getAgentName() can be used to have agent-specific log files 
		//
		// The directory name can use special patterns defined in the
		// class, java.util.logging.FileHandler, 
		// such as "%h" for the user's home directory.
		// 
		
		String logfile = pf!=null? (String)pf.getProperty("logging.file"): null;
		if(logfile!=null)
		{
		    try
		    {
			    Handler fh	= new FileHandler(logfile);
		    	fh.setFormatter(new SimpleFormatter());
		    	logger.addHandler(fh);
		    }
		    catch (IOException e)
		    {
		    	System.err.println("I/O Error attempting to create logfile: "
		    		+ logfile + "\n" + e.getMessage());
		    }
		}
		
		// Add further custom log handlers.
		prop = pf!=null? pf.getProperty("logging.handlers"): null;
		if(prop!=null)
		{
			if(prop instanceof Handler)
			{
				logger.addHandler((Handler)prop);
			}
			else if(SReflect.isIterable(prop))
			{
				for(Iterator<?> it=SReflect.getIterator(prop); it.hasNext(); )
				{
					Object obj = it.next();
					if(obj instanceof Handler)
					{
						logger.addHandler((Handler)obj);
					}
					else
					{
						logger.warning("Property is not a logging handler: "+obj);
					}
				}
			}
			else
			{
				logger.warning("Property 'logging.handlers' must be Handler or list of handlers: "+prop);
			}
		}
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		if(fetcher==null)
		{
			// Return a fetcher that tries features in reverse order first.
			return new IValueFetcher()
			{
				public Object fetchValue(String name)
				{
					Object	ret	= null;
					boolean	found	= false;
					
					for(int i=lfeatures.size()-1; !found && i>=0; i--)
					{
						IValueFetcher	vf	= lfeatures.get(i).getValueFetcher();
						if(vf!=null)
						{
							try
							{
								// Todo: better (faster) way than throwing exceptions?
								ret	= vf.fetchValue(name);
								found	= true;
							}
							catch(Exception e)
							{
							}
						}
					}
					
					if(!found && "$component".equals(name))
					{
						ret	= getInternalAccess();
						found	= true;
					}
					else if(!found && "$config".equals(name))
					{
						ret	= getConfiguration();
						found	= true;
					}
					
					if(!found)
						throw new RuntimeException("Value not found: "+name);
//					else
//						System.out.println("fetcher: "+name+" "+ret);
					
					return ret;
				}
			};
		}
		
		return fetcher;
	}
	
	/**
	 *  Get the parameter guesser.
	 *  @return The parameter guesser.
	 */
	// Todo: move to IPlatformComponent?
	public IParameterGuesser getParameterGuesser()
	{
		// Return a fetcher that tries features first.
		// Todo: better (faster) way than throwing exceptions?
		return new IParameterGuesser()
		{
//			IParameterGuesser parent;
//			
//			public void setParent(IParameterGuesser parent)
//			{
//				this.parent = parent;
//			}
//			
//			public IParameterGuesser getParent()
//			{
//				return parent;
//			}
			
			public Object guessParameter(Class<?> type, boolean exact)
			{
				Object	ret	= null;
				boolean	found = false;
				
				for(int i=lfeatures.size()-1; !found && i>=0; i--)
				{
					try
					{
						if(lfeatures.get(i).getParameterGuesser()!=null)
						{
							ret	= lfeatures.get(i).getParameterGuesser().guessParameter(type, exact);
							found	= true;
						}
					}
					catch(Exception e)
					{
					}
				}
				
				if(!found && ((exact && IInternalAccess.class.equals(type))
					|| (!exact && SReflect.isSupertype(type, IInternalAccess.class))))
				{
					ret	= getInternalAccess();
					found	= true;
				}
				else if(!found && ((exact && IExternalAccess.class.equals(type))
					|| (!exact && SReflect.isSupertype(type, IExternalAccess.class))))
				{
					ret	= getExternalAccess();
					found	= true;
				}
				
				if(!found)
				{
					throw new RuntimeException("Value not found: "+type);
				}
				
				return ret;
			}
			
		};
	}

	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader()
	{
		return getModel()!=null? ((ModelInfo)getModel()).getClassLoader(): null;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final String type, IComponentIdentifier parent)
	{
		if(parent!=null && !getId().equals(parent))
		{
			return SServiceProvider.getExternalAccessProxy(getInternalAccess(), parent).scheduleStep(new IComponentStep<IComponentIdentifier[]>()
			{
				public IFuture<IComponentIdentifier[]> execute(IInternalAccess ia)
				{
					return ia.getChildren(type, null);
				}
			});
		}
		
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		final String filename = getComponentFilename(type);
		
		if(filename==null && type!=null)
		{
			ret.setException(new IllegalArgumentException("Unknown type: "+type));
		}
		else if(type==null)
		{
			SComponentManagementService.getChildren(getId(), getInternalAccess()).addResultListener(new DelegationResultListener<>(ret));
		}
		else
		{
//			IComponentManagementService cms = getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			// Can use the parent resource identifier as child must depend on parent
			
			SComponentFactory.loadModel(getExternalAccess(), filename, getModel().getResourceIdentifier())
//			cms.loadComponentModel(filename, getModel().getResourceIdentifier())
				.addResultListener(getFeature(IExecutionFeature.class).createResultListener(
				new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier[]>(ret)
			{
				public void customResultAvailable(IModelInfo model)
				{
					final String modelname = model.getFullName();
				
					final Future<Collection<IExternalAccess>> childaccesses	= new Future<Collection<IExternalAccess>>();
					
					SComponentManagementService.getChildren(getId(), getInternalAccess())
						.addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret)
					{
						public void customResultAvailable(IComponentIdentifier[] children)
						{
							IResultListener<IExternalAccess> crl = new CollectionResultListener<IExternalAccess>(children.length, true,
								new DelegationResultListener<Collection<IExternalAccess>>(childaccesses));
							for(int i=0; !ret.isDone() && i<children.length; i++)
							{
								getExternalAccess(children[i]).addResultListener(crl);
							}
						}
					});
					
					childaccesses.addResultListener(getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, IComponentIdentifier[]>(ret)
					{
						public void customResultAvailable(Collection<IExternalAccess> col)
						{
							List<IComponentIdentifier> res = new ArrayList<IComponentIdentifier>();
							for(Iterator<IExternalAccess> it=col.iterator(); it.hasNext(); )
							{
								IExternalAccess subcomp = it.next();
								
								subcomp.getModelAsync().addResultListener(getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier[]>(ret)
								{
									public void customResultAvailable(IModelInfo model)
									{
										if(modelname.equals(model.getFullName()))
										{
											res.add(subcomp.getId());
										}
									}
								}));
							}
							ret.setResult((IComponentIdentifier[])res.toArray(new IComponentIdentifier[0]));
						}
					}));
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Get the file name for a logical type name of a subcomponent of this application.
	 */
	public String getComponentFilename(String type)
	{
		String ret = null;
		SubcomponentTypeInfo[] subcomps = getModel().getSubcomponentTypes();
		for(int i=0; ret==null && i<subcomps.length; i++)
		{
			SubcomponentTypeInfo subct = (SubcomponentTypeInfo)subcomps[i];
			if(subct.getName().equals(type))
				ret = subct.getFilename();
		}
		return ret;
	}
	
//	/**
//	 *  Get the lifecycle state. 
//	 *  @return The lifecycle state
//	 */
//	public ComponentLifecycleState getLifecycleState()
//	{
//		return state;
//	}
	
	/**
	 *  Get the step number when endstate began.
	 *  @return The step cnt.
	 */
	public int getEndstateStart()
	{
		return ((IInternalExecutionFeature)getFeature(IExecutionFeature.class)).getEndstateStart();
	}
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid)
	{
		return SComponentManagementService.getExternalAccess(cid, getInternalAccess());
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		// todo: parameter for name
		
		if(component==null && (info==null || info.getFilename()==null))
			return new Future<>(new RuntimeException("Component must not null."));
		
		final Future<IExternalAccess> ret = new Future<>();
		
		info = prepare(component, info);
		
		IFuture<IComponentIdentifier> fut = SComponentManagementService.createComponent(info.getName(), info.getFilename(), info, resultlistener, getInternalAccess());
		
		fut.addResultListener(new ComponentResultListener<>(new IResultListener<IComponentIdentifier>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex:"+exception);
				ret.setException(exception);
			}
			
			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
//				System.out.println("created: "+result);
				SComponentManagementService.getExternalAccess(result, getInternalAccess()).addResultListener(new DelegationResultListener<>(ret));
			}
		}, getExternalAccess()));
		
		return ret;
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info)
	{
		// todo: resultlistener for results?!
		
		if(component==null && (info==null || info.getFilename()==null))
			return new SubscriptionIntermediateFuture<>(new RuntimeException("Component must not null."));
		
		info = prepare(component, info);
		
		return SComponentManagementService.createComponent(info, info.getName(), info.getFilename(), getInternalAccess());
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info)
	{
		// todo: resultlistener for results?!
		
//		System.out.println("tuplecreate: "+info.getFilename());
		
		if(component==null && (info==null || info.getFilename()==null))
			return new Tuple2Future<IComponentIdentifier, Map<String, Object>>(new RuntimeException("Component must not null."));
				
		info = prepare(component, info);
		
		return SComponentManagementService.createComponent(info.getName(), info.getFilename(), info, getInternalAccess());
	}
	
	/**
	 *  Helper method for preparing the creation info.
	 *  @param component The pojo or filename
	 *  @param info The creation info. 
	 *  @return The creation info.
	 */
	public CreationInfo prepare(Object component, CreationInfo info)
	{
		if(info==null)
			info = new CreationInfo();
//		if(info.getParent()==null)
//			info.setParent(getId());
		
		String modelname = null;
		
		if(component instanceof String)
		{
			modelname = (String)component;
			info.setFilename(modelname);
		}
		else if(component instanceof Class<?>)
		{
			modelname = ((Class<?>)component).getName()+".class";
			info.setFilename(modelname);
		}
		else if(component != null)
		{
			modelname = component.getClass().getName()+".class";
			info.addArgument("__pojo", component); // hack?! use constant
			info.setFilename(modelname);
		}
		
		return info;
	}
	
	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent(IComponentIdentifier componentid)
	{
		return SComponentManagementService.suspendComponent(componentid, getInternalAccess());
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier componentid)
	{
		return SComponentManagementService.resumeComponent(componentid, false, getInternalAccess());
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(IComponentIdentifier cid, String stepinfo)
	{
		return SComponentManagementService.stepComponent(cid, stepinfo, getInternalAccess());
	}
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier cid, String[] breakpoints)
	{
		return SComponentManagementService.setComponentBreakpoints(cid, breakpoints, getInternalAccess()); 
	}
	
	/**
	 *  Add a component listener for a specific component.
	 *  The listener is registered for component changes.
	 *  @param cid	The component to be listened.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(IComponentIdentifier cid)
	{
		return SComponentManagementService.listenToComponent(cid, getInternalAccess());
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)//, boolean remote)
	{
		return SComponentManagementService.searchComponents(adesc, con, getInternalAccess());//, sid);
	}
	
	/**
	 *  Get the platform component.
	 */
	public PlatformComponent getPlatformComponent()
	{
		return this;
	}

	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return getId()!=null? getId().getName(): "n/a";
	}
}
