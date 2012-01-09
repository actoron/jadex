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
	 * 
	 */
	protected void writeField(Object val, String fieldname)
	{
		try
		{
			System.out.println("write: "+this+" "+val+" "+fieldname);
			Field f = this.getClass().getDeclaredField(fieldname);
			f.setAccessible(true);
			f.set(this, val);
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
