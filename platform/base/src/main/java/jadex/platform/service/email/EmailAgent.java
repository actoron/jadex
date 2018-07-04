package jadex.platform.service.email;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.IntervalBehavior;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  The email agent can be used to send emails and subscribe for incoming mails.
 */
@Agent
@Service
@Imports({"jadex.bridge.service.types.email.*"})
@Arguments(
{
	@Argument(name="checkformail", description="Delay between checking for new mails.", clazz=long.class, defaultvalue="60000"),
	@Argument(name="account", clazz=EmailAccount.class, defaultvalue="new EmailAccount(\"default_account.properties\")", 
		description="The default email account that is used to send/receive emails.")
})
@ProvidedServices(@ProvidedService(type = IEmailService.class))
@RequiredServices(@RequiredService(name="emailfetcher", type=IEmailFetcherService.class))
@ComponentTypes(@ComponentType(name="fetcher", clazz=EmailFetcherAgent.class))
@Configurations(@Configuration(name="default", components=@Component(type="fetcher")))
public class EmailAgent implements IEmailService
{
	//-------- constants --------
	
	/** Default SSL versions. */
	protected static final String	DEFAULT_SSL_VERSION;
	
	static
	{
		String	ret;
		try
		{
			SSLServerSocket ssocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket();
			StringBuffer	buf	= new StringBuffer();
			for(String protocol: ssocket.getEnabledProtocols())
			{
				if(buf.length()>0)
				{
					buf.append(" ");
				}
				buf.append(protocol);
			}
			ret	= buf.toString();
		}
		catch(Exception e)
		{
			ret	= "TLSv1 TLSv1.1 TLSv1.2";
		}
		DEFAULT_SSL_VERSION	= ret;
	}
	
	//-------- attributes --------
	
	/** The component. */
	@Agent
	protected IInternalAccess agent;
	
	/** The delay between checking for mail. */
	@AgentArgument
	protected long checkformail;
	
	/** The email account. */
	@AgentArgument
	protected EmailAccount account;
	
	/** The receive behavior. */
	protected IntervalBehavior<Void> receive; 
	
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<Email>, SubscriptionInfo> subscriptions;

	//-------- agent lifecycle methods --------
	
	/**
	 *  Called on agent start.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
//		System.out.println("email agent started");
		
		if(account==null)
		{
			try
			{
				account = new EmailAccount("default_account.properties");
			}
			catch(Exception e)
			{
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Called when service is shudowned.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		if(subscriptions!=null)
		{
			SubscriptionIntermediateFuture<Email>[] subs = subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[subscriptions.size()]);
			for(int i=0; i<subs.length; i++)
			{
				subs[i].terminate(); // removes itself on terminate
			}
		}
		
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Send an email.
	 *  @param email The email.
	 *  @param account The email account.
	 */
	public IFuture<Void> sendEmail(Email email, EmailAccount acc)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(acc==null && this.account==null)
		{
//			System.out.println("email agent send email: no account");
			ret.setException(new RuntimeException("No email account given."));
			return ret;
		}
		
		if(email.getReceivers()==null || email.getReceivers().length==0)
		{
			ret.setException(new RuntimeException("No receivers given."));
			return ret;
		}
		
//		System.out.println("email agent trying to send email");
		final EmailAccount account = acc!=null? acc: this.account;
		Properties props = account.getProperties();
		if(props.getProperty("mail.smtp.ssl.protocols")==null)
		{
			props.setProperty("mail.smtp.ssl.protocols", DEFAULT_SSL_VERSION);
		}
		if(props.getProperty("mail.smtps.ssl.protocols")==null)
		{
			props.setProperty("mail.smtps.ssl.protocols", DEFAULT_SSL_VERSION);
		}
		
		// Override account sender, if set in mail.
		if(email.getSender()!=null)
		{
			props.setProperty("mail.from", email.getSender());
		}
		else
		{
			email.setSender(account.getSender());
		}
		
//		props.put("mail.debug", "true");
		
		// Hack!!! bypass certificate check.
//		props.setProperty("mail.smtp.ssl.trust", account.getSmtpHost());

		// Todo: needed?
//		if(account.isSsl())
//		{
//			props.put("mail.smtp.socketFactory.port", ""+account.getSmtpPort());
//			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		}
		
		Session sess;
		
//		System.out.println("account: auth? "+account.isNoAuthentication());
		
		if(account.isNoAuthentication())
		{
			sess = Session.getInstance(props);
		}
		else
		{
			sess = Session.getInstance(props, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(account.getUser(), account.getPassword());
				}
			});
		}
		
