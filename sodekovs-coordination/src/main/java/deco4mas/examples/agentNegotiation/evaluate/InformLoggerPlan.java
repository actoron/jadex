package deco4mas.examples.agentNegotiation.evaluate;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;

/**
 * Creates SMAs for the workflow
 */
public class InformLoggerPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		try
		{
			IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer().getService(
				IComponentManagementService.class);
			
			if ((IComponentIdentifier) getBeliefbase().getBelief("logger").getFact() == null)
			{
				dispatchSubgoalAndWait(createGoal("getLogger"));
			}

			//get Info
			IInternalEvent reason = (IInternalEvent) getReason();
			String name = (String) reason.getParameter("id").getValue();
			Long value = (Long) reason.getParameter("value").getValue();
						
			//get Excess
			SyncResultListener lis = new SyncResultListener();
			cms.getExternalAccess((IComponentIdentifier) getBeliefbase().getBelief("logger").getFact(), lis);
			ExternalAccessFlyweight logger = (ExternalAccessFlyweight) lis.waitForResult();
			
			//create Goal
			IGoal informGoal = logger.createGoal("informationProcess");
			informGoal.getParameter("id").setValue(name);
			informGoal.getParameter("value").setValue(value);
			logger.dispatchTopLevelGoal(informGoal);

		} catch (Exception e)
		{
			fail(e);
		}
	}
}
