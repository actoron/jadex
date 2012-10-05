package jadex.backup.job;

import jadex.backup.resource.ILocalResourceService;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  Agent that is responsible for processing a job.
 */
@Agent
@Arguments(@Argument(name="job", clazz=SyncJob.class, description="The job that is executed by the agent."))
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
	
	/** The corresponding resource service. */
	protected ILocalResourceService resser;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		
//		System.out.println("args: "+agent.getArguments());
		
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
				System.out.println("initiating sync");
				resser.updateAll().addResultListener(new IResultListener<Collection<FileData>>()
				{
					public void resultAvailable(Collection<FileData> result)
					{
						System.out.println("finished sync");
						agent.waitForDelay(60000, self);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Update error: "+exception);
						agent.waitForDelay(60000, self);
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
}
