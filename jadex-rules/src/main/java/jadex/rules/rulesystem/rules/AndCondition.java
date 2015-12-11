package jadex.rules.rulesystem.rules;

import java.util.List;

import jadex.rules.rulesystem.ICondition;

/**
 *  Condition for and-ing contained conditions.
 */
public class AndCondition extends ComplexCondition
{
	//-------- constructors --------
	
	/**
	 *  Create a new and condition.
	 */
	public AndCondition()
	{
		super();
	}
	
	/**
	 *  Create a new and condition.
	 */
	public AndCondition(List conditions)
	{
		super(conditions);
	}
	
	/**
	 *  Create a new and condition.
	 */
	public AndCondition(ICondition[] conditions)
	{
		super(conditions);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer("(and\n");
		for(int i=0; i<conditions.size(); i++)
			ret.append(conditions.get(i).toString()+"\n");
		ret.append(")");
		return ret.toString();
	}
	
}
