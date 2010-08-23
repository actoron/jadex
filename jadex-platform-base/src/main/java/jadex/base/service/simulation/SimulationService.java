package jadex.base.service.simulation;

import jadex.commons.ChangeEvent;
import jadex.commons.Future;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.ClockService;
import jadex.commons.service.clock.IClock;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.clock.ITimer;
import jadex.commons.service.execution.IExecutionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  The execution control is the access point for controlling the
 *  execution of one application. It provides basic features for
 *  starting, stopping and stepwise execution.
 */
public class SimulationService extends BasicService implements ISimulationService
{		
	//-------- attributes --------

	/** The platform. */
	protected IServiceProvider provider;
	
	/** The execution mode. */
	protected String mode;
	
	/** The executing flag. */
	protected boolean executing;
	
	/** The listeners. */
	protected List listeners;

	/** The simcommand for a simulation clock. */
	protected ICommand simcommand;
		
	/** The time of a time step. */
	protected long timesteptime;
		
	/** The clock service. */
	protected IClockService clockservice;
	
	/** The execution service. */
	protected IExecutionService exeservice;
	
	/** The outstanding listener notifications (change events). */
	protected List	notifications;
	
	/** Change indicator to coordinate separate threads. */
	protected int change;
	
	//-------- constructors --------

	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IServiceProvider provider)
	{
		this(provider, null);
	}
	
	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), ISimulationService.class, properties);

		this.provider = provider;
