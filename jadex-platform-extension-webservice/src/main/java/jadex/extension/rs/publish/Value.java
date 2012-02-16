package jadex.extension.rs.publish;

import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;

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
	
	/**
	 * 
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
	 * 
	 */
	public static Object evaluate(jadex.micro.annotation.Value value, String[] imports) throws Exception
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
