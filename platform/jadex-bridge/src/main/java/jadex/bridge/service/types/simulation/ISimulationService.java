package jadex.bridge.service.types.simulation;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.annotation.GuiClassNames;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.IChangeListener;
import jadex.commons.future.IFuture;

/**
 *  Interface for the time simulation service.
 */
@GuiClassNames({
	@GuiClassName("jadex.tools.simcenter.SimServiceViewerPanel"),
	@GuiClassName("jadex.android.controlcenter.settings.SimulationSettings")
})
@Service(system=true)
public interface ISimulationService
{
	//-------- constants --------
	
	/** The execution mode normal. */
	public static String MODE_NORMAL = "mode_normal";
	
	/** The execution mode time step. */
	public static String MODE_TIME_STEP = "mode_time_step";
	
	/** The execution mode action step. */
	public static String MODE_ACTION_STEP = "mode_action_step";
	
	//-------- methods --------
	
	/**
	 *  Pause the execution (can be resumed via start or step).
	 */
	public IFuture<Void> pause();
	
	/**
	 *  Restart the execution after pause.
	 */
	public IFuture<Void> start();
	
	/**
	 *  Perform one event.
	 */
	public IFuture<Void> stepEvent();
	
	/**
	 *  Perform all actions belonging to one time point.
	 */
	public IFuture<Void> stepTime();
	
	/**
	 *  Set the clock type.
	 *  @param type The clock type.
	 */
	public IFuture<Void> setClockType(String type);
	
	/**
	 *  Get the execution mode.
	 *  @return The mode.
	 */
	public IFuture<String> getMode();
	
	/**
	 *  Test if context is executing.
	 */
	public IFuture<Boolean> isExecuting();
	
	// todo: remove the following methods
	
	/**
	 *  Get the platform clock.
	 *  @return The clock.
	 */
//	@Excluded
	public IClockService getClockService();
	
	/**
	 *  Get the executor service.
	 *  @return The executor service.
	 */
//	@Excluded
	public IExecutionService getExecutorService();
	
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
