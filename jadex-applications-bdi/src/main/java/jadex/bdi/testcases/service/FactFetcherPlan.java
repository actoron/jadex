package jadex.bdi.testcases.service;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Test fetching a fact via a service.
 */
public class FactFetcherPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		TestReport tr = new TestReport("#1", "Tests if own service can be found.");
		
		Collection services = (Collection)SServiceProvider.getServices((IServiceProvider)getComponentFeature(IRequiredServiceFeature.class), 
			IBeliefGetter.class, RequiredServiceInfo.SCOPE_LOCAL).get(this);
//		System.out.println("Found: "+services);
		if(services!=null)
		{
			for(Iterator it=services.iterator(); it.hasNext(); )
			{
				IBeliefGetter getter = (IBeliefGetter)it.next();
				/*Object fact =*/ getter.getFact("money").get(this);
//				System.out.println("Fact is: "+fact);
				tr.setSucceeded(true);
			}
		}
		else
		{
			tr.setReason("No service found.");
		}
		
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
