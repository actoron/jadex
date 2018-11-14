package jadex.bridge.service.search;

import jadex.bridge.service.IService;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;

/**
 *  Filter for service ids.
 */
public class ServiceIdFilter implements IAsyncFilter
{
	//-------- attributes --------
	
	/** The service id. */
	protected Object sid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new filter.
	 */
	public ServiceIdFilter()
	{
	}
	
	/**
	 *  Create a new filter.
	 */
	public ServiceIdFilter(Object sid)
	{
		this.sid = sid;
	}

	//-------- methods --------
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public Object getId()
	{
		return sid;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(Object sid)
	{
		this.sid = sid;
	}

	/**
	 *  Test if service is a proxy.
	 */
	public IFuture<Boolean> filter(Object obj)
	{
		return obj instanceof IService && ((IService)obj).getServiceId().equals(sid) ? IFuture.TRUE : IFuture.FALSE;
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return sid.hashCode();
	}

	/**
	 *  Test if an object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof IService && ((IService)obj).getServiceId().equals(sid);
	}
}
