package jadex.platform.service.remote.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteServiceManagementService;

/**
 *  Command for executing a remote method.
 */
public class RemoteFuturePullCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The call identifier. */
	protected String callid;
	
	/** The call identifier to pull. */
	protected String pullcallid;
	
	/** The exception. */
	protected Exception exception;
		
	//-------- constructors --------
	
	/**
	 *  Create a new remote method invocation command.
	 */
	public RemoteFuturePullCommand()
	{
	}
	
	/**
	 *  Create a new remote method invocation command. 
	 */
	public RemoteFuturePullCommand(String callid, String pullcallid)
	{
		this.callid = callid;
		this.pullcallid = pullcallid;
//		System.out.println("rmi on client: "+callid+" "+pullcallid);
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
		Object tfut = rsms.getProcessingCall(pullcallid);
		if(tfut!=null)
		{
//			System.out.println("terminating remote future: "+tfut.hashCode());
			if(tfut instanceof IPullIntermediateFuture)
			{
				((IPullIntermediateFuture<?>)tfut).pullIntermediateResult();
			}
			else
			{
				((IPullSubscriptionIntermediateFuture<?>)tfut).pullIntermediateResult();
			}
		}
		else
		{
//			System.out.println("remote future not found");
			rsms.addFutureCommand(pullcallid, new Runnable()
			{
				public void run()
				{
					Object tfut = rsms.getProcessingCall(pullcallid);
					if(tfut!=null)
					{
//						System.out.println("terminated future afterwards");
						if(tfut instanceof IPullIntermediateFuture)
						{
							((IPullIntermediateFuture<?>)tfut).pullIntermediateResult();
						}
						else
						{
							((IPullSubscriptionIntermediateFuture<?>)tfut).pullIntermediateResult();
						}
					}
				}
			});
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
	 *  Get the pullcallid.
	 *  @return The pullcallid.
	 */
	public String getPullCallId()
	{
		return pullcallid;
	}

	/**
	 *  Set the pullcallid.
	 *  @param pullcallid The pullcallid to set.
	 */
	public void setPullCallId(String pullcallid)
	{
		this.pullcallid = pullcallid;
	}
}

