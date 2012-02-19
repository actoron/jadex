package jadex.commons.future;

import jadex.commons.Tuple2;

/**
 *  Helper class to access future notification stack
 */
public abstract class FutureHelper
{
	public static boolean	notifyStackedListeners()
	{
		boolean	notified	= false;
		while(Future.STACK.get()!=null && !Future.STACK.get().isEmpty())
		{
			notified	= true;
			Tuple2<Future<?>, IResultListener<?>>	tup	= Future.STACK.get().remove(0);
			Future<?> fut	= tup.getFirstEntity();
			if(fut.exception!=null)
			{
				tup.getSecondEntity().exceptionOccurred(fut.exception);
			}
			else
			{
				((IResultListener)tup.getSecondEntity()).resultAvailable(fut.result); 
			}
		}
		return notified;
	}
}