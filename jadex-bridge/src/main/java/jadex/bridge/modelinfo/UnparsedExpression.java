package jadex.bridge.modelinfo;

import jadex.bridge.ClassInfo;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;

import java.util.Map;


/**
 *  An unparsed expression for being able to transfer to other nodes.
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
	public UnparsedExpression(String name, Class<?> clazz, String value, String language)
	{
		this.name = name;
		this.clazz = clazz!=null? new ClassInfo(clazz): null; 
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
	
	//-------- static helpers --------
	
	/**
	 *  Get a parsed property.
	 *  Handles properties, which may be parsed or unparsed,
	 *  and always returns a parsed property value.
	 *  @param	name	The property name.  
	 *  @return The property value or null if property not defined.
	 */
	public static Object	getProperty(Map<String, Object> properties, String name, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		// Todo: caching of parsed values?
		Object	ret	= properties!=null ? properties.get(name) : null;
		if(ret instanceof UnparsedExpression)
		{
			// todo: language
			UnparsedExpression	upe	= (UnparsedExpression)ret;
			ret = SJavaParser.evaluateExpression(upe.getValue(), imports, fetcher, classloader);
		}
		return ret;
	}
	
	/**
	 *  Get a parsed value.
	 *  Handles values, which may be parsed or unparsed,
	 *  and always returns a parsed value.
	 *  @param	value	The value.  
	 *  @return The parsed and evaluated value.
	 */
	public static Object	getParsedValue(Object value, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		if(value instanceof UnparsedExpression)
		{
			// todo: language
			UnparsedExpression	upe	= (UnparsedExpression)value;
			value = upe.getValue()==null ? null
				: SJavaParser.evaluateExpression(upe.getValue(), imports, fetcher, classloader);
		}
		return value;
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
