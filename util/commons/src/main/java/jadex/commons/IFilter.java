package jadex.commons;

/**
 *  Generic filter interface.
 */
public interface IFilter<T>
{
	//-------- constants --------
	
	/** A filter that always returns true. */
	public static final IFilter<Object> ALWAYS = new IFilter<Object>()
	{
		public boolean filter(Object obj) 
		{
			return true;
		}
	};
	
	/** A filter that always returns false. */
	public static final IFilter<Object> NEVER = new IFilter<Object>()
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
	public boolean filter(T obj);
}
