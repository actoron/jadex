package jadex.javaparser.javaccimpl;

import java.lang.reflect.Array;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;


/**
 *  Node for selection from array.
 */
public class SelectionNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public SelectionNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- evaluation --------

	/**
	 *  Check if value is array and precompute the static type.
	 */
	public void precompile()
	{
		ExpressionNode	arraynode	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	indexnode	= (ExpressionNode)jjtGetChild(1);

		// Precompute type.
		if(arraynode.getStaticType()!=null)
		{
			if(arraynode.getStaticType().getComponentType()==null)
			{
				throw new ParseException("Type is not array: "+this);
			}
			setStaticType(arraynode.getStaticType().getComponentType());
		}

		// Check expression type.
		if(indexnode.getStaticType()!=null && !SReflect.isSupertype(
			Integer.class, indexnode.getStaticType()))
		{
			throw new ParseException("Index type not int: "+this);
		}

		// This node is constant, when the subnodes are constant.
		if(arraynode.isConstant() && indexnode.isConstant())
		{
			try
			{
				setConstantValue(getValue(null));
				setConstant(true);
			}
			catch(Exception e)
			{
			}
		}
	}

	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher) //throws Exception
	{
		// Return constant value, if any.
		if(isConstant())
		{
			return getConstantValue();
		}

		// Get array object and index value,m and return selection.
		Object	array	= ((ExpressionNode)jjtGetChild(0)).getValue(fetcher);
		Object	index	= ((ExpressionNode)jjtGetChild(1)).getValue(fetcher);

		Array.get(array, ((Number)index).intValue());
		
		return Array.get(array, ((Number)index).intValue());
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		return jjtGetChild(0).toPlainString() + "["+jjtGetChild(1).toPlainString()+"]";
	}
}

