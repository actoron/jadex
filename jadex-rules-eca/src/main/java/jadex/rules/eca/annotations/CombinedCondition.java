package jadex.rules.eca.annotations;

import jadex.commons.Tuple2;
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
	public Tuple2<Boolean, Object> evaluate(IEvent event)
	{
		Tuple2<Boolean, Object> ret = ICondition.TRUE;
		
		for(int i=0; ret.getFirstEntity().booleanValue() && i<conditions.length; i++)
		{
			ret = conditions[i].evaluate(event);
		}
		
		return ret;
	}
}
