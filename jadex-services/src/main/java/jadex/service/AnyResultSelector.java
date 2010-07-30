package jadex.service;

import java.util.Arrays;
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
	
	/** The flag if type should be part of result. */
	protected boolean includetype;
	
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
		this(oneresult, false);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public AnyResultSelector(boolean oneresult, boolean includetype)
	{
		this.oneresult = oneresult;
		this.includetype = includetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public void	selectServices(Map services, Collection results)
	{
		Object[]	keys	= services.keySet().toArray();
		if(oneresult)
		{
			boolean	found	= false;
			for(int i=0; !found && i<keys.length; i++)
			{
				Collection	coll	= (Collection)services.get(keys[i]);
				if(coll!=null && !coll.isEmpty())
				{
					if(includetype)
					{
						results.add(new Object[]{keys[i], coll.toArray()[0]});
					}
					else
					{
						results.add(coll.toArray()[0]);
					}
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
					Object[]	ares	= coll.toArray(); 
					if(includetype)
					{
						for(int j=0; j<ares.length; j++)
						{
							results.add(new Object[]{keys[i], ares[j]});
						}
					}
					else
					{
						results.addAll(Arrays.asList(ares));
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
	 *  Get the includetype.
	 *  @return the includetype.
	 */
	public boolean isIncludeType()
	{
		return includetype;
	}

	/**
	 *  Set the includetype.
	 *  @param includetype The includetype to set.
	 */
	public void setIncludeType(boolean includetype)
	{
		this.includetype = includetype;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AnyResultSelector(oneresult=" + oneresult+ ")";
	}
}
