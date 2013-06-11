package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

/**
 *  Test threaded access to raw services.
 */
@Agent
@Arguments(@Argument(name="testcnt", clazz=int.class, defaultvalue="1"))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RemoteBlockingTestAgent	extends TestAgent
{
	protected IFuture<Void> performTests(Testcase tc)
	{
		IExternalAccess	exta	= createPlatform(null).get();
		
		IComponentManagementService	cms	= SServiceProvider.getService(exta.getServiceProvider(),
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		
		cms.getComponentDescriptions().get();
		
		tc.addReport(new TestReport("#1", "Test blocking wait.", true, null));
		
		return IFuture.DONE;
	}
}
