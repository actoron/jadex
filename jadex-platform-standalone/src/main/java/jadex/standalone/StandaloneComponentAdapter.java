package jadex.standalone;

import jadex.bridge.CheckedAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;
import jadex.service.execution.IExecutionService;
import jadex.standalone.service.ComponentManagementService;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
 *  Component adapter for built-in standalone platform. 
 *  This platform is built for simplicity and for being
 *  able to execute Jadex components without any 3rd party
 *  execution platform.
 */
public class StandaloneComponentAdapter implements IComponentAdapter, IExecutable, Serializable
{
	//-------- attributes --------

	/** The component identifier. */
	protected IComponentIdentifier cid;

	/** The component identifier. */
	protected IExternalAccess parent;

	/** The component instance. */
	protected IComponentInstance component;
	
	/** The component model. */
	protected ILoadableComponentModel model;

	/** The description holding the execution state of the component
	   (read only! managed by component execution service). */
	protected IComponentDescription	desc;
	
	/** The component logger. */
	protected Logger logger;
	
	/** Flag to indicate a fatal error (component termination will not be passed to instance) */
	protected boolean fatalerror;
	
	//-------- steppable attributes --------
	
	/** The flag for a scheduled step (true when a step is allowed in stepwise execution). */
	protected boolean	dostep;
	
	/** The listener to be informed, when the requested step is finished. */
	protected IResultListener	steplistener;
	
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
	
	/** The own service provider. */
//	protected IServiceContainer provider;

	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public StandaloneComponentAdapter(IComponentDescription desc, ILoadableComponentModel model, IComponentInstance component, IExternalAccess parent)
	{
		this.desc = desc;
		this.cid	= desc.getName();
		this.model = model;
		this.component = component;
		this.parent	= parent;
		this.ext_entries = Collections.synchronizedList(new ArrayList());
//		this.provider = component.createServiceContainer();
	}
	
	/**
	 *  Set the component.
	 *  @param component The component to set.
	 * /
	public void setComponent(IComponentInstance component, ILoadableComponentModel model)
	{
		this.component = component;
		this.model = model;
	}*/
	
