package jadex.bdi.examples.cleanerworld_classic.environment;

import jadex.bdi.examples.cleanerworld_classic.Chargingstation;
import jadex.bdi.examples.cleanerworld_classic.Cleaner;
import jadex.bdi.examples.cleanerworld_classic.Environment;
import jadex.bdi.examples.cleanerworld_classic.RequestCompleteVision;
import jadex.bdi.examples.cleanerworld_classic.Vision;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdi.examples.cleanerworld_classic.Wastebin;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.fipa.Done;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;

/**
 *  Update the environment belief.
 */
public class UpdateEnvironmentPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			// Search and store the environment agent.
			if(getBeliefbase().getBelief("environmentagent").getFact()==null)
				searchEnvironmentAgent();
			if(getBeliefbase().getBelief("environmentagent").getFact()==null)
			{
				// If no environment agent found, wait a while before trying again.
				waitFor(5000);
			}
			else
			{
				RequestCompleteVision rv = new RequestCompleteVision();
				IGoal rg = createGoal("rp_initiate");
				rg.getParameter("receiver").setValue(getBeliefbase().getBelief("environmentagent").getFact());
				rg.getParameter("action").setValue(rv);
				rg.getParameter("ontology").setValue("cleaner_ontology");
				//rg.getParameter("language").setValue(SFipa.FIPA_SL0);

				try
				{
					dispatchSubgoalAndWait(rg);
					Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
					env.clear();
					Vision vision = ((RequestCompleteVision)((Done)rg.getParameter("result").getValue()).getAction()).getVision();
					Cleaner[] cleaners = vision.getCleaners();
					for(int i=0; i<cleaners.length; i++)
						env.addCleaner(cleaners[i]);
					Waste[] wastes = vision.getWastes();
					for(int i=0; i<wastes.length; i++)
						env.addWaste(wastes[i]);
					Wastebin[] wastebins = vision.getWastebins();
					for(int i=0; i<wastebins.length; i++)
						env.addWastebin(wastebins[i]);
					Chargingstation[] stations = vision.getStations();
					for(int i=0; i<stations.length; i++)
						env.addChargingStation(stations[i]);
					env.setDaytime(vision.isDaytime());
				}
				catch(GoalFailureException gfe)
				{
					gfe.printStackTrace();
					getLogger().warning("Request vision failed: "+gfe);
				}
				waitFor(100);
				//System.out.println("Updated environement");
			}
		}
	}

	/**
	 *  Search the environent agent and store its AID in the beliefbase.
	 */
	protected void searchEnvironmentAgent()
	{
		try
		{
			IDF df = (IDF)SServiceProvider.getService(getAgent(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
			IDFServiceDescription sd = new DFServiceDescription(null, "dispatch vision", null);
			IDFComponentDescription ad = new DFComponentDescription(null, sd);
			IDFComponentDescription[] tas = df.search(ad, null).get();
	
			if(tas.length!=0)
			{
				getBeliefbase().getBelief("environmentagent").setFact(tas[0].getName());
				if(tas.length>1)
					System.out.println("WARNING: more than environment agent found.");
			}
		}
		catch(GoalFailureException gfe)
		{
			getLogger().warning("DF search failed: "+gfe);
		}
	}
}
