package jadex.backup.job.processing;

import jadex.backup.job.SyncJob;
import jadex.backup.job.SyncProfile;
import jadex.backup.job.SyncTask;
import jadex.backup.job.SyncTaskEntry;
import jadex.backup.job.Task;
import jadex.backup.job.management.IJobManagementService;
import jadex.backup.resource.BackupEvent;
import jadex.backup.resource.ILocalResourceService;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Agent that is responsible for processing a job.
 */
@Agent
@Arguments(
{
	@Argument(name="job", clazz=SyncJob.class, description="The job that is executed by the agent."),
	@Argument(name="autoupdate", clazz=boolean.class, defaultvalue="false", description="Automatically update files or let user manually decide.")
})
@ProvidedServices(
{
	@ProvidedService(type=IJobProcessingService.class, implementation=@Implementation(JobProcessingService.class))
})
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="rps", type=IResourceService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@ComponentTypes(
{
	@ComponentType(name="rpa", filename="jadex/backup/resource/ResourceProviderAgent.class")
})
public class SyncJobProcessingAgent
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The job. */
	@AgentArgument
	protected SyncJob job;
	
	/** Perform automatic updates (or just collect changes and let user decide). */
	@AgentArgument
	protected boolean autoupdate;

	/** The corresponding resource service. */
	protected ILocalResourceService resser;
	
	/** The list of resource services to sync with. */
	protected List<IResourceService> resservices;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		
