package jadex.extension.rs.publish;

import jadex.javaparser.SJavaParser;

/**
 *  Java class representation for the Value annotation
 *  as annotations cannot be created at runtime.
 */
public class Value
{
	//-------- attributes --------
	
	/** The expression. */
	protected String expression;
	
	/** The class. */
	protected Class<?> clazz;

	//-------- constructors --------
	
	/**
	 *  Create a new value.
	 *  @param expression The creation expression.
	 */
	public Value(String expression)
	{
		this.expression = expression;
	}

	/**
	 *  Create a new value.
	 *  @param clazz The clazz.
	 */
	public Value(Class<?> clazz)
	{
		this.clazz = clazz;
	}
	
	//-------- methods --------

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
	
	/**
	 *  Evaluate a value.
	 */
	public static Object evaluate(Value value, String[] imports) throws Exception
	{
		Object ret = null;
		
		Class<?> clazz = value.getClazz();
		if(clazz!=null && !Object.class.equals(clazz))
		{
			ret = clazz.newInstance();
		}
		else if(value.getExpression()!=null)
		{
			ret = SJavaParser.evaluateExpression(value.getExpression(), imports, null, null);
		}
		
		return ret;
	}
	
	/**
	 *  Evaluate a value.
	 */
	public static Object evaluate(jadex.bridge.service.annotation.Value value, String[] imports) throws Exception
	{
		Object ret = null;
		
		Class<?> clazz = value.clazz();
		if(clazz!=null && !Object.class.equals(clazz))
		{
			ret = clazz.newInstance();
		}
		else if(value.value().length()>0)
		{
			ret = SJavaParser.evaluateExpression(value.value(), imports, null, null);
		}
		
		return ret;
	}
}
