package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;

public class ConstantValueFilter implements IFilter<IService>
{
	/** Name of the value being kept constant. */
	protected String valuename;
	
	/** The value once it is bound. */
	protected Object value;
	
	/**
	 *  Creates a constant value filter.
	 */
	public ConstantValueFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public ConstantValueFilter(String valuename)
	{
		this.valuename = valuename;
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(IService service)
	{
		return false;
	}
	
	/**
	 *  Binds the constant value.
	 *  
	 *  @param value The value
	 */
	public void bind(Object value)
	{
		this.value = value;
	}
	
	/**
	 *  Unbinds the constant value.
	 */
	public void unbind()
	{
		this.value = null;
	}

	/**
	 *  Gets the valuename.
	 *
	 *  @return The valuename.
	 */
	public String getValueName()
	{
		return valuename;
	}

	/**
	 *  Sets the valuename.
	 *
	 *  @param valuename The valuename to set.
	 */
	public void setValueName(String valuename)
	{
		this.valuename = valuename;
	}

	/**
	 *  Gets the value.
	 *
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *  Sets the value.
	 *
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	
}
