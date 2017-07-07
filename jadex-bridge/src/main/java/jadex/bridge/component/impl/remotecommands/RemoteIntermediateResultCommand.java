package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteConversationCommand;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateFuture;

/**
 * Command for intermediate results.
 */
public class RemoteIntermediateResultCommand<T>	extends AbstractInternalRemoteCommand	implements IRemoteConversationCommand<Collection<T>>
{
	/** The result. */
	protected T result;
	
	/**
	 *  Create the command.
	 */
	public RemoteIntermediateResultCommand()
	{
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteIntermediateResultCommand(T result, Map<String, Object> nonfunc)
	{
		super(nonfunc);
		this.result = result;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<Collection<T>> future, IMsgSecurityInfos secinf)
	{
		((IntermediateFuture<T>)future).addIntermediateResult(result);
	}
	
	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public T getIntermediateResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setIntermediateResult(T result)
	{
		this.result = result;
	}
}
