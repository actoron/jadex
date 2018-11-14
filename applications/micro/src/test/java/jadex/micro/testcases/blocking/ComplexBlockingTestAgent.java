package jadex.micro.testcases.blocking;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.Boolean3;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Calls the step service twice, which in turn calls the block service.
 *  Intermediate results of the step service calls should come interleaved despite
 *  the step component being blocked.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
@ComponentTypes({
	@ComponentType(name="block", filename="jadex/micro/testcases/blocking/BlockAgent.class"),
	@ComponentType(name="step", filename="jadex/micro/testcases/blocking/StepAgent.class")
})
@Configurations(@Configuration(name="default", components={
	@Component(type="block"),
	@Component(type="step")
}))
public class ComplexBlockingTestAgent extends JunitAgentTest
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
		IStepService	step	= agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IStepService.class)).get();
		
//		System.out.println("Calling perform steps: "+agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
		IIntermediateFuture<Integer>	first	= step.performSteps(3, 1000);
		agent.getFeature(IExecutionFeature.class).waitForDelay(500).get();
//		System.out.println("Calling perform steps: "+agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
		IIntermediateFuture<Integer>	second	= step.performSteps(3, 1000);

		final List<Integer>	steps	= new ArrayList<Integer>();
		IIntermediateResultListener<Integer>	lis	= new IntermediateDefaultResultListener<Integer>()
		{
			public void intermediateResultAvailable(Integer result)
			{
				steps.add(result);
			}
		};
		first.addResultListener(lis);
		second.addResultListener(lis);
		
		first.get();
		second.get();
		
		if("[1, 1, 2, 2, 3, 3]".equals(steps.toString()))
		{
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
				new TestReport[]{new TestReport("#1", "Test interleaved blocking.", true, null)}));
		}
		else
		{
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
				new TestReport[]{new TestReport("#1", "Test interleaved blocking.", false, "Wrong steps: "+steps)}));
		}
	}
//	
//	@Override
//	public IPlatformConfiguration getConfig()
//	{
//		IPlatformConfiguration	ret	= super.getConfig();
//		ret.setLogging(true);
//		return ret;
//	}
}
