package jadex.bdiv3.actions;

import java.lang.reflect.Field;
import java.util.List;

import jadex.bdiv3.IBDIClassGenerator;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPI;
import jadex.bdiv3.annotation.GoalParent;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MParameter.Direction;
import jadex.bdiv3.model.MParameter.EvaluationMode;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RParameterElement.RParameter;
import jadex.bdiv3.runtime.impl.RParameterElement.RParameterSet;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RProcessableElement.State;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bdiv3x.runtime.IParameterSet;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SAccess;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Action for adopting a goal.
 */
public class AdoptGoalAction implements IConditionalComponentStep<Void>
{
	/** The goal. */
	protected RGoal goal;
	
	/** The state. */
	protected PlanLifecycleState state;
	
	/**
	 *  Create a new action.
	 */
	public AdoptGoalAction(RGoal goal)
	{
//		System.out.println("adopting: "+goal.getId()+" "+goal.getPojoElement().getClass().getName());
		this.goal = goal;
		
		// todo: support this also for a parent goal?!
		if(goal.getParent() instanceof RPlan)
		{
			this.state = goal.getParentPlan().getLifecycleState();
		}
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		return (state==null || state.equals(goal.getParentPlan().getLifecycleState())) 
			&& RGoal.GoalLifecycleState.NEW.equals(goal.getLifecycleState());
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
		adoptGoal(ia, goal);
		return IFuture.DONE;
	}
	
	/**
	 *  Adopt a goal.
	 */
	public static void adoptGoal(IInternalAccess agent, RGoal goal)
	{
		Future<Void> ret = new Future<Void>();
		try
		{
//			BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			// todo: observe class and goal itself!
//			goal.observeGoal(ia);
			
			// inject agent in static inner class goals
			MGoal mgoal = (MGoal)goal.getModelElement();
			Class<?> gcl = mgoal.getTargetClass(agent.getClassLoader());
			if(gcl!=null)// && gcl.isMemberClass() && Modifier.isStatic(gcl.getModifiers()))
			{
				try
				{
					Field f = gcl.getDeclaredField(IBDIClassGenerator.AGENT_FIELD_NAME);
					f.set(goal.getPojoElement(), agent);

					// Init goal parameter list/map/set wrappers with the agent
					List<MParameter> mps = mgoal.getParameters();
					if(mps!=null)
					{
						for(MParameter mp: mps)
						{
							Object val = mp.getValue(goal.getPojoElement(), agent.getClassLoader());
							if(val instanceof ListWrapper && ((ListWrapper<?>)val).isInitWrite())
							{
								((ListWrapper<?>)val).setAgent(agent);
							}
							else if(val instanceof MapWrapper && ((MapWrapper<?,?>)val).isInitWrite())
							{
								((MapWrapper<?,?>)val).setAgent(agent);
							}
							else if(val instanceof SetWrapper && ((SetWrapper<?>)val).isInitWrite())
							{
								((SetWrapper<?>)val).setAgent(agent);
							}
						}
					}
					
					// Perform init writes means that the events of constructor parameter changes are thrown
					BDIAgentFeature.performInitWrites(agent, goal.getPojoElement());
				}
				catch(Exception e)
				{
					// nop
				}
			}
			
			// inject goal elements
			if(goal.getPojoElement()!=null)
			{
				Class<?> cl = goal.getPojoElement().getClass();
			
				while(cl.isAnnotationPresent(Goal.class))
				{
					Field[] fields = cl.getDeclaredFields();
					for(Field f: fields)
					{
						if(f.isAnnotationPresent(GoalAPI.class))
						{
							SAccess.setAccessible(f, true);
							f.set(goal.getPojoElement(), goal);
						}
						else if(f.isAnnotationPresent(GoalParent.class))
						{
							if(goal.getParent()!=null)
							{
								Object pa = goal.getParent();
								Object pojopa = null;
								if(pa instanceof RPlan)
								{
									pojopa = ((RPlan)pa).getPojoPlan();
								}
								else if(pa instanceof RGoal)
								{
									pojopa = ((RGoal)pa).getPojoElement();
								}	
									
								if(SReflect.isSupertype(f.getType(), pa.getClass()))
								{
									SAccess.setAccessible(f, true);
									f.set(goal.getPojoElement(), pa);
								}
								else if(pojopa!=null && SReflect.isSupertype(f.getType(), pojopa.getClass()))
								{
									SAccess.setAccessible(f, true);
									f.set(goal.getPojoElement(), pojopa);
								}
							}
						}
					}
					cl = cl.getSuperclass();
				}
			}
			
			// Reset initial values of push parameters (hack???)
			for(IParameter param: goal.getParameters())
			{
				if(((MParameter)param.getModelElement()).getEvaluationMode()==EvaluationMode.PUSH)
				{
					State	state	= null;
					if(((MParameter)param.getModelElement()).getDirection()==Direction.OUT)
					{
						state	= goal.getState();
						goal.setState(State.UNPROCESSED);	// Set hack state due to parameter protection
					}
					((RParameter)param).updateDynamicValue();
					if(state!=null)
					{
						goal.setState(state);
					}
				}
			}
			for(IParameterSet param: goal.getParameterSets())
			{
				if(((MParameter)param.getModelElement()).getEvaluationMode()==EvaluationMode.PUSH)
				{
					State	state	= null;
					if(((MParameter)param.getModelElement()).getDirection()==Direction.OUT)
					{
						state	= goal.getState();
						goal.setState(State.UNPROCESSED);	// Set hack state due to parameter protection
					}
					((RParameterSet)param).updateDynamicValues();
					if(state!=null)
					{
						goal.setState(state);
					}
				}
			}
			
			agent.getFeature(IInternalBDIAgentFeature.class).getCapability().addGoal(goal);
			goal.setLifecycleState(agent, RGoal.GoalLifecycleState.ADOPTED);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
}
