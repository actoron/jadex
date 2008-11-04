package jadex.rules.profiler;


/**
 *  Do nothing implementation for profiler when running in production mode.
 */
public class NoProfiler implements IProfiler
{
	//-------- IProfiler interface --------
	
	/**
	 *  Start profiling an item. Nesting (i.e. calling start() several times before
	 *  calling corresponding stops) is allowed.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	start(String type, Object item)
	{
	}

	/**
	 *  Stop profiling the current item.
	 *  Calls to stop() have match the last unstopped call to start(), with respect to the supplied types and items.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	stop(String type, Object item)
	{
	}
	
	/**
	 *  Get the current profiling infos from the given start index.
	 *  @param start	The start index (use 0 for all profiling infos).
	 */
	public ProfilingInfo[] getProfilingInfos(int start)
	{
		return new ProfilingInfo[0];
	}
}
