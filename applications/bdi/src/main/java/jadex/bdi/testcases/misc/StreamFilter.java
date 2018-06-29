package jadex.bdi.testcases.misc;

import jadex.bridge.IConnection;
import jadex.commons.IFilter;

/**
 * 
 */
public class StreamFilter implements IFilter
{
	/**
	 * 
	 */
	public boolean filter(Object obj)
	{
		return obj instanceof IConnection;
	}
}
