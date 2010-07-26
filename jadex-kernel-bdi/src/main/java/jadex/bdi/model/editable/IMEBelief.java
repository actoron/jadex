package jadex.bdi.model.editable;

import jadex.bdi.model.IMBelief;

/**
 * 
 */
public interface IMEBelief extends IMBelief, IMETypedElement
{
	/**
	 *  Create the fact.
	 *  @return The fact. 
	 */
	public IMEExpression createFact();
	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setArgument(boolean arg);
	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setResult(boolean arg);
}
