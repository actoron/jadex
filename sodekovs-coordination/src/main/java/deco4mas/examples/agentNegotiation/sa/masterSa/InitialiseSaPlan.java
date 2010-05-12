package deco4mas.examples.agentNegotiation.sa.masterSa;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Init Sas
 */
public class InitialiseSaPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		String[] names = (String[]) getBeliefbase().getBeliefSet("names").getFacts();
		Integer[] quantities = (Integer[]) getBeliefbase().getBeliefSet("quantities").getFacts();
		Integer[] length = (Integer[]) getBeliefbase().getBeliefSet("length").getFacts();

		IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
			IComponentManagementService.class);
		Random rnd = new Random();
	
		if (names.length == quantities.length)
		{
			for (int i = 0; i < names.length; i++)
			{
				for (int j = 0; j < (Integer) quantities[i]; j++)
				{
					Map args = new HashMap();
					args.put("providedService", names[i]);
					args.put("serviceLength", length[i]);
	
					Double proposal = 1000.0;
	
					args.put("proposalBase", proposal.intValue());
					cms.createComponent("SA" + id, "deco4mas/examples/AgentNegotiation/sa/serviceAgent.agent.xml", new CreationInfo(
						null, args, interpreter.getParent().getComponentIdentifier()), null, null);
					id++;
				}
			}
		} else
		{
			fail();
		}
		killAgent();
	}
}
