package jadex.bdi.examples.blackjack.player;

import java.util.Random;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.fipa.SearchConstraints;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;

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
		IDFServiceDescription sd = new DFServiceDescription(null, "blackjack", null);
		IDFComponentDescription ad = new DFComponentDescription(null, sd);
		ISearchConstraints sc = new SearchConstraints(-1, 0);
		IDFComponentDescription[]	result	= df.search(ad, sc).get();

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
