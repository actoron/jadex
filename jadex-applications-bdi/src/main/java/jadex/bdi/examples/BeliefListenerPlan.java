package jadex.bdi.examples;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.Plan;

/**
 *  Test if a belief change can be observed in a listener.
 */
public class BeliefListenerPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		getBeliefbase().getBelief("text").addBeliefListener(new IBeliefListener()
		{
			public void beliefChanged(AgentEvent ae)
			{
				System.out.println("The belief changed: "+ae.getValue());
			}
		});
		getBeliefbase().getBelief("text").setFact("new text 1");
		getBeliefbase().getBelief("text").setFact("new text 2");
		
		getBeliefbase().getBeliefSet("texts").addBeliefSetListener(new IBeliefSetListener()
		{
			public void factChanged(AgentEvent ae)
			{
				System.out.println("Fact changed: "+ae.getValue());
			}
			public void factAdded(AgentEvent ae)
			{
				System.out.println("Fact added: "+ae.getValue());
			}
			public void factRemoved(AgentEvent ae)
			{
				System.out.println("Fact removed: "+ae.getValue());
			}
		});
		
		waitFor(10);
		
		getBeliefbase().getBeliefSet("texts").addFact("d");
		getBeliefbase().getBeliefSet("texts").removeFact("a");
	}
}
