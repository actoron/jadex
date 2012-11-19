package jadex.bdi.runtime;

import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;


/**
 *  A currently instantiated plan of the agent (=intention).
 */
public interface IPlan	extends IParameterElement
{
	//-------- constants --------
	
	/** The lifecycle state "new" (just created). */
	public static final String	PLANLIFECYCLESTATE_NEW	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_NEW;
	
	/** The state, indicating the execution of the plan body. */
	public static final String	PLANLIFECYCLESTATE_BODY	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY;
	
	/** The state, indicating the execution of the passed code. */
	public static final String	PLANLIFECYCLESTATE_PASSED	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED;
	
	/** The state, indicating the execution of the failed code. */
	public static final String	PLANLIFECYCLESTATE_FAILED	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED;
	
	/** The state, indicating the execution of the aborted. */
	public static final String	PLANLIFECYCLESTATE_ABORTED	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED;

	//-------- methods --------
	
	/**
	 *  Get the lifecycle state of the plan (e.g. body or aborted).
	 *  @return The lifecycle state.
	 */
	public String	getLifecycleState();

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue();

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody();
	
	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public IElement getReason();
	
	/**
	 *  Abort a running plan. 
	 */
	public void abortPlan();
	
	/**
	 *  Start plan processing.
	 */
	public void startPlan();
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public void addPlanListener(IPlanListener listener);
	
	/**
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public void removePlanListener(IPlanListener listener);
}