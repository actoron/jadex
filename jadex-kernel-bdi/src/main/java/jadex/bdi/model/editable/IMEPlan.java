package jadex.bdi.model.editable;

import jadex.bdi.model.IMPlan;

/**
 *  Editable interface for a plan.
 */
public interface IMEPlan extends IMPlan, IMEParameterElement
{
	/**
	 *  Set the priority.
	 *  param priority	The priority.
	 */
	public void	setPriority(int priority);
	
	/**
	 *  Create a precondition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The precondition.
	 */
	public IMEExpression	createPrecondition(String expression, String language);
	
	/**
	 *  Create a context condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The context condition.
	 */
	public IMECondition createContextCondition(String expression, String language);
	
	/**
	 *  Create the body.
	 *  @param impl	The implementation (e.g. class or file name).
	 *  @param type	The plan body type (null for standard java plans).
	 *  @return The body.
	 */
	public IMEPlanBody createBody(String impl, String type);
	
	/**
	 *  Create the waitqueue.
	 *  @return The waitqueue.
	 */
	public IMETrigger createWaitqueue();
	
	/**
	 *  Create the trigger.
	 *  @return The trigger.
	 */
	public IMEPlanTrigger createTrigger();
}
