package jadex.base;

import jadex.bridge.CheckedAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *  Base component adapter with reusable functionality for all platforms.
 */
public abstract class AbstractComponentAdapter implements IComponentAdapter, IExecutable, Serializable
{
	//-------- attributes --------

	/** The component identifier. */
	protected IExternalAccess parent;

	/** The component instance. */
	protected IComponentInstance component;
	
	/** The component model. */
	protected IModelInfo model;

	/** The description holding the execution state of the component
	   (read only! managed by component execution service). */
	protected IComponentDescription	desc;
	
	/** The component logger. */
	protected Logger logger;
	
	/** Flag to indicate a fatal error (component termination will not be passed to instance) */
	protected Exception exception;
	
	/** Flag to indicate that the component instance is created. */
	protected boolean	instantiated;
	
	/** The kill future to be notified in case of fatal error during shutdown. */
	protected Future<Void>	killfuture;
	
	//-------- steppable attributes --------
	
	/** The flag for a scheduled step (true when a step is allowed in stepwise execution). */
	protected boolean	dostep;
	
	/** The listener to be informed, when the requested step is finished. */
	protected Future stepfuture;
	
	/** The selected breakpoints (component will change to step mode, when a breakpoint is reached). */
	protected Set	breakpoints;
	
	/** The breakpoint commands (executed, when a breakpoint triggers). */
	protected ICommand[]	breakpointcommands;
	
	//-------- external actions --------

	/** The thread executing the component (null for none). */
	// Todo: need not be transient, because component should only be serialized when no action is running?
	protected transient Thread componentthread;

	// todo: ensure that entries are empty when saving
	/** The entries added from external threads. */
	protected List	ext_entries;

	/** The flag if external entries are forbidden. */
	protected boolean ext_forbidden;
	
	/** Set when wakeup was called. */
	protected boolean	wokenup;
	
	/** Does the instance want to be executed again. */
	protected boolean	again;
	
	/** The cached cms. */
	protected IFuture<IComponentManagementService>	cms;

