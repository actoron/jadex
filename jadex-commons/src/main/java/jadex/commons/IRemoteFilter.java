package jadex.commons;

/**
 *  Generic filter interface.
 */
public interface IRemoteFilter
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IRemoteFilter ALWAYS = new AlwaysFilter();
	
	/** A filter that always returns true. */
	public static class AlwaysFilter implements IRemoteFilter
	{
		public IFuture filter(Object obj) 
		{
			return new Future(Boolean.TRUE);
		}
	}
	
	/** A filter that always returns false. */
	public static final IRemoteFilter NEVER = new NeverFilter();
	
	/** A filter that always returns false. */
	public static class NeverFilter implements IRemoteFilter
	{
		public IFuture filter(Object obj) 
		{
			return new Future(Boolean.FALSE);
		}
	}

	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture filter(Object obj);
}
