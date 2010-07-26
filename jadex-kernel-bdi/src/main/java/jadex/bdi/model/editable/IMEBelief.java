package jadex.bdi.model.editable;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMExpression;

/**
 * 
 */
public interface IMEBelief extends IMBelief
{
	/**
	 *  Get the fact.
	 *  @return The fact. 
	 */
	public IMExpression createFact();
	
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
