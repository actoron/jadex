package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.HashMap;
import java.util.Map;

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
		// Sub component will not be initialized before wait future is done.
		Future	wait	= new Future();

		// Create component.
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider
			.getServiceUpwards(getScope().getServiceProvider(), IComponentManagementService.class).get(this);
		IComponentIdentifier cid = ces.generateComponentIdentifier("ExternalAccessWorker");
		Map	args	= new HashMap();
		args.put("future", wait);
		IFuture init = ces.createComponent(cid.getLocalName(), "jadex/bdi/testcases/misc/ExternalAccessWorker.agent.xml",
			new CreationInfo(null, args, getComponentIdentifier(), false, false), null);
		
		// Get and use external access.
		final boolean[]	gotexta	= new boolean[3];	// 0: got exception, 1: got access, 2: got belief value.	
		IResultListener	lis	= new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IBDIExternalAccess exta = (IBDIExternalAccess)result;
				gotexta[0]	= true;
//				System.out.println("Got external access: "+exta);
				
				exta.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						IBDIInternalAccess bia = (IBDIInternalAccess)ia;
						Object fact = bia.getBeliefbase().getBelief("test").getFact();
						gotexta[1]	= "testfact".equals(fact);
						return null;
					}
				});
//				exta.getBeliefbase().getBelief("test").addResultListener(new DefaultResultListener() 
//				{
//					public void resultAvailable(Object source, Object result) 
//					{
//						((IEABelief)result).getFact().addResultListener(new DefaultResultListener()
//						{
//							public void resultAvailable(Object source, Object result) 
//							{
//								gotexta[1]	= "testfact".equals(result);
//							}
//						});
//					}
//				}); 
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				// Expected on first call.
			}
		};

		// External access should not be made available before component has resumed.
		TestReport	tr	= new TestReport("#1", "No external access before init.");
		waitFor(300);
		ces.getExternalAccess(cid).addResultListener(lis);
		waitFor(300);
		if(gotexta[0])
			tr.setFailed("Got external access");
		else
			tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		// External access should be made available after component is inited.
		tr	= new TestReport("#2", "External access after init.");
		wait.setResult(null);
		init.get(this);
		ces.getExternalAccess(cid).addResultListener(lis);
		waitFor(300);
		if(gotexta[0] && gotexta[1])
			tr.setSucceeded(true);
		else
			tr.setFailed("Didn't get external access or belief value.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
