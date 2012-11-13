package jadex.bridge.service.search;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;
import jadex.commons.IRemoteFilter;
import jadex.commons.Tuple;

import java.util.Collection;
import java.util.Map;


/**
 *  Select first service to be returned as result of service search.
 */
public class TypeResultSelector<T> extends BasicResultSelector<T>
{
	//-------- attributes --------
		
	/** The type. */
	protected Class<?> type;
	
	//-------- constructors --------
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector()
	{
	}
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class<?> type)
	{
		this(type, true);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class<?> type, boolean oneresult)
	{
		this(type, oneresult, false);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class<?> type, boolean oneresult, boolean remote)
	{
		this(type, oneresult, false, null);
	}
	
	/**
	 *  Create a type result listener.
	 */
	public TypeResultSelector(Class<?> type, boolean oneresult, boolean remote, IFilter<T> filter)
	{
		super(IRemoteFilter.ALWAYS, oneresult, remote, filter);
		this.type = type;
	}
	
	//-------- methods --------


	/**
	 *  Get all services of the map as linear collection.
	 */
	public IService[] generateServiceArray(Map<Class<?>, Collection<IService>> servicemap)	
	{
		Collection<IService> tmp = servicemap.get(type);
		return tmp==null? IService.EMPTY_SERVICES: (IService[])tmp.toArray(new IService[0]);
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return new Tuple(new Object[]{this.getClass().getName(), filter, 
			oneresult? Boolean.TRUE: Boolean.FALSE, oneresult? Boolean.TRUE: Boolean.FALSE, type.getName()});
	}
	
	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "TypeResultSelector(type=" + type + ", oneresult=" + oneresult
			+ ", remote=" + remote + ")";
	}
}

