package jadex.bdi.examples.blackjack.player;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

import java.util.Random;

/**
 *
 */
public class PlayerSearchDealerPlan extends Plan
{
	//-------- methods --------

	/**
	 *  First the player searches a dealer, then sends a join-request to this
	 *  dealer.
	 */
	public void body()
	{
		//System.out.println("Searching dealer...");
		// Create a service description to search for.
		IDF df = (IDF)getScope().getPlatform().getService(IDF.class);
		IDFServiceDescription sd = df.createDFServiceDescription(null, "blackjack", null);
		IDFAgentDescription ad = df.createDFAgentDescription(null, sd);
		ISearchConstraints sc = df.createSearchConstraints(-1, 0);
		
		/*ServiceDescription sd = new ServiceDescription();
		sd.setType("blackjack");
		AgentDescription dfadesc = new AgentDescription();
		dfadesc.addService(sd);
		SearchConstraints	sc	= new SearchConstraints();
		sc.setMaxResults(-1);*/

		// Use a subgoal to search for a dealer-agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(ad);
		ft.getParameter("constraints").setValue(sc);
		dispatchSubgoalAndWait(ft);
		IDFAgentDescription[]	result	= (IDFAgentDescription[])ft.getParameterSet("result").getValues();

		if(result==null || result.length==0)
		{
			getLogger().warning("No blackjack-dealer found.");
			fail();
		}
		else
		{
			// at least one matching AgentDescription found,
			getLogger().info(result.length + " blackjack-dealer found");

			// choose one dealer randomly out of all the dealer-agents
			IAgentIdentifier dealer = result[new Random().nextInt(result.length)].getName();
			getBeliefbase().getBelief("dealer").setFact(dealer);
		}

	}

	/**
	 *  Called when something went wrong (e.g. timeout).
	 */
	public void	failed()
	{
		// Remove dealer fact.
		getBeliefbase().getBelief("dealer").setFact(null);
	}
}
