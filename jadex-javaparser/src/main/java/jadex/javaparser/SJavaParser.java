package jadex.javaparser;

import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 * 
 */
public class SJavaParser
{
	//-------- constants --------
	
	/** The java parser. */
	protected static IExpressionParser parser = new JavaCCExpressionParser();

	//-------- methods --------

	/**
	 *  Evaluates a java expression. 
	 *  @return fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static Object evaluateExpression(String exptxt, IValueFetcher fetcher)
	{
		return evaluateExpression(exptxt, null, fetcher, null);
	}

	/**
	 *  Evaluates a java expression. 
	 *  @return fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static Object evaluateExpression(String exptxt, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		IParsedExpression exp = parser.parseExpression(exptxt, imports, null, classloader);
		return exp.getValue(fetcher);
	}

	/**
	 *  Evaluates a java expression. 
	 *  @return fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static IParsedExpression	parseExpression(String exptxt, String[] imports, ClassLoader classloader)
	{
		return parser.parseExpression(exptxt, imports, null, classloader);
	}
}
