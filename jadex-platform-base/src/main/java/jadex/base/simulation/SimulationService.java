package jadex.base.simulation;

import jadex.commons.ChangeEvent;
import jadex.commons.Future;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;
import jadex.service.clock.ClockService;
import jadex.service.clock.IClock;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimer;
import jadex.service.execution.IExecutionService;

import java.util.List;

/**
 *  The execution control is the access point for controlling the
 *  execution of one application. It provides basic features for
 *  starting, stopping and stepwise execution.
 */
public class SimulationService implements ISimulationService
{		
	//-------- attributes --------

	/** The platform. */
	protected IServiceContainer container;
	
	/** The execution mode. */
	protected String mode;
	
	/** The running flag. */
	protected boolean running;
	
	/** The executing flag. */
	protected boolean executing;
	
	/** The listeners. */
	protected List listeners;

	/** The current time step time. */
	protected long timesteptime;
	
	/** The simcommand for a simulation clock. */
	protected ICommand simcommand;
		
	/** The clock service. */
	protected IClockService clockservice;
	
	/** The execution service. */
	protected IExecutionService exeservice;

	
	//-------- constructors --------

	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IServiceContainer container)
	{
		this.container = container;
//		this.mode = mode;
		this.mode = MODE_NORMAL;
		this.listeners = SCollection.createArrayList();
		
		this.simcommand = new ICommand()
		{
			public void execute(Object args)
			{				
				IClockService simclock = getClockService();
				
				boolean steptime = false;
				boolean advanceevent = false;
				synchronized(SimulationService.this)
				{
					if(MODE_NORMAL.equals(getMode()) && executing)
					{
						advanceevent = true;
					}
					if(MODE_TIME_STEP.equals(getMode()))
					{
						//System.out.println("Do one step: "+timesteptime);
						ITimer t = simclock.getNextTimer();
						if(t!=null && t.getNotificationTime()<=timesteptime)
							//stepTime();
							steptime = true;
						else
							setExecuting(false);
					}
					else if(MODE_ACTION_STEP.equals(getMode()))
					{
						setExecuting(false);
					}
				}
				
				if(steptime)
					stepTime();
				else if(advanceevent)
					simclock.advanceEvent();
			}	
		};
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public IFuture	shutdownService()
	{
		pause();
		return new Future(null);	// Already done.
	}
	
	//-------- methods --------
	
	/**
	 *  Start (and run) the execution. 
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		final boolean[]	services	= new boolean[2];

		SServiceProvider.getService(container, IExecutionService.class).addResultListener(new DefaultResultListener()
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
					ret.setResult(null);
			}
		});
				
		SServiceProvider.getService(container, IClockService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				clockservice = (IClockService)result;
				String type = clockservice.getClockType();
		
				if(IClock.TYPE_EVENT_DRIVEN.equals(type)
					|| IClock.TYPE_TIME_DRIVEN.equals(type))
				{
					getExecutorService().addIdleCommand(simcommand);
				}
	
				boolean dorun = false;
				synchronized(this)
				{
					setMode(MODE_NORMAL);
					setExecuting(true);
					if(!running)
					{
						dorun = true;
						running = true;
					}
				}
				
				if(dorun)
					getClockService().start();
					
				getExecutorService().startService();

				boolean	setresult;
				synchronized(services)
				{
					services[1]	= true;
					setresult	= services[0] && services[1];
				}
				if(setresult)
					ret.setResult(null);
			}
		});

		return ret;
	}
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public void pause()
	{
		boolean dostop = false;
		synchronized(this)
		{
			if(running)
			{
	//			executor.stop();
				dostop = true;
				running = false;
			}
			setExecuting(false);
		}
		
		if(dostop)
			getClockService().stop();
	}
	
	/**
	 *  Perform one event.
	 */
	public void stepEvent()
	{
		boolean dorun = false;
		synchronized(this)
		{
			if(executing)
				return;
//				throw new RuntimeException("Step only possible when executing.");

			if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
			{
				throw new RuntimeException("Step only possible in simulation mode.");
			}
	
			setMode(MODE_ACTION_STEP);
			setExecuting(true);
			if(!running)
			{
				dorun = true;
				running = true;
			}
		}
		
		if(dorun)
		{
			getClockService().start();
		}
		
		boolean advanced = getClockService().advanceEvent();
		
		synchronized(this)
		{
			// Have to make sure that executing is set back to false,
			// even if there is no time point or timing entry does not cause any execution.
			if(!advanced || getExecutorService().isIdle())
			{
				//System.out.println("No further timepoint.");
				setExecuting(false);
			}
		}
	}
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public void stepTime()
	{
		boolean dorun = false;
		synchronized(this)
		{
			if(executing)
				return;
//				throw new RuntimeException("Step only possible when not executing.");
			
			if(IClock.TYPE_CONTINUOUS.equals(getClockService().getClockType())
				|| IClock.TYPE_SYSTEM.equals(getClockService().getClockType()))
			{
				throw new RuntimeException("Step only possible in simulation mode.");
			}
			
			//System.out.println(simclock.getTimers().length+" "+jadex.commons.SUtil.arrayToString(simclock.getTimers()));
				
			setMode(MODE_TIME_STEP);
			setExecuting(true);
			if(!running)
			{
				dorun = true;
				running = true;
			}
		}

		if(dorun)
		{
			getClockService().start();
		}
		
		// Do not hold lock while clock is advanced to avoid deadlocks
		boolean advanced = getClockService().advanceEvent();
		
		synchronized(this)
		{
			if(!advanced || getExecutorService().isIdle())
			{
				setExecuting(false);
			}
			else
			{
				timesteptime = getClockService().getTime();
				//System.out.println("Steptime is: "+timesteptime);
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
			return;
//			throw new RuntimeException("Change clock not allowed during execution.");
		
		
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
			
			running = false;
			notifyListeners(new ChangeEvent(this, "clock_type", type));
		}
	}
		
	/**
	 *  Test if context is running.
	 */
	public boolean isRunning()
	{
		return running;
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
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Set the executing state.
	 */
	public void setExecuting(boolean executing)
	{
		if(executing!=this.executing)
		{
//			System.out.println("executing: "+executing);
			this.executing = executing;
			notifyListeners(new ChangeEvent(this, "executing", executing? Boolean.TRUE: Boolean.FALSE));
		}
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent ce)
	{
		for(int i=0; i<listeners.size(); i++)
			((IChangeListener)listeners.get(i)).changeOccurred(ce);
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
