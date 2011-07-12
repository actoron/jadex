package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.component.ComponentInterpreter;

/**
 *  Behavior of the component result test.
 */
public class EndStepTestStep implements IComponentStep
{
	/**
	 *  Execute the test.
	 */
	public Object execute(final IInternalAccess ia)
	{
		final Future	ret	= new Future();
		
		((ComponentInterpreter)ia).waitFor(2000, new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				((ComponentInterpreter)ia).setResultValue("testresults", new Testcase(1,
					new TestReport[]{new TestReport("#1", "Test if end step is executed", true, null)}));
				ret.setResult(null);
				return null;
			}
		});
		
		return ret;
	}
}
