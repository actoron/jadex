package jadex.rules.eca;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Command version of the condition.
 */
public class CommandCondition implements ICondition
{
	/** The command. */
	protected IResultCommand<Boolean, IEvent> command;
	
	/**
	 * 
	 */
	public CommandCondition(IResultCommand<Boolean, IEvent> command)
	{
		this.command = command;
	}
	
	/**
	 * 
	 */
	public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
	{
		Object res = command.execute(event);
		return evaluateResult(res);
	}
	
	/**
	 * 
	 */
	public static IFuture<Tuple2<Boolean, Object>> evaluateResult(Object res)
	{
		final Future<Tuple2<Boolean, Object>> ret = new Future<Tuple2<Boolean, Object>>();
		if(res==null)
		{
			ret.setResult(new Tuple2<Boolean, Object>(Boolean.FALSE, null));
		}
		else if(res instanceof Tuple2)
		{
			ret.setResult((Tuple2<Boolean, Object>)res);
		}
		else if(res instanceof Boolean)
		{
			boolean bs = ((Boolean)res).booleanValue();
			ret.setResult(bs? ICondition.TRUE: ICondition.FALSE);
		}
		else if(res instanceof Future)
		{
			((Future<Object>)res).addResultListener(new ExceptionDelegationResultListener<Object, Tuple2<Boolean, Object>>(ret)
			{
				public void customResultAvailable(Object res)
				{
					evaluateResult(res).addResultListener(new DelegationResultListener<Tuple2<Boolean,Object>>(ret));
				}
			});
		}
		else
		{
			ret.setResult(new Tuple2<Boolean, Object>(Boolean.TRUE, res));
		}
		return ret;
	}
}
