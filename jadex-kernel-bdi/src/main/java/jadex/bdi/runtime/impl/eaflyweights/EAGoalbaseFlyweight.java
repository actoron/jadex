package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAGoalbase;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for goalbase on runtime level.
 */
public class EAGoalbaseFlyweight extends ElementFlyweight implements IEAGoalbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAGoalbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAGoalbaseFlyweight getGoalbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAGoalbaseFlyweight ret = (EAGoalbaseFlyweight)ip.getFlyweightCache(IEAGoalbase.class).get(new Tuple(IEAGoalbase.class, scope));
		if(ret==null)
		{
			ret = new EAGoalbaseFlyweight(state, scope);
			ip.getFlyweightCache(IEAGoalbase.class).put(new Tuple(IEAGoalbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- IGoalbase interface --------

	/**
	 *  Get a (proprietary) adopted goal by name.
	 *  @param name	The goal name.
	 *  @return The goal (if found).
	 * /
	public IGoal getGoal(String name)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}*/

	/**
	 *  Test if an adopted goal is already contained in the goal base.
	 *  @param goal	The goal to test.
	 *  @return True, if the goal is contained.
	 */
	public IFuture containsGoal(final IEAGoal goal)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
					boolean bool = goals!=null && goals.contains(((EAGoalFlyweight)goal).getHandle());
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
			boolean bool = goals!=null && goals.contains(((EAGoalFlyweight)goal).getHandle());
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Get all proprietary goals of a specified type (=model element name).
	 *  @param type The goal type.
	 *  @return All proprietary goals of the specified type.
	 */
	public IFuture getGoals(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getGoals(getState(), getScope(), true, type));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getGoals(getState(), getScope(), true, type));
		}
		
		return ret;
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IFuture getGoals()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getGoals(getState(), getScope(), true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getGoals(getState(), getScope(), true));
		}
		
		return ret;
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IFuture createGoal(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.createGoal(getState(), getScope(), true, type));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.createGoal(getState(), getScope(), true, type));
		}
		
		return ret;
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public IFuture	dispatchTopLevelGoal(final IEAGoal goal)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());		
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
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
	public IFuture addGoalListener(final String type, final IGoalListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mgoal = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
					addEventListener(listener, mgoal);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mgoal = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
			addEventListener(listener, mgoal);
			ret.setResult(null);
		}
		
		return ret;
	}

	
	/**
	 *  Remove a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public IFuture removeGoalListener(final String type, final IGoalListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mgoal = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
					removeEventListener(listener, mgoal, false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mgoal = FlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_goals);
			removeEventListener(listener, mgoal, false);
			ret.setResult(null);
		}
		
		return ret;
	}

	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
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
	}*/
	
	//-------- helper methods --------
	
	/**
	 *  Create a goal.
	 *  @param ref	The goal name (may include capability with dot notation).
	 *  @param rcapa	The local capability.
	 *  @param state	The state.
	 * /
	public static EGoalFlyweight	createGoal(String ref, Object rcapa, IOAVState state)
	{
		Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.goal_type, rcapa, state);
		Object rgoal = GoalLifecycleRules.createGoal(state, scope[1], (String)scope[0]);
		return EGoalFlyweight.getGoalFlyweight(state, scope[1], rgoal);
	}*/
}

