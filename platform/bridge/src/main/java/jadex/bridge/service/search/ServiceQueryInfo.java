package jadex.bridge.service.search;

import jadex.commons.future.TerminableIntermediateFuture;

/**
 *  Info with query and result future.
 */
public class ServiceQueryInfo<T>
{
	/** The query. */
	protected ServiceQuery<T> query;
	
	/** The futures. */
	protected TerminableIntermediateFuture<T> future;

	/**
	 *  Create a new query info.
	 */
	public ServiceQueryInfo(ServiceQuery<T> query, TerminableIntermediateFuture<T> future)
	{
		this.query = query;
		this.future = future;
	}

	/**
	 *  Get the query.
	 *  @return The query
	 */
	public ServiceQuery<T> getQuery()
	{
		return query;
	}

	/**
	 *  Set the query.
	 *  @param query The query to set
	 */
	public void setQuery(ServiceQuery<T> query)
	{
		this.query = query;
	}

	/**
	 *  Get the future.
	 *  @return The future
	 */
	public TerminableIntermediateFuture<T> getFuture()
	{
		return future;
	}

	/**
	 *  Set the future.
	 *  @param future The future to set
	 */
	public void setFuture(TerminableIntermediateFuture<T> future)
	{
		this.future = future;
	}
}