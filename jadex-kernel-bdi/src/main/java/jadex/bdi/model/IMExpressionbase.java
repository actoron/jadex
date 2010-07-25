package jadex.bdi.model;

/**
 *  Interface for expression base.
 */
public interface IMExpressionbase extends IMElement
{
	/**
	 *  Get a expression for a name.
	 *  @param name	The expression name.
	 */
	public IMExpression getExpression(String name);

	/**
	 *  Returns all expressions.
	 *  @return All expressions.
	 */
	public IMExpression[] getExpressions();
}
