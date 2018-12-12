package jadex.javaparser;


import java.util.Map;

/**
 *  Interface for expression parsers. Must support the parsing of expressions
 *  into evaluable objects (IParsedExpression).
 */
public interface IExpressionParser
{
	/**
	 *  Parse an expression string.
	 *  @param expression The expression string.
	 *  @param imports A list of imports.
	 *  @param tmodel The type model.
	 *  @param parameters Parameters declared in the expression (name -> OAV type).
	 *  @return The parsed expression.
	 */
	public IParsedExpression parseExpression(String expression, String[] imports, Map parameters, ClassLoader classloader);	
}
