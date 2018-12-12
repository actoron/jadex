package jadex.javaparser.javaccimpl;

import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 *  Compare node compares it's (two) child nodes.
 *  Also supports instanceof operator
 *  (second child has to evaluate to class object).
 */
public class CompareNode	extends ExpressionNode
{
	//-------- constants --------

	/** The equal (==) operator. */
	public static final int	EQUAL	= 1;

	/** The unequal (!=) operator. */
	public static final int	UNEQUAL	= 2;

	/** The greater-than (>) operator. */
	public static final int	GREATER	= 3;

	/** The less-than (<) operator. */
	public static final int	LESS	= 4;

	/** The greater-equal (>=) operator. */
	public static final int	GREATEREQUAL	= 5;

	/** The less-equal (<=) operator. */
	public static final int	LESSEQUAL	= 6;

	/** The instanceof operator. */
	public static final int	INSTANCEOF	= 7;

	//-------- attributes --------

	/** The operation. */
	protected int op;

	//-------- constructors --------

	/**
	 *  Create a node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public CompareNode(ParserImpl p, int id)
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
	 *  Check argument number, and precompute expression
	 *  when all children are constant.
	 */
	public void precompile()
	{
		// Check number of children and operator type.
		if(jjtGetNumChildren()!=2)
		{
			throw new ParseException("Wrong number of subterms for expression: "+this);
		}
		switch(op)
		{
			case EQUAL:
			case UNEQUAL:
			case GREATER:
			case LESS:
			case GREATEREQUAL:
			case LESSEQUAL:
			case INSTANCEOF:
				break;
			default:
				throw new ParseException("Unknown operator type "+op+": "+this);
		}

		ExpressionNode	left	= (ExpressionNode)jjtGetChild(0);
		ExpressionNode	right	= (ExpressionNode)jjtGetChild(1);

		// Check if second subterm of instanceof is a class.
		if(op==INSTANCEOF && !Class.class.equals(right.getStaticType()))
		{
			throw new ParseException("Right hand side of instanceof has to be a type: "+this);
		}

		if(left.isConstant() && right.isConstant())
		{
			try
			{
				setConstantValue(getValue(null));
				setConstant(true);
				if(getConstantValue()!=null)
				{
					setStaticType(getConstantValue().getClass());
				}
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
		if(isConstant())
		{
			return getConstantValue();
		}

		// Get subterms.
		Object	val1	= ((ExpressionNode)jjtGetChild(0)).getValue(fetcher);
		Object	val2	= ((ExpressionNode)jjtGetChild(1)).getValue(fetcher);
		// System.out.println("left: "+val1);
		// System.out.println("right: "+val2);

		switch(op)
		{
			case EQUAL:
				// Number.equals() works only for number objects of same type
				return val1 instanceof Number && val2 instanceof Number
					? Boolean.valueOf(compare(val1,val2)==0)
					: Boolean.valueOf(SUtil.equals(val1,val2));
			case UNEQUAL:
				// Number.equals() works only for number objects of same type
				return val1 instanceof Number && val2 instanceof Number
					? Boolean.valueOf(compare(val1,val2)!=0)
					: Boolean.valueOf(!SUtil.equals(val1,val2));
			case GREATER:
				return Boolean.valueOf(compare(val1,val2)>0);
			case LESS:
				return Boolean.valueOf(compare(val1,val2)<0);
			case GREATEREQUAL:
				return Boolean.valueOf(compare(val1,val2)>=0);
			case LESSEQUAL:
				return Boolean.valueOf(compare(val1,val2)<=0);
			case INSTANCEOF:
				// Just assume val2 is class, as the type node already
				// takes care of generating useful errors messages.
				return Boolean.valueOf(val1!=null
					&& SReflect.isSupertype((Class)val2, val1.getClass()));
			default:
				throw new RuntimeException("Unknown operator type: "+op);
		}
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		return subnodeToString(0) + toString(op) + subnodeToString(1);
	}

	//-------- helper methods --------

	/**
	 *  Compare two values.
	 *  @param val1	The first value.
	 *  @param val2	The second value.
	 *  @return	A negative integer, zero, or a positive integer as the
	 *    first value is less than, equal to, or greater than the second value.
	 *  @throws ClassCastException	when the values are not comparable.
	 */
	protected int	compare(Object val1, Object val2)
	{
		// Should support some external comparators???

		// Check for null values.
		if(val1==null || val2==null)
		{
			throw new NullPointerException("Cannot compare null "+this);
		}

		// Compare numbers.
		// Number.compareTo() works only for number objects of same type, grrr...
		else if(val1 instanceof Number && val2 instanceof Number)
		{
			Number	numval1	= (Number)val1;
			Number	numval2	= (Number)val2;
			if(numval1 instanceof Double || numval2 instanceof Double
				|| numval1 instanceof Float || numval2 instanceof Float)
			{
				double	cmp	= numval1.doubleValue() - numval2.doubleValue();
				return cmp>0 ? 1 : (cmp<0 ? -1 : 0);
			}
			else if(numval1 instanceof Long || numval2 instanceof Long)
			{
				return (int)(numval1.longValue() - numval2.longValue());
			}
			else
			{
				return numval1.intValue() - numval2.intValue();
			}
		}

		// Use Comparable interface.
		else
		{
			return ((Comparable)val1).compareTo(val2);
		}
	}

	//-------- static part --------

	/**
	 *  Convert an operator to a string representation.
	 *  @param operator	The operator
	 *  @return A string representation of the operator.
	 */
	public static String	toString(int operator)
	{
		switch(operator)
		{
			case EQUAL:
				return "==";
			case UNEQUAL:
				return "!=";
			case GREATER:
				return ">";
			case LESS:
				return "<";
			case GREATEREQUAL:
				return ">=";
			case LESSEQUAL:
				return "<=";
			case INSTANCEOF:
				return " instanceof ";
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
		if("==".equals(operator))
		{
			return EQUAL;
		}
		else if("!=".equals(operator))
		{
			return UNEQUAL;
		}
		else if(">".equals(operator))
		{
			return GREATER;
		}
		else if("<".equals(operator))
		{
			return LESS;
		}
		else if(">=".equals(operator))
		{
			return GREATEREQUAL;
		}
		else if("<=".equals(operator))
		{
			return LESSEQUAL;
		}
		else if("instanceof".equals(operator))
		{
			return INSTANCEOF;
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
		return super.equals(o) && op==((CompareNode)o).op;
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		return super.hashCode()*31 + op;
	}
}

