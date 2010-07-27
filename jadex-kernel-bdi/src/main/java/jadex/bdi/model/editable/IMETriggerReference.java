package jadex.bdi.model.editable;

import jadex.bdi.model.IMTriggerReference;

/**
 *  Editable interface for a reference in a trigger.
 */
public interface IMETriggerReference	extends IMTriggerReference, IMEElement
{
	/**
	 *  Set the reference.
	 *  @param reference	The name of the referenced element.
	 */
	public void	setReference(String reference);
	
	/**
	 *  Create a match expression.
	 *  @param expression	The match expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return the expression.
	 */
	public IMEExpression	createMatchExpression(String expression, String language);	
}
