package jadex.commons;

/**
 *  Filter with fixed return value.
 */
public class ConstantFilter implements IFilter
{
	/** The return value. */
	protected boolean value;
	
	/**
	 *  Create filter instance.
	 */
	public ConstantFilter()
	{
	}
	
	/**
	 *  Create filter instance.
	 */
	public ConstantFilter(boolean value)
	{
		this.value = value;
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj)
	{
		return value;
	}

	/**
	 *  Get the value.
	 *  @return the value.
	 */
	public boolean isValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(boolean value)
	{
		this.value = value;
	}
	
}
