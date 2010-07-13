package jadex.base.simulation;

import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.IService;

/**
 *  Interface for the time simulation service.
 */
public interface ISimulationService	extends IService
{
	//-------- constants --------
	
	/** The execution mode normal. */
	public static String MODE_NORMAL = "mode_normal";
	
	/** The execution mode time step. */
	public static String MODE_TIME_STEP = "mode_time_step";
	
	/** The execution mode time step. */
	public static String MODE_ACTION_STEP = "mode_action_step";
	
	//-------- methods --------
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public void pause();
	
	/**
	 *  Perform one event.
	 */
	public void stepEvent();
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public void stepTime();
	
	/**
	 *  Set the clock type.
	 *  @param type The clock type.
	 */
	// todo: remove thread pool
	public void setClockType(String type, IThreadPool tp);
	
	/**
	 *  Get the execution mode.
	 *  @return The mode.
	 */
	public String getMode();
	
	/**
	 *  Test if context is executing.
	 */
	public boolean isExecuting();
	
	
	// todo: hack remove method?!
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IChangeListener listener);
	
	// todo: hack remove method?!
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IChangeListener listener);

}
