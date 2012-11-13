package jadex.platform.service.cron;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
@Arguments(
{
	@Argument(name="realtime", clazz=boolean.class, defaultvalue="true", description="Realtime means using system clock, otherwise Jadex clock is used (allows for simulation)"),
	@Argument(name="lookahead", clazz=long.class, defaultvalue="1000L*60*60*24*365*2", description="Maximum lookahead for the next timepoint in time patterns (default=2 years)")
})
@Service
@ProvidedServices(@ProvidedService(type=ICronService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Configurations(
{
	@Configuration(name="realtime clock"),
	@Configuration(name="platform clock", arguments=@NameValue(name="realtime", value="false"))
})
public class CronAgent implements ICronService
{
	//-------- attributes --------

	/** Argument if realtime should be used. */
	@AgentArgument
	protected boolean realtime;
	
	@AgentArgument
	protected long lookahead;
	
	/** The cron jobs (id -> job). */
	protected Map<String, Tuple2<CronJob<Object>, SubscriptionIntermediateFuture<Object>>> jobs;
	
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
		if(realtime)
		{
			IComponentStep<Void> check = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// check pattern
					long time = System.currentTimeMillis();
					
					if(jobs!=null)
					{
						Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>>[] cjs = (Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>>[])jobs.values()
							.toArray(new Tuple2[jobs.size()]);
						for(final Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>> tup: cjs)
						{
							if(tup.getFirstEntity().getFilter().filter(time))
							{
								// schedule job on subagent?!
								IFuture<Object> res = (IFuture<Object>)tup.getFirstEntity().getCommand().execute(new Tuple2<IInternalAccess, Long>(agent, new Long(time)));
								res.addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										((IntermediateFuture<Object>)tup.getSecondEntity()).addIntermediateResultIfUndone(result);
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
	}
	
	/**
	 *  Add a schedule job.
	 *  @param job The cron job.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addJob(final CronJob<T> job)
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
				jobs = new LinkedHashMap<String, Tuple2<CronJob<Object>, SubscriptionIntermediateFuture<Object>>>();
			}
			jobs.put(job.getId(), new Tuple2<CronJob<Object>, SubscriptionIntermediateFuture<Object>>
				((CronJob<Object>)job, (SubscriptionIntermediateFuture<Object>)ret));
		
			ret.setTerminationCommand(new TerminationCommand()
			{
				public void terminated(Exception reason)
				{
					jobs.remove(job.getId());
				}
			});
			
			// create check behavior and determine next trigger timepoint for job with lookahead
			if(!realtime)
			{
				IComponentStep<Void> check = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						// quitting jobs that have been removed
						if(jobs.containsKey(job.getId()))
						{
							final IComponentStep<Void> self = this;
							IFuture<IClockService> fut = agent.getRequiredService("clockser");
							fut.addResultListener(new DefaultResultListener<IClockService>()
							{
								public void resultAvailable(IClockService clockser)
								{
									long start = clockser.getTime();
									long end = start+lookahead;
									
									TimePatternFilter tpf = null;
									if(job.getFilter() instanceof TimePatternFilter)
									{
										tpf = (TimePatternFilter)job.getFilter();
									}
									else
									{
										tpf = new TimePatternFilter(job.getPattern());
									}
									
									try
									{
										long next = tpf.getNextTimepoint(start, end);
										long wait = next-start;
										System.out.println("waiting for: "+wait);
										agent.waitFor(wait, self);
									}
									catch(Exception e)
									{
										System.out.println("No next timepoint found for: "+tpf.getPattern()+" with lookahead: "+lookahead);
									}
								}
							});
						}
						
						return IFuture.DONE;
					}
				};
				agent.scheduleStep(check);
			}
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
			Tuple2<CronJob<Object>, SubscriptionIntermediateFuture<Object>> tup = jobs.remove(jobid);
			tup.getSecondEntity().setFinishedIfUndone();
		}
		
		return IFuture.DONE;
	}
}
