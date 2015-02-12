package jadex.backup.job.management;

import jadex.backup.JadexBackup;
import jadex.backup.job.Job;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Service for handling the jobs, i.e. adding and removing jobs.
 */
@Service
public class JobManagementService implements IJobManagementService
{
	//-------- attributes --------

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The map of jobs (id -> job). */
	protected Map<String, Job> jobs;
	
	/** The job agents. */
	protected Map<String, IExternalAccess> jobagents;
	
	/** The futures of active subscribers. */
	protected Set<SubscriptionIntermediateFuture<JobManagementEvent>> subscribers;
	
	/** The configfile. */
	protected String cfgfile;

	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		
		this.jobs = new LinkedHashMap<String, Job>();
		this.jobagents = new HashMap<String, IExternalAccess>();

		String[] cmdargs = (String[])agent.getComponentFeature(IArgumentsFeature.class).getArguments().get("cmdargs");
		if(cmdargs!=null)
		{
			for(int i=0; i<cmdargs.length; i++)
			{
				if(JadexBackup.CFG_FILE.equals(cmdargs[i]))
				{
					cfgfile = cmdargs[++i];
				}
			}
		}
		loadSettings(cfgfile).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		if(subscribers!=null)
		{
			for(SubscriptionIntermediateFuture<JobManagementEvent> fut: subscribers)
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
	public IFuture<Void> addJob(final Job job)
	{
		final Future<Void> ret = new Future<Void>();
		
		jobs.put(job.getId(), job);
		
		saveSettings(cfgfile);
		
		IFuture<IComponentManagementService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("job", job);
//					System.out.println("job is: "+sjob);
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				ci.setArguments(args);
				cms.createComponent(null, job.getAgentType(), ci, null).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						System.out.println("created job agent: "+cid);
						cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
						{
							public void customResultAvailable(IExternalAccess result) 
							{
								jobagents.put(job.getId(), result);
								
								publishEvent(new JobManagementEvent(JobManagementEvent.JOB_ADDED, job));
								
								ret.setResult(null);
							}
						});
					}
				});
			}
		});
			
		return ret;
	}
	
	/**
	 *  Remove a job.
	 *  @param jobid The job id.
	 */
	public IFuture<Void> removeJob(final String jobid)
	{
		final Future<Void> ret = new Future<Void>();
		
		IExternalAccess ea = jobagents.remove(jobid);
		
		System.out.println("killing: "+ea+" "+jobid);
		if(ea!=null)
		{
			ea.killComponent().addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
			{
				public void customResultAvailable(Map<String, Object> result)
				{
					Job job = jobs.remove(jobid);
					saveSettings(cfgfile);
					publishEvent(new JobManagementEvent(JobManagementEvent.JOB_REMOVED, job));
					ret.setResult(null);
				}
			});
		}
		
		return ret;
	}
	
//	/**
//	 *  Modify a job.
//	 *  @param job The job.
//	 */
//	public IFuture<Void> modifyJob(Job job)
//	{
//		jobs.put(job.getId(), job);
//		
//		publishEvent(new JobEvent(JobEvent.JOB_CHANGED, job));
//		
//		return IFuture.DONE;
//	}

	
	/**
	 *  Subscribe for job news.
	 */
	public ISubscriptionIntermediateFuture<JobManagementEvent> subscribe()
	{
		if(subscribers==null)
		{
			subscribers	= new LinkedHashSet<SubscriptionIntermediateFuture<JobManagementEvent>>();
		}
		
		final SubscriptionIntermediateFuture<JobManagementEvent> ret = new SubscriptionIntermediateFuture<JobManagementEvent>();
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
			
			public void	terminated(Exception reason)
			{
				subscribers.remove(ret);
			}
		});
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
	protected void publishEvent(JobManagementEvent event)
	{
		if(subscribers!=null)
		{
			for(SubscriptionIntermediateFuture<JobManagementEvent> sub: subscribers)
			{
				sub.addIntermediateResult(event);
			}
		}
	}
	
	/**
	 * 
	 */
	protected void saveSettings(String file)
	{
		if(jobs!=null)
		{
			FileOutputStream fos = null;
			try
			{
				File fs = file!=null? new File(file): new File("./backup-settings.xml");
				fos = new FileOutputStream(fs);
				JavaWriter.objectToOutputStream(new ArrayList(jobs.values()), fos, null);
				System.out.println("Saved settings: "+fs.getPath()+" "+agent.getComponentIdentifier());
			}
			catch(Exception e)
			{
				System.out.println("Error saving backup configuration: "+e);
			}
			finally
			{
				if(fos!=null)
				{
					try
					{
						fos.close();
					}
					catch(Exception e)
					{
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> loadSettings(String file)
	{
		final Future<Void> ret = new Future<Void>();
		
		FileInputStream fis = null;
		try
		{
			File fs = file!=null? new File(file): new File("./backup-settings.xml");
			if(fs.exists())
			{
				fis = new FileInputStream(fs);
				Collection<Job> jobs = (Collection<Job>)JavaReader.objectFromInputStream(fis, null, null);
				addJobs(jobs.iterator()).addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						ret.setResult(null);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error loading backup configuration: "+e);
			ret.setResult(null);
		}
		finally
		{
			if(fis!=null)
			{
				try
				{
					fis.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> addJobs(final Iterator<Job> it)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			addJob(it.next()).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					addJobs(it).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
}
