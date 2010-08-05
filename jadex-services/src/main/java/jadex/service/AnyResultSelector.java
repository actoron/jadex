package jadex.service;

import jadex.commons.ConstantFilter;
import jadex.commons.IFilter;

/**
 *  Select first service to be returned as result of service search.
 */
public class AnyResultSelector extends BasicResultSelector
{
	//-------- constructors --------
	
	/**
	 *  Create a id result listener.
	 */
	public AnyResultSelector()
	{
	}
	
	/**
	 *  Create a id result listener.
	 */
	public AnyResultSelector(boolean oneresult)
	{
		this(oneresult, true);
	}
	
	/**
	 *  Create a id result listener.
	 */
	public AnyResultSelector(boolean oneresult, boolean onlylocal)
	{
		super(new ConstantFilter(true), oneresult, onlylocal);
	}
}
