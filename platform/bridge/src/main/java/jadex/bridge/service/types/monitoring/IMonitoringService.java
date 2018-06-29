package jadex.bridge.service.types.monitoring;

import jadex.bridge.service.annotation.Service;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The monitoring service allows for:
 *   
 *  sources: publishing new events
 *  consumers: subscribing for event patterns 
 */
@Service(system=true)
public interface IMonitoringService
{
	/**
	 *  The publish target 
	 */
	public static enum PublishTarget
	{
		TOALL,
		TOMONITORING,
		TOSUBSCRIBERS
	}
	
//	/**
//	 *  The publish level describes for a 
//	 *  component which event it allows to emit.
//	 */
//	public static enum PublishEmitLevel
//	{
//		ALL(0),
//		MOST(1),
//		SOME(2),
//		NONE(3);
//		
//		protected int level;
//		
//		/**
//		 *  Create a new PublishLevel.
//		 */
//		private PublishEmitLevel(int level)
//		{
//			this.level = level;
//		}
//
//		/**
//		 *  Get the level.
//		 *  return The level.
//		 */
//		public int getLevel()
//		{
//			return level;
//		}
//	}
	
	/**
	 *  The event importance.
	 */
	public static enum PublishEventLevel
	{
		NULL(99),
		FINE(3),
		MEDIUM(2),
		COARSE(1),
		OFF(0);
		
		protected int level;

		/**
		 *  Create a new PublishImportance.
		 */
		private PublishEventLevel(int level)
		{
			this.level = level;
		}

		/**
		 *  Get the level.
		 *  return The level.
		 */
		public int getLevel()
		{
			return level;
		}
	}
	
	/**
	 *  Publish a new event.
	 *  @param event The event. 
	 */
	public IFuture<Void> publishEvent(IMonitoringEvent event);
	
	/**
	 *  Subscribe to monitoring events.
	 *  @param filter An optional filter.
	 */
	// Now done with SFuture.getNotTimeoutFuture() to allow for detecting communication aborts
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(IFilter<IMonitoringEvent> filter);
}
