package jadex.platform.service.cron;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;
import java.util.Date;

/**
 *  Agent that tests the cron service.
 */
@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="crons", type=ICronService.class, 
		binding=@Binding(create=true, creationinfo=@CreationInfo(type="cronagent", configuration="platform clock"))),
})
@ComponentTypes(@ComponentType(name="cronagent", filename="jadex/platform/service/cron/CronAgent.class"))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport[] trs = new TestReport[1];
		
		testPattern().addResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport tr)
			{
				trs[0] = tr;
				agent.setResultValue("testresults", new Testcase(1, trs));
				ret.setResult(null);
			}
		});
	
		return ret;
	}
	
	/**
	 *  Test pattern.
	 */
	protected IFuture<TestReport> testPattern()
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#1", "Test if cron every minute works.");
		
		IFuture<ICronService> fut = agent.getServiceContainer().getRequiredService("crons");
		fut.addResultListener(new ExceptionDelegationResultListener<ICronService, TestReport>(ret)
		{
			public void customResultAvailable(final ICronService crons)
			{
				String pattern = "* * * * *";
				crons.addJob(new CronJob<Long>(pattern, new TimePatternFilter(pattern), 
					new IResultCommand<IFuture<Long>, Tuple2<IInternalAccess, Long>>()
				{
					public IFuture<Long> execute(Tuple2<IInternalAccess, Long> args)
					{
		//				System.out.println("triggered at: "+SUtil.SDF.format(new Date(args.getSecondEntity().longValue())));
						
						// return trigger time
						return new Future<Long>(args.getSecondEntity());
					}
				})).addResultListener(new IIntermediateResultListener<Long>()
				{
					public void intermediateResultAvailable(Long result)
					{
						System.out.println("triggered at: "+SUtil.SDF.format(new Date(result.longValue())));
					}
					
					public void resultAvailable(Collection<Long> result)
					{
					}
					
					public void finished()
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
		});
		
//		ret.setResult(tr);
		
		return ret;
	}
	
}