package jadex.bdiv3.runtime;

import jadex.bdiv3.actions.FindApplicableCandidatesAction;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class RGoal extends RProcessableElement
{
	//-------- goal lifecycle states --------
	
	/** The lifecycle state "new" (just created). */
	public static final String	GOALLIFECYCLESTATE_NEW	= "new";

	/** The lifecycle state "adopted" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_ADOPTED	= "adopted";

	/** The lifecycle state "option" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_OPTION	= "option";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_ACTIVE	= "active";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_SUSPENDED	= "suspended";

	/** The lifecycle state "dropping" (just before finished, but still dropping its subgoals). */
	public static final String	GOALLIFECYCLESTATE_DROPPING	= "dropping";

	/** The lifecycle state "dropped" (goal and all subgoals finished). */
	public static final String	GOALLIFECYCLESTATE_DROPPED	= "dropped";
	
	//-------- goal processing states --------
	
	/** The goal idle state. */
	public static final String	GOALPROCESSINGSTATE_IDLE	= "idle";
	
	/** The goal in-process state. */
	public static final String	GOALPROCESSINGSTATE_INPROCESS	= "in-process";

	/** The goal paused state. */
	public static final String	GOALPROCESSINGSTATE_PAUSED	= "paused";
	
	/** The goal succeeded state. */
	public static final String	GOALPROCESSINGSTATE_SUCCEEDED	= "succeeded";
	
	/** The goal failed state. */
	public static final String	GOALPROCESSINGSTATE_FAILED	= "failed";

	
	/** The lifecycle state. */
	protected String lifecyclestate;

	/** The processing state. */
	protected String processingstate;

	/** The exception. */
	protected Exception exception;
	
	/**
	 *  Create a new rgoal. 
	 */
	public RGoal(MGoal mgoal, Object goal)
	{
		super(mgoal, goal);
		this.lifecyclestate = GOALLIFECYCLESTATE_NEW;
		this.processingstate = GOALPROCESSINGSTATE_IDLE;
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
	public void setLifecycleState(String lifecycleState)
	{
		this.lifecyclestate = lifecycleState;
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
	public void setProcessingState(String processingState)
	{
		this.processingstate = processingState;
	}

	/**
	 * 
	 */
	public IComponentStep<Void> createReasoningStep(IInternalAccess ia)
	{
		return new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				MGoal mgoal = (MGoal)getModelElement();
				
				if(getLifecycleState().equals(GOALLIFECYCLESTATE_ADOPTED))
				{
					setLifecycleState(GOALLIFECYCLESTATE_ACTIVE);
				}
				
				if(getLifecycleState().equals(GOALLIFECYCLESTATE_ACTIVE))
				{
					ia.getExternalAccess().scheduleStep(new FindApplicableCandidatesAction(RGoal.this));
				}
				else
				{
					System.out.println("goal state: "+getLifecycleState());
				}
				
				return IFuture.DONE;
			}
		};
	}
}
