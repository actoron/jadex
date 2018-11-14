package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;


/**
 *  Boolean node performs mathematical operations on it's (two) child nodes.
 */
public class BooleanNode	extends ExpressionNode
{
	//-------- constants --------

	/** The and (&&) operator. */
	public static final int	AND	= 1;

	/** The or (||) operator. */
	public static final int	OR	= 2;

	/** The not (!) operator. */
	public static final int	NOT	= 3;

	//-------- attributes --------

	/** The operation. */
	protected int op;

	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public BooleanNode(ParserImpl p, int id)
	{
		super(p, id);
		setStaticType(Boolean.class);
	}

	//-------- attribute accessors --------

	/**
	 *  Set the token text.
	 *  @param text	The token text.
	 */
	public void	setText(String text)
	{
		super.setText(text);
		this.op	= fromString(text);
	}

	//-------- evaluation --------

	/**
	 *  Check argument types, and precompute expression
	 *  when some children are constant.
	 */
	public void precompile()
	{
		// Check number of children and operator type.
		if((op==AND || op==OR) && jjtGetNumChildren()<2
		|| op==NOT && jjtGetNumChildren()!=1)
		{
			throw new ParseException("Wrong number of subterms for expression: "+this);
		}
		if(!(op==AND || op==OR || op==NOT))
		{
			throw new ParseException("Unknown operator type "+op+": "+this);
		}

		boolean	allfalse	= true;
		boolean	alltrue	= true;
		for(int i=0; i<jjtGetNumChildren(); i++)
		{
			// Check type of children.
			ExpressionNode	node	= (ExpressionNode)jjtGetChild(i);
			Class type	= node.getStaticType();
			if(type!=null && !type.equals(Boolean.class))
			{
				throw new ParseException("Term of expression not boolean: "+this);
			}

			// Precompute constant value, when children are constant.
			if(node.isConstant())
			{
				try
				{
					boolean	val	= ((Boolean)node.getValue(null)).booleanValue();
					allfalse	= allfalse && !val;
					alltrue	= alltrue && val;
					if(op==NOT)
					{
						setConstant(true);
						setConstantValue(Boolean.valueOf(!val));
					}
					else if(op==AND && !val)
					{
						// If one term of AND is false, expression is false.
						setConstant(true);
						setConstantValue(Boolean.FALSE);
					}
					else if(op==OR && val)
					{
						// If one term of OR is true, expression is true.
						setConstant(true);
						setConstantValue(Boolean.TRUE);
					}
				}
				catch(Exception e)
				{
				}
			}
			else
			{
				// Value unknown.
				allfalse	= false;
				alltrue	= false;
			}
		}

		// Save constant value, when all terms of and are true,
		// or all terms of or are false.
		if(op==AND && alltrue)
		{
			setConstant(true);
			setConstantValue(Boolean.TRUE);
		}
		else if(op==OR && allfalse)
		{
			setConstant(true);
			setConstantValue(Boolean.FALSE);
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
		if(isConstant())
		{
			return getConstantValue();
		}

		boolean ret;

		switch(op)
		{
			// Evaluate AND expression.
			case AND:
				ret	= true;
				for(int i=0; i<jjtGetNumChildren() && ret; i++)
				{
					Object	val	= ((ExpressionNode)jjtGetChild(i)).getValue(fetcher);
					// System.out.println("Term "+i+": "+val);
					if(!(val instanceof Boolean))
					{
						throw new RuntimeException("Term of expression not boolean: "+this+", "+val);
					}
					ret	= ((Boolean)val).booleanValue();
				}
			break;

			// Evaluate OR expression.
			case OR:
				ret	= false;
				for(int i=0; i<jjtGetNumChildren() && !ret; i++)
				{
					Object	val	= ((ExpressionNode)jjtGetChild(i)).getValue(fetcher);
					// System.out.println("Term "+i+": "+val);
					if(!(val instanceof Boolean))
					{
						throw new RuntimeException("Term of expression not boolean: "+this);
					}
					ret	= ((Boolean)val).booleanValue();
				}
			break;

			// Evaluate NOT expression.
			case NOT:
				Object	val	= ((ExpressionNode)jjtGetChild(0)).getValue(fetcher);
				// System.out.println("Term: "+val);
				if(!(val instanceof Boolean))
				{
					throw new RuntimeException("Term of expression not boolean: "+this);
				}
				ret	= !((Boolean)val).booleanValue();
			break;
			default:
				throw new RuntimeException("Unknown operator type "+op+": "+this);
		}

		return Boolean.valueOf(ret);
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		String	ret;
		if(jjtGetNumChildren()>1)
		{
			ret	= subnodeToString(0);
			for(int i=1; i<jjtGetNumChildren(); i++)
				ret	+= toString(op) + subnodeToString(i);
		}
		else
		{
			ret	= toString(op) + subnodeToString(0);
		}
		return ret;
	}

	//-------- static part --------

	/**
	 *  Convert an operator to a string representation.
	 *  @param operator	The operator.
	 *  @return A string representation of the operator.
	 */
	public static String	toString(int operator)
	{
		switch(operator)
		{
			case AND:
				return "&&";
			case OR:
				return "||";
			case NOT:
				return "!";
			default:
				return ""+operator;
		}
	}

	/**
	 *  Convert an operator from a string representation.
	 *  @param operator	The operator as string.
	 *  @return The int value of the operator.
	 */
	public static int	fromString(String operator)
	{
		if("&&".equals(operator))
		{
			return AND;
		}
		else if("||".equals(operator))
		{
			return OR;
		}
		else if("!".equals(operator))
		{
			return NOT;
		}
		else
		{
			throw new ParseException("Unknown operator: "+operator);
		}
	}

	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && op==((BooleanNode)o).op;
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		return super.hashCode()*31 + op;
	}
}

