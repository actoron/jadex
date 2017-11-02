package jadex.bridge.component.impl.remotecommands;

import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * Command for results.
 */
public class RemoteResultCommand<T>	extends AbstractResultCommand
{
	/** The result. */
	protected T result;

	/** The exception. */
	protected Exception exception;
	
	/**
	 *  Create the command.
	 */
	public RemoteResultCommand()
	{
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteResultCommand(T result, Map<String, Object> nonfunc)
	{
		super(nonfunc);
		this.result = result;
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteResultCommand(Exception exception, Map<String, Object> nonfunc)
	{
		super(nonfunc);
		this.exception = exception;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	@SuppressWarnings("unchecked")
	public void	doExecute(IInternalAccess access, IFuture<?> future, IMsgSecurityInfos secinf)
	{
		if (exception!=null)
			((Future<T>) future).setException(exception);
		else
			((Future<T>) future).setResult(result);
	}
	
	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public T getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(T result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return "RemoteResultCommand("+(exception!=null?"ex: "+exception:result)+")";
	}
}
