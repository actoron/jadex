package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;


/**
 *  A dummy node for grouping the arguments of a method call or
 *  constructor invocation.
 */
public class ArgumentsNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ArgumentsNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- evaluation --------

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher)
	{
		// Arguments node has no value.
		return null;
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		String ret	= "(";
		for(int i=0; i<jjtGetNumChildren(); i++)
		{
			ret	+= jjtGetChild(i).toPlainString();
			if(i<jjtGetNumChildren()-1)
			{
				ret	+= ", ";
			}
		}
		ret	+= ")";
		return ret;
	}
}

