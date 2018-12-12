package jadex.platform.service.cron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentServiceSearch;
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
 *  Agent that tests the cron service.
 */
@RequiredServices(
{
	@RequiredService(name="clock", type=IClockService.class),
	@RequiredService(name="crons", type=ICronService.class),
})
@ComponentTypes(@ComponentType(name="cronagent", clazz=CronAgent.class))
@Configurations(@Configuration(name="default", components=@Component(type="wrapagent", configuration="platform clock")))

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The library service. */
	@AgentServiceSearch
	protected IClockService clock;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final String[] patterns = new String[]
		{
			"* * * * *",
			"*/5 * * * *",
			"*/5 * * * *|*/3 * * * *",
			"3-18/5 * * * *",
			"*/20 */2 * * *",
			"*/45 9-12 * * *",
			"0,45 0,3,6,9,12,15,18,21 * * *|30 1,4,7,10,13,16,19,22  * * *|15 2,5,8,11,14,17,20,23 * * *",
			"*/30 12 1-3,15,20-22 * *",
			"55 7 * * 1,2,3,4,5",
			"55 7 * * 1-5",
			"0 * * * *",
			"0 0 * * *",
			"0 0 * * 0",
			"0 0 1 * *",
			"55 7 * * 1-5",
			"55 7 * * 1,2,3,4,5",
			"55 7 * * 1-5",
			"0 * * * *",
			"0 0 * * *",
			"0 0 * * 0",
			"0 0 1 * *",
			"0 0 1 1 *"
		};
		
		testPatterns(patterns).addResultListener(new IIntermediateResultListener<TestReport>()
		{
			protected List<TestReport> trs = new ArrayList<TestReport>();
			
			public void intermediateResultAvailable(TestReport result)
			{
				trs.add(result);
				if(trs.size()==patterns.length)
				{
					finished();
				}
			}
			
			public void finished()
			{
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.size(), trs.toArray(new TestReport[trs.size()])));
				ret.setResult(null);
			}
			
			public void resultAvailable(Collection<TestReport> result)
			{
				trs = new ArrayList<TestReport>(result);
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test pattern.
	 */
	protected IIntermediateFuture<TestReport> testPatterns(final String[] patterns)
	{
		final IntermediateFuture<TestReport> ret = new IntermediateFuture<TestReport>();
		
		IFuture<ICronService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("crons");
		fut.addResultListener(new IResultListener<ICronService>()
		{
			public void resultAvailable(final ICronService crons)
			{
				for(int i=0; i<patterns.length; i++)
				{
					testPattern(patterns[i], crons, i).addResultListener(new IResultListener<TestReport>()
					{
						public void resultAvailable(TestReport result)
						{
							ret.addIntermediateResultIfUndone(result);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setExceptionIfUndone(exception);
						}
					});
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test pattern.
	 */
	protected IFuture<TestReport> testPattern(final String pattern, final ICronService crons, int i)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+i, "Test pattern: "+pattern);
		
//		final CronJob<Long> job = new CronJob<Long>(pattern, new TimePatternFilter(pattern), 
//			new IResultCommand<IFuture<Long>, Tuple2<IInternalAccess, Long>>()
//		{
//			public IFuture<Long> execute(Tuple2<IInternalAccess, Long> args)
//			{
//				return new Future<Long>(args.getSecondEntity());
//			}
//		});
		
		final CronJob<Long> job = new CronJob<Long>(pattern, new TimePatternFilter(pattern), 
			new IResultCommand<ISubscriptionIntermediateFuture<Long>, Tuple2<IInternalAccess, Long>>()
		{
			public ISubscriptionIntermediateFuture<Long> execute(Tuple2<IInternalAccess, Long> args)
			{
				SubscriptionIntermediateFuture<Long> ret = new SubscriptionIntermediateFuture<Long>();
				ret.addIntermediateResult(args.getSecondEntity());
				ret.setFinished();
				return ret;
			}
		});
		
		JobListener jl = new JobListener(pattern, tr, new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(final TestReport rep)
			{
				crons.removeJob(job.getId()).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setResult(rep);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Could not remove cron job: "+job);
						ret.setResult(rep);
					}
				});
			}
		});	

		crons.addJob(job).addResultListener(jl);
		
//		crons.addJob(job).addResultListener(new IIntermediateResultListener<Long>()
//		{
//			public void intermediateResultAvailable(Long result)
//			{
//				System.out.println("im: "+result);
//			}
//			
//			public void finished()
//			{
//				System.out.println("fin");
//			}
//			
//			public void resultAvailable(Collection<Long> result)
//			{
//				System.out.println("result: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Simulate time progress and record matches.
	 */
	public static long[] simulate(String pattern, long start, long end)
	{
//		System.out.println("Testing pattern: "+pattern);
		TimePatternFilter tp = new TimePatternFilter(pattern);

		int num = 10;
		long[] ret = new long[num];
				
		long cur = start;
		// find num (10) matches
		for(int i=0; i<num; i++)
		{
			cur = tp.getNextTimepoint(cur, end);
//			System.out.println(i+": "+SUtil.SDF.format(cur));
			ret[i] = cur;
			cur+=60000; // avoid to find the date more than once
		}
		
//		System.out.println(SUtil.arrayToString(ret));
		
		return ret;
	}

	/**
	 * 
	 */
	public static class JobListener implements IIntermediateResultListener<Long>
	{
		protected String pattern;
		
		protected long[] actual = new long[10];
		
		protected int cnt = 0;
		
		protected IResultListener<TestReport> delegate;
		
		protected TestReport report;
		
		/**
		 * 
		 */
		public JobListener(String pattern, TestReport report, IResultListener<TestReport> delegate)
		{
			this.pattern = pattern;
			this.delegate = delegate;
			this.report = report;
		}
		
		/**
		 * 
		 */
		public void intermediateResultAvailable(Long result)
		{
//			System.out.println("triggered at: "+SUtil.SDF.format(new Date(result.longValue())));
			actual[cnt++] = result;
			
			if(cnt==10)
			{
				// test if values match
				long start = actual[0];
				long end = start+1000L*60*60*24*365*15; // 15 years
				long[] expected = simulate(pattern, start, end);
				if(!Arrays.equals(actual, expected))
				{
					System.out.println("actual: "+SUtil.arrayToString(actual));
					System.out.println("expected: "+SUtil.arrayToString(expected));
					report.setFailed("actual: "+SUtil.arrayToString(actual)+" expected: "+SUtil.arrayToString(expected));
				}
				else
				{
					report.setSucceeded(true);
				}
				delegate.resultAvailable(report);
			}
		}
		
		/**
		 * 
		 */
		public void resultAvailable(Collection<Long> result)
		{
		}
		
		/**
		 * 
		 */
		public void finished()
		{
		}
		
		/**
		 * 
		 */
		public void exceptionOccurred(Exception exception)
		{
			exception.printStackTrace();
		}
	};
}