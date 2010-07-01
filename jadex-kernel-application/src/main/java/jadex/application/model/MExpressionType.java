package jadex.application.model;

import jadex.javaparser.IParsedExpression;

/**
 *  Expression type.
 */
public class MExpressionType
{
	//-------- attributes --------

	/** The name. */
	protected String	name;

	/** The class name. */
	protected String classname;
	
	/** The value. */
	protected String value;

	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public MExpressionType()
	{
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public String getClassName()
	{
		return classname;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 * /
	public IParsedExpression getValue()
	{
		return this.value;
	}*/

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 * /
	public void setValue(IParsedExpression value)
	{
		this.value = value;
	}*/
	
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
}
