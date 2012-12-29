package jadex.bdiv3.runtime;

import java.lang.reflect.Method;

import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MethodInfo;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;

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
	public Object reason;

	/** The plan has a dispatched element (current goal/event). */
	public Object dispatchedelement;
	
//	/** The plan has subgoals attribute (hack!!! redundancy to goal_has_parentplan). */
//	public static OAVAttributeType plan_has_subgoals;
		
	/** The plan has a wait abstraction attribute. */
	public WaitAbstraction waitabstraction;
		
	/** The plan has a waitqueue wait abstraction attribute. */
	public WaitAbstraction waitqueuewa;
	
//	/** The plan has a waitqueue processable elements attribute. */
//	public static OAVAttributeType plan_has_waitqueueelements;
	
	/** The plan has exception attribute. */
	public Exception exception;
	
	/** The plan has lifecycle state attribute. */
	public String lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	public String processingstate;
	
//	/** The plan has a timer attribute (when waiting). */
//	public static ? plan_has_timer;
	
	/** The plan body. */
	protected IPlanBody body;
	
	/** The candidate from which this plan was created. Used for tried plans in proc elem. */
	protected Object candidate;
	
	/**
	 * 
	 */
	public static RPlan createRPlan(MPlan mplan, Object reason, IInternalAccess ia)
	{
		RPlan rplan = new RPlan((MPlan)mplan, mplan);
		Method mbody = ((MethodInfo)mplan.getBody()).getMethod(ia.getClassLoader());
		IPlanBody body = new MethodPlanBody(ia, rplan, mbody);
		rplan.setBody(body);
		rplan.setReason(reason);
		rplan.setDispatchedElement(reason);
		return rplan;
	}
	
	/**
	 * 
	 */
	public static void adoptPlan(RPlan rplan, IInternalAccess ia)
	{
		IConditionalComponentStep<Void> action = new ExecutePlanStepAction(rplan);
		ia.getExternalAccess().scheduleStep(action);
	}
	
	/**
	 *  Create a new plan.
	 */
	public RPlan(MPlan mplan, Object candidate)
	{
		super(mplan);
		this.candidate = candidate;
		setLifecycleState(PLANLIFECYCLESTATE_NEW);
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
	 *  Get the reason.
	 *  @return The reason.
	 */
	public Object getReason()
	{
		return reason;
	}

	/**
	 *  Set the reason.
	 *  @param reason The reason to set.
	 */
	public void setReason(Object reason)
	{
		this.reason = reason;
	}

	/**
	 *  Get the dispatchedelement.
	 *  @return The dispatchedelement.
	 */
	public Object getDispatchedElement()
	{
		return dispatchedelement;
	}

	/**
	 *  Set the dispatchedelement.
	 *  @param dispatchedelement The dispatchedelement to set.
	 */
	public void setDispatchedElement(Object dispatchedelement)
	{
		this.dispatchedelement = dispatchedelement;
	}
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public IPlanBody getBody()
	{
		return body;
	}

	/**
	 *  Set the body.
	 *  @param body The body to set.
	 */
	public void setBody(IPlanBody body)
	{
		this.body = body;
	}
	
	/**
	 *  Get the candidate.
	 *  @return The candidate.
	 */
	public Object getCandidate()
	{
		return candidate;
	}

	/**
	 *  Set the candidate.
	 *  @param candidate The candidate to set.
	 */
	public void setCandidate(Object candidate)
	{
		this.candidate = candidate;
	}

	/**
	 * 
	 */
	public boolean isWaitingFor(Object procelem)
	{
		return RPlan.PLANPROCESSINGTATE_WAITING.equals(getProcessingState()) 
			&& waitabstraction!=null && waitabstraction.isWaitingFor(procelem);
	}
	
	/**
	 * 
	 */
	public boolean isPassed()
	{
		return RPlan.PLANLIFECYCLESTATE_PASSED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isFailed()
	{
		return RPlan.PLANLIFECYCLESTATE_FAILED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isAborted()
	{
		return RPlan.PLANLIFECYCLESTATE_ABORTED.equals(lifecyclestate);
	}

}
