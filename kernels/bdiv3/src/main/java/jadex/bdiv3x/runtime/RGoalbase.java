package jadex.bdiv3x.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The goal base runtime element.
 */
public class RGoalbase extends RElement implements IGoalbase
{
	//-------- attributes --------
	
	/** The scope (for local views). */
	protected String	scope;
	
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase.
	 */
	public RGoalbase(IInternalAccess agent, String scope)
	{
		super(null, agent);
		this.scope	= scope;
	}
	
	//-------- IGoalbase methods --------

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
		MGoal mgoal = getCapability().getMCapability().getResolvedGoal(scope, type);
		Collection<RGoal> ret = getCapability().getGoals(mgoal);
		return ret.toArray(new IGoal[ret.size()]);
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[] getGoals()
	{
		List<IGoal>	ret	= new ArrayList<IGoal>();
		for(IGoal goal: getCapability().getGoals())
		{
			if(SUtil.equals(goal.getModelElement().getCapabilityName(), scope))
			{
				ret.add(goal);
			}
		}
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
		MGoal mgoal = getCapability().getMCapability().getResolvedGoal(scope, type);
		return new RGoal(getAgent(), mgoal, null, null, null, null, null);
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public <T>	IFuture<T>	dispatchTopLevelGoal(final IGoal goal)
	{
		final Future<T> ret = new Future<T>();
		
		goal.addListener(new ExceptionDelegationResultListener<Void, T>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Object res = RGoal.getGoalResult((RGoal)goal, agent.getClassLoader());
				ret.setResult((T)res);
			}
		});

//		System.out.println("adopt goal");
		RGoal.adoptGoal((RGoal)goal, getAgent());
	
		return ret;
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

}
