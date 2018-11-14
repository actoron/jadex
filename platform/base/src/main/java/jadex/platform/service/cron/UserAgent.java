package jadex.platform.service.cron;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.ICronService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.cron.jobs.CliJob;
import jadex.platform.service.cron.jobs.CreateComponentJob;

@RequiredServices(
{
	@RequiredService(name="libs", type=ILibraryService.class),
	@RequiredService(name="crons", type=ICronService.class)
})
@ComponentTypes(@ComponentType(name="cronagent", filename="jadex/platform/service/cron/CronAgent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="cronagent")))
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
		
		IFuture<ICronService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("crons");
		fut.addResultListener(new ExceptionDelegationResultListener<ICronService, Void>(ret)
		{
			public void customResultAvailable(final ICronService crons)
			{
				IFuture<ILibraryService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("libs");
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
