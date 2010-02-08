package jadex.service;

import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 *  The hierarchical service container allows to specifiy a parent
 *  container, which will first be used for fetching services.
 * 
 *  It also allows to reuse service from the parent if the init checks
 *  if they are already available in the parent container.
 */
public class HierarchicalServiceContainer extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The parent service container. */
	protected IServiceContainer parent;
	
	/**
	 *  Create a new property service container.
	 */
	public void init(IServiceContainer parent)
	{
		setParent(parent);
	}
	
	//-------- methods --------
	
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public Collection getServices(Class type)
	{
		Collection	ret	= parent!=null? parent.getServices(type): null;
		
		if(ret==null || ret.size()==0)
		{
			Map tmp = getServiceMap(type);
			if(tmp != null)
				ret	= tmp.values();
			else
				ret	= Collections.EMPTY_SET;
	//			throw new RuntimeException("No services found of type: " + type);
		}
		
		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type, String name)
	{
		Object ret = parent!=null? parent.getService(type, name): null;

		if(ret==null)
		{
			Map tmp = getServiceMap(type);
			if(tmp != null)
			 ret = tmp.get(name);
	//		if(ret == null)
	//			throw new RuntimeException("Service not found");
		}
		return ret;
	}

	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type)
	{
		Object ret = parent!=null? parent.getService(type): null;

		if(ret==null)
		{
			Map tmp = getServiceMap(type);
			if(tmp != null && !tmp.isEmpty())
				ret = tmp.values().iterator().next();
	//		if(ret == null)
	//			throw new RuntimeException("Service not found");
		}
		return ret;
	}
	
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		// Start parent container.
		if(parent!=null)
			parent.start();
			
		// Start the services.
		if(services!=null)
		{
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
						service.startService();
					}
				}
			}
		}
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
		// Stop the services.
		if(services!=null)
		{
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
						service.shutdownService(null); // Todo: use result listener?
					}
				}
			}
		}
		
		// Shutdown parent container.
		if(parent!=null)
			parent.shutdown(listener);
	}

	//-------- additional methods --------
	
	/**
	 *  Set the parent.
	 *  @param parent The parent to set.
	 */
	public void setParent(IServiceContainer parent)
	{
		this.parent = parent;
	}
}
