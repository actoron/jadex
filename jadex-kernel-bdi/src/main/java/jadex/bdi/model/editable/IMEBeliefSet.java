package jadex.bdi.model.editable;

import jadex.bdi.model.IMBeliefSet;
import jadex.bdi.model.IMExpression;

/**
 * 
 */
public interface IMEBeliefSet extends IMBeliefSet, IMETypedElement
{
	/**
	 *  Create the fact.
	 *  @return The fact. 
	 */
	public IMEExpression createFact();
	
	/**
	 *  Create a facts expression.
	 *  @param The facts expression. 
	 */
	public IMExpression createFactsExpression();
	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setArgument(boolean arg);
	
	/**
	 *  Set the belief is used as argument.
	 *  @param res The result flag. 
	 */
	public void setResult(boolean arg);
}
