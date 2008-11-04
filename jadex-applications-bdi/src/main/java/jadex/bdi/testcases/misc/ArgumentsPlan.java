package jadex.bdi.testcases.misc;

import jadex.bdi.planlib.test.TestReport;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.collection.SCollection;

import java.util.Map;

/**
 *  Test if arguments can be accessed.
 */
public class ArgumentsPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test if a worker agent can be started and supplied with arguments.");
		try
		{
			IGoal ca = createGoal("amscap.ams_create_agent");
			ca.getParameter("type").setValue("/jadex/bdi/testcases/misc/ArgumentsWorker.agent.xml");
			Map args = SCollection.createHashMap();
			args.put("creator", getAgentIdentifier());
			ca.getParameter("arguments").setValue(args);
			dispatchSubgoalAndWait(ca);
			IAgentIdentifier worker = (IAgentIdentifier)ca.getParameter("agentidentifier").getValue();
			waitForMessageEvent("inform_created", 1000);
			tr.setSucceeded(true);
		}
		catch(GoalFailureException e)
		{
			tr.setReason("Could not create worker agent.");
			getLogger().severe("Exception while creating the worker agent: "+ e);
		}
		catch(TimeoutException e)
		{
			tr.setReason("Worker did not send message.");
			getLogger().severe("Timeout while waiting for message receival: "+ e);
			
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		/*String[] belnames = getBeliefbase().getBeliefNames();
		for(int i=0; i<belnames.length; i++)
		{
			IBelief bel = getBeliefbase().getBelief(belnames[i]);
			IMElement mbel = bel.getModelElement();
			if(mbel instanceof IMBelief && ((IMBelief)mbel).getExported().equals(IMTypedElement.EXPORTED_TRUE))
				System.out.println(bel.getName()+": "+bel.getFact());
		}*/
		//Map args = getScope().getArguments();
		//System.out.println("Arguments: "+args);
	}
}
