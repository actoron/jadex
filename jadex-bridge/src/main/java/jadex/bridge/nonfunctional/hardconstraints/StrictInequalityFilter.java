package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Hard constraint filter for strict inequalities (> and <).
 *
 */
public class StrictInequalityFilter extends AbstractConstraintFilter
{
	/** Value used during comparison. */
	protected int comparevalue;
	
	/**
	 *  Creates a constant value filter.
	 */
	public StrictInequalityFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public StrictInequalityFilter(boolean less)
	{
		comparevalue = less? -1 : 1;
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public StrictInequalityFilter(String propname, Object value)
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
		final Future<Boolean> ret = new Future<Boolean>();
		if (value instanceof Comparable<?>)
		{
			int val = (int) Math.signum(((Comparable<Object>) value).compareTo(this.value));
			ret.setResult(val == comparevalue);
		}
		else
		{
			ret.setException(new RuntimeException("Property is not a Comparable value: " + propname));
		}
		return ret;
	}
}
