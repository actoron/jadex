package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;
	
/**
 *  Test threaded access to raw services.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RemoteBlockingTestAgent	extends TestAgent
{
	/**
	 *  The test count.
	 */
	protected int	getTestCount()
	{
		return 1;
	}

	protected IFuture<Void> performTests(Testcase tc)
	{
		// timeout none due to remote call and simulation mode
		IExternalAccess	exta	= createPlatform(null).get(Timeout.NONE);
		
		ILibraryService	ls = exta.searchService(
			new ServiceQuery<>(ILibraryService.class)).get(Timeout.NONE);
		
//		cms.getComponentDescriptions().get(Timeout.NONE);
		SComponentManagementService.getComponentDescriptions(agent).get(Timeout.NONE);
		
		tc.addReport(new TestReport("#1", "Test blocking wait.", true, null));
		
		return IFuture.DONE;
	}
}
