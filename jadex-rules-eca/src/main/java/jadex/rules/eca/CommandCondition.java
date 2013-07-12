package jadex.rules.eca;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;

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
	public Tuple2<Boolean, Object> evaluate(IEvent event)
	{
		Tuple2<Boolean, Object> ret;
		Object res = command.execute(event).booleanValue();
		if(res instanceof Tuple2)
		{
			ret = (Tuple2<Boolean, Object>)res;
		}
		else
		{
			boolean bs = ((Boolean)res).booleanValue();
			ret = bs? ICondition.TRUE: ICondition.FALSE;
		}
		return ret;
	}
}
