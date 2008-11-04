package jadex.javaparser.javaccimpl;

import jadex.javaparser.IValueFetcher;


/**
 *  Constant node representing a constant value.
 */
public class ConstantNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ConstantNode(ParserImpl p, int id)
	{
		super(p, id);
		setConstant(true);
	}

	//-------- attribute accessors --------

	/**
	 *  Set the constant value.
	 *  @param value	The constant value.
	 */
	public void	setValue(Object value)
	{
		setConstantValue(value);
		if(value!=null)
		{
			setStaticType(value.getClass());
		}
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
		return getConstantValue();
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		if(getConstantValue() instanceof String)
			return "\"" + getConstantValue() + "\"";
		else if(getConstantValue() instanceof Character)
			return "'" + getConstantValue() + "'";
		else
			return "" + getConstantValue();
	}

	//-------- methods --------

	/**
	 *  Create a string representation of this node for dumping in a tree.
	 *  @return A string representation of this node.
	 */
	public String toString(String prefix)
	{
		return prefix + ParserImplTreeConstants.jjtNodeName[id]+"("+getConstantValue()+")";
	}
}

