package jadex.base.service.remote.commands;

import jadex.base.service.remote.ExceptionInfo;
import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.RemoteReferenceModule;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.base.service.remote.RemoteServiceManagementService.WaitingCallInfo;
import jadex.base.service.remote.xml.RMIPreProcessor;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
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
	
	/** The flag if result is declared as reference. */
	protected boolean isref;
	
	/** The method name. For debugging. */
	protected String methodname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote result command.
	 */
	public RemoteResultCommand()
	{
	}

	/**
	 *  Create a new remote result command.
	 */
	public RemoteResultCommand(Object result, Exception exception, String callid, boolean isref)
	{
		this(result, exception, callid, isref, null);
	}
	
	/**
	 *  Create a new remote result command.
	 */
	public RemoteResultCommand(Object result, Exception exception, String callid, boolean isref, String methodname)
	{
//		if(methodname!=null && methodname.equals("getInputStream"))
//			System.out.println("callid of getResult result: "+callid+" "+result);
		
		this.result = result;
		this.exceptioninfo = exception!=null? new ExceptionInfo(exception): null;
		this.callid = callid;
		this.isref = isref;
		this.methodname = methodname;
	}
	
	//-------- methods --------
	
	/**
	 *  Preprocess command and replace if they are remote references.
	 */
	public IFuture<Void>	preprocessCommand(IInternalAccess component, final RemoteReferenceModule rrm, final IComponentIdentifier target)
	{
		final Future<Void>	ret	= new Future<Void>();
		// Do not preprocess result commands in security service, as results are allowed by default.
//		super.preprocessCommand(component, rrm, target)
//			.addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void v)
//			{
				if(result!=null)
				{
					if(isref || rrm.getMarshalService().isRemoteReference(result))
					{
						RMIPreProcessor preproc = new RMIPreProcessor(rrm);
						WriteContext context = new WriteContext(null, new Object[]{target, null}, null, null);
						result = preproc.preProcess(context, result);
					}
				}
				ret.setResult(null);
//			}
//		});
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture execute(IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
//		System.out.println("result command: "+result+" "+exceptioninfo+" "+callid);
//		if(callid.equals(RemoteMethodInvocationHandler.debugcallid))
//			System.out.println("debuggcallid");
		
//		if(methodname!=null && methodname.equals("getInputStream"))
//			System.out.println("callid of getResult result: "+callid);
		
		WaitingCallInfo wci = rsms.getWaitingCall(callid);
		
//		Object call = rsms.interestingcalls.remove(callid);
//		if(call!=null)
//			System.out.println("here");
		
		if(wci==null)
		{
			// NOP, ignore invocation results that arrive late.
//			System.out.println("Unexpected result, no outstanding call for:" +callid);
		}
		else //if(!future.isDone())
		{
			Future future = wci.getFuture();
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
		
		return IIntermediateFuture.DONE;
	}
	
	//-------- getter/setter methods --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		// Always pass through results.
		return Security.UNRESTRICTED;
	}
	
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

	/**
	 *  Get the methodname.
	 *  @return the methodname.
	 */
	public String getMethodName()
	{
		return methodname;
	}

	/**
	 *  Set the methodname.
	 *  @param methodname The methodname to set.
	 */
	public void setMethodName(String methodname)
	{
		this.methodname = methodname;
	}
	
	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"(result="+result+", exception="+exceptioninfo+", callid="+callid+")";
	}
}
