package jadex.adapter.standalone;

import jadex.adapter.base.ISimulationService;
import jadex.adapter.base.clock.ContinuousClock;
import jadex.adapter.base.clock.IClock;
import jadex.adapter.base.clock.ISimulationClock;
import jadex.adapter.base.clock.SimulationEventClock;
import jadex.adapter.base.clock.SimulationTickClock;
import jadex.adapter.base.clock.SystemClock;
import jadex.adapter.base.execution.IExecutionService;
import jadex.bridge.IClockService;
import jadex.bridge.ITimer;
import jadex.commons.ICommand;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.ThreadPoolFactory;

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
	protected AbstractPlatform platform;
	
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
	public SimulationService(AbstractPlatform platform)
	{
		this.platform = platform;
//		this.mode = mode;
		this.mode = MODE_NORMAL;
		this.listeners = SCollection.createArrayList();
		
		this.simcommand = new ICommand()
		{
			public void execute(Object args)
			{
				synchronized(SimulationService.this)
				{
					ISimulationClock simclock = (ISimulationClock)getClock();
					if(MODE_NORMAL.equals(getMode()) && executing)
					{
						if(!simclock.advanceEvent())
							setExecuting(false);
					}
					else if(MODE_TIME_STEP.equals(getMode()))
					{
						//System.out.println("Do one step: "+timesteptime);
						ITimer t = simclock.getNextTimer();
						if(t!=null && t.getNotificationTime()<=timesteptime)
							stepTime();
						else
							setExecuting(false);
					}
					else if(MODE_ACTION_STEP.equals(getMode()))
					{
						setExecuting(false);
					}
				}
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
	public synchronized void start()
	{
		setMode(MODE_NORMAL);
		setExecuting(true);
		if(!running)
		{
			getClock().start();
			running = true;
		}
		getExecutorService().start();
	}
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public synchronized void pause()
	{
		if(running)
		{
			getClock().stop();
//			executor.stop();
			running = false;
		}
		setExecuting(false);
	}
	
	/**
	 *  Perform one event.
	 */
	public synchronized void stepEvent()
	{
		if(!(getClock() instanceof ISimulationClock))
			throw new RuntimeException("Step only possible in simulation mode.");
		if(executing)
			throw new RuntimeException("Step only possible when executing.");

		setMode(MODE_ACTION_STEP);
		setExecuting(true);
		if(!running)
		{
			getClock().start();
			getExecutorService().start();
			running = true;
		}

		boolean advanced = ((ISimulationClock)getClock()).advanceEvent();
		
		// Have to make sure that executing is set back to false,
		// even if there is no time point or timing entry does not cause any execution.
		if(!advanced || getExecutorService().isIdle())
		{
			//System.out.println("No further timepoint.");
			setExecuting(false);
		}
	}
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public synchronized void stepTime()
	{
		if(!(getClock() instanceof ISimulationClock))
			throw new RuntimeException("Step only possible in simulation mode.");
		if(executing)
			throw new RuntimeException("Step only possible when not executing.");

		//System.out.println(simclock.getTimers().length+" "+jadex.commons.SUtil.arrayToString(simclock.getTimers()));
			
		setMode(MODE_TIME_STEP);
		setExecuting(true);
		if(!running)
		{
			getClock().start();
			getExecutorService().start();
			running = true;
		}

		boolean advanced = ((ISimulationClock)getClock()).advanceEvent();
		if(!advanced || getExecutorService().isIdle())
		{
			setExecuting(false);
		}
		else
		{
			timesteptime = getClock().getTime();
			//System.out.println("Steptime is: "+timesteptime);
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
		IClock oldclock	= (IClock)getClock();

		IClock clock = null;
		if(IClock.TYPE_CONTINUOUS.equals(type))
			clock = new ContinuousClock(oldclock, ThreadPoolFactory.getThreadPool(platform.getName()));
		else if(IClock.TYPE_SYSTEM.equals(type))
			clock = new SystemClock(oldclock, ThreadPoolFactory.getThreadPool(platform.getName()));
		else if(IClock.TYPE_TIME_DRIVEN.equals(type))
			clock = new SimulationTickClock(oldclock);
		else if(IClock.TYPE_EVENT_DRIVEN.equals(type))
			clock = new SimulationEventClock(oldclock);
		else
			throw new RuntimeException("Unknown clock type: "+type);
		
		if(isExecuting())
			throw new RuntimeException("Change clock not allowed during execution.");
		
		//System.out.println("Exchanged clock!!! "+clock);
		if(getClock() instanceof ISimulationClock)
			getExecutorService().removeIdleCommand(simcommand);
		
		platform.removeService(IClockService.class, null);
		platform.addService(IClockService.class, "clock_service", (IClockService)clock);
		
		if(clock instanceof ISimulationClock)
			getExecutorService().addIdleCommand(simcommand);
		
		running = false;
		
		if(oldclock!=null)
			oldclock.dispose();
		notifyListeners();
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
		//System.out.println("executing: "+executing);
		this.executing = executing;
		notifyListeners();
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners()
	{
		ChangeEvent ce = new ChangeEvent(this);
		for(int i=0; i<listeners.size(); i++)
			((ChangeListener)listeners.get(i)).stateChanged(ce);
	}
	
	/**
	 *  Get the platform clock.
	 *  @return The clock.
	 */
	protected IClock getClock()
	{
		return (IClock)platform.getService(IClockService.class);
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
