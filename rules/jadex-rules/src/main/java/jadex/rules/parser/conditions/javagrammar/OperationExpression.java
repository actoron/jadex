package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;


/**
 *  An operation composes two values.
 */
public class OperationExpression	extends Expression
{
	//-------- constants --------
	
	/** The OR operator (||). */
	public static final IOperator	OPERATOR_OR	= new IOperator()
	{
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			return ((Boolean)(val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1)).booleanValue() 
				|| ((Boolean)(val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2)).booleanValue();
		}

		public String toString()
		{
			return "||";
		}
	};
	
	/** The AND operator (&&). */
	public static final IOperator	OPERATOR_AND	= new IOperator()
	{
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			return ((Boolean)(val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1)).booleanValue() 
				&& ((Boolean)(val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2)).booleanValue();
		}

		public String toString()
		{
			return "&&";
		}
	};
	
	//-------- attributes --------
	
	/** The left value expression. */
	protected Expression	left;

	/** The right value expression. */
	protected Expression	right;
	
	/** The operator. */
	protected Object	operator;

	//-------- constructors --------
	
	/**
	 *  Create a new operation.
	 */
	public OperationExpression(Expression left, Expression right, IOperator operator)
	{
		this.left	= left;
		this.right	= right;
		this.operator	= operator;
	}
	
	/**
	 *  Create a new operation.
	 */
	public OperationExpression(Expression left, Expression right, IFunction operator)
	{
		this.left	= left;
		this.right	= right;
		this.operator	= operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return left.containsVariable(var)
			|| right.containsVariable(var);
	}
	
	/**
	 *  Get the left value.
	 */
	public Expression	getLeftValue()
	{
		return this.left;
	}
	
	/**
	 *  Get the right value.
	 */
	public Expression	getRightValue()
	{
		return this.right;
	}
	
	/**
	 *  Get the operator.
	 */
	public Object	getOperator()
	{
		return this.operator;
	}
	
	/**
	 *  Get a string representation of this operation.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(getLeftValue().toString());
		ret.append(" ");
		ret.append(getOperator());
		ret.append(" ");
		ret.append(getRightValue().toString());
		return ret.toString();
	}

	/**
	 *  Test if this operation is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof OperationExpression
			&& ((OperationExpression)o).getLeftValue().equals(getLeftValue())
			&& ((OperationExpression)o).getRightValue().equals(getRightValue())
			&& ((OperationExpression)o).getOperator().equals(getOperator());
	}
	
	/**
	 *  Get the hash code of this operation.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getLeftValue().hashCode();
		ret	= 31*ret + getRightValue().hashCode();
		ret	= 31*ret + getOperator().hashCode();
		return ret;
	}

	/**
	 *  Get the inverse operator.
	 *  @param operator	The operator
	 *  @return	The inverse operator or null if none exists.
	 */
	public static IOperator getInverseOperator0(IOperator operator)
	{
		IOperator	ret	= null;
		if(operator.equals(IOperator.CONTAINS))
			ret	= IOperator.EXCLUDES;
		else if(operator.equals(IOperator.EQUAL))
			ret	= IOperator.NOTEQUAL;
		else if(operator.equals(IOperator.EXCLUDES))
			ret	= IOperator.CONTAINS;
		else if(operator.equals(IOperator.GREATER))
			ret	= IOperator.LESSOREQUAL;
		else if(operator.equals(IOperator.GREATEROREQUAL))
			ret	= IOperator.LESS;
//		else if(operator.equals(IOperator.INSTANCEOF))
//			ret	= ???
		else if(operator.equals(IOperator.LESS))
			ret	= IOperator.GREATEROREQUAL;
		else if(operator.equals(IOperator.LESSOREQUAL))
			ret	= IOperator.GREATER;
//		else if(operator.equals(IOperator.MATCHES))
//			ret	= ???
		else if(operator.equals(IOperator.NOTEQUAL))
			ret	= IOperator.EQUAL;
//		else if(operator.equals(IOperator.STARTSWITH))
//			ret	= IOperator.???
//		else if(operator.equals(OPERATOR_AND))
//			ret	= ???
//		else if(operator.equals(OPERATOR_OR))
//			ret	= ???
		return ret;
	}
}
