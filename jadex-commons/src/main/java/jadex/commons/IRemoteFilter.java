package jadex.commons;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Generic filter interface.
 */
public interface IRemoteFilter<T>
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IRemoteFilter ALWAYS = new AlwaysFilter();
	
	/** A filter that always returns true. */
	public static class AlwaysFilter implements IRemoteFilter
	{
		public IFuture<Boolean> filter(Object obj) 
		{
			return new Future<Boolean>(Boolean.TRUE);
		}
	}
	
	/** A filter that always returns false. */
	public static final IRemoteFilter NEVER = new NeverFilter();
	
	/** A filter that always returns false. */
	public static class NeverFilter implements IRemoteFilter
	{
		public IFuture<Boolean> filter(Object obj) 
		{
			return new Future<Boolean>(Boolean.FALSE);
		}
	}

	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture<Boolean> filter(T obj);
}
