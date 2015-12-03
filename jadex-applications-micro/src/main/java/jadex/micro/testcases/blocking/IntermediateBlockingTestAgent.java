package jadex.micro.testcases.blocking;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
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
 *  Calls the step service and waits for the intermediate results twice.
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
public class IntermediateBlockingTestAgent
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
		IStepService	step	= agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IStepService.class).get();
		
		final IIntermediateFuture<Integer>	fut	= step.performSteps(3, 1000);

		final List<Integer>	steps1	= new ArrayList<Integer>();
		final List<Integer>	steps2	= new ArrayList<Integer>();
		final List<Integer>	stepsall	= new ArrayList<Integer>();
		
		IFuture<Void>	step1	= agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				while(fut.hasNextIntermediateResult())
				{
					Integer	res	= fut.getNextIntermediateResult();
					steps1.add(res);
					stepsall.add(res);
				}
				return IFuture.DONE;
			}
		});
		IFuture<Void>	step2	= agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				try
				{
					while(true)
					{
						Integer	res	= fut.getNextIntermediateResult();
						steps2.add(res);
						stepsall.add(res);
					}
				}
				catch(NoSuchElementException e)
				{
					// got all results
				}
				
				return IFuture.DONE;
			}
		});
		
		step1.get();
		step2.get();
		
		if("[1, 2, 3]".equals(steps1.toString())
			&& "[1, 2, 3]".equals(steps2.toString())
			&& "[1, 1, 2, 2, 3, 3]".equals(stepsall.toString()))
		{
			agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
				new TestReport[]{new TestReport("#1", "Test intermediate blocking.", true, null)}));
		}
		else
		{
			agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
				new TestReport[]{new TestReport("#1", "Test intermediate blocking.", false, "Wrong steps: "+steps1+", "+steps2+", "+stepsall)}));
		}
	}
}
