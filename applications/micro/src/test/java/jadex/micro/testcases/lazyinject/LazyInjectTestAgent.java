package jadex.micro.testcases.lazyinject;

import java.util.ArrayList;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalIntermediateFinishedListener;
import jadex.commons.future.IFunctionalIntermediateResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
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
	@ComponentType(name="provider", filename="ProviderAgent.class"),
})
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class),
})
@Configurations(@Configuration(name="default", components=@Component(type="provider")))

@Results(@Result(name="testresults", clazz=Testcase.class))
// Scope global causes search timeouts -> increase test timeout to exceed search timeout
// Hangs with jadex_deftimeout -1 when incompatible platforms are online, because global search does not return and creation binding never happens :(
@Properties(
	@NameValue(name="test.timeout", value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 1.5)"))
public class LazyInjectTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	@AgentServiceSearch(lazy=true)
	protected ITestService ts;
	
	protected List<TestReport>	reports	= new ArrayList<TestReport>();
	protected Testcase tc	= new Testcase(4);

	/**
	 *
	 */
	@AgentBody
	public void body()
	{
		intermediateFutureTest();
		tuple2FutureTest();
	}

	private void intermediateFutureTest() 
	{
		IIntermediateFuture<String> fut = ts.getIntermediateResults();

		System.out.println("If test fails after this line, lazy delegation is broken");
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");
		reports.add(tr1);

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
		reports.add(tr2);

		fut.addIntermediateResultListener(new IFunctionalIntermediateResultListener<String>() 
		{
			@Override
			public void intermediateResultAvailable(String result) 
			{
				System.out.println("first: " + result);
				if ("hello".equals(result)) 
				{
					tr2.setSucceeded(true);
				} 
				else 
				{
					tr2.setFailed("Received wrong results.");
				}
				checkFinished();
			}
		}, new IFunctionalIntermediateFinishedListener<Void>() 
		{
			@Override
			public void finished() 
			{
				// should not happen as finish is never called
				tr2.setFailed(new Exception("finish unexpected"));
				checkFinished();
			}
		}, new IFunctionalExceptionListener() 
		{
			@Override
			public void exceptionOccurred(Exception exception) {
				System.out.println("ex: "+exception);
				tr2.setFailed(exception);
				checkFinished();
			}
		});

	}

	private void tuple2FutureTest() 
	{
		ITuple2Future<String, Integer> fut = ts.getFirstTupleResult();

		System.out.println("If test fails after this line, lazy delegation is broken");
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");
		reports.add(tr1);

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
		reports.add(tr2);

		fut.addResultListener(new DefaultTuple2ResultListener<String, Integer>()
		{
			public void firstResultAvailable(String result)
			{
				System.out.println("first: "+result);
				if("hello".equals(result)) 
				{
					tr2.setSucceeded(true);
				}
				else
				{
					tr2.setFailed("Received wrong results.");
				}
				checkFinished();
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
				
				checkFinished();
			}
		});
	}
	
	protected void	checkFinished()
	{
		boolean	finished = reports.size()==tc.getTestCount();
		for(TestReport report: reports)
		{
			finished = finished && report.isFinished();
		}

		if(finished)
		{
			tc.setReports(reports.toArray(new TestReport[reports.size()]));
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
			agent.killComponent();
		}
	}
	
	/**
	 *  Starter for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		// Start platform with agent.
		IPlatformConfiguration	config1	= PlatformConfigurationHandler.getMinimal();
		config1.getExtendedPlatformConfiguration().setSecurity(true);
		config1.getExtendedPlatformConfiguration().setTcpTransport(true);
//		config1.addComponent(UserAgent.class);
		for (int i = 0; i < 2000; ++i)
		{
			IExternalAccess plat = Starter.createPlatform(config1).get();
			plat.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ia.createComponent(null, new CreationInfo().setFilename(LazyInjectTestAgent.class.getCanonicalName() + ".class")).getSecondResult();
					System.out.println("Step done.");
					return IFuture.DONE;
				}
			}).get();
			plat.killComponent().get();
		}
		System.out.println("Done.");
		System.exit(0);
	}
}
