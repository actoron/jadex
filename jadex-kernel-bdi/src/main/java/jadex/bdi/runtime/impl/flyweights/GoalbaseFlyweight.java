package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MGoalbaseFlyweight;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for goalbase on runtime level.
 */
public class GoalbaseFlyweight extends ElementFlyweight implements IGoalbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private GoalbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static GoalbaseFlyweight getGoalbaseFlyweight(IOAVState state, Object scope)
	{
		IBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		GoalbaseFlyweight ret = (GoalbaseFlyweight)ip.getFlyweightCache(IGoalbase.class, new Tuple(IGoalbase.class, scope));
		if(ret==null)
		{
			ret = new GoalbaseFlyweight(state, scope);
			ip.putFlyweightCache(IGoalbase.class, new Tuple(IGoalbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- IGoalbase interface --------

	/**
	 *  Get a (proprietary) adopted goal by name.
	 *  @param name	The goal name.
	 *  @return The goal (if found).
	 */
	public IGoal getGoal(String name)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 *  Test if an adopted goal is already contained in the goal base.
	 *  @param goal	The goal to test.
	 *  @return True, if the goal is contained.
	 */
	public boolean containsGoal(final IGoal goal)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
					bool = goals!=null && goals.contains(((GoalFlyweight)goal).getHandle());
				}
			};
			return invoc.bool;
		}
		else
		{
			Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
			return goals!=null && goals.contains(((GoalFlyweight)goal).getHandle());
		}
	}

	/**
	 *  Get all proprietary goals of a specified type (=model element name).
	 *  @param type The goal type.
	 *  @return All proprietary goals of the specified type.
	 */
	public IGoal[] getGoals(final String type)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getGoals(getState(), getScope(), type);
				}
			};
			return (IGoal[])invoc.object;
		}
		else
		{
			return (IGoal[])SFlyweightFunctionality.getGoals(getState(), getScope(), type);
		}
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[] getGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = (IGoal[])SFlyweightFunctionality.getGoals(getState(), getScope());
				}
			};
			return (IGoal[])invoc.object;
		}
		else
		{
			return (IGoal[])SFlyweightFunctionality.getGoals(getState(), getScope());
		}
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(final String type)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.createGoal(getState(), getScope(), type);
				}
			};
			return (IGoal)invoc.object;
		}
		else
		{
			return (IGoal)SFlyweightFunctionality.createGoal(getState(), getScope(), type);
		}
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public void	dispatchTopLevelGoal(final IGoal goal)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());		
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Register a new goal model.
	 *  @param mgoal The goal model.
	 * /
	public void registerGoal(IMGoal mgoal)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}*/

	/**
	 *  Deregister a goal model.
	 *  @param mgoal The goal model.
	 * /
	public void deregisterGoal(IMGoal mgoal)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}*/

	/**
	 *  Register a new goal reference model.
	 *  @param mgoalref The goal reference model.
	 * /
	public void registerGoalReference(IMGoalReference mgoalref)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}*/

	/**
	 *  Deregister a goal reference model.
	 *  @param mgoalref The goal reference model.
	 * /
	public void deregisterGoalReference(IMGoalReference mgoalref)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public void addGoalListener(final String type, final IGoalListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mgoal = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
					addEventListener(listener, mgoal);
				}
			};
		}
		else
		{
			Object mgoal = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
			addEventListener(listener, mgoal);
		}
	}

	
	/**
	 *  Remove a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public void removeGoalListener(final String type, final IGoalListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mgoal = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
					removeEventListener(listener, mgoal, false);
				}
			};
		}
		else
		{
			Object mgoal = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
			removeEventListener(listener, mgoal, false);
		}
	}

	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MGoalbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MGoalbaseFlyweight(getState(), mscope);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create a goal.
	 *  @param ref	The goal name (may include capability with dot notation).
	 *  @param rcapa	The local capability.
	 *  @param state	The state.
	 * /
	public static GoalFlyweight	createGoal(String ref, Object rcapa, IOAVState state)
	{
		Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.goal_type, rcapa, state);
		Object rgoal = GoalLifecycleRules.createGoal(state, scope[1], (String)scope[0]);
		return GoalFlyweight.getGoalFlyweight(state, scope[1], rgoal);
	}*/
}
