package jadex.bdi.examples.blackjack.player;

import jadex.bdi.examples.blackjack.dealer.IDealerService;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.IService;

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
//		IDF df = (IDF)getAgent().getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IDF.class, ServiceScope.PLATFORM)).get();
//		IDFServiceDescription sd = new DFServiceDescription(null, "blackjack", null);
//		IDFComponentDescription ad = new DFComponentDescription(null, sd);
//		ISearchConstraints sc = new SearchConstraints(-1, 0);
//		IDFComponentDescription[]	result	= df.search(ad, sc).get();

		IDealerService ds = getAgent().getLocalService(IDealerService.class);
		
//		if(result==null || result.length==0)
		if(ds==null)
		{
			getLogger().warning("No blackjack-dealer found.");
			fail();
		}
		else
		{
			// at least one matching description found,
//			getLogger().info(result.length + " blackjack-dealer found");
			getLogger().info(ds + " blackjack-dealer found");

//			IComponentIdentifier dealer = result[new Random().nextInt(result.length)].getName();
			getBeliefbase().getBelief("dealer").setFact(((IService)ds).getServiceId().getProviderId());
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
