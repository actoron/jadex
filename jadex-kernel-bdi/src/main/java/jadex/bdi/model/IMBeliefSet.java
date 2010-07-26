package jadex.bdi.model;


/**
 *  Interface for belief set model.
 */
public interface IMBeliefSet extends IMTypedElement
{
	/**
	 *  Get the facts.
	 *  @return The facts. 
	 */
	public IMExpression[] getFacts();
	
	/**
	 *  Get the facts expression.
	 *  @return The facts expression. 
	 */
	public IMExpression getFactsExpression();
	
	/**
	 *  Test if the belief is used as argument.
	 *  @return True if used as argument. 
	 */
	public boolean isArgument();
	
	/**
	 *  Test if the belief is used as result.
	 *  @return True if used as result. 
	 */
	public boolean isResult();

}
