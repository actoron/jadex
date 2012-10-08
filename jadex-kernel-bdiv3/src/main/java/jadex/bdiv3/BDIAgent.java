package jadex.bdiv3;

import java.lang.reflect.Field;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

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
		
//		System.out.println("adopt goal");
		ip.scheduleStep(new AdoptGoalAction(goal));
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
