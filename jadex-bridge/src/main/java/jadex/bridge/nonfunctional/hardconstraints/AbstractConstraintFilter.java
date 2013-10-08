package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.IRemoteFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public abstract class AbstractConstraintFilter implements IRemoteFilter<IService>
{
	/** Name of the property being kept constant. */
	protected String propname;
	
	/** The value once it is bound. */
	protected Object value;
	
	/**
	 *  Creates a constant value filter.
	 */
	public AbstractConstraintFilter()
	{
	}
	
	/**
	 *  Creates a constant value filter.
	 */
	public AbstractConstraintFilter(String propname, Object value)
	{
		this.propname = propname;
		this.value = value;
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public final IFuture<Boolean> filter(IService service)
	{
		if (getValue() == null)
		{
			return new Future<Boolean>(true);
		}
		return doFilter(service);
	}
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public abstract IFuture<Boolean> doFilter(IService service);

	/**
	 *  Gets the valuename.
	 *
	 *  @return The valuename.
	 */
	public String getValueName()
	{
		return propname;
	}

	/**
	 *  Sets the valuename.
	 *
	 *  @param valuename The valuename to set.
	 */
	public void setValueName(String valuename)
	{
		this.propname = valuename;
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
