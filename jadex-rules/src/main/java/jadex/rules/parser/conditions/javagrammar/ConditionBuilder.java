package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  The condition builder takes a set of constraints
 *  (e.g. from the parser) and generates appropriate
 *  object conditions for it.
 */
public class ConditionBuilder
{
	/**
	 *  Build or adapt conditions for representing the given constraints.
	 *  @param constraints	The constraints to represent.
	 *  @param condition	Predefined condition that can be used if necessary.
	 *  @return The generated condition.
	 */
	public static ICondition	buildCondition(Constraint[] constraints, ICondition condition)
	{
		// Unfold AND conditions.
		List	lcons	= new ArrayList();
		lcons.add(condition);
		for(int i=0; i<lcons.size(); i++)
		{
			if(lcons.get(i) instanceof AndCondition)
			{
				lcons.remove(i);
				lcons.addAll(i, ((AndCondition)condition).getConditions());
				i--;
			}
		}
		
		Map	bcons	= buildConditionMap(lcons);
		
		for(int i=0; i<constraints.length; i++)
		{
			// Get object condition and value source left part.
			Object[]	left	= getObjectConditionAndValueSource(constraints[i].getLeftValue(), lcons, bcons);
			ObjectCondition	ocon	= (ObjectCondition)left[0];
			Object	valuesource	= left[1];

			// Get literal or variable for right part.
			Object	right	= flattenToPrimary(constraints[i].getRightValue(), lcons, bcons);
			
			if(right instanceof Variable)
			{
				ocon.addConstraint(new BoundConstraint(valuesource, (Variable)right, getOperator(constraints[i].getOperator())));
			}
			else // Right is constant value
			{
				ocon.addConstraint(new LiteralConstraint(valuesource, right, getOperator(constraints[i].getOperator())));
			}
		}

		return lcons.size()>1 ? new AndCondition(lcons) : (ICondition)lcons.get(0);
	}
	
	/**
	 *  Find or create an object condition for a value and
	 *  also return the appropriate value source.
	 *  @param value	The value to be obtained.
	 *  @param lcons	The existing conditions.
	 *  @param bcons	The conditions for existing variables.
	 *  @return	A tuple containing the object conditions and the remaining value source.
	 */
	protected static Object[] getObjectConditionAndValueSource(UnaryExpression value, List lcons, Map bcons)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  Flatten a value to a primary value (literal or variable).
	 *  For a complex expression, additional conditions might be created
	 *  that store the value in a variable. 
	 *  @param value	The value to be obtained.
	 *  @param lcons	The existing conditions.
	 *  @param bcons	The conditions for existing variables.
	 *  @return	The primary value (i.e. variable or literal).
	 */
	protected static Object flattenToPrimary(UnaryExpression rightValue, List lcons, Map bcons)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  Build a map of conditions by variable.
	 */
	protected static Map	buildConditionMap(List lcons)
	{
		// Find conditions, bound to specific variables.
		Map	bcons	= new HashMap();
		for(int i=0; i<lcons.size(); i++)
		{
			List	bcs	= null;
			if(lcons.get(i) instanceof ObjectCondition)
			{
				bcs	= ((ObjectCondition)lcons.get(i)).getBoundConstraints();
			}
			else if(lcons.get(i) instanceof CollectCondition)
			{
				bcs	= ((CollectCondition)lcons.get(i)).getBoundConstraints();
			}
			
			for(int j=0; bcs!=null && j<bcs.size(); j++)
			{
				BoundConstraint	bc	= (BoundConstraint)bcs.get(j);
				if(bc.getValueSource()==null && bc.getOperator().equals(IOperator.EQUAL))
				{
					List	bvars	= bc.getBindVariables();
					for(int k=0; k<bvars.size(); k++)
					{
						bcons.put(bvars.get(k), lcons.get(i));
					}
				}
			}
		}
		
		return bcons;
	}

	/**
	 *  Get the operator object for a string.
	 */
	protected static IOperator	getOperator(String operator)
	{
		IOperator	ret;
		if("==".equals(operator))
			ret	= IOperator.EQUAL;
		else if("!=".equals(operator))
			ret	= IOperator.NOTEQUAL;
		else if(">".equals(operator))
			ret	= IOperator.GREATER;
		else if(">=".equals(operator))
			ret	= IOperator.GREATEROREQUAL;
		else if("<".equals(operator))
			ret	= IOperator.LESS;
		else if("<=".equals(operator))
			ret	= IOperator.LESSOREQUAL;
		else
			throw new RuntimeException("Unknown operator '"+operator+"'.");
		
		return ret;
	}
}
