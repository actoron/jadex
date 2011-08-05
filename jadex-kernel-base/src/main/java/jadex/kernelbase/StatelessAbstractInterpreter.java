package jadex.kernelbase;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
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
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInfo;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

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
	 *  Can be called concurrently (also during executeAction()).
	 *  
	 *  Inform the agent that a message has arrived.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param message The message that arrived.
	 */
	public abstract void messageArrived(final IMessageAdapter message);

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
	public abstract Map getResults();

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
	 *  Request component to kill itself.
	 *  The component might perform arbitrary cleanup activities during which executeAction()
	 *  will still be called as usual.
	 *  Can be called concurrently (also during executeAction()).
	 *  @param listener	When cleanup of the component is finished, the listener must be notified.
	 */
	public IFuture cleanupComponent()
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future ret = new Future();

		ComponentChangeEvent.dispatchTerminatingEvent(getComponentAdapter(), getCreationTime(), getModel(), getServiceProvider(), getInternalComponentListeners(), null);
		
		startEndSteps().addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				terminateExtensions().addResultListener(createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ComponentChangeEvent.dispatchTerminatedEvent(getComponentAdapter(), getCreationTime(), getModel(), getServiceProvider(), getInternalComponentListeners(), ret);
					}
				}));
			}
		}));
		
		return ret;
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
	
	/**
	 *  Called when a component has been created as a subcomponent of this component.
	 *  This event may be ignored, if no special reaction to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The newly created component.
	 */
	public IFuture	componentCreated(final IComponentDescription desc, final IModelInfo model)
	{
		// cannot use scheduleStep as it is not available in init phase of component.
//		return scheduleStep(new IComponentStep()
		return getExternalAccess().scheduleImmediate(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
//				System.out.println("created: "+desc.getName());
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, TYPE_COMPONENT, model.getFullName(), desc.getName().getName(), getComponentIdentifier(), getCreationTime(), desc);
				notifyListeners(event);
				return null;
			}
		});
	}
	
	/**
	 *  Called when a subcomponent of this component has been destroyed.
	 *  This event may be ignored, if no special reaction  to new or destroyed components is required.
	 *  The current subcomponents can be accessed by IComponentAdapter.getSubcomponents().
	 *  @param comp	The destroyed component.
	 */
	public IFuture	componentDestroyed(final IComponentDescription desc)
	{
//		System.out.println("dest: "+desc.getName());
		return scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
//				System.out.println("destroyed: "+desc.getName());
				ComponentChangeEvent event = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, TYPE_COMPONENT, desc.getModelName(), desc.getName().getName(), getComponentIdentifier(), getCreationTime(), desc);
				notifyListeners(event);
				return null;
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
					step	= UnparsedExpression.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getModel().getClassLoader());
				}
				else
				{
					Class	clazz	= SReflect.findClass0(upes[i].getClassName(), getModel().getAllImports(), getModel().getClassLoader());
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
					throw new RuntimeException("Unsupported initial component step, class="+upes[i].getClassName()+", value="+upes[i].getValue());
				}
			}
		}
	}
	
	/**
	 *  Start the end steps of the component.
	 *  Called as part of cleanup behavior.
	 */
	public IFuture	startEndSteps()
	{
		Future	ret	= new Future();
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
					step	= UnparsedExpression.getParsedValue(upes[i], getModel().getAllImports(), getFetcher(), getModel().getClassLoader());
				}
				else
				{
					Class	clazz	= SReflect.findClass0(upes[i].getClassName(), getModel().getAllImports(), getModel().getClassLoader());
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
					ret.setException(new RuntimeException("Unsupported component end step, class="+upes[i].getClassName()+", value="+upes[i].getValue()));
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
	public abstract IFuture scheduleStep(IComponentStep step);
	
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
	 *  Return the creation time of the component.
	 *  
	 *  @return The creation time of the component.
	 */
	public abstract long getCreationTime();
	
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public abstract IComponentListener[] getComponentListeners();
	
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public abstract Collection getInternalComponentListeners();

	/**
	 *  Add component listener.
	 */
	public abstract IFuture addComponentListener(IComponentListener listener);

	/**
	 *  Remove component listener.
	 */
	public abstract IFuture removeComponentListener(IComponentListener listener);
	
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
	public abstract Map getProperties();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public abstract Map getArguments();
	
	/**
	 *  Flag for parameter copy.
	 *  @return True, if copy copy should be used. 
	 */
	public abstract boolean isCopy();
	
	//-------- methods --------
	
	/**
	 *  Main init method consists of the following steps:
	 *  - init future properties
	 *  - init arguments and results
	 *  - init extensions
	 *  - init required and provided services
	 *  - init subcomponents
	 */
	public IFuture init(final IModelInfo model, final String config, final Map arguments)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future fut = new Future();
		IFuture	ret	= fut;
		
		if(config!=null && model.getConfiguration(config)==null)
		{
			fut.setException(new RuntimeException("No such configuration in model: "+model.getFullName()+", "+config));
		}
		else
		{
			initFutureProperties(model).addResultListener(
				createResultListener(new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					initArguments(model, config, arguments).addResultListener(
						createResultListener(new DelegationResultListener(fut)
					{
						public void customResultAvailable(Object result)
						{
							initExtensions(model, config).addResultListener(
								createResultListener(new DelegationResultListener(fut)
							{
								public void customResultAvailable(Object result)
								{
									initServices(model, config).addResultListener(
										createResultListener(new DelegationResultListener(fut)
									{
										public void customResultAvailable(Object result)
										{
											initComponents(model, config).addResultListener(
												createResultListener(new DelegationResultListener(fut)
											{
												public void customResultAvailable(Object result)
												{
													super.customResultAvailable(new Object[]{StatelessAbstractInterpreter.this, getComponentAdapter()});
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
			final Future iret = new Future();
			fut.addResultListener(createResultListener(new DelegationResultListener(iret)
			{
				public void exceptionOccurred(final Exception exception)
				{
					terminateExtensions().addResultListener(new DelegationResultListener(iret)
					{
						public void customResultAvailable(Object result)
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
	public IFuture initArguments(IModelInfo model, final String config, Map arguments)
	{
		assert !getComponentAdapter().isExternalThread();
		
		// Call add default argument also for passed arguments.
		if(arguments!=null)
		{
			for(Iterator it=arguments.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
				addArgument(key, arguments.get(key));
			}
		}
		
		ConfigurationInfo	ci	= config!=null ? model.getConfiguration(config) : null;
		
		// Init the arguments with initial or default values.
		Set	done	= new HashSet();
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
		done	= new HashSet();
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
			if(!done.contains(res[i].getName()))
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
	public IFuture initServices(final IModelInfo model, final String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future	ret	= new Future();
		
		try
		{
			// Provided services.
//			System.out.println("init sers: "+services);
			ProvidedServiceInfo[] ps = model.getProvidedServices();
			
			Map sermap = new LinkedHashMap();
			for(int i=0; i<ps.length; i++)
			{
				Object key = ps[i].getName()!=null? ps[i].getName(): ps[i].getType();
				sermap.put(key, ps[i]);
			}
			if(config!=null)
			{
				ConfigurationInfo cinfo = model.getConfiguration(config);
				ProvidedServiceInfo[] cs = cinfo.getProvidedServices();
				for(int i=0; i<cs.length; i++)
				{
					Object key = cs[i].getName()!=null? cs[i].getName(): cs[i].getType();
					ProvidedServiceInfo psi = (ProvidedServiceInfo)sermap.get(key);
					ProvidedServiceInfo newpsi= new ProvidedServiceInfo(psi.getName(), psi.getType(), new ProvidedServiceImplementation(cs[i].getImplementation()));
					sermap.put(key, newpsi);
				}
			}
			ProvidedServiceInfo[] services = (ProvidedServiceInfo[])sermap.values().toArray(new ProvidedServiceInfo[sermap.size()]);
			initProvidedServices(0, services, model).addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					// Required services.
					RequiredServiceInfo[] ms = model.getRequiredServices();
					
					Map	sermap = new LinkedHashMap();
					for(int i=0; i<ms.length; i++)
					{
						ms[i]	= new RequiredServiceInfo(getServicePrefix()+ms[i].getName(), ms[i].getType(), ms[i].isMultiple(), ms[i].getDefaultBinding());
						sermap.put(ms[i].getName(), ms[i]);
					}

					if(config!=null)
					{
						ConfigurationInfo cinfo = model.getConfiguration(config);
						RequiredServiceInfo[] cs = cinfo.getRequiredServices();
						for(int i=0; i<cs.length; i++)
						{
							RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(getServicePrefix()+cs[i].getName());
							RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType(), rsi.isMultiple(), 
								new RequiredServiceBinding(cs[i].getDefaultBinding()));
							sermap.put(rsi.getName(), newrsi);
						}
					}
					if(getBindings()!=null)
					{
						for(int i=0; i<getBindings().length; i++)
						{
							RequiredServiceInfo rsi = (RequiredServiceInfo)sermap.get(getBindings()[i].getName());
							RequiredServiceInfo newrsi = new RequiredServiceInfo(rsi.getName(), rsi.getType(), rsi.isMultiple(), 
								new RequiredServiceBinding(getBindings()[i]));
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
	protected IFuture	initProvidedServices(final int i, final ProvidedServiceInfo[] services, final IModelInfo model)
	{
		final IFuture	ret;
		
		if(i<services.length)
		{
			ret	= new Future();
			IFuture	fut;
			ProvidedServiceImplementation impl = services[i].getImplementation(); 
			if(impl!=null && (impl.getExpression()!=null || impl.getImplementation()!=null))
			{
				try
				{
					fut	= initService(services[i], model);
				}
				catch(Exception e)
				{
					fut	= new Future(e);
				}
			}
			else 
			{
				RequiredServiceInfo info = new RequiredServiceInfo(BasicService.generateServiceName(services[i].getType())+":virtual", services[i].getType());
				IServiceIdentifier sid = BasicService.createServiceIdentifier(getExternalAccess().getServiceProvider().getId(), 
					info.getName(), info.getType(), BasicServiceInvocationHandler.class);
				IInternalService service = BasicServiceInvocationHandler.createDelegationProvidedServiceProxy(getExternalAccess(), getComponentAdapter(), 
					sid, info, impl.getBinding(), isCopy());
				fut	= getServiceContainer().addService(service);
			}
			
			fut.addResultListener(createResultListener(new DelegationResultListener((Future)ret)
			{
				public void customResultAvailable(Object result)
				{
					initProvidedServices(i+1, services, model)
						.addResultListener(createResultListener(new DelegationResultListener((Future)ret)));
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
	public IFuture initFutureProperties(IModelInfo model)
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
					final UnparsedExpression unexp = (UnparsedExpression)value;
					final Object val = SJavaParser.evaluateExpression(unexp.getValue(), model.getAllImports(), getFetcher(), model.getClassLoader());
					Class clazz = unexp.getClazz(model.getClassLoader(), model.getAllImports());
					if(SReflect.isSupertype(IFuture.class, clazz!=null? clazz: val.getClass()))
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
							throw new RuntimeException("Future property must be instance of jadex.commons.IFuture: "+name+", "+unexp.getValue());
						}
					}
					else
					{
						// Todo: handle specific properties (logging etc.)
						addProperty(name, val);
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
	public IFuture initComponents(final IModelInfo model, String config)
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
					createComponent(components, ces, model, 0)
						.addResultListener(createResultListener(new DelegationResultListener(ret)));
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
	public IFuture initExtensions(IModelInfo model, String config)
	{
		assert !getComponentAdapter().isExternalThread();
		
		final Future ret = new Future();
		
		if(config!=null)
		{
			ConfigurationInfo conf = model.getConfiguration(config);
			final IExtensionInfo[] exts = conf.getExtensions();
			IResultListener	rl	= new DelegationResultListener(ret)
			{
				int	i=0;
				public void customResultAvailable(Object result)
				{
					if(i>0)
					{
						addExtension(exts[i-1].getName(), (IExtensionInstance)result);
					}
					
					if(i<exts.length)
					{
						i++;
						exts[i-1].createInstance(getExternalAccess(), getFetcher())
							.addResultListener(createResultListener(this));
					}
					else
					{
						super.customResultAvailable(result);
					}
				}
			};
			
			rl.resultAvailable(null);
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Terminate all extensions.
	 */
	public IFuture terminateExtensions()
	{
		// Hack!!! When init fails , terminateExtensions() can not be called on component thread
		// as component already terminated.
		assert !getComponentAdapter().isExternalThread() || IComponentDescription.STATE_TERMINATED.equals(getComponentDescription().getState());
		
		Future ret = new Future();
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
	public IFuture	addService(String name, Class type, String proxytype, IServiceInvocationInterceptor[] ics, Object service)
	{
		assert !getComponentAdapter().isExternalThread();
		
		IInternalService proxy = BasicServiceInvocationHandler.createProvidedServiceProxy(
			getInternalAccess(), getComponentAdapter(), service, name, type, proxytype, ics, isCopy());
		return getServiceContainer().addService(proxy);
	}
	
	/**
	 *  Add a service to the component. 
	 *  @param info The provided service info.
	 */
	protected IFuture	initService(ProvidedServiceInfo info, IModelInfo model)
	{
		IFuture	ret	= null;
		assert !getComponentAdapter().isExternalThread();
		
		try
		{
			ProvidedServiceImplementation	impl	= info.getImplementation();
			Object	ser	= null;
			if(impl.getExpression()!=null)
			{
				// todo: other Class imports, how can be found out?
				ser = SJavaParser.evaluateExpression(impl.getExpression(), model.getAllImports(), getFetcher(), model.getClassLoader());
//				System.out.println("added: "+service+" "+getAgentAdapter().getComponentIdentifier());
			}
			else if(impl.getImplementation()!=null)
			{
				ser = impl.getImplementation().newInstance();
			}
			
			if(ser==null)
			{
				ret = new Future(new RuntimeException("Service creation error: "+impl.getImplementation()+" "+impl.getExpression()+" "+impl.getBinding()));
			}
			else
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
							ics[i] = (IServiceInvocationInterceptor)SJavaParser.evaluateExpression(ins[i].getValue(), model.getAllImports(), getFetcher(), model.getClassLoader());
						}
						else
						{
							ics[i] = (IServiceInvocationInterceptor)ins[i].getClazz(model.getClassLoader(), model.getAllImports()).newInstance();
						}
					}
				}
				ret	= addService(info.getName(), info.getType(), info.getImplementation().getProxytype(), ics, ser);
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			ret	= new Future(e);
		}
		
		return ret;
	}
	
	//-------- methods to be called by adapter --------
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				((IComponentManagementService)result).destroyComponent(getComponentAdapter().getComponentIdentifier())
					.addResultListener(new DelegationResultListener(ret));
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
	public ClassLoader getClassLoader()
	{
		return getModel().getClassLoader();
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
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, getComponentAdapter());
	}

	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public IIntermediateResultListener createResultListener(IIntermediateResultListener listener)
	{
		return new IntermediateComponentResultListener(listener, getComponentAdapter());
	}

	/**
	 *  Create subcomponents.
	 */
	public IFuture	createComponent(final ComponentInstanceInfo[] components, final IComponentManagementService cms, final IModelInfo model, final int i)
	{
		assert !getComponentAdapter().isExternalThread();
		
		IFuture	ret;
		if(i<components.length)
		{
			final Future	fut	= new Future();
			ret	= fut;
			int num = getNumber(components[i], model);
			IResultListener crl = new CollectionResultListener(num, false, createResultListener(new DelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					createComponent(components, cms, model, i+1)
						.addResultListener(createResultListener(new DelegationResultListener(fut)));
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
					cms.createComponent(getName(components[i], model, j+1), type.getName(),
						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), getComponentAdapter().getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, model.getAllImports(), bindings), null).addResultListener(crl);
				}
				else
				{
					crl.exceptionOccurred(new RuntimeException("No such component type: "+components[i].getTypeName()));
				}
			}
		}
		else
		{
			ret	= IFuture.DONE;
		}
		return ret;
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
					Object val = SJavaParser.evaluateExpression(arguments[i].getValue(), model.getAllImports(), getFetcher(), model.getClassLoader());
					ret.put(arguments[i].getName(), val);
				}
			}
		}
		else if(argumentsexp!=null && argumentsexp.getValue()!=null && argumentsexp.getValue().length()>0)
		{
			// todo: language
			ret = (Map)SJavaParser.evaluateExpression(argumentsexp.getValue(), model.getAllImports(), getFetcher(), model.getClassLoader());
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
		
		Object ret = component.getNumber()!=null? SJavaParser.evaluateExpression(component.getNumber(), model.getAllImports(), getFetcher(), model.getClassLoader()): null;
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
				ret = (String)SJavaParser.evaluateExpression(component.getName(), model.getAllImports(), fetcher, model.getClassLoader());
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
	public IFuture getChildren(final String type)
	{
		final Future ret = new Future();
		final String filename = getComponentFilename(type);
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.loadComponentModel(filename).addResultListener(createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IModelInfo model = (IModelInfo)result;
						final String modelname = model.getFullName();
					
						getChildren().addResultListener(createResultListener(new DelegationResultListener(ret)
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
								super.customResultAvailable(res);
							}
						}));
					}
				}));
			}	
		}));
		
		return ret;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
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
	
}
