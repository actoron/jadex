package jadex.platform.service.processengine;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.IResultCommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.platform.service.cron.jobs.CreateCommand;
import jadex.rules.eca.CommandAction.CommandData;

/**
 *  The create command is used to create a component via the cms.
 *  
 *  Wraps a create command in a rule engine friendly version.
 */
public class RuleCreateCommand implements IResultCommand<IFuture<Collection<CMSStatusEvent>>, CommandData>
{
	/** The name. */
	protected CreateCommand command;
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public RuleCreateCommand(CreateCommand command)
	{
		this.command = command;
	}
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public RuleCreateCommand(String name, String model, CreationInfo info)
//		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this(new CreateCommand(name, model, info));//, resultlistener));
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public IIntermediateFuture<CMSStatusEvent> execute(final CommandData args)
	{
		return command.execute((IInternalAccess)args.getContext());
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
