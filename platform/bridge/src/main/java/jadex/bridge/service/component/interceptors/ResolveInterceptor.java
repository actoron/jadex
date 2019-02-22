package jadex.bridge.service.component.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.nonfunctional.INFMethodPropertyProvider;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.IParameterGuesser;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The resolve interceptor is responsible for determining
 *  the object on which the method invocation is finally performed.
 * 
 *  Checks whether the object is a ServiceInfo. In this case
 *  it delegates method calls of I(Internal)Service to 
 *  the automatically created BasicService instance and all
 *  other calls to the domain object.
 *  
 *  // todo: much annotation stuff and injection of objects to the pojo.
 */
public class ResolveInterceptor extends AbstractApplicableInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	public static final Set<Method> SERVICEMETHODS;
	protected static final Method START_METHOD;
	protected static final Method SHUTDOWN_METHOD;
	protected static final Method INVOKE_METHOD;
//	protected static final Method CREATESID_METHOD;
	
	static
	{
		try
		{
			START_METHOD = IInternalService.class.getMethod("startService", new Class[0]);
			SHUTDOWN_METHOD = IInternalService.class.getMethod("shutdownService", new Class[0]);
			INVOKE_METHOD = IService.class.getMethod("invokeMethod", new Class[]{String.class, ClassInfo[].class, Object[].class});
			SERVICEMETHODS = new HashSet<Method>();
			SERVICEMETHODS.add(IService.class.getMethod("getServiceId", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("getPropertyMap", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("isValid", new Class[0]));

			// internal methods???
			SERVICEMETHODS.add(IInternalService.class.getMethod("setServiceIdentifier", new Class[]{IServiceIdentifier.class}));
			SERVICEMETHODS.add(IInternalService.class.getMethod("setComponentAccess", new Class[]{IInternalAccess.class}));
//			SERVICEMETHODS.add(IService.class.getMethod("getExternalComponentFeature", new Class[]{Class.class}));
			
			Method[] ms = INFPropertyProvider.class.getDeclaredMethods();
			for(Method m: ms)
			{
//				System.out.println("m: "+m.getName());
				SERVICEMETHODS.add(m);
			}
			
			ms = INFMethodPropertyProvider.class.getDeclaredMethods();
			for(Method m: ms)
			{
//				System.out.println("m: "+m.getName());
				SERVICEMETHODS.add(m);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/** The component. */
	protected IInternalAccess ia;
	
	/**
	 *  Create a new ResolveInterceptor.
	 */
	public ResolveInterceptor(IInternalAccess ia)
	{
		this.ia = ia;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(sic.getMethod().getName().indexOf("invoke")!=-1)
//			System.out.println("herere");
		
		Object service = sic.getObject();
		if(service instanceof ServiceInfo)
		{
			final ServiceInfo si = (ServiceInfo)service;
			
			if(START_METHOD.equals(sic.getMethod()))
			{
				// invoke 1) basic service start 2) domain service start
				invokeDoubleMethod(sic, si, START_METHOD, ServiceStart.class, true).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else if(SHUTDOWN_METHOD.equals(sic.getMethod()))
			{
				// invoke 1) domain service shutdown 2) basic service shutdown
				invokeDoubleMethod(sic, si, SHUTDOWN_METHOD, ServiceShutdown.class, false).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else if(INVOKE_METHOD.equals(sic.getMethod()))
			{
				// If reflective method invokeMethod() is invoked it will be redirected to the real method
				// String servicename, String methodname, ClassInfo[] argtypes, Object[] args)
				sic.setObject(si.getDomainService());
				
				List<Object> args = sic.getArguments();
				String methodname = (String)args.get(0);
				ClassInfo[] argtypes = (ClassInfo[])args.get(1);
				Object[] as = (Object[])args.get(2);
				
				if(argtypes!=null)
				{
					for(int i=0; i<argtypes.length; i++)
					{
						Class<?> target = argtypes[i].getType(ia.getClassLoader());
						Object cval = Starter.convertParameter(as[i], target);
						if(cval!=null)
							as[i] = cval;
					}
				}
				
				Method m = BasicService.getInvokeMethod(si.getDomainService().getClass(), ia.getClassLoader(), methodname, argtypes);
				sic.setMethod(m);

				sic.setArguments(SUtil.arrayToList(as));
				
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
			else if(SERVICEMETHODS.contains(sic.getMethod()))
			{
				sic.setObject(si.getManagementService());
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				sic.setObject(si.getDomainService());
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
		}
		else
		{
			sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Invoke double methods.
	 *  The boolean 'firstorig' determines if basicservice method is called first.
	 */
	protected IFuture<Void> invokeDoubleMethod(final ServiceInvocationContext sic, final ServiceInfo si, Method m, Class<? extends Annotation> annotation, boolean firstorig)
	{
		final Future<Void> ret = new Future<Void>();
		
		Object obj = ProxyFactory.isProxyClass(si.getDomainService().getClass())? ProxyFactory.getInvocationHandler(si.getDomainService()): si.getDomainService();
		
		Method[] methods = SReflect.getAllMethods(obj.getClass());
		Method found = null;
		
		for(int i=0; !ret.isDone() && i<methods.length; i++)
		{
			if(methods[i].isAnnotationPresent(annotation))
			{
				if(found==null)
				{
					if((methods[i].getModifiers()&Modifier.PUBLIC)!=0)
					{
						found	= methods[i];
					}
					else
					{
						ret.setException(new RuntimeException("Annotated method @"+annotation.getSimpleName()+" must be public: "+methods[i]));
					}
				}
				
				// Fail on duplicate annotation if not from overridden method.
				else if(!Arrays.equals(methods[i].getParameterTypes(), found.getParameterTypes()))
				{
					ret.setException(new RuntimeException("Duplicate annotation @"+annotation.getSimpleName()+" in methods "+methods[i]+" and "+found));
				}
			}
		}
		
		if(!ret.isDone())
		{
			if(found!=null)
			{
				final ServiceInvocationContext domainsic = new ServiceInvocationContext(sic);
				domainsic.setMethod(found);
				domainsic.setObject(obj);
				// Guess parameters for allowing injected value in pojo methods
				IParameterGuesser guesser = ia.getParameterGuesser();
				List<Object> args = new ArrayList<Object>();
				for(int i=0; i<found.getParameterTypes().length; i++)
				{
					args.add(guesser.guessParameter(found.getParameterTypes()[i], false));
				}
				domainsic.setArguments(args);
				
				sic.setObject(si.getManagementService());
				
				if(firstorig)
				{
					sic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							// Mgmt method is always future<void>
							IResultListener<Object>	lis	= new IResultListener<Object>()
							{
								public void resultAvailable(Object result)
								{
									domainsic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
									{
										public void customResultAvailable(Void result)
										{
//											if(sic.getObject() instanceof BasicService && ((BasicService)sic.getObject()).getInterfaceType().getName().indexOf("Peer")!=-1)
//												System.out.println("hhhhhhhhhhhhhhhhhh");
											
											// If domain result is future, replace finished mgmt result with potentially not yet finished domain future.
											if(domainsic.getResult() instanceof IFuture<?> || domainsic.getResult() instanceof Exception)
												sic.setResult(domainsic.getResult());
											super.customResultAvailable(result);
										}
									});
								}
								public void exceptionOccurred(Exception exception)
								{
									// Invocation finished, exception available in result future. 
									ret.setResult(null);
								}
							};
							((IFuture)sic.getResult()).addResultListener(lis);
						}
					});
				}
				else
				{
					domainsic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
					{
						public void customResultAvailable(Void result)
						{
							// Domain method may be void or future<void>
							if(domainsic.getResult() instanceof IFuture<?>)
							{
								IResultListener<Object>	lis	= new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
									}
									public void exceptionOccurred(Exception exception)
									{
										// Make exception available in result future.
										sic.setResult(new Future<Void>(exception));
										ret.setResult(null);
									}
								};
								((IFuture)domainsic.getResult()).addResultListener(lis);
							}
							else
							{
								sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
							}
						}
					});
				}
			}
			else
			{				
				sic.setObject(si.getManagementService());
				sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
			}
		}
		
		return ret;
	}
	
}
