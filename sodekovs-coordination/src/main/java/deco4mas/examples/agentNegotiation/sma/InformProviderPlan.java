package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Inform provider about sign
 */
public class InformProviderPlan extends Plan
{
	public void body()
	{
		//inform provider
		IMessageEvent me = createMessageEvent("informProviderAboutSign");
		IComponentIdentifier provider = (IComponentIdentifier)getBeliefbase().getBelief("provider").getFact();
		List cis= new LinkedList();
		cis.add(provider);
		me.getParameter("receivers").setValue(cis);
		me.getParameter("content").setValue(this.getComponentIdentifier().getName());
		sendMessage(me);
	}
}
