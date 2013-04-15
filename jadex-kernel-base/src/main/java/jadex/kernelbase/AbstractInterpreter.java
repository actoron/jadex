package jadex.kernelbase;

import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.bridge.service.component.interceptors.ServiceGetter;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  The abstract interpreter add state to the 
 *  stateless interpreter and implements several abstract methods.
 */
public abstract class AbstractInterpreter extends StatelessAbstractInterpreter
{
	//-------- attributes --------
	
	/** The application type. */
	protected IModelInfo model;

	/** The application configuration. */
	protected String config;
	
	/** The arguments. */
	private Map<String, Object> arguments;
	
	/** The results. */
	protected Map<String, Object> results;
	
	/** The properties. */
	protected Map<String, Object> properties;
	
	/** The parent component. */
	protected IExternalAccess parent;

	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The value fetcher. */
	protected IValueFetcher	fetcher;
	
	/** The service container. */
	protected IServiceContainer container;
		
	/** The external access (cached). */
	protected IExternalAccess access;
	
	/** The required service binding information. */
	protected RequiredServiceBinding[] bindings;
	
	/** The extension instances. */
	protected Map<String, IExtensionInstance> extensions;	

	
	/** The parameter copy allowed flag. */
	protected boolean copy;

	/** The flag if local timeouts should be realtime. */
	protected boolean realtime;
	
	
//	/** The component listeners. */
//	protected List<IComponentListener> componentlisteners;
	
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<IMonitoringEvent>, IFilter<IMonitoringEvent>> subscriptions;

	/** The result listener. */
	protected IIntermediateResultListener<Tuple2<String, Object>> resultlistener;

	/** The monitoring service getter. */
	protected ServiceGetter<IMonitoringService> getter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public AbstractInterpreter(final IComponentDescription desc, final IModelInfo model, final String config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, 
		final RequiredServiceBinding[] bindings, boolean copy, boolean realtime,
		IIntermediateResultListener<Tuple2<String, Object>> resultlistener, final Future<Void> inited)
	{
		this.config = config!=null? config: model.getConfigurationNames().length>0? 
			model.getConfigurationNames()[0]: null;
		this.model = model;
		this.parent = parent;
		this.bindings = bindings;
		this.copy = copy;
		this.realtime = realtime;
		this.resultlistener = resultlistener;
		if(factory != null)
			this.adapter = factory.createComponentAdapter(desc, model, this, parent);
		this.container = createServiceContainer();
//		this.arguments = arguments!=null? new HashMap(arguments): null; // clone arguments
		
//		System.out.println("hhh: "+desc.getName()+" "+desc.getCause());
	}
	
	//-------- methods to be called by adapter --------
		
