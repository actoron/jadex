package jadex.bdiv3.runtime;

import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Interface for goal deliberation strategies.
 */
public interface IDeliberationStrategy
{
	/**
	 *  Init the strategy.
	 *  @param agent The agent.
	 */
	public void init(IInternalAccess agent);
	
	/**
	 *  Called when a goal has been adopted.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsAdopted(RGoal goal);
	
	/**
	 *  Called when a goal has been dropped.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsDropped(RGoal goal);
	
	/**
	 *  Called when a goal becomes an option.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsOption(RGoal goal);
	
	/**
	 *  Called when a goal becomes active.
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsActive(RGoal goal);
	
	/**
	 *  Called when a goal is not active any longer (suspended or option).
	 *  @param goal The goal.
	 */
	public IFuture<Void> goalIsNotActive(RGoal goal);
}
