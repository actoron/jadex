package jadex.bdi.testcases.service;

import java.util.Collection;
import java.util.Iterator;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;

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
		
		Collection services = getAgent().getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IBeliefGetter.class, ServiceScope.COMPONENT_ONLY));
//		System.out.println("Found: "+services);
		if(services!=null)
		{
			for(Iterator it=services.iterator(); it.hasNext(); )
			{
				IBeliefGetter getter = (IBeliefGetter)it.next();
				/*Object fact =*/ getter.getFact("money").get();
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
