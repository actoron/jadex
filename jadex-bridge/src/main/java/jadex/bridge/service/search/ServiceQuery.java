package jadex.bridge.service.search;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IAsyncFilter;

/**
 * 
 */
public class ServiceQuery<T>
{
	/** The service type. */
	protected ClassInfo type;
	
	/** The search scope. */
	protected String scope;
	
	/** Filter for checking further service attributes. */
	protected IAsyncFilter<T> filter;
	
	/** The query owner. */
	protected IComponentIdentifier owner;
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery()
	{
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(Class<T> type, String scope, IAsyncFilter<T> filter, IComponentIdentifier owner)
	{
		this(new ClassInfo(type), scope, filter, owner);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(ClassInfo type, String scope, IAsyncFilter<T> filter, IComponentIdentifier owner)
	{
		this.type = type;
		this.scope = scope;
		this.filter = filter;
		this.owner = owner;
	}

	/**
	 *  Get the type.
	 *  @return The type
	 */
	public ClassInfo getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set
	 */
	public void setType(ClassInfo type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	/**
	 *  Get the filter.
	 *  @return The filter
	 */
	public IAsyncFilter<T> getFilter()
	{
		return filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set
	 */
	public void setFilter(IAsyncFilter<T> filter)
	{
		this.filter = filter;
	}

	/**
	 *  Get the owner.
	 *  @return The owner
	 */
	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	/**
	 *  Set the owner.
	 *  @param owner The owner to set
	 */
	public void setOwner(IComponentIdentifier owner)
	{
		this.owner = owner;
	}
}
