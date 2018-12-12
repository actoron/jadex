package jadex.bridge.service.component.interceptors;

import java.lang.annotation.Annotation;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.IFuture;

/**
 *  Interceptor that can be used to realize authenticated end-to-end communication.
 *  - verifies that a call is authenticated by checking the requested/annotated role(s) against the actual role(s) authenticated by the security service.
 */
public class AuthenticationInterceptor extends AbstractLRUApplicableInterceptor
{
	//-------- attributes --------
	
	/** The mode (send or receive). */
	protected boolean send;
	
	//-------- constructors --------
	
	/**
	 *  Create a new AuthenticationInterceptor.
	 */
	public AuthenticationInterceptor(IInternalAccess ia, boolean send)
	{
		super(ia);
		this.send = send;
	}

	//-------- constructors --------
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean customIsApplicable(ServiceInvocationContext context)
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
		return anno instanceof Security;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
//		final Future<Void> ret = new Future<Void>();
		
//		if(send)
//		{
//			createAuthentication(context).addResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					context.invoke().addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});
//		}
//		else
//		{
//			checkAuthentication(context).addResultListener(new DelegationResultListener<Void>(ret)
//			{
//				public void customResultAvailable(Void result)
//				{
//					context.invoke().addResultListener(new DelegationResultListener<Void>(ret));
//				}
//			});
//		}
		
		return context.invoke();
	}
	
	/**
	 *  Check the authentication.
	 */
	/*protected IFuture<Void> createAuthentication(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		String classname = context.getMethod().getDeclaringClass().getName();
		String methodname = context.getMethod().getName();
		Object[] args = context.getArgumentArray();
		Object[] t = new Object[]{context.getCaller().getPlatformPrefix(), classname, methodname, args};
		final byte[] content = SBinarySerializer.writeObjectToByteArray(t, null);
		
		getComponent().searchService( new ServiceQuery<>( ISecurityService.class, ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
		{
			public void customResultAvailable(ISecurityService sser)
			{
				sser.signCall(content).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
				{
					public void customResultAvailable(byte[] signed)
					{
//						System.out.println("Signed: "+SUtil.arrayToString(signed));
						
						// get service call meta object and set the timeout
						context.getNextServiceCall().setProperty(Authenticated.AUTHENTICATED, signed);		
						ret.setResult(null);
					}
				});
			}
		});
	
		return ret;
	}*/
	
	/**
	 *  Check the authentication.
	 */
	/*protected IFuture<Void> checkAuthentication(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
//		final ServiceCall call = ServiceCall.getCurrentInvocation();
		final ServiceCall call = context.getNextServiceCall();
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

				// try to find implementation method annotation
				Class<?> targetcl = context.getTargetObject().getClass();
				Method targetm = targetcl.getDeclaredMethod(context.getMethod().getName(), context.getMethod().getParameterTypes());
				Authenticated au = targetm.getAnnotation(Authenticated.class);
				
				// if not available use interface method anno
				if(au==null)
				{
					au = context.getMethod().getAnnotation(Authenticated.class);
					if(au.names().length==0 && au.virtuals().length==0)
						au = null;
				}
				
				// if not available use implementation service anno
				if(au==null)
				{
					au = targetcl.getAnnotation(Authenticated.class);
				}
				
				// if not available use interface service anno
				if(au==null)
				{
					au = context.getMethod().getClass().getAnnotation(Authenticated.class);
				}
				
				Set<String> allowed = SUtil.arrayToSet(au.names());
				if(!allowed.contains(callername))
				{
					// if not contained in direct names check virtual name mappings
					final String[] virtuals = au.virtuals();
					ia.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ISecurityService.class, ServiceScope.PLATFORM))
						.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
					{
						public void customResultAvailable(ISecurityService sser)
						{
							sser.checkVirtual(virtuals, callername).addResultListener(new DelegationResultListener<Void>(ret)
							{
								public void customResultAvailable(Void result)
								{
									// In principle allowed caller, now has to be authenticated
									// todo: timepoint
									internalCheck(context, callername, signed).addResultListener(new DelegationResultListener<Void>(ret));
								}
							});
						}
					});
				}
				else
				{
					// In principle allowed caller, now has to be authenticated
					// todo: timepoint
					internalCheck(context, callername, signed).addResultListener(new DelegationResultListener<Void>(ret));
				}
			}
			catch(Exception e)
			{
				ret.setException(new SecurityException(e));
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Internal check method that calls verify on 
	 */
	/*protected IFuture<Void> internalCheck(ServiceInvocationContext context, final String callername, final byte[] signed)
	{
		final Future<Void> ret = new Future<Void>();
		
		String classname = context.getMethod().getDeclaringClass().getName();
		String methodname = context.getMethod().getName();
		Object[] args = context.getArgumentArray();
		Object[] t = new Object[]{callername, classname, methodname, args};
		final byte[] content = SBinarySerializer.writeObjectToByteArray(t, null);
		
		ia.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ISecurityService.class, ServiceScope.PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
		{
			public void customResultAvailable(ISecurityService sser)
			{
				sser.verifyCall(content, signed, callername).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}*/
}
