package jadex.kernelbase;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IExtensionInfo;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.NFPropertyInfo;
import jadex.bridge.modelinfo.NFRPropertyInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.nonfunctional.NFPropertyProvider;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.component.interceptors.ServiceGetter;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.IValueFetcher;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
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
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *  Base class for all kinds of interpreters.
 *  Implements the IComponentInstance interface and is responsible
 *  for realizing the outer structure of active components.
 */
public abstract class StatelessAbstractInterpreter extends NFPropertyProvider implements IComponentInstance
{
	/** Constant for step event. */
	public static final String TYPE_COMPONENT = "component";
	
	//-------- interface methods --------
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public abstract IServiceContainer getServiceContainer();
	
	/**
	 *  Create the service container.
	 *  @return The service conainer.
	 */
	public abstract IServiceContainer createServiceContainer();
	
	/**
	 *  Test if the component's execution is currently at one of the
	 *  given breakpoints. If yes, the component will be suspended by
	 *  the platform.
	 *  @param breakpoints	An array of breakpoints.
	 *  @return True, when some breakpoint is triggered.
	 */
	public abstract boolean isAtBreakpoint(String[] breakpoints);

	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this component.
	 *  External access objects must implement the IExternalAccess interface. 
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @return External access is delivered via future.
	 */
	public abstract IExternalAccess getExternalAccess();

	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public abstract Map<String, Object> getResults();

	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public abstract void setResultValue(String name, Object value);
	
	/**
	 *  Can be called on the component thread only.
	 * 
	 *  Main method to perform component execution.
	 *  Whenever this method is called, the component performs
	 *  one of its scheduled actions.
	 *  The platform can provide different execution models for components
	 *  (e.g. thread based, or synchronous).
	 *  To avoid idle waiting, the return value can be checked.
	 *  The platform guarantees that executeAction() will not be called in parallel. 
	 *  @return True, when there are more actions waiting to be executed. 
	 */
	public abstract boolean executeStep();

	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		// Do nothing.
		getLogger().warning("Unhandled message: "+message);
//		System.out.println("rec: "+message);
//		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the component that a stream has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con)
	{
		// Do nothing.
		getLogger().warning("Unhandled stream: "+con);
	}
	
	/**
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Request component to kill itself.
	 *  The component might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param listener	When cleanup of the component is finished, the listener must be notified.
	 */
	public IFuture<Void> cleanupComponent()
	{
//		System.out.println("cleanup: "+getComponentIdentifier());
		assert !getComponentAdapter().isExternalThread();
		
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IClockService> fut = SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(final IClockService clock)
			{
//				if(getComponentDescription().getName().getLocalName().startsWith("Initiator"))
//					System.out.println("ini1: "+getComponentIdentifier());
//				MonitoringEvent me = new MonitoringEvent(getComponentIdentifier().toString(), 
//					MonitoringEvent.TYPE_COMPONENT_DISPOSED, clock.getTime());
//				me.setProperty("details", getComponentDescription());
//				publishEvent(me).addResultListener(new DelegationResultListener<Void>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						ComponentChangeEvent.dispatchTerminatingEvent(getComponentAdapter(), getComponentDescription().getCreationTime(), getModel(), getServiceProvider(), getInternalComponentListeners())
//							.addResultListener(new DelegationResultListener<Void>(ret)
//						{
//							public void customResultAvailable(Void result)
//							{
								startEndSteps().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
								{
									public void customResultAvailable(Void result)
									{
//										if(getComponentDescription().getName().getLocalName().startsWith("Initiator"))
//											System.out.println("ini1: "+getComponentIdentifier());
										terminateExtensions().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
										{
											public void customResultAvailable(Void result)
											{
//												if(getComponentDescription().getName().getLocalName().startsWith("Initiator"))
//													System.out.println("ini1: "+getComponentIdentifier());
												shutdownNFPropertyProvider().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
												{
													public void customResultAvailable(Void result)
													{
//														if(getComponentDescription().getName().getLocalName().startsWith("Initiator"))
//															System.out.println("ini1: "+getComponentIdentifier());
														IResultListener<Void> reslis	= new IResultListener<Void>()
														{
															public void resultAvailable(Void result)
															{
																proceed(null);
//																ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), getModel(), lis, clock)
//																	.addResultListener(new DelegationResultListener<Void>(ret));
//																ret.setResult(null);
															}
															public void exceptionOccurred(final Exception exception)
															{
																proceed(exception);
//																ret.setException(exception);
//																ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), getModel(), lis, clock)
//																	.addResultListener(new DelegationResultListener<Void>(ret)
//																{
//																	public void customResultAvailable(Void result)
//																	{
//																		ret.setException(exception);
//																	}
//																	public void exceptionOccurred(Exception exception)
//																	{
//																		ret.setException(exception);
//																	}
//																});
															}
															
															protected void proceed(final Exception ex)
															{
																invalidateAccess(true);
																
																if(hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
																{
																	MonitoringEvent event = new MonitoringEvent(getComponentDescription().getName(), getComponentDescription().getCreationTime(),
																		IMonitoringEvent.TYPE_COMPONENT_DISPOSED, getComponentDescription().getCause(), System.currentTimeMillis(), PublishEventLevel.COARSE);
																	event.setProperty("details", getComponentDescription());
																	publishEvent(event, PublishTarget.TOALL).addResultListener(new DelegationResultListener<Void>(ret)
																	{
																		public void customResultAvailable(Void result)
																		{
																			if(ex!=null)
																				ret.setException(ex);
																			else
																				ret.setResult(null);
																		}
																		public void exceptionOccurred(Exception exception)
																		{
																			ret.setException(exception);
																		}
																	});
																}
																else
																{
																	ret.setResult(null);
																}
															}
														};
														
														// If platform, do not schedule listener on component as execution service already terminated after terminate service container.  
														if(getComponentIdentifier().getParent()!=null)
														{
															reslis	= createResultListener(reslis);
														}
														
														terminateResultSubscribers();
														
														terminateServiceContainer().addResultListener(reslis);
													}
												}));
											}
										}));
									}
								}));
//							}
//						});
					}
