package jadex.bdi.model;


/**
 *  Interface for plan model element.
 */
public interface IMPlan extends IMParameterElement
{
	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority();
	
	/**
	 *  Get the precondition.
	 *  @return The precondition.
	 */
	public IMExpression getPrecondition();
	
	/**
	 *  Get the context condition.
	 *  @return The context condition.
	 */
	public IMCondition getContextCondition();
	
	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public IMPlanBody getBody();
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IMTrigger getWaitqueue();
	
	/**
	 *  Get the trigger.
	 *  @return The trigger.
	 */
	public IMPlanTrigger getTrigger();
}

