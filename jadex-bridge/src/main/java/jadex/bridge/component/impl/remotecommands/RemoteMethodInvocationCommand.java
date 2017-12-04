package jadex.bridge.component.impl.remotecommands;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

/**
 *  Invoke a remote method.
 */
public class RemoteMethodInvocationCommand<T>	extends AbstractInternalRemoteCommand	implements IRemoteCommand<T>
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
	public RemoteMethodInvocationCommand(Object target, Method method, Object[] args, Map<String, Object> nonfunc)
	{
		super(nonfunc);
		this.target	= target;
		this.method	= new MethodInfo(method);
		this.args	= args;
//		System.out.println("created rmi command: "+target+" "+method.getName());
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
	public IFuture<T>	execute(IInternalAccess access, IMsgSecurityInfos secinf)
	{
//		System.out.println("Executing requested remote method invocation: "+access.getComponentIdentifier()+", "+method);
		
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
					if(service==null)
					{
						ret = new Future<Object>(new ServiceNotFoundException(sid.getServiceType()+" on component: "+access));
					}
					else
					{
						ret	= m.invoke(service, args);
					}
				}
				catch(NullPointerException nex)
				{
					ret	= new Future<Object>(nex);
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
		IFuture<T>	fret	= ret instanceof IFuture<?> ? (IFuture<T>)ret : new Future<T>((T)ret);
		return fret;
	}
	
	/**
	 *  Method to provide the required security level.
	 *  Overridden by subclasses.
	 */
	@Override
	protected String	getSecurityLevel(IInternalAccess access)
	{
		String	level;
		if(target instanceof IServiceIdentifier)
		{
			IServiceIdentifier	sid	= (IServiceIdentifier)target;
			Class<?>	type	= sid.getServiceType().getType(access.getClassLoader());
			Security	secreq	= type!=null ? type.getAnnotation(Security.class) : null;
			level	= secreq!=null ? secreq.value()[0] : null;	// TODO: multiple roles
		}
		else
		{
			level	= super.getSecurityLevel(access);
		}
		
		return level==null ? super.getSecurityLevel(access)
			: (String)SJavaParser.evaluateExpressionPotentially(level, access.getModel().getAllImports(), access.getFetcher(), access.getClassLoader());
	}

	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return "RemoteMethodInvocationCommand("+method.getName()+SUtil.arrayToString(args)+")";
	}
}