//		System.out.println("args: "+agent.getArguments());

		this.resservices = new ArrayList<IResourceService>();
		
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("dir", job.getLocalResource());
				args.put("id", job.getGlobalResource());
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				ci.setArguments(args);
				cms.createComponent(null, "rpa", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						agent.getServiceContainer().getService(ILocalResourceService.class, cid)
							.addResultListener(new ExceptionDelegationResultListener<ILocalResourceService, Void>(ret)
						{
							public void customResultAvailable(ILocalResourceService result)
							{
								resser = result;
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
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final long delay = 60000;
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> self = this;
								
				final Future<Void> ret = new Future<Void>();
				
				// search remote resource services (if none available)
				if(resservices.size()==0)
				{
					agent.getServiceContainer().searchServices(IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL)
						.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
					{
						public void intermediateResultAvailable(IResourceService result)
						{
							if(result.getResourceId().equals(job.getGlobalResource())
								&& !result.getLocalId().equals(resser.getLocalId()))
							{
								resservices.add(result);
								ret.setResultIfUndone(null);
							}
						}
						
						public void finished()
						{
							if(resservices.size()>0)
							{
								ret.setResultIfUndone(null);
							}
							else
							{
								ret.setExceptionIfUndone(new RuntimeException("No sync partner"));
							}
						}
						
						public void resultAvailable(Collection<IResourceService> result)
						{
							for(IResourceService resser: result)
							{
								intermediateResultAvailable(resser);
							}
							finished();
						}
						
						public void exceptionOccurred(Exception exception) 
						{
							ret.setExceptionIfUndone(new RuntimeException("No sync partner"));
						}
					});
				}
				else
				{
					ret.setResult(null);
				}
				
				// start sync with first partner
				ret.addResultListener(new IResultListener<Void>() 
				{
					public void resultAvailable(Void result)
					{
						startSync().addResultListener(new IResultListener<Void>()
						{ 
							public void resultAvailable(Void result)
							{
								System.out.println("waiting...");
								agent.waitForDelay(delay, self);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("waiting...");
								agent.waitForDelay(delay, self);
							}
						});	
					}	
					
					public void exceptionOccurred(Exception exception) 
					{
						System.out.println("waiting...");
						agent.waitForDelay(delay, self);
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> startSync()
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("starting sync with: "+resservices.get(0));

		final IResourceService remresser = resservices.get(0);
		
		final SyncTask task = new SyncTask(job.getId(), remresser.getLocalId(), System.currentTimeMillis());
		
		// Scan for changes wrt a specific remote resource
		resser.scanForChanges(remresser).addResultListener(new IIntermediateResultListener<BackupEvent>()
		{
			public void intermediateResultAvailable(BackupEvent result)
			{
//				System.out.println(result);
				SyncTaskEntry entry = null;
				
				// Todo: user-editable policies for default action sets (e.g. skip conflicts vs. copy on conflict)
				List<String>	actions	= SyncProfile.ALLOWED_ACTIONS.get(result.getType());
				if(actions.size()>1)
				{
					entry	= new SyncTaskEntry(task, result.getLocalFile(), result.getRemoteFile(), result.getType(), actions.get(0));
				}
										
				if(entry!=null)
				{
					if(!autoupdate)
					{
//						changelist.add(new Tuple2<String, FileInfo>(result.getType(), result.getFile()));
						task.addSyncEntry(entry);
					}
					else
					{
						performAction(resser, remresser, entry).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
							}
							
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
						});
					}
				}
			}
			
			public void finished()
			{
				System.out.println("finished sync scan");
				if(!autoupdate)
				{
					SServiceProvider.getService(agent.getServiceProvider(), IJobManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new IResultListener<IJobManagementService>()
					{
						public void resultAvailable(IJobManagementService js)
						{
							if(task.getEntries()!=null && task.getEntries().size()>0)
							{
								// Publish modified job 
								System.out.println("publishing sync task: Job@"+job.hackCode());
								job.addTask(task);
								publishEvent(new TaskEvent(AJobProcessingEvent.TASK_ADDED, task));
								ret.setResult(null);
							}
							else
							{
								ret.setResult(null);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setResult(null);
						}
					});
					
//					updateFiles(resser, remresser, changelist.iterator())
//						.addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//							resservices.remove(0);
//							agent.waitForDelay(60000, self);
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							resultAvailable(null);
//						}
//					});
				}
				else
				{
					ret.setResult(null);
				}
			}
			
			public void resultAvailable(Collection<BackupEvent> result)
			{
				System.out.println("finished sync");
				resservices.remove(0);
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("Update error: "+exception);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void jobModified(SyncJob job)
	{
		System.out.println("todo: job was modified...");
	}
	
	/**
	 * 
	 */
	public void taskModified(Task t)
	{
		final SyncTask task = (SyncTask)t;
		
		System.out.println("sync agent received changed task: "+task+" Job@"+job.hackCode());
		
		List<Task> srs = job.getTasks();
		if(srs!=null && srs.contains(task))
		{
			// Update task
			srs.remove(task);
			srs.add(task);
			
			if(SyncTask.STATE_ACKNOWLEDGED.equals(task.getState()))
			{
				task.setState(SyncTask.STATE_ACTIVE);
				List<SyncTaskEntry> ses = task.getEntries();
				if(ses!=null)
				{
					IResourceService remresser = findRessourceService(task.getSource());
					if(remresser!=null)
					{
						performActions(resser, remresser, ses.iterator())
							.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								task.setState(SyncTask.STATE_FINISHED);
								publishEvent(new TaskEvent(JobProcessingEvent.TASK_CHANGED, task));
								System.out.println("Finished updating files");
							}
	
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("Exception during updating files");
							}
						});
					}
					else
					{
						System.out.println("Update src not available: "+remresser);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected IResourceService findRessourceService(String localid)
	{
		IResourceService ret = null;
		for(IResourceService res: resservices)
		{
			if(res.getLocalId().equals(localid))
			{
				ret = res;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> performActions(final ILocalResourceService localresser, final IResourceService resser, final Iterator<SyncTaskEntry> it)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			SyncTaskEntry entry = it.next();
			performAction(localresser, resser, entry).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					performActions(localresser, resser, it).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> performAction(ILocalResourceService localresser, IResourceService resser, final SyncTaskEntry entry)
	{
		final Future<Void> ret = new Future<Void>();
		
		ITerminableIntermediateFuture<BackupEvent>	fut	= null;
		
		if(SyncProfile.isUpdate(entry.getAction()))
		{
			fut	= localresser.updateFromRemote(resser, entry.getLocalFileInfo(), entry.getRemoteFileInfo());
		}
		else if(SyncProfile.isOverride(entry.getAction()))
		{
			fut	= localresser.overrideRemoteChange(resser, entry.getLocalFileInfo(), entry.getRemoteFileInfo());
		}
		else if(SyncProfile.isCopy(entry.getAction()))
		{
			fut	= localresser.updateAsCopy(resser, entry.getLocalFileInfo(), entry.getRemoteFileInfo());
		}
			
		if(fut!=null)
		{
			fut.addResultListener(new IIntermediateResultListener<BackupEvent>()
			{
				public void intermediateResultAvailable(BackupEvent be)
				{
	//				System.out.println("upfi: "+be);
					publishEvent(new SyncTaskEntryEvent(entry.getTaskId(), entry.getId(), ((Double)be.getDetails()).doubleValue()));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println(exception);
					ret.setResult(null);
				}
				
				public void resultAvailable(Collection<BackupEvent> result)
				{
	//				System.out.println(result);
					ret.setResult(null);
				}
				
				public void finished()
				{
	//				System.out.println("fini");
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Get the job.
	 *  @return The job.
	 */
	public SyncJob getJob()
	{
		return job;
	}
	
	/**
	 * 
	 */
	protected void publishEvent(AJobProcessingEvent event)
	{
		JobProcessingService jps = (JobProcessingService)agent.getServiceContainer().getProvidedServiceRawImpl(IJobProcessingService.class);
		jps.publishEvent(event);
	}
	
}
