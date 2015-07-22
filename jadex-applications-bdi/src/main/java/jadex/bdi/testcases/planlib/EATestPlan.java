package jadex.bdi.testcases.planlib;

import jadex.base.test.TestReport;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdi.planlib.protocols.ExponentialPriceCalculator;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

import java.util.List;
import java.util.Map;


/**
 *  Tests the english auction protocol with one initiator and four bidders with
 *  different price-strategies.
 */
public class EATestPlan extends AbstractMultipleAgentsPlan
{
	/**
	 * The body method is called on the instatiated plan instance from the
	 * scheduler.
	 */
	public void body()
	{
		// The round timeout, if the testcase fails, adjust it according to the
		// speed of your pc.
		long roundtimeout = 1000;

		// Create 4 bidders
		Map[] args = new Map[4];
		//Map[] args = new Map[1];
		for(int i=0; i<args.length; i++)
		{
			args[i] = SCollection.createHashMap();
			args[i].put("max_price", Double.valueOf(115+i*5));
			//args[i].put("participate", new Boolean(i!=2));
			args[i].put("participate", Boolean.TRUE);
		}
		List agents = createAgents("/jadex/bdi/testcases/planlib/EAReceiver.agent.xml", args);	

		TestReport tr = new TestReport("#1", "Test with four bidders. " +
			"Should terminate with a winner.");
		if(assureTest(tr))
		{
			try
			{
				IGoal ea = createGoal("eacap.ea_initiate");
				ea.getParameterSet("receivers").addValues(agents.toArray(new IComponentIdentifier[agents.size()]));
				ea.getParameter("cfp").setValue(Double.valueOf(110));
				ea.getParameter("auction_description").setValue(new AuctionDescription(getTime()+1000,
					roundtimeout, "Test auction 1"));
				ea.getParameter("limit").setValue(Double.valueOf(120));
				dispatchSubgoalAndWait(ea);
				Object[] res = (Object[])ea.getParameter("result").getValue();
				getLogger().info("Auction result: "+SUtil.arrayToString(res));
				if(ea.getParameter("result").getValue() != null)
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("No winner determined though this testcase should terminate with a winner.");
				}
	
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: " + e);
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#2", "Test with four bidders and offer generator. " +
			"Should terminate with a winner.");
		if(assureTest(tr))
		{
			try
			{
				IGoal ea = createGoal("eacap.ea_initiate");
				ea.getParameterSet("receivers").addValues(agents.toArray(new IComponentIdentifier[agents.size()]));
					
				ExponentialPriceCalculator pc = new ExponentialPriceCalculator(100, 10000, 1.1);
			
				ea.getParameter("cfp").setValue(pc.getCurrentOffer());
				ea.getParameter("cfp_info").setValue(pc);
				
				ea.getParameter("auction_description").setValue(new AuctionDescription(getTime()+1000,
					roundtimeout, "Test auction 1"));
				dispatchSubgoalAndWait(ea);
				Object[] res = (Object[])ea.getParameter("result").getValue();
				getLogger().info("Auction result: "+SUtil.arrayToString(res));
				if(ea.getParameter("result").getValue() != null)
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("No winner determined though this testcase should terminate with a winner.");
				}
	
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: " + e);
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#3", "Test if acceptance plan was invoked once.");
		int ac = ((Integer)getBeliefbase().getBelief("acceptplan_invoked").getFact()).intValue();
		if(ac==1)
			tr.setSucceeded(true);
		else
			tr.setReason("Acceptance plan was invoked: "+ac);
		
		// Add the last report, so that the testcase can terminate.
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
	
	
}
