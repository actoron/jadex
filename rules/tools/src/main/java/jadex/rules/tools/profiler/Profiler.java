package jadex.rules.tools.profiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.SUtil;
import jadex.rules.state.IProfiler;

/**
 *  Default implementation for profiler.
 */
public class Profiler implements IProfiler, Serializable
{
	//-------- attributes --------
	
	/** The file name for saving. */
	protected String	name;
	
	/** The recorded (finished) profilings. */
	protected List	profiles;
	
	/** The stack of open profilings. */
	protected List	stack;
	
	/** The cut index (older entries are removed). */
	protected int	cut;
	
	//-------- constructors --------
	
	/**
	 *  Create a new profiler.
	 */
	public Profiler(String name)
	{
		this.name	= name;
		this.profiles	= new ArrayList();
		this.stack	= new ArrayList();
		this.cut	= 0;
	}
	
	//-------- IProfiler interface --------
	
	/**
	 *  Start profiling an item. Nesting (i.e. calling start() several times before
	 *  calling corresponding stops) is allowed.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item An optional element corresponding to the activity (e.g. the object type).
	 */
	public void	start(String type, Object item)
	{
		if(!IProfiler.TYPE_NODE.equals(type))
			return;
		ProfilingInfo	parent	= stack.isEmpty() ? null : (ProfilingInfo)stack.get(stack.size()-1);
		// System.nanoTime() : @since 1.5
		stack.add(new ProfilingInfo(type, item, parent, System.nanoTime(), 0));
//		stack.add(new ProfilingInfo(type, item, parent, System.currentTimeMillis(), 0));
//		System.out.println("start: "+stack);
	}

	/**
	 *  Stop profiling the current item.
	 *  Calls to stop() have match the last unstopped call to start(), with respect to the supplied types and items.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	stop(String type, Object item)
	{
		if(!IProfiler.TYPE_NODE.equals(type))
			return;
//		System.out.println("stop: "+stack);
		
		// Consistency check for start() / stop() call nesting.
		assert stack.size()!=0 && SUtil.equals(stack.get(stack.size()-1), new ProfilingInfo(type, item, null, 0, 0))
			: "Wrong nesting of calls to start/stop: "+type+", "+item+", "+stack;

		ProfilingInfo	current	= (ProfilingInfo)stack.get(stack.size()-1);
		// Calculate total and inherent time.
		// System.nanoTime() : @since 1.5
		current.time	= System.nanoTime() - current.time;
//		current.time	= System.currentTimeMillis() - current.time;
		current.inherent	= current.time - current.inherent;
						
		// Create profiling entry.
		stack.remove(stack.size()-1);
		synchronized(profiles)
		{
			profiles.add(current);
		}
		
		// Add total time for this event to sumtime of super event
		if(!stack.isEmpty())
		{
			ProfilingInfo	parent	= (ProfilingInfo)stack.get(stack.size()-1);
			parent.inherent	+= current.time;
		}
//		else if(name!=null)
//		{
//			// Save profiling info.
//			try
//			{
//				File	file	= new File(name);
//				ObjectOutputStream	oos	= new ObjectOutputStream(new FileOutputStream(file));
//				oos.writeObject(this);
//				oos.close();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
	}
	
	/**
	 *  Get the current profiling infos from the given start index.
	 *  @param start	The start index (use 0 for all profiling infos).
	 */
	public ProfilingInfo[] getProfilingInfos(int start)
	{
		ProfilingInfo[]	ret;
		synchronized(profiles)
		{
			ret	= (ProfilingInfo[])profiles.subList(start-cut, profiles.size()).toArray(new ProfilingInfo[profiles.size()-(start-cut)]);
			cut	+= ret.length;
			profiles.clear();
		}
		return ret;
	}
}
