package jadex.rules.eca.annotations;

import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;

/**
 * 
 */
public class CombinedCondition implements ICondition
{
	/** The conditions. */
	protected ICondition[] conditions;
	
	/**
	 *  Create a new CombinedCondition.
	 */
	public CombinedCondition(ICondition[] conditions)
	{
		this.conditions = conditions;
	}
	
	/**
	 * 
	 */
	public boolean evaluate(IEvent event)
	{
		boolean ret = true;
		
		for(int i=0; ret && i<conditions.length; i++)
		{
			ret = conditions[i].evaluate(event);
		}
		
		return ret;
	}
}
