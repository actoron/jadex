package jadex.platform.service.servicepool;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.PublishInfo;
import jadex.commons.IPoolStrategy;

import java.util.HashMap;
import java.util.Map;


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
	protected IPoolStrategy poolstrategy;
	
	/** The publication info. */
	protected PublishInfo publishinfo;

	/** The argument names. */
	protected Map<String, Object> arguments;
	
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
	public PoolServiceInfo(String workermodel, Class<?> servicetype, PublishInfo publishinfo)
	{
		this(workermodel, servicetype, null, publishinfo, null, null);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, IPoolStrategy poolstrategy)
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
		IPoolStrategy poolstrategy, PublishInfo publishinfo, String[] argnames, Object[] argvals)
	{
		this.workermodel = workermodel;
		this.servicetype = new ClassInfo(servicetype);
		this.poolstrategy = poolstrategy;
		this.publishinfo = publishinfo;
		if(argnames!=null && argnames.length>0)
		{
			this.arguments = new HashMap<String, Object>();
			for(int i=0; i<argnames.length; i++)
			{
				arguments.put(argnames[i], argvals[i]);
			}
		}
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
	public IPoolStrategy getPoolStrategy()
	{
		return poolstrategy;
	}

	/**
	 *  Set the pool strategy. 
	 *  @param poolstrategy The pool strategy to set.
	 */
	public void setPoolStrategy(IPoolStrategy poolstrategy)
	{
		this.poolstrategy = poolstrategy;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return arguments;
	}

	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(Map<String, Object> arguments)
	{
		this.arguments = arguments;
	}
}
