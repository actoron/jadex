package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class InequalityFilter extends StrictInequalityFilter
{
	/**
	 *  Creates a constant value filter.
	 */
	public InequalityFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public InequalityFilter(boolean less)
	{
		super(less);
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
			ret.setResult((val == comparevalue) || (val == 0));
		}
		else
		{
			ret.setException(new RuntimeException("Property is not a Comparable value: " + propname));
		}
		return ret;
	}
}
