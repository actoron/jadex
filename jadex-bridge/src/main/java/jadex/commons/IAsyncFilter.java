package jadex.commons;

import jadex.commons.future.IFuture;

/**
 *  Generic filter interface.
 */
public interface IAsyncFilter<T>
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IAsyncFilter<Object> ALWAYS = new AlwaysFilter();

	/** A filter that always returns true. */
	public static class AlwaysFilter<E> implements IAsyncFilter<E>
	{
		public IFuture<Boolean> filter(E obj)
		{
			return IFuture.TRUE;
		}
	}
	
	/** A filter that always returns false. */
	public static final IAsyncFilter<Object> NEVER = new NeverFilter();
	
	/** A filter that always returns false. */
	public static class NeverFilter<E> implements IAsyncFilter<E>
	{
		public IFuture<Boolean> filter(E obj)
		{
			return IFuture.FALSE;
		}
	}

	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture<Boolean> filter(T obj);
}
