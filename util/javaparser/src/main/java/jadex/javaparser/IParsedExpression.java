package jadex.javaparser;

import java.util.Set;

import jadex.commons.IValueFetcher;


/**
 *  An expression, that can be evaluated in a given state.
 */
public interface IParsedExpression
{
	/**
	 *  Get the expression text.
	 *  @return The expression text.
	 */
	public String	getExpressionText();

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher);

	/**
	 *  Get the static type.
	 *  If no information about the return type of an expression
	 *  is available (e.g. because it depends on the evaluation context),
	 *  the static type is null.
	 *  @return The static type.
	 */
	public Class<?> getStaticType();
	
	/**
	 *  Get the parameters used in the expression.
	 */
	public Set<String>	getParameters();
}