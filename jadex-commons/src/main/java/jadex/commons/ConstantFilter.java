package jadex.commons;

/**
 *  Filter with fixed return value.
 */
public class ConstantFilter implements IFilter
{
	//-------- attributes --------
	
	/** The return value. */
	protected boolean value;
	
	//-------- constructors --------
	
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
	
	//-------- methods --------
	
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
	
	/**
	 *  Is equal?
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ConstantFilter && value==((ConstantFilter)obj).isValue();
	}
	
	/**
	 *  Hash code.
	 */
	public int hashCode()
	{
		return 31 + 31*(getClass().hashCode()+ (value ? 1 : 2));
	}
}
