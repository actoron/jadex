package jadex.adapter.base;

import jadex.adapter.base.clock.ClockService;
import jadex.adapter.base.clock.ExtendedChangeEvent;
import jadex.adapter.base.execution.IExecutionService;
import jadex.bridge.IClock;
import jadex.bridge.IClockService;
import jadex.bridge.IPlatform;
import jadex.bridge.ITimer;
import jadex.commons.ICommand;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;

import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  The execution control is the access point for controlling the
 *  execution of one application. It provides basic features for
 *  starting, stopping and stepwise execution.
 */
public class SimulationService implements ISimulationService
{		
	//-------- attributes --------

	/** The platform. */
	protected IPlatform platform;
	
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
		
	//-------- constructors --------

	/**
	 *  Create a new execution control.
	 */
	public SimulationService(IPlatform platform)
	{
		this.platform = platform;
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
	public void shutdown(IResultListener listener)
	{
		pause();
		if(listener!=null)
			listener.resultAvailable(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Start (and run) the execution. 
	 */
	public void start()
	{
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
			
		getExecutorService().start();
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
//			getExecutorService().start();
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
//			getExecutorService().start();
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
	public void setClockType(String type)
	{
		if(isExecuting())
			return;
//			throw new RuntimeException("Change clock not allowed during execution.");
		
		
		IClockService cs = (IClockService)platform.getService(IClockService.class);
		String oldtype = cs.getClockType();
		
		if(!type.equals(oldtype))
		{
			//System.out.println("Exchanged clock!!! "+clock);
			if(IClock.TYPE_EVENT_DRIVEN.equals(oldtype)
				|| IClock.TYPE_TIME_DRIVEN.equals(oldtype))
			{
				getExecutorService().removeIdleCommand(simcommand);
			}
			
			((ClockService)cs).setClock(type);
			
			if(IClock.TYPE_EVENT_DRIVEN.equals(type)
				|| IClock.TYPE_TIME_DRIVEN.equals(type))
			{
				getExecutorService().addIdleCommand(simcommand);
			}
			
			running = false;
			notifyListeners(new ExtendedChangeEvent(this, "clock_type", type));
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
	public void addChangeListener(ChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(ChangeListener listener)
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
			System.out.println("executing: "+executing);
			this.executing = executing;
			notifyListeners(new ExtendedChangeEvent(this, "executing", executing? Boolean.TRUE: Boolean.FALSE));
		}
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent ce)
	{
		for(int i=0; i<listeners.size(); i++)
			((ChangeListener)listeners.get(i)).stateChanged(ce);
	}
	
	/**
	 *  Get the platform clock.
	 *  @return The clock.
	 */
	protected IClockService getClockService()
	{
		return (IClockService)platform.getService(IClockService.class);
	}
	
	/**
	 *  Get the executor service.
	 *  @return The executor service.
	 */
	public IExecutionService getExecutorService()
	{
		return (IExecutionService)platform.getService(IExecutionService.class);
	}
}
