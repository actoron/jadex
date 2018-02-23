package jadex.bridge.component.impl.remotecommands;

import java.lang.reflect.Method;
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

/**
 *  Invoke a remote method.
 */
public class RemoteMethodInvocationCommand<T>	extends AbstractInternalRemoteCommand	implements IRemoteCommand<T>, ISecuredRemoteCommand
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
//		if(method.toString().toLowerCase().indexOf("transport")==-1)
//			System.out.println("Executing requested remote method invocation: "+access.getComponentIdentifier()+", "+method);
		
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
	public Security	getSecurityLevel(IInternalAccess access)
	{
		Security	level	= null;
		Method	m0	= method.getMethod(access.getClassLoader());
		
		// For service call -> look for annotation in impl class hierarchy
		if(target instanceof IServiceIdentifier)
		{
			IServiceIdentifier	sid	= (IServiceIdentifier)target;
			Object	impl	= access.getComponentFeature(IProvidedServicesFeature.class).getProvidedServiceRawImpl(sid);
			Class<?>	implclass	= impl!=null ? impl.getClass() : null;
			
			// Precedence: hierarchy before specificity (e.g. class annotation in subclass wins over method annotation in superclass)
			while(level==null && implclass!=null)
			{
				// Todo: cache for speed?
				try
				{
					level	= implclass.getDeclaredMethod(m0.getName(), m0.getParameterTypes()).getAnnotation(Security.class);
				}
				catch(Exception e)
				{
					// ignore (e.g. NoSuchMethodException)
				}
				
				if(level==null)
				{
					level	= implclass.getAnnotation(Security.class);
				}
				
				implclass	= implclass.getSuperclass();
			}
			
			// Default to interface if not specified in impl.
			if(level==null)
			{
				// Specificity: method before class
				level	= m0.getAnnotation(Security.class);
				if(level==null)
				{
					Class<?>	type	= sid.getServiceType().getType(access.getClassLoader());
					level	= type!=null ? type.getAnnotation(Security.class) : null;
				}
			}
		}
		
		// Default: use method annotation, if any.
		else
		{
			level	= m0.getAnnotation(Security.class);
		}
		
		// level==null -> disallow direct access to components (overridden by trusted platform)
		
		return level;
	}

	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return "RemoteMethodInvocationCommand("+method.getName()+SUtil.arrayToString(args)+")";
	}
}
