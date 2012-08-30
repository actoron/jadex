package jadex.platform.service.cli;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.bridge.service.types.security.DefaultAuthorizable;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Base64;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *  The agent listens on a specified email account for command line emails
 *  (must have 'command' in subject).
 *  
 *  The email has to be signed using the Jadex email sign tool (in JCC).
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
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, componenttype="cliagent")),
	@RequiredService(name="secser", type=ISecurityService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
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
	 *  Called when a command email was received.
	 */
	protected IFuture<Email> receivedCommandEmail(final Email eml) 
	{
		final Future<Email> ret = new Future<Email>();
		
		String content = eml.getContent();
		if(content!=null)
		{
			IFuture<ICliService> clifut = agent.getServiceContainer().getRequiredService("cliser");
			clifut.addResultListener(new ExceptionDelegationResultListener<ICliService, Email>(ret)
			{
				public void customResultAvailable(final ICliService cliser)
				{
					try
					{
						// First find commands and rest is digest
						StringTokenizer stok = new StringTokenizer(eml.getContent(), ";");
						final List<String> lines = new ArrayList<String>();
						String digests = null;
						StringBuffer buf = new StringBuffer();
						boolean end = false;
						while(stok.hasMoreTokens())
						{
							String tmp = stok.nextToken();
							String str = tmp.trim();
							if(str.startsWith("#"))
							{
								end = true;
								digests = str;
							}
							else
							{
								lines.add(str);
							}
							
							if(!end)
							{
								buf.append(tmp).append(";");
							}
						}
						
						String fcnt = buf.toString();
						final String content = fcnt.replaceAll("\\r|\\n", "");
						
						if(digests!=null)
						{
							stok = new StringTokenizer(digests, "#");
							final List<String> dgs = new ArrayList<String>();
							while(stok.hasMoreTokens())
							{
								String tmp = stok.nextToken().trim();
								if(tmp.length()>0) // skip empty strings
									dgs.add(tmp);
							}
	//						System.out.println("digests: "+dgs);
							final long ts = Long.parseLong(dgs.get(0));
							final long dur = Long.parseLong(dgs.get(1));
							
							final List<byte[]> authdata = new ArrayList<byte[]>();
							for(int i=2; i<dgs.size(); i++)
							{
								byte[] dec = Base64.decode(dgs.get(i).getBytes());
								authdata.add(dec);
	//							System.out.println("authdata: "+SUtil.arrayToString(dec));
							}
	//						System.out.println("ts: "+ts);
	//						System.out.println("dur: "+dur);
	//						System.out.println("content: "+content);
							
							IFuture<ISecurityService> secfut = agent.getServiceContainer().getRequiredService("secser");
							secfut.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Email>(ret)
							{
								public void customResultAvailable(final ISecurityService secser)
								{
									DefaultAuthorizable da = new DefaultAuthorizable();
									da.setTimestamp(ts);
									da.setValidityDuration(dur);
									da.setDigestContent(content);
									da.setAuthenticationData(authdata);
									secser.validateRequest(da).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											executeCommands(cliser, lines.iterator(), eml, new StringBuffer(), new StringBuffer())
												.addResultListener(new DelegationResultListener<Email>(ret));
										}
										
										public void exceptionOccurred(Exception exception)
										{
											Email rep = new Email(account.getSender(), "Security exception, invalid request.", "failed to execute: "
												+SUtil.arrayToString(lines).replaceAll("\\r|\\n", ""), eml.getSender());
											ret.setResult(rep);
										}
									});
								}
							});
						}
						else
						{
							Email rep = new Email(account.getSender(), "Security exception, not signed.", "failed to execute: "
								+SUtil.arrayToString(lines).replaceAll("\\r|\\n", ""), eml.getSender());
							ret.setResult(rep);
						}
					}
					catch(Exception e)
					{
						Email rep = new Email(account.getSender(), "Processing exception."+SUtil.LF+SUtil.getStackTrace(e), 
							"failed to execute commands", eml.getSender());
						ret.setResult(rep);
					}
				}
			});
		}
		else
		{
			Email rep = new Email(account.getSender(), "No commands in body.", "no commands", eml.getSender());
			ret.setResult(rep);
		}
		
		return ret;
	}
	
	/**
	 *  Execute a number of commands.
	 */
	protected IFuture<Email> executeCommands(final ICliService cliser, final Iterator<String> cmds, final Email eml, 
		final StringBuffer content, final StringBuffer subject)
	{
		final Future<Email> ret = new Future<Email>();
		
		if(cmds.hasNext())
		{
			String cmd = cmds.next();
			System.out.println("Executing email command: "+cmd);
			if(subject.length()!=0)
				subject.append(" ");
			subject.append(cmd);

			cliser.executeCommand(cmd, agent.getExternalAccess()).addResultListener(new IResultListener<String>()
			{
				public void resultAvailable(String result)
				{
					if(content.length()==0)
						content.append("Result of the execution :").append(SUtil.LF).append(SUtil.LF);
					content.append(result);
					executeCommands(cliser, cmds, eml, content, subject).addResultListener(new DelegationResultListener<Email>(ret));
				}
				public void exceptionOccurred(Exception exception)
				{
					if(content.length()==0)
						content.append("Result of the execution :").append(SUtil.LF).append(SUtil.LF);
					content.append(SUtil.getStackTrace(exception));
					executeCommands(cliser, cmds, eml, content, subject).addResultListener(new DelegationResultListener<Email>(ret));
				}
			});
		}
		else
		{
			Email rep = new Email(account.getSender(), content.toString(), "executed: "+subject, eml.getSender());
			ret.setResult(rep);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		String test = "cc -model jadex/micro/examples/helloworld/HelloWorldAgent.class -rid\r\napplications-micro;help;\r\n==1346225963008==\r\n==xiA5dSIYwQYt6veZ4P4XqkUgMr34PdWnlU5NXOmzManvGkQiJoAdIw7bXiSMYrRv==\r\n==QrxVsuMG1Y2xQ0GK2km+lmPWQ21jFmsWPri2R9BWoTvUiKmBRpztOv4gcy2iItLk==\r\n==FwuRNAUJRrsb+gAVanlyp5TDKlvmfoRcr/AfUpVwpZFFtT2ZS5sLQJcMos911IoL==\r\n==bgxBOGCoODY7ydTxS4PfX4bRBCt1dCqnn9ik0Cj0UM3UV874VERNLu70Mj/Fj3k0==";
		
		StringTokenizer stok = new StringTokenizer(test, ";");
		List<String> lines = new ArrayList<String>();
		String digests = null;
		while(stok.hasMoreTokens())
		{
			String str = stok.nextToken().trim();
			if(str.startsWith("#"))
			{
				digests = str;
			}
			else
			{
				lines.add(str);
			}
		}
		
		if(digests!=null)
		{
			stok = new StringTokenizer(digests, "#");
			List<String> dgs = new ArrayList<String>();
			while(stok.hasMoreTokens())
			{
				String tmp = stok.nextToken().trim();
				if(tmp.length()>0)
					dgs.add(tmp);
			}
			System.out.println(dgs);
		}
	}
}
