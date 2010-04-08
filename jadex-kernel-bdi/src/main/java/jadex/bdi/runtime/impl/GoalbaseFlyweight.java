package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		GoalbaseFlyweight ret = (GoalbaseFlyweight)ip.getFlyweightCache(IGoalbase.class).get(new Tuple(IGoalbase.class, scope));
		if(ret==null)
		{
			ret = new GoalbaseFlyweight(state, scope);
			ip.getFlyweightCache(IGoalbase.class).put(new Tuple(IGoalbase.class, scope), ret);
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object[]	scope	= AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, getScope(), getState());
					
					Object	mcap	= getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
					Object	mgoal	= getState().getAttributeValue(mcap, OAVBDIMetaModel.capability_has_goals, scope[0]);
					if(mgoal==null)
						throw new RuntimeException("Undefined goal type '"+type+"'.");
					
					IGoal[]	ret;
					Collection	goals	= getState().getAttributeValues(scope[1], OAVBDIRuntimeModel.capability_has_goals);
					if(goals!=null)
					{
						List	matched	= new ArrayList();
						for(Iterator it=goals.iterator(); it.hasNext(); )
						{
							Object	rgoal	= it.next();
							if(mgoal.equals(getState().getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model)))
							{
								matched.add(GoalFlyweight.getGoalFlyweight(getState(), scope[1], rgoal));
							}
						}
						ret	= (IGoal[])matched.toArray(new IGoal[matched.size()]);
					}
					else
					{
						ret	= new IGoal[0];
					}
					
					object = ret;
				}
			};
			return (IGoal[])invoc.object;
		}
		else
		{
			Object[]	scope	= AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, getScope(), getState());
			
			Object	mcap	= getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
			Object	mgoal	= getState().getAttributeValue(mcap, OAVBDIMetaModel.capability_has_goals, scope[0]);
			if(mgoal==null)
				throw new RuntimeException("Undefined goal type '"+type+"'.");
			
			IGoal[]	ret;
			Collection	goals	= getState().getAttributeValues(scope[1], OAVBDIRuntimeModel.capability_has_goals);
			if(goals!=null)
			{
				List	matched	= new ArrayList();
				for(Iterator it=goals.iterator(); it.hasNext(); )
				{
					Object	rgoal	= it.next();
					if(mgoal.equals(getState().getAttributeValue(rgoal, OAVBDIRuntimeModel.element_has_model)))
					{
						matched.add(GoalFlyweight.getGoalFlyweight(getState(), scope[1], rgoal));
					}
				}
				ret	= (IGoal[])matched.toArray(new IGoal[matched.size()]);
			}
			else
			{
				ret	= new IGoal[0];
			}
			
			return ret;
		}
	}

	/**
	 *  Get all the adopted goals in this scope (including subgoals).
	 *  @return All goals and subgoals.
	 */
	public IGoal[]	getGoals()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IGoal[]	ret;
					Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
					if(goals!=null)
					{
						List	flyweights	= new ArrayList();
						for(Iterator it=goals.iterator(); it.hasNext(); )
						{
							flyweights.add(GoalFlyweight.getGoalFlyweight(getState(), getHandle(), it.next()));
						}
						ret	= (IGoal[])flyweights.toArray(new IGoal[flyweights.size()]);
					}
					else
					{
						ret	= new IGoal[0];
					}
					
					object = ret;
				}
			};
			return (IGoal[])invoc.object;
		}
		else
		{
			IGoal[]	ret;
			Collection	goals	= getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_goals);
			if(goals!=null)
			{
				List	flyweights	= new ArrayList();
				for(Iterator it=goals.iterator(); it.hasNext(); )
				{
					flyweights.add(GoalFlyweight.getGoalFlyweight(getState(), getHandle(), it.next()));
				}
				ret	= (IGoal[])flyweights.toArray(new IGoal[flyweights.size()]);
			}
			else
			{
				ret	= new IGoal[0];
			}
			
			return ret;
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = createGoal(type, getScope(), getState());
				}
			};
			return (IGoal)invoc.object;
		}
		else
		{
			IGoal ret =  createGoal(type, getScope(), getState());
			return ret;
		}
	}

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public void	dispatchTopLevelGoal(final IGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object rgoal = ((GoalFlyweight)goal).getHandle();
					GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), rgoal);
				}
			};
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			Object rgoal = ((GoalFlyweight)goal).getHandle();
			GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), rgoal);		
			getInterpreter().endMonitorConsequences();
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
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, type);
					if(mgoal==null)
						throw new RuntimeException("Goal not found: "+type);

					addEventListener(listener, mgoal);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, type);
			if(mgoal==null)
				throw new RuntimeException("Goal not found: "+type);

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
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, type);
					if(mgoal==null)
						throw new RuntimeException("Goal not found: "+type);

					removeEventListener(listener, mgoal, false);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, type);
			if(mgoal==null)
				throw new RuntimeException("Goal not found: "+type);
			
			removeEventListener(listener, mgoal, false);
		}
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
	 */
	public static GoalFlyweight	createGoal(String ref, Object rcapa, IOAVState state)
	{
		Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.goal_type, rcapa, state);
		Object rgoal = GoalLifecycleRules.createGoal(state, scope[1], (String)scope[0]);
		return GoalFlyweight.getGoalFlyweight(state, scope[1], rgoal);
	}
}
