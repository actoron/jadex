package jadex.bridge.service.component.interceptors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
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
	protected String scope;
	
	/** The time of the last search. */
	protected long lastsearch;
	
	/** The delay between searches when no service was found. */
	protected long delay = 30000;

	/**
	 *  Create a new service getter.
	 */
	public ServiceGetter(IInternalAccess component, Class<T> type, String scope)
	{
		this(component, 30000, type, scope);
	}
	
	/**
	 *  Create a new service getter.
	 */
	public ServiceGetter(IInternalAccess component, long delay, Class<T> type, String scope)
	{
		this.component = component;
		this.delay = delay;
		this.type = type;
		this.scope = scope;
	}
	
	/**
	 *  Get or search the service with a delay in case not found.
	 */
	public IFuture<T> getService()
	{
		final Future<T> ret = new Future<T>();

		if(service==null)
		{
			if(lastsearch==0 || System.currentTimeMillis()>lastsearch+delay)
			{
				lastsearch = System.currentTimeMillis();
				
				SServiceProvider.getService(component.getServiceContainer(), type, scope)
					.addResultListener(component.createResultListener(new IResultListener<T>()
				{
					public void resultAvailable(T result)
					{
						service = result;
						ret.setResult(service);
					}
					
					public void exceptionOccurred(Exception exception)
					{
	//					exception.printStackTrace();
						ret.setResult(null);
					}
				}));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(service);
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
}
