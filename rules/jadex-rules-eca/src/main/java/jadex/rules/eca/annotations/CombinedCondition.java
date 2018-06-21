package jadex.rules.eca.annotations;

import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
	public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
	{
		return doEvaluate(0, event);
//		Tuple2<Boolean, Object> ret = ICondition.TRUE;
//		
//		for(int i=0; ret.getFirstEntity().booleanValue() && i<conditions.length; i++)
//		{
//			ret = conditions[i].evaluate(event);
//		}
//		
//		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Tuple2<Boolean, Object>> doEvaluate(final int i, final IEvent event)
	{
		final Future<Tuple2<Boolean, Object>> ret = new Future<Tuple2<Boolean,Object>>();
		if(i<conditions.length)
		{
			conditions[i].evaluate(event).addResultListener(new DelegationResultListener<Tuple2<Boolean,Object>>(ret)
			{
				public void customResultAvailable(Tuple2<Boolean, Object> result)
				{
					if(result.getFirstEntity().booleanValue())
					{
						if(i+1<conditions.length)
						{
							doEvaluate(i+1, event).addResultListener(new DelegationResultListener<Tuple2<Boolean,Object>>(ret));
						}
						else
						{
							ret.setResult(result);
						}
					}
					else
					{
						ret.setResult(result);
					}
				}
			});
		}
		return ret;
	}
}
