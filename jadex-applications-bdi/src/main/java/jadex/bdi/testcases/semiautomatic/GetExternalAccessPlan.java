package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.IResultListener;

/**
 *  This plan performs an illegal action. 
 */
public class GetExternalAccessPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		final IComponentExecutionService ces = (IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class);
		
		final SyncResultListener lis2 = new SyncResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess exta = (IExternalAccess)result;
				System.out.println("Got external access: "+exta);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		};
		
		SyncResultListener lis = new SyncResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				System.out.println("created: "+result);
				ces.getExternalAccess((IComponentIdentifier)result, lis2);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		};
		ces.createComponent("myagent", "jadex/bdi/testcases/semiautomatic/ExternalAccess.agent.xml", 
			"donothing", null, true, lis, null, null, false);
		
		lis.waitForResult();
		
		lis2.waitForResult();
	}
}
