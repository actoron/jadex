package jadex.backup.job;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class JobService implements IJobService
{
	//-------- attributes --------

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The map of jobs (id -> job). */
	protected Map<String, Job> jobs;
	
	/** The futures of active subscribers. */
	protected Set<SubscriptionIntermediateFuture<JobEvent>> subscribers;

	//-------- constructors --------
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		if(subscribers!=null)
		{
			for(SubscriptionIntermediateFuture<JobEvent> fut: subscribers)
			{
				fut.terminate();
			}
		}
		
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new job.
	 *  @param job The job.
	 */
	public IFuture<Void> addJob(Job job)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(jobs==null)
		{
			this.jobs = new LinkedHashMap<String, Job>();
		}
		
		jobs.put(job.getId(), job);
		if(job instanceof SyncJob)
		{
			final SyncJob sjob = (SyncJob)job;
			IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
			fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("dir", sjob.getLocalResource());
					args.put("id", sjob.getGlobalResource());
					CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
					ci.setArguments(args);
					cms.createComponent(null, "sa", ci, null).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
					{
						public void customResultAvailable(IComponentIdentifier cid) 
						{
							System.out.println("created job agent: "+cid);
							
							publishEvent(new JobEvent(JobEvent.JOB_ADDED, sjob));
							
							ret.setResult(null);
						}
					});
				}
			});
		}
			
		return ret;
	}
	
	/**
	 *  Remove a job.
	 *  @param jobid The job id.
	 */
	public IFuture<Void> removeJob(String jobid)
	{
		// todo: kill agent
		
		Job job = jobs.remove(jobid);
		
		if(job!=null)
		{
			publishEvent(new JobEvent(JobEvent.JOB_REMOVED, job));
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe for job news.
	 */
	public ISubscriptionIntermediateFuture<JobEvent> subscribe()
	{
		if(subscribers==null)
		{
			subscribers	= new LinkedHashSet<SubscriptionIntermediateFuture<JobEvent>>();
		}
		
		SubscriptionIntermediateFuture<JobEvent> ret	= new SubscriptionIntermediateFuture<JobEvent>();
		subscribers.add(ret);
		
		return ret;		
	}
	
	/**
	 *  Get all jobs. 
	 *  @return All jobs.
	 */
	public IIntermediateFuture<Job> getJobs()
	{
		final IntermediateFuture<Job> ret = new IntermediateFuture<Job>();
		ret.setResult(jobs.values());
		return ret;
	}
	
	/**
	 *  Publish an event to all subscribers.
	 *  @param event The event.
	 */
	protected void publishEvent(JobEvent event)
	{
		if(subscribers!=null)
		{
			for(SubscriptionIntermediateFuture<JobEvent> sub: subscribers)
			{
				sub.addIntermediateResult(event);
			}
		}
	}
}
