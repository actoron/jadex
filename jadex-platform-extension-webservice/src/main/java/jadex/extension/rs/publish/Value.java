package jadex.extension.rs.publish;

/**
 * 
 */
public class Value
{
	/** The expression. */
	protected String expression;
	
	/** The class. */
	protected Class<?> clazz;

	/**
	 * 
	 */
	public Value(String expression)
	{
		this.expression = expression;
	}

	/**
	 * 
	 */
	public Value(Class< ? > clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the expression.
	 *  @return the expression.
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 *  Get the clazz.
	 *  @return the clazz.
	 */
	public Class<?> getClazz()
	{
		return clazz;
	}
}