//				});
//			}
		}));
		
		return ret;
		
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
	
	/**
	 *  Called from cleanupComponent.
	 */
	public IFuture<Void> terminateServiceContainer()
	{
//		if("testcases".equals(getName()))
//			System.out.println("sdkug sdib ");
		final Future<Void> ret = new Future<Void>();
		
//		if(getComponentIdentifier().getParent()==null)
//			System.out.println("start shudown service container of: "+getComponentIdentifier());
		
		getServiceContainer().shutdown().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				if(getComponentIdentifier().getParent()==null)
//					System.out.println("end shudown service container of: "+getComponentIdentifier());
				
				ret.setResult(null);
//				listener.resultAvailable(this, getComponentIdentifier());
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				if(getComponentIdentifier().getParent()==null)
//					System.out.println("end shudown service container of: "+getComponentIdentifier()+" "+exception);
				
//				exception.printStackTrace();
//				getLogger().warning("Exception during service container shutdown: "+exception);
//				listener.resultAvailable(this, getComponentIdentifier());
//				ret.setResult(getComponentIdentifier());	// Exception should be propagated?
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public IFuture<Void>	componentCreated(final IComponentDescription desc, final IModelInfo model)
	{
		final Future<Void> ret = new Future<Void>();
		
//		// cannot use scheduleStep as it is not available in init phase of component.
////		return scheduleStep(new IComponentStep()
		
		// Must throw component events here for extensions
		
		getExternalAccess().scheduleImmediate(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("created: "+desc.getName());
//				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_COMPONENT, model.getFullName(), 
//					desc.getName().getName(), getComponentIdentifier(), desc.getCreationTime(), desc);
//				notifyListeners(event);
				if(hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
				{
					MonitoringEvent me = new MonitoringEvent(desc.getName(), desc.getCreationTime(), 
						MonitoringEvent.TYPE_COMPONENT_CREATED, desc.getCause(), desc.getCreationTime(), PublishEventLevel.COARSE);
					me.setProperty("details", desc);
					// for extensions only
					publishEvent(me, PublishTarget.TOALL) .addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					ret.setResult(null);
				}
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public IFuture<Void>	componentDestroyed(final IComponentDescription desc)
	{
//		System.out.println("dest: "+desc.getName());
		return scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
//				System.out.println("destroyed: "+desc.getName());
//				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_COMPONENT, desc.getModelName(), desc.getName().getName(), getComponentIdentifier(), getComponentDescription().getCreationTime(), desc);
//				notifyListeners(event);
				
				if(hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
				{
					// todo: clock time? , creation time
					MonitoringEvent event = new MonitoringEvent(desc.getName(), desc.getCreationTime(), IMonitoringEvent.TYPE_COMPONENT_DISPOSED, desc.getCause(), System.currentTimeMillis(), PublishEventLevel.COARSE);
					event.setProperty("details", getComponentDescription());
					// for extensions only
	//				publishEvent(event, false, false).addResultListener(new IResultListener<Void>()
					publishEvent(event, PublishTarget.TOALL).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							ret.setResult(null);
						}
						public void exceptionOccurred(Exception exception)
						{
							ret.setResult(null);
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
				
				return ret;
//				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Start the initial steps of the component.
	 *  Called as part of start behavior.
	 */
	public void startInitialSteps()
	{
		ConfigurationInfo	ci	= getConfiguration()!=null
			? getModel().getConfiguration(getConfiguration())
			: getModel().getConfigurations().length>0 ? getModel().getConfigurations()[0] : null;
		
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getInitialSteps();
			for(int i=0; i<upes.length; i++)
			{
				Object	step;
				if(upes[i].getValue()!=null)
				{
					step	= SJavaParser.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getClassLoader());
				}
				else
				{
					Class clazz = upes[i].getClazz().getType(getClassLoader(), getModel().getAllImports());
					try
					{
						step	= clazz.newInstance();
					}
					catch(Exception e)
					{
						throw new RuntimeException("Cannot instantiate class: "+clazz, e);
					}
				}
				
				if(step instanceof IComponentStep)
				{
					scheduleStep((IComponentStep)step);
				}
				else
				{
					throw new RuntimeException("Unsupported initial component step, class="+upes[i].getClazz()+", value="+upes[i].getValue());
				}
			}
		}
	}
	
	/**
	 *  Start the end steps of the component.
	 *  Called as part of cleanup behavior.
	 */
	public IFuture<Void>	startEndSteps()
	{
		Future<Void>	ret	= new Future<Void>();
		ConfigurationInfo	ci	= getConfiguration()!=null
			? getModel().getConfiguration(getConfiguration())
			: getModel().getConfigurations().length>0 ? getModel().getConfigurations()[0] : null;
		
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getEndSteps();
			List<IComponentStep<?>>	steps	= new ArrayList<IComponentStep<?>>();
			for(int i=0; !ret.isDone() && i<upes.length; i++)
			{
				Object	step	= null;
				if(upes[i].getValue()!=null)
				{
					step	= SJavaParser.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getClassLoader());
				}
				else
				{
					Class<?> clazz = upes[i].getClazz().getType(getClassLoader(), getModel().getAllImports());
					try
					{
						step	= clazz.newInstance();
					}
					catch(Exception e)
					{
						ret.setException(new RuntimeException("Cannot instantiate class: "+clazz, e));
					}
				}
				
				if(step instanceof IComponentStep)
				{
					steps.add((IComponentStep<?>)step);
				}
				else if(step!=null)
				{
					ret.setException(new RuntimeException("Unsupported component end step, class="+upes[i].getClazz()+", value="+upes[i].getValue()));
				}
			}
			
			if(!ret.isDone())
			{
				CounterResultListener	crl	= new CounterResultListener(steps.size(), new DelegationResultListener(ret));
				for(int i=0; i<steps.size(); i++)
					scheduleStep((IComponentStep)steps.get(i)).addResultListener(crl);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	
	/**
	 *  Start the component behavior.
	 */
	public void startBehavior()
	{
		startInitialSteps();
	}
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public abstract String getConfiguration();
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @return The result of the step.
	 */
	public abstract <T> IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Get a space of the component.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public abstract IExtensionInstance getExtension(final String name);
	
	/**
	 *  Get all spaces of the component.
	 *  @return	The spaces.
	 */
	public abstract IExtensionInstance[] getExtensions();
	
	/**
	 *  Get a property of the component.
	 *  May only be called after the component's init has finished.
	 *  @param name	The property name.
	 *  @return	The property value
	 */
	public Object	getProperty(String name)
	{
		Map<String, Object>	props	= getProperties();
		return props!=null ? props.get(name) : null;
	}
	
	/**
	 *  Create a service proxy for registering a provided service.
	 */
	public IInternalService	createInternalService(Object service, Class<?> type)
	{
//		boolean moni = getComponentDescription().getMonitoring()!=null? getComponentDescription().getMonitoring().booleanValue(): false;
		PublishEventLevel elm = getComponentDescription().getMonitoring()!=null? getComponentDescription().getMonitoring(): null;
		// todo: remove this? currently the level cannot be turned on due to missing interceptor
		boolean moni = elm!=null && !PublishEventLevel.OFF.equals(elm); 
		
		IInternalService	is	= BasicServiceInvocationHandler.createProvidedServiceProxy(
			getInternalAccess(), getComponentAdapter(), service, null,
			type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED,
			null, isCopy(), isRealtime(),
			getModel().getResourceIdentifier(), moni, null);
		
		return is;
	}

	//-------- internally used methods --------
	
	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public abstract IComponentAdapter getComponentAdapter();

	/**
	 *  Get the model info.
	 *  @return The model info.
	 */
	public abstract IModelInfo getModel();
	
	/**
	 *  Get the value fetcher.
	 */
	public abstract IValueFetcher getFetcher();
	
	/**
	 *  Add a value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public abstract boolean addArgument(String name, Object value);

	/**
	 *  Add a default value for a result (if not already present).
	 *  Called once for each result during init.
	 *  @param name	The result name.
	 *  @param value	The result value.
	 */
	public abstract void addDefaultResult(String name, Object value);

	/**
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public abstract IInternalAccess getInternalAccess();
	
	/**
	 *  Add an extension instance.
	 *  @param extension The extension instance.
	 */
	public abstract void addExtension(String name, IExtensionInstance extension);
	
	/**
	 *  Add a property value.
	 *  @param name The name.
	 *  @param val The value.
	 */
	public abstract void addProperty(String name, Object val);
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public abstract Map<String, Object> getProperties();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public abstract Map<String, Object> getArguments();
	
	/**
	 *  Flag for parameter copy.
	 *  @return True, if copy copy should be used. 
	 */
	public abstract boolean isCopy();
	
	/**
	 *  Get the realtime.
	 *  @return The realtime.
	 */
	public abstract boolean isRealtime();
	
	/**
	 *  Check if event targets exist.
	 */
	public abstract boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi);
	
	/**
	 *  Get the monitoring event emit level.
	 */
	public abstract PublishEventLevel getPublishEmitLevelMonitoring();
	
	//-------- methods --------
	
	/**
	 *  Main init method consists of the following steps:
	 *  - init future properties
	 *  - init arguments and results
	 *  - init extensions
	 *  - init required and provided services
	 *  - init subcomponents
	 */
	public IFuture<Void> init(final IModelInfo model, final String config, final Map<String, Object> arguments)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future<Void> fut = new Future<Void>();
		IFuture<Void>	ret	= fut;
		
		if(config!=null && model.getConfiguration(config)==null)
		{
			fut.setException(new RuntimeException("No such configuration in model: "+model.getFullName()+", "+config));
		}
		else
		{
//			if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//				System.out.println("Initing arguments: "+this);
			initArguments(model, config, arguments).addResultListener(
				createResultListener(new DelegationResultListener<Void>(fut)
			{
				public void customResultAvailable(Void result)
				{
//					if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//						System.out.println("Initing future properties: "+this);
					// properties depend on arguments (e.g. logging_level in Platform.component.xml)
					initFutureProperties(model).addResultListener(
						createResultListener(new DelegationResultListener<Void>(fut)
					{
						public void customResultAvailable(Void result)
						{
//							if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//								System.out.println("Initing extensions: "+this);
							initExtensions(model, config).addResultListener(
								createResultListener(new DelegationResultListener<Void>(fut)
							{
								public void customResultAvailable(Void result)
								{
//									if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//										System.out.println("Initing required services: "+this);
									initRequiredServices(model, config).addResultListener(
										createResultListener(new DelegationResultListener<Void>(fut)
									{
										public void customResultAvailable(Void result)
										{
//											if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//												System.out.println("Initing provided services: "+this);
											initProvidedServices(model, config).addResultListener(
												createResultListener(new DelegationResultListener<Void>(fut)
											{
												public void customResultAvailable(Void result)
												{
//													if(model.getFullName().equals("jadex.bdibpmn.examples.marsworld.MarsWorld"))
//														System.out.println("Initing components: "+this);
													initComponents(model, config).addResultListener(
														createResultListener(new DelegationResultListener<Void>(fut)
													{
														public void customResultAvailable(Void result)
														{
//															ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_COMPONENT, model.getFullName(), 
//																getComponentDescription().getName().getName(), getComponentIdentifier(), getComponentDescription().getCreationTime(), getComponentDescription());
//															notifyListeners(event);
															
															if(hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
															{
																MonitoringEvent me = new MonitoringEvent(getComponentDescription().getName(), getComponentDescription().getCreationTime(),
																	MonitoringEvent.TYPE_COMPONENT_CREATED, getComponentDescription().getCause(), getComponentDescription().getCreationTime(), PublishEventLevel.COARSE);
																me.setProperty("details", getComponentDescription());
																publishEvent(me, PublishTarget.TOALL);
//																	.addResultListener(new DelegationResultListener<Void>(ret));
															}
															super.customResultAvailable(result);
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
			}));
			
			// Terminate extensions on error.
			final Future<Void> iret = new Future<Void>();
			fut.addResultListener(createResultListener(new DelegationResultListener<Void>(iret)
			{
				public void exceptionOccurred(final Exception exception)
				{
//					Thread.dumpStack();
//					exception.printStackTrace();
//					System.err.println("error state: "+getComponentIdentifier()+", "+getComponentDescription().getState()+", "+getComponentAdapter().isExternalThread());
					terminateExtensions().addResultListener(new DelegationResultListener<Void>(iret)
					{
						public void customResultAvailable(Void result)
						{
							super.exceptionOccurred(exception);
						}
					});
				}
			}));
			ret	= iret;
		}
		
		return ret;
	}
	
	/**
	 *  Init the arguments and results.
	 */
	public IFuture<Void> initArguments(IModelInfo model, final String config, Map<String, Object> arguments)
	{
		assert !getComponentAdapter().isExternalThread();
		
		// Call add default argument also for passed arguments.
		if(arguments!=null)
		{
			for(Iterator<Map.Entry<String, Object>> it=arguments.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Object> entry = it.next();
				addArgument(entry.getKey(), entry.getValue());
			}
		}
		
		ConfigurationInfo	ci	= config!=null ? model.getConfiguration(config) : null;
		
		// Init the arguments with initial or default values.
		Set<String>	done	= new HashSet<String>();
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getArguments();
			for(int i=0; i<upes.length; i++)
			{
				addArgument(upes[i].getName(), SJavaParser.getParsedValue(upes[i], model.getAllImports(), getFetcher(), getClassLoader()));
				done.add(upes[i].getName());
			}
		}
		IArgument[] margs = model.getArguments();
		for(int i=0; i<margs.length; i++)
		{
			if(!done.contains(margs[i].getName()) && (arguments==null || !arguments.containsKey(margs[i].getName())))
			{
				addArgument(margs[i].getName(),
					SJavaParser.getParsedValue(margs[i].getDefaultValue(), model.getAllImports(), getFetcher(), getClassLoader()));
			}
		}
		
		// Init the results with default values.
		
		// Hack?! add component identifier to result as long as we don't have better future type for results
		// could one somehow use the CallLocal for that purpose instead?
		setResultValue(IComponentIdentifier.RESULTCID, getComponentIdentifier());
		
		done	= new HashSet<String>();
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getResults();
			for(int i=0; i<upes.length; i++)
			{
				addDefaultResult(upes[i].getName(), SJavaParser.getParsedValue(upes[i], model.getAllImports(), getFetcher(), getClassLoader()));
				done.add(upes[i].getName());
			}
		}
		IArgument[] res = model.getResults();
		for(int i=0; i<res.length; i++)
		{
			// Prevents unset results being added to be able to check whether a user has
			// set an argument explicitly to null or if it just is null (e.g. for field injections)
			if(!done.contains(res[i].getName()) && res[i].getDefaultValue().getValue()!=null)
			{
				addDefaultResult(res[i].getName(), 
					SJavaParser.getParsedValue(res[i].getDefaultValue(), model.getAllImports(), getFetcher(), getClassLoader()));
			}
		}

		return IFuture.DONE;
	}
	
	/**
	 *  Init the services.
	 */
	public IFuture<Void> initRequiredServices(final IModelInfo model, final String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		// Required services.
		RequiredServiceInfo[] ms = model.getRequiredServices();
		
		Map<String, RequiredServiceInfo>	sermap = new LinkedHashMap<String, RequiredServiceInfo>();
		for(int i=0; i<ms.length; i++)
		{
			ms[i]	= new RequiredServiceInfo(getServicePrefix()+ms[i].getName(), ms[i].getType().getType(getClassLoader(), getModel().getAllImports()), ms[i].isMultiple(), 
				ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(getClassLoader(), getModel().getAllImports()), ms[i].getDefaultBinding(), ms[i].getNFRProperties());
			sermap.put(ms[i].getName(), ms[i]);
		}

		if(config!=null)
		{
			ConfigurationInfo cinfo = model.getConfiguration(config);
			RequiredServiceInfo[] cs = cinfo.getRequiredServices();
			for(int i=0; i<cs.length; i++)
			{
				RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(getServicePrefix()+cs[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(getClassLoader(), getModel().getAllImports()), rsi.isMultiple(), 
					ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(getClassLoader(), getModel().getAllImports()), new RequiredServiceBinding(cs[i].getDefaultBinding()), ms[i].getNFRProperties());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		RequiredServiceBinding[]	bindings	= getBindings();
		if(bindings!=null)
		{
			for(int i=0; i<bindings.length; i++)
			{
				RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(bindings[i].getName());
				RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(getClassLoader(), getModel().getAllImports()), rsi.isMultiple(), 
					rsi.getMultiplexType()==null? null: rsi.getMultiplexType().getType(getClassLoader(), getModel().getAllImports()), new RequiredServiceBinding(bindings[i]), ms[i].getNFRProperties());
				sermap.put(rsi.getName(), newrsi);
			}
		}
		
		// Create place holder required service properties
		RequiredServiceInfo[]	rservices	= (RequiredServiceInfo[])sermap.values().toArray(new RequiredServiceInfo[sermap.size()]);
		getServiceContainer().addRequiredServiceInfos(rservices);
		for(RequiredServiceInfo rsi: rservices)
		{
			List<NFRPropertyInfo> nfprops = rsi.getNFRProperties();
			if(nfprops!=null)
			{
				INFMixedPropertyProvider nfpp = getServiceContainer().getRequiredServicePropertyProvider(null); // null for unbound
				
				for(NFRPropertyInfo nfprop: nfprops)
				{
					MethodInfo mi = nfprop.getMethodInfo();
					Class<?> clazz = nfprop.getClazz().getType(getClassLoader(), getModel().getAllImports());
					INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getInternalAccess(), null, nfprop.getMethodInfo());
					if(mi==null)
					{
						nfpp.addNFProperty(nfp);
					}
					else
					{
						nfpp.addMethodNFProperty(mi, nfp);
					}
				}
			}
		}
					
		return IFuture.DONE;
	}
	
	/**
	 *  Init the services.
	 */
	public IFuture<Void> initProvidedServices(final IModelInfo model, final String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future<Void>	ret	= new Future<Void>();
		
		try
		{
			// Provided services.
//			System.out.println("init sers: "+services);
			ProvidedServiceInfo[] ps = model.getProvidedServices();
			
			Map<Object, ProvidedServiceInfo> sermap = new LinkedHashMap<Object, ProvidedServiceInfo>();
			for(int i=0; i<ps.length; i++)
			{
				Object key = ps[i].getName()!=null? ps[i].getName(): ps[i].getType().getType(getClassLoader(), getModel().getAllImports());
				if(sermap.put(key, ps[i])!=null)
					throw new RuntimeException("Services with same type must have different name.");  // Is catched and set to ret below
			}
			if(config!=null)
			{
				ConfigurationInfo cinfo = model.getConfiguration(config);
				ProvidedServiceInfo[] cs = cinfo.getProvidedServices();
				for(int i=0; i<cs.length; i++)
				{
					Object key = cs[i].getName()!=null? cs[i].getName(): cs[i].getType().getType(getClassLoader(), getModel().getAllImports());
					ProvidedServiceInfo psi = (ProvidedServiceInfo)sermap.get(key);
					ProvidedServiceInfo newpsi= new ProvidedServiceInfo(psi.getName(), psi.getType().getType(getClassLoader(), getModel().getAllImports()), 
						new ProvidedServiceImplementation(cs[i].getImplementation()), psi.getPublish());
					sermap.put(key, newpsi);
				}
			}
			ProvidedServiceInfo[] services = (ProvidedServiceInfo[])sermap.values().toArray(new ProvidedServiceInfo[sermap.size()]);
			initProvidedServices(0, services, model).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					// Start service container.
					startServiceContainer().addResultListener(createResultListener(new DelegationResultListener(ret)));
				}
			}));

		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Init provided services.
	 */
	protected IFuture<Void>	initProvidedServices(final int i, final ProvidedServiceInfo[] services, final IModelInfo model)
	{
		final IFuture<Void>	ret;
		
		if(i<services.length)
		{
			ret	= new Future<Void>();
			IFuture<Void>	fut;

//			ProvidedServiceImplementation impl = services[i].getImplementation(); 
//			// Virtual service (e.g. promoted)
//			if(impl!=null && impl.getBinding()!=null)
//			{
//				RequiredServiceInfo info = new RequiredServiceInfo(BasicService.generateServiceName(services[i].getType(model, getClassLoader()))+":virtual", services[i].getType(model, getClassLoader()));
//				IServiceIdentifier sid = BasicService.createServiceIdentifier(getExternalAccess().getServiceProvider().getId(), 
//					info.getName(), info.getType(model, getClassLoader()), BasicServiceInvocationHandler.class);
//				IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(getExternalAccess(), getComponentAdapter(), 
//					sid, info, impl.getBinding(), isCopy(), getClassLoader());
//				fut	= getServiceContainer().addService(service);
//			}
			
			// Directly provided service.
//			else
//			{
				try
				{
					fut	= initService(services[i], model, null);
				}
				catch(Exception e)
				{
					fut	= new Future<Void>(e);
				}
//			}
			
			fut.addResultListener(createResultListener(new DelegationResultListener<Void>((Future<Void>)ret)
			{
				public void customResultAvailable(Void result)
				{
					initProvidedServices(i+1, services, model)
						.addResultListener(createResultListener(new DelegationResultListener<Void>((Future<Void>)ret)));
				}
			}));
		}
		else
		{
			ret	= IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Get the service prefix.
	 *  @return The prefix for required services.
	 */
	public String getServicePrefix()
	{
		return "";
	}

	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public abstract RequiredServiceBinding[] getBindings();

	
	/**
	 *  Start the services.
	 */
	public IFuture startServiceContainer()
	{
		assert !getComponentAdapter().isExternalThread();
		
		return getServiceContainer().start();
	}
	
	/**
	 *  Init the (future) properties.
	 */
	public IFuture<Void> initFutureProperties(IModelInfo model)
	{
		assert !getComponentAdapter().isExternalThread();
		
		// Init nf component props
		List<NFPropertyInfo> nfprops = model.getNFProperties();
		if(nfprops!=null)
		{
			for(NFPropertyInfo nfprop: nfprops)
			{
				try
				{
					Class<?> clazz = nfprop.getClazz().getType(getClassLoader(), getModel().getAllImports());
					INFProperty<?, ?> nfp = AbstractNFProperty.createProperty(clazz, getInternalAccess(), null, null);
					addNFProperty(nfp);
				}
				catch(Exception e)
				{
					getLogger().warning("Property creation problem: "+e);
				}
			}
		}
		
		Future<Void> ret = new Future<Void>();
		
		// Evaluate (future) properties.
		final List	futures	= new ArrayList();
		final Map<String, Object>	props	= model.getProperties();
		if(props!=null)
		{
			for(Iterator<Map.Entry<String, Object>> it=props.entrySet().iterator(); it.hasNext(); )
			{
				final Map.Entry<String, Object> entry = it.next();
				final String name = entry.getKey();
				final Object value = entry.getValue();
				if(value instanceof UnparsedExpression)
				{
					try
					{
						final UnparsedExpression unexp = (UnparsedExpression)value;
						Class<?> clazz = unexp.getClazz()!=null? unexp.getClazz().getType(getClassLoader(), model.getAllImports()): null;
						Object tmp;
						if(unexp.getValue()==null || unexp.getValue().length()==0 && clazz!=null)
						{
							tmp = clazz.newInstance();
						}
						else
						{
							tmp = SJavaParser.evaluateExpression(unexp.getValue(), model.getAllImports(), getFetcher(), getClassLoader());
						}
					
						final Object val = tmp;
						if(SReflect.isSupertype(IFuture.class, clazz!=null? clazz: val != null ? val.getClass() : null))
						{
	//						System.out.println("Future property: "+unexp.getName()+", "+val);
							if(val instanceof IFuture)
							{
								// Use second future to start component only when value has already been set.
								final Future retu = new Future();
								((IFuture)val).addResultListener(createResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
	//									System.out.println("Setting future property: "+unexp.getName()+" "+result);
										addProperty(unexp.getName(), result);
										retu.setResult(result);
									}
								}));
								futures.add(retu);
							}
							else if(val!=null)
							{
								futures.add(new Future(new RuntimeException("Future property must be instance of jadex.commons.IFuture: "+name+", "+unexp.getValue())));
//								throw new RuntimeException("Future property must be instance of jadex.commons.IFuture: "+name+", "+unexp.getValue());
							}
						}
						else
						{
							// Todo: handle specific properties (logging etc.)
							addProperty(name, val);
						}
					}
					catch(Exception e)
					{
						futures.add(new Future(e));
					}
				}
			}
			
			IResultListener	crl	= new CounterResultListener(futures.size(), new DelegationResultListener(ret));
			for(int i=0; i<futures.size(); i++)
			{
				((IFuture)futures.get(i)).addResultListener(crl);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Init the subcomponents.
	 */
	public IFuture<Void> initComponents(final IModelInfo model, String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future<Void> ret = new Future<Void>();
		
		if(config!=null)
		{
			final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
			ConfigurationInfo conf = model.getConfiguration(config);
			final ComponentInstanceInfo[] components = conf.getComponentInstances();
			SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					// NOTE: in current implementation application waits for subcomponents
					// to be finished and cms implements a hack to get the external
					// access of an uninited parent.
					
					// (NOTE1: parent cannot wait for subcomponents to be all created
					// before setting itself inited=true, because subcomponents need
					// the parent external access.)
					
					// (NOTE2: subcomponents must be created one by one as they
					// might depend on each other (e.g. bdi factory must be there for jcc)).
					
					createComponent(components, cms, model, 0, ret, cids);
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
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
	 *  Init the extensions.
	 */
	public IFuture<Void> initExtensions(IModelInfo model, String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future<Void> ret = new Future<Void>();
		
		if(config!=null)
		{
			ConfigurationInfo conf = model.getConfiguration(config);
			final IExtensionInfo[] exts = conf.getExtensions();
			if(exts.length>0)
			{
				initExtension(exts, 0).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Init an extension and continue with next.
	 */
	public IFuture<Void> initExtension(final IExtensionInfo[] exts, final int i)
	{
		final Future<Void> ret = new Future<Void>();
		exts[i].createInstance(getExternalAccess(), getFetcher())
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IExtensionInstance, Void>(ret)
		{
			public void customResultAvailable(IExtensionInstance ext)
			{
				addExtension(exts[i].getName(), ext);
				ext.init().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(i<exts.length-1)
						{
							initExtension(exts, i+1).addResultListener(new DelegationResultListener<Void>(ret));
						}
						else
						{
							super.customResultAvailable(result);
						}
					}
				}));
			}
		}));
		return ret;
	}
	
	/**
	 *  Terminate all extensions.
	 */
	public IFuture<Void> terminateExtensions()
	{
		// Hack!!! When init fails , terminateExtensions() can not be called on component thread
		// as component already terminated.
		assert !getComponentAdapter().isExternalThread() || IComponentDescription.STATE_TERMINATED.equals(getComponentDescription().getState());
		
		Future<Void> ret = new Future<Void>();
		IExtensionInstance[] exts = getExtensions();
		CounterResultListener<Void> lis = new CounterResultListener<Void>(exts.length, false, new DelegationResultListener<Void>(ret));
		for(int i=0; i<exts.length; i++)
		{
			exts[i].terminate().addResultListener(lis);
		}
		return ret;
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return getComponentAdapter().getLogger();
	}
	
	//-------- methods --------

	/**
	 *  Get the name.
	 */
	// todo: remove.
	public String	getName()
	{
		return getComponentAdapter().getComponentIdentifier().getLocalName();		
	}
	
	/**
	 *  Get a string representation of the context.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(SReflect.getInnerClassName(getClass()));
		ret.append("(name=");
		ret.append(getComponentAdapter().getComponentIdentifier().getLocalName());
//		ret.append(", parent=");
//		ret.append(getParentContext());
//		IComponentIdentifier[]	aids	= getComponents(); 
//		if(aids!=null)
//		{
//			ret.append(", components=");
//			ret.append(SUtil.arrayToString(aids));
//		}
//		IContext[]	subcs	= getSubContexts(); 
//		if(subcs!=null)
//		{
//			ret.append(", subcontexts=");
//			ret.append(SUtil.arrayToString(subcs));
//		}
		ret.append(")");
		return ret.toString();
	}
		
	//-------- methods --------
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return getComponentAdapter().getComponentIdentifier();
	}
	
	//-------- methods to be called by kernel --------
	
	/**
	 *  Add a service to the component. 
	 *  @param type The service interface.
	 *  @param service The service.
	 *  @param proxytype	The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<IInternalService> addService(final String name, final Class<?> type, final String proxytype, 
		final IServiceInvocationInterceptor[] ics, final Object service, final ProvidedServiceInfo info, IResultCommand<Object, Class<?>> componentfetcher)
	{
//		System.out.println("addS:"+service);

		assert !getComponentAdapter().isExternalThread();
		final Future<IInternalService> ret = new Future<IInternalService>();
		
		PublishEventLevel elm = getComponentDescription().getMonitoring()!=null? getComponentDescription().getMonitoring(): null;
		// todo: remove this? currently the level cannot be turned on due to missing interceptor
//		boolean moni = elm!=null? !PublishEventLevel.OFF.equals(elm.getLevel()): false; 
		boolean moni = elm!=null && !PublishEventLevel.OFF.equals(elm); 
		final IInternalService proxy = BasicServiceInvocationHandler.createProvidedServiceProxy(
			getInternalAccess(), getComponentAdapter(), service, name, type, proxytype, ics, isCopy(), 
			isRealtime(), getModel().getResourceIdentifier(), moni, componentfetcher);
		getServiceContainer().addService(proxy, info).addResultListener(new ExceptionDelegationResultListener<Void, IInternalService>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(proxy);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a service to the component. 
	 *  @param info The provided service info.
	 */
	protected IFuture<Void>	initService(final ProvidedServiceInfo info, final IModelInfo model, IResultCommand<Object, Class<?>> componentfetcher)
	{
		final Future<Void> ret = new Future<Void>();
		assert !getComponentAdapter().isExternalThread();
		
		try
		{
			final ProvidedServiceImplementation	impl = info.getImplementation();
			// Virtual service (e.g. promoted)
			if(impl!=null && impl.getBinding()!=null)
			{
				RequiredServiceInfo rsi = new RequiredServiceInfo(BasicService.generateServiceName(info.getType().getType( 
					getClassLoader(), getModel().getAllImports()))+":virtual", info.getType().getType(getClassLoader(), getModel().getAllImports()));
				IServiceIdentifier sid = BasicService.createServiceIdentifier(getExternalAccess().getServiceProvider().getId(), 
					rsi.getName(), rsi.getType().getType(getClassLoader(), getModel().getAllImports()), BasicServiceInvocationHandler.class, getModel().getResourceIdentifier());
				final IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(
					getInternalAccess(), getComponentAdapter(), sid, rsi, impl.getBinding(), getClassLoader(), isRealtime());
				getServiceContainer().addService(service, info).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
			}
			else
			{
				Object ser = createServiceImplementation(info, model);
				
//				Object ser = null;
//				if(impl!=null && impl.getValue()!=null)
//				{
//					// todo: other Class imports, how can be found out?
//					try
//					{
//						ser = SJavaParser.getParsedValue(impl, model.getAllImports(), getFetcher(), getClassLoader());
////								System.out.println("added: "+ser+" "+model.getName());
//					}
//					catch(RuntimeException e)
//					{
//						e.printStackTrace();
//						throw new RuntimeException("Service creation error: "+info, e);
//					}
//				}
//				else if(impl!=null && impl.getClazz().getType(getClassLoader())!=null)
//				{
//					ser = impl.getClazz().getType(getClassLoader()).newInstance();
//				}
				
				// Implementation may null to disable service in some configurations.
				if(ser!=null)
				{
					UnparsedExpression[] ins = info.getImplementation().getInterceptors();
					IServiceInvocationInterceptor[] ics = null;
					if(ins!=null)
					{
						ics = new IServiceInvocationInterceptor[ins.length];
						for(int i=0; i<ins.length; i++)
						{
							if(ins[i].getValue()!=null && ins[i].getValue().length()>0)
							{
								ics[i] = (IServiceInvocationInterceptor)SJavaParser.evaluateExpression(ins[i].getValue(), model.getAllImports(), getFetcher(), getClassLoader());
							}
							else
							{
								ics[i] = (IServiceInvocationInterceptor)ins[i].getClazz().getType(getClassLoader(), model.getAllImports()).newInstance();
							}
						}
					}
					
					final Class<?> type = info.getType().getType(getClassLoader(), getModel().getAllImports());
					addService(info.getName(), type, info.getImplementation().getProxytype(), ics, ser, info, componentfetcher)
						.addResultListener(new ExceptionDelegationResultListener<IInternalService, Void>(ret)
					{
						public void customResultAvailable(final IInternalService service)
						{
							ret.setResult(null);
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Create a service implementation from description.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info, IModelInfo model)
	{
		Object	ser	= null;
		ProvidedServiceImplementation impl = info.getImplementation();
		if(impl!=null && impl.getValue()!=null)
		{
			// todo: other Class imports, how can be found out?
			try
			{
				SimpleValueFetcher fetcher = new SimpleValueFetcher(getFetcher());
				fetcher.setValue("$servicename", info.getName());
				fetcher.setValue("$servicetype", info.getType().getType(getClassLoader(), getModel().getAllImports()));
//				System.out.println("sertype: "+fetcher.fetchValue("$servicetype")+" "+info.getName());
				ser = SJavaParser.getParsedValue(impl, model.getAllImports(), fetcher, getClassLoader());
//				System.out.println("added: "+ser+" "+model.getName());
			}
			catch(RuntimeException e)
			{
//				e.printStackTrace();
				throw new RuntimeException("Service creation error: "+info, e);
			}
		}
		else if(impl!=null && impl.getClazz().getType(getClassLoader(), getModel().getAllImports())!=null)
		{
			try
			{
				ser = impl.getClazz().getType(getClassLoader(), getModel().getAllImports()).newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return ser;
	}
	
//	/**
//	 *  Get the thread pool.
//	 */
//	protected IFuture<IThreadPoolService> getThreadPoolService()
//	{
//		final Future<IThreadPoolService> ret = new Future<IThreadPoolService>();
//		
//		// Don't use service container to avoid using proxy for thread pool:
//		// proxy may lead to endless loop in decoupling return interceptor, trying to call threadpool.execute() again and again for rescue thread.
////		getServiceContainer().searchService(IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//		SServiceProvider.getService(getServiceProvider(), IThreadPoolService.class)
//			.addResultListener(new IResultListener<IThreadPoolService>()
//		{
//			public void resultAvailable(IThreadPoolService tp)
//			{
//				ret.setResult(tp);
//			}
//			
//			public void exceptionOccurred(Exception e)
//			{
//				ret.setResult(null);
//			}
//		});
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the publish service for a publish type (e.g. web service).
//	 *  @param type The type.
//	 *  @param services The iterator of publish services (can be null).
//	 *  @return The publish service.
//	 */
//	protected IFuture<IPublishService> getPublishService(final String type, final Iterator<IPublishService> services)
//	{
//		final Future<IPublishService> ret = new Future<IPublishService>();
//		
//		if(services==null)
//		{
//			IFuture<Collection<IPublishService>> fut = SServiceProvider.getServices(getServiceProvider(), IPublishService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//			fut.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IPublishService>, IPublishService>(ret)
//			{
//				public void customResultAvailable(Collection<IPublishService> result)
//				{
//					getPublishService(type, result.iterator()).addResultListener(new DelegationResultListener<IPublishService>(ret));
//				}
//			}));
//		}
//		else
//		{
//			if(services.hasNext())
//			{
//				final IPublishService ps = (IPublishService)services.next();
//				ps.isSupported(type).addResultListener(createResultListener(new ExceptionDelegationResultListener<Boolean, IPublishService>(ret)
//				{
//					public void customResultAvailable(Boolean supported)
//					{
//						if(supported.booleanValue())
//						{
//							ret.setResult(ps);
//						}
//						else
//						{
//							getPublishService(type, services).addResultListener(new DelegationResultListener<IPublishService>(ret));
//						}
//					}
//				}));
//			}
//			else
//			{
//				ret.setException(new ServiceNotFoundException("IPublishService"));
//			}
//		}
//		
//		return ret;
//	}
	
	//-------- methods to be called by adapter --------
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
//		if(getComponentIdentifier().getParent()==null)
//		{
//			System.err.println("inter: "+getComponentIdentifier().getName());
//			Thread.dumpStack();
//		}
		
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(getServiceContainer(), 
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.destroyComponent(getComponentAdapter().getComponentIdentifier())
					.addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the class loader of the component.
	 *  The component class loader is required to avoid incompatible class issues,
	 *  when changing the platform class loader while components are running. 
	 *  This may occur e.g. when decoding messages and instantiating parameter values.
	 *  @return	The component class loader. 
	 */
	public abstract ClassLoader getClassLoader();
//	{
//		return getModel().getClassLoader();
//	}

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
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return getComponentAdapter().getDescription().getLocalType();
	}
	
	/**
	 *  Get the component description.
	 *  @return The component description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return getComponentAdapter().getDescription();
	}
		
	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return new ComponentResultListener<T>(listener, getComponentAdapter());
	}

	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
	{
		return new IntermediateComponentResultListener<T>(listener, getComponentAdapter());
	}

	/**
	 *  Create subcomponents.
	 */
	public void	createComponent(final ComponentInstanceInfo[] components, final IComponentManagementService cms, final IModelInfo model, final int i, final Future<Void> fut, final List<IComponentIdentifier> cids)
	{
		assert !getComponentAdapter().isExternalThread();
		
		if(i<components.length)
		{
			int num = getNumber(components[i], model);
			IResultListener<IComponentIdentifier> crl = new CollectionResultListener<IComponentIdentifier>(num, false, 
				createResultListener(new ExceptionDelegationResultListener<Collection<IComponentIdentifier>, Void>(fut)
			{
				public void customResultAvailable(Collection<IComponentIdentifier> result)
				{
					cids.addAll(result);
					createComponent(components, cms, model, i+1, fut, cids);
				}
			}));
			for(int j=0; j<num; j++)
			{
				SubcomponentTypeInfo type = components[i].getType(model);
				if(type!=null)
				{
					final Boolean suspend	= components[i].getSuspend()!=null ? components[i].getSuspend() : type.getSuspend();
					Boolean	master = components[i].getMaster()!=null ? components[i].getMaster() : type.getMaster();
					Boolean	daemon = components[i].getDaemon()!=null ? components[i].getDaemon() : type.getDaemon();
					Boolean	autoshutdown = components[i].getAutoShutdown()!=null ? components[i].getAutoShutdown() : type.getAutoShutdown();
					PublishEventLevel monitoring = components[i].getMonitoring()!=null ? components[i].getMonitoring() : type.getMonitoring();
					Boolean	synchronous = components[i].getSynchronous()!=null ? components[i].getSynchronous() : type.getSynchronous();
					RequiredServiceBinding[] bindings = components[i].getBindings();
					// todo: rid
//					System.out.println("curcall: "+getName(components[i], model, j+1)+" "+CallAccess.getCurrentInvocation().getCause());
					cms.createComponent(getName(components[i], model, j+1), type.getName(),
						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), getComponentAdapter().getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, monitoring, synchronous, model.getAllImports(), bindings, null), null).addResultListener(crl);
				}
				else
				{
					crl.exceptionOccurred(new RuntimeException("No such component type: "+components[i].getTypeName()));
				}
			}
		}
		else
		{
			fut.setResult(null);
		}
	}
	
	/**
	 *  Create a subcomponent.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
				Future<Void> fut = new Future<Void>();
				fut.addResultListener(new ExceptionDelegationResultListener<Void, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(cids.size()>0)
						{
							ret.setResult(cids.get(0));
						}
						else
						{
							ret.setException(new RuntimeException("Component not created: "+component));
						}
					}
				});
				createComponent(new ComponentInstanceInfo[]{component}, cms, getModel(), 0, fut, cids);
			}
		}));
			
		return ret;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	public Map<String, Object> getArguments(ComponentInstanceInfo component, IModelInfo model)
	{
		assert !getComponentAdapter().isExternalThread();
		
		Map<String, Object> ret = null;		
		UnparsedExpression[] arguments = component.getArguments();
		UnparsedExpression argumentsexp = component.getArgumentsExpression();
		
		if(arguments.length>0)
		{
			ret = new HashMap<String, Object>();

			for(int i=0; i<arguments.length; i++)
			{
				// todo: language
				if(arguments[i].getValue()!=null && arguments[i].getValue().length()>0)
				{
					Object val = SJavaParser.evaluateExpression(arguments[i].getValue(), model.getAllImports(), getFetcher(), getClassLoader());
					ret.put(arguments[i].getName(), val);
				}
			}
		}
		else if(argumentsexp!=null && argumentsexp.getValue()!=null && argumentsexp.getValue().length()>0)
		{
			// todo: language
			ret = (Map<String, Object>)SJavaParser.evaluateExpression(argumentsexp.getValue(), model.getAllImports(), getFetcher(), getClassLoader());
		}
		
		return ret;
	}
	
	/**
	 *  Get the number of components to start.
	 *  @return The number.
	 */
	public int getNumber(ComponentInstanceInfo component, IModelInfo model)
	{
		assert !getComponentAdapter().isExternalThread();
		
		Object ret = component.getNumber()!=null? SJavaParser.evaluateExpression(component.getNumber(), model.getAllImports(), getFetcher(), getClassLoader()): null;
		return ret instanceof Integer? ((Integer)ret).intValue(): 1;
	}
	
	/**
	 *  Get the name of components to start.
	 *  @return The name.
	 */
	public String getName(ComponentInstanceInfo component, IModelInfo model, int cnt)
	{
		assert !getComponentAdapter().isExternalThread();
		
		String ret = component.getName();
		if(ret!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(getFetcher());
			fetcher.setValue("$n", Integer.valueOf(cnt));
			try
			{
				ret = (String)SJavaParser.evaluateExpression(component.getName(), model.getAllImports(), fetcher, getClassLoader());
				if(ret==null)
					ret = component.getName();
			}
			catch(RuntimeException e)
			{
			}
		}
		return ret;
	}

	/**
	 *  Get the service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return getServiceContainer();
	}
	
	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object getRawService(String name)
	{
		assert !getComponentAdapter().isExternalThread();
		
		return convertRawService(getServiceContainer().getProvidedService(name));
	}
	
	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object getRawService(Class type)
	{
		assert !getComponentAdapter().isExternalThread();
		
		IService[] sers = getServiceContainer().getProvidedServices(type);
		return convertRawService(sers[0]);
	}
	
	/**
	 *  Get a raw reference to a provided service implementation.
	 */
	public Object[] getRawServices(Class type)
	{
		IService[] sers = getServiceContainer().getProvidedServices(type);
		Object[] ret = new Object[sers.length];
		
		for(int i=0; i<ret.length; i++)
		{
			convertRawService(sers[i]);
		}
		
		return ret;
	}
	
	/**
	 *  Convert to raw service.
	 */
	protected Object convertRawService(IService service)
	{
		Object ret;
		if(Proxy.isProxyClass(service.getClass()))
		{
			BasicServiceInvocationHandler ih	= (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
			if(ih.getService() instanceof ServiceInfo)
			{
				ret	= ((ServiceInfo)ih.getService()).getDomainService();
			}
			else
			{
				ret	= ih.getService();
			}
		}
		else
		{
			ret = service;
		}
		return ret;
	}
	
//	/**
//	 *  Create the service container.
//	 *  @return The service container.
//	 */
//	public IServiceContainer createServiceContainer()
//	{
//		IServiceContainer ret = null;
//		// Init service container.
//		UnparsedExpression mex = getComponentType().getContainer();
//		if(mex!=null)
//		{
//			ret = (IServiceContainer)mex.getParsedValue().getValue(getFetcher());
//		}
//		else
//		{
////				container = new CacheServiceContainer(new ComponentServiceContainer(getComponentAdapter()), 25, 1*30*1000); // 30 secs cache expire
//			ret = new ComponentServiceContainer(getComponentAdapter(), 
//				getComponentTypeName(), getModel().getRequiredServices(), getServiceBindings());
//		}			
//		return ret;
//	}
	
//	/**
//	 *  Add an component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture addComponentListener(List componentlisteners, IComponentListener listener)
//	{
//		if(componentlisteners==null)
//			componentlisteners = new ArrayList();
//		
//		// Hack! How to find out if remote listener?
//		if(Proxy.isProxyClass(listener.getClass()))
//			listener = new RemoteComponentListener(getExternalAccess(), listener);
//		
//		componentlisteners.add(listener);
//		return IFuture.DONE;
//	}
//	
//	/**
//	 *  Remove a component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture removeComponentListener(List componentlisteners, IComponentListener listener)
//	{
//		// Hack! How to find out if remote listener?
//		if(Proxy.isProxyClass(listener.getClass()))
//			listener = new RemoteComponentListener(getExternalAccess(), listener);
//		
//		if(componentlisteners!=null)
//			componentlisteners.remove(listener);
//		
////		System.out.println("cl: "+componentlisteners);
//		return IFuture.DONE;
//	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final String type)
	{
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		final String filename = getComponentFilename(type);
		
		if(filename==null)
		{
			ret.setException(new IllegalArgumentException("Unknown type: "+type));
		}
		else
		{
			SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier[]>(ret)
			{
				public void customResultAvailable(final IComponentManagementService cms)
				{
					// Can use the parent resource identifier as child must depend on parent
					cms.loadComponentModel(filename, getModel().getResourceIdentifier()).addResultListener(createResultListener(
						new ExceptionDelegationResultListener<IModelInfo, IComponentIdentifier[]>(ret)
					{
						public void customResultAvailable(IModelInfo model)
						{
							final String modelname = model.getFullName();
						
							final Future<Collection<IExternalAccess>>	childaccesses	= new Future<Collection<IExternalAccess>>();
							cms.getChildren(getComponentIdentifier()).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret)
							{
								public void customResultAvailable(IComponentIdentifier[] children)
								{
									IResultListener<IExternalAccess>	crl	= new CollectionResultListener<IExternalAccess>(children.length, true,
										new DelegationResultListener<Collection<IExternalAccess>>(childaccesses));
									for(int i=0; !ret.isDone() && i<children.length; i++)
									{
										cms.getExternalAccess(children[i]).addResultListener(crl);
									}
								}
							});
							childaccesses.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, IComponentIdentifier[]>(ret)
							{
								public void customResultAvailable(Collection<IExternalAccess> col)
								{
									List<IComponentIdentifier> res = new ArrayList<IComponentIdentifier>();
									for(Iterator<IExternalAccess> it=col.iterator(); it.hasNext(); )
									{
										IExternalAccess subcomp = it.next();
										if(modelname.equals(subcomp.getModel().getFullName()))
										{
											res.add(subcomp.getComponentIdentifier());
										}
									}
									ret.setResult((IComponentIdentifier[])res.toArray(new IComponentIdentifier[0]));
								}
							}));
						}
					}));
				}	
			}));
		}
		
		return ret;
	}
		
	/**
	 *  Test if is external thread.
	 *  @return True if is not agent thread.
	 *  Note: Should not be called directly. 
	 *  Will be called from adapter.
	 */
	public boolean isExternalThread()
	{
		// Per default just returns false and lets the adapter decide.
		// Can be overridden to ensure that the interpreter has the last word.
		return true;
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step)
	{
		return waitForDelay(delay, step, false);
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(final long delay, final IComponentStep<T> step, final boolean realtime)
	{
		// todo: remember and cleanup timers in case of component removal.
		
		final Future<T> ret = new Future<T>();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ITimedObject	to	= new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(createResultListener(new DelegationResultListener<T>(ret)));
					}
					
					public String toString()
					{
						return "waitForDelay[Step]("+getComponentIdentifier()+")";
					}
				};
				if(realtime)
				{
					cs.createRealtimeTimer(delay, to);
				}
				else
				{
					cs.createTimer(delay, to);					
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(final long delay)
	{
		return waitForDelay(delay, false);
	}

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(final long delay, final boolean realtime)
	{
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ITimedObject	to	=  	new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ret.setResult(null);
								return IFuture.DONE;
							}
						});
					}
					
					public String toString()
					{
						return "waitForDelay("+getComponentIdentifier()+")";
					}
				};
				
				if(realtime)
				{
					cs.createRealtimeTimer(delay, to);
				}
				else
				{
					cs.createTimer(delay, to);
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test if current thread is the component thread.
	 *  @return True if the current thread is the component thread.
	 */
	public boolean isComponentThread()
	{
		return !getComponentAdapter().isExternalThread();
	}
	
	/**
	 *  Create intermediate of direct future.
	 */
	protected Future<?> createStepFuture(IComponentStep<?> step)
	{
		Future<?> ret;
		try
		{
			Method method = step.getClass().getMethod("execute", new Class[]{IInternalAccess.class});
			Class<?> clazz = method.getReturnType();
//			ret = FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(getLogger()));
			// Must not be fetched before properties are available!
			ret = FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(new IResultCommand<Logger, Void>()
			{
				public Logger execute(Void args)
				{
					return getLogger();
				}
			}));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	//-------- component listeners --------
	
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public abstract IComponentListener[] getComponentListeners();
//	
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public abstract Collection<IComponentListener> getInternalComponentListeners();
//
//	/**
//	 *  Add component listener.
//	 */
//	public abstract IFuture<Void> addComponentListener(IComponentListener listener);
//
//	/**
//	 *  Remove component listener.
//	 */
//	public abstract IFuture<Void> removeComponentListener(IComponentListener listener);

//	/**
//	 *  Notify the component listeners.
//	 */
//	public void notifyListeners(IComponentChangeEvent event)
//	{
//		assert !getComponentAdapter().isExternalThread();
//		
//		IComponentListener[] componentlisteners = getComponentListeners();
//		for(int i=0; i<componentlisteners.length; i++)
//		{
//			final IComponentListener lis = componentlisteners[i];
//			if(lis.getFilter().filter(event))
//			{
//				lis.eventOccured(event).addResultListener(new IResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						//Print exception?
//						removeComponentListener(lis);
//					}
//				});
//			}
//		}
//	}
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	public abstract ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel els);
	
	/**
	 *  Subscribe to receive results.
	 */
	public abstract ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults();
	
	/**
	 *  Terminate the result subscribers.
	 */
	public abstract void terminateResultSubscribers();
	
	/**
	 *  Invalidate the external access.
	 */
	public abstract void invalidateAccess(boolean terminate);

	/**
	 *  Get the current state as events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		return null;
	}
	
	/**
	 *  Forward event to all currently registered subscribers.
	 */
	public abstract void publishLocalEvent(IMonitoringEvent event);
	
	/**
	 *  Get the monitoring service getter.
	 *  @return The monitoring service getter.
	 */
	public abstract ServiceGetter<IMonitoringService> getMonitoringServiceGetter();

	/**
	 *  Get the state of the interpreter.
	 *  @return The state of the interpreter.
	 */
	public IFuture<Object> getPersistableState()
	{
		return null;
	}
		
//	/**
//	 *  Publish a monitoring event. This event is automatically send
//	 *  to the monitoring service of the platform (if any). 
//	 */
//	public IFuture<Void> publishEvent(IMonitoringEvent event)
//	{
//		return publishEvent(event, PublishTarget.TOALL);
//	}
	
	/**
	 *  Publish a monitoring event. This event is automatically send
	 *  to the monitoring service of the platform (if any). 
	 *  @param tomonitor Flag, if event should be sent to the monitoring service.
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt)
	{
		if(event.getCause()==null)
		{
			ServiceCall call = CallAccess.getCurrentInvocation();
			if(call!=null)
			{
//				System.out.println("injecting call cause: "+call.getCause());
				event.setCause(call.getCause());
			}
			else if(getComponentDescription().getCause()!=null)
			{
//				System.out.println("injecting root cause: "+call.getCause());
				event.setCause(getComponentDescription().getCause().createNext());//event.getSourceIdentifier().toString()));
			}
		}
		
		// Publish to local subscribers
		publishLocalEvent(event);
		
//		// Publish to monitoring service if monitoring is turned on
//		if((PublishTarget.TOALL.equals(pt) || PublishTarget.TOMONITORING.equals(pt) 
//			&& event.getLevel().getLevel()<=getPublishEmitLevelMonitoring().getLevel()))
//		{
			return publishEvent(event, getMonitoringServiceGetter());
//		}
//		else
//		{
//			return IFuture.DONE;
//		}
	}
	
	/**
	 *  Publish a monitoring event to the monitoring service.
	 */
	public static IFuture<Void> publishEvent(final IMonitoringEvent event, final ServiceGetter<IMonitoringService> getter)
	{
//		return IFuture.DONE;
		
		final Future<Void> ret = new Future<Void>();
		
		if(getter!=null)
		{
			getter.getService().addResultListener(new ExceptionDelegationResultListener<IMonitoringService, Void>(ret)
			{
				public void customResultAvailable(IMonitoringService monser)
				{
					if(monser!=null)
					{
//						System.out.println("Published: "+event);
						monser.publishEvent(event).addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void exceptionOccurred(Exception exception)
							{
								getter.resetService();
								ret.setException(exception);
							}
						});
					}
					else
					{
//						System.out.println("Could not publish: "+event);
						ret.setResult(null);
					}
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	// Overrides getParent() of nfproperty provider super class
	/**
	 *  Get the parent.
	 *  return The parent.
	 */
	public INFPropertyProvider getParent()
	{
		return getComponentAdapter().getParent();
	}
}
