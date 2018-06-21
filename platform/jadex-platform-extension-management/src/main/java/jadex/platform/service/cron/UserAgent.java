package jadex.platform.service.cron;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.jobs.CliJob;
import jadex.platform.service.cron.jobs.CreateComponentJob;

@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="crons", type=ICronService.class, 
		binding=@Binding(create=true, creationinfo=@jadex.micro.annotation.CreationInfo(type="cronagent")))
//		binding=@Binding(create=true, creationtype="cronagent"))
})
@ComponentTypes(@ComponentType(name="cronagent", filename="jadex/platform/service/cron/CronAgent.class"))
@Agent
public class UserAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<ICronService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("crons");
		fut.addResultListener(new ExceptionDelegationResultListener<ICronService, Void>(ret)
		{
			public void customResultAvailable(final ICronService crons)
			{
				IFuture<ILibraryService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("libs");
				fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService libs)
					{
						// job that creates a hello world agent every minute
						IResourceIdentifier rid = libs.getRootResourceIdentifier();
						CreationInfo ci = new CreationInfo(rid);
						String pattern = "* * * * *";
						crons.addJob(new CreateComponentJob(pattern, null,
//							"jadex/platform/service/cron/CronAgent.class"));
							"jadex/micro/examples/helloworld/HelloWorldAgent.class", ci));
						
						// job that lists the platforms every minute
						crons.addJob(new CliJob("* * * * *", new String[]{"lp", "lc"}));
					}
				});
			}
		});
	
		return ret;
	}
}
