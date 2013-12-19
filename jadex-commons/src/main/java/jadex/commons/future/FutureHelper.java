package jadex.commons.future;

import jadex.commons.Tuple2;

import java.util.LinkedList;
import java.util.List;

/**
 *  Helper class to access future notification stack
 */
public abstract class FutureHelper
{
	/**
	 *  Process all collected listener notifications for the current thread.
	 *  @return True, if at least one listener has been notified.
	 */
	public static boolean	notifyStackedListeners()
	{
		boolean	notified	= false;
		while(Future.STACK.get()!=null && !Future.STACK.get().isEmpty())
		{
			notified	= true;
			Tuple2<Future<?>, IResultListener<?>>	tup	= Future.STACK.get().remove(0);
			Future<?> fut	= tup.getFirstEntity();
			IResultListener lis = tup.getSecondEntity();
			if(fut.exception!=null)
			{
				if(fut.undone && lis instanceof IUndoneResultListener)
				{
					((IUndoneResultListener)lis).exceptionOccurredIfUndone(fut.exception);
				}
				else
				{
					lis.exceptionOccurred(fut.exception);
				}
			}
			else
			{
				if(fut.undone && lis instanceof IUndoneResultListener)
				{
					((IUndoneResultListener)lis).resultAvailableIfUndone(fut.result);
				}
				else
				{
					lis.resultAvailable(fut.result); 
				}
			}
		}
		Future.STACK.remove();
		return notified;
	}
	
	/**
	 *  Remove all collected listener notifications for the current thread.
	 */
	public static List<Tuple2<Future<?>, IResultListener<?>>>	removeStackedListeners()
	{
		List<Tuple2<Future<?>, IResultListener<?>>>	tmp	= Future.STACK.get();
		List<Tuple2<Future<?>, IResultListener<?>>>	ret	= null;
		if(tmp!=null)
		{
			ret	= new LinkedList<Tuple2<Future<?>,IResultListener<?>>>();
			ret.addAll(tmp);
			tmp.clear();
		}
		Future.STACK.remove();
		return ret;
	}
	
	/**
	 *  Add listener notifications to the current thread.
	 */
	public static void	addStackedListeners(List<Tuple2<Future<?>, IResultListener<?>>> notifications)
	{
		List<Tuple2<Future<?>, IResultListener<?>>>	list	= Future.STACK.get();
		if(list==null)
		{
    		list	= new LinkedList<Tuple2<Future<?>, IResultListener<?>>>();
    		Future.STACK.set(list);
		}
		list.addAll(notifications);
	}
}