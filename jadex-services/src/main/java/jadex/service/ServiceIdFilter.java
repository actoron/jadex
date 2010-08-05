package jadex.service;

import jadex.commons.IFilter;

/**
 *  Filter for service ids.
 */
public class ServiceIdFilter implements IFilter
{
	/** The service id. */
	protected Object sid;
	
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
	public void setId(Class sid)
	{
		this.sid = sid;
	}

	/**
	 *  Test if service is a proxy.
	 */
	public boolean filter(Object obj)
	{
		return obj instanceof IService && ((IService)obj).getServiceIdentifier().equals(sid);
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
		return obj instanceof IService && ((IService)obj).getServiceIdentifier().equals(sid);
	}
}
