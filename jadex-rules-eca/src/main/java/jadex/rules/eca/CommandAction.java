package jadex.rules.eca;

import jadex.commons.ICommand;
import jadex.commons.Tuple3;

/**
 * 
 */
public class CommandAction implements IAction
{
	/** The command. */
	protected ICommand<Tuple3<IEvent, IRule, Object>> command;
	
	/**
	 * 
	 */
	public CommandAction(ICommand<Tuple3<IEvent, IRule, Object>> command)
	{
		this.command = command;
	}

	/**
	 * 
	 */
	public void execute(IEvent event, IRule rule, Object context)
	{
		try
		{
			command.execute(new Tuple3<IEvent, IRule, Object>(event, rule, context));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the command.
	 *  @return The command.
	 */
	public ICommand<Tuple3<IEvent, IRule, Object>> getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The command to set.
	 */
	public void setCommand(ICommand<Tuple3<IEvent, IRule, Object>> command)
	{
		this.command = command;
	}
}
