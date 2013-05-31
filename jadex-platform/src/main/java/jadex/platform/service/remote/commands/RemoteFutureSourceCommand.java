package jadex.platform.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.future.ICommandFuture.Type;
import jadex.commons.future.ICommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.micro.IMicroExternalAccess;
import jadex.platform.service.remote.RemoteServiceManagementService;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

import java.util.Map;

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
	public RemoteFutureSourceCommand(IComponentIdentifier realreceiver, Type cmd, String callid, boolean isref, 
		String methodname, Map<String, Object> nonfunc)
	{
		super(realreceiver, cmd, null, callid, isref, methodname, nonfunc);
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture execute(IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
//		System.out.println("intermediate result command: "+result+" "+exceptioninfo+" "+callid);
		
		if(ICommandFuture.Type.UPDATETIMER.equals(getResult()))
		{
			WaitingCallInfo wci = rsms.getWaitingCall(callid);
			
			if(wci!=null)
			{
				System.out.println("remote timer refresh");
				wci.refresh();
				IFuture<?> fut = wci.getFuture();
				if(fut instanceof ICommandFuture)
				{
					((ICommandFuture)fut).sendCommand((Type)getResult());
				}
			}
		}
		else
		{
			System.out.println("Unknown command: "+getResult());
		}
		
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
