package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *  A constraint is a part of a condition. It restricts
 *  the allowed values via 
 *  - a constant test (LiteralConstraint)
 *  - a predicate test (PredicateConstraint)
 *  - a constant test via a bound variable (BoundConstraint)
 */
public abstract class Constraint implements IConstraint
{
	//-------- attributes --------
	
	/** The attribute or method. */
	protected Object valuesource;

	/** The operator. */
	protected IOperator operator;
	
	//-------- constructors --------
	
	/**
	 *  Create a new constraint.
	 */
	public Constraint(Object valuesource, IOperator operator)
	{
		this.valuesource = valuesource;
		this.operator = operator;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value source.
	 *  @return The attribute or method.
	 */
	public Object getValueSource()
	{
		return valuesource;
	}
	
	/**
	 *  Get the operator.
	 *  @return The operator.
	 */
	public IOperator getOperator()
	{
		return operator;
	}
	
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		List	ret;
		if(valuesource instanceof MethodCall)
		{
			ret	= ((MethodCall)valuesource).getVariables();
		}
		else if(valuesource instanceof Variable)
		{
			ret = new ArrayList();
			ret.add(valuesource);
		}
		else
		{
			ret	= Collections.EMPTY_LIST;
		}
		return ret;
	}
}
