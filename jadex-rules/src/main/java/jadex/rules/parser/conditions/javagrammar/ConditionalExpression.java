package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;

import java.util.Collections;
import java.util.Set;


/**
 *  A conditional operation uses a condition to choose from two values:
 *  condition ? first_value : second_value
 */
public class ConditionalExpression	extends Expression
{
	//-------- constants --------
	
	/** A function to evaluate conditionals. */
	public static final IFunction	FUNCTION_CONDITIONAL	= new IFunction()
	{
		public Set getRelevantAttributes()
		{
			return Collections.EMPTY_SET;
		}
		public Class getReturnType()
		{
			// Todo: get most specific return type of params 1 and 2!?
			return Object.class;
		}
		public Object invoke(Object[] paramvalues, IOAVState state)
		{
			if(paramvalues==null || paramvalues.length!=3)
				throw new IllegalArgumentException("Conditional requires three parameters.");
			
			return ((Boolean)(paramvalues[0] instanceof ILazyValue? ((ILazyValue)(paramvalues[0])).getValue(): paramvalues[0])).booleanValue()
			? (paramvalues[1] instanceof ILazyValue? ((ILazyValue)(paramvalues[1])).getValue(): paramvalues[1]): 
				paramvalues[2] instanceof ILazyValue? ((ILazyValue)(paramvalues[2])).getValue(): paramvalues[2];
		}
		public String	toString()
		{
			return "?:";
		}
	};
	
	 //-------- attributes --------
	
	/** The condition. */
	protected Expression	condition;

	/** The first value expression. */
	protected Expression	first;

	/** The second value expression. */
	protected Expression	second;
	
	//-------- constructors --------
	
	/**
	 *  Create a new operation.
	 */
	public ConditionalExpression(Expression condition, Expression first, Expression second)
	{
		this.condition	= condition;
		this.first	= first;
		this.second	= second;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the condition.
	 */
	public Expression	getCondition()
	{
		return this.condition;
	}

	/**
	 *  Get the first value.
	 */
	public Expression	getFirstValue()
	{
		return this.first;
	}
	
	/**
	 *  Get the second value.
	 */
	public Expression	getSecondValue()
	{
		return this.second;
	}
		
	/**
	 *  Get a string representation of this constraint.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append(getCondition().toString());
		ret.append(" ? ");
		ret.append(getFirstValue().toString());
		ret.append(" : ");
		ret.append(getSecondValue().toString());
		return ret.toString();
	}

	/**
	 *  Test if this constraint is equal to some object.
	 */
	public boolean	equals(Object o)
	{
		return o instanceof ConditionalExpression
			&& ((ConditionalExpression)o).getCondition().equals(getCondition())
			&& ((ConditionalExpression)o).getFirstValue().equals(getFirstValue())
			&& ((ConditionalExpression)o).getSecondValue().equals(getSecondValue());
	}
	
	/**
	 *  Get the hash code of this field access.
	 */
	public int	hashCode()
	{
		int	ret	= 31 + getCondition().hashCode();
		ret	= 31*ret + getFirstValue().hashCode();
		ret	= 31*ret + getSecondValue().hashCode();
		return ret;
	}
}
