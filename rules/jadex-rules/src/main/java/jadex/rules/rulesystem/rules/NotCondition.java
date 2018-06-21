package jadex.rules.rulesystem.rules;

import java.util.Collections;
import java.util.List;

import jadex.rules.rulesystem.ICondition;

/**
 *  Condition for negating another condition. 
 */
public class NotCondition implements ICondition
{
	//-------- attributes --------
	
	/** The negated condition. */
	protected ICondition cond;
	
	//-------- constructors --------
	
	/**
	 *  Create a new not condition.
	 */
	public NotCondition(ICondition cond)
	{
		this.cond = cond;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public ICondition getCondition()
	{
		return cond;
	}
	
	/**
	 *  Get all variables.
	 */
	public List getVariables()
	{
		return Collections.EMPTY_LIST;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "(not "+cond.toString()+")";
	}
}
