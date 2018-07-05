package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;

/**
 *  Behavior of the component result test.
 */
public class EndStepTestStep implements IComponentStep<Void>
{
	/**
	 *  Execute the test.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		return ia.getFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1,
					new TestReport[]{new TestReport("#1", "Test if end step is executed", true, null)}));
				return IFuture.DONE;
			}
		});
	}
}
