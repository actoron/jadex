package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$pojoagent")))
@Results(@Result(name="testcases", clazz=List.class))
@Service(IAService.class)
@Agent
public class AAgent implements IAService 
{
	@Agent
	protected IInternalAccess agent; 
	
	/**
	 *  Init service method.
	 */
	@AgentCreated
	public IFuture<Void> test()
	{
		boolean ext = !agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
		String reason = ext? "Wrong thread: "+Thread.currentThread(): null;
		List<TestReport> tests = new ArrayList<TestReport>();
		tests.add(new TestReport("#A1", "Test if service is called on component thread.", !ext, reason));
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testcases", tests);
		
//		System.out.println("called service");
		return IFuture.DONE;
	}
}
