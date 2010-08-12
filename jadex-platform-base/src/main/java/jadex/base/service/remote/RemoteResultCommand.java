package jadex.base.service.remote;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.micro.IMicroExternalAccess;

import java.util.Map;

/**
 *  Command that represents the result(s) of a remote command.
 *  Notifies the caller about the result.
 */
public class RemoteResultCommand implements IRemoteCommand
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;

	/** The exception. */
	protected ExceptionInfo exceptioninfo;
	
	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public RemoteResultCommand()
	{
	}

	/**
	 *  Create a new remote result command.
	 */
	public RemoteResultCommand(Object result, Exception exception, String callid)
	{
		this.result = result;
		this.exceptioninfo = exception!=null? new ExceptionInfo(exception): null;
		this.callid = callid;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(IMicroExternalAccess component, Map waitingcalls)
	{
		Future future = (Future)waitingcalls.get(callid);
		
		if(future==null)
		{
			System.out.println("Unexpected result, no outstanding call for:" +callid);
		}
		else //if(!future.isDone())
		{
			if(exceptioninfo!=null)
			{
				future.setExceptionIfUndone(exceptioninfo.recreateException());
			}
			else
			{
				future.setResultIfUndone(result);
			}
		}
		
		return new Future(null);
	}
	
	//-------- getter/setter methods --------
	
	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the exception.
	 *  @return the exception.
	 */
	public ExceptionInfo getExceptionInfo()
	{
		return exceptioninfo;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setExceptionInfo(ExceptionInfo exception)
	{
		this.exceptioninfo = exception;
	}
	
	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
	}
	
	
}
