package jadex.bdiv3;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.RCapability;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for application agents.
 */
public class BDIAgent extends MicroAgent
{
	/**
	 *  Get the capability.
	 *  @return the capability.
	 */
	public RCapability getCapability()
	{
		return ((BDIAgentInterpreter)getInterpreter()).getCapability();
	}
	
//	/**
//	 *  Adopt a new goal.
//	 *  @param goal The goal.
//	 */
//	public void adoptGoal(final Object goal)
//	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
//		ip.getRuleSystem().observeObject(goal);
//
//		BDIModel bdim = ip.getBDIModel();
//		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass());
//		if(mgoal==null)
//			throw new RuntimeException("Unknown goal type: "+goal);
//		final RGoal rgoal = new RGoal(mgoal, goal);
//
////		System.out.println("adopt goal");
//		ip.scheduleStep(new AdoptGoalAction(rgoal));
//	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T> IFuture<T> dispatchTopLevelGoal(final T goal)
	{
		final Future<T> ret = new Future<T>();
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
//		ip.getRuleSystem().observeObject(goal);

		BDIModel bdim = ip.getBDIModel();
		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(mgoal, goal, null);
		rgoal.addGoalListener(new ExceptionDelegationResultListener<Void, T>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(goal);
			}
		});

//		System.out.println("adopt goal");
		ip.scheduleStep(new AdoptGoalAction(rgoal));
	
		return ret;
	}
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final String name, final IBeliefListener listener)
	{
		List<String> events = new ArrayList<String>();
		RGoal.addBeliefEvents(this, events, name);

		final boolean multi = ((MCapability)getCapability().getModelElement()).getBelief(name).isMulti(getClassLoader());
		
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
		Rule<Void> rule = new Rule<Void>(rulename, 
			ICondition.TRUE_CONDITION, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
			{
				if(!multi)
				{
					listener.beliefChanged(event.getContent());
				}
				else
				{
					if(event.getType().startsWith(ChangeEvent.FACTADDED))
					{
						listener.factAdded(event.getContent());
					}
					else if(event.getType().startsWith(ChangeEvent.FACTREMOVED))
					{
						listener.factAdded(event.getContent());
					}
				}
				return IFuture.DONE;
			}
		});
		rule.setEvents(events);
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.getRuleSystem().getRulebase().addRule(rule);
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener listener)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
		ip.getRuleSystem().getRulebase().removeRule(rulename);
	}
	
	//-------- internal method used for rewriting field access -------- 
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	public void writeField(Object val, final String fieldname, Object obj)
	{
		// todo: support for belief sets (un/observe values? insert mappers when setting value etc.
		
		try
		{
//			System.out.println("write: "+val+" "+fieldname+" "+obj);
			BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
			RuleSystem rs = ip.getRuleSystem();

			Field f = obj.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			
			// unobserve old value for property changes
			Object oldval = f.get(obj);
			rs.unobserveObject(oldval);
			
			f.set(obj, val);
			rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+fieldname, val));
			
			// observe new value for property changes
			rs.observeObject(val, true, false, new IResultCommand<IFuture<IEvent>, PropertyChangeEvent>()
			{
				public IFuture<IEvent> execute(final PropertyChangeEvent event)
				{
					return scheduleStep(new IComponentStep<IEvent>()
					{
						public IFuture<IEvent> execute(IInternalAccess ia)
						{
//							Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname+"."+event.getPropertyName(), event.getNewValue());
							Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname, event.getNewValue());
							return new Future<IEvent>(ev);
						}
					});
				}
			});
			
			// initiate a step to reevaluate the conditions
			scheduleStep(new IComponentStep()
			{
				public IFuture execute(IInternalAccess ia)
				{
					return IFuture.DONE;
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
