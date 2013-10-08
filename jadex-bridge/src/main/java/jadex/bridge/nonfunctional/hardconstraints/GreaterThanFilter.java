package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class GreaterThanFilter extends AbstractConstraintFilter
{
	/**
	 *  Creates a constant value filter.
	 */
	public GreaterThanFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public GreaterThanFilter(String propname, Object value)
	{
		this.propname = propname;
		this.value = value;
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public IFuture<Boolean> doFilter(IService service)
	{
		return new Future<Boolean>(true);
	}
}
