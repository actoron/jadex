package jadex.platform.service.cli;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.commons.IFilter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;

/**
 * 
 */
@RequiredServices(
{
	@RequiredService(name="emailser", type=IEmailService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cliser", type=ICliService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Agent
public class CliEmailAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<ICliService> clifut = agent.getServiceContainer().getRequiredService("cliser");
		clifut.addResultListener(new ExceptionDelegationResultListener<ICliService, Void>(ret)
		{
			public void customResultAvailable(final ICliService cliser)
			{
				IFuture<IEmailService> emlfut = agent.getServiceContainer().getRequiredService("emailser");
				emlfut.addResultListener(new ExceptionDelegationResultListener<IEmailService, Void>(ret)
				{
					public void customResultAvailable(IEmailService emailser)
					{
						emailser.subscribeForEmail(new IFilter<Email>()
						{
							public boolean filter(Email eml)
							{
								return eml.getSubject().indexOf("command")!=-1;
							}
						}, EmailAccount.TEST_ACCOUNT).addResultListener(new IntermediateExceptionDelegationResultListener<Email, Void>(ret)
						{
							public void intermediateResultAvailable(Email eml)
							{
								if(eml.getContent()!=null)
								{
									System.out.println("Executing email command: "+eml.getContent());
									cliser.executeCommand(eml.getContent(), agent.getExternalAccess())
										.addResultListener(new DefaultResultListener<String>()
									{
										public void resultAvailable(String result)
										{
											System.out.println("Result: "+result);
										}
									});
								}
							}
							
							public void finished()
							{
							}

							public void customResultAvailable(Collection<Email> result)
							{
							}
						});
					}
				});
			}
		});
		
		
			
		return ret;
	}
}
