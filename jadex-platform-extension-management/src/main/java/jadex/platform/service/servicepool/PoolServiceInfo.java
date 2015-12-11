package jadex.platform.service.servicepool;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IPoolStrategy;


/**
 *  Info struct for a service in the pool.
 */
public class PoolServiceInfo 
{
	/** The worker agent model. */
	protected String workermodel;
	
	/** The service interface type. */
	protected ClassInfo servicetype;
	
	/** The pool strategy. */
	protected Object poolstrategy; // IPoolStrategy or IGlobalPoolStrategy
	
	/** The publication info. */
	protected PublishInfo publishinfo;

	/** The creation info for the worker. */
	protected CreationInfo info;
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype)
	{
		this(workermodel, servicetype, null, null);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(Class<?> workermodel, Class<?> servicetype)
	{
		this(workermodel.getName()+".class", servicetype, null, null);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, PublishInfo publishinfo)
	{
		this(workermodel, servicetype, null, publishinfo, null, null);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, Object poolstrategy)
	{
		this(workermodel, servicetype, poolstrategy, null, null, null);
	}

	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, String[] argnames, Object[] argvals)
	{
		this(workermodel, servicetype, null, null, argnames, argvals);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype,
		Object poolstrategy, PublishInfo publishinfo, String[] argnames, Object[] argvals)
	{
		this(workermodel, servicetype, null, null, publishinfo, argnames, argvals);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, IResourceIdentifier rid,
		Object poolstrategy, PublishInfo publishinfo, String[] argnames, Object[] argvals)
	{
		this.workermodel = workermodel;
		this.servicetype = new ClassInfo(servicetype);
		this.poolstrategy = poolstrategy;
		this.publishinfo = publishinfo;
		this.info = new CreationInfo();
		if(argnames!=null && argnames.length>0)
		{
			Map<String, Object> arguments = new HashMap<String, Object>();
			for(int i=0; i<argnames.length; i++)
			{
				arguments.put(argnames[i], argvals[i]);
			}
			info.setArguments(arguments);
		}
		info.setResourceIdentifier(rid);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(CreationInfo info, String workermodel, Class<?> servicetype,
		IPoolStrategy poolstrategy, PublishInfo publishinfo)
	{
		this.info = info;
		this.workermodel = workermodel;
		this.servicetype = new ClassInfo(servicetype);
		this.poolstrategy = poolstrategy;
		this.publishinfo = publishinfo;
	}

	/**
	 *  Get the workermodel.
	 *  return The workermodel.
	 */
	public String getWorkermodel()
	{
		return workermodel;
	}

	/**
	 *  Set the workermodel. 
	 *  @param workermodel The workermodel to set.
	 */
	public void setWorkermodel(String workermodel)
	{
		this.workermodel = workermodel;
	}

	/**
	 *  Get the servicetype.
	 *  return The servicetype.
	 */
	public ClassInfo getServicetype()
	{
		return servicetype;
	}

	/**
	 *  Set the servicetype. 
	 *  @param servicetype The servicetype to set.
	 */
	public void setServicetype(ClassInfo servicetype)
	{
		this.servicetype = servicetype;
	}

	/**
	 *  Get the publish info.
	 *  return The publish info.
	 */
	public PublishInfo getPublishInfo()
	{
		return publishinfo;
	}

	/**
	 *  Set the publish info. 
	 *  @param publishinfo The publish info to set.
	 */
	public void setPublishInfo(PublishInfo publishinfo)
	{
		this.publishinfo = publishinfo;
	}

	/**
	 *  Get the poolstrategy.
	 *  return The poolstrategy.
	 */
	public Object getPoolStrategy()
	{
		return poolstrategy;
	}

	/**
	 *  Set the pool strategy. 
	 *  @param poolstrategy The pool strategy to set.
	 */
	public void setPoolStrategy(Object poolstrategy)
	{
		this.poolstrategy = poolstrategy;
	}

	/**
	 *  Get the info.
	 *  @return the info
	 */
	public CreationInfo getCreationInfo() 
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set
	 */
	public void setCreationInfo(CreationInfo info) 
	{
		this.info = info;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return info==null? null: info.getArguments();//arguments;
	}
//
//	/**
//	 *  Set the arguments.
//	 *  @param arguments The arguments to set.
//	 */
//	public void setArguments(Map<String, Object> arguments)
//	{
//		info.setArguments(arguments);
////		this.arguments = arguments;
//	}
	
	
}
