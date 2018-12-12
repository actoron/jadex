package jadex.javaparser.javaccimpl;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Map;

import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;

/**
 *  The jadex parser parses all types of expressions in ADF and queries.
 */
public class JavaCCExpressionParser	implements IExpressionParser, Serializable
{
	//-------- methods --------

	/**
	 *  Parse an expression string.
	 *  @param expression The expression string.
	 *  @param imports A list of imports.
	 *  @param tmodel The type model.
	 *  @param parameters Parameters declared in the expression (name -> OAV type).
	 *  @return The parsed expression.
	 */
	public IParsedExpression parseExpression(String expression, String[] imports, Map parameters, ClassLoader classloader)	
	{
		// todo: use parameters for checking

		if(expression==null)
			throw new NullPointerException("String required for parsing.");
		// Init the parser.
		// Created every time, because JavaCC otherwise has memory leaks
		// and isn't thread safe.
		ParserImpl	parser	= new ParserImpl(new StringReader(expression));
		parser.setImports(imports);
		parser.setClassLoader(classloader);
		// todo: parser.setParameters(parameters);

		ExpressionNode	node;
		try
		{
			// Parse the expression.
			node	= parser.parseExpression();
			
			// Keep text available for further use (e.g. display in starter panel)
			node.setExpressionText(expression);
			//node.dump("");

			// Check and precompile the expression.
			node.precompileTree();
		}
//		catch(ParseException e)
//		{
//			ParserSourceLocation loc = new ParserSourceLocation(null, e.currentToken.next.beginLine, 
//				e.currentToken.next.beginColumn); 
//			throw new ParserException("Error parsing: "+expression+"\n"+e.getMessage(), e, loc);
//		}
//		catch(RuntimeException e)
//		{
//			throw e;
//		}
		catch(Throwable e)
		{
//			e.printStackTrace();
			String	msg	= e.getMessage();
			if(msg != null) 
			{
				int index = msg.indexOf("Was expecting one of");
				if(index != -1) 
				{
					msg = msg.substring(0, index);// + "Maybe missing import?";
					msg = msg.replace("\n", " ");
				}
			}
//			else
//			{
				msg = msg+", expr="+expression;
//			}
//			throw new RuntimeException("Error parsing: "+expression+": "+e.getClass().getName()+": "+msg);
			throw new RuntimeException(e.getClass().getName()+": "+msg);
		}
		

		// Now return that stuff.
		return  node;
	}
}

