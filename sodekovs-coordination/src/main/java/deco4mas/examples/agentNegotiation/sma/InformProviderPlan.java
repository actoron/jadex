package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Inform provider about sign
 */
public class InformProviderPlan extends Plan
{
	public void body()
	{
		Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

		IComponentIdentifier provider = (IComponentIdentifier) getBeliefbase().getBelief("provider").getFact();
		smaLogger.info("inform provider " + provider.getLocalName());

		// inform provider
		IMessageEvent me = createMessageEvent("informMessage");

		List cis = new LinkedList();
		cis.add(provider);
		me.getParameter("receivers").setValue(cis);
		me.getParameter("content").setValue(this.getComponentIdentifier().getName());
		sendMessage(me);
	}
}
