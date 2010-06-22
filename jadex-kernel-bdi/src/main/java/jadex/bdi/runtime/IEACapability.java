package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  A capability is a self-contained agent module
 *  as specified in  an agent definition file (ADF).
 */
public interface IEACapability extends IEAElement
{
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IFuture getExternalAccess();

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IFuture getParent();

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IFuture getBeliefbase();

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IFuture getGoalbase();

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IFuture getPlanbase();

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IFuture getEventbase();

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IFuture getExpressionbase();

	/**
	 * Get the property base.
	 * @return The property base.
	 */
//		public IPropertybase getPropertybase();

	/**
	 *  Register a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	registerSubcapability(IMCapabilityReference subcap);

	/**
	 *  Deregister a subcapability.
	 *  @param subcap	The subcapability.
	 * /
	public void	deregisterSubcapability(IMCapabilityReference subcap);*/

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public IFuture getLogger();

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public IFuture getAgentName();

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public IFuture getConfigurationName();

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IFuture getComponentIdentifier();

	/**
	 *  Get the platform specific agent object.
	 *  Allows to do platform specific things.
	 *  @return The agent object.
	 */
	public IFuture getPlatformComponent();

	/**
	 *  Get the container
	 *  @return The container.
	 */
	public IFuture getServiceContainer();
	
	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public IFuture getTime();

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public IFuture getClassLoader();
	
	/**
	 *  Kill the agent.
	 */
	public void killAgent();
	
//		/**
//		 *  Get the application context.
//		 *  @return The application context (or null).
//		 */
//		public IApplicationContext getApplicationContext();

	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
	public void addAgentListener(IAgentListener listener);
	
	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	public void removeAgentListener(IAgentListener listener);
}
