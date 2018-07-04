package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Testing if required services can be equipped with interceptors.
 */
@Description("Testing if required services can be equipped with interceptors.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@ProvidedServices(@ProvidedService(name="aservice", type=IAService.class, implementation=
	@Implementation(expression="$pojoagent", interceptors=@Value("$pojoagent.provinter"))))
@RequiredServices(@RequiredService(name="aservice", type=IAService.class, 
	binding=@Binding(scope="local", interceptors=@Value("$pojoagent.reqinter"))))
@Service(IAService.class)
@Agent
public class InterceptorAgent extends JunitAgentTest implements IAService
{	
	@Agent
	protected IInternalAccess agent;
	
	public SimpleInterceptor provinter = new SimpleInterceptor();
	public SimpleInterceptor reqinter = new SimpleInterceptor();
	
	/**
	 *  Just finish the test by setting the result and killing the agent.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final List<TestReport> testresults = new ArrayList<TestReport>();
		performProvidedServiceTest(testresults).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				performRequiredServiceTest(testresults).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
//						System.out.println("testresults: "+testresults);
						TestReport[] tr = (TestReport[])testresults.toArray(new TestReport[testresults.size()]);
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tr.length, tr));
//						killAgent();
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform test for provided service.
	 */
	public IFuture<Void> performProvidedServiceTest(final List<TestReport> testresults)
	{
		final Future<Void> ret = new Future<Void>();
		IAService ser = (IAService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService("aservice");
		ser.test().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				TestReport tr = new TestReport("#1", "Provided service test.");
				if(provinter.getCnt()==1)
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setReason("Wrong interceptor count: "+provinter.getCnt());
				}
				testresults.add(tr);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Perform test for required service.
	 */
	public IFuture<Void> performRequiredServiceTest(final List<TestReport> testresults)
	{
		final Future<Void> ret = new Future<Void>();
		agent.getComponentFeature(IRequiredServicesFeature.class).getService("aservice").addResultListener(new DefaultResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				IAService ser = (IAService)result;
				ser.test().addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						TestReport tr = new TestReport("#2", "Required service test.");
						if(reqinter.getCnt()==1)
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setReason("Wrong interceptor count: "+reqinter.getCnt());
						}
						testresults.add(tr);
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Init service method.
	 */
	public IFuture<Void> test()
	{
//		System.out.println("called service");
		return IFuture.DONE;
	}
}

/**
 *  Simple interceptor that remembers how often it was called.
 */
class SimpleInterceptor implements IServiceInvocationInterceptor
{
	protected int interceptcnt;

	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		try
		{
			return context.getMethod().getName().equals("test");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext context)
	{
		interceptcnt++;
//		System.out.println("exe: "+interceptcnt);
		return context.invoke();
	}
	
	/**
	 *  Get the interceptor cnt.
	 */
	public int getCnt()
	{
		return interceptcnt;
	}
}