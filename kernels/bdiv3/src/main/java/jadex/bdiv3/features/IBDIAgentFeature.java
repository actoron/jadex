package jadex.bdiv3.features;

import java.util.Collection;

import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.IGoal;
import jadex.commons.future.IFuture;

/**
 *  Public methods for working with BDI agents.
 */
public interface IBDIAgentFeature extends IExternalBDIAgentFeature
{
	/**
	 *  Get the goals of a given type as pojos.
	 *  @param clazz The pojo goal class.
	 *  @return The currently instantiated goals of that type.
	 */
	public <T> Collection<T> getGoals(Class<T> clazz);
	
	/**
	 *  Get the current goals as api representation.
	 *  @return All currently instantiated goals.
	 */
	public Collection<IGoal> getGoals();
	
	/**
	 *  Get the goal api representation for a pojo goal.
	 *  @param goal The pojo goal.
	 *  @return The api goal.
	 */
	public IGoal getGoal(Object goal);

//	/**
//	 *  Dispatch a pojo goal wait for its result.
//	 *  @param goal The pojo goal.
//	 *  @return The goal result.
//	 */
//	public <T, E> IFuture<E> dispatchTopLevelGoal(T goal);
	
	/**
	 *  Drop a pojo goal.
	 *  @param goal The pojo goal.
	 */
	public void dropGoal(Object goal);	
	
//	/**
//	 *  Dispatch a pojo plan and wait for its result.
//	 *  @param plan The pojo plan or plan name.
//	 *  @return The plan result, i.e. the return value of the plan body method, if any.
//	 */
//	public <T, E> IFuture<E> adoptPlan(T plan);
//	
//	/**
//	 *  Dispatch a goal wait for its result.
//	 *  @param plan The pojo plan or plan name.
//	 *  @param args The plan arguments.
//	 *  @return The plan result, i.e. the return value of the plan body method, if any.
//	 */
//	public <T, E> IFuture<E> adoptPlan(T plan, Object... args);
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, final IBeliefListener<?> listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener<?> listener);
	
}
