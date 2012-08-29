package jadex.platform.service.cli;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.Collection;

/**
 *  The agent listens on a specified email account for command line emails
 *  (must have 'command' in subject).
 *  
 *  The content of the email is sent to the command line agent for interpretation.
 *  
 *  The result of the invocation is sent back to the issuer.
 */
@Imports(
{	
	"jadex.bridge.service.types.email.EmailAccount"
})
@Arguments(@Argument(name="account", clazz=EmailAccount.class, defaultvalue="EmailAccount.TEST_ACCOUNT"))
@RequiredServices(
{
	@RequiredService(name="emailser", type=IEmailService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, componenttype="emailagent")),
	@RequiredService(name="cliser", type=ICliService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, componenttype="cliagent"))
})
@ComponentTypes(
{
	@ComponentType(name="emailagent", filename="jadex/platform/service/email/EmailAgent.class"),
	@ComponentType(name="cliagent", filename="jadex/platform/service/cli/CliAgent.class")
})
@Agent
public class CliEmailAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The email account. */
	@AgentArgument
	protected EmailAccount account;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
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
					public void customResultAvailable(final IEmailService emailser)
					{
						emailser.subscribeForEmail(new IFilter<Email>()
						{
							public boolean filter(Email eml)
							{
								return eml.getSubject().indexOf("command")!=-1;
							}
						}, account).addResultListener(new IntermediateExceptionDelegationResultListener<Email, Void>(ret)
						{
							public void intermediateResultAvailable(final Email eml)
							{
								receivedCommandEmail(eml).addResultListener(new DefaultResultListener<Email>()
								{
									public void resultAvailable(Email rep)
									{
										emailser.sendEmail(rep, account).addResultListener(new DefaultResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
											}
										});
									}
								});
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
	
	/**
	 * 
	 */
	protected IFuture<Email> receivedCommandEmail(final Email eml) 
	{
		final Future<Email> ret = new Future<Email>();
		
		String content = eml.getContent();
		if(content!=null)
		{
			if(content.indexOf("<html>")!=-1)
			{
				// Remove all tags
				content = content.replaceAll("\\<[^>]*>","");
			}
			
			System.out.println("Executing email command: "+eml.getContent());
			
			IFuture<ICliService> clifut = agent.getServiceContainer().getRequiredService("cliser");
			clifut.addResultListener(new ExceptionDelegationResultListener<ICliService, Email>(ret)
			{
				public void customResultAvailable(final ICliService cliser)
				{
					cliser.executeCommand(eml.getContent(), agent.getExternalAccess())
						.addResultListener(new IResultListener<String>()
					{
						public void resultAvailable(String result)
						{
							System.out.println("Result: "+result);
							String cnt = "Result of the execution :"+SUtil.LF+result;
							Email rep = new Email(account.getSender(), cnt, "command executed: "+eml.getContent(), eml.getSender());
							ret.setResult(rep);
						}
		
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Exception: "+exception);
							String cnt = "Result of the execution :"+SUtil.LF+SUtil.getStackTrace(exception);
							Email rep = new Email(account.getSender(), cnt, "command failed: "+eml.getContent(), eml.getSender());
							ret.setResult(rep);
						}
					});
				}
			});
		}
		
		return ret;
	}

}
