package jadex.platform.service.cms;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.search.LocalServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.kernelbase.ExternalAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  Standalone platform component implementation.
 */
public class PlatformComponent implements IPlatformComponentAccess, IInternalAccess
{
//	//-------- constants -------- 
//	
//	/** The currently executing component (if any). */
//	// Provided for fast caller/callee context-switching avoiding to use cms.
//	public static final ThreadLocal<IInternalAccess>	LOCAL	= new ThreadLocal<IInternalAccess>();
//	
	//-------- attributes --------
	
	/** The creation info. */
	protected ComponentCreationInfo	info;
	
	/** The features. */
	protected Map<Class<?>, IComponentFeature>	features;
	
	/** The feature instances as list (for reverse execution, cached for speed). */
	protected List<IComponentFeature>	lfeatures;
	
	/** The logger. */
	protected Logger	logger;
	
	//-------- IPlatformComponentAccess interface --------
	
	/**
	 *  Create the component, i.e. instantiate its features.
	 *  
	 *  @param info The component creation info.
	 *  @param templates The component feature templates to be instantiated for this component.
	 */
	public void	create(ComponentCreationInfo info, Collection<IComponentFeature> templates)
	{
		this.info	= info;
		this.features	= new LinkedHashMap<Class<?>, IComponentFeature>();
		this.lfeatures	= new ArrayList<IComponentFeature>();
		for(IComponentFeature feature: templates)
		{
			IComponentFeature	instance	= feature.createInstance(getInternalAccess(), info);
			features.put((Class<?>)feature.getType(), instance);
			lfeatures.add(instance);
		}
	}
	
