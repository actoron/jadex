package jadex.bridge.modelinfo;


import jadex.bridge.ClassInfo;


/**
 *  An unparsed expression for being able to transfer to other nodes.
 *  
 *  idea: allow storing also imports in the expression for later evaluation
 */
public class UnparsedExpression
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The clazz info. */
	protected ClassInfo clazz;
	
	/** The value. */
	protected String value;
	
	/** The language. */
	protected String language;
	
	/** The parsed expression (cached for speed, but not transmitted). */
	protected Object parsed;
	
	//-------- constructors --------

	/**
	 *  Create a new expression.
	 */
	public UnparsedExpression()
	{
	}
	
	/**
	 *  Create a new expression.
	 */
	public UnparsedExpression(String name, String value)
	{
		this(name, (Class<?>)null, value, null);
	}
	
	/**
	 *  Create a new expression.
	 */
	public UnparsedExpression(String name, Class<?> clazz, String value)
	{
		this(name, clazz, value, null);
	}
	
	
	/**
	 *  Create a new expression.
	 */
	public UnparsedExpression(String name, Class<?> clazz, String value, String language)
	{
		this.name = name;
		this.clazz = clazz!=null? new ClassInfo(clazz.getName()): null; 
		this.value = value;
		this.language = language;
	}
	
	/**
	 *  Create a new expression.
	 */
	public UnparsedExpression(String name, String classname, String value, String language)
	{
		this.name = name;
		this.clazz = classname!=null? new ClassInfo(classname): null; 
		this.value = value;
		this.language = language;
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
	 *  @return the clazz.
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
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
	
	/**
	 *  Get the parsed.
	 *  @return The parsed.
	 */
	public Object getParsed()
	{
		return parsed;
	}

	/**
	 *  Set the parsed.
	 *  @param parsed The parsed to set.
	 *  // changed name to exclude from transfer
	 */
	public void setParsedExp(Object parsed)
	{
		this.parsed = parsed;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "UnparsedExpression(name=" + name + ", classname=" + clazz
				+ ", value=" + value + ")";
	}
}