//		Properties props = new Properties();
//		props.setProperty("mail.smtp.auth", "true");
//		props.setProperty("mail.smtps.auth", "true");
////		if(account.isStartTls())
//		{
//			props.put("mail.smtp.starttls.enable", "true");
//		}
////        if(account.isSsl())
////        {
//////	        props.put("mail.smtp.socketFactory.port", account.getPort());
////        	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
////        }
//        
//		Session sess = Session.getDefaultInstance(props, null);
//		sess.setDebug(true);

		try
		{
			MimeMessage message = new MimeMessage(sess);
			message.setFrom(new InternetAddress(account.getSender()));
			if(email.getContent()!=null)
				message.setContent(email.getContent(), email.getContentType()==null? "text/plain": email.getContentType());
			if(email.getSubject()!=null)
				message.setSubject(email.getSubject());
			String[] recs = email.getReceivers();
			if(recs!=null)
			{
				for(int i=0; i<email.getReceivers().length; i++)
				{
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recs[i]));
				}
			}
			String[] ccs = email.getCcs();
			if(ccs!=null)
			{
				for(int i=0; i<ccs.length; i++)
				{
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccs[i]));
				}
			}
			String[] bccs = email.getBccs();
			if(bccs!=null)
			{
				for(int i=0; i<bccs.length; i++)
				{
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccs[i]));
				}
			}
			
			Transport tr;
			if(account.isSsl())
			{
				tr	= sess.getTransport("smtps");
			}
			else
			{
				tr	= sess.getTransport("smtp");
			}
			
			if(account.isNoAuthentication())
			{
//				System.out.println("connect0: "+account.getSmtpHost()+" "+account.getSmtpPort()+" "+account.getUser()+" "+account.getPassword());
				tr.connect();
			}
			else
			{
				if(account.getSmtpPort()!=null)
				{
//					System.out.println("connect1: "+account.getSmtpHost()+" "+account.getSmtpPort()+" "+account.getUser()+" "+account.getPassword());
					tr.connect(account.getSmtpHost(), account.getSmtpPort().intValue(), account.getUser(), account.getPassword());
				}
				else
				{
//					System.out.println("connect2: "+account.getSmtpHost()+" "+account.getUser()+" "+account.getPassword());
					tr.connect(account.getSmtpHost(), account.getUser(), account.getPassword());
				}
			}
			
			message.saveChanges();
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
			ret.setResult(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}

	/**
	 *  Subscribe for email.
	 *  @param filter The filter.
	 *  @param account The email account.
	 */
	public ISubscriptionIntermediateFuture<Email> subscribeForEmail(IFilter<Email> filter, EmailAccount acc)
	{
		return subscribeForEmail(filter, acc, false);
	}
	
	/**
	 *  Subscribe for email.
	 *  @param filter The filter.
	 *  @param account The email account.
	 */
	public ISubscriptionIntermediateFuture<Email> subscribeForEmail(IFilter<Email> filter, EmailAccount acc, boolean fullconv)
	{
//		final SubscriptionIntermediateFuture<Email> ret = new SubscriptionIntermediateFuture<Email>();
		final SubscriptionIntermediateFuture<Email> ret = (SubscriptionIntermediateFuture<Email>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		
		if(acc==null && this.account==null)
		{
			ret.setException(new RuntimeException("No email account given."));
			return ret;
		}
		
		final EmailAccount account = acc!=null? acc: this.account;
		
		ITerminationCommand tcom = new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeSubscription(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		};
		ret.setTerminationCommand(tcom);
		
		addSubscription(ret, new SubscriptionInfo(filter, account, fullconv));
		getReceiveBehavior(checkformail);
		
		return ret;
	}
	
	/**
	 *  Add a new subscription.
	 *  @param future The subscription future.
	 *  @param si The subscription info.
	 */
	protected void addSubscription(SubscriptionIntermediateFuture<Email> future, SubscriptionInfo si)
	{
		if(subscriptions==null)
			subscriptions = new LinkedHashMap<SubscriptionIntermediateFuture<Email>, SubscriptionInfo>();
		subscriptions.put(future, si);
	}
	
	/**
	 *  Remove an existing subscription.
	 *  @param fut The subscription future to remove.
	 */
	protected void removeSubscription(SubscriptionIntermediateFuture<Email> fut)
	{
		if(subscriptions==null || !subscriptions.containsKey(fut))
			throw new RuntimeException("Subscriber not known: "+fut);
		subscriptions.remove(fut);
	}
	
	/**
	 *  Check for new emails and notify the 
	 *  corresponding subscribers.
	 */
	protected void checkForNewMails()
	{
//		System.out.println("checking for new mails");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		agent.getLogger().info("checking for new mails: "+sdf.format(new Date(System.currentTimeMillis())));
		
		if(subscriptions!=null)
		{
			for(final SubscriptionIntermediateFuture<Email> fut: subscriptions.keySet().toArray(new SubscriptionIntermediateFuture[0]))
			{
				SubscriptionInfo si = subscriptions.get(fut);
				final long start = System.currentTimeMillis();
				IEmailFetcherService fetcher = (IEmailFetcherService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("emailfetcher").get();
//				System.out.println("Email fetcher ser: "+fetcher);
				fetcher.fetchEmails(si).addResultListener(new IResultListener<Collection<Email>>()
				{
					public void resultAvailable(Collection<Email> emails) 
					{
						long dur = (System.currentTimeMillis()-start)/1000;
//						System.out.println("Needed for email fetching [s]: "+dur);
						
						if(emails!=null && emails.size()>0)
						{
							for(Email email: emails)
							{
								if(!fut.addIntermediateResultIfUndone(email))
								{
									subscriptions.remove(fut);
								}
							}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						agent.getLogger().warning("Email fetching error: "+exception.getMessage());
					}
				});
				
//				List<Email> emails = si.getNewEmails();
//				if(emails!=null && emails.size()>0)
//				{
//					for(Email email: emails)
//					{
//						fut.addIntermediateResult(email);
//					}
//				}
			}
		}
	}
	
	/**
	 *  Get (or create, or renew) the receive behavior.
	 */
	public IntervalBehavior<Void> getReceiveBehavior(long delay)
	{
		if(receive==null)
		{
			receive = new IntervalBehavior<Void>(agent, delay, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					checkForNewMails();
					return IFuture.DONE;
				}
			}, false);
		}

		// In any case invoke start to immediately check for new email
		receive.startBehavior().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("receive ended");
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("exeception checking mail: "+exception);
//				exception.printStackTrace();
			}
		});
		
		return receive;
	}
	
}
