package jadex.platform.service.cron.jobs;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.ICommand;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;

/**
 *  The create command is used to create a component via the cms.
 */
public class CronCreateCommand implements ICommand<Tuple2<IInternalAccess, Long>>
{
	/** The name. */
	protected CreateCommand command;
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public CronCreateCommand(CreateCommand command)
	{
		this.command = command;
	}
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public CronCreateCommand(String name, String model, CreationInfo info,
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this(new CreateCommand(name, model, info, resultlistener));
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public void execute(final Tuple2<IInternalAccess, Long> args)
	{
		command.execute(args.getFirstEntity());
	}

	/**
	 *  Get the command.
	 *  @return The command.
	 */
	public CreateCommand getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The command to set.
	 */
	public void setCommand(CreateCommand command)
	{
		this.command = command;
	}
}