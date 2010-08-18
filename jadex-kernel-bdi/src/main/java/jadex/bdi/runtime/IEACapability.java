package jadex.bdi.runtime;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;
import jadex.service.IServiceProvider;

import java.util.logging.Logger;

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
	public IBDIExternalAccess getExternalAccess();

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IComponentIdentifier getParent();

	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IEABeliefbase getBeliefbase();

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IEAGoalbase	getGoalbase();

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IEAPlanbase	getPlanbase();

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEAEventbase	getEventbase();

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IEAExpressionbase getExpressionbase();

	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger();

	/**
	 * Get the agent name.
	 * @return The agent name.
	 */
	public String getComponentName();

	/**
	 * Get the configuration name.
	 * @return The configuration name.
	 */
	public IFuture getConfigurationName();

	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier();

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
	public IServiceProvider getServiceProvider();
	
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
	public IFuture killAgent();
	
	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	public void addAgentListener(IAgentListener listener);
	
	/**
	 *  Add an agent listener
	 *  @param listener The listener.
	 */
	public void removeAgentListener(IAgentListener listener);
}
