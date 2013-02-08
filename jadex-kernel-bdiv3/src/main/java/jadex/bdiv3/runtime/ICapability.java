package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;

import java.util.Collection;

/**
 * 
 */
public interface ICapability
{
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement();
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId();
	
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals();
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals(MGoal mgoal);
	
	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RGoal> getGoals(Class<?> type);
	
	/**
	 *  Test if a goal is contained.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public boolean containsGoal(Object pojogoal);

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public Collection<RPlan> getPlans();

	/**
	 *  Get goals of a specific pojo type.
	 *  @param type The type.
	 *  @return The goals.
	 */
	public Collection<RPlan> getPlans(MPlan mplan);
}
