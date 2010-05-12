package deco4mas.examples.agentNegotiation.evaluate;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Evaluate Logger
 */
public class EvaluateLoggerPlan extends Plan
{
	public void body()
	{
		try
		{
			// inform logger
			IMessageEvent me = createMessageEvent("InformLoggerAboutEnd");
			IComponentIdentifier logger = (IComponentIdentifier) getBeliefbase().getBelief("logger").getFact();
			List cis = new LinkedList();
			cis.add(logger);
			me.getParameter("receivers").setValue(cis);
			sendMessage(me);

		} catch (Exception e)
		{
			fail(e);
		}
	}
}
