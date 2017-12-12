package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if a pojo can be injected into a service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IAService.class))
@Service
@Description("Test if a pojo agent can be injected into a service as servicecomponent.")
@Results(@Result(name="testresults", clazz=Testcase.class))
public class PojoInjectionAgent extends JunitAgentTest implements IAService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	/** The service component. */
	@ServiceComponent
	protected PojoInjectionAgent pojo;
	
	@AgentBody
	public IFuture<Void> body()
	{
//		System.out.println("pojo is: "+pojo);
		TestReport	tr	= new TestReport("#1", "Test if a pojo agent can be injected into a service as servicecomponent.");
		if(pojo!=null)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Service component not set: "+pojo);
		}
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		
		return IFuture.DONE;
	}
	
	/**
	 *  Dummy test method.
	 */
	public IFuture<Void> test()
	{
		return IFuture.DONE;
	}
}
