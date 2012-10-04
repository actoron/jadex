package jadex.backup.job;

import jadex.backup.swing.ObjectivesPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Agent that is responsible for processing a job.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IJobService.class, implementation=@Implementation(expression="$pojoagent")))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="sa", filename="jadex/backup/resource/ResourceProviderAgent.class"))
public class JobManagerAgent implements IJobService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The gui frame. */
	protected JFrame gui;
	
	/** The map of jobs (id -> job). */
	protected List<Job> jobs;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		this.jobs = new ArrayList<Job>();
		
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	ea	= agent.getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui = ObjectivesPanel.createFrame(ea);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	stop()
	{
		final Future<Void>	ret	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Add a new job.
	 *  @param job The job.
	 */
	public IFuture<Void> addJob(Job job)
	{
		final Future<Void> ret = new Future<Void>();
		
		jobs.add(job);
		if(job instanceof SyncJob)
		{
			final SyncJob sjob = (SyncJob)job;
			IFuture<IComponentManagementService> fut = agent.getRequiredService("cms");
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
		jobs.remove(jobid);
		return IFuture.DONE;
	}
	
	/**
	 *  Get all jobs. 
	 *  @return All jobs.
	 */
	public IIntermediateFuture<Job> getJobs()
	{
		final IntermediateFuture<Job> ret = new IntermediateFuture<Job>();
		ret.setResult(jobs);
		return ret;
	}

}
