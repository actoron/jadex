package jadex.platform.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.micro.IMicroExternalAccess;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteReference;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.platform.service.remote.RemoteServiceManagementService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  Command for executing a remote method.
 */
@Alias("jadex.base.service.remote.commands.RemoteMethodInvocationCommand")
public class RemoteMethodInvocationCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The remote reference. */
	protected RemoteReference rr;
	
	/** The method. */
	// Is not transferred, only used for preprocessing
	protected Method method;
	
	/** The methodname. */
	protected String methodname;
	
	/** The parameter types. */
	protected Class<?>[] parametertypes;
	
	/** The parameter values. */
	protected Object[] parametervalues;
		
	/** The declared reference flag for the return value. */
	protected boolean returnisref;
	
	/** The call identifier. */
	protected String callid;
	
	/** The security level (set by postprocessing). */
	protected String securitylevel;
	
	/** The target object (set by postprocessing). */
	protected Object target;
	
	/** The caller. */
	protected IComponentIdentifier caller;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote method invocation command.
	 */
	public RemoteMethodInvocationCommand()
	{
	}
	
	/**
	 *  Create a new remote method invocation command. 
	 */
	public RemoteMethodInvocationCommand(RemoteReference rr, Method method, 
		Object[] parametervalues, String callid, IComponentIdentifier caller, Map<String, Object> nonfunc)
	{
		super(nonfunc);
//		if(method.getName().equals("secMethod"))
//		System.out.println("caller: "+caller);
		
		this.rr = rr;
		this.method = method;
		this.methodname = method.getName();
		this.parametertypes = method.getParameterTypes();
		this.parametervalues = parametervalues!=null? parametervalues.clone(): null;
		this.callid = callid;
		this.caller	= caller;
//		this.timeout = timeout;
//		System.out.println("rmi on client: "+callid+" "+methodname);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		return securitylevel;
	}

	/**
	 *  Preprocess command and replace if they are remote references.
	 */
	public IFuture<Void>	preprocessCommand(IInternalAccess component, final RemoteReferenceModule rrm, final IComponentIdentifier target)
	{
		
		Future<Void>	ret	= new Future<Void>();
		super.preprocessCommand(component, rrm, target)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// Do we still need this code? Is done via post processor during marshalling.
//				if(parametertypes.length>0)
//				{
//					RMIPreProcessor preproc = new RMIPreProcessor(rrm);
//					boolean[] refs = SServiceProvider.getRemoteReferenceInfo(method, false);
//					WriteContext context = new WriteContext(null, new Object[]{target, null}, null, null);
//					for(int i=0; i<parametertypes.length; i++)
//					{
//						if(refs[i] || rrm.getMarshalService().isRemoteReference(parametervalues[i]))
//						{
////							System.out.println("found ref: "+parametervalues[i]);
//							parametervalues[i] = preproc.preProcess(context, parametervalues[i]);
//						}
//					}
//				}
				
				returnisref = SServiceProvider.isReturnValueRemoteReference(method, false);
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
	
	/**
	 *  Post-process a received command before execution
	 *  for e.g. setting security level.
	 */
	public IFuture<Void>	postprocessCommand(IInternalAccess component, RemoteReferenceModule rrm, final IComponentIdentifier target)
	{
		final Future<Void> ret = new Future<Void>();
		
		rrm.getTargetObject(getRemoteReference())
			.addResultListener(component.createResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				try
				{
					RemoteMethodInvocationCommand.this.target	= result;
					method = result.getClass().getMethod(methodname, parametertypes);
					
					// Try to find security level.
					Security	sec	= null;
					List<Class<?>>	classes	= new ArrayList<Class<?>>();
					classes.add(result.getClass());
					for(int i=0; sec==null && i<classes.size(); i++)
					{
						Class<?>	clazz	= classes.get(i);
						try
						{
							Method	m	= clazz.getMethod(methodname, parametertypes);
							sec	= m.getAnnotation(Security.class);
						}
						catch(Exception e)
						{
						}
						
						if(sec==null)
						{
							sec	= clazz.getAnnotation(Security.class);
							if(sec==null)
							{
								classes.addAll(Arrays.asList((Class<?>[])clazz.getInterfaces()));
								if(clazz.getSuperclass()!=null)
								{
									classes.add(clazz.getSuperclass());
								}
							}
						}
					}
					// Default to max security if not found.
					securitylevel	= sec!=null ? sec.value() : Security.PASSWORD;
					
					ret.setResult(null);
				}
				catch(Exception e)
				{
					super.exceptionOccurred(e);
				}
			}
		}));
		
		return ret;
	}

	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture<IRemoteCommand> execute(IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
		final IntermediateFuture<IRemoteCommand> ret = new IntermediateFuture<IRemoteCommand>();
		
		// RMS acts as representative of remote caller.
		IComponentAdapter	ada	= IComponentAdapter.LOCAL.get();
		IComponentIdentifier.LOCAL.set(caller);
		IComponentAdapter.LOCAL.set(null);	// No adapter for remote component.
		Map<String, Object> props = getNonFunctionalProperties();
		
		ServiceCall.getInvocation(props);
		
		invokeMethod(ret, rsms);
		
		IComponentIdentifier.LOCAL.set(component.getComponentIdentifier());
		IComponentAdapter.LOCAL.set(ada);
		
		return ret;
	}

	/**
	 *  Invoke remote method.
	 *  @param targetName The target object.
	 *  @param ret The result future.
	 */
	public void invokeMethod(final IntermediateFuture<IRemoteCommand> ret, final RemoteServiceManagementService rsms)
	{
//		final IntermediateFuture ret = new IntermediateFuture();
		
//		if("addMessageListener".equals(methodname))
//			System.out.println("remote addMessageListener");
		
		final IComponentIdentifier ridcom = getRealReceiver();
		
		try
		{
			final boolean terminable = SReflect.isSupertype(ITerminableFuture.class, method.getReturnType());
			
//			System.out.println("invoke: "+m);
			
			// Necessary due to Java inner class bug 4071957
			if(target.getClass().isAnonymousClass())
				method.setAccessible(true);
			
			final Object res = method.invoke(target, parametervalues);
			
			// Remember invocation for termination invocation
			if(terminable)
			{
				rsms.putProcessingCall(callid, res);
				Runnable cmd = rsms.removeTerminationCommand(callid);
				if(cmd!=null)
					cmd.run();
			}
			
			if(res instanceof IIntermediateFuture)
			{
				((IIntermediateFuture)res).addResultListener(new IIntermediateResultListener()
				{
					int cnt = 0;
					public void intermediateResultAvailable(Object result)
					{
//						System.out.println("inter: "+result);
						ret.addIntermediateResult(new RemoteIntermediateResultCommand(ridcom, result, callid, 
							returnisref, methodname, false, getNonFunctionalProperties(), (IFuture<?>)res, cnt++));
					}
					
					public void finished()
					{
//						System.out.println("fin");
						ret.addIntermediateResult(new RemoteIntermediateResultCommand(ridcom, null, callid, 
							returnisref, methodname, true, getNonFunctionalProperties(), (IFuture<?>)res, cnt));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
					
					public void resultAvailable(Object result)
					{
//						System.out.println("ra");
						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
							returnisref, methodname, getNonFunctionalProperties()));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
					
					public void resultAvailable(Collection result)
					{
//						System.out.println("ra");
						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
							returnisref, methodname, getNonFunctionalProperties()));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("ex: "+exception);
						ret.addIntermediateResult(new RemoteResultCommand(ridcom, null, exception, callid, 
							false, methodname, getNonFunctionalProperties()));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
				});
			}
			else if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
							returnisref, methodname, getNonFunctionalProperties()));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.addIntermediateResult(new RemoteResultCommand(ridcom, null, exception, callid, 
							false, methodname, getNonFunctionalProperties()));
						ret.setFinished();
						rsms.removeProcessingCall(callid);
					}
				});
			}
			else
			{
				ret.addIntermediateResult(new RemoteResultCommand(ridcom, res, null, callid, 
					returnisref, methodname, getNonFunctionalProperties()));
				ret.setFinished();
				rsms.removeProcessingCall(callid);
			}
		}
		catch(Exception exception)
		{
			if(exception instanceof InvocationTargetException
				&& ((InvocationTargetException)exception).getTargetException() instanceof Exception)
			{
				exception	= (Exception)((InvocationTargetException)exception).getTargetException();
			}
			ret.addIntermediateResult(new RemoteResultCommand(ridcom, null, exception, callid, 
				false, methodname, getNonFunctionalProperties()));
			ret.setFinished();
			rsms.removeProcessingCall(callid);
		}
		
