package jadex.tools.testcenter;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 *  Perform one testcase.
 */
public class PerformTestPlan extends Plan
{
	//-------- attributes --------
	
	/** The created test agent. */
	protected IComponentIdentifier	testagent;
	
	//-------- methods --------
	
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Testcase testcase = (Testcase)getParameter("testcase").getValue();
		Long timeout = (Long)getBeliefbase().getBelief("timeout").getFact();

		getLogger().info("Performing testcase: "+testcase.getType());
		long	starttime	= getTime();

		IComponentManagementService	ces	= (IComponentManagementService)getScope().getServiceProvider().getService(IComponentManagementService.class).get(this);
		try
		{
//			SyncResultListener	id	= new SyncResultListener();
			SyncResultListener	res	= new SyncResultListener();
			Map	args	= new HashMap();
			args.put("timeout", timeout);
			IFuture ret = ces.createComponent(null, testcase.getType(), new CreationInfo(args, getComponentIdentifier()), res);
//			testagent	= (IComponentIdentifier)id.waitForResult();
			testagent = (IComponentIdentifier)ret.get(this);
			Testcase	result	= (Testcase)((Map)res.waitForResult(timeout)).get("testresults");
			if(result!=null)
			{
				testcase.setTestCount(result.getTestCount());
				testcase.setReports(result.getReports());
			}
			else
			{
				testcase.setTestCount(1);
				testcase.setReports(new TestReport[]{new TestReport("#1", "Test execution", false, "Component did not produce a result.")});
			}
		}
		catch(TimeoutException te)
		{
			ces.destroyComponent(testagent);
			testagent	= null;
			testcase.setReports(new TestReport[]{new TestReport("answer", 
				"Test center report", false, "Test agent did not finish in time.")});
		}
		catch(Exception cause)
		{
			cause.printStackTrace();
			if(testagent!=null)
			{
				ces.destroyComponent(testagent);
				testagent	= null;
			}
			testcase.setReports(new TestReport[]{new TestReport("creation", "Test center report", 
				false, "Test agent could not be created: "+cause)});
		}

		testcase.setDuration(getTime()-starttime);
	}
	
	/**
	 *  When plan is aborted, kill created agent.
	 */
	public void aborted()
	{
		if(testagent!=null)
		{
			IComponentManagementService	cms	= (IComponentManagementService)getScope().getServiceProvider().getService(IComponentManagementService.class);
			// Empty listener avoids failures printed to console.
			cms.destroyComponent(testagent).get(this);
			/*, new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
				}
				public void exceptionOccurred(Object source, Exception exception)
				{
				}
			});*/
		}
	}
}
