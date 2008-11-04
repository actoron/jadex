package jadex.rules.rulesystem.rules;

import jadex.rules.rulesystem.ICondition;

import java.util.List;

/**
 *  Condition for or-ing contained conditions.
 */
public class OrCondition extends ComplexCondition
{
	//-------- constructors --------
	
	/**
	 *  Create a new or condition.
	 */
	public OrCondition()
	{
		super();
	}
	
	/**
	 *  Create a new or condition.
	 */
	public OrCondition(List conditions)
	{
		super(conditions);
	}
	
	/**
	 *  Create a new or condition.
	 */
	public OrCondition(ICondition[] conditions)
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
		StringBuffer ret = new StringBuffer("(or\n");
		for(int i=0; i<conditions.size(); i++)
			ret.append(conditions.get(i).toString()+"\n");
		ret.append(")");
		return ret.toString();
	}
}
