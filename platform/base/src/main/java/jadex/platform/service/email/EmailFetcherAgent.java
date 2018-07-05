package jadex.platform.service.email;

import java.util.List;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.email.Email;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  Agent that is responsible for fetching mails for one subscription.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class EmailFetcherAgent implements IEmailFetcherService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Fetch emails for a subscription.
	 *  @param sub The subscription.
	 *  @return The emails.
	 */
	public IIntermediateFuture<Email> fetchEmails(SubscriptionInfo sub)
	{
		List<Email> emails = sub.getNewEmails();
		
		// Hack, needed because pool cannot be used (pro)
		agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println("kill: "+agent);
				agent.killComponent();
				return IFuture.DONE;
			}
		});
		
		return new IntermediateFuture<Email>(emails);
	}
}
