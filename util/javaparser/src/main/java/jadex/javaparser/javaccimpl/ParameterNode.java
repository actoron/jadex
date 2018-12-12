package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;


/**
 *  Parameter node representing a parameter.
 *  Parameter values are supplied at evaluation time.
 */
public class ParameterNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ParameterNode(ParserImpl p, int id)
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
		if(fetcher==null)// || !params.containsKey(getText()))
			throw new RuntimeException("Parameter not accessible: "+getText());
		
		//return params.get(getText());
		return fetcher.fetchValue(getText());
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		return getText();			
	}

	/**
	 *  Get unbound parameter nodes.
	 *  @return The unbound parameter nodes.
	 */
	public ParameterNode[]	getUnboundParameterNodes()
	{
		return new ParameterNode[]{this};
	}

	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && SUtil.equals(getText(), ((ParameterNode)o).getText());
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		return super.hashCode()*31 + (getText()!=null ? getText().hashCode() : 1);
	}
}

