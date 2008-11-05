package jadex.bridge;

import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;



/**
 *  A configuration property.
 */
public class Property
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The property type (defines the kind of property). */
	protected String type;
	
	/** The value. */
	protected String value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new property.
	 */
	public Property(String type, String value)
	{
		this(null, type, value);
	}

	/**
	 *  Create a new property.
	 */
	public Property(String name, String type, String value)
	{
		this.name = name;
		this.type = type;
		this.value = value;
	}

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 *  Set the value.
	 *  @return The value.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 *  Evaluates a java expression. 
	 *  @return fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public Object	getJavaObject(IValueFetcher fetcher)
	{
		IExpressionParser	parser	= new JavaCCExpressionParser();
		IParsedExpression	exp	= parser.parseExpression(value, null, null, null);
		return exp.getValue(fetcher);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Property( type="+type+", name="+name+" , value="+value+")";
	}
}
