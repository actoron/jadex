package jadex.rules.eca;

import jadex.commons.IResultCommand;

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
	public boolean evaluate(IEvent event)
	{
		return command.execute(event).booleanValue();
	}
}
