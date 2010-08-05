package jadex.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Select service by id.
 */
public class IdResultSelector implements IResultSelector
{
	//-------- attributes --------
	
	/** The id. */
	protected Object sid;
	
	//-------- constructors --------
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector()
	{
	}
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	//-------- methods --------
	
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public void	selectServices(Map services, Collection results)
	{
		for(Iterator keys=services.keySet().iterator(); keys.hasNext(); )
		{
			Class key = (Class)keys.next();
			for(Iterator vals=((Collection)services.get(key)).iterator(); vals.hasNext(); )
			{
				IService tmp = (IService)vals.next();
				if(sid.equals(tmp.getServiceIdentifier()))
				{
					results.add(tmp);
					break;
				}
			}
		}
	}
	
	/**
	 *  Get the result.
	 *  Called once after search is finished.
	 *  @param results	The collection of selected services.
	 *  @return A single service or a list of services.
	 */
	public Object getResult(Collection results)
	{
		return results.size()>0? results.toArray()[0]: null;
	}
	
	/**
	 *  Test if the search result is sufficient to stop the search.
	 *  @param results	The collection of selected services.
	 *  @return True, if the search should be stopped.
	 */
	public boolean isFinished(Collection results)
	{
		return results.size()>0;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return this.getClass().getName()+sid;
	}
	
	/**
	 *  Get the service identifier.
	 *  @return the service identifier.
	 */
	public Object getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the service identifier.
	 *  @param sid The service identifier to set.
	 */
	public void setServiceIdentifier(Object sid)
	{
		this.sid = sid;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "IdResultSelector(id="+sid+")";
	}
}
