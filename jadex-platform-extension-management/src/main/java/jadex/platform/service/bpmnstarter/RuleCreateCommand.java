package jadex.platform.service.bpmnstarter;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.ICommand;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.IResultListener;
import jadex.platform.service.cron.jobs.CreateCommand;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;

import java.util.Collection;

/**
 *  The create command is used to create a component via the cms.
 *  
 *  Wraps a create command in a rule engine friendly version.
 */
public class RuleCreateCommand implements ICommand<Tuple3<IEvent, IRule, Object>>
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
	public void execute(final Tuple3<IEvent, IRule, Object> args)
	{
		command.execute((IInternalAccess)args.getThirdEntity());
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
