package jadex.micro.testcases.servicefakeproxy;

import java.util.Arrays;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.RemoteTestBaseAgent;

/**
 *  Test if service implementations can be omitted when the agent implements them.
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ServiceFakeProxyTestAgent extends RemoteTestBaseAgent
{
	@Agent
	protected IInternalAccess agent;

	/**
	 *  The agent body.
	 */
	@AgentBody()
	public IFuture<Void> body()
	{
		TestReport tr1 = new TestReport("#1", "Test if local service proxy can be created");
		try 
		{
			IComponentManagementService cms = SServiceProvider.getServiceProxy(agent, agent.getComponentIdentifier().getRoot(), IComponentManagementService.class);
			IComponentDescription[] descs = cms.getComponentDescriptions().get();
			System.out.println(Arrays.toString(descs));
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			tr1.setFailed(e.getMessage());
		}
		
		TestReport tr2 = new TestReport("#1", "Test if remote service proxy can be created");
		try 
		{
//			String url	= SUtil.getOutputDirsExpression("jadex-applications-micro", true);	// Todo: support RID for all loaded models.
//			IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig(), new String[]{"-libpath", url, "-platformname", agent.getComponentIdentifier().getPlatformPrefix()+"_*",
//				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", //"-awareness", "false",
//			//	"-logging_level", "java.util.logging.Level.INFO",
//				"-gui", "false", "-simulation", "false", "-printpass", "false", "-wstransport", "false",
//				"-superpeerclient", "false",
//			}).get();
			IExternalAccess plat = STest.createPlatform();
			
			createProxies(plat).get();
			// awareness is disabled in testsuite
//			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000).get();
			
			IComponentManagementService cms = SServiceProvider.getServiceProxy(agent, plat.getComponentIdentifier(), IComponentManagementService.class);
			IComponentDescription[] descs = cms.getComponentDescriptions().get();
			System.out.println(Arrays.toString(descs));
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			tr1.setFailed(e.getMessage());
		}
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
		return IFuture.DONE;
	}
}
