package jadex.bdiv3x.runtime;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;

import java.util.Collection;

/**
 * 
 */
public class RGoalbase extends RElement implements IGoalbase
{
	/**
	 *  Create a new goalbase.
	 */
	public RGoalbase(IInternalAccess agent)
	{
		super(null, agent);
	}
	
	/**
	 *  Get a (proprietary) adopted goal by name.
	 *  @param name	The goal name.
	 *  @return The goal (if found).
	 */
//	public IGoal getGoal(String name);

	/**
	 *  Test if an adopted goal is already contained in the goal base.
	 *  @param goal	The goal to test.
	 *  @return True, if the goal is contained.
	 */
	public boolean containsGoal(IGoal goal)
	{
		return getCapability().containsGoal(goal);
	}

	/**
	 *  Get all proprietary goals of a specified type (=model element name).
	 *  @param type The goal type.
	 *  @return All proprietary goals of the specified type.
	 */
	public IGoal[] getGoals(String type)
	{
		MCapability mcapa = (MCapability)getCapability().getModelElement();
		MGoal mgoal = mcapa.getGoal(type);
		Collection<RGoal> ret = getCapability().getGoals(mgoal);
		return ret.toArray(new IGoal[ret.size()]);
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[] getGoals()
	{
		Collection<RGoal> ret = getCapability().getGoals();
		return ret.toArray(new IGoal[ret.size()]);
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(String type)
	{
		final MGoal mgoal = ((MCapability)getCapability().getModelElement()).getGoal(type);
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+type);
		return new RGoal(agent, mgoal, null, (RPlan)null);
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public void	dispatchTopLevelGoal(IGoal goal)
	{
		agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
	}

	/**
	 *  Register a new goal model.
	 *  @param mgoal The goal model.
	 */
//	public void registerGoal(IMGoal mgoal);

	/**
	 *  Deregister a goal model.
	 *  @param mgoal The goal model.
	 */
//	public void deregisterGoal(IMGoal mgoal);

	/**
	 *  Register a new goal reference model.
	 *  @param mgoalref The goal reference model.
	 */
//	public void registerGoalReference(IMGoalReference mgoalref);

	/**
	 *  Deregister a goal reference model.
	 *  @param mgoalref The goal reference model.
	 */
//	public void deregisterGoalReference(IMGoalReference mgoalref);
	
	//-------- listeners --------
	
//	/**
//	 *  Add a goal listener.
//	 *  @param type	The goal type.
//	 *  @param listener The goal listener.
//	 */
//	public void addGoalListener(String type, IGoalListener listener);	
//	
//	/**
//	 *  Remove a goal listener.
//	 *  @param type	The goal type.
//	 *  @param listener The goal listener.
//	 */
//	public void removeGoalListener(String type, IGoalListener listener);
	
	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	protected RCapability getCapability()
	{
		return ((IInternalBDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapability();
	}
}
