package jadex.kernelbase;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IExternalAccess;
import jadex.bridge.RemoteComponentListener;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IValueFetcher;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	protected Map arguments;
	
	/** The arguments. */
	protected Map results;
	
	/** The properties. */
	protected Map properties;
	
	/** The parent component. */
	protected IExternalAccess parent;

	/** The component adapter. */
	protected IComponentAdapter	adapter;
	
	/** The value fetcher. */
	protected IValueFetcher	fetcher;
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** The component listeners. */
	protected List componentlisteners;
	
	/** The external access (cached). */
	protected IExternalAccess access;
	
	/** The required service binding information. */
	protected RequiredServiceBinding[] bindings;
	
	/** The extensions. */
	protected Map extensions;

	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public AbstractInterpreter(final IComponentDescription desc, final IModelInfo model, final String config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, 
		final RequiredServiceBinding[] bindings, final Future inited)
	{
		try
		{
			this.config = config!=null? config: model.getConfigurationNames().length>0? 
				model.getConfigurationNames()[0]: null;
			this.model = model;
			this.parent = parent;
			this.arguments = arguments;
			this.bindings = bindings;
			this.adapter = factory.createComponentAdapter(desc, getModel(), this, parent);
		}
		catch(Exception e)
		{
			inited.setException(e);
		}
	}
	
	//-------- methods to be called by adapter --------
	
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
		ComponentChangeEvent.dispatchTerminatingEvent(adapter, getModel(), getServiceProvider(), componentlisteners, null);
		
		// todo: call some application functionality for terminating?!
		
		final Future ret = new Future();
		
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				ComponentChangeEvent.dispatchTerminatedEvent(adapter, getModel(), getServiceProvider(), componentlisteners, ret);
			}
		});
		
		return ret;
//		return adapter.getServiceContainer().shutdown(); // done in adapter
	}
		
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
	public Map getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Get the results of the component (considering it as a functionality).
	 *  Note: The method cannot make use of the asynchrnonous result listener
	 *  mechanism, because the it is called when the component is already
	 *  terminated (i.e. no invokerLater can be used).
	 *  @return The results map (name -> value). 
	 */
	public Map getResults()
	{
		return results!=null? Collections.unmodifiableMap(results): Collections.EMPTY_MAP;
	}
	
	/**
	 *  Set a result value.
	 *  @param name The result name.
	 *  @param value The result value.
	 */
	public void setResultValue(String name, Object value)
	{
		if(results==null)
			results	= new HashMap();
		results.put(name, value);
	}
	
	/**
	 *  Get the properties.
	 *  @return the properties.
	 */
	public Map getProperties()
	{
		return properties;
	}

	/**
	 *  Add an component listener.
	 *  @param listener The listener.
	 */
	public IFuture addComponentListener(IComponentListener listener)
	{
		if(componentlisteners==null)
			componentlisteners = new ArrayList();
		
		// Hack! How to find out if remote listener?
		if(Proxy.isProxyClass(listener.getClass()))
			listener = new RemoteComponentListener(getExternalAccess(), listener);
		
		componentlisteners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a component listener.
	 *  @param listener The listener.
	 */
	public IFuture removeComponentListener(IComponentListener listener)
	{
		// Hack! How to find out if remote listener?
		if(Proxy.isProxyClass(listener.getClass()))
			listener = new RemoteComponentListener(getExternalAccess(), listener);
		
		if(componentlisteners!=null)
			componentlisteners.remove(listener);
		
//		System.out.println("cl: "+componentlisteners);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the component listeners.
	 *  @return The component listeners.
	 */
	public IComponentListener[] getComponentListeners()
	{
		return componentlisteners==null? new IComponentListener[0]: 
			(IComponentListener[])componentlisteners.toArray(new IComponentListener[componentlisteners.size()]);
	}
	
	/**
	 *  Get the parent.
	 *  @return The parent.
	 */
	public IExternalAccess getParent()
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
	public void	addDefaultArgument(String name, Object value)
	{
		if(arguments==null)
		{
			arguments	= new HashMap();
		}
		if(!arguments.containsKey(name))
		{
			arguments.put(name, value);
		}
	}

	/**
	 *  Add a default value for a result (if not already present).
	 *  Called once for each result during init.
	 *  @param name	The result name.
	 *  @param value	The result value.
	 */
	public void	addDefaultResult(String name, Object value)
	{
		if(results==null)
		{
			results	= new HashMap();
		}
		results.put(name, value);
	}
	
	/**
	 *  Add a default value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public void	addExtension(String name, Object value)
	{
		if(extensions==null)
		{
			extensions = new HashMap();
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
		if(properties==null)
			properties = new HashMap();
		properties.put(name, val);
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public Object getExtension(final String name)
	{
		return extensions==null? null: extensions.get(name);
	}
	
	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.config;
	}

}
