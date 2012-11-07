package jadex.rules.eca;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class CommandAction<T> implements IAction<T>
{
	/** The command. */
	protected IResultCommand<IFuture<T>, Tuple3<IEvent, IRule<?>, Object>> command;
	
	/**
	 * 
	 */
	public CommandAction(IResultCommand<IFuture<T>, Tuple3<IEvent, IRule<?>, Object>> command)
	{
		this.command = command;
	}

	/**
	 * 
	 */
	public IFuture<T> execute(IEvent event, IRule<T> rule, Object context)
	{
		try
		{
			return command.execute(new Tuple3<IEvent, IRule<?>, Object>(event, rule, context));
		}
		catch(Exception e)
		{
			return new Future<T>(e);
//			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the command.
	 *  @return The command.
	 */
	public IResultCommand<IFuture<T>, Tuple3<IEvent, IRule<?>, Object>> getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The command to set.
	 */
	public void setCommand(IResultCommand<IFuture<T>, Tuple3<IEvent, IRule<?>, Object>> command)
	{
		this.command = command;
	}
}
