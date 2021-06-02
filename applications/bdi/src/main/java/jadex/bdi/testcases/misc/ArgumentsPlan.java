package jadex.bdi.testcases.misc;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.TimeoutException;
import jadex.commons.collection.SCollection;

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
			Map<String, Object> args = SCollection.createHashMap();
			args.put("creator", getComponentIdentifier());
			getAgent().createComponent(
				new CreationInfo(args).setFilename("/jadex/bdi/testcases/misc/ArgumentsWorker.agent.xml")).get();

			waitForMessageEvent("inform_created", 1000);
			tr.setSucceeded(true);
		}
		catch(GoalFailureException e)
		{
			tr.setReason("Could not create worker agent.");
		}
		catch(TimeoutException e)
		{
			tr.setReason("Worker did not send message.");
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
