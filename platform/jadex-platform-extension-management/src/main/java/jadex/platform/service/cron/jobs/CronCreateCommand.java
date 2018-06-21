package jadex.platform.service.cron.jobs;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The create command is used to create a component via the cms.
 */
public class CronCreateCommand implements IResultCommand<ISubscriptionIntermediateFuture<CMSStatusEvent>, Tuple2<IInternalAccess, Long>>
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
	public CronCreateCommand(String name, String model, CreationInfo info)
//		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this(new CreateCommand(name, model, info));
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> execute(final Tuple2<IInternalAccess, Long> args)
	{
		return command.execute(args.getFirstEntity());
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