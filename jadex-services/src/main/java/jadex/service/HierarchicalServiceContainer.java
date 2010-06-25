package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
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
	public IFuture getServices(final Class type)
	{
		final Future ret = new Future();
		
		if(parent!=null)
		{
			parent.getServices(type).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					Collection res = (Collection)result;
					
					if(res==null || res.size()==0)
					{
						Map tmp = getServiceMap(type);
						if(tmp != null)
							res	= tmp.values();
						else
							res	= Collections.EMPTY_SET;
				//			throw new RuntimeException("No services found of type: " + type);
					}
					
					ret.setResult(res);
				}
			});
		}
		else
		{
			Collection res;
			Map tmp = getServiceMap(type);
			if(tmp != null)
				res	= tmp.values();
			else
				res	= Collections.EMPTY_SET;
	//			throw new RuntimeException("No services found of type: " + type);
			ret.setResult(res);
		}
		
		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public IFuture getService(final Class type, final String name)
	{
		final Future ret = new Future();
		
		if(parent!=null)
		{
			parent.getService(type, name).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					Object res = result;
					
					if(result==null)
					{
						Map tmp = getServiceMap(type);
						if(tmp != null)
							res = tmp.get(name);
				//		if(ret == null)
				//			throw new RuntimeException("Service not found");
					}
					
					ret.setResult(res);
				}
			});
		}
		else
		{
			Object res = null; 
			Map tmp = getServiceMap(type);
			if(tmp != null)
				res = tmp.get(name);
			ret.setResult(res);
		}
		
		return ret;
	}

	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public IFuture getService(final Class type)
	{
		final Future ret = new Future();
		
		if(parent!=null)
		{
			parent.getService(type).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					Object res = null;
					if(ret==null)
					{
						Map tmp = getServiceMap(type);
						if(tmp != null && !tmp.isEmpty())
							res = tmp.values().iterator().next();
				//		if(ret == null)
				//			throw new RuntimeException("Service not found");
					}
					
					ret.setResult(res);
				}
			});
		}
		else
		{
			Object res = null;
			Map tmp = getServiceMap(type);
			if(tmp != null && !tmp.isEmpty())
				res = tmp.values().iterator().next();
	//		if(ret == null)
	//			throw new RuntimeException("Service not found");
			ret.setResult(res);
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
						try
						{
							service.shutdownService(null); // Todo: use result listener?
						}
						catch(Exception e)
						{
							
						}
					}
				}
			}
		}
		
		// Shutdown parent container.
		if(parent!=null)
			parent.shutdown(listener);
		else if(listener!=null)
			listener.resultAvailable(this, null);
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
