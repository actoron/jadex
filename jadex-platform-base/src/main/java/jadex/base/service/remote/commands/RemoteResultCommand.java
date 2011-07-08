package jadex.base.service.remote.commands;

import jadex.base.service.remote.ExceptionInfo;
import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.RemoteReferenceModule;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.writer.WriteContext;

/**
 *  Command that represents the result(s) of a remote command.
 *  Notifies the caller about the result.
 */
public class RemoteResultCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;

	/** The exception. */
	protected ExceptionInfo exceptioninfo;
	
	/** The callid. */
	protected String callid;
	
	/** The falg if result is declared as reference. */
	protected boolean isref;
	
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
	public RemoteResultCommand(Object result, Exception exception, String callid, boolean isref)
	{
//		System.out.println("result command: "+result+" "+callid);
		this.result = result;
		this.exceptioninfo = exception!=null? new ExceptionInfo(exception): null;
		this.callid = callid;
		this.isref = isref;
		
		if(isref)
			System.out.println("hhhhu");
	}
	
	//-------- methods --------
	
	/**
	 *  Preprocess command and replace  if they are remote references.
	 */
	public void preprocessCommand(RemoteReferenceModule rrm, IComponentIdentifier target)
	{
		if(result!=null)
		{
			if(isref || SServiceProvider.isRemoteReference(result))
			{
				RMIPreProcessor preproc = new RMIPreProcessor(rrm);
				WriteContext context = new WriteContext(null, target, null, null);
				result = preproc.preProcess(context, result);
			}
		}
	}
	
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
		
//		Object call = rsms.interestingcalls.remove(callid);
//		if(call!=null)
//			System.out.println("here");
		
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
		
		return IFuture.DONE;
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
