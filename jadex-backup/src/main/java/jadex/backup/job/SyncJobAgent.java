package jadex.backup.job;

import jadex.backup.resource.BackupEvent;
import jadex.backup.resource.BackupResource;
import jadex.backup.resource.FileInfo;
import jadex.backup.resource.ILocalResourceService;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="rps", type=IResourceService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class SyncJobAgent
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
				cms.createComponent(null, "jadex/backup/resource/ResourceProviderAgent.class", ci, null).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
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
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> self = this;
								
				final Future<Void> fini = new Future<Void>();
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
								fini.setResultIfUndone(null);
							}
						}
						
						public void finished()
						{
							if(resservices.size()>0)
							{
								fini.setResultIfUndone(null);
							}
							else
							{
								fini.setExceptionIfUndone(new RuntimeException("No sync partner"));
							}
						}
						
						public void resultAvailable(Collection<IResourceService> result)
						{
							for(IResourceService resser: result)
							{
								intermediateResultAvailable(resser);
							}
						}
						
						public void exceptionOccurred(Exception exception) 
						{
							fini.setExceptionIfUndone(new RuntimeException("No sync partner"));
						}
					});
				}
				
				fini.addResultListener(new IResultListener<Void>() 
				{
					public void resultAvailable(Void result)
					{
						final IResourceService remresser = resservices.get(0);
						System.out.println("starting sync with: "+resservices.get(0));
						
						final List<Tuple2<String, FileInfo>> changelist = new ArrayList<Tuple2<String, FileInfo>>();
						
						resser.update(remresser).addResultListener(new IIntermediateResultListener<BackupEvent>()
						{
							public void intermediateResultAvailable(BackupEvent result)
							{
//								System.out.println(result);
								if(BackupResource.FILE_ADDED.equals(result.getType())
//									|| BackupResource.FILE_REMOVED.equals(result.getType())
									|| BackupResource.FILE_MODIFIED.equals(result.getType()))
								{
									if(!autoupdate)
									{
										changelist.add(new Tuple2<String, FileInfo>(result.getType(), result.getFile()));
									}
									else
									{
										updateFile(resser, remresser, result.getFile()).addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
											}
											
											public void exceptionOccurred(Exception exception)
											{
											}
										});
									}
								}
							}
							
							public void finished()
							{
								System.out.println("finished sync");
								if(!autoupdate)
								{
									for(Tuple2<String, FileInfo> tup: changelist)
									{
										System.out.println(tup);
										updateFile(resser, remresser, tup.getSecondEntity()).addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
											}
											
											public void exceptionOccurred(Exception exception)
											{
											}
										});
									}
								}
								resservices.remove(0);
								agent.waitForDelay(60000, self);
							}
							
							public void resultAvailable(Collection<BackupEvent> result)
							{
								System.out.println("finished sync");
								resservices.remove(0);
								agent.waitForDelay(60000, self);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("Update error: "+exception);
								agent.waitForDelay(60000, self);
							}
						});
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						agent.waitForDelay(60000, self);
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * 
	 */
	public IFuture<Void> updateFile(ILocalResourceService localresser, IResourceService resser, FileInfo fi)
	{
		final Future<Void> ret = new Future<Void>();
		
		localresser.updateFile(resser, fi)
			.addResultListener(new IIntermediateResultListener<BackupEvent>()
		{
			public void intermediateResultAvailable(BackupEvent result)
			{
				System.out.println(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println(exception);
				ret.setResult(null);
			}
			
			public void resultAvailable(Collection<BackupEvent> result)
			{
				System.out.println(result);
				ret.setResult(null);
			}
			
			public void finished()
			{
				System.out.println("fini");
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
