package jadex.bdiv3;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RCapability;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.IAction;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	
	/**
	 *  Adopt a new goal.
	 *  @param goal The goal.
	 */
	public void adoptGoal(final Object goal)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.getRuleSystem().observeObject(goal);

		BDIModel bdim = ip.getBDIModel();
		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(mgoal, goal);

		Method[] ms = goal.getClass().getDeclaredMethods();
		for(Method m: ms)
		{
			if(m.isAnnotationPresent(GoalTargetCondition.class))
			{			
				Annotation[][] annos = m.getParameterAnnotations();
				List<String> events = new ArrayList<String>();
				for(Annotation[] ana: annos)
				{
					for(Annotation an: ana)
					{
						if(an instanceof jadex.rules.eca.annotations.Event)
						{
							events.add(((jadex.rules.eca.annotations.Event)an).value());
						}
					}
				}
				Rule rule = new Rule("goal_"+goal, 
					new MethodCondition(goal, m), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule rule, Object context)
					{
						System.out.println("Goal succeeded: "+rgoal);
						
						// todo: call rgoal.finished()? succeeded or set lifecycle state directly?
						//rgoal.
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				
				ip.getRuleSystem().getRulebase().addRule(rule);
			}
		}
		
//		System.out.println("adopt goal");
		ip.scheduleStep(new AdoptGoalAction(rgoal));
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	public void writeField(Object val, String fieldname, Object obj)
	{
		try
		{
//			System.out.println("write: "+val+" "+fieldname+" "+obj);
			Field f = obj.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			f.set(obj, val);
			BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
			RuleSystem rs = ip.getRuleSystem();
			rs.addEvent(new Event(fieldname, val));
			
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
