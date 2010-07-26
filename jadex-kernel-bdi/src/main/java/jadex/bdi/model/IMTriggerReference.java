package jadex.bdi.model;



public interface IMTriggerReference	extends IMElement
{
	/**
	 *  Get the reference.
	 */
	public String	getReference();
	
	/**
	 *  Get the match expression.
	 */
	public IMExpression	getMatchExpression();	
}
