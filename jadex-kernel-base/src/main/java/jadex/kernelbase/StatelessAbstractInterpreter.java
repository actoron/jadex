package jadex.kernelbase;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IExtensionInfo;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
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
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
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
public abstract class StatelessAbstractInterpreter implements IComponentInstance
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
				ComponentChangeEvent.dispatchTerminatingEvent(getComponentAdapter(), getComponentDescription().getCreationTime(), getModel(), getServiceProvider(), getInternalComponentListeners())
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						startEndSteps().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								terminateExtensions().addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
								{
									public void customResultAvailable(Void result)
									{
										final Collection<IComponentListener> lis = getInternalComponentListeners();
										IResultListener<Void> reslis	= new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
												ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), getModel(), lis, clock)
													.addResultListener(new DelegationResultListener<Void>(ret));
											}
											public void exceptionOccurred(final Exception exception)
											{
												ComponentChangeEvent.dispatchTerminatedEvent(getComponentIdentifier(), getComponentDescription().getCreationTime(), getModel(), lis, clock)
													.addResultListener(new DelegationResultListener<Void>(ret)
												{
													public void customResultAvailable(Void result)
													{
														ret.setException(exception);
													}
													public void exceptionOccurred(Exception exception)
													{
														ret.setException(exception);
													}
												});
											}
										};
										// If platform, do not schedule listener on component as execution service already terminated after terminate service container.  
										if(getComponentIdentifier().getParent()!=null)
											reslis	= createResultListener(reslis);
										
										terminateServiceContainer().addResultListener(reslis);
									}
								}));
							}
						}));
					}
				});
			}
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
		
		getServiceContainer().shutdown().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				ret.setResult(null);
