package jadex.platform.service.servicepool;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *  The service handler is used as service implementation for proxy services.
 *  Incoming calls will be served by instances from the pool.
 */
@Service
public class ServiceHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** List of idle services. */
	protected List<Object> servicepool;
	
	/** The service count. */
	protected int count;
	
	/** A queue of open requests. */
	protected List<Object[]> queue;
	
	/** The strategy. */
	protected ServicePoolStrategy strategy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public ServiceHandler(IInternalAccess component, Class<?> servicetype, ServicePoolStrategy strategy)
	{
		this.component = component;
		this.servicetype = servicetype;
		this.strategy = strategy;
		this.servicepool = new LinkedList<Object>();
		this.queue = new LinkedList<Object[]>();
	}
	
	//-------- methods --------

	/**
	 *  Callback of the invocation handler interface.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		assert component.isComponentThread();
		
		if(!SReflect.isSupertype(IFuture.class, method.getReturnType()))
			return new Future<Object>(new IllegalArgumentException("Return type must be future: "+method.getName()));
		
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
		
		// Add task to queue.
		queue.add(new Object[]{method, args, ret});
		
		// Use idle service, if available.
		if(!servicepool.isEmpty())
		{
			final Object service = servicepool.remove(0);
			addService(service);
		}
		
		// Create new service.
		else if(count<strategy.getMax())
		{
			count++;
			component.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
			{
				public void customResultAvailable(final IComponentManagementService cms)
				{
					CreationInfo ci  = new CreationInfo(component.getComponentIdentifier());
					ci.setImports(component.getModel().getAllImports());
					cms.createComponent(null, strategy.getComponentModel(), ci, null)
						.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
					{
						public void customResultAvailable(IComponentIdentifier result)
						{
							cms.getExternalAccess(result)
								.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
							{
								public void customResultAvailable(IExternalAccess ea)
								{
									Future<Object> fut = (Future<Object>)SServiceProvider.getService(ea.getServiceProvider(), servicetype, RequiredServiceInfo.SCOPE_LOCAL);
									fut.addResultListener(component.createResultListener(new DelegationResultListener<Object>(ret)
									{
										public void customResultAvailable(Object service)
										{
											addService(service);
										}
									}));
								}
							});
						};
					});
				}
			});
		}
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Called when a service becomes idle.
	 */
	protected void	addService(final Object service)
	{
		assert component.isComponentThread();

		if(queue.isEmpty())
		{
			servicepool.add(service);
		}
		else
		{
			final Object[] task = queue.remove(0);
			Method method = (Method)task[0];
			Object[] args = (Object[])task[1];
			Future<?> ret = (Future<?>)task[2];
			
			try
			{
				IFuture<Object> res = (IFuture<Object>)method.invoke(service, args);
				FutureFunctionality.connectDelegationFuture(ret, res);
				
				// put the components back in pool after call is done
				res.addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						addService(service);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Exception during service invocation in service pool:_"+exception.getMessage());
						exception.printStackTrace();
						addService(service);
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
	}
}
