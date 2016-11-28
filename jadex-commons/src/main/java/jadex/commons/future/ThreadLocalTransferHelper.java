package jadex.commons.future;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Helps to transfer registered thread local values between different threads.
 */
public class ThreadLocalTransferHelper
{
	/** The registered thread locals. */
	protected static final Set<ThreadLocal<Object>> THREADLOCALS = Collections.synchronizedSet(new HashSet<ThreadLocal<Object>>());
	
	/**
	 *  Method to be called on old thread before thread switch.
	 */
	public static Map<ThreadLocal<Object>, Object> saveValues()
	{
		Map<ThreadLocal<Object>, Object> ret = null;
		if(THREADLOCALS.size()>0)
		{
			ret = new HashMap<ThreadLocal<Object>, Object>();
			for(ThreadLocal<Object> tl: THREADLOCALS)
			{
				ret.put(tl, tl.get());
			}
		}
		return ret;
	}
	
	/**
	 *  Method to be called on new thread on resumption.
	 */
	public static void restoreValues(Map<ThreadLocal<Object>, Object> vals)
	{
		if(vals!=null)
		{
			for(Map.Entry<ThreadLocal<Object>, Object> entry: vals.entrySet())
			{
				entry.getKey().set(entry.getValue());
			}
		}
	}
	
	/**
	 *  Add a thread local that will be automatically copied on thread resumption.
	 */
	public static void addThreadLocal(ThreadLocal<?> tl)
	{
		THREADLOCALS.add((ThreadLocal)tl);
	}
	
	/**
	 *  Add a thread local that will be automatically copied on thread resumption.
	 */
	public static void removeThreadLocal(ThreadLocal<?> tl)
	{
		THREADLOCALS.remove((ThreadLocal)tl);
	}
	
	/** The thread locals. */
	protected Map<ThreadLocal<Object>, Object> vals;
	
	/**
	 *  Create a new transfer helper.
	 */
	public ThreadLocalTransferHelper()
	{
	}
	
	/**
	 *  Create a new transfer helper.
	 */
	public ThreadLocalTransferHelper(boolean beforeswitch)
	{
		if(beforeswitch)
			beforeSwitch();
	}

	/**
	 *  Must be called before a thread switch occurs to save the thread local values.
	 */
	public void beforeSwitch()
	{
		vals = saveValues();
	}
	
	/**
	 *  Must be called after a thread switch occurs to restore the thread local values.
	 */
	public void afterSwitch()
	{
		restoreValues(vals);
	}
}
