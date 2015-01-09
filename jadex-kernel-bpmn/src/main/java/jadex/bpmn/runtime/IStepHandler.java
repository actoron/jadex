package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bridge.IInternalAccess;

/**
 *  Handles the transition of steps.
 */
public interface IStepHandler
{
	//-------- constants --------
	
	/** The step handler identifier. */
	public static final String  STEP_HANDLER = "step_handler";

	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, IInternalAccess instance, ProcessThread thread, Object event);
}
