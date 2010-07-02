package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.concurrent.CounterListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SJavaParser;

import java.util.Iterator;
import java.util.Map;

/**
 *  A service container is a simple infrastructure for a collection of
 *  services. It allows for starting/shutdowning the container and fetching
 *  service by their type/name.
 *  
 *  The configuration of services is not handled here but can be done
 *  e.g. in subclasses like the PropertyServiceContainer.
 */
public class BasicServiceContainer extends BasicServiceProvider implements IServiceContainer
{	
	//-------- attributes --------
	
	/** The platform name. */
	protected String name;

	//-------- interface methods --------
		
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
		// Start the services.
		if(services!=null && services.size()>0)
		{
			// Start notifies the future when all services have been started.
			CounterListener lis = new CounterListener(services.size(), new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					ret.setResult(null);
				}
			});
			for(Iterator it=services.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				Map tmp = (Map)services.get(key);
				if(tmp!=null)
				{
					for(Iterator it2=tmp.keySet().iterator(); it2.hasNext(); )
					{
						Object key2 = it2.next();
						IService service = (IService)tmp.get(key2);
						service.startService().addResultListener(lis);
					}
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 */
	public IFuture shutdown()
	{
		final Future ret = new Future();
		
		// Stop the services.
		if(services!=null && services.size()>0)
		{
			CounterListener lis = new CounterListener(services.size(), new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					ret.setResult(null);
				}
			});
			for(Iterator it = services.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				Map tmp = (Map)services.get(key);
				if(tmp != null)
				{
					for(Iterator it2 = tmp.keySet().iterator(); it2.hasNext();)
					{
						Object key2 = it2.next();
						IService service = (IService)tmp.get(key2);
	//					System.out.println("Service shutdown: " + service);
						service.shutdownService().addResultListener(lis); 
					}
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
