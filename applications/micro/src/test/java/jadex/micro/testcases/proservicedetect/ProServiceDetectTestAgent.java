package jadex.micro.testcases.proservicedetect;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that implements a service and uses it automatically as
 *  provided service (without declaration).
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
//@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
//@ProvidedServices(@ProvidedService(type=ITestService.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ProServiceDetectTestAgent extends JunitAgentTest implements ITestService
{
	/** 
	 * The agent body.
	 */
	@AgentBody
	public void body(IInternalAccess agent)
	{
		// test if agent has the provided service
		TestReport tr = new TestReport("#1", "Test if provided service is present.");
		IService iser = (IService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(ITestService.class);
		if(iser==null)
		{
			tr.setFailed("Auto provided service not found.");
		}
		else
		{
			tr.setSucceeded(true);
		}
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killComponent();
	}
	
	/**
	 *  A test method.
	 */
	public IFuture<Void> testMethod()
	{
		return IFuture.DONE;
	}
}
