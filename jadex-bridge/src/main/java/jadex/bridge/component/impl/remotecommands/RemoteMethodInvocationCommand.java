package jadex.bridge.component.impl.remotecommands;

import java.lang.reflect.Method;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IProvidedServicesFeature;
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
//		if(method.getName().indexOf("Step")!=-1)
//		{
//			System.out.println("sril");
//		}
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
		System.out.println("Executing requested remote method invocation: "+access.getComponentIdentifier()+", "+method);
		
		Object	ret	= null;
		if(target instanceof IServiceIdentifier)
		{
			IServiceIdentifier	sid	= (IServiceIdentifier)target;
			if(sid.getProviderId().equals(access.getComponentIdentifier()))
			{
				try
				{
					Method	m	= method.getMethod(access.getClassLoader());
					Object	service	= access.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(sid);
					ret	= m.invoke(service, args);
				}
				catch(Exception e)
				{
					ret	= new Future<Object>(e);
				}				
			}
			else
			{
				ret	= new Future<Object>(new IllegalArgumentException("Can not invoke service of other component: "+access.getComponentIdentifier()+", "+sid));
			}
		}
		else if(target instanceof IComponentIdentifier)
		{
			IComponentIdentifier	cid	= (IComponentIdentifier)target;
			if(cid.equals(access.getComponentIdentifier()))
			{
				try
				{
					Method	m	= method.getMethod(access.getClassLoader());
					ret	= m.invoke(access.getExternalAccess(), args);
				}
				catch(Exception e)
				{
					ret	= new Future<Object>(e);
				}
			}
			else
			{
				ret	= new Future<Object>(new IllegalArgumentException("Can not access other component: "+access.getComponentIdentifier()+", "+cid));
			}			
		}
		
		@SuppressWarnings("unchecked")
		IFuture<Object>	fret	= ret instanceof IFuture<?> ? (IFuture<Object>)ret : new Future<Object>(ret);
		return fret;
	}
	
	/**
	 *  Get the return type.
	 *  @return A class representing a future interface for mapping the result of the command.
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends IFuture<Object>>	getReturnType(IInternalAccess access)
	{
		return (Class<IFuture<Object>>)(IFuture.class.isAssignableFrom(method.getMethod(access.getClassLoader()).getReturnType())
			? (Class<IFuture<Object>>)method.getMethod(access.getClassLoader()).getReturnType()
			: IFuture.class);
	}
}