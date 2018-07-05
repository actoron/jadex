package jadex.micro.testcases.tuplefuture;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
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
 * 
 */
@Agent
@ComponentTypes({
	@ComponentType(name="ta", filename="jadex.micro.testcases.tuplefuture.ProviderAgent.class"),
	@ComponentType(name="ta2", filename="jadex.micro.testcases.tuplefuture.Provider2Agent.class")
})
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class)
		// Hangs with global search during micro tests?
//		binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, create=true, creationinfo=@CreationInfo(type="ta"))),
})
@Configurations(@Configuration(name="default", components=@Component(type="ts")))
@Results(@Result(name="testresults", clazz=Testcase.class))
// Scope global causes search timeouts -> increase test timeout to exceed search timeout
// Hangs with jadex_deftimeout -1 when incompatible platforms are online, because global search does not return and creation binding never happens :(
@Properties(
	@NameValue(name="test.timeout", value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 1.5)"))
public class TupleFutureTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		ITestService ts = (ITestService)agent.getFeature(IRequiredServicesFeature.class).getService("ts").get();
		
		ITuple2Future<String, Integer> fut = ts.getSomeResults();
		
		final TestReport tr1 = new TestReport("#1", "Test if blocking get works.");
		
		String res1 = fut.getFirstResult();
		Integer res2 = fut.getSecondResult();
		
		System.out.println("first result: "+res1);
		System.out.println("second result: "+res2);
		
		if("hello".equals(res1) && res2.intValue()==99)
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Received wrong results: "+res1+" "+res2);
		}
		
		final TestReport tr2 = new TestReport("#2", "Test if default tuple2 listener works.");
		
		fut.addResultListener(new DefaultTuple2ResultListener<String, Integer>()
		{
			boolean[] res = new boolean[2];
			public void firstResultAvailable(String result)
			{
				System.out.println("first: "+result);
				if("hello".equals(result))
					res[0] = true;
			}
			
			public void secondResultAvailable(Integer result)
			{
				System.out.println("second: "+result);
				if(result!=null && result.intValue()==99)
					res[1] = true;
			}
			
			public void finished()
			{
				System.out.println("finished: ");
				if(res[0] && res[1])
				{
					tr2.setSucceeded(true);
				}
				else
				{
					tr2.setFailed("Received wrong results.");
				}
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
				agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				tr2.setFailed(exception);
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
				agent.killComponent();
			}
		});
		
//		fut.addResultListener(new IIntermediateResultListener<TupleResult>()
//		{
//			public void intermediateResultAvailable(TupleResult result)
//			{
//				System.out.println("ira: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//			
//			public void finished()
//			{
//				System.out.println("fini");
//			}
//			
//			public void resultAvailable(Collection<TupleResult> result)
//			{
//				System.out.println("ra: "+result);
//			}
//		});
		
//		ts.getSomeResults().addResultListener(new IResultListener<Collection<String>>()
//		{
//			public void resultAvailable(Collection<String> result)
//			{
//				System.out.println("result: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
	}
}
