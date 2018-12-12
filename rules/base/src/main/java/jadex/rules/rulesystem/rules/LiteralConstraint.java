package jadex.rules.rulesystem.rules;

import java.util.List;


/**
 *  A literal contraint assures that an object field/method has the
 *  specified (return) value.
 *  LiteralConstraint assures that (slot|method(|var) <op> <value>)
 */
public class LiteralConstraint extends Constraint
{
	//-------- attributes --------
	
	/** The value to test against. */
	protected Object value;

	//-------- constructors --------
	
	/**
	 *  Create a new literal constraint.
	 */
	public LiteralConstraint(Object valuesource, Object value)
	{
		this(valuesource, value, IOperator.EQUAL);
	}
	
	/**
	 *  Create a new literal constraint.
	 */
	public LiteralConstraint(Object valuesource, Object value, IOperator operator)
	{
		super(valuesource, operator);
		this.value = value;
	}

	//-------- methods --------
	
	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		return getVariablesForValueSource(getValueSource());
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "("+getValueSource()+getOperator()+value+")";
	}
}
