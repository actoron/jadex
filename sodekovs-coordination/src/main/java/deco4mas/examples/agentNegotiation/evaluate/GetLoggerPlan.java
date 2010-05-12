package deco4mas.examples.agentNegotiation.evaluate;

import jadex.base.fipa.ComponentIdentifier;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ISearchConstraints;

/**
 * Creates SMAs for the workflow
 */
public class GetLoggerPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		try
		{
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);
			IComponentIdentifier logger = (IComponentIdentifier) getBeliefbase().getBelief("logger").getFact();

			if (logger == null)
			{
				// search Logger over cms
				Integer ids = id;

				ComponentIdentifier comp = null;
				IComponentDescription desc = cms.createComponentDescription(null, null, null, "BDI Agent", null);
				ISearchConstraints constraints = cms.createSearchConstraints(1, 0);

				SyncResultListener lis = new SyncResultListener();
				cms.searchComponents(desc, constraints, lis);
				 IComponentDescription[] result = (IComponentDescription[]) lis.waitForResult();
				if (result.length != 0)
				{
					for (IComponentDescription compDes : result)
					{
						if (compDes.getName().getName().contains("Logger"))
						{
							logger = (IComponentIdentifier) compDes.getName();
						}
					}
					
				} else
				{
					System.out.println("Can't find logger!");
					fail();
				}
			}
			getBeliefbase().getBelief("logger").setFact(logger);

		} catch (Exception e)
		{
			fail(e);
		}
	}
}
