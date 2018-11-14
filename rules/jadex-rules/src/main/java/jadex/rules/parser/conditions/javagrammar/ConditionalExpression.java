package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;


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
		public AttributeSet getRelevantAttributes()
		{
			return AttributeSet.EMPTY_ATTRIBUTESET;
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
	 *  Test if a variable is contained in the expression.
	 *  @param var	The variable.
	 *  @return	True, when the variable is contained.
	 */
	public boolean	containsVariable(Variable var)
	{
		return condition.containsVariable(var)
			|| first.containsVariable(var)
			|| second.containsVariable(var);
	}
	
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
