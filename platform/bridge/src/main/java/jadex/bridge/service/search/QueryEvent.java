package jadex.bridge.service.search;

/**
 *  Query event for signaling when a query is added or removed.
 */
public class QueryEvent
{
	/** Query was added event. */
	public static final int QUERY_ADDED = 0;
	
	/** Query was removed event. */
	public static final int QUERY_REMOVED = 1;
	
	/** Event type. */
	protected int type;
	
	/** The service. */
	protected ServiceQuery<?> query;
	
	/** Bean constructor. */
	public QueryEvent()
	{
	}
	
	/**
	 *  Creates the query event.
	 *  @param query The affected query.
	 *  @param eventtype The event type.
	 */
	public QueryEvent(ServiceQuery<?> query, int eventtype)
	{
		this.query = query;
		this.type = eventtype;
	}

	/**
	 *  Gets the event type.
	 *  @return The event type.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 *  Sets the event type.
	 *  @param eventtype The event type.
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 *  Gets the query.
	 *  @return The query.
	 */
	public ServiceQuery<?> getQuery()
	{
		return query;
	}

	/**
	 *  Sets the query.
	 *  @param query The query.
	 */
	public void setQuery(ServiceQuery<?> query)
	{
		this.query = query;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "QueryEvent [type=" + type + ", query=" + query + "]";
	}
}
