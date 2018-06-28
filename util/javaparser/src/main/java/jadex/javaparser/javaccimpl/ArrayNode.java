package jadex.javaparser.javaccimpl;

import java.lang.reflect.Array;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;


/**
 *  A node representing an array to create.
 */
public class ArrayNode	extends ExpressionNode
{
	//-------- constants --------

	/** The array with content constructor. */
	public static final int	ARRAY	= 1;

	/** The empty array constructor. */
	public static final int	ARRAY_DIMENSION	= 2;

	//-------- attributes --------

	/** The node type. */
	protected int type;

	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ArrayNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- attribute accessors --------

	/**
	 *  Set the node type.
	 *  @param type	The node type.
	 */
	public void	setType(int type)
	{
		this.type	= type;
	}

	/**
	 *  Get the node type.
	 *  @return The node type.
	 */
	public int	getType()
	{
		return this.type;
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
		else if(type!=ARRAY && type!=ARRAY_DIMENSION)
		{
			throw new ParseException("Unknown node type "+type+": "+this);
		}

		// Get child nodes.
		ExpressionNode	typenode	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	argsnode	= (ExpressionNode)jjtGetChild(1);

		// Precompute type.
		Class	clazz	= null;
		if(typenode.isConstant())
		{
			try
			{
				clazz	= (Class)typenode.getValue(null);
				setStaticType(clazz);
			}
			catch(Exception e)
			{
			}
		}

		// Array: Check content types.
		if(type==ARRAY && clazz!=null)
		{
			// Check for array type.
			if(clazz.getComponentType()==null)
			{
				throw new ParseException("Type not array: "+this);
			}

			for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
			{
				ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);
				if(node.getStaticType()!=null && !SReflect.isSupertype(
					clazz.getComponentType(), node.getStaticType()))
				{
					throw new ParseException("Content does not match array type: "+this);
				}
			}
		}

		// Array Dimension: Check for integer.
		else if(type==ARRAY_DIMENSION)
		{
			for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
			{
				ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);
				if(node.getStaticType()!=null && !SReflect
					.isSupertype(Integer.class, node.getStaticType()))
				{
					throw new ParseException("Dimension specification must be int: "+this);
				}
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
		ExpressionNode	typenode	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	argsnode	= (ExpressionNode)jjtGetChild(1);
		Class	clazz	= (Class)typenode.getValue(fetcher);
		Object	value	= null;

		// Array with content.
		if(type==ARRAY)
		{
			// Check for array type.
			if(clazz.getComponentType()==null)
			{
				throw new RuntimeException("Type not array: "+this);
			}

			value	= Array.newInstance(clazz.getComponentType(), argsnode.jjtGetNumChildren());

			for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
			{
				ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);
				Object	val	= node.getValue(fetcher);
				if(val!=null && !SReflect.isSupertype(
					clazz.getComponentType(), val.getClass()))
				{
					throw new RuntimeException("Content does not match array type: "+this);
				}
				else if(val==null && clazz.getComponentType().isPrimitive())
				{
					throw new RuntimeException("Cannot put null into basic type array: "+this);
				}
				Array.set(value, i, val);
			}
		}

		// Array Dimension: Check for integer.
		else if(type==ARRAY_DIMENSION)
		{
			int[]	dims	= new int[argsnode.jjtGetNumChildren()];
			for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
			{
				clazz	= clazz.getComponentType();
				ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);
				Object	val	= node.getValue(fetcher);
				if(!(val instanceof Number))
				{
					throw new ParseException("Dimension specification must be int: "+this);
				}
				dims[i]	= ((Number)val).intValue();
			}
			value	= Array.newInstance(clazz, dims);
		}

		return value;
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		String ret	= "new " + jjtGetChild(0).toPlainString();
		Node	argsnode	= jjtGetChild(1);

		if(type==ARRAY)
		{
			// Append array content.
			ret	+= "{";
			for(int i=0; i<argsnode.jjtGetNumChildren(); i++)
			{
				ret	+= argsnode.jjtGetChild(i).toPlainString();
				if(i<argsnode.jjtGetNumChildren()-1)
				{
					ret	+= ", ";
				}
			}
			ret	+= "}";
		}
		else // if(type==ARRAY_DIMENSION)
		{
			// Fill in dimension specifications.
			int idx	= ret.indexOf("[");
			int	dims	= ret.substring(idx).length()/2;
			ret	= ret.substring(0, idx);
			for(int i=0; i<dims; i++)
			{
				ret	+= "[";
				if(i<argsnode.jjtGetNumChildren())
				{
					ret	+= argsnode.jjtGetChild(i).toPlainString();
				}
				ret	+= "]";
			}
		}

		return ret;
	}


	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && type==((ArrayNode)o).getType();
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		return super.hashCode()*31 + type;
	}
}

