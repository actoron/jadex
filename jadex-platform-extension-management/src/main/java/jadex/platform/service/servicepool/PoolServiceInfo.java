package jadex.platform.service.servicepool;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.PublishInfo;
import jadex.commons.DefaultPoolStrategy;
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
	protected IPoolStrategy poolstrategy;
	
	/** The publication info. */
	protected PublishInfo publishinfo;

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
		this(workermodel, servicetype, null, publishinfo);
	}
	
	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype, IPoolStrategy poolstrategy)
	{
		this(workermodel, servicetype, poolstrategy, null);
	}

	/**
	 *  Create a new PoolServiceInfo.
	 */
	public PoolServiceInfo(String workermodel, Class<?> servicetype,
		IPoolStrategy poolstrategy, PublishInfo publishinfo)
	{
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
	
}
