package jadex.platform.service.cron;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
	@Argument(name="lookahead", clazz=long.class, defaultvalue="1000L*60*60*24*365*2", description="Maximum lookahead for the next timepoint in time patterns (default=2 years)"),
	@Argument(name="useworkeragent", clazz=boolean.class, defaultvalue="true", description="Flag if a worker agent should be used to execute a cron job.")
})
@Service
@ProvidedServices(@ProvidedService(type=ICronService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class, scope=ServiceScope.PLATFORM))
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
	
	/** The agent lookahead. */
	@AgentArgument
	protected long lookahead;
	
	/** The agent lookahead. */
	@AgentArgument
	protected boolean useworkeragent;
	
	/** The cron jobs (id -> job). */
	protected Map<String, Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>>> jobs;
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
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
								executeJob(tup, time).addResultListener(new DefaultResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
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
						agent.getFeature(IExecutionFeature.class).waitForDelay(sleep, this);
					}
					else
					{
						agent.getFeature(IExecutionFeature.class).scheduleStep(this);
					}
					
					return IFuture.DONE;
				}
			};
			
			agent.getFeature(IExecutionFeature.class).scheduleStep(check);
		}
	}
	
	/**
	 *  Add a schedule job.
	 *  @param job The cron job.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addJob(final CronJob<T> job)
	{
//		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		final SubscriptionIntermediateFuture<T>	ret	= (SubscriptionIntermediateFuture<T>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

		if(job.getFilter()==null || job.getCommand()==null)
		{
			ret.setException(new IllegalArgumentException("Job filter and command must not null: "+job));
		}
		else
		{
			if(jobs==null)
			{
				jobs = new LinkedHashMap<String, Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>>>();
			}
			final Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>> jobtup = new Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>>
				((CronJob<?>)job, (SubscriptionIntermediateFuture<?>)ret);
			jobs.put(job.getId(), jobtup);
		
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
					long last = -1;
					public IFuture<Void> execute(IInternalAccess ia)
					{
						// quitting jobs that have been removed
						if(jobs.containsKey(job.getId()))
						{
							final IComponentStep<Void> self = this;
							IFuture<IClockService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("clockser");
							fut.addResultListener(new DefaultResultListener<IClockService>()
							{
								public void resultAvailable(IClockService clockser)
								{
									final long cur = clockser.getTime();

									// Execute job if pattern triggered
									if(last!=-1)
									{
										executeJob(jobtup, cur).addResultListener(new DefaultResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
												determineNext(cur);
											}
										});
									}
									else
									{
										determineNext(cur);
									}
								}
								
								protected void determineNext(long start)
								{
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
										if(next==last)
										{
											next = tpf.getNextTimepoint(start+60000, end);
										}
										last = next;
										long wait = next-start;
//										System.out.println("waiting for: "+wait);
										if(wait>0)
										{
											agent.getFeature(IExecutionFeature.class).waitForDelay(wait, self);
										}
										else
										{
											agent.getFeature(IExecutionFeature.class).scheduleStep(self);
										}
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
				agent.getFeature(IExecutionFeature.class).scheduleStep(check);
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
			Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>> tup = jobs.remove(jobid);
			tup.getSecondEntity().setFinishedIfUndone();
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Test if a job is scheduled with an id.
	 *  @param jobid The jobid.
	 */
	public IFuture<Boolean> containsJob(String jobid)
	{
		Future<Boolean> ret = new Future<Boolean>();
		ret.setResult(jobs!=null && jobs.containsKey(jobid)? Boolean.TRUE: Boolean.FALSE);
		return ret;
	}
	
	/**
	 *  Execute a job on worker agent or directly.
	 */
	protected IFuture<Void> executeJob(final Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>> jobtup, final long time)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(useworkeragent)
		{
			CreationInfo ci = new CreationInfo(agent.getId());
			ci.setFilename("jadex/platform/service/cron/WorkerAgent.class");
			agent.createComponent(ci)
//					cms.createComponent(null, "jadex/platform/service/cron/WorkerAgent.class", ci, null)
				.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess exta) 
				{
					// Set to finished before executing command to decouple from cron main task
					ret.setResult(null);
					
					exta.scheduleStep(new IComponentStep<Object>()
					{
						public IFuture<Object> execute(final IInternalAccess ia)
						{
							doExecuteCommand(jobtup, time).addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									ia.killComponent();
								}
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
								}
							}));
							
							return Future.getEmptyFuture();
						}
					});
				}
			}));
		}
		else
		{
			return doExecuteCommand(jobtup, time);
		}
		
		
		return ret;
	}
	
	/**
	 *  Execute the command of a job.
	 */
	protected IFuture<Void> doExecuteCommand(final Tuple2<CronJob<?>, SubscriptionIntermediateFuture<?>> jobtup, final long time)
	{
		final Future<Void> ret = new Future<Void>();
		
		ISubscriptionIntermediateFuture<Object> res = (ISubscriptionIntermediateFuture<Object>)jobtup.getFirstEntity().getCommand().execute(new Tuple2<IInternalAccess, Long>(agent, Long.valueOf(time)));
		res.addResultListener(new IIntermediateResultListener<Object>()
		{
			public void intermediateResultAvailable(Object result)
			{
				((IntermediateFuture<Object>)jobtup.getSecondEntity()).addIntermediateResultIfUndone(result);
			}
			
			public void finished()
			{
				ret.setResult(null);
			}
			
			public void resultAvailable(Collection<Object> result)
			{
				for(Object res: result)
				{
					intermediateResultAvailable(res);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				jobtup.getSecondEntity().setExceptionIfUndone(exception);
				ret.setResult(null);
			}
		});
		
//		res.addResultListener(new IResultListener<Object>()
//		{
//			public void resultAvailable(Object result)
//			{
//				((IntermediateFuture<Object>)jobtup.getSecondEntity()).addIntermediateResultIfUndone(result);
//				ret.setResult(null);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				// or ignore?
//				jobtup.getSecondEntity().setExceptionIfUndone(exception);
//				ret.setResult(null);
//			}
//		});
		
		return ret;
	}
}