//		return ret;
	}
	
	//-------- getter/setter methods --------

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
	 *  Get the parametertypes.
	 *  @return the parametertypes.
	 */
	public Class[] getParameterTypes()
	{
		return parametertypes;
	}
	
	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypes(Class[] parametertypes)
	{
		this.parametertypes = parametertypes.clone();
	}

	/**
	 *  Get the parametervalues.
	 *  @return the parametervalues.
	 */
	public Object[] getParameterValues()
	{
		return parametervalues;
	}

	/**
	 *  Set the parametervalues.
	 *  @param parametervalues The parametervalues to set.
	 */
	public void setParameterValues(Object[] parametervalues)
	{
		this.parametervalues = parametervalues.clone();
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
//		System.out.println("rmi on server: "+callid);
		this.callid = callid;
	}

	/**
	 *  Get the caller.
	 *  @return the caller.
	 */
	public IComponentIdentifier getCaller()
	{
		return caller;
	}

	/**
	 *  Set the caller.
	 *  @param caller The caller to set.
	 */
	public void setCaller(IComponentIdentifier caller)
	{
		this.caller = caller;
	}

	/**
	 *  Get the remote reference.
	 *  @return The remote reference.
	 */
	public RemoteReference getRemoteReference()
	{
		return rr;
	}

	/**
	 *  Set the remote reference.
	 *  @param rr The remote reference to set.
	 */
	public void setRemoteReference(RemoteReference rr)
	{
		this.rr = rr;
	}
	
	/**
	 *  Get the returnisref.
	 *  @return the returnisref.
	 */
	public boolean isReturnValueReference()
	{
		return returnisref;
	}

	/**
	 *  Set the returnisref.
	 *  @param returnisref The returnisref to set.
	 */
	public void setReturnValueReference(boolean returnisref)
	{
		this.returnisref = returnisref;
	}

	/**
	 *  Get the receiver component (if other than rms).
	 *  @return the real receiver.
	 */
	public IComponentIdentifier getSender()
	{
		return caller;
	}
	
	/**
	 *  Get the real receiver (other than rms).
	 *  @return the real receiver.
	 */
	public IComponentIdentifier getRealReceiver()
	{
		IComponentIdentifier ret = null;
		Object ti = getRemoteReference().getTargetIdentifier();
		if(ti instanceof IComponentIdentifier)
			ret = (IComponentIdentifier)ti;
		else if(ti instanceof IServiceIdentifier)
			ret = ((IServiceIdentifier)ti).getProviderId();
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RemoteMethodInvocationCommand(remote reference=" + rr + ", methodname="
			+ methodname + ", callid=" + callid + ")";
	}
	
}

