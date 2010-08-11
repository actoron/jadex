package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.service.IServiceIdentifier;
import jadex.service.SServiceProvider;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *  Command for executing a remote method.
 */
public class RemoteMethodInvocationCommand implements IRemoteCommand
{
	//-------- attributes --------
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The component identifier (for alternatively invoking method on external access). */
	protected IComponentIdentifier cid;
	
	/** The methodname. */
	protected String methodname;
	
	/** The parameter types. */
	protected Class[] parametertypes;
	
	/** The parameter values. */
	protected Object[] parametervalues;
	
	/** The call identifier. */
	protected String callid;
	
	/** The remote management service identifier. */
	protected IComponentIdentifier rms;
	
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
	public RemoteMethodInvocationCommand(IServiceIdentifier sid, String methodname, 
		Class[] parametertypes, Object[] parametervalues, String callid, IComponentIdentifier rms)
	{
		this.sid = sid;
		this.methodname = methodname;
		this.parametertypes = parametertypes;
		this.parametervalues = parametervalues;
		this.callid = callid;
		this.rms = rms;
	}
	
	/**
	 *  Create a new remote method invocation command. 
	 */
	public RemoteMethodInvocationCommand(IComponentIdentifier cid, String methodname, 
		Class[] parametertypes, Object[] parametervalues, String callid, IComponentIdentifier rms)
	{
		this.cid = cid;
		this.methodname = methodname;
		this.parametertypes = parametertypes;
		this.parametervalues = parametervalues;
		this.callid = callid;
		this.rms = rms;
	}
	
	//-------- methods --------

	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(IMicroExternalAccess component, Map waitingcalls)
	{
		final Future ret = new Future();
		
		// fetch component via target component id
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				
				cms.getExternalAccess(getTargetId()).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						
						// fetch service on target component 
						if(sid!=null)
						{
							SServiceProvider.getDeclaredService(exta.getServiceProvider(), sid)
								.addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									invokeMethod(result, ret);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									ret.setResult(new RemoteResultCommand(null, exception, callid));
								}
							});
						}
						// invoke method directly on external access
						else
						{
							invokeMethod(exta, ret);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setResult(new RemoteResultCommand(null, exception, callid));
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setResult(new RemoteResultCommand(null, exception, callid));
			}
		}));
			
		return ret;
	}

	/**
	 *  Invoke remote method.
	 *  @param target The target object.
	 *  @param ret The result future.
	 */
	public void invokeMethod(Object target, final Future ret)
	{
		try
		{
			// fetch method on service and invoke method
			Method m = target.getClass().getMethod(methodname, parametertypes);
			Object res = m.invoke(target, parametervalues);
			
			if(res instanceof IFuture)
			{
				((IFuture)res).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						ret.setResult(new RemoteResultCommand(result, null, callid));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setResult(new RemoteResultCommand(null, exception, callid));
					}
				});
			}
			else
			{
				ret.setResult(new RemoteResultCommand(res, null, callid));
			}
		}
		catch(Exception exception)
		{
			ret.setResult(new RemoteResultCommand(null, exception, callid));
		}
	}
	
	/**
	 *  Get the component target id.
	 *  @return The component id of the target component.
	 */
	public IComponentIdentifier getTargetId()
	{
		return sid!=null? (IComponentIdentifier)sid.getProviderId(): cid;
	}
	
	//-------- getter/setter methods --------

	
	
	/**
	 *  Get the remote management service identifier.
	 *  @return The remote management service identifier.
	 */
	public IComponentIdentifier getRemoteManagementServiceIdentifier()
	{
		return rms;
	}

	/**
	 *  Get the component identifier.
	 *  @return the cid.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the component identifier.
	 *  @param cid The cid to set.
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid = cid;
	}

	/**
	 *  Set the remote management service identifier.
	 *  @param rms The remote management service to set identifier.
	 */
	public void setRemoteManagementServiceIdentifier(IComponentIdentifier rms)
	{
		this.rms = rms;
	}
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the service identifier.
	 *  @param sid The service identifier to set.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
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
		this.parametertypes = parametertypes;
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
		this.parametervalues = parametervalues;
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RemoteMethodInvocationCommand(sid=" + sid + ", methodname="
			+ methodname + ", callid=" + callid + ")";
	}
	
	
}

