package jadex.bdiv3;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RCapability;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Field;

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
	 * 
	 */
	public <T> IFuture<T> dispatchGoalAndWait(final T goal)
	{
		final Future<T> ret = new Future<T>();
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.getRuleSystem().observeObject(goal);

		BDIModel bdim = ip.getBDIModel();
		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(mgoal, goal);
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
