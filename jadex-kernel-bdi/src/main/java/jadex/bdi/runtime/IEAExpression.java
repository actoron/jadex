package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  The common interface for expressions.
 */
public interface IEAExpression extends IEAElement
{
	//-------- methods --------

	/**
	 *  Evaluate the expression.
	 *  @return	The value of the expression.
	 */
	public IFuture getValue();

	/**
	 *  Refresh the cached expression value.
	 * /
	public void refresh();*/

	//-------- expression parameters --------

	/**
	 *  Set an expression parameter.
	 *  @param name The parameter name.
	 *  @param value The parameter value.
	 */
//	public void setParameter(String name, Object value);

	/**
	 *  Get an expression parameter.
	 *  @param name The parameter name.
	 *  @return The parameter value.
	 */
//	public Object getParameter(String name);

	/**
	 *  Execute the query.
	 *  @return the result value of the query.
	 */
	public IFuture execute();

	/**
	 *  Execute the query using a local parameter.
	 *  @param name The name of the local parameter.
	 *  @param value The value of the local parameter.
	 *  @return the result value of the query.
	 */
	public IFuture execute(String name, Object value);

	/**
	 *  Execute the query using local parameters.
	 *  @param names The names of parameters.
	 *  @param values The parameter values.
	 *  @return The return value.
	 */
	public IFuture execute(String[] names, Object[] values);
}
