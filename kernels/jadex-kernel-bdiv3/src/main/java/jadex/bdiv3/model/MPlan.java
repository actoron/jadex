package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;


/**
 *  Modelelement for a plan.
 */
public class MPlan extends MParameterElement
{
	/** The trigger. */
	protected MTrigger trigger;
	
	/** The waitqueue trigger. */
	protected MTrigger waitqueue;
	
	/** The plan body. */
	protected MBody body;
	
	/** The plan priority. */
	protected int priority;
	
	//-------- additional xml properties --------

	// pojo pre- and contextcondition are in body
		
	/** The precondition. */
	protected UnparsedExpression precondition;
		
	/** The context condition. */
	protected MCondition contextcondition;
	
	/**
	 *	Bean Constructor. 
	 */
	public MPlan()
	{
	}
	
	/**
	 *  Create a new belief.
	 */
	public MPlan(String name, MBody body, MTrigger trigger, MTrigger waitqueue, int priority)
	{
		super(name);
		this.body = body;
		this.trigger = trigger;
		this.waitqueue = waitqueue;
		this.priority = priority;
	}

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public MBody getBody()
	{
		return body;
	}

	/**
	 *  Set the body.
	 *  @param body The body to set.
	 */
	public void setBody(MBody body)
	{
		this.body = body;
	}

	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public MTrigger getTrigger()
	{
		return trigger;
	}

	/**
	 *  Set the trigger.
	 *  @param trigger The trigger to set.
	 */
	public void setTrigger(MTrigger trigger)
	{
		this.trigger = trigger;
	}
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public MTrigger getWaitqueue()
	{
		return waitqueue;
	}

	/**
	 *  Set the waitqueue.
	 *  @param waitqueue The waitqueue to set.
	 */
	public void setWaitqueue(MTrigger waitqueue)
	{
		this.waitqueue = waitqueue;
	}

	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 *  Set the priority.
	 *  @param priority The priority to set.
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	/**
	 *  Get the precondition.
	 */
	public UnparsedExpression	getPrecondition()
	{
		return precondition;
	}
	
	/**
	 *  Set the precondition.
	 */
	public void	setPrecondition(UnparsedExpression precondition)
	{
		this.precondition	= precondition;
	}

	/**
	 *  Get the contextcondition.
	 *  @return The contextcondition
	 */
	public MCondition getContextCondition()
	{
		return contextcondition;
	}

	/**
	 *  The contextcondition to set.
	 *  @param contextcondition The contextcondition to set
	 */
	public void setContextCondition(MCondition contextcondition)
	{
		this.contextcondition = contextcondition;
	}
}
