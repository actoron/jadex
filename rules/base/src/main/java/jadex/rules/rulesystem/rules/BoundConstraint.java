package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *  A BoundConstraint assures that (<attr | method> <op> var)
 */
public class BoundConstraint extends Constraint
{
    //-------- attributes --------
	
	/** The variable to bind/test against. */
	protected List vars;
	
	/** The variables, from which the constraint depends
	 * (might be additional variables from value source). */
	protected List depvars;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bound constraint.
	 */
	public BoundConstraint(Object valuesource, Variable var)
	{
		this(valuesource, Collections.singletonList(var), IOperator.EQUAL);
	}
	
	/**
	 *  Create a new bound constraint.
	 */
	public BoundConstraint(Object valuesource, Variable var, IOperator operator)
	{
		this(valuesource, Collections.singletonList(var), operator);
	}
	
	/**
	 *  Create a new bound constraint.
	 */
	public BoundConstraint(Object valuesource, List vars, IOperator operator)
	{
		super(valuesource, operator);
		for(Iterator it=vars.iterator(); it.hasNext(); )
			if(it.next()==null)
				throw new NullPointerException();
		this.vars = vars;
	}

	//-------- methods --------
	
	/**
	 *  Test if the constraint is a multi constraint.
	 *  @return True, if multi constraint.
	 */
	public boolean isMultiConstraint()
	{
		return vars.size()>1;
	}
	
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		if(depvars==null)
		{
			List	vfvs	= getVariablesForValueSource(getValueSource());
			if(vfvs.isEmpty())
			{
				depvars	= vars;
			}
			else if(!vars.isEmpty())
			{
				depvars	= new ArrayList(vfvs);
				depvars.addAll(vars);
			}
			else
			{
				depvars	= Collections.EMPTY_LIST;
			}
		}
		return depvars;
	}
	
	
	/**
	 *  Get the variables to be bound.
	 *  @return The variables to bind.
	 */
	public List getBindVariables()
	{
		return vars;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append("(");
		sbuf.append(getValueSource());
		sbuf.append(getOperator());
		if(vars.size()>1)
		{
			sbuf.append("(");
			for(int i=0; i<vars.size(); i++)
			{
				sbuf.append(vars.get(i));
			}
			sbuf.append(")");
		}
		else
		{
			sbuf.append(vars.get(0));			
		}
			
		sbuf.append(")");
		return sbuf.toString();
	}
}
