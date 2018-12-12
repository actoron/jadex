package jadex.rules.rulesystem.rules;

import java.util.List;

import jadex.rules.rulesystem.ICondition;

/**
 *  A test condition has the purpose to evaluation a predicate.
 *  It is true when the predicate result is true.
 */
public class TestCondition implements ICondition
{
	//-------- attributes --------
	
	/** The predicate constraint. */
	protected PredicateConstraint constraint;
	
	//-------- constructors --------
	
	/**
	 *  Create a new object condition.
	 */
	public TestCondition(PredicateConstraint constraint)
	{
		this.constraint = constraint;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		return constraint.getVariables();
	}

	/**
	 *  Get the constraint.
	 *  @return The constraint.
	 */
	public PredicateConstraint getConstraint()
	{
		return constraint;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer("(Test "+constraint+")");
		return ret.toString();
	}
}
