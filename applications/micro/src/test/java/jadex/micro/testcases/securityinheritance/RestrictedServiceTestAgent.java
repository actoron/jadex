package jadex.micro.testcases.securityinheritance;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent testing whether unrestricted security status is inherited in via service interfaces.
 *
 */

@Agent(autoprovide = Boolean3.TRUE)
public class RestrictedServiceTestAgent extends TestAgent implements IInheritingService
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		tc.setTestCount(1);
		final Future<Void> ret = new Future<Void>();
		TestReport tr = new TestReport("Restricted Service Test", "Tests if restricted service is not marked as unrestricted.");
		
		ServiceQuery<IInheritingService> query = new ServiceQuery<>(IInheritingService.class);
		query.setScope(ServiceScope.GLOBAL);
		query.setProvider(agent.getId());
		
		agent.searchService(query, 0).then((rserv) ->
		{
			if (((IService) rserv).getServiceId().isUnrestricted())
				tr.setSucceeded(true);
			else
				tr.setFailed("Method isUnrestricted() of service inheriting unrestricted interface ID returned false.");
				
			
			tc.addReport(tr);
			ret.setResult(null);
		}).catchEx(ret);
		
		return ret;
	}
}
