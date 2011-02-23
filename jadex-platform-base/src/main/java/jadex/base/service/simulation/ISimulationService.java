package jadex.base.service.simulation;

import jadex.commons.IChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;
import jadex.commons.service.annotation.Excluded;

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
	public IFuture pause();
	
	/**
	 *  Restart the execution after pause.
	 */
	public IFuture start();
	
	/**
	 *  Perform one event.
	 */
	public IFuture stepEvent();
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public IFuture stepTime();
	
	/**
	 *  Set the clock type.
	 *  @param type The clock type.
	 */
	// todo: remove thread pool
	public IFuture setClockType(String type);
	
	/**
	 *  Get the execution mode.
	 *  @return The mode.
	 */
	public IFuture getMode();
	
	/**
	 *  Test if context is executing.
	 */
	public IFuture isExecuting();
	
	
	// todo: hack remove method?!
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	@Excluded
	public void addChangeListener(IChangeListener listener);
	
	// todo: hack remove method?!
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	@Excluded
	public void removeChangeListener(IChangeListener listener);

}
