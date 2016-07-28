package jadex.platform.service.remote.commands;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Security;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.platform.service.remote.RemoteServiceManagementService;
import jadex.platform.service.remote.RemoteServiceManagementService.WaitingCallInfo;

/**
 *  Command that represents the result(s) of a remote command.
 *  Notifies the caller about the result.
 */
@Alias("jadex.base.service.remote.commands.RemoteResultCommand")
public class RemoteResultCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The sending component. */
	protected IComponentIdentifier sender;
	
	/** The eventual receiver of the command. */
	protected IComponentIdentifier realrec;
	
	/** The result. */
	protected Object result;

	/** The exception. */
	protected Exception exception;
	
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

//	/**
//	 *  Create a new remote result command.
//	 */
//	public RemoteResultCommand(IComponentIdentifier sender, Object result, Exception exception, String callid, boolean isref)
//	{
//		this(sender, result, exception, callid, isref, null);
//	}
	
	/**
	 *  Create a new remote result command.
	 */
	public RemoteResultCommand(IComponentIdentifier sender, IComponentIdentifier realrec, Object result, Exception exception, String callid, 
		boolean isref, String methodname, Map<String, Object> nonfunc)
	{
		super(nonfunc);
//		if(methodname!=null && methodname.equals("getInputStream"))
//			System.out.println("callid of getResult result: "+callid+" "+result);
		
		this.result = result;
		this.sender = sender;
		this.realrec = realrec;
		this.exception = exception;
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
		super.preprocessCommand(component, rrm, target)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void v)
			{
//				if(result!=null)
//				{
//					if(isref || rrm.getMarshalService().isRemoteReference(result))
//					{
//						RMIPreProcessor preproc = new RMIPreProcessor(rrm);
//						WriteContext context = new WriteContext(null, new Object[]{target, null}, null, null);
//						result = preproc.preProcess(context, result);
//					}
//				}
				ret.setResult(null);
			}
		});
		return ret;
//		return IFuture.DONE;
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture execute(IExternalAccess component, RemoteServiceManagementService rsms)
	{
//		System.out.println("result command: "+result+" "+exception+" "+callid);
//		if(callid.equals(RemoteMethodInvocationHandler.debugcallid))
//			System.out.println("debuggcallid");
		
		if(methodname!=null && methodname.equals("getInputStream"))
			System.out.println("callid of getResult result: "+callid);
		
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
			if(nonfunc!=null)// && wci.getContext()!=null)
			{
//				ServiceCall sc = ((ServiceInvocationContext)wci.getContext()).getServiceCall();
//				ServiceCall sc1 = ((ServiceInvocationContext)wci.getContext()).getServiceCall();
//				ServiceCall sc2 = ((ServiceInvocationContext)wci.getContext()).getLastServiceCall();
				ServiceCall sc = ServiceCall.getLastInvocation();
//				ServiceCall sc1 = ServiceCall.getCurrentInvocation();
//				ServiceCall sc2 = ServiceCall.getNextInvocation();
				for(String name: nonfunc.keySet())
				{
					sc.setProperty(name, nonfunc.get(name));
				}
			}
			
			Future future = wci.getFuture();
			if(exception!=null)
			{
				future.setExceptionIfUndone(exception);
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
//		if("getInputStream".equals(methodname))
//			System.out.println("setting methodname");
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
	
//	/**
//	 *  Get the exception.
//	 *  @return the exception.
//	 */
//	public ExceptionInfo getExceptionInfo()
//	{
//		return exceptioninfo;
//	}
//
//	/**
//	 *  Set the exception.
//	 *  @param exception The exception to set.
//	 */
//	public void setExceptionInfo(ExceptionInfo exception)
//	{
//		this.exceptioninfo = exception;
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
//		if("getInputStream".equals(methodname))
//			System.out.println("setting methodname");
		this.methodname = methodname;
	}

	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public IComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(IComponentIdentifier sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Get the eventual receiver.
	 *  @return the eventual receiver.
	 */
	public IComponentIdentifier getRealReceiver()
	{
		return realrec;
	}

	/**
	 *  Set the eventual receiver.
	 *  @param sender The eventual receiver to set.
	 */
	public void setRealReceiver(IComponentIdentifier sender)
	{
		this.realrec = sender;
	}

	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"(result="+result+", exception="+exception+", callid="+callid+")";
	}
}
