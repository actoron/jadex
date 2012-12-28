package jadex.base.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.IMicroExternalAccess;
import jadex.platform.service.remote.RemoteServiceManagementService;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

import java.util.Map;

/**
 * 
 */
public class RemoteIntermediateResultCommand extends RemoteResultCommand
{
	/** The flag if is finished. */
	protected boolean finished;
	
	/** The original future (not transmitted). */
	protected IFuture<?>	orig;
	
	/**
	 *  Create a new remote intermediate result command.
	 */
	public RemoteIntermediateResultCommand()
	{
	}

//	/**
//	 *  Create a new remote intermediate result command.
//	 */
//	public RemoteIntermediateResultCommand(IComponentIdentifier realreceiver, Object result, String callid, boolean isref)
//	{
//		this(realreceiver, result, callid, isref, null, false);
//	}
	
	/**
	 *  Create a new remote intermediate result command.
	 */
	public RemoteIntermediateResultCommand(IComponentIdentifier realreceiver, Object result, String callid, boolean isref, 
		String methodname, boolean finished, Map<String, Object> nonfunc, IFuture<?> orig)
	{
		super(realreceiver, result, null, callid, isref, methodname, nonfunc);
		this.finished = finished;
		this.orig	= orig;
	}
	
	/**
	 *  Get the original future.
	 */
	public IFuture<?>	getOriginalFuture()
	{
		return orig;
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
//		if(callid.equals(RemoteMethodInvocationHandler.debugcallid))
//			System.out.println("debuggcallid");
		
		WaitingCallInfo wci = rsms.getWaitingCall(callid);
		
//		Object call = rsms.interestingcalls.remove(callid);
//		if(call!=null)
//			System.out.println("here");
		
//		if(wci==null)
//		{
//			// NOP, ignore invocation results that arrive late.
////			System.out.println("Unexpected result, no outstanding call for:" +callid);
//		}
//		else //if(!future.isDone())
		if(wci!=null)
		{
			wci.refresh();
			
			IntermediateFuture future = (IntermediateFuture)wci.getFuture();
			if(finished)
			{
				future.setFinished();
			}
			else
			{
				future.addIntermediateResult(result);
			}
		}
		
		return IIntermediateFuture.DONE;
	}
	
	/**
	 *  Get the finished.
	 *  @return the finished.
	 */
	public boolean isFinished()
	{
		return finished;
	}

	/**
	 *  Set the finished.
	 *  @param finished The finished to set.
	 */
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}

	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"(result="+result+", callid="+callid+", finished="+finished+")";
	}
}
