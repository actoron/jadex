package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

public class ConstantValueFilter extends AbstractConstraintFilter
{
	/**
	 *  Creates a constant value filter.
	 */
	public ConstantValueFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public ConstantValueFilter(String propname, Object value)
	{
		this.propname = propname;
		this.value = value;
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture<Boolean> doFilter(IService service, Object value)
	{
		return getValue().equals(value) ? IFuture.TRUE : IFuture.FALSE;
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
}
