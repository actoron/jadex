package jadex.platform.service.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Base64;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
@Arguments(
{
	@Argument(name="subject", clazz=String.class, defaultvalue="\"command\"", 
		description="The regular expression for the subject to match identify command emails."),
	@Argument(name="account", clazz=EmailAccount.class,
		description="The email account used to listen for email commands.")
})
@RequiredServices(
{
	@RequiredService(name="emailser", type=IEmailService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, creationinfo=@CreationInfo(type="emailagent"))),
	@RequiredService(name="cliser", type=ICliService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, creationinfo=@CreationInfo(type="cliagent"))),
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
	protected IInternalAccess agent;
	
	/** The email account. */
	@AgentArgument
	protected EmailAccount account;
	
	/** The subject. */
	@AgentArgument
	protected String subject;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<ICliService> clifut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("cliser");
		clifut.addResultListener(new ExceptionDelegationResultListener<ICliService, Void>(ret)
		{
			public void customResultAvailable(final ICliService cliser)
			{
				IFuture<IEmailService> emlfut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("emailser");
				emlfut.addResultListener(new ExceptionDelegationResultListener<IEmailService, Void>(ret)
				{
					public void customResultAvailable(final IEmailService emailser)
					{
						// filter is passed as argument to other agent -> cannot access this.subject
						final String sub = subject;
						emailser.subscribeForEmail(new IFilter<Email>()
						{
							public boolean filter(Email eml)
							{
								return eml.getSubject()!=null && eml.getSubject().matches(sub);
							}
						}, account).addResultListener(new IntermediateExceptionDelegationResultListener<Email, Void>(ret)
						{
							public void intermediateResultAvailable(final Email eml)
							{
								receivedCommandEmail(eml).addResultListener(new DefaultResultListener<Email>()
								{
									public void resultAvailable(final Email rep)
									{
										emailser.sendEmail(rep, account).addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
											}
											
											public void exceptionOccurred(Exception exception)
											{
												System.out.println("Exception while sending email: "+rep+" "+exception);
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
							
							// not push exception to body ret
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof FutureTerminatedException)
									System.out.println("email subscription ended");
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
			IFuture<ICliService> clifut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("cliser");
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
								lines.add(str.replaceAll("\\r|\\n", ""));
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
								byte[] dec = Base64.decode(dgs.get(i).getBytes("UTF-8"));
								authdata.add(dec);
	//							System.out.println("authdata: "+SUtil.arrayToString(dec));
							}
	//						System.out.println("ts: "+ts);
	//						System.out.println("dur: "+dur);
	//						System.out.println("content: "+content);
							
							throw new UnsupportedOperationException("todo: fix security check");
							
//							IFuture<ISecurityService> secfut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("secser");
//							secfut.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Email>(ret)
//							{
//								public void customResultAvailable(final ISecurityService secser)
//								{
//									DefaultAuthorizable da = new DefaultAuthorizable();
//									da.setTimestamp(ts);
//									da.setValidityDuration(dur);
//									da.setDigestContent(content);
//									da.setAuthenticationData(authdata);
//									secser.validateRequest(da).addResultListener(new IResultListener<Void>()
//									{
//										public void resultAvailable(Void result)
//										{
//											executeCommands(cliser, lines.iterator(), eml, new StringBuffer(), new StringBuffer())
//												.addResultListener(new DelegationResultListener<Email>(ret));
//										}
//										
//										public void exceptionOccurred(Exception exception)
//										{
//											Email rep = new Email(null, "Security exception, invalid request.", "failed to execute: "
//												+SUtil.arrayToString(lines), eml.getSender());
//											ret.setResult(rep);
//										}
//									});
//								}
//							});
						}
						else
						{
							Email rep = new Email(null, "Security exception, not signed.", "failed to execute: "
								+SUtil.arrayToString(lines), eml.getSender());
							ret.setResult(rep);
						}
					}
					catch(Exception e)
					{
						Email rep = new Email(null, "Processing exception."+SUtil.LF+SUtil.getStackTrace(e), 
							"failed to execute commands", eml.getSender());
						ret.setResult(rep);
					}
				}
			});
		}
		else
		{
			Email rep = new Email(null, "No commands in body.", "no commands", eml.getSender());
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

			Tuple2<String, Integer> sess = new Tuple2<String, Integer>(SUtil.createUniqueId("emailsess"), Integer.valueOf(0));
			
			cliser.executeCommand(cmd, sess).addResultListener(new IResultListener<String>()
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
	
}
