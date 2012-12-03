package jadex.bridge.service.component.interceptors;

import jadex.base.test.TestReport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Authenticated;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.binaryserializer.BinarySerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 
 */
public class AuthenticationInterceptor implements IServiceInvocationInterceptor
{
	/** The external access. */
	protected IExternalAccess ea;
	
	/** The mode (send or receive). */
	protected boolean send;
	
	/**
	 *  Create a new AuthenticationInterceptor.
	 */
	public AuthenticationInterceptor(IExternalAccess ea, boolean send)
	{
		this.ea = ea;
		this.send = send;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = false;
		Annotation[] anns = context.getMethod().getDeclaringClass().getAnnotations();
		for(int i=0; !ret && i<anns.length; i++)
		{
			ret = isAuthenticated(anns[i]);
		}
		if(!ret)
		{
			Annotation[] methodannos = context.getMethod().getAnnotations();
			for(int i=0; !ret && i<methodannos.length; i++)
			{
				ret = isAuthenticated(methodannos[i]);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Check if an annotation belongs to the supported
	 *  types of pre/postconditions.
	 */
	protected boolean isAuthenticated(Annotation anno)
	{
		return anno instanceof Authenticated;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(send)
		{
			createAuthentication(context).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					context.invoke().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			checkAuthentication(context).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					context.invoke().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Check the authentication.
	 */
	protected IFuture<Void> createAuthentication(ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		String classname = context.getMethod().getDeclaringClass().getName();
		String methodname = context.getMethod().getName();
		Object[] args = context.getArgumentArray();
		Object[] t = new Object[]{context.getCaller().getPlatformPrefix(), classname, methodname, args};
		final byte[] content = BinarySerializer.objectToByteArray(t, null);
		
		SServiceProvider.getService(ea.getServiceProvider(), ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
		{
			public void customResultAvailable(ISecurityService sser)
			{
				sser.signCall(content).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
				{
					public void customResultAvailable(byte[] signed)
					{
						System.out.println("Signed: "+SUtil.arrayToString(signed));
						
						// get service call meta object and set the timeout
						ServiceCall call = ServiceCall.getInvocation();
						call.setProperty(Authenticated.AUTHENTICATED, signed);		
						ret.setResult(null);
					}
				});
			}
		});
	
		return ret;
	}
	
	/**
	 *  Check the authentication.
	 */
	protected IFuture<Void> checkAuthentication(ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		final ServiceCall call = ServiceCall.getCurrentInvocation();
		final IComponentIdentifier caller = call.getCaller();
		final String callername = caller.getPlatformPrefix();
		final byte[] signed = (byte[])call.getProperty(Authenticated.AUTHENTICATED);
		
		if(signed==null)
		{
			ret.setException(new SecurityException("No authentication info provided: "+context.getMethod().getName()));
		}
		else
		{
			try
			{
				// Find allowed caller names by inspecting the implementation annotation
				// todo: make this more configurable somehow
				Class<?> targetcl = context.getTargetObject().getClass();
				Method targetm = targetcl.getDeclaredMethod(context.getMethod().getName(), context.getMethod().getParameterTypes());
				Authenticated au = targetm.getAnnotation(Authenticated.class);
				if(au==null)
					au = targetcl.getAnnotation(Authenticated.class);
				Set<String> allowed = SUtil.arrayToSet(au.value());
				if(!allowed.contains(callername))
				{
					ret.setException(new SecurityException("Authentication failed: "+allowed+" "+callername));
				}
				else
				{
					// In principle allowed caller, now has to be authenticated
					// todo: timepoint
					String classname = context.getMethod().getDeclaringClass().getName();
					String methodname = context.getMethod().getName();
					Object[] args = context.getArgumentArray();
					Object[] t = new Object[]{callername, classname, methodname, args};
					final byte[] content = BinarySerializer.objectToByteArray(t, null);
					
					SServiceProvider.getService(ea.getServiceProvider(), ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
					{
						public void customResultAvailable(ISecurityService sser)
						{
							sser.verifyCall(content, signed, callername).addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
				}
			}
			catch(Exception e)
			{
				ret.setException(new SecurityException(e));
			}
		}
		
		return ret;
	}
}
