package jadex.platform.service.cron;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  The cron agent is an agent based implementation
 *  of unix cron. Allows for specifying time pattern
 *  and a job. The time pattern will be checked every
 *  minute and the job will be executed if the pattern 
 *  matches. The pattern corresponds to the way unix
 *  cron patterns are defined:
 *  
 *  *    *    *            *     *
 *  Min  Hour Day of Month Month Mo-Fr   
 *  0-59 0-23 1-31         1-12  0-6
 *  
 *  Specification allows for:
 *  - wildcard: * (any value)
 *  - value: 1
 *  - values: 2,3,4
 *  - range: 1-5
 *  - steps: *\/5 (every 5)
 *  - combined patterns: p1|p2|p3 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ICronService.class, implementation=@Implementation(expression="$pojoagent")))
public class CronAgent<T> implements ICronService<T>
{
	//-------- attributes --------
	
	/** The cron jobs (id -> job). */
	protected Map<String, Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>>> jobs;
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IComponentStep<Void> check = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// check pattern
				long time = System.currentTimeMillis();
				
				if(jobs!=null)
				{
					Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>>[] cjs = (Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>>[])jobs.values()
						.toArray(new Tuple2[jobs.size()]);
					for(final Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>> tup: cjs)
					{
						if(tup.getFirstEntity().getFilter().filter(time))
						{
							// schedule job on subagent?!
							tup.getFirstEntity().getCommand().execute(new Tuple2<IInternalAccess, Long>(agent, new Long(time)))
								.addResultListener(new IResultListener<T>()
							{
								public void resultAvailable(T result)
								{
									tup.getSecondEntity().addIntermediateResultIfUndone(result);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									// or ignore?
									tup.getSecondEntity().setExceptionIfUndone(exception);
								}
							});
						}
					}
				}
				
				// wait
				long cur = System.currentTimeMillis();
				long min = ((cur/60000)+1)*60000; // next minute
				long sleep = (min - cur);
				if(sleep>0)
				{
					agent.waitFor(sleep, this);
				}
				else
				{
					agent.scheduleStep(this);
				}
				
				return IFuture.DONE;
			}
		};
		
		agent.scheduleStep(check);
	}
	
	/**
	 *  Add a schedule job.
	 *  @param job The cron job.
	 */
	public ISubscriptionIntermediateFuture<T> addJob(final CronJob<T> job)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(job.getFilter()==null || job.getCommand()==null)
		{
			ret.setException(new IllegalArgumentException("Job filter and command must not null: "+job));
		}
		else
		{
			if(jobs==null)
			{
				jobs = new LinkedHashMap<String, Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>>>();
			}
			jobs.put(job.getId(), new Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>>(job, ret));
		
			ret.setTerminationCommand(new TerminationCommand()
			{
				public void terminated(Exception reason)
				{
					jobs.remove(job.getId());
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a schedule job.
	 *  @param jobid The job id.
	 */
	public IFuture<Void> removeJob(String jobid)
	{
		if(jobs!=null)
		{
			Tuple2<CronJob<T>, SubscriptionIntermediateFuture<T>> tup = jobs.remove(jobid);
			tup.getSecondEntity().setFinishedIfUndone();
		}
		
		return IFuture.DONE;
	}
}
