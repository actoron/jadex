package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/** Command for results. */
public class ResultCommand<T> implements IRemoteCommand<T>
{
	/** The result. */
	protected T result;

	/** The exception. */
	protected Exception exception;
	
	/**
	 *  Create the command.
	 */
	public ResultCommand()
	{
	}
	
	/**
	 *  Create the command.
	 */
	public ResultCommand(T result)
	{
		this.result = result;
	}
	
	/**
	 *  Create the command.
	 */
	public ResultCommand(Exception exception)
	{
		this.exception = exception;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active command.
	 *  @param secinf The established security level to decide if the command is allowed.
	 *  @return A return value to be sent back.
	 */
	public IFuture<T> execute(IInternalAccess access, IFuture<T> future, IMsgSecurityInfos secinf)
	{
		if (exception!=null)
			((Future<T>) future).setException(exception);
		else
			((Future<T>) future).setResult(result);
		return future;
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
	
}