	/**
	 *  Can be called concurrently (also during executeAction()).
	 * 
	 *  Get the external access for this component.
	 *  The specific external access interface is kernel specific
	 *  and has to be casted to its corresponding incarnation.
	 *  @param listener	External access is delivered via result listener.
	 */
	public IExternalAccess getExternalAccess()
	{
		if(access==null)
		{
			synchronized(this)
			{
				if(access==null)
				{
					access	= new ExternalAccess(this);
				}
			}
		}
		
		return access;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return arguments!=null ? arguments : Collections.EMPTY_MAP;
	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchronous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map<String, Object> getResults()
	{
		// Todo: should be unmodifiable?
//		return results!=null? Collections.unmodifiableMap(results): Collections.EMPTY_MAP;
		return results!=null? results: Collections.EMPTY_MAP;
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{
		assert !getComponentAdapter().isExternalThread();
		
		// todo: store results only within listener?!
		if(results==null)
			results	= new HashMap<String, Object>();
		results.put(name, value);
		
		if(resultlistener!=null)
		{
			resultlistener.intermediateResultAvailable(new Tuple2<String, Object>(name, value));
		}
	}
	
	/**
	 *  Get the properties.
	 *  @return the properties.
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}
	
	/**
	 *  Get the parent.
	 *  @return The parent.
	 */
	public IExternalAccess getParentAccess()
	{
		return parent;
	}
	
	//-------- abstract interpreter methods --------
	
	/**
	 *  Get the component adapter.
	 *  @return The component adapter.
	 */
	public IComponentAdapter getComponentAdapter()
	{
		return adapter;
	}

	/**
	 *  Get the model.
	 */
	public IModelInfo getModel()
	{
		return model;
	}
	
	/**
	 *  Get the service bindings.
	 */
	public RequiredServiceBinding[] getServiceBindings()
	{
		return bindings;
	}
	
	/**
	 *  Get the value fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		assert !getComponentAdapter().isExternalThread();
		
		if(fetcher==null)
		{
			fetcher = new InterpreterFetcher(this);
		}
		return fetcher;
	}

	/**
	 *  Add a default value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public boolean addArgument(String name, Object value)
	{
		boolean ret = false;
		
		// Also called from constructor.
//		assert !getComponentAdapter().isExternalThread();
		
		if(arguments==null)
		{
			arguments	= new HashMap<String, Object>();
		}
		if(!arguments.containsKey(name))
		{
			arguments.put(name, value);
			ret = true;
		}
		
		return ret;
	}

	/**
	 *  Add a default value for a result (if not already present).
	 *  Called once for each result during init.
	 *  @param name	The result name.
	 *  @param value	The result value.
	 */
	public void	addDefaultResult(String name, Object value)
	{
		assert !getComponentAdapter().isExternalThread();
		
//		System.out.println("add def res: "+name+" "+value);
		
		if(results==null)
		{
			results	= new HashMap<String, Object>();
		}
		results.put(name, value);
	}
	
	/**
	 *  Add an extension.
	 *  @param name	The argument name.
	 *  @param value	The extension.
	 */
	public void	addExtension(String name, IExtensionInstance value)
	{
		assert !getComponentAdapter().isExternalThread();
		
		if(extensions==null)
		{
			extensions = new HashMap<String, IExtensionInstance>();
		}
		extensions.put(name, value);
	}
	
	/**
	 *  Add a property value.
	 *  @param name The name.
	 *  @param val The value.
	 */
	public void addProperty(String name, Object val)
	{
		assert !getComponentAdapter().isExternalThread();
		
		if(properties==null)
			properties = new HashMap<String, Object>();
		properties.put(name, val);
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IExtensionInstance getExtension(final String name)
	{
		assert !getComponentAdapter().isExternalThread();
		
		return extensions==null? null: (IExtensionInstance)extensions.get(name);
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IExtensionInstance[] getExtensions()
	{
		// Hack!!! When init fails , terminateExtensions() can not be called on component thread
		// as component already terminated.
		assert !getComponentAdapter().isExternalThread() || IComponentDescription.STATE_TERMINATED.equals(getComponentDescription().getState());
		
		return extensions==null? new IExtensionInstance[0]: 
			(IExtensionInstance[])extensions.values().toArray(new IExtensionInstance[extensions.size()]);
	}
	
	/**
	 *  Check if threa is allowed. 
	 *  
	 *  There is the problem that a component is already terminated and calls come back later.
	 *  In that case we allow the listeners to be called on the wrong thread.
	 */
	public void checkAllowedThread()
	{
		assert !getComponentAdapter().isExternalThread() || IComponentDescription.STATE_TERMINATED.equals(getComponentDescription().getState());
	}
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.config;
	}
	
	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public RequiredServiceBinding[]	getBindings()
	{
		return bindings;
	}
	
	/**
	 *  Create the service container.
	 *  @return The service conainer.
	 */
	public IServiceContainer createServiceContainer()
	{
		assert container==null;
		return new ComponentServiceContainer(adapter, getComponentAdapter().getDescription().getType(), getInternalAccess(), isRealtime());
	}
	
	/**
	 *  Create the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer()
	{
		assert container!=null;
//		if(container==null)
//			container = createServiceContainer();
		return container;
	}

	/**
	 *  Get the copy.
	 *  @return the copy.
	 */
	public boolean isCopy()
	{
		return copy;
	}

	/**
	 *  Get the realtime.
	 *  @return The realtime.
	 */
	public boolean isRealtime()
	{
		return realtime;
	}
	
	//-------- component listeners --------
	
//	/**
//	 *  Add an component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture<Void> addComponentListener(IComponentListener listener)
//	{
//		assert !getComponentAdapter().isExternalThread();
//		
//		if(componentlisteners==null)
//			componentlisteners = new ArrayList<IComponentListener>();
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
//	public IFuture<Void> removeComponentListener(IComponentListener listener)
//	{
//		assert !getComponentAdapter().isExternalThread();
//		
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
	
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public IComponentListener[] getComponentListeners()
//	{
//		assert !getComponentAdapter().isExternalThread();
//		
//		return componentlisteners==null? new IComponentListener[0]: 
//			(IComponentListener[])componentlisteners.toArray(new IComponentListener[componentlisteners.size()]);
//	}
//	
//	/**
//	 *  Get the component listeners.
//	 *  @return The component listeners.
//	 */
//	public Collection<IComponentListener> getInternalComponentListeners()
//	{
//		assert !getComponentAdapter().isExternalThread();
//		
//		return componentlisteners;	
//	}
	
	/**
	 *  Get the monitoring service getter.
	 *  @return The monitoring service getter.
	 */
	public ServiceGetter<IMonitoringService> getMonitoringServiceGetter()
	{
		if(getter==null)
			getter = new ServiceGetter<IMonitoringService>(getInternalAccess(), IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		return getter;
	}
	
	/**
	 *  Forward event to all currently registered subscribers.
	 */
	public void publishLocalEvent(IMonitoringEvent event)
	{
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<IMonitoringEvent> sub: subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[0]))
			{
				publishLocalEvent(event, sub);
			}
		}
	}
	
	/**
	 *  Forward event to one subscribers.
	 */
	protected void publishLocalEvent(IMonitoringEvent event, SubscriptionIntermediateFuture<IMonitoringEvent> sub)
	{
		IFilter<IMonitoringEvent> fil = subscriptions.get(sub);
		try
		{
			if(fil==null || fil.filter(event))
			{
//				System.out.println("forward to: "+event+" "+sub);
				if(!sub.addIntermediateResultIfUndone(event))
				{
					subscriptions.remove(fil);
				}
			}
		}
		catch(Exception e)
		{
			// catch filter exceptions
			e.printStackTrace();
		}
	}
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial)
	{
		final SubscriptionIntermediateFuture<IMonitoringEvent> ret = new SubscriptionIntermediateFuture<IMonitoringEvent>();
		
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeSubscription(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		// signal that subscription has been done
		ret.addIntermediateResult(null);

		addSubscription(ret, filter);
		
		if(initial)
		{
			List<IMonitoringEvent> evs = getCurrentStateEvents();
			if(evs!=null && evs.size()>0)
			{
				BulkMonitoringEvent bme = new BulkMonitoringEvent(evs.toArray(new IMonitoringEvent[evs.size()]));
				ret.addIntermediateResult(bme);
			}
		}
		
		return ret;
	}
		
	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> future, IFilter<IMonitoringEvent> filter)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<SubscriptionIntermediateFuture<IMonitoringEvent>, IFilter<IMonitoringEvent>>();
		subscriptions.put(future, filter);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param fut The subscription future to remove.
	 */
	protected void removeSubscription(SubscriptionIntermediateFuture<IMonitoringEvent> fut)
	{
		if(subscriptions==null || !subscriptions.containsKey(fut))
			throw new RuntimeException("Subscriber not known: "+fut);
		subscriptions.remove(fut);
	}
}