	/** The cached clock service. */
	protected IClockService clock;
	
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public AbstractComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInstance component, IExternalAccess parent)
	{
		this.desc = desc;
		this.model = model;
		this.component = component;
		this.parent	= parent;
	}
	
	//-------- IComponentAdapter methods --------

	/**
	 *  Called by the component when it probably awoke from an idle state.
	 *  The platform has to make sure that the component will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no component related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void wakeup()
	{
		// Do not wake up until component instance is completely instantiated by factory
		// (to avoid double execution between constructor and executor)
		if(!instantiated)
			return;
//		System.err.println("wakeup: "+getComponentIdentifier());
//		Thread.dumpStack();
		
		if(clock==null)
		{
			SServiceProvider.getService(getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener<IClockService>(logger)
			{
				public void resultAvailable(IClockService result)
				{
					clock = result;
					wakeup();
				}

				public void exceptionOccurred(Exception exception)
				{
					if(!(exception instanceof ComponentTerminatedException))
						super.exceptionOccurred(exception);
				}
			});
		}
		else
		{
			wokenup	= true;
			if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
				throw new ComponentTerminatedException(desc.getName());
			
			// Set processing state to ready if not running.
//			if(IComponentDescription.PROCESSINGSTATE_IDLE.equals(desc.getProcessingState()))
//			{
//				getCMS().addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						((ComponentManagementService)result).setProcessingState(cid, IComponentDescription.PROCESSINGSTATE_READY);
//					}
//					public void exceptionOccurred(Exception exception)
//					{
//						// Might happen during platform init -> ignore
//					}
//				});				
//			}
//			if(IComponentDescription.PROCESSINGSTATE_IDLE.equals(desc.getProcessingState()))
//			{
//				getCMS().addResultListener(new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						((ComponentManagementService)result).setProcessingState(cid, IComponentDescription.PROCESSINGSTATE_READY);
//					}
//				});				
//			}
			
			// Resume execution of the component.
			if(IComponentDescription.STATE_ACTIVE.equals(desc.getState())
				|| IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))	// Hack!!! external entries must also be executed in suspended state.
			{
				doWakeup();
			}
		}
	}

	/**
	 *  Return a component-identifier that allows to send
	 *  messages to this component.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return desc.getName();
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
			
			// if logger does not already exists, create it
			if(logger==null)
			{
				// Hack!!! Might throw exception in applet / webstart.
				try
				{
					logger = Logger.getLogger(name);
					initLogger(logger);
					logger = new LoggerWrapper(logger, clock);
					//System.out.println(logger.getParent().getLevel());
				}
				catch(SecurityException e)
				{
					// Hack!!! For applets / webstart use anonymous logger.
					logger = Logger.getAnonymousLogger();
					initLogger(logger);
					logger = new LoggerWrapper(logger, clock);
				}
			}
		}
		
		return logger;
	}

	public static String getLoggerName(IComponentIdentifier cid)
	{
		//String name = getComponentIdentifier().getLocalName();
		//String name = getModel().getFullName()+"."+getComponentIdentifier().getLocalName();
		// Prepend parent names for nested loggers.
		String	name	= null;
		for(; cid!=null; cid=cid.getParent())
		{
			name	= name==null ? cid.getLocalName() : cid.getLocalName() + "." +name;
		}
		// System.out.println("logname: "+name);
		return name;
	}
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected void initLogger(Logger logger)
	{
		if(logger==null)
			System.out.println("gshdfghsdf");
		
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		
		Object prop = component.getProperty("logging.level");
		Level level = prop!=null? (Level)prop : logger.getParent()!=null ? logger.getParent().getLevel() : Level.SEVERE;
		logger.setLevel(level);
		
//		System.out.println("set: "+logger.getName()+" "+level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = component.getProperty("logging.useParentHandlers");
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = component.getProperty("logging.addConsoleHandler");
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
		String logfile =	(String)component.getProperty("logging.file");
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
		prop = component.getProperty("logging.handlers");
		if(prop!=null)
		{
			if(prop instanceof Handler)
			{
				logger.addHandler((Handler)prop);
			}
			else if(SReflect.isIterable(prop))
			{
				for(Iterator it=SReflect.getIterator(prop); it.hasNext(); )
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
	 *  Get the model.
	 *  @return The model.
	 */
	public IModelInfo getModel()
	{
		return this.model;
	}

	/**
	 *  Get the parent component.
	 *  @return The parent (if any).
	 */
	public IExternalAccess getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 * /
	public IFuture getChildren()
	{
		final Future ret = new Future();
		
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				cms.getChildren(getComponentIdentifier()).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentIdentifier[] childs = (IComponentIdentifier[])result;
						IResultListener	crl	= new CollectionResultListener(childs.length, true, new DelegationResultListener(ret));
						for(int i=0; !ret.isDone() && i<childs.length; i++)
						{
							cms.getExternalAccess(childs[i]).addResultListener(crl);
						}
					}
				});
			}
		});
		
		return ret;
	}*/
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 * /
	public IFuture getChildren()
	{
		final Future ret = new Future();
		
		getCMS().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				cms.getChildren(getComponentIdentifier()).addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}*/
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildrenIdentifiers()
	{
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		
		getCMS().addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier[]>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getChildren(getComponentIdentifier()).addResultListener(
					new DelegationResultListener<IComponentIdentifier[]>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
	{
		final Future<Collection<IExternalAccess>> ret = new Future<Collection<IExternalAccess>>();
		
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<IExternalAccess>>(ret)
		{
			public void customResultAvailable(IComponentManagementService result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				cms.getChildren(getComponentIdentifier()).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier[], Collection<IExternalAccess>>(ret)
				{
					public void customResultAvailable(IComponentIdentifier[] children)
					{
						IResultListener<IExternalAccess>	crl	= new CollectionResultListener<IExternalAccess>(children.length, true,
							new DelegationResultListener<Collection<IExternalAccess>>(ret));
						for(int i=0; !ret.isDone() && i<children.length; i++)
						{
							cms.getExternalAccess(children[i]).addResultListener(crl);
						}
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  String representation of the component.
	 */
	public String toString()
	{
		return "StandaloneComponentAdapter("+desc.getName().getName()+")";
	}

	
	/**
	 *  Get the service provider.
	 */
	public IServiceContainer getServiceContainer()
	{
		return component.getServiceContainer();
	}
	
	/**
	 *  Get the (cached) cms.
	 */
	protected IFuture<IComponentManagementService> getCMS()
	{
		// Change comments below to test performance of cached cms vs. direct access.
		if(getServiceContainer()==null)
		{
			System.out.println("container is null: "+component+", "+getComponentIdentifier());
		}
//		return SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class);
		if(cms==null)
		{
			cms	= SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class);
		}
		return cms;
	}
	
	//-------- methods called by the standalone platform --------
	
	/**
	 *  Set the inited flag to allow external component wake ups.
	 */
	public void	setInited(boolean inited)
	{
		this.instantiated	= inited;
	}
	
	/**
	 *  Get description.
	 */
	public IComponentDescription getDescription()
	{
		return desc;
	}
	
	/**
	 *  Gracefully terminate the component.
	 *  This method is called from cms and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @return A future to indicate, when cleanup of the component is finished.
	 */
	public IFuture<Void> killComponent()
	{
		assert killfuture==null;
		
//		if("Application".equals(desc.getType()))
//			System.out.println("killComponent: "+getComponentIdentifier());
		
		killfuture = new Future<Void>();
		
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
		{
//			if("Application".equals(desc.getType()))
//				System.out.println("killComponent0: "+getComponentIdentifier());
			killfuture.setException(new ComponentTerminatedException(desc.getName()));
		}
		else
		{
			if(exception==null)
			{
//				if("Application".equals(desc.getType()))
//					System.out.println("killComponent1: "+getComponentIdentifier());
				invokeLater(new Runnable()
				{
					public void run()
					{
//						if("Application".equals(desc.getType()))
//							System.out.println("killComponent2: "+getComponentIdentifier());
						component.cleanupComponent()
							.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								synchronized(AbstractComponentAdapter.this)
								{
									// Do final cleanup step as (last) ext_entry
									// for allowing previously added entries still be executed.
									Runnable laststep = new LastStep();
//									Runnable	laststep	= new Runnable()
//									{								
//										public void run()
//										{
//											clock	= null;
//											cms	= null;
////											component	= null;	// Required by getResults()
//											model	= null;
////											desc	= null;	// Required by toString()
//											parent	= null;
//											killfuture.setResult(null);
//											
////											System.out.println("Checking ext entries after cleanup: "+cid);
//											assert ext_entries==null || ext_entries.isEmpty() : "Ext entries after cleanup: "+desc.getName()+", "+ext_entries;
//										}
//									};
									// In case of platform invokerLater cannot be called.
									if(getComponentIdentifier().getParent()!=null)
									{
										invokeLater(laststep);
//										if(ext_entries==null)
//											ext_entries	= new ArrayList();
//										ext_entries.add(laststep);
										// No more ext entries after cleanup step allowed.
										ext_forbidden	= true;
//										wakeup();
									}
									else
									{
										// Execute last step of platform directly
										// No more ext entries after cleanup step allowed.
										
										// Resets component thread to avoid asserts
										Thread oldct = componentthread;
										componentthread	= Thread.currentThread();
										ext_forbidden	= true;
										if(ext_entries==null)
											ext_entries	= new ArrayList();
										ext_entries.add(laststep);
										executeExternalEntries(true);
										ext_forbidden	= true;
										componentthread = oldct;
//										laststep.run();
									}
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								getLogger().warning("Exception during component cleanup: "+exception);
								killfuture.setException(exception);
//								shutdownContainer().addResultListener(new DelegationResultListener(killfuture));
							}
						});
					}
				});
			}
			else
			{
//				if("Application".equals(desc.getType()))
//					System.out.println("killComponent3: "+getComponentIdentifier());
				killfuture.setResult(null);
//				listener.resultAvailable(this, getComponentIdentifier());
			}
		}
		
		return killfuture;
		
		// LogManager causes memory leak till Java 7
		// No way to remove loggers and no weak references. 
	}
	
	/**
	 *  Called when a message was sent to the component.
	 *  (Called from message transport).
	 *  (Is it ok to call on external thread?).
	 */
	public void	receiveMessage(Map message, MessageType type)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || exception!=null)
			throw new ComponentTerminatedException(desc.getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		IMessageAdapter msg = new DefaultMessageAdapter(message, type);
		component.messageArrived(msg);
	}
	
	/**
	 *  Called when a stream was sent to the component.
	 *  (Called from message transport).
	 *  (Is it ok to call on external thread?).
	 */
	public void	receiveStream(IConnection con)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || exception!=null)
			throw new ComponentTerminatedException(desc.getName());

		component.streamArrived(con);
	}
	
	//-------- IExecutable interface --------
	
	// for testing double execution.
	boolean executing;
//	Exception	rte;

	/**
	 *  Executable code for running the component
	 *  in the platforms executor service.
	 */
	public boolean	execute()
	{
//		synchronized(AsyncExecutionService.DEBUG)
//		{
//			AsyncExecutionService.DEBUG.put(this, "adapter execute()");
//		}
		
//		if(getComponentIdentifier().getLocalName().indexOf("Alex")!=-1)
//			System.out.println("entering exe: "+getComponentIdentifier());
		
		if(executing)
		{
			System.err.println(getComponentIdentifier()+": double execution");
//			List	debug	= (List)AsyncExecutionService.DEBUG.getCollection(this);
//			for(int i=0; i<debug.size(); i++)
//				System.err.println(getComponentIdentifier()+": "+debug.get(i));
//			rte.printStackTrace();
			new RuntimeException("executing: "+getComponentIdentifier()).printStackTrace();
		}
//		rte	= new DebugException("executing: "+getComponentIdentifier());
		executing	= true;
		wokenup	= false;	
		
//		if(instantiated && Future.STACK.get()!=null)
//		{
//			System.out.println("futurestack in adapter!");
//		}
		// Note: wakeup() can be called from arbitrary threads (even when the
		// component itself is currently running. I.e. it cannot be ensured easily
		// that an execution task is enqueued and the component has terminated
		// meanwhile.
		boolean	ret;
		if(!IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
		{
			if(exception!=null)
				return false;	// Component already failed: tell executor not to call again. (can happen during failed init)
	
			// Remember execution thread.
			this.componentthread	= Thread.currentThread();
			IComponentIdentifier.LOCAL.set(getComponentIdentifier());
			
			ClassLoader	cl	= componentthread.getContextClassLoader();
			componentthread.setContextClassLoader(component.getClassLoader());
	
//			getCMS().addResultListener(new DefaultResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
//					((ComponentManagementService)result).setProcessingState(cid, IComponentDescription.PROCESSINGSTATE_RUNNING);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					// CMS may be null during platform init
//					if(!(exception instanceof ServiceNotFoundException))
//						super.exceptionOccurred(exception);
//				}
//			});
			
			// Copy actions from external threads into the state.
			// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
			boolean	extexecuted	= executeExternalEntries(false);
				
			// Suspend when breakpoint is triggered.
			// Necessary because component wakeup could be called anytime even if is at breakpoint..
			boolean	breakpoint_triggered	= false;
			if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
			{
				if(component.isAtBreakpoint(desc.getBreakpoints()))
				{
					breakpoint_triggered	= true;
					getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>(logger)
					{
						public void resultAvailable(IComponentManagementService cms)
						{
							cms.suspendComponent(desc.getName());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(!(exception instanceof ComponentTerminatedException))
							{
								super.exceptionOccurred(exception);
							}
						}
					});
				}
			}
			
			if(!breakpoint_triggered && !extexecuted && (!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()) || dostep))
			{
				try
				{
//					System.out.println("Executing: "+getComponentIdentifier());
					again	= component.executeStep();
				}
				catch(Exception e)
				{
					fatalError(e);
				}
				if(dostep)
				{
					dostep	= false;
					if(stepfuture!=null)
					{
						stepfuture.setResult(desc);
					}
				}
				
				// Suspend when breakpoint is triggered.
				if(!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
				{
					if(component.isAtBreakpoint(desc.getBreakpoints()))
					{
						breakpoint_triggered	= true;
						getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>(logger)
						{
							public void resultAvailable(IComponentManagementService cms)
							{
								cms.suspendComponent(desc.getName());
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(!(exception instanceof ComponentTerminatedException))
								{
									super.exceptionOccurred(exception);
								}
							}
						});
					}
				}
			}
			
//			final boolean	ready	= again && !breakpoint_triggered || extexecuted || wokenup;
//			getCMS().addResultListener(new DefaultResultListener()
//			{
//				public void resultAvailable(Object result)
//				{
//					((ComponentManagementService)result).setProcessingState(cid, ready
//						? IComponentDescription.PROCESSINGSTATE_READY : IComponentDescription.PROCESSINGSTATE_IDLE);
//				}
//			});

			// Reset execution thread.
			IComponentIdentifier.LOCAL.set(null);
			componentthread.setContextClassLoader(cl);
			this.componentthread = null;		

			ret	= (again && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState())) || extexecuted;
		}
		else
		{
			ret	= false;
		}
		
