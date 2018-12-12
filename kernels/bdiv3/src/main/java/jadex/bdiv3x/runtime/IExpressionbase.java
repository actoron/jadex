package jadex.bdiv3x.runtime;


/**
 *  The expressionbase provides access to the expressions
 *  and conditions defined in the ADF and allows to define
 *  new expressions and conditions at runtime.
 */
public interface IExpressionbase extends IElement
{
	/**
	 *  Get a predefined expression. 
	 *  Creates a new instance on every call.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The expression object.
	 */
	public IExpression	getExpression(String name);

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression);

//	/**
//	 *  Create a precompiled expression.
//	 *  @param expression	The expression string.
//	 *  @param paramnames The parameter names.
//	 *  @param paramtypes The parameter types.
//	 *  @return The precompiled expression.
//	 */
//	public IExpression	createExpression(String expression, String[] paramnames, Class[] paramtypes);

	/**
	 *  Get a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  Creates a new instance on every call.
	 *  @param name	The condition name.
	 *  @return The condition.
	 */
//	public ICondition	getCondition(String name);

	/**
	 *  Create a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  @param expression	The condition expression.
	 *  @return The condition.
	 */
//	public ICondition	createCondition(String expression);

	/**
	 *  Create a condition.
	 *  @param expression	The condition expression.
	 *  @param trigger	The condition trigger.
	 *  @return The condition.
	 */
//	public ICondition	createCondition(String expression, String trigger, String[] paramnames, Class[] paramtypes);

	/**
	 *  Register a new expression model.
	 *  @param mexpression The expression model.
	 */
//	public void registerExpression(IMExpression mexpression);

	/**
	 *  Register a new expression reference model.
	 *  @param mexpression The expression reference model.
	 */
//	public void registerExpressionReference(IMExpressionReference mexpression);

	/**
	 *  Register a new condition model.
	 *  @param mcondition The condition model.
	 */
//	public void registerCondition(IMCondition mcondition);

	/**
	 *  Register a new condition reference model.
	 *  @param mcondition The condition reference model.
	 */
//	public void registerConditionReference(IMConditionReference mcondition);

	/**
	 *  Deregister an expression model.
	 *  @param mexpression The expression model.
	 */
//	public void deregisterExpression(IMExpression mexpression);

	/**
	 *  Deregister an expression reference model.
	 *  @param mexpression The expression reference model.
	 */
//	public void deregisterExpressionReference(IMExpressionReference mexpression);
	
	/**
	 *  Deregister an condition model.
	 *  @param mcondition The condition model.
	 */
//	public void deregisterCondition(IMCondition mcondition);

	/**
	 *  Deregister an condition reference model.
	 *  @param mcondition The condition reference model.
	 */
//	public void deregisterConditionReference(IMConditionReference mcondition);
	
	//-------- listeners --------
	
	/**
	 *  Add a condition listener.
	 *  @param type The condition type.
	 *  @param listener The condition listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
//	public void addConditionListener(String type, IConditionListener listener, boolean async);
	
	/**
	 *  Remove a condition listener.
	 *  @param type The condition type.
	 *  @param listener The condition listener.
	 */
//	public void removeConditionListener(String type, IConditionListener listener);
}
