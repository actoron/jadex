package jadex.platform.service.servicepool;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceScope;
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
	
	/** The publication scope. */
	protected ServiceScope publicationscope;
	
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
		this(workermodel, servicetype, null, poolstrategy, publishinfo, argnames, argvals);
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
	public PoolServiceInfo setWorkermodel(String workermodel)
	{
		this.workermodel = workermodel;
		return this;
	}

	/**
	 *  Get the servicetype.
	 *  return The servicetype.
	 */
	public ClassInfo getServiceType()
	{
		return servicetype;
	}

	/**
	 *  Set the servicetype. 
	 *  @param servicetype The servicetype to set.
	 */
	public PoolServiceInfo setServiceType(ClassInfo servicetype)
	{
		this.servicetype = servicetype;
		return this;
	}
	
	/**
	 *  Set the servicetype. 
	 *  @param servicetype The servicetype to set.
	 */
	public PoolServiceInfo setServiceType(Class<?> servicetype)
	{
		this.servicetype = new ClassInfo(servicetype);
		return this;
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
	public PoolServiceInfo setPublishInfo(PublishInfo publishinfo)
	{
		this.publishinfo = publishinfo;
		return this;
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
	public PoolServiceInfo setPoolStrategy(Object poolstrategy)
	{
		this.poolstrategy = poolstrategy;
		return this;
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
	public PoolServiceInfo setCreationInfo(CreationInfo info) 
	{
		this.info = info;
		return this;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return info==null? null: info.getArguments();//arguments;
	}

	/**
	 *  Get the publication scope.
	 *  @return the publicationscope
	 */
	public ServiceScope getPublicationScope()
	{
		return publicationscope;
	}

	/**
	 *  Set the publication scope.
	 *  @param publicationScope the publicationScope to set
	 */
	public PoolServiceInfo setPublicationScope(ServiceScope publicationscope)
	{
		this.publicationscope = publicationscope;
		return this;
	}

	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 * /
	//public PoolServiceInfo setArguments(Map<String, Object> arguments)
	public PoolServiceInfo setArgs(Map<String, Object> arguments)
	{
		if(info!=null)
			info.setArguments(arguments);
//		this.arguments = arguments;
		return this;
	}*/

	
	
}
