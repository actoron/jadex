package jadex.bdi.examples.hunterprey_classic.creature.actsense;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.IDFServiceDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.PlanFailureException;
import jadex.bridge.IAgentIdentifier;


/**
 *  Offers a methods for interacting with the hunterprey environment.
 */
public abstract class RemoteActionPlan extends Plan
{
	/**
	 *  Request an action from the environment.
	 *  @param action The action.
	 *  @return The finished goal.
	 *  @throws GoalFailureException	when the request goal fails.
	 */
	public IGoal requestAction(Object action)
		throws GoalFailureException
	{
		// Search and store the environment agent.
		IAgentIdentifier	env	= searchEnvironmentAgent();

		IGoal rg = createGoal("rp_initiate");
		rg.getParameter("receiver").setValue(env);
		rg.getParameter("action").setValue(action);
//		rg.getParameter("ontology").setValue(HunterPreyOntology.ONTOLOGY_NAME);
		//rg.getParameter("language").setValue(SFipa.NUGGETS_XML);

		dispatchSubgoalAndWait(rg);
		
		return rg;
	}

	/**
	 *  When the plan has failed, assume that environment is down.
	 *  Remove fact to enable new search for environment.
	 */
	public void failed()
	{
		// Received a timeout. Probably the environment agent has died.
		getBeliefbase().getBelief("environmentagent").setFact(null);
	}
	
	//--------- helper methods --------
	
	/**
	 *  Search the environent agent and store its AID in the beliefbase.
	 */
	protected IAgentIdentifier	searchEnvironmentAgent()
	{
		IAgentIdentifier	res	= (IAgentIdentifier)getBeliefbase().getBelief("environmentagent").getFact();

		if(res==null)
		{
			IDF df = (IDF)getScope().getPlatform().getService(IDF.class, SFipa.DF_SERVICE);
			IDFServiceDescription sd = df.createDFServiceDescription(null, "hunter-prey environment", null);
			IDFAgentDescription ad = df.createDFAgentDescription(null, sd);
			ISearchConstraints	cons = df.createSearchConstraints(-1, 0);
			
			// Create a service description to search for.
			/*ServiceDescription sd = new ServiceDescription();
			sd.setType("hunter-prey environment");
			AgentDescription dfadesc = new AgentDescription();
			dfadesc.addService(sd);*/
	
			// Use a subgoal to search for a translation agent
			IGoal ft = createGoal("df_search");
			ft.getParameter("description").setValue(ad);
			IBelief bel = getBeliefbase().getBelief("df");
			ft.getParameter("df").setValue(bel.getFact());
			dispatchSubgoalAndWait(ft);
			//Object result = ft.getResult();
			IDFAgentDescription[] tas = (IDFAgentDescription[])ft.getParameterSet("result").getValues();

			if(tas.length!=0)
			{
				// Found.
				res	= tas[0].getName();
				getBeliefbase().getBelief("environmentagent").setFact(res);
				if(tas.length>1)
					getLogger().warning("More than environment agent found.");
			}
			else
			{
				// Not found.
				throw new PlanFailureException();
			}
		}

		return res;
	}

}
