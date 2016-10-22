package jadex.micro.testcases.lazyinject;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalIntermediateFinishedListener;
import jadex.commons.future.IFunctionalIntermediateResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * Tests whether lazy service calls work together with tuple2 / intermediatefutures.
 */
@Agent
@ComponentTypes({
	@ComponentType(name="provider", filename="jadex.micro.testcases.lazyinject.ProviderAgent.class"),
})
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class,
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_COMPONENT, create=true, creationinfo=@CreationInfo(type="provider"))),
})
@Results(@Result(name="testresults", clazz=Testcase.class))
// Scope global causes search timeouts -> increase test timeout to exceed search timeout
// Hangs with jadex_deftimeout -1 when incompatible platforms are online, because global search does not return and creation binding never happens :(
@Properties(
	@NameValue(name="test.timeout", value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 1.5)"))
public class UserAgent
{
	@Agent
	protected IInternalAccess agent;

	@AgentService(lazy=true)
	protected ITestService ts;

	/**
	 *
	 */
	@AgentBody
	public void body()
	{
		testIntermediateFuture();
		testTuple2Future();
	}

	private void testIntermediateFuture() {
		IIntermediateFuture<String> fut = ts.getIntermediateResults();

		System.out.println("If test fails after this line, lazy delegation is broken");
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");

		String res1 = fut.getNextIntermediateResult(); // if broken, this hangs

		System.out.println("first result: "+res1);

		if("hello".equals(res1))
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Received wrong results: "+res1+" ");
		}

		final TestReport tr2 = new TestReport("#2", "Test if functional listener works.");

		fut.addIntermediateResultListener(new IFunctionalIntermediateResultListener<String>() {
			@Override
			public void intermediateResultAvailable(String result) {

				System.out.println("first: " + result);
				if ("hello".equals(result)) {
					tr2.setSucceeded(true);
					agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
					agent.killComponent();
				} else {
					tr2.setFailed("Received wrong results.");
				}
			}
		}, new IFunctionalIntermediateFinishedListener<Void>() {
			@Override
			public void finished() {
				// should not happen as finish is never called
				tr2.setFailed(new Exception("finish unexpected"));

				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
				agent.killComponent();
			}
		}, new IFunctionalExceptionListener() {
			@Override
			public void exceptionOccurred(Exception exception) {
				System.out.println("ex: "+exception);
				tr2.setFailed(exception);

				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
				agent.killComponent();
			}
		});

	}

	private void testTuple2Future() {
		ITuple2Future<String, Integer> fut = ts.getFirstTupleResult();

		System.out.println("If test fails after this line, lazy delegation is broken");
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");

		String res1 = fut.getFirstResult(); // if broken, this hangs

		System.out.println("first result: "+res1);

		if("hello".equals(res1))
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Received wrong results: "+res1+" ");
		}

		final TestReport tr2 = new TestReport("#2", "Test if default tuple2 listener works.");

		fut.addResultListener(new DefaultTuple2ResultListener<String, Integer>()
		{
			boolean res =false;
			public void firstResultAvailable(String result)
			{
				System.out.println("first: "+result);
				if("hello".equals(result)) {
					tr2.setSucceeded(true);
					agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
					agent.killComponent();
				}
				else
				{
					tr2.setFailed("Received wrong results.");
				}
			}

			public void secondResultAvailable(Integer result)
			{
				// should not happen as finish is never called
				exceptionOccurred(new Exception("second result unexpected"));
			}

			public void finished()
			{
				// should not happen as finish is never called
				exceptionOccurred(new Exception("finish unexpected"));
			}

			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				tr2.setFailed(exception);

				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
				agent.killComponent();
			}
		});
	}
}