//		System.out.println("end: "+getComponentIdentifier()+", "+ret);
		executing	= false;
//		synchronized(AsyncExecutionService.DEBUG)
//		{
//			AsyncExecutionService.DEBUG.put(this, "adapter execute() finished");
//		}
		
//		if(getComponentIdentifier().getLocalName().indexOf("Alex")!=-1)
//			System.out.println("exiting exe: "+getComponentIdentifier());
		
		return ret;
	}

	/**
	 *  Execute external entries.
	 */
	protected boolean executeExternalEntries(boolean platform)
	{
		// Copy actions from external threads into the state.
		// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
		boolean	extexecuted	= false;
		
		Runnable[]	entries	= null;
		synchronized(this)
		{
			if(ext_entries!=null && !(ext_entries.isEmpty()))
			{
				entries	= (Runnable[])ext_entries.toArray(new Runnable[ext_entries.size()]);
				ext_entries.clear();
				
				extexecuted	= true;
			}
		}
		for(int i=0; entries!=null && i<entries.length; i++)
		{
			if(entries[i] instanceof CheckedAction)
			{
				if(((CheckedAction)entries[i]).isValid())
				{
					try
					{
						entries[i].run();
					}
					catch(Exception e)
					{
//						if(!platform)
							fatalError(e);
					}
				}
				try
				{
					((CheckedAction)entries[i]).cleanup();
				}
				catch(Exception e)
				{
					fatalError(e);
				}
			}
			else //if(entries[i] instanceof Runnable)
			{
				try
				{
//					if(entries[i].toString().indexOf("calc")!=-1)
//					{
//						System.out.println("scheduleStep: "+getComponentIdentifier());
//					}
//					if(platform)
//						System.out.println(entries[i]+" "+entries[i].getClass());
					entries[i].run();
				}
				catch(Exception e)
				{
//					e.printStackTrace();
//					if(!platform)
						fatalError(e);
				}
			}
		}
		
		return extexecuted;
	}
	
	/**
	 * 	Called when an error occurs during component execution.
	 *  @param e	The error.
	 */
	protected void fatalError(final Exception e)
	{
		getLogger().info("fatal error: "+getComponentIdentifier()+e.getMessage());
		if(getComponentIdentifier().getParent()==null)
		{
			System.err.println("fatal error: "+getComponentIdentifier());
			e.printStackTrace();
		}
		
		// Fatal error!
		exception = e;
		
		if(killfuture!=null)
		{
			// Already in termination.
			killfuture.setException(exception);
		}
		else
		{
			// Remove component from platform.
			getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>(logger)
			{
				public void resultAvailable(IComponentManagementService cms)
				{
//					cms.setComponentException(cid, e);
//					System.err.println("fatal error -> destroy: "+getComponentIdentifier());
//					e.printStackTrace();
					cms.destroyComponent(desc.getName());
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(!(exception instanceof ComponentTerminatedException))
					{
						super.exceptionOccurred(exception);
					}
				}
			});
		}
	}
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		boolean ret = Thread.currentThread()!=componentthread;
		if(ret)
			ret = getComponentInstance().isExternalThread();
		return ret;
	}
	
	//-------- external access --------
	
	/**
	 *  Execute an action on the component thread.
	 *  May be safely called from any (internal or external) thread.
	 *  The contract of this method is as follows:
	 *  The component adapter ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action to be executed on the component thread.
	 */
	public void invokeLater(Runnable action)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || exception!=null)
			throw new ComponentTerminatedException(desc.getName());

		synchronized(this)
		{
//			System.out.println("Adding to ext entries: "+cid);
			if(ext_forbidden)
			{
				throw new ComponentTerminatedException(desc.getName());
//				{
//					public void printStackTrace()
//					{
//						Thread.dumpStack();
//					}
//				};
			}
			else
			{
				if(ext_entries==null)
					ext_entries	= new ArrayList();
				ext_entries.add(action);
			}
		}
		wakeup();
	}
	
	//-------- test methods --------
	
	/**
	 *  Make kernel component available.
	 */
	public IComponentInstance	getComponentInstance()
	{
		return component;
	}

	//-------- step handling --------
	
	/**
	 *  Set the step mode.
	 */
	public IFuture doStep()
	{
		Future ret = new Future();
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || exception!=null)
			ret.setException(new ComponentTerminatedException(desc.getName()));
		else if(dostep)
			ret.setException(new RuntimeException("Only one step allowed at a time."));
			
		this.dostep	= true;		
		this.stepfuture = ret;
		
		wakeup();
		
		return ret;
	}
	
	class LastStep implements Runnable
	{
		public void run()
		{
			clock	= null;
			cms	= null;
//			component	= null;	// Required by getResults()
			model	= null;
//			desc	= null;	// Required by toString()
			parent	= null;
			killfuture.setResult(null);
			
//			System.out.println("Checking ext entries after cleanup: "+cid);
			assert ext_entries==null || ext_entries.isEmpty() : "Ext entries after cleanup: "+desc.getName()+", "+ext_entries;

		}
	}
	
	/**
	 *  Wake up this component.
	 */
	protected abstract void	doWakeup();
}
