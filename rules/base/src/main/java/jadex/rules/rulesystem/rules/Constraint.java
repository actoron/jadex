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
	 *  Get the variables for a value source.
	 *  @param valuesource	The value source
	 *  @return The variables.
	 */
	public static List	getVariablesForValueSource(Object valuesource)
	{
		List	ret;
		if(valuesource instanceof Object[])
		{
			ret	= new ArrayList();
			Object[]	srcs	= (Object[]) valuesource;
			for(int i=0; i<srcs.length; i++)
			{
				ret.addAll(getVariablesForValueSource(srcs[i]));
			}
		}
		else if(valuesource instanceof List)
		{
			ret	= new ArrayList();
			List	srcs	= (List) valuesource;
			for(int i=0; i<srcs.size(); i++)
			{
				ret.addAll(getVariablesForValueSource(srcs.get(i)));
			}
		}
		else if(valuesource instanceof MethodCall)
		{
			ret	= ((MethodCall)valuesource).getVariables();
		}
		else if(valuesource instanceof Variable)
		{
			ret = Collections.singletonList(valuesource);
		}
		else if(valuesource instanceof FunctionCall)
		{
			ret	= ((FunctionCall)valuesource).getVariables();
		}
		else
		{
			ret	= Collections.EMPTY_LIST;
		}
		return ret;
	}
}