//				listener.resultAvailable(this, getComponentIdentifier());
			}
			
			public void exceptionOccurred(Exception exception)
			{
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
		// cannot use scheduleStep as it is not available in init phase of component.
//		return scheduleStep(new IComponentStep()
		return getExternalAccess().scheduleImmediate(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("created: "+desc.getName());
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_COMPONENT, model.getFullName(), desc.getName().getName(), getComponentIdentifier(), getComponentDescription().getCreationTime(), desc);
				notifyListeners(event);
				return IFuture.DONE;
			}
		});
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
//				System.out.println("destroyed: "+desc.getName());
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_COMPONENT, desc.getModelName(), desc.getName().getName(), getComponentIdentifier(), getComponentDescription().getCreationTime(), desc);
				notifyListeners(event);
				return IFuture.DONE;
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
					step	= UnparsedExpression.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getClassLoader());
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
			List	steps	= new ArrayList();
			for(int i=0; !ret.isDone() && i<upes.length; i++)
			{
				Object	step	= null;
				if(upes[i].getValue()!=null)
				{
					step	= UnparsedExpression.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getClassLoader());
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
						ret.setException(new RuntimeException("Cannot instantiate class: "+clazz, e));
					}
				}
				
				if(step instanceof IComponentStep)
				{
					steps.add(step);
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
		Map	props	= getProperties();
		return props!=null ? props.get(name) : null;
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
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public abstract IComponentListener[] getComponentListeners();
	
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public abstract Collection<IComponentListener> getInternalComponentListeners();

	/**
	 *  Add component listener.
	 */
	public abstract IFuture<Void> addComponentListener(IComponentListener listener);

	/**
	 *  Remove component listener.
	 */
	public abstract IFuture<Void> removeComponentListener(IComponentListener listener);
	
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
			initArguments(model, config, arguments).addResultListener(
				createResultListener(new DelegationResultListener<Void>(fut)
			{
				public void customResultAvailable(Void result)
				{
					// properties depend on arguments (e.g. logging_level in Platform.component.xml)
					initFutureProperties(model).addResultListener(
						createResultListener(new DelegationResultListener<Void>(fut)
					{
						public void customResultAvailable(Void result)
						{
							initExtensions(model, config).addResultListener(
								createResultListener(new DelegationResultListener<Void>(fut)
							{
								public void customResultAvailable(Void result)
								{
									initServices(model, config).addResultListener(
										createResultListener(new DelegationResultListener<Void>(fut)
									{
										public void customResultAvailable(Void result)
										{
											initComponents(model, config).addResultListener(
												createResultListener(new DelegationResultListener<Void>(fut)));
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
			for(Iterator<String> it=arguments.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
				addArgument(key, arguments.get(key));
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
				addArgument(upes[i].getName(), UnparsedExpression.getParsedValue(upes[i], model.getAllImports(), getFetcher(), getClassLoader()));
				done.add(upes[i].getName());
			}
		}
		IArgument[] margs = model.getArguments();
		for(int i=0; i<margs.length; i++)
		{
			if(!done.contains(margs[i].getName()))
			{
				addArgument(margs[i].getName(),
					UnparsedExpression.getParsedValue(margs[i].getDefaultValue(), model.getAllImports(), getFetcher(), getClassLoader()));
			}
		}
		
		// Init the results with default values.
		done	= new HashSet<String>();
		if(ci!=null)
		{
			UnparsedExpression[]	upes	= ci.getResults();
			for(int i=0; i<upes.length; i++)
			{
				addDefaultResult(upes[i].getName(), UnparsedExpression.getParsedValue(upes[i], model.getAllImports(), getFetcher(), getClassLoader()));
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
					UnparsedExpression.getParsedValue(res[i].getDefaultValue(), model.getAllImports(), getFetcher(), getClassLoader()));
			}
		}

		return IFuture.DONE;
	}
	
	/**
	 *  Init the services.
	 */
	public IFuture<Void> initServices(final IModelInfo model, final String config)
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
				Object key = ps[i].getName()!=null? ps[i].getName(): ps[i].getType().getType(getClassLoader());
				if(sermap.put(key, ps[i])!=null)
					throw new RuntimeException("Services with same type must have different name.");  // Is catched and set to ret below
			}
			if(config!=null)
			{
				ConfigurationInfo cinfo = model.getConfiguration(config);
				ProvidedServiceInfo[] cs = cinfo.getProvidedServices();
				for(int i=0; i<cs.length; i++)
				{
					Object key = cs[i].getName()!=null? cs[i].getName(): cs[i].getType().getType(getClassLoader());
					ProvidedServiceInfo psi = (ProvidedServiceInfo)sermap.get(key);
					ProvidedServiceInfo newpsi= new ProvidedServiceInfo(psi.getName(), psi.getType().getType(getClassLoader()), 
						new ProvidedServiceImplementation(cs[i].getImplementation()), psi.getPublish());
					sermap.put(key, newpsi);
				}
			}
			ProvidedServiceInfo[] services = (ProvidedServiceInfo[])sermap.values().toArray(new ProvidedServiceInfo[sermap.size()]);
			initProvidedServices(0, services, model).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					// Required services.
					RequiredServiceInfo[] ms = model.getRequiredServices();
					
					Map<String, RequiredServiceInfo>	sermap = new LinkedHashMap<String, RequiredServiceInfo>();
					for(int i=0; i<ms.length; i++)
					{
						ms[i]	= new RequiredServiceInfo(getServicePrefix()+ms[i].getName(), ms[i].getType().getType(getClassLoader()), ms[i].isMultiple(), 
							ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(getClassLoader()), ms[i].getDefaultBinding());
						sermap.put(ms[i].getName(), ms[i]);
					}

					if(config!=null)
					{
						ConfigurationInfo cinfo = model.getConfiguration(config);
						RequiredServiceInfo[] cs = cinfo.getRequiredServices();
						for(int i=0; i<cs.length; i++)
						{
							RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(getServicePrefix()+cs[i].getName());
							RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(getClassLoader()), rsi.isMultiple(), 
								ms[i].getMultiplexType()==null? null: ms[i].getMultiplexType().getType(getClassLoader()), new RequiredServiceBinding(cs[i].getDefaultBinding()));
							sermap.put(rsi.getName(), newrsi);
						}
					}
					if(getBindings()!=null)
					{
						for(int i=0; i<getBindings().length; i++)
						{
							RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(getBindings()[i].getName());
							RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType().getType(getClassLoader()), rsi.isMultiple(), 
								rsi.getMultiplexType()==null? null: rsi.getMultiplexType().getType(getClassLoader()), new RequiredServiceBinding(getBindings()[i]));
							sermap.put(rsi.getName(), newrsi);
						}
					}
					RequiredServiceInfo[]	rservices	= (RequiredServiceInfo[])sermap.values().toArray(new RequiredServiceInfo[sermap.size()]);
					getServiceContainer().addRequiredServiceInfos(rservices);
					
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
					fut	= initService(services[i], model);
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
	 *  Init the future properties.
	 */
	public IFuture<Void> initFutureProperties(IModelInfo model)
	{
		assert !getComponentAdapter().isExternalThread();
		
		Future ret = new Future();
		
		// Evaluate (future) properties.
		final List	futures	= new ArrayList();
		final Map	props	= model.getProperties();
		if(props!=null)
		{
			for(Iterator it=props.keySet().iterator(); it.hasNext(); )
			{
				final String name = (String)it.next();
				final Object value = props.get(name);
				if(value instanceof UnparsedExpression)
				{
					try
					{
						final UnparsedExpression unexp = (UnparsedExpression)value;
						Class clazz = unexp.getClazz()!=null? unexp.getClazz().getType(getClassLoader(), model.getAllImports()): null;
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
		
		final Future ret = new Future();
		
		if(config!=null)
		{
			ConfigurationInfo conf = model.getConfiguration(config);
			final ComponentInstanceInfo[] components = conf.getComponentInstances();
			SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
				.addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					// NOTE: in current implementation application waits for subcomponents
					// to be finished and cms implements a hack to get the external
					// access of an uninited parent.
					
					// (NOTE1: parent cannot wait for subcomponents to be all created
					// before setting itself inited=true, because subcomponents need
					// the parent external access.)
					
					// (NOTE2: subcomponents must be created one by one as they
					// might depend on each other (e.g. bdi factory must be there for jcc)).
					
					final IComponentManagementService ces = (IComponentManagementService)result;
					createComponent(components, ces, model, 0, ret);
				}
				public void exceptionOccurred(Exception exception)
				{
					// TODO Auto-generated method stub
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
		CounterResultListener lis = new CounterResultListener(exts.length, false, new DelegationResultListener(ret));
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
	public IFuture<IInternalService> addService(final String name, final Class type, final String proxytype, 
		final IServiceInvocationInterceptor[] ics, final Object service, final ProvidedServiceInfo info)
	{
//		System.out.println("addS:"+service);

		assert !getComponentAdapter().isExternalThread();
		final Future<IInternalService> ret = new Future<IInternalService>();
		
		final IInternalService proxy = BasicServiceInvocationHandler.createProvidedServiceProxy(
			getInternalAccess(), getComponentAdapter(), service, name, type, proxytype, ics, isCopy(), isRealtime(), getModel().getResourceIdentifier());
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
	protected IFuture<Void>	initService(final ProvidedServiceInfo info, final IModelInfo model)
	{
		final Future<Void> ret = new Future<Void>();
		assert !getComponentAdapter().isExternalThread();
		
		try
		{
			final ProvidedServiceImplementation	impl	= info.getImplementation();
			// Virtual service (e.g. promoted)
			if(impl!=null && impl.getBinding()!=null)
			{
				RequiredServiceInfo rsi = new RequiredServiceInfo(BasicService.generateServiceName(info.getType().getType( 
					getClassLoader()))+":virtual", info.getType().getType(getClassLoader()));
				IServiceIdentifier sid = BasicService.createServiceIdentifier(getExternalAccess().getServiceProvider().getId(), 
					rsi.getName(), rsi.getType().getType(getClassLoader()), BasicServiceInvocationHandler.class, getModel().getResourceIdentifier());
				final IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(
					getExternalAccess(), getComponentAdapter(), sid, rsi, impl.getBinding(), getClassLoader());
				getServiceContainer().addService(service, info).addResultListener(createResultListener(new DelegationResultListener<Void>(ret)));
			}
			else
			{
				Object	ser	= null;
				if(impl!=null && impl.getValue()!=null)
				{
					// todo: other Class imports, how can be found out?
					try
					{
						ser = UnparsedExpression.getParsedValue(impl, model.getAllImports(), getFetcher(), getClassLoader());
//								System.out.println("added: "+ser+" "+model.getName());
					}
					catch(RuntimeException e)
					{
						e.printStackTrace();
						throw new RuntimeException("Service creation error: "+info, e);
					}
				}
				else if(impl!=null && impl.getClazz().getType(getClassLoader())!=null)
				{
					ser = impl.getClazz().getType(getClassLoader()).newInstance();
				}
				
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
					
					final Class<?> type = info.getType().getType(getClassLoader());
					addService(info.getName(), type, info.getImplementation().getProxytype(), ics, ser, info)
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
//					e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
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
		return new ComponentResultListener(listener, getComponentAdapter());
	}

	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener)
	{
		return new IntermediateComponentResultListener(listener, getComponentAdapter());
	}

	/**
	 *  Create subcomponents.
	 */
	public void	createComponent(final ComponentInstanceInfo[] components, final IComponentManagementService cms, final IModelInfo model, final int i, final Future<Void> fut)
	{
		assert !getComponentAdapter().isExternalThread();
		
		if(i<components.length)
		{
			int num = getNumber(components[i], model);
			IResultListener crl = new CollectionResultListener(num, false, createResultListener(new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					createComponent(components, cms, model, i+1, fut);
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
					RequiredServiceBinding[] bindings = components[i].getBindings();
					// todo: rid
					cms.createComponent(getName(components[i], model, j+1), type.getName(),
						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), getComponentAdapter().getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, model.getAllImports(), bindings, null), null).addResultListener(crl);
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
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	public Map getArguments(ComponentInstanceInfo component, IModelInfo model)
	{
		assert !getComponentAdapter().isExternalThread();
		
		Map ret = null;		
		UnparsedExpression[] arguments = component.getArguments();
		UnparsedExpression argumentsexp = component.getArgumentsExpression();
		
		if(arguments.length>0)
		{
			ret = new HashMap();

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
			ret = (Map)SJavaParser.evaluateExpression(argumentsexp.getValue(), model.getAllImports(), getFetcher(), getClassLoader());
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
			fetcher.setValue("$n", new Integer(cnt));
			try
			{
				ret = (String)SJavaParser.evaluateExpression(component.getName(), model.getAllImports(), fetcher, getClassLoader());
				if(ret==null)
					ret = component.getName();
			}
			catch(Exception e)
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
				.addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					// Can use the parent resource identifier as child must depend on parent
					cms.loadComponentModel(filename, getModel().getResourceIdentifier()).addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IModelInfo model = (IModelInfo)result;
							final String modelname = model.getFullName();
						
							getChildrenAccesses().addResultListener(createResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									Collection col = (Collection)result;
									List res = new ArrayList();
									for(Iterator it=col.iterator(); it.hasNext(); )
									{
										IExternalAccess subcomp = (IExternalAccess)it.next();
										if(modelname.equals(subcomp.getModel().getFullName()))
										{
											res.add(subcomp.getComponentIdentifier());
										}
									}
									super.customResultAvailable((IComponentIdentifier[])res.toArray(new IComponentIdentifier[0]));
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
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildrenIdentifiers()
	{
		return getComponentAdapter().getChildrenIdentifiers();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
	{
		return getComponentAdapter().getChildrenAccesses();
	}
	
	/**
	 *  Notify the component listeners.
	 */
	public void notifyListeners(IComponentChangeEvent event)
	{
		assert !getComponentAdapter().isExternalThread();
		
		IComponentListener[] componentlisteners = getComponentListeners();
		for(int i=0; i<componentlisteners.length; i++)
		{
			final IComponentListener lis = componentlisteners[i];
			if(lis.getFilter().filter(event))
			{
				lis.eventOccured(event).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						//Print exception?
						removeComponentListener(lis);
					}
				});
			}
		}
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
		// todo: remember and cleanup timers in case of component removal.
		
		final Future<T> ret = new Future<T>();
		
		SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
					}
				});
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
	protected Future createStepFuture(IComponentStep step)
	{
		Future ret;
		try
		{
			Method method = step.getClass().getMethod("execute", new Class[]{IInternalAccess.class});
			Class clazz = method.getReturnType();
			ret = FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality(getLogger()));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
}
