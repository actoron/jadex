package jadex.bdi.examples.blackjack.player;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;

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
		IDF df = (IDF)SServiceProvider.getService(getAgent(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IDFServiceDescription sd = df.createDFServiceDescription(null, "blackjack", null);
		IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
		ISearchConstraints sc = df.createSearchConstraints(-1, 0);
		
		// Use a subgoal to search for a dealer-agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(ad);
		ft.getParameter("constraints").setValue(sc);
		dispatchSubgoalAndWait(ft);
		IDFComponentDescription[]	result	= (IDFComponentDescription[])ft.getParameterSet("result").getValues();

		if(result==null || result.length==0)
		{
			getLogger().warning("No blackjack-dealer found.");
			fail();
		}
		else
		{
			// at least one matching description found,
			getLogger().info(result.length + " blackjack-dealer found");

			// choose one dealer randomly out of all the dealer-agents
			IComponentIdentifier dealer = result[new Random().nextInt(result.length)].getName();
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
