package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;

/**
 *  Prepend capability prefix to expression names.
 */
public class ExpressionbaseWrapper implements IExpressionbase
{
	//-------- attributes --------
	
	/** The flat expression base. */
	protected IExpressionbase	expressionbase;
	
	/** The full capability prefix. */
	protected String	prefix;
		
	//-------- constructors --------
	
	/**
	 *  Create an expression base wrapper.
	 */
	public ExpressionbaseWrapper(IExpressionbase expressionbase, String prefix)
	{
		this.expressionbase	= expressionbase;
		this.prefix	= prefix;
	}
	
	//-------- element methods ---------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement()
	{
		return expressionbase.getModelElement();
	}
	
	//-------- IExpressionbase methods --------
	
	/**
	 *  Get a predefined expression. 
	 *  Creates a new instance on every call.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The expression object.
	 */
	public IExpression	getExpression(String name)
	{
		return expressionbase.getExpression(prefix + name);
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression)
	{
		return expressionbase.createExpression(expression);
	}
}