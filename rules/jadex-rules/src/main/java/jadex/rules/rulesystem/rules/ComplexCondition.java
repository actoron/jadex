package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.ICondition;

/**
 *  A condition consists of 1..n base conditions that are logically combined.
 */
public class ComplexCondition implements ICondition
{
	//-------- attributes --------
	
	/** The rules. */
	protected List conditions;
	
	//-------- constructors --------
	
	/**
	 *  Create a new complex condition.
	 */
	public ComplexCondition()
	{
	}
	
	/**
	 *  Create a new complex condition.
	 */
	public ComplexCondition(List conditions)
	{
		this.conditions = conditions;
	}
	
	/**
	 *  Create a new complex condition.
	 */
	public ComplexCondition(ICondition[] conditions)
	{
		this.conditions = new ArrayList();
		for(int i=0; i<conditions.length; i++)
			this.conditions.add(conditions[i]);
	}

	//-------- methods --------
	
	/**
	 *  Get the conditions.
	 *  @return The conditions.
	 */
	public List getConditions()
	{
		return conditions;
	}
	
	/**
	 *  Add a new condition.
	 *  @param cond The condition.
	 */
	public void addCondition(ICondition cond)
	{
		conditions.add(cond);
	}
	
	/**
	 *  Get all variables.
	 *  @return The variables.
	 */
	public List getVariables()
	{
		List vars = new ArrayList();
		for(int i=0; i<conditions.size(); i++)
		{
			List cvars = ((ICondition)conditions.get(i)).getVariables();
			if(cvars!=null)
			{
				for(int j=0; j<cvars.size(); j++)
				{
					if(!vars.contains(cvars.get(j)))
						vars.add(cvars.get(j));
				}
			}
		}
		return vars;
	}
}
