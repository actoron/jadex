package jadex.commons;

/**
 *  Generic filter interface.
 */
public interface IFilter
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IFilter ALWAYS = new IFilter()
	{
		public boolean filter(Object obj) 
		{
			return true;
		}
	};
	
	/** A filter that always returns false. */
	public static final IFilter NEVER = new IFilter()
	{
		public boolean filter(Object obj) 
		{
			return false;
		}
	};
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj);
}
