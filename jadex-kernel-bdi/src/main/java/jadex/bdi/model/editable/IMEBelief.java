package jadex.bdi.model.editable;

import jadex.bdi.model.IMBelief;

/**
 * 
 */
public interface IMEBelief extends IMBelief, IMETypedElement
{
	/**
	 *  Create the fact.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The fact. 
	 */
	public IMEExpression createFact(String expression, String language);
	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setArgument(boolean arg);
	
	/**
	 *  Set the belief is used as argument.
	 *  @param res The result flag. 
	 */
	public void setResult(boolean res);
}
