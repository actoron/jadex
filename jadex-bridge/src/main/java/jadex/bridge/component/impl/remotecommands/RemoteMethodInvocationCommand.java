package jadex.bridge.component.impl.remotecommands;

import java.lang.reflect.Method;
import java.util.Arrays;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Invoke a remote method.
 */
public class RemoteMethodInvocationCommand implements IRemoteCommand<Object>
{
	//-------- attributes --------
	
	/** The target id. */
	private Object	target;
	
	/** The remote method. */
	private MethodInfo	method;
	
	/** The arguments. */
	private Object[]	args;

	/**
	 *  Create a remote method invocation command.
	 */
	public RemoteMethodInvocationCommand()
	{
		// Bean constructor.
	}

	/**
	 *  Create a remote method invocation command.
	 */
	public RemoteMethodInvocationCommand(Object target, Method method, Object[] args)
	{
		this.target	= target;
		this.method	= new MethodInfo(method);
		this.args	= args;
	}
	
	/**
	 *  Get the target id.
	 */
	public Object	getTargetId()
	{
		return target;
	}
	
	/**
	 *  Set the target id.
	 */
	public void	setTargetId(Object target)
	{
		this.target	= target;
	}

	/**
	 *  Get the method.
	 */
	public MethodInfo	getMethod()
	{
		return method;
	}
	
	/**
	 *  Set the method.
	 */
	public void	setMethod(MethodInfo method)
	{
		this.method	= method;
	}

	/**
	 *  Get the arguments.
	 */
	public Object[]	getArguments()
	{
		return args;
	}
	
	/**
	 *  Set the arguments.
	 */
	public void	setArguments(Object[] args)
	{
		this.args	= args;
	}

	/**
	 *  Execute the method.
	 */
	@Override
	public IFuture<Object>	execute(IInternalAccess access, IMsgSecurityInfos secinf)
	{
		Object	ret	= null;
		Method	m	= method.getMethod(access.getClassLoader());
		if(target instanceof IServiceIdentifier)
		{
			IServiceIdentifier	sid	= (IServiceIdentifier)target;
			if(sid.getProviderId().equals(access.getComponentIdentifier()))
			{
				
			}
		}
		else if(target instanceof IComponentIdentifier)
		{
			
		}
		
		@SuppressWarnings("unchecked")
		IFuture<Object>	fret	= ret instanceof IFuture<?> ? (IFuture<Object>)ret : new Future<Object>(ret);
		return fret;
	}
}