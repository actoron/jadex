package jadex.commons.future;

/**
 *  Helper class to access future notification stack
 */
public abstract class FutureHelper
{
	/**
	 *  Process all collected listener notifications for the current thread, i.e. temporarily disable stack compaction.
	 */
	public static void	notifyStackedListeners()
	{
		Future.NOTIFYING.remove();	// force new loop even when in outer notification loop
		Future.startScheduledNotifications();
	}
	
//	/**
//	 *  Remove all collected listener notifications for the current thread.
//	 */
//	public static Set<Future<?>>	removeStackedListeners()
//	{
//		synchronized(Future.NOTIFICATIONS)
//		{
//			List<Tuple3<Future<?>, IResultListener<?>, ICommand<IResultListener<?>>>> ret	= Future.NOTIFICATIONS.remove(Thread.currentThread());
//		}
//	}
//	
//	/**
//	 *  Add listener notifications to the current thread.
//	 */
//	public static void	addStackedListeners(Set<Future<?>> notifications)
//	{
//		Set<Future<?>>	stack	= Future.NOTIFY.get();
//		if(stack==null)
//		{
//    		Future.NOTIFY.set(notifications);
//		}
//		else
//		{
//			stack.addAll(notifications);
//		}
//	}
}