	/**
	 *  Perform the initialization of the component.
	 *  Tries to switch to a separate thread for the component as soon as possible.
	 *  
	 *  @return A future to indicate when the initialization is done.
	 */
	public IFuture<Void>	init()
	{
		Future<Void>	ret	= new Future<Void>();
		
		// Run init on component thread (hack!!! requires that execution feature works before its init)
		IExecutionFeature exe	= getComponentFeature(IExecutionFeature.class);
		exe.scheduleImmediate(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return initFeatures(lfeatures.iterator());
			}
		}).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}
	
	/**
	 *  Recursively init the features.
	 */
	protected IFuture<Void>	initFeatures(final Iterator<IComponentFeature> features)
	{
		if(features.hasNext())
		{
			final Future<Void>	ret	= new Future<Void>();
			features.next().init()
				.addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					initFeatures(features).addResultListener(new DelegationResultListener<Void>(ret));
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
	 *  Perform the main execution of the component (if any).
	 *  
	 *  @return A future to indicate when the body is done.
	 */
	public IFuture<Void>	body()
	{
		// todo...
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get the user view of this platform component.
	 *  
	 *  @return An internal access exposing user operations of the component.
	 */
	public IInternalAccess	getInternalAccess()
	{
		return this;
	}
	
	/**
	 *  Get the local platform service registry.
	 *  
	 *  @return The local platform service registry.
	 */
	public LocalServiceRegistry	getServiceRegistry()
	{
		return info.getServiceRegistry();
	}
	
	//-------- IInternalAccess interface --------
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return info.getModel();
	}

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		return info.getConfiguration();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return info.getComponentDescription().getName();
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription	getComponentDescription()
	{
		return info.getComponentDescription();
	}

	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T	getComponentFeature(Class<? extends T> type)
	{
		if(!features.containsKey(type))
		{
			throw new RuntimeException("No such feature: "+type);
		}
		else
		{
			return type.cast(features.get(type));
		}
	}

	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	// Todo: convenience object? -> fix search!?
	public IServiceContainer getServiceContainer()
	{
		// Todo: wrapped services with local interceptor chain
		return new IServiceContainer()
		{
			public <T> IIntermediateFuture<T> searchServices(Class<T> type, String scope)
			{
				return SServiceProvider.getServices(PlatformComponent.this, type, scope);
			}
			
			public <T> IIntermediateFuture<T> searchServices(Class<T> type)
			{
				return SServiceProvider.getServices(PlatformComponent.this, type);
			}
			
			public <T> IFuture<T> searchServiceUpwards(Class<T> type)
			{
				return SServiceProvider.getServiceUpwards(PlatformComponent.this, type);
			}
			
			public <T> IFuture<T> searchService(Class<T> type, String scope)
			{
				return SServiceProvider.getService(PlatformComponent.this, type, scope);
			}
			
			public <T> IFuture<T> searchService(Class<T> type)
			{
				return SServiceProvider.getService(PlatformComponent.this, type);
			}
			
			public <T> IFuture<T> getService(Class<T> type, IComponentIdentifier cid)
			{
				return SServiceProvider.getService(PlatformComponent.this, cid, type);
			}
		};
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		// Todo:
		return new Future<Map<String, Object>>();
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		// Todo: shadow access and invalidation
		return new ExternalAccess(this);
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
			String name = getLoggerName(getComponentIdentifier());
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
		// Todo: properties
		logger.setLevel(Level.INFO);
		
//		// get logging properties (from ADF)
//		// the level of the logger
//		// can be Integer or Level
//		
//		Object prop = component.getProperty("logging.level");
//		Level level = prop!=null? (Level)prop : logger.getParent()!=null && logger.getParent().getLevel()!=null ? logger.getParent().getLevel() : Level.SEVERE;
//		logger.setLevel(level);
//		
//		// if logger should use Handlers of parent (global) logger
//		// the global logger has a ConsoleHandler(Level:INFO) by default
//		prop = component.getProperty("logging.useParentHandlers");
//		if(prop!=null)
//		{
//			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
//		}
//			
//		// add a ConsoleHandler to the logger to print out
//        // logs to the console. Set Level to given property value
//		prop = component.getProperty("logging.addConsoleHandler");
//		if(prop!=null)
//		{
//			Handler console;
//			/*if[android]
//			console = new jadex.commons.android.AndroidHandler();
//			 else[android]*/
//			console = new ConsoleHandler();
//			/* end[android]*/
//			
//            console.setLevel(Level.parse(prop.toString()));
//            logger.addHandler(console);
//        }
//		
//		// Code adapted from code by Ed Komp: http://sourceforge.net/forum/message.php?msg_id=6442905
//		// if logger should add a filehandler to capture log data in a file. 
//		// The user specifies the directory to contain the log file.
//		// $scope.getAgentName() can be used to have agent-specific log files 
//		//
//		// The directory name can use special patterns defined in the
//		// class, java.util.logging.FileHandler, 
//		// such as "%h" for the user's home directory.
//		// 
//		String logfile =	(String)component.getProperty("logging.file");
//		if(logfile!=null)
//		{
//		    try
//		    {
//			    Handler fh	= new FileHandler(logfile);
//		    	fh.setFormatter(new SimpleFormatter());
//		    	logger.addHandler(fh);
//		    }
//		    catch (IOException e)
//		    {
//		    	System.err.println("I/O Error attempting to create logfile: "
//		    		+ logfile + "\n" + e.getMessage());
//		    }
//		}
//		
//		// Add further custom log handlers.
//		prop = component.getProperty("logging.handlers");
//		if(prop!=null)
//		{
//			if(prop instanceof Handler)
//			{
//				logger.addHandler((Handler)prop);
//			}
//			else if(SReflect.isIterable(prop))
//			{
//				for(Iterator<?> it=SReflect.getIterator(prop); it.hasNext(); )
//				{
//					Object obj = it.next();
//					if(obj instanceof Handler)
//					{
//						logger.addHandler((Handler)obj);
//					}
//					else
//					{
//						logger.warning("Property is not a logging handler: "+obj);
//					}
//				}
//			}
//			else
//			{
//				logger.warning("Property 'logging.handlers' must be Handler or list of handlers: "+prop);
//			}
//		}
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public IValueFetcher getFetcher()
	{
		// Return a fetcher that tries features first.
		// Todo: better (faster) way than throwing exceptions?
		return new IValueFetcher()
		{
			public Object fetchValue(String name)
			{
				Object	ret	= null;
				boolean	found	= false;
				
				for(int i=lfeatures.size()-1; !found && i>=0; i--)
				{
					try
					{
						ret	= lfeatures.get(i).fetchValue(name);
						found	= true;
					}
					catch(Exception e)
					{
					}
				}
				
				if(ret==null && "$component".equals(name))
				{
					ret	= getInternalAccess();
					found	= true;
				}
				
				if(!found)
				{
					throw new UnsupportedOperationException("Value not found: "+name);
				}
				
				return ret;
			}
			
			public Object fetchValue(String name, Object object)
			{
				Object	ret	= null;
				boolean	found	= false;
				
				for(int i=lfeatures.size()-1; !found && i>=0; i--)
				{
					try
					{
						ret	= lfeatures.get(i).fetchValue(name, object);
						found	= true;
					}
					catch(Exception e)
					{
					}
				}
				
				if(!found)
				{
					throw new UnsupportedOperationException("Value not found: "+name+", "+object);
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
		return ((ModelInfo)getModel()).getClassLoader();
	}
	
	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 *  @param initial True, for receiving the current state.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter, boolean initial, PublishEventLevel elm)
	{
		throw new UnsupportedOperationException();
	}
	

	/**
	 *  Publish a monitoring event. This event is automatically send
	 *  to the monitoring service of the platform (if any). 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event, PublishTarget pt)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Check if event targets exist.
	 */
	public boolean hasEventTargets(PublishTarget pt, PublishEventLevel pi)
	{
		// Todo!!!
		return false;
//		throw new UnsupportedOperationException();
	}
}
