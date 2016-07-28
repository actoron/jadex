package jadex.platform.service.remote.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteServiceManagementService;

/**
 *  Command for executing a remote method.
 *  
 *  Fetches the future of the call via the callid
 *  and forwards the termination on it.
 */
@Alias("jadex.base.service.remote.commands.RemoteFutureTerminationCommand")
public class RemoteFutureTerminationCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The call identifier. */
	protected String callid;
	
	/** The call identifier to terminate. */
	protected String terminatecallid;
	
	/** The exception. */
	protected Exception exception;
		
	//-------- constructors --------
	
	/**
	 *  Create a new remote method invocation command.
	 */
	public RemoteFutureTerminationCommand()
	{
	}
	
	/**
	 *  Create a new remote method invocation command. 
	 */
	public RemoteFutureTerminationCommand(String callid, String terminatecallid, Exception reason)
	{
		this.callid = callid;
		this.terminatecallid = terminatecallid;
		this.exception	= reason;
//		System.out.println("rmi on client: "+callid+" "+methodname);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		// No security issues here.
		return Security.UNRESTRICTED;
	}

	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture<IRemoteCommand> execute(IExternalAccess component, final RemoteServiceManagementService rsms)
	{
		final IntermediateFuture<IRemoteCommand> ret = new IntermediateFuture<IRemoteCommand>();
		
		// RMS acts as representative of remote caller.
//		System.out.println("callid: "+callid);
//		System.out.println("terminatecallid: "+terminatecallid);
		Object tfut = rsms.getProcessingCall(terminatecallid);
		if(tfut instanceof ITerminableFuture)
		{
			// directly invoke terminate when call has already been received
//			System.out.println("terminating remote future: "+tfut.hashCode());
			((ITerminableFuture<?>)tfut).terminate(exception);
		}
		else if(tfut==null)
		{
//			System.out.println("remote future not found");
			// store as command if not already received
			rsms.addFutureCommand(terminatecallid, new Runnable()
			{
				public void run()
				{
					Object tfut = rsms.getProcessingCall(terminatecallid);
					if(tfut!=null)
					{
//						System.out.println("terminated future afterwards");
						((ITerminableFuture<?>)tfut).terminate(exception);
					}
				}
			});
		}
		else
		{
			System.err.println("Cannot terminate incompatible future: "+tfut+", "+callid+", "+terminatecallid+", "+exception);
		}
		
		ret.addIntermediateResult(new RemoteResultCommand(null, getSender(), null, null, callid, 
			false, null, getNonFunctionalProperties()));
		ret.setFinished();
		return ret;
	}

	//-------- getter/setter methods --------

	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the call id.
	 *  @param callid The call id to set.
	 */
	public void setCallId(String callid)
	{
//		System.out.println("rmi on server: "+callid);
		this.callid = callid;
	}

//	/**
//	 *  Get the exception.
//	 *  @return the exception.
//	 */
//	public ExceptionInfo getExceptionInfo()
//	{
//		return exception;
//	}
//
//	/**
//	 *  Set the exception.
//	 *  @param exception The exception to set.
//	 */
//	public void setExceptionInfo(ExceptionInfo exception)
//	{
//		this.exception = exception;
//	}

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
	 *  Get the terminate call id.
	 *  @return the terminate call id.
	 */
	public String getTerminateCallId()
	{
		return terminatecallid;
	}

	/**
	 *  Set the terminate call id.
	 *  @param terminatecallid The terminate call id to set.
	 */
	public void setTerminateCallId(String terminatecallid)
	{
		this.terminatecallid = terminatecallid;
	}
}

