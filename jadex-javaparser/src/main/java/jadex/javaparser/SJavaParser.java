package jadex.javaparser;

import java.util.Map;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 *  Static java parser helper.
 */
public class SJavaParser
{
	//-------- constants --------
	
	/** The java parser. */
	protected static final IExpressionParser parser = new JavaCCExpressionParser();

	//-------- methods --------

	/**
	 *  Evaluates a java expression. 
	 *  @param fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static Object evaluateExpression(String exptxt, IValueFetcher fetcher)
	{
		return evaluateExpression(exptxt, null, fetcher, null);
	}

	/**
	 *  Evaluates a java expression. 
	 *  @param fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static Object evaluateExpression(String exptxt, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		IParsedExpression exp = parser.parseExpression(exptxt, imports, null, classloader);
		return exp.getValue(fetcher);
	}

	/**
	 *  Evaluates a java expression. 
	 *  @return The evaluated object.
	 */
	public static IParsedExpression	parseExpression(String exptxt, String[] imports, ClassLoader classloader)
	{
		return parser.parseExpression(exptxt, imports, null, classloader);
	}
	
//	public static void main(String[] args)
//	{
//		Object val = SJavaParser.evaluateExpression("\"abc\\\\rst\"", null);
//		System.out.println("val is: "+val);
//	}
	
	/**
	 *  Parse the expression.
	 *  The result is cached for later accesses.
	 */
	public static IParsedExpression parseExpression(UnparsedExpression ue, String[] imports, ClassLoader classloader)
	{
		IParsedExpression ret = (IParsedExpression)ue.getParsed();
		
		// todo: language
		if(ret==null && ue.getValue()!=null)
		{
			ret = SJavaParser.parseExpression(ue.getValue(), imports, classloader);
			ue.setParsedExp(ret);
		}
		
		return ret;
	}
	
	//-------- static helpers --------
	
	/**
	 *  Get a parsed property.
	 *  Handles properties, which may be parsed or unparsed,
	 *  and always returns a parsed property value.
	 *  @param	name	The property name.  
	 *  @return The property value or null if property not defined.
	 */
	public static Object getProperty(Map<String, Object> properties, String name, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		return getParsedValue(properties!=null ? properties.get(name) : null, imports, fetcher, classloader);
	}
	
	/**
	 *  Get a parsed value.
	 *  Handles values, which may be parsed or unparsed,
	 *  and always returns a parsed value.
	 *  @param	value	The value.  
	 *  @return The parsed and evaluated value.
	 */
	public static Object getParsedValue(Object value, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		if(value instanceof UnparsedExpression)
		{
			// todo: language
			UnparsedExpression	upe	= (UnparsedExpression)value;
			IParsedExpression	pe	= parseExpression(upe, imports, classloader);
			value	= pe!=null ? pe.getValue(fetcher) : null;
		}
		return value;
	}
	
	/**
	 *  Evaluate a string that is possiby an expression.
	 *  Expressions must use %{}
	 *  @param text The expression text.
	 *  @param imports The imports.
	 *  @param fetcher The fetcher.
	 *  @param cl The classloader.
	 *  @return The evaluated expression or the text.
	 */
	public static Object evaluateExpressionPotentially(String text, String[] imports, IValueFetcher fetcher, ClassLoader cl)
	{
		Object ret = text;
		
		if(text!=null && isExpressionString(text))
		{
			try
			{
				ret = SJavaParser.evaluateExpression(text.substring(2, text.length()-1), imports, fetcher, cl);
			}
			catch(Exception e)
			{
				ret = text;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Check if string is an expression.
	 *  @param text The expression text.
	 *  @return True, if is expression.
	 */
	public static boolean isExpressionString(String text)
	{
		return text.startsWith("%{");
	}
}
