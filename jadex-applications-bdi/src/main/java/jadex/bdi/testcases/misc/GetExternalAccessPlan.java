package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEABelief;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.SServiceProvider;

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
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider
			.getServiceUpwards(getScope().getServiceProvider(), IComponentManagementService.class).get(this);
		IFuture ret = ces.createComponent(null, "jadex/bdi/testcases/misc/ExternalAccess.agent.xml",
			new CreationInfo("donothing", null, getComponentIdentifier(), true, false), null);
		IComponentIdentifier cid = (IComponentIdentifier)ret.get(this);
		
		// Get external access.
		IResultListener lis2 = new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IBDIExternalAccess exta = (IBDIExternalAccess)result;
//				System.out.println("Got external access: "+exta);
				exta.getBeliefbase().getBelief("somebelief").addResultListener(new DefaultResultListener() 
				{
					public void resultAvailable(Object source, Object result) 
					{
						((IEABelief)result).getFact().addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result) 
							{
								gotexta	= "some value".equals(result);
							}
						});
					}
				}); 

				// alternative with blocking calls
//				ThreadSuspendable sus = new ThreadSuspendable(new Object());
//				String	somevalue	= (String)((IEBelief)((IEBeliefbase)exta.getBeliefbase().get(sus))
//					.getBelief("somebelief").get(sus)).getFact().get(sus);
				
//				String	somevalue	= (String)exta.getBeliefbase().getBelief("somebelief").getFact();
//				System.out.println("Got fact: "+somevalue);	
//				gotexta	= "some value".equals(somevalue);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		};
		IFuture fut = ces.getExternalAccess(cid);
		fut.addResultListener(lis2);

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
		ces.resumeComponent(cid);
		waitFor(300);
		if(gotexta)
			tr.setSucceeded(true);
		else
			tr.setFailed("Didn't get external access.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
