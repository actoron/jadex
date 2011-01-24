package jadex.base.service.remote.commands;

import jadex.base.service.remote.ExceptionInfo;
import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.RemoteMethodInvocationHandler;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.micro.IMicroExternalAccess;

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
//		System.out.println("result command: "+result);
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
	public IFuture execute(IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
//		if(callid.equals(RemoteMethodInvocationHandler.debugcallid))
//			System.out.println("debuggcallid");
		
		Future future = (Future)rsms.getWaitingCall(callid);
		
		if(future==null)
		{
			// NOP, ignore invocation results that arrive late.
//			System.out.println("Unexpected result, no outstanding call for:" +callid);
		}
		else //if(!future.isDone())
		{
			if(exceptioninfo!=null)
			{
				future.setExceptionIfUndone(exceptioninfo.recreateException());
			}
			else
			{
//				System.out.println("resu: "+result);
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
