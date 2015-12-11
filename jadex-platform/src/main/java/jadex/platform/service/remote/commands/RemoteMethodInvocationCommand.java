package jadex.platform.service.remote.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteReference;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.platform.service.remote.RemoteServiceManagementService;

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
//			System.out.println("caller: "+caller);
		
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
			.addResultListener(component.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
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
	public IIntermediateFuture<IRemoteCommand> execute(IExternalAccess component, RemoteServiceManagementService rsms)
	{
		final IntermediateFuture<IRemoteCommand> ret = new IntermediateFuture<IRemoteCommand>();
		
//		if(caller==null && getMethodName().equals("status"))
//		{
//			System.out.println("dglkysfi");
//			Thread.dumpStack();
//		}
		
		// todo: non-functional props
		
		// RMS acts as representative of remote caller.
		IInternalAccess	ada	= IInternalExecutionFeature.LOCAL.get();
		IComponentIdentifier.LOCAL.set(caller);
		IInternalExecutionFeature.LOCAL.set(null);	// No adapter for remote component.
		Map<String, Object> props = getNonFunctionalProperties();
		
//		props.put("method3", method.getName());

//		if(ServiceCall.getInvocation0()!=null)
//		{
//			System.out.println("lsdjgho");
//		}
		
		ServiceCall.getOrCreateNextInvocation(props);
		
		invokeMethod(ret, rsms);
		
		CallAccess.resetNextInvocation();
		
		IComponentIdentifier.LOCAL.set(component.getComponentIdentifier());
		IInternalExecutionFeature.LOCAL.set(ada);
		
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
		
//		System.out.println("remote invoke: "+methodname);
		
//		if(methodname.indexOf("destroy")!=-1)
//			System.out.println("remote destroyComp");
		
		final IComponentIdentifier ridcom = getRealReceiver();
		
		try
		{
			final boolean terminable = SReflect.isSupertype(ITerminableFuture.class, method.getReturnType());
			
//			System.out.println("invoke: "+m);
			
			// Necessary due to Java inner class bug 4071957
			if(target.getClass().isAnonymousClass())
				method.setAccessible(true);

//			if(method.getName().indexOf("method")!=-1)
//			{
//				System.out.println("hhh: "+ServiceCall.getCurrentInvocation());
//				System.out.println("ggg: "+ServiceCall.getNextInvocation());
//			}
			
			final Object res = method.invoke(target, parametervalues);
			
//			if(methodname.indexOf("method")!=-1)
//				System.out.println("gggg");
			
//			Map<String, Object> nfunc = nonfunc;
//			ServiceCall sc = ServiceCall.getOrCreateNextInvocation(); // hmm has not been switched during call to last
//			ServiceCall sc = ServiceCall.getCurrentInvocation();
//			if(sc!=null)
//				nfunc = sc.getProperties();
			
			handleResultFuture(terminable, rsms, callid, res, terminable, methodname, ridcom, ret);
			
//			// Remember invocation for termination invocation
//			if(terminable) // or pullable
//			{
//				rsms.putProcessingCall(callid, res);
//				List<Runnable> cmds = rsms.removeFutureCommands(callid);
//				if(cmds!=null)
//				{
//					for(Runnable cmd: cmds)
//					{
//						cmd.run();
//					}
//				}
//			}
//			
//			if(res instanceof IIntermediateFuture)
//			{
//				((IIntermediateFuture)res).addResultListener(new IIntermediateFutureCommandResultListener()
//				{
//					int cnt = 0;
//					public void intermediateResultAvailable(Object result)
//					{
////						System.out.println("inter: "+result);
//						ret.addIntermediateResult(new RemoteIntermediateResultCommand(ridcom, result, callid, 
//							returnisref, methodname, false, getNonFunctionalProperties(), (IFuture<?>)res, cnt++));
//					}
//					
//					public void finished()
//					{
////						System.out.println("fin");
//						ret.addIntermediateResult(new RemoteIntermediateResultCommand(ridcom, null, callid, 
//							returnisref, methodname, true, getNonFunctionalProperties(), (IFuture<?>)res, cnt));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					
//					public void resultAvailable(Object result)
//					{
////						System.out.println("ra");
//						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
//							returnisref, methodname, getNonFunctionalProperties()));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					
//					public void resultAvailable(Collection result)
//					{
////						System.out.println("ra");
//						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
//							returnisref, methodname, getNonFunctionalProperties()));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
////						System.out.println("ex: "+exception);
//						ret.addIntermediateResult(new RemoteResultCommand(ridcom, null, exception, callid, 
//							false, methodname, getNonFunctionalProperties()));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					public void commandAvailable(Type command)
//					{
//						ret.addIntermediateResult(new RemoteFutureSourceCommand(ridcom, command, callid, 
//							returnisref, methodname, getNonFunctionalProperties()));
//					}
//				});
//			}
//			else if(res instanceof IFuture)
//			{
//				((IFuture)res).addResultListener(new IFutureCommandResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						ret.addIntermediateResult(new RemoteResultCommand(ridcom, result, null, callid, 
//							returnisref, methodname, getNonFunctionalProperties()));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						ret.addIntermediateResult(new RemoteResultCommand(ridcom, null, exception, callid, 
//							false, methodname, getNonFunctionalProperties()));
//						ret.setFinished();
//						rsms.removeProcessingCall(callid);
//					}
//					
//					public void commandAvailable(Type command)
//					{
//						ret.addIntermediateResult(new RemoteFutureSourceCommand(ridcom, command, callid, 
//							returnisref, methodname, getNonFunctionalProperties()));
//					}
//				});
//			}
//			else
//			{
//				ret.addIntermediateResult(new RemoteResultCommand(ridcom, res, null, callid, 
//					returnisref, methodname, getNonFunctionalProperties()));
//				ret.setFinished();
//				rsms.removeProcessingCall(callid);
//			}
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
	
	/**
	 *  Handle the result future by checking what future it is and
	 *  sending intermediate results as commands. 
	 */
	public void handleResultFuture(boolean terminable, final RemoteServiceManagementService rsms, final String callid, final Object res,
		final boolean returnisref, final String methodname, final IComponentIdentifier rec, final IntermediateFuture<IRemoteCommand> ret)
//		final Map<String, Object> nonfunc)
	{
		// Remember invocation for termination invocation
		if(terminable) // or pullable
		{
			rsms.putProcessingCall(callid, res);
			List<Runnable> cmds = rsms.removeFutureCommands(callid);
			if(cmds!=null)
			{
				for(Runnable cmd: cmds)
				{
					cmd.run();
				}
			}
		}
		
		if(res instanceof IIntermediateFuture)
		{
			((IIntermediateFuture)res).addResultListener(new IIntermediateFutureCommandResultListener()
			{
				int cnt = 0;
				public void intermediateResultAvailable(Object result)
				{
//					System.out.println("inter: "+result);
					ret.addIntermediateResult(new RemoteIntermediateResultCommand(rec, result, callid, 
						returnisref, methodname, false, getNFProps(true), (IFuture<?>)res, cnt++));
				}
				
				public void finished()
				{
//					System.out.println("fin");
					ret.addIntermediateResult(new RemoteIntermediateResultCommand(rec, null, callid, 
						returnisref, methodname, true, getNFProps(false), (IFuture<?>)res, cnt));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void resultAvailable(Object result)
				{
//					System.out.println("ra");
					ret.addIntermediateResult(new RemoteResultCommand(rec, result, null, callid, 
						returnisref, methodname, getNFProps(false)));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void resultAvailable(Collection result)
				{
//					System.out.println("ra");
					ret.addIntermediateResult(new RemoteResultCommand(rec, result, null, callid, 
						returnisref, methodname, getNFProps(false)));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("ex: "+exception);
					ret.addIntermediateResult(new RemoteResultCommand(rec, null, exception, callid, 
						false, methodname, getNFProps(false)));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void commandAvailable(Object command)
				{
					ret.addIntermediateResult(new RemoteFutureSourceCommand(rec, command, callid, 
						returnisref, methodname, getNFProps(true)));
				}
			});
		}
		else if(res instanceof IFuture)
		{
			((IFuture)res).addResultListener(new IFutureCommandResultListener()
			{
				public void resultAvailable(Object result)
				{
					ret.addIntermediateResult(new RemoteResultCommand(rec, result, null, callid, 
						returnisref, methodname, getNFProps(false)));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.addIntermediateResult(new RemoteResultCommand(rec, null, exception, callid, 
						false, methodname, getNFProps(false)));
					ret.setFinished();
					rsms.removeProcessingCall(callid);
				}
				
				public void commandAvailable(Object command)
				{
					ret.addIntermediateResult(new RemoteFutureSourceCommand(rec, command, callid, 
						returnisref, methodname, getNFProps(true)));
				}
			});
		}
		else
		{
			ret.addIntermediateResult(new RemoteResultCommand(rec, res, null, callid, 
				returnisref, methodname, getNFProps(false)));
			ret.setFinished();
			rsms.removeProcessingCall(callid);
		}
	}
	
	/**
	 *  Get the non functional props from the executed call.
	 *  @return The call props.
	 */
	protected Map<String, Object> getNFProps(boolean intermediate)
	{
//		if(method.getName().indexOf("method")!=-1)
//			System.out.println("aas");
		Map<String, Object> ret = nonfunc;
		// During intermediate results the call is still running and nf vals must be fetched from current invoc
		ServiceCall sc = intermediate? ServiceCall.getCurrentInvocation(): ServiceCall.getLastInvocation();
		if(sc!=null)
			ret = sc.getProperties();
		return ret;
	}
}

