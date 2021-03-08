package jadex.bridge.service.component.interceptors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The service getter allows for getting a service 
 */
public class ServiceGetter<T>
{
	/** The internal access. */
	protected IInternalAccess component;
	
	/** The service type. */
	protected Class<T> type;
	
	/** The cached service. */
	protected T service;

	/** The scope. */
	protected ServiceScope scope;
	
	/** The time of the last search. */
	protected long lastsearch;
	
	/** The delay between searches when no service was found. */
	protected long delay = 30000;

	/** Ongoing call future. */
	protected Future<T> callfut;
	
	/**
	 *  Create a new service getter.
	 */
	public ServiceGetter(IInternalAccess component, Class<T> type, ServiceScope scope)
	{
		this(component, 30000, type, scope);
	}
	
	/**
	 *  Create a new service getter.
	 */
	public ServiceGetter(IInternalAccess component, long delay, Class<T> type, ServiceScope scope)
	{
		this.component = component;
		this.delay = delay;
		this.type = type;
		
		if(ServiceScope.EXPRESSION.equals(scope))
			throw new IllegalArgumentException("Cannot use scope 'expression' directly.");

		this.scope = scope;
	}
	
	/**
	 *  Get or search the service with a delay in case not found.
	 */
	public IFuture<T> getService()
	{
//		System.out.println("getMon");
		
//		final Future<T> ret = new Future<T>();

//		component.getServiceContainer().searchService( new ServiceQuery<>( type, scope))
//			.addResultListener(component.createResultListener(new IResultListener<T>()
//		{
//			public void resultAvailable(T result)
//			{
//				service = result;
//				ret.setResult(service);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//	//			exception.printStackTrace();
//				ret.setResult(null);
//			}
//		}));
		
		Future<T> ret;

		// Must use a call future to ensure that all calls get a result if one can be found
		if(callfut==null)
		{
			callfut = new Future<T>();
			ret = callfut;
			
			if(service==null)
			{
				if(lastsearch==0 || System.currentTimeMillis()>lastsearch+delay)
				{
					lastsearch = System.currentTimeMillis();
					
					component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(type, scope))
						.addResultListener(new IResultListener<T>()
					{
						public void resultAvailable(T result)
						{
							service = result;
	//						ret.setResult(service);
							Future<T> fut = callfut;
							callfut = null;
							fut.setResult(service);
						}
						
						public void exceptionOccurred(Exception exception)
						{
		//					exception.printStackTrace();
	//						ret.setResult(null);
							Future<T> fut = callfut;
							callfut = null;
							fut.setResult(null);
						}
					});
				}
				else
				{
					Future<T> fut = callfut;
					callfut = null;
					fut.setResult(null);
				}
			}
			else
			{
				Future<T> fut = callfut;
				callfut = null;
				fut.setResult(service);
			}
		}
		else
		{
			ret = callfut;
		}
		
		return ret;
	}
	
	/**
	 *  Set the service to null, if e.g. broken.
	 */
	public void resetService()
	{
		this.service = null;
	}
	
	/**
	 *  Get last service.
	 */
	public T getLastService()
	{
		return this.service;
	}
}
