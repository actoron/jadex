package jadex.bridge.service.search;

import jadex.commons.IRemoteFilter;

/**
 *  Select first service to be returned as result of service search.
 */
public class AnyResultSelector<T> extends BasicResultSelector<T>
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
		this(oneresult, false);
	}
	
	/**
	 *  Create a id result listener.
	 */
	public AnyResultSelector(boolean oneresult, boolean remote)
	{
		super(IRemoteFilter.ALWAYS, oneresult, remote);
	}
}
