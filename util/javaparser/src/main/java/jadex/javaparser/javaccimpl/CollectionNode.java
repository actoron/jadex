package jadex.javaparser.javaccimpl;

import java.util.Collection;
import java.util.Map;

import jadex.commons.IValueFetcher;


/**
 *  A node representing collection values to fill in.
 */
public class CollectionNode	extends ExpressionNode
{
	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public CollectionNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- evaluation --------

	/**
	 *  Precompute type, and perform checks.
	 */
	public void precompile()
	{
		// Check number of children and node type.
		if(	!(jjtGetNumChildren()==2))
		{
			throw new ParseException("Wrong number of child nodes: "+this);
		}

		// Get child nodes.
		ExpressionNode	collnode	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	argsnode	= (ExpressionNode)jjtGetChild(1);

		// Precompute type.
		Class	clazz	= null;
		if(collnode.getStaticType()!=null)
		{
			setStaticType(collnode.getStaticType());
			clazz	= collnode.getStaticType();
		}

		// Check for Map and Collection interface for arguments.
		for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
		{
			ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);

			// Map content (key = value).
			if(node instanceof ArgumentsNode)
			{
				if(node.jjtGetNumChildren()!=2)
				{
					throw new ParseException("Wrong number of subnodes for map content: "+this);
				}
				else if(clazz!=null && !Map.class.isAssignableFrom(clazz))
				{
					throw new ParseException("Collection type not java.util.Map: "+this);
				}
			}

			// Collection content.
			else if(clazz!=null && !Collection.class.isAssignableFrom(clazz))
			{
				throw new ParseException("Collection type not java.util.Collection: "+this);
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
		// Get child nodes.
		ExpressionNode	collnode	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	argsnode	= (ExpressionNode)jjtGetChild(1);
		Object	collection	= collnode.getValue(fetcher);

		// Fill content.
		for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
		{
			ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);

			// Map content (key = value).
			if(node instanceof ArgumentsNode)
			{
				ExpressionNode	key	= (ExpressionNode)node.jjtGetChild(0);
				ExpressionNode	val	= (ExpressionNode)node.jjtGetChild(1);
				((Map)collection).put(key.getValue(fetcher),
					val.getValue(fetcher));
			}

			// Collection content.
			else
			{
				((Collection)collection).add(node.getValue(fetcher));
			}
		}

		return collection;
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		String ret	= jjtGetChild(0).toPlainString() + "{";
		Node	argsnode	= jjtGetChild(1);
		for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
		{
			ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);

			// Map content (key = value).
			if(node instanceof ArgumentsNode)
			{
				ret	+= node.subnodeToString(0) + "=" + node.subnodeToString(1);
			}

			// Collection content.
			else
			{
				ret	+= node.toPlainString();
			}

			if(i<argsnode.jjtGetNumChildren()-1)
			{
				ret	+= ", ";
			}
		}
		ret	+= "}";
		return ret;
	}
}

