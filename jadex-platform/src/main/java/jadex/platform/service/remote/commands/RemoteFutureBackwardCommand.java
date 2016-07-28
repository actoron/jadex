package jadex.platform.service.remote.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteServiceManagementService;

/**
 *  Command for sending back an info to the src of a future.
 *  
 *  Fetches the future of the call via the callid
 *  and forwards the info on it.
 */
public class RemoteFutureBackwardCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The call identifier. */
	protected String callid;
	
	/** The call identifier to terminate. */
	protected String cmdcallid;
	
	/** The exception. */
	protected Object info;
		
	//-------- constructors --------
	
	/**
	 *  Create a new remote method invocation command.
	 */
	public RemoteFutureBackwardCommand()
	{
	}
	
	/**
	 *  Create a new remote method invocation command. 
	 */
	public RemoteFutureBackwardCommand(String callid, String terminatecallid, Object info)
	{
		this.callid = callid;
		this.cmdcallid = terminatecallid;
		this.info = info;
//		System.out.println("rmi on client: "+callid+" "+methodname);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String getSecurityLevel()
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
		Object tfut = rsms.getProcessingCall(cmdcallid);
		if(tfut instanceof ITerminableFuture)
		{
			// directly invoke terminate when call has already been received
//			System.out.println("terminating remote future: "+tfut.hashCode());
			((ITerminableFuture<?>)tfut).sendBackwardCommand(info);
		}
		else if(tfut==null)
		{
//			System.out.println("remote future not found");
			// store as command if not already received
			rsms.addFutureCommand(cmdcallid, new Runnable()
			{
				public void run()
				{
					Object tfut = rsms.getProcessingCall(cmdcallid);
					if(tfut!=null)
					{
//						System.out.println("terminated future afterwards");
						((ITerminableFuture<?>)tfut).sendBackwardCommand(info);
					}
				}
			});
		}
		else
		{
			System.err.println("Cannot send backward command to incompatible future: "+tfut+", "+callid+", "+cmdcallid+", "+info);
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

	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public Object getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(Object info)
	{
		this.info = info;
	}

	/**
	 *  Get the cmdcallid.
	 *  @return The cmdcallid.
	 */
	public String getCmdCallid()
	{
		return cmdcallid;
	}

	/**
	 *  Set the cmdcallid.
	 *  @param cmdcallid The cmdcallid to set.
	 */
	public void setCmdCallid(String cmdcallid)
	{
		this.cmdcallid = cmdcallid;
	}
}

