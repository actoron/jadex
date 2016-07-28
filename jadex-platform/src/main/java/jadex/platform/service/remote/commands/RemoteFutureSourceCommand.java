package jadex.platform.service.remote.commands;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.IForwardCommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.platform.service.remote.RemoteServiceManagementService;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

/**
 * 
 */
public class RemoteFutureSourceCommand extends RemoteResultCommand
{
	/**
	 *  Create a new remote intermediate result command.
	 */
	public RemoteFutureSourceCommand()
	{
	}

	/**
	 *  Create a new remote intermediate result command.
	 */
	public RemoteFutureSourceCommand(IComponentIdentifier sender,  IComponentIdentifier realrec, Object cmd, String callid, boolean isref, 
		String methodname, Map<String, Object> nonfunc)
	{
		super(sender, realrec, cmd, null, callid, isref, methodname, nonfunc);
//		System.out.println("RFSC: "+realreceiver+", "+System.currentTimeMillis()); 
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture execute(IExternalAccess component, RemoteServiceManagementService rsms)
	{
//		System.out.println("intermediate result command: "+result+" "+exceptioninfo+" "+callid);
		
//		if(ICommandFuture.Type.UPDATETIMER.equals(getResult()))
//		{
			WaitingCallInfo wci = rsms.getWaitingCall(callid);
			
			if(wci!=null)
			{
//				System.out.println("remote timer refresh: "+System.currentTimeMillis());
				wci.refresh();
				IFuture<?> fut = wci.getFuture();
				if(fut instanceof IForwardCommandFuture)
				{
					((IForwardCommandFuture)fut).sendForwardCommand(getResult());
				}
			}
			else
			{				
				System.out.println("no waiting call to send command to: "+System.currentTimeMillis());
			}
//		}
//		else
//		{
//			System.out.println("Unknown command: "+getResult());
//		}
		
		return IIntermediateFuture.DONE;
	}
	
	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"(result="+result+", callid="+callid+")";
	}
}