//		this.mode = mode;
		this.mode = MODE_NORMAL;
		this.listeners = SCollection.createArrayList();
		
		this.simcommand = new ICommand()
		{
			public void execute(Object args)
			{				
				IClockService simclock = getClockService();
				
				int	choice	= 0;
				final int	advance	= 1;
				final int	step_time	= 2;
				synchronized(SimulationService.this)
				{
					if(isExecuting() && (MODE_TIME_STEP.equals(getMode()) || MODE_ACTION_STEP.equals(getMode())))
					{
						setExecuting(false);						
					}

					if(MODE_NORMAL.equals(getMode()) && executing)
					{
						choice	= advance;
					}
					else if(MODE_TIME_STEP.equals(getMode()))
					{
						ITimer t = simclock.getNextTimer();
						if(t!=null && t.getNotificationTime()<=timesteptime)
						{
//							System.out.println("continuing time step: "+timesteptime);
							choice	= step_time;
						}
//						else
//						{
//							System.out.println("time step finished");
//						}
					}
				}
				
				notifyListeners();	// delayed notifications of setExecuting()
				
				// Perform action out of synchronized block.
				switch(choice)
				{
					case advance:
						simclock.advanceEvent();
						break;
					case step_time:
						stepTime();
						break;
					default:
				}
			}	
		};
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public IFuture	shutdownService()
	{
		IFuture ret = super.shutdownService();
		pause();
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Start (and run) the execution. 
	 */
	public synchronized IFuture	startService()
	{
		final Future	ret	= new Future();
		
		super.startService().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final boolean[]	services	= new boolean[2];

				SServiceProvider.getService(provider, IExecutionService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						exeservice = (IExecutionService)result;
						boolean	setresult;
						synchronized(services)
						{
							services[0]	= true;
							setresult	= services[0] && services[1];
						}
						if(setresult)
						{
							init();
							ret.setResult(SimulationService.this);
						}
					}
				});
						
				SServiceProvider.getService(provider, IClockService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						clockservice = (IClockService)result;

						boolean	setresult;
						synchronized(services)
						{
							services[1]	= true;
							setresult	= services[0] && services[1];
						}
						if(setresult)
						{
							init();
							ret.setResult(SimulationService.this);
						}
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});

		return ret;
	}
	
	/**
	 *  Called after all required services have been found.
	 */
	protected void	init()
	{
		String type = getClockService().getClockType();
		
		if(IClock.TYPE_EVENT_DRIVEN.equals(type)
			|| IClock.TYPE_TIME_DRIVEN.equals(type))
		{
			getExecutorService().addIdleCommand(simcommand);
		}
		
		start();		
	}
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public void pause()
	{
		boolean	dostop	= false;
		synchronized(this)
		{
			if(isExecuting())
			{
				dostop	= true;
				setExecuting(false);
			}
		}
		notifyListeners();	// delayed notifications of setExecuting()
		if(dostop)
			getClockService().stop();
	}
	
	/**
	 *  Restart the execution after pause.
	 */
	public void start()
	{
		boolean	dostart	= false;
		String	type	= null;
		synchronized(this)
		{
			if(!isExecuting())
			{
				type = getClockService().getClockType();
				setMode(MODE_NORMAL);
				setExecuting(true);
				dostart	= true;
			}
		}
		notifyListeners();	// delayed notifications of setExecuting()
		
		if(dostart)
		{
			getClockService().start();
			
			if(IClock.TYPE_EVENT_DRIVEN.equals(type)
				|| IClock.TYPE_TIME_DRIVEN.equals(type))
			{
				getClockService().advanceEvent();
			}
		}
	}
	
	/**
	 *  Perform one event.
	 */
	public void stepEvent()
	{
		boolean	dostart	= false;
		int	oldchange;
		synchronized(this)
		{
			if(!isExecuting())
			{
				if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
					|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
				{
					throw new RuntimeException("Step only possible in simulation mode.");
				}
				setMode(MODE_ACTION_STEP);
				setExecuting(true);
				dostart	= true;
			}
			oldchange	= change;
		}
		notifyListeners();	// delayed notifications of setExecuting()

		if(dostart)
		{
			getClockService().start();
			boolean advanced = getClockService().advanceEvent();
			synchronized(this)
			{
				// Have to make sure that executing is set back to false,
				// even if there is no time point or timing entry does not cause any execution.
				if(oldchange==change && (!advanced || getExecutorService().isIdle()))
				{
					//System.out.println("No further timepoint.");
					setExecuting(false);
				}
			}
			notifyListeners();	// delayed notifications of setExecuting()
		}
	}
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public void stepTime()
	{
		boolean	dostart	= false;
		int	oldchange;
		synchronized(this)
		{
			if(!isExecuting())
			{
				if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
					|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
				{
					throw new RuntimeException("Step only possible in simulation mode.");
				}
				setMode(MODE_TIME_STEP);
				setExecuting(true);
				dostart	= true;
			}
			oldchange	= change;
		}
		notifyListeners();	// delayed notifications of setExecuting()

		if(dostart)
		{
			ITimer	next	= getClockService().getNextTimer();
			if(next!=null)
			{
				timesteptime	= next.getNotificationTime();
//				System.out.println("time step: "+timesteptime);
				getClockService().start();
				boolean advanced = getClockService().advanceEvent();
				synchronized(this)
				{
					// Have to make sure that executing is set back to false,
					// even if there is no time point or timing entry does not cause any execution.
					if(oldchange==change && (!advanced || getExecutorService().isIdle()))
					{
//						System.out.println("No further timepoint.");
						setExecuting(false);
					}
				}
				notifyListeners();	// delayed notifications of setExecuting()
			}
		}
	}
	
	/**
	 *  Get the execution mode.
	 *  @return The mode.
	 */
	public String getMode()
	{
		return mode;
	}
	
	/**
	 *  Get the execution mode.
	 *  @param mode The mode.
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 *  Set the clock.
	 *  @param clock The new clock.
	 */
	public void setClockType(String type, IThreadPool tp)
	{
		if(isExecuting())
			throw new RuntimeException("Change clock not allowed during execution.");
		
		
//		IClockService cs = (IClockService)container.getService(IClockService.class);
		String oldtype = clockservice.getClockType();
		
		if(!type.equals(oldtype))
		{
			//System.out.println("Exchanged clock!!! "+clock);
			if(IClock.TYPE_EVENT_DRIVEN.equals(oldtype)
				|| IClock.TYPE_TIME_DRIVEN.equals(oldtype))
			{
				getExecutorService().removeIdleCommand(simcommand);
			}
			
			((ClockService)clockservice).setClock(type, tp);
			
			if(IClock.TYPE_EVENT_DRIVEN.equals(type)
				|| IClock.TYPE_TIME_DRIVEN.equals(type))
			{
				getExecutorService().addIdleCommand(simcommand);
			}
			
			if(notifications==null)
				notifications	= new ArrayList();
			notifications.add(new ChangeEvent(this, "clock_type", type));
			notifyListeners();
		}
	}
		
	/**
	 *  Test if context is executing.
	 */
	public boolean isExecuting()
	{
		return executing;
	}
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener)
	{
		synchronized(this)
		{
			listeners.add(listener);
		}
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		synchronized(this)
		{
			listeners.remove(listener);
		}
	}
	
	/**
	 *  Set the executing state.
	 */
	public void setExecuting(boolean executing)
	{
		synchronized(this)
		{
			if(executing!=this.executing)
			{
				change++;
				this.executing = executing;
				if(notifications==null)
					notifications	= new ArrayList();
				notifications.add(new ChangeEvent(this, "executing", executing? Boolean.TRUE: Boolean.FALSE));
			}
			else
			{
				Thread.dumpStack();
			}
		}
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners()
	{
		ChangeEvent[]	events;
		IChangeListener[]	alisteners;
		synchronized(this)
		{
			events	= notifications!=null ? (ChangeEvent[])notifications.toArray(new ChangeEvent[notifications.size()]) : null;
			alisteners	= listeners.isEmpty() ? null : (IChangeListener[])listeners.toArray(new IChangeListener[listeners.size()]);
			notifications	= null;
		}
		if(alisteners!=null && events!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				for(int j=0; j<events.length; j++)
				{
					alisteners[i].changeOccurred(events[j]);
				}
			}
		}
	}
	
	/**
	 *  Get the platform clock.
	 *  @return The clock.
	 */
	protected IClockService getClockService()
	{
		return clockservice;
//		return (IClockService)container.getService(IClockService.class);
	}
	
	/**
	 *  Get the executor service.
	 *  @return The executor service.
	 */
	public IExecutionService getExecutorService()
	{
		return exeservice;
//		return (IExecutionService)container.getService(IExecutionService.class);
	}
}
