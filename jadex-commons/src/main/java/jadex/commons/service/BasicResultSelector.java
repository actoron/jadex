package jadex.commons.service;

import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  Select one or more services according to a filter.
 */
public class BasicResultSelector implements IResultSelector
{
	//-------- attributes --------
	
	/** The type. */
	protected IFilter filter;
	
	/** The one result flag. */
	protected boolean oneresult;
	
	/** The only local services flag. */
	protected boolean remote;
	
	//-------- constructors --------
	
	/**
	 *  Create a type result listener.
	 */
	public BasicResultSelector()
	{
	}
	
	/**
	 *  Create a type result listener.
	 */
	public BasicResultSelector(IFilter filter)
	{
		this(filter, true);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public BasicResultSelector(IFilter filter, boolean oneresult)
	{
		this(filter, oneresult, false);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public BasicResultSelector(IFilter filter, boolean oneresult, boolean remote)
	{
		this.filter = filter;
		this.oneresult = oneresult;
		this.remote = remote;
	}
	
	//-------- methods --------
	
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public void	selectServices(Map servicemap, Collection results)
	{
		IFilter fil = filter;
		if(!remote)
		{
			if(fil!=null)
			{
				fil = new ComposedFilter(new IFilter[]{filter, ProxyFilter.PROXYFILTER}, ComposedFilter.AND);
			}
			else
			{
				fil = ProxyFilter.PROXYFILTER;
			}
		}
		
		IService[] services = generateServiceArray(servicemap);
		
		if(services!=null)
		{
//			if(services.length>0)
//				System.out.println("adding: "+SUtil.arrayToString(services)+" "+this);
			if(oneresult && services.length>0)
			{				
				for(int i=0; i<services.length; i++)
				{
					if(fil.filter(services[i]))
					{
						results.add(services[i]);
						break;
					}
				}
			}
			else
			{
//				if(services.length>0)
//					System.out.println("adding: "+SUtil.arrayToString(services)+" "+this);
				for(int i=0; i<services.length; i++)
				{
					if(fil.filter(services[i]) && !results.contains(services[i]))
					{
//						if(services[i].getClass().getName().indexOf("Shop")!=-1)
//							System.out.println("add: "+services[i]+" to: "+results);
						results.add(services[i]);
					}
				}
			}
		}
	}
	
	/**
	 *  Get all services of the map as linear collection.
	 */
	public IService[] generateServiceArray(Map servicemap)
	{
		List ret = new ArrayList();
		Object[] keys = servicemap.keySet().toArray();
		for(int i=0; i<keys.length; i++)
		{
			Collection coll = (Collection)servicemap.get(keys[i]);
			if(coll!=null)
			{
				Object[] vals = coll.toArray();
				for(int j=0; j<vals.length; j++)
				{
					ret.add(vals[j]);
				}
			}
		}
		
		return (IService[])ret.toArray(new IService[ret.size()]);
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
		return new Tuple(new Object[]{this.getClass().getName(), filter, 
			oneresult? Boolean.TRUE: Boolean.FALSE, remote? Boolean.TRUE: Boolean.FALSE});
	}

	/**
	 *  Get the filter.
	 *  @return the filter.
	 */
	public IFilter getFilter()
	{
		return filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter filter)
	{
		this.filter = filter;
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
	
	// NOTE! This methods currently must be commented for remote searches
	// to work. Otherwise, remote=true will lead to cache hits of proxies.
	
//	/**
//	 *  Get the remote.
//	 *  @return the remote.
//	 */
//	public boolean isRemote()
//	{
//		return remote;
//	}
//
//	/**
//	 *  Set the remote.
//	 *  @param remote The remote to set.
//	 */
//	public void setRemote(boolean remote)
//	{
//		this.remote = remote;
//	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return getClass().getName()+"(filter=" + filter + ", oneresult=" + oneresult+ ", remote=" + remote + ")";
	}
}




