package jadex.bdi.testcases.misc;

import jadex.adapter.base.test.TestReport;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;

/**
 *  This plan performs an illegal action. 
 */
public class GetExternalAccessPlan extends Plan
{
	boolean	gotexta	= false;
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Create component.
		IComponentManagementService ces = (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
		SyncResultListener lis = new SyncResultListener();
		ces.createComponent("myagent", "jadex/bdi/testcases/misc/ExternalAccess.agent.xml", 
			"donothing", null, true, lis, null, null, false);
		IComponentIdentifier cid	= (IComponentIdentifier)lis.waitForResult();
		
		// Get external access.
		IResultListener lis2 = new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IBDIExternalAccess exta = (IBDIExternalAccess)result;
//				System.out.println("Got external access: "+exta);
				String	somevalue	= (String)exta.getBeliefbase().getBelief("somebelief").getFact();
//				System.out.println("Got fact: "+somevalue);	
				gotexta	= "some value".equals(somevalue);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		};
		ces.getExternalAccess(cid, lis2);

		// External access should not be made available before component has resumed.
		TestReport	tr	= new TestReport("#1", "No external access before resume.");
		waitFor(300);
		if(gotexta)
			tr.setFailed("Got external access");
		else
			tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		// External access should be made available after component has resumed.
		tr	= new TestReport("#2", "External access after resume.");
		ces.resumeComponent(cid, null);
		waitFor(300);
		if(gotexta)
			tr.setSucceeded(true);
		else
			tr.setFailed("Didn't get external access.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
