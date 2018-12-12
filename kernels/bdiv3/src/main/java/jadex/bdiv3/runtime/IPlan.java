package jadex.bdiv3.runtime;

import jadex.bdiv3x.runtime.IFinishableElement;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.ICondition;

/**
 *  User interface for plans.
 */
public interface IPlan extends IParameterElement, IFinishableElement<Object>  // todo: do not extend IParameterElement in case of non bdiv3x
{
	/**
	 *  Get the id.
	 */
	public String getId();
	
	/**
	 *  Abort the plan.
	 */
	public IFuture<Void> abort();
	
	/**
	 *  Test if plan is passed.
	 */
	public boolean isPassed();
	
	/**
	 *  Test if plan is aborted.
	 */
	public boolean isAborted();
	
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
	 *  Wait for a delay.
	 */
	public IFuture<Void> waitFor(long delay);
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchSubgoal(T goal);
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchSubgoal(T goal, long timeout);
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactChanged(String belname);
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactChanged(String belname, long timeout);
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAdded(String belname);
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAdded(String belname, long timeout);

	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactRemoved(String belname);
	
	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactRemoved(String belname, long timeout);
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAddedOrRemoved(String belname);
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAddedOrRemoved(String belname, long timeout);
	
	/**
	 *  Wait for a collection change.
	 */
	public <T> IFuture<ChangeInfo<T>> waitForCollectionChange(String belname, long timeout, IFilter<ChangeInfo<T>> filter);
	
	/**
	 *  Wait for a collection change.
	 */
	public <T> IFuture<ChangeInfo<T>> waitForCollectionChange(String belname, long timeout, Object id);

	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events);
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events, long timeout);
	
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> invokeInterruptable(IResultCommand<IFuture<T>, Void> command);

}
