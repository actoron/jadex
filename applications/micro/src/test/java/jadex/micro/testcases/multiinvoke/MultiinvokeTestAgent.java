package jadex.micro.testcases.multiinvoke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that uses a multiplexed service.
 */
@RequiredServices(@RequiredService(name="ms", type=IExampleService.class, multiple=true))
//	multiplextype=IMultiplexExampleService.class))	// TODO? removed in v4
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
@ComponentTypes(@ComponentType(name="provider", filename="ProviderAgent.class"))
@Configurations(@Configuration(name="def", components=@Component(type="provider", number="5")))
public class MultiinvokeTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IMultiplexExampleService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("ms");
		fut.addResultListener(new ExceptionDelegationResultListener<IMultiplexExampleService, Void>(ret)
		{
			public void customResultAvailable(IMultiplexExampleService ser)
			{
				final int cmpcnt = 5;
				final int rescnt = 5;
				final int testcnt = 10;
				final List<TestReport> reports = new ArrayList<TestReport>();
				
				CounterResultListener<Void> endlis = new CounterResultListener<Void>(testcnt, new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
//						System.out.println("countlis: "+agent.getComponentFeature(IExecutionFeature.class).isComponentThread());
						
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(testcnt, reports.toArray(new TestReport[reports.size()])));
						ret.setResult(null);
					}

					public void exceptionOccurred(Exception exception)
					{
						resultAvailable(null);
					}
				});
				
				// indirect intermediate future version
				
				TestReport tr = new TestReport("#1a", "Test indirect intermediate future version.");
				reports.add(tr);
				ser.getItem1().addResultListener(new CustomIntermediateResultListener<IFuture<String>>(tr, cmpcnt, endlis));
				tr = new TestReport("#1b", "Test indirect intermediate future version.");
				reports.add(tr);
				ser.getItems1(rescnt).addResultListener(new CustomIntermediateResultListener<IIntermediateFuture<String>>(tr, cmpcnt, endlis));
					
				// indirect future version
				
				tr = new TestReport("#2a", "Test indirect future version.");
				reports.add(tr);
				ser.getItem2().addResultListener(new CustomResultListener<Collection<IFuture<String>>>(tr, cmpcnt, endlis));
				tr = new TestReport("#2b", "Test indirect future version.");
				reports.add(tr);
				ser.getItems2(rescnt).addResultListener(new CustomResultListener<Collection<IIntermediateFuture<String>>>(tr, cmpcnt, endlis));

				// flattened intermediate future version
				
				tr = new TestReport("#3a", "Test flattened intermediate future version.");
				reports.add(tr);
				ser.getItem3().addResultListener(new CustomIntermediateResultListener<String>(tr, cmpcnt, endlis));
				tr = new TestReport("#3b", "Test flattened intermediate future version.");
				reports.add(tr);
				ser.getItems3(rescnt).addResultListener(new CustomIntermediateResultListener<String>(tr, cmpcnt*rescnt, endlis));

				// flattened future version

				tr = new TestReport("#4a", "Test flattened future version.");
				reports.add(tr);
				ser.getItem4().addResultListener(new CustomResultListener<Collection<String>>(tr, cmpcnt, endlis));
				tr = new TestReport("#4b", "Test flattened future version.");
				reports.add(tr);
				ser.getItems4(rescnt).addResultListener(new CustomResultListener<Collection<String>>(tr, cmpcnt*rescnt, endlis));

				// sequential multiplexer
			
				List<Object[]> tasks = new ArrayList<Object[]>();
				tasks.add(new Object[]{Integer.valueOf(1), Integer.valueOf(2)});
				tasks.add(new Object[]{Integer.valueOf(3), Integer.valueOf(4)});
				tasks.add(new Object[]{Integer.valueOf(5), Integer.valueOf(6)});
				
				tr = new TestReport("#5a", "Test sequential multuplexer.");
				reports.add(tr);
				ser.add(tasks).addResultListener(new CustomIntermediateResultListener<Integer>(tr, 3, endlis));
			
				tr = new TestReport("#5b", "Test sequential multuplexer with collector.");
				reports.add(tr);
				ser.sum(tasks).addResultListener(new CustomResultListener<Integer>(tr, 1, endlis));

			}	
		});

		return ret;
	}
	
	/**
	 *  Custom intermediate listener.
	 */
	public class CustomIntermediateResultListener<T> implements IIntermediateResultListener<T>
	{
		protected int cnt = 0;
		protected int rescnt;
		protected TestReport tr;
		protected IResultListener<Void> endlis;
		
		public CustomIntermediateResultListener(TestReport tr, int rescnt, IResultListener<Void> endlis)
		{
			this.tr = tr;
			this.rescnt = rescnt;
			this.endlis = endlis;
		}
		
		public void intermediateResultAvailable(T result)
		{
			System.out.println("result: "+result);
			cnt++;
		}
		
		public void finished()
		{
			if(cnt==rescnt)
				tr.setSucceeded(true);
			else
				tr.setReason("Wrong number of results: "+cnt);
			endlis.resultAvailable(null);
		}
		
		public void resultAvailable(Collection<T> result)
		{
			if(result.size()==rescnt)
				tr.setSucceeded(true);
			else
				tr.setReason("Wrong number of results: "+result.size());
			endlis.resultAvailable(null);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			tr.setFailed(exception);
			endlis.resultAvailable(null);
		}
	}
	
	/**
	 *  Custom listener.
	 */
	public class CustomResultListener<T> implements IResultListener<T>
	{
		protected int cnt = 0;
		protected int rescnt;
		protected TestReport tr;
		protected IResultListener<Void> endlis;
		
		public CustomResultListener(TestReport tr, int rescnt, IResultListener<Void> endlis)
		{
			this.tr = tr;
			this.rescnt = rescnt;
			this.endlis = endlis;
		}
		
		public void resultAvailable(T result)
		{
			System.out.println("result: "+result);

			if(result instanceof Collection && ((Collection<?>)result).size()==rescnt
				|| rescnt==1)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setReason("Wrong number of results: "+result);
			}
			endlis.resultAvailable(null);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			tr.setFailed(exception);
			endlis.resultAvailable(null);
		}
	}

//	/**
//	 *  Get a multi service.
//	 *  @param reqname The required service name.
//	 *  @param multitype The interface of the multi service.
//	 */
//	public <T> T getMultiService(String reqname, Class<T> multitype)
//	{
//		return (T)Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{multitype}, new MultiServiceInvocationHandler(agent, reqname, multitype));
//	}
	
}