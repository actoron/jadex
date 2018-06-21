package jadex.rules.eca;

import jadex.commons.IResultCommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Wrapper that maps a command into a rule action.
 */
public class CommandAction<T> implements IAction<T>
{
	/** The command. */
	protected IResultCommand<IFuture<T>, CommandData> command;
	
	/**
	 * 
	 */
	public CommandAction(IResultCommand<IFuture<T>, CommandData> command)
	{
		this.command = command;
	}

	/**
	 * 
	 */
	public IFuture<T> execute(IEvent event, IRule<T> rule, Object context, Object condresult)
	{
		try
		{
			return command.execute(new CommandData(event, rule, context, condresult));
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
	public IResultCommand<IFuture<T>, CommandData> getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The command to set.
	 */
	public void setCommand(IResultCommand<IFuture<T>, CommandData> command)
	{
		this.command = command;
	}
	
	/**
	 * 
	 */
	public static class CommandData
	{
		/** The event. */
		protected IEvent event;
		
		/** The rule. */
		protected IRule<?> rule;
		
		/** The context. */
		protected Object context;
		
		/** The condition result. */
		protected Object condresult;

		/**
		 *  Create a new CommandData. 
		 */
		public CommandData(IEvent event, IRule<?> rule, Object context, Object condresult)
		{
			this.event = event;
			this.rule = rule;
			this.context = context;
			this.condresult = condresult;
		}

		/**
		 *  Get the event.
		 *  @return The event.
		 */
		public IEvent getEvent()
		{
			return event;
		}

		/**
		 *  Get the rule.
		 *  @return The rule.
		 */
		public IRule<?> getRule()
		{
			return rule;
		}

		/**
		 *  Get the context.
		 *  @return The context.
		 */
		public Object getContext()
		{
			return context;
		}

		/**
		 *  Get the condresult.
		 *  @return The condresult.
		 */
		public Object getCondresult()
		{
			return condresult;
		}
	}
}
