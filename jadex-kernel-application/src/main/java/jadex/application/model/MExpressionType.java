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
	
	/** The class. */
	protected Class clazz;
	
	/** The value. */
	protected String value;
	
	/** The parsed value. */
	protected IParsedExpression parsedvalue;

	/** The language. */
	protected String language;
	
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
	 *  Get the clazz name.
	 *  @return The clazz name.
	 */
	public String getClassName()
	{
		return classname;
	}

	/**
	 *  Set the clazz name.
	 *  @param clazz The clazz name.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz.
	 */
	public void setClazz(Class clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public IParsedExpression getParsedValue()
	{
		return this.parsedvalue;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setParsedValue(IParsedExpression parsedvalue)
	{
		this.parsedvalue = parsedvalue;
	}
	
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

	/**
	 *  Get the language.
	 *  @return The language.
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 *  Set the language.
	 *  @param language The language.
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}
}
