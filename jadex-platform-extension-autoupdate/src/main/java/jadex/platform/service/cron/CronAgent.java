package jadex.platform.service.cron;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cron.CronJob;
import jadex.bridge.service.types.cron.ICronService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
public class CronAgent implements ICronService
{
	//-------- attributes --------
	
	/** The cron jobs (id -> job). */
	protected Map<String, CronJob> jobs;
	
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
					CronJob[] cjs = (CronJob[])jobs.values().toArray(new CronJob[jobs.size()]);
					for(CronJob cj: cjs)
					{
						if(cj.getFilter().filter(time))
						{
							// schedule job on subagent?!
							cj.getCommand().execute(new Tuple2<IInternalAccess, Long>(agent, new Long(time)));
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
	public IFuture<Void> addJob(CronJob job)
	{
		if(job.getFilter()==null || job.getCommand()==null)
			return new Future<Void>(new IllegalArgumentException("Job filter and command must not null: "+job));
		
		if(jobs==null)
		{
			jobs = new LinkedHashMap<String, CronJob>();
		}
		jobs.put(job.getId(), job);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a schedule job.
	 *  @param jobid The job id.
	 */
	public IFuture<Void> removeJob(String jobid)
	{
		if(jobs!=null)
		{
			jobs.remove(jobid);
		}
		
		return IFuture.DONE;
	}
}
