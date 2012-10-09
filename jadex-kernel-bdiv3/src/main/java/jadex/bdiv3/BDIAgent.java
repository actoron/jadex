package jadex.bdiv3;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
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
	 * 
	 */
	public void adoptGoal(Object goal)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.getRuleSystem().observeObject(goal);
		BDIModel bdim = ip.getBDIModel();
		MGoal mgoal = bdim.getGoal(goal.getClass());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		RGoal rgoal = new RGoal(goal, mgoal);
		
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
