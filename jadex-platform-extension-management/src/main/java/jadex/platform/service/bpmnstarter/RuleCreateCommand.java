package jadex.platform.service.bpmnstarter;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.platform.service.cron.jobs.CreateCommand;
import jadex.rules.eca.CommandAction.CommandData;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;

import java.util.Collection;

/**
 *  The create command is used to create a component via the cms.
 *  
 *  Wraps a create command in a rule engine friendly version.
 */
public class RuleCreateCommand implements IResultCommand<IFuture<IComponentIdentifier>, CommandData>
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
	public RuleCreateCommand(String name, String model, CreationInfo info,
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this(new CreateCommand(name, model, info, resultlistener));
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public IFuture<IComponentIdentifier> execute(final CommandData args)
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
