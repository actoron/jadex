package jadex.platform.service.servicepool;

import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
 *  User agent that first registers services A, B at the service pool (which is created if not present).
 *  Then searches for A, B and invokes the methods.
 *  The calls will be distributed among the instances of the pool.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="poolser", type=IServicePoolService.class),
	@RequiredService(name="aser", type=IAService.class),
	@RequiredService(name="bser", type=IBService.class)
})
@ComponentTypes(@ComponentType(name="spa", filename="jadex.platform.service.servicepool.ServicePoolAgent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="spa")))

@Results(@Result(name="testresults", clazz=Testcase.class))
public class UserAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		registerServices().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				searchServices().addResultListener(new ExceptionDelegationResultListener<Tuple2<IAService,IBService>, Void>(ret)
				{
					public void customResultAvailable(Tuple2<IAService, IBService> sers)
					{
						useServices(sers.getFirstEntity(), sers.getSecondEntity())
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
			
		return ret;
	}
	
	/**
	 *  Register services at the service pool service.
	 */
	public IFuture<Void> registerServices()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IServicePoolService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("poolser");
		fut.addResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
		{
			public void customResultAvailable(final IServicePoolService sps)
			{
//				if(!agent.getComponentIdentifier().equals(((IService)sps).getServiceIdentifier().getProviderId().getParent()))
//					System.out.println("gasjjjjjjjashjfha");
				
				sps.addServiceType(IAService.class, new DefaultPoolStrategy(5, 35000, 10), "jadex.platform.service.servicepool.example.AAgent.class")
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						sps.addServiceType(IBService.class, new DefaultPoolStrategy(3, 10), "jadex.platform.service.servicepool.example.BAgent.class")
							.addResultListener(new DelegationResultListener<Void>(ret));
					}	
				});
			}
		});
			
		return ret;
	}
	
	/**
	 *  Search the services.
	 */
	protected IFuture<Tuple2<IAService, IBService>> searchServices()
	{
		final Future<Tuple2<IAService, IBService>> ret = new Future<Tuple2<IAService, IBService>>();
		
		IFuture<IAService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("aser");
		fut.addResultListener(new ExceptionDelegationResultListener<IAService, Tuple2<IAService, IBService>>(ret)
		{
			public void customResultAvailable(final IAService aser)
			{				
				IFuture<IBService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("bser");
				fut.addResultListener(new ExceptionDelegationResultListener<IBService, Tuple2<IAService, IBService>>(ret)
				{
					public void customResultAvailable(final IBService bser)
					{
						ret.setResult(new Tuple2<IAService, IBService>(aser, bser));
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Use the services.
	 */
	protected IFuture<Void> useServices(final IAService aser, final IBService bser)
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport rep1 = new TestReport("#1", "Test invoking service A ma1");
		final int cnt1 = 100;
		CounterResultListener<String> lis1 = new CounterResultListener<String>(cnt1, new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result) 
			{
//				System.out.println("called "+cntma1+" times ma1");
				rep1.setSucceeded(true);
			
				final TestReport rep2 = new TestReport("#2", "Test invoking service A ma2");
				final int cnt2 = 10;
				CounterResultListener<Collection<Integer>> lis = new CounterResultListener<Collection<Integer>>(cnt2, new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result) 
					{
//						System.out.println("called "+cnt+" times ma2");
						rep2.setSucceeded(true);
						
						final TestReport rep3 = new TestReport("#3", "Test if no A services besides proxy can be found");
						// Ensure that only 
						agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IAService.class))
							.addResultListener(new ExceptionDelegationResultListener<Collection<IAService>, Void>(ret)
						{
							public void customResultAvailable(Collection<IAService> result)
							{
//								System.out.println("found: "+result.size());
								if(result.size()==1)
								{
									rep3.setSucceeded(true);
								}
								else
								{
									rep3.setReason("Found more than one A service: "+result.size());
								}
								
								final TestReport rep4 = new TestReport("#4", "Test invoking service B mb1");
								final int cnt4 = 1000;
								CounterResultListener<String> lis4 = new CounterResultListener<String>(cnt4, new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result) 
									{
//										System.out.println("called "+cnt4+" times ma1");
										rep4.setSucceeded(true);
										
										ServiceCall call = CallAccess.getOrCreateNextInvocation();
										call.setTimeout(33000);
										call.setProperty("myprop", "myval");
										
										aser.ma3(call.getProperties()).addResultListener(new IResultListener<TestReport>()
										{
											public void resultAvailable(TestReport rep5)
											{
//												System.err.println("FFFFFFFFFFFINI: "+agent.getComponentIdentifier());
												agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(5, new TestReport[]{rep1, rep2, rep3, rep4, rep5}));
												ret.setResult(null);
											}
											
											public void exceptionOccurred(Exception exception)
											{
												TestReport rep5 = new TestReport("#5", "Test non-func props");
												rep5.setFailed(exception);
												agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(5, new TestReport[]{rep1, rep2, rep3, rep4, rep5}));
												ret.setResult(null);
											}
										});
									}
								});
								for(int i=0; i<cnt4; i++)
								{
									bser.mb1("hello "+i).addResultListener(lis4);
								}
							}
						});
					}
				});
				for(int i=0; i<cnt2; i++)
				{
					ServiceCall call = CallAccess.getOrCreateNextInvocation();
					call.setTimeout(32000);
					call.setProperty("myprop", "myval");

					aser.ma2().addResultListener(lis);
				}
			}
		});
		
		for(int i=0; i<cnt1; i++)
		{
			ServiceCall call = CallAccess.getOrCreateNextInvocation();
			call.setTimeout(31000);
			call.setProperty("myprop", "myval");

			aser.ma1("hello "+i).addResultListener(lis1);
		}
		
		return ret;
	}
}
