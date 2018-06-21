package jadex.bdi.testcases.planlib;

import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdi.planlib.protocols.AuctionDescription;
import jadex.bdi.planlib.protocols.ExponentialPriceCalculator;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

/**
 *  Tests the dutch auction protocol with one initiator and four bidders with
 *  different price-strategies.
 */
public class DATestPlan extends AbstractMultipleAgentsPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// The round timeout, if the testcase fails, adjust it according to the
		// speed of your pc.
		long roundtimeout = 250;

		// Create 4 bidders
		Map[] args = new Map[4];
		for(int i=0; i<args.length; i++)
		{
			args[i] = SCollection.createHashMap();
			args[i].put("max_price", Double.valueOf(115+i*5));
			//args[i].put("participate", new Boolean(i!=2));
			args[i].put("participate", Boolean.TRUE);
		}
		List agents = createAgents("/jadex/bdi/testcases/planlib/DAReceiver.agent.xml", args);	

		// Test five auctions with different strategies and different endings.
		TestReport tr = new TestReport("#1", "Test with four bidders." +
			" Should terminate with a winner.");
		if(assureTest(tr))
		{
			try
			{
				IGoal da = createGoal("dacap.da_initiate");
				da.getParameterSet("receivers").addValues(agents.toArray(new IComponentIdentifier[agents.size()]));
				da.getParameter("cfp").setValue(Double.valueOf(200));
				da.getParameter("auction_description").setValue(new AuctionDescription(getTime()+1000,
					roundtimeout, "Test auction 1"));
				dispatchSubgoalAndWait(da);
				getLogger().info("Auction result: "+SUtil.arrayToString(da.getParameter("result").getValue()));
				if(da.getParameter("result").getValue() != null)
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("No winner determined though this testcase should terminate with a winner!");
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
				IGoal ea = createGoal("dacap.da_initiate");
				ea.getParameterSet("receivers").addValues(agents.toArray(new IComponentIdentifier[agents.size()]));
					
				ExponentialPriceCalculator pc = new ExponentialPriceCalculator(200, 100, 1.1);
			
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

		// Add the last report, so that the testcase can terminate.
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
