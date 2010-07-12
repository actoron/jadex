package jadex.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 *  Select first service to be returned as result of service search.
 */
public class TypeResultSelector implements IResultSelector
{
	//-------- attributes --------
	
	/** The type. */
	protected Class type;
	
	/** The one result flag. */
	protected boolean oneresult;
	
	//-------- constructors --------
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class type)
	{
		this(type, true);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class type, boolean oneresult)
	{
		this.type = type;
		this.oneresult = oneresult;
	}
	
	//-------- methods --------
	
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public void	selectServices(Map services, Collection results)
	{
		Collection res = (Collection)services.get(type);
		if(res!=null)
		{
			Object[]	ares	= res.toArray();
			if(oneresult && ares.length>0)
			{
				results.add(res.toArray()[0]);
			}
			else
			{
				results.addAll(Arrays.asList(ares));
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
		return oneresult? results.size()>0? results.toArray()[0]: null: results;
	}
	
	/**
	 *  Test if the search result is sufficient to stop the search.
	 *  @param results	The collection of selected services.
	 *  @return True, if the search should be stopped.
	 */
	public boolean	isFinished(Collection results)
	{
		return oneresult && results.size()>0;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return this.getClass().getName()+type.getName();
	}
}
