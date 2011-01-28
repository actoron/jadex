package jadex.commons;

/**
 *  Generic filter interface.
 */
public interface IRemoteFilter
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IRemoteFilter ALWAYS = new IRemoteFilter()
	{
		public static final String XML_CLASSNAME = "always";
		public IFuture filter(Object obj) 
		{
			return new Future(Boolean.TRUE);
		}
	};
	
	/** A filter that always returns false. */
	public static final IRemoteFilter NEVER = new IRemoteFilter()
	{
		public static final String XML_CLASSNAME = "never";
		public IFuture filter(Object obj) 
		{
			return new Future(Boolean.FALSE);
		}
	};
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture filter(Object obj);
}
