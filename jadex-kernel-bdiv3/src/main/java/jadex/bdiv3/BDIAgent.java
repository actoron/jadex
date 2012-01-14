package jadex.bdiv3;

import java.lang.reflect.Field;

import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

/**
 *  Base class for application agents.
 */
public class BDIAgent extends MicroAgent
{
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	public void writeField(Object val, String fieldname, Object obj)
	{
		try
		{
			System.out.println("write: "+val+" "+fieldname+" "+obj);
			Field f = obj.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			f.set(obj, val);
			BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
			RuleSystem rs = ip.getRuleSystem();
			rs.addEvent(new Event(fieldname, val));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
