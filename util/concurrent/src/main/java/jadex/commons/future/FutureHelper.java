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
		// TODO: resetting notification state breaks some BDI agents (e.g. MaintainGoalContext.agent.xml)
//		boolean	noti	= Future.NOTIFYING.get()!=null;
		Future.NOTIFYING.remove();	// force new loop even when in outer notification loop
		Future.startScheduledNotifications();
//		if(noti)
//			Future.NOTIFYING.set(true);
	}
}