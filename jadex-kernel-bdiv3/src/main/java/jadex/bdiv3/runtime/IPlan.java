package jadex.bdiv3.runtime;

import jadex.commons.future.IFuture;
import jadex.rules.eca.ICondition;

import java.util.List;

/**
 *  User interface for plans.
 */
public interface IPlan
{
	/**
	 *  Get the reason.
	 *  @return The reason.
	 */
	public Object getReason();
	
	/**
	 *  Get the dispatched element.
	 *  @return The dispatched element.
	 */
	public Object getDispatchedElement();
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public List<Object> getWaitqueue();
	
	/**
	 *  Wait for a delay.
	 */
	public IFuture<Void> waitFor(long delay);
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T> IFuture<T> dispatchSubgoal(T goal);
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T> IFuture<T> dispatchSubgoal(T goal, long timeout);
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<Object> waitForFactChanged(String belname);
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<Object> waitForFactChanged(String belname, long timeout);
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<Object> waitForFactAdded(String belname);
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<Object> waitForFactAdded(String belname, long timeout);

	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<Object> waitForFactRemoved(String belname);
	
	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<Object> waitForFactRemoved(String belname, long timeout);
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeEvent> waitForFactAddedOrRemoved(String belname);
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeEvent> waitForFactAddedOrRemoved(String belname, long timeout);
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events);
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events, long timeout);
}