	/**
	 *  Get description.
	 */
	public IComponentDescription getDescription()
	{
		return desc;
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
		if(cid.getName().indexOf("Sokrates")!=-1)
		{
			Thread.dumpStack();
		}
		
		wokenup	= true;

		// Ignore wakeup calls, when component not yet inited (hack???).
		if(component!=null)
		{
			// todo: check this assert meaning!
			
			// Verify that the component is running.
		//		assert !IComponentDescription.STATE_INITIATED.equals(state) : this;
			
			if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
				throw new ComponentTerminatedException(cid.getName());
			
			// Set processing state to ready if not running.
			if(IComponentDescription.PROCESSINGSTATE_IDLE.equals(desc.getProcessingState()))
			{
				SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						((ComponentManagementService)result).setProcessingState(cid, IComponentDescription.PROCESSINGSTATE_READY);
					}
				});				
			}
			
			// Resume execution of the component (when active or terminating).
			if(IComponentDescription.STATE_ACTIVE.equals(desc.getState())
				/*|| IComponentDescription.STATE_TERMINATING.equals(desc.getState())*/
				|| IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))	// Hack!!! external entries must also be executed in suspended state.
			{
				//System.out.println("wakeup called: "+state);
				SServiceProvider.getService(getServiceContainer(), IExecutionService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						((IExecutionService)result).execute(StandaloneComponentAdapter.this);
					}
				});
			}
		}
	}

	/**
	 *  Return a component-identifier that allows to send
	 *  messages to this component.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		// todo: problem: loggers can cause memory leaks
		// http://bugs.sun.com/view_bug.do;jsessionid=bbdb212815ddc52fcd1384b468b?bug_id=4811930
		
		// Todo: include parent name for nested loggers.
		String name = getComponentIdentifier().getLocalName();
		logger = LogManager.getLogManager().getLogger(name);
		
		// if logger does not already exists, create it
		if(logger==null)
		{
			// Hack!!! Might throw exception in applet / webstart.
			try
			{
				logger = Logger.getLogger(name);
				initLogger(logger);
				//System.out.println(logger.getParent().getLevel());
			}
			catch(SecurityException e)
			{
				// Hack!!! For applets / webstart use anonymous logger.
				logger = Logger.getAnonymousLogger();
				initLogger(logger);
			}
		}
		
		return logger;
	}
	
	/**
	 *  Init the logger with capability settings.
	 *  @param logger The logger.
	 */
	protected void initLogger(Logger logger)
	{
		// get logging properties (from ADF)
		// the level of the logger
		// can be Integer or Level
		
		Object prop = model.getProperties().get("logging.level");
		Level level = prop==null? Level.WARNING: (Level)prop;
		logger.setLevel(level);

		// if logger should use Handlers of parent (global) logger
		// the global logger has a ConsoleHandler(Level:INFO) by default
		prop = model.getProperties().get("logging.useParentHandlers");
		if(prop!=null)
		{
			logger.setUseParentHandlers(((Boolean)prop).booleanValue());
		}
			
		// add a ConsoleHandler to the logger to print out
        // logs to the console. Set Level to given property value
		prop = model.getProperties().get("addConsoleHandler");
		if(prop!=null)
		{
            ConsoleHandler console = new ConsoleHandler();
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
		String logfile =	(String)model.getProperties().get("logging.file");
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
	}
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public ILoadableComponentModel getModel()
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
	 */
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
	}
	
	/**
	 *  String representation of the component.
	 */
	public String toString()
	{
		return "StandaloneComponentAdapter("+cid.getName()+")";
	}

	
	/**
	 *  Get the service provider.
	 */
	public IServiceContainer getServiceContainer()
	{
		return component.getServiceContainer();
	}
	
	//-------- methods called by the standalone platform --------

	/**
	 *  Gracefully terminate the component.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @param listener	When cleanup of the component is finished, the listener must be notified.
	 */
	public void killComponent(final IResultListener listener)
	{
//		System.out.println("killComponent: "+listener);
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()))
			throw new ComponentTerminatedException(cid.getName());

		if(!fatalerror)
		{
			component.cleanupComponent().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					getServiceContainer().shutdown();
					listener.resultAvailable(this, getComponentIdentifier());
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					getServiceContainer().shutdown();
					listener.resultAvailable(this, getComponentIdentifier());
				}
			});
		}
		else if(listener!=null)
		{
			listener.resultAvailable(this, getComponentIdentifier());
		}
			
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
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		// Add optional receival time.
//		String rd = type.getReceiveDateIdentifier();
//		Object recdate = message.get(rd);
//		if(recdate==null)
//			message.put(rd, new Long(getClock().getTime()));
		
		IMessageAdapter msg = new DefaultMessageAdapter(message, type);
		component.messageArrived(msg);
	}
	
	//-------- IExecutable interface --------

	/**
	 *  Executable code for running the component
	 *  in the platforms executor service.
	 */
	public boolean	execute()
	{
		wokenup	= false;
//		if(cid.getName().indexOf("Sokrates")!=-1)
//		{
//			System.err.println("Execute: "+cid.getName());
//		}
//		if(cid.getName().indexOf("platform")!=-1)
//			System.out.println("platform start");
//		if(cid.getName().indexOf("kernel_micro")!=-1)
//			System.out.println("micro start");
//		else if(cid.getName().indexOf("bdi")!=-1)
//			System.out.println("bdi start");
		
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		// Remember execution thread.
		this.componentthread	= Thread.currentThread();
		
		ClassLoader	cl	= componentthread.getContextClassLoader();
		componentthread.setContextClassLoader(model.getClassLoader());

		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null)	// may be null during platform init 
					((ComponentManagementService)result).setProcessingState(cid, IComponentDescription.PROCESSINGSTATE_RUNNING);
			}
		});
		
		// Copy actions from external threads into the state.
		// Is done in before tool check such that tools can see external actions appearing immediately (e.g. in debugger).
		boolean	extexecuted	= false;
		Runnable[]	entries	= null;
		synchronized(ext_entries)
		{
			if(!(ext_entries.isEmpty()))
			{
//				if(cid.getName().indexOf("Sokrates")!=-1)
//				{
//					System.err.println(cid.getName()+" extentries: "+ext_entries);
//				}
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
					entries[i].run();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					fatalError(e);
				}
			}
		}

		// Suspend when breakpoint is triggered.
		boolean	breakpoint_triggered	= false;
		if(!dostep && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
		{
			if(component.isAtBreakpoint(desc.getBreakpoints()))
			{
				breakpoint_triggered	= true;
				SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						((IComponentManagementService)result).suspendComponent(cid);
					}
				});
			}
		}
		
		if(!breakpoint_triggered && !extexecuted && (!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()) || dostep))
		{
			try
			{
//				System.out.println("Executing: "+component);
				again	= component.executeStep();
				if(cid.getName().indexOf("Sokrates")!=-1)
				{
					System.err.println("again: "+again);
				}
			}
			catch(Exception e)
			{
				fatalError(e);
			}
			if(dostep)
			{
				dostep	= false;
				if(steplistener!=null)
					steplistener.resultAvailable(this, desc);
			}
			
			// Suspend when breakpoint is triggered.
			if(!IComponentDescription.STATE_SUSPENDED.equals(desc.getState()))
			{
				if(component.isAtBreakpoint(desc.getBreakpoints()))
				{
					breakpoint_triggered	= true;
					SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							((IComponentManagementService)result).suspendComponent(cid);
						}
					});
				}
			}
		}
		
		final boolean	ready	= again && !breakpoint_triggered || extexecuted || wokenup;
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				((ComponentManagementService)result).setProcessingState(cid, ready
					? IComponentDescription.PROCESSINGSTATE_READY : IComponentDescription.PROCESSINGSTATE_IDLE);
			}
		});

		// Reset execution thread.
		componentthread.setContextClassLoader(cl);
		this.componentthread = null;
		
//		System.out.println("end: "+getComponentIdentifier());
		
		return (again && !IComponentDescription.STATE_SUSPENDED.equals(desc.getState())) || extexecuted;
	}

	/**
	 * 	Called when an error occurs during component execution.
	 *  @param e	The error.
	 */
	protected void fatalError(final Exception e)
	{
		// Fatal error!
		fatalerror	= true;
		
		// Remove component from platform.
		SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ComponentManagementService	cms	= (ComponentManagementService)result;
				cms.setComponentException(cid, e);
				cms.destroyComponent(cid);
			}
		});
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		return Thread.currentThread()!=componentthread;
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
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		synchronized(ext_entries)
		{
			if(ext_forbidden)
				throw new ComponentTerminatedException("External actions cannot be accepted " +
					"due to terminated component state: "+this);
			{
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
	public void	doStep(IResultListener listener)
	{
		if(IComponentDescription.STATE_TERMINATED.equals(desc.getState()) || fatalerror)
			throw new ComponentTerminatedException(cid.getName());

		if(dostep)
			listener.exceptionOccurred(this, new RuntimeException("Only one step allowed at a time."));
			
		this.dostep	= true;		
		this.steplistener	= listener;
		
		wakeup();
	}

	public void setComponentThread(Thread thread)
	{
		this.componentthread	= thread;
	}
}
