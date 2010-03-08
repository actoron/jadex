package deco4mas.helper;

import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 * 
 */
public class SJavaParser
{
	protected static IExpressionParser parser = new JavaCCExpressionParser();
	
	/**
	 *  Evaluates a java expression. 
	 *  @return fetcher	Expression parameters can be supplied as value fetcher. 
	 *  @return The evaluated object.
	 */
	public static Object evaluateExpression(String exptxt, IValueFetcher fetcher)
	{
//		IExpressionParser parser = new JavaCCExpressionParser();
		IParsedExpression exp = parser.parseExpression(exptxt, null, null, null);
		return exp.getValue(fetcher);
	}
}
