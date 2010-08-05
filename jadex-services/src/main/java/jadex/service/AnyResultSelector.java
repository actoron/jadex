package jadex.service;

import java.util.Collection;
import java.util.Map;

/**
 *  Select first service to be returned as result of service search.
 */
public class AnyResultSelector implements IResultSelector
{
	//-------- attributes --------
	
	/** The one result flag. */
	protected boolean oneresult;
	
	//-------- constructors --------
	
	/**
	 *  Create a type result listener.
	 */
	public AnyResultSelector()
	{
		this(false);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public AnyResultSelector(boolean oneresult)
	{
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
		Class[]	keys	= (Class[])services.keySet().toArray(new Class[services.keySet().size()]);
		if(oneresult)
		{
			boolean	found	= false;
			for(int i=0; !found && i<keys.length; i++)
			{
				Collection	coll	= (Collection)services.get(keys[i]);
				if(coll!=null && !coll.isEmpty())
				{
					results.add(coll.toArray()[0]);
					found	= true;
				}
			}
		}
		else
		{
			for(int i=0; i<keys.length; i++)
			{
				Collection	coll	= (Collection)services.get(keys[i]);
				if(coll!=null && !coll.isEmpty())
				{
					IService[] ares = (IService[])coll.toArray(new IService[coll.size()]); 
					for(int j=0; j<ares.length; j++)
					{
						results.add(ares[j]);
					}
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
		Object	ret	= oneresult? results.size()>0? results.toArray()[0]: null: results;
		return ret;
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
		return this.getClass().getName();
	}

	/**
	 *  Get the oneresult.
	 *  @return the oneresult.
	 */
	public boolean isOneResult()
	{
		return oneresult;
	}

	/**
	 *  Set the oneresult.
	 *  @param oneresult The oneresult to set.
	 */
	public void setOneResult(boolean oneresult)
	{
		this.oneresult = oneresult;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AnyResultSelector(oneresult=" + oneresult+ ")";
	}
}
