package jadex.commons.future;

import java.util.Set;

/**
 *  Helper class to access future notification stack
 */
public abstract class FutureHelper
{
	/**
	 *  Process all collected listener notifications for the current thread.
	 *  @return True, if at least one listener has been notified.
	 */
	public static void	notifyStackedListeners()
	{
		Future.NOTIFYING.remove();	// force new loop even when in outer notification loop
		new Future<Void>().startScheduledNotifications();
	}
	
	/**
	 *  Remove all collected listener notifications for the current thread.
	 */
	public static Set<Future<?>>	removeStackedListeners()
	{
		Set<Future<?>>	ret	= Future.NOTIFY.get();
		Future.NOTIFY.remove();
		return ret;
	}
	
	/**
	 *  Add listener notifications to the current thread.
	 */
	public static void	addStackedListeners(Set<Future<?>> notifications)
	{
		Set<Future<?>>	stack	= Future.NOTIFY.get();
		if(stack==null)
		{
    		Future.NOTIFY.set(notifications);
		}
		else
		{
			stack.addAll(notifications);
		}
	}
}