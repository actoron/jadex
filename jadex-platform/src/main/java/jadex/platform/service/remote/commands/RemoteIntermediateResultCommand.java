package jadex.platform.service.remote.commands;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.RemoteServiceManagementService;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

/**
 * 
 */
@Alias("jadex.base.service.remote.commands.RemoteIntermediateResultCommand")
public class RemoteIntermediateResultCommand extends RemoteResultCommand
{
	/** The flag if is finished. */
	protected boolean finished;
	
	/** The original future (not transmitted). */
	protected IFuture<?> orig;
	
	/** The result cnt. */
	protected int cnt;
		
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
	public RemoteIntermediateResultCommand(IComponentIdentifier sender, IComponentIdentifier realrec, Object result, String callid, boolean isref, 
		String methodname, boolean finished, Map<String, Object> nonfunc, IFuture<?> orig, int cnt)
	{
		super(sender, realrec, result, null, callid, isref, methodname, nonfunc);
//		if(result!=null && result.getClass().getName().indexOf("Bunch")!=-1)
//			System.out.println("ires com: "+result);
		this.finished = finished;
		this.orig	= orig;
		this.cnt = cnt;
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
	public IIntermediateFuture execute(IExternalAccess component, RemoteServiceManagementService rsms)
	{
//		System.out.println("intermediate result command: "+result+" "+exception+" "+callid);
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
			
			wci.addIntermediateResult(cnt, result, finished);
			
//			IntermediateFuture future = (IntermediateFuture)wci.getFuture();
//			if(finished)
//			{
//				future.setFinishedIfUndone();
//			}
//			else
//			{
//				future.addIntermediateResultIfUndone(result);
//			}
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
	 *  Get the cnt.
	 *  @return The cnt.
	 */
	public int getCount()
	{
		return cnt;
	}

	/**
	 *  Set the cnt.
	 *  @param cnt The cnt to set.
	 */
	public void setCount(int cnt)
	{
		this.cnt = cnt;
	}
	
	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"(result="+result+", callid="+callid+", finished="+finished+")";
	}
}
