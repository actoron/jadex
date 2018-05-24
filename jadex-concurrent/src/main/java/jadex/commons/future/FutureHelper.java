package jadex.commons.future;

import java.util.Queue;

import jadex.commons.ICommand;
import jadex.commons.Tuple3;

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
		new Future<Void>().startScheduledNotifications();
	}
	
	/**
	 *  Remove all collected listener notifications for the current thread.
	 */
	public static Queue<Tuple3<Future<?>, IResultListener<?>, ICommand<IResultListener<?>>>>	removeStackedListeners()
	{
		Queue<Tuple3<Future<?>, IResultListener<?>, ICommand<IResultListener<?>>>>	ret	= Future.STACK.get();
		Future.STACK.remove();
		return ret;
	}
	
	/**
	 *  Add listener notifications to the current thread.
	 */
	public static void	addStackedListeners(Queue<Tuple3<Future<?>, IResultListener<?>, ICommand<IResultListener<?>>>> notifications)
	{
		Queue<Tuple3<Future<?>, IResultListener<?>, ICommand<IResultListener<?>>>>	stack	= Future.STACK.get();
		if(stack==null)
		{
    		Future.STACK.set(notifications);
		}
		else
		{
			stack.addAll(notifications);
		}
	}
}