package jadex.micro.testcases.proservicedetect;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that implements a service and uses it automatically as
 *  provided service (without declaration).
 */
@Agent(autoprovide=true)
@Service
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
//@ProvidedServices(@ProvidedService(type=ITestService.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestAgent implements ITestService
{
	/** 
	 * The agent body.
	 */
	@AgentBody
	public void body(MicroAgent agent)
	{
		// test if agent has the provided service
		TestReport tr = new TestReport("#1", "Test if provided service is present.");
		IService iser = agent.getServiceContainer().getProvidedService(ITestService.class);
		if(iser==null)
		{
			tr.setFailed("Auto provided service not found.");
		}
		else
		{
			tr.setSucceeded(true);
		}
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killAgent();
	}
	
	/**
	 *  A test method.
	 */
	public IFuture<Void> testMethod()
	{
		return IFuture.DONE;
	}
}
