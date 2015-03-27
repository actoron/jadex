package jadex.bdiv3.features;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.commons.future.IFuture;
import jadex.rules.eca.RuleSystem;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * 
 */
public interface IBDIAgentFeature
{
//	/**
//	 *  Get the bdi agent.
//	 *  @return The bdi agent.
//	 */
//	public BDIAgent getAgent();
	
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

	/**
	 *  Dispatch a pojo goal wait for its result.
	 *  @param goal The pojo goal.
	 *  @return The goal result.
	 */
	public <T, E> IFuture<E> dispatchTopLevelGoal(T goal);
	
	/**
	 *  Drop a pojo goal.
	 *  @param goal The pojo goal.
	 */
	public void dropGoal(Object goal);

	/**
	 *  Dispatch a pojo plan and wait for its result.
	 *  @param plan The pojo plan or plan name.
	 *  @return The plan result.
	 */
	public <T, E> IFuture<E> adoptPlan(T plan);
	
	/**
	 *  Dispatch a goal wait for its result.
	 *  @param plan The pojo plan or plan name.
	 *  @param args The plan arguments.
	 *  @return The plan result.
	 */
	public <T, E> IFuture<E> adoptPlan(T plan, Object[] args);
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, final IBeliefListener listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener listener);
	
	
	// internal methods?
	
//	/**
//	 *  Get the rulesystem.
//	 *  @return The rulesystem.
//	 */
//	public RuleSystem getRuleSystem();
//	
//	/**
//	 *  Get the bdimodel.
//	 *  @return the bdimodel.
//	 */
//	public BDIModel getBDIModel();
//
//	/**
//	 *  Get the state.
//	 *  @return the state.
//	 */
//	public RCapability getCapability();
//	
//	/**
//	 *  Get a capability pojo object.
//	 */
//	public Object	getCapabilityObject(String name);
//	
//	/**
//	 *  Get parameter values for injection into method and constructor calls.
//	 */
//	public Object[] getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe);
//
//	public Object[]	getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe, Collection<Object> vs);

}
