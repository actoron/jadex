package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MPlan;

/**
 * 
 */
public class RPlan extends RElement
{
	//-------- plan states --------
	
	/** The plan ready state. */
	public static final String	PLANPROCESSINGTATE_READY	= "ready";
	
	/** The plan running state. */
	public static final String	PLANPROCESSINGTATE_RUNNING	= "running";
	
	/** The plan waiting state. */
	public static final String	PLANPROCESSINGTATE_WAITING	= "waiting";
	
	/** The plan goalcleanup state (wait for subgoals being dropped
	 *  after body is exited and before passed/failed/aborted is called). */
	public static final String	PLANPROCESSINGTATE_GOALCLEANUP	= "goalcleanup";
	
	/** The plan finished state. */
	public static final String	PLANPROCESSINGTATE_FINISHED	= "finished";
	
	/** The lifecycle state "new" (just created). */
	public static final String	PLANLIFECYCLESTATE_NEW	= "new";
	
	/** The state, indicating the execution of the plan body. */
	public static final String	PLANLIFECYCLESTATE_BODY	= "body";
	
	/** The state, indicating the execution of the passed code. */
	public static final String	PLANLIFECYCLESTATE_PASSED	= "passed";
	
	/** The state, indicating the execution of the failed code. */
	public static final String	PLANLIFECYCLESTATE_FAILED	= "failed";
	
	/** The state, indicating the execution of the aborted. */
	public static final String	PLANLIFECYCLESTATE_ABORTED	= "aborted";
	
	
	/** The plan has a reason. */
	public RProcessableElement reason;

	/** The plan has a dispatched element (current goal/event). */
	public RProcessableElement dispatchedelement;
	
//	/** The plan has subgoals attribute (hack!!! redundancy to goal_has_parentplan). */
//	public static OAVAttributeType plan_has_subgoals;
		
	/** The plan has a wait abstraction attribute. */
	public WaitAbstraction waitabstraction;
		
	/** The plan has a waitqueue wait abstraction attribute. */
	public WaitAbstraction waitqueuewa;
	
//	/** The plan has a waitqueue processable elements attribute. */
//	public static OAVAttributeType plan_has_waitqueueelements;
	
	/** The plan has exception attribute. */
	public Exception plan_has_exception;
	
	/** The plan has lifecycle state attribute. */
	public String lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	public String processingstate;
	
//	/** The plan has a timer attribute (when waiting). */
//	public static ? plan_has_timer;
	
	/**
	 *  Create a new plan.
	 */
	public RPlan(MPlan mplan)
	{
		super(mplan);
	}

	/**
	 *  Get the processingState.
	 *  @return The processingState.
	 */
	public String getProcessingState()
	{
		return processingstate;
	}

	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(String processingstate)
	{
		this.processingstate = processingstate;
	}

	/**
	 *  Get the lifecycleState.
	 *  @return The lifecycleState.
	 */
	public String getLifecycleState()
	{
		return lifecyclestate;
	}

	/**
	 *  Set the lifecycleState.
	 *  @param lifecycleState The lifecycleState to set.
	 */
	public void setLifecycleState(String lifecyclestate)
	{
		this.lifecyclestate = lifecyclestate;
	}
	
	/**
	 * 
	 */
	public boolean isWaitingFor(Object procelem)
	{
		return RPlan.PLANPROCESSINGTATE_WAITING.equals(getProcessingState()) 
			&& waitabstraction!=null && waitabstraction.isWaitingFor(procelem);
	}
	
}
