package jadex.platform.service.email;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.types.email.Email;
import jadex.bridge.service.types.email.EmailAccount;
import jadex.bridge.service.types.email.IEmailService;
import jadex.commons.ConstantFilter;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import com.sun.mail.imap.IMAPFolder;


/**
 * 
 */
@Agent
@Service
@Arguments(@Argument(name="checkformail", description="Delay between checking for new mails.", clazz=long.class, defaultvalue="60000"))
@ProvidedServices(@ProvidedService(type = IEmailService.class, implementation = @Implementation(expression="$pojoagent")))
public class EmailAgent implements IEmailService
{
	//-------- attributes --------
	
	/** The component. */
	@Agent
	protected MicroAgent	agent;
	
	/** The delay between checking for mail. */
	@AgentArgument
	protected long checkformail;
	
	/** The receive behavior. */
	protected IntervalBehavior<Void> receive; 
	
	/** The subscriptions (subscription future -> subscription info). */
	protected Map<SubscriptionIntermediateFuture<Email>, SubscriptionInfo> subscriptions;

	//-------- agent lifecycle methods --------
	
	/**
	 * The agent body.
	 */
//	@AgentBody
//	public void body()
//	{
//		Email email = new Email("Hi all, its a test message.", "test",
//			"braubach@informatik.uni-hamburg.de");
//
//		IEmailService es = (IEmailService)agent.getServiceContainer()
//			.getProvidedServices(IEmailService.class)[0];
//		
////		es.sendEmail(email, account);
////		System.out.println("sent successfully");
//		
//		es.subscribeForEmail(new ConstantFilter<Email>(true), EmailAccount.TEST_ACCOUNT)
//			.addResultListener(new IIntermediateResultListener<Email>()
//		{
//			public void intermediateResultAvailable(Email result)
//			{
//				System.out.println("ir: "+result);
//			}
//
//			public void finished()
//			{
//				System.out.println("fini");
//			}
//
//			public void resultAvailable(Collection<Email> result)
//			{
//				System.out.println("ra: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
//	}
			
	/**
	 *  Called when service is shudowned.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		if(subscriptions!=null)
		{
			for(Iterator<SubscriptionIntermediateFuture<Email>> 
				it = subscriptions.keySet().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Email> fut = it.next();
				fut.terminate();
				it.remove();
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
	public IFuture<Void> sendEmail(Email email, final EmailAccount account)
	{
		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtps.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
        
		Session sess = Session.getDefaultInstance(props, null);
		sess.setDebug(true);

		try
		{
			MimeMessage message = new MimeMessage(sess);
			message.setFrom(new InternetAddress("jadexagent"));
			message.setContent("test", "text/ plain");
			message.setSubject("test");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("braubach@informatik.uni-hamburg.de"));
			
			Transport tr;
			tr	= sess.getTransport("smtp");
			tr.connect(EmailAccount.TEST_ACCOUNT.getSmtpHost(), EmailAccount.TEST_ACCOUNT.getSmtpPort().intValue(), 
				EmailAccount.TEST_ACCOUNT.getUser(), EmailAccount.TEST_ACCOUNT.getPassword());
//			tr.connect(account.getSmtpHost(), account.getUser(), account.getPassword());
			
			message.saveChanges();
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		final Future<Void> ret = new Future<Void>();
		
		// Todo: attachments?
//			Object[]	attachments	= getParameterSet("attachments").getValues(); // todo:
		
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
//		
//
//		try
//		{
//			Transport tr = sess.getTransport("smtp");
//			tr.connect(account.getSmtpHost(), account.getSmtpPort().intValue(), account.getUser(), account.getPassword());
//
//			MimeMessage message = new MimeMessage(sess);
//			message.setFrom(new InternetAddress(account.getSender()));
//			message.setContent(email.getContent(), "text/ plain");
//			message.setSubject(email.getSubject());
//			String[] recs = email.getReceivers();
//			if(recs!=null)
//			{
//				for(int i=0; i<email.getReceivers().length; i++)
//				{
//					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recs[i]));
//				}
//			}
//			String[] ccs = email.getCcs();
//			if(ccs!=null)
//			{
//				for(int i=0; i<ccs.length; i++)
//				{
//					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccs[i]));
//				}
//			}
//			String[] bccs = email.getBccs();
//			if(bccs!=null)
//			{
//				for(int i=0; i<bccs.length; i++)
//				{
//					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccs[i]));
//				}
//			}
//			
////			Transport tr;
//			if(account.isSsl())
//			{
//				tr	= sess.getTransport("smtps");
//			}
//			else
//			{
//				tr	= sess.getTransport("smtp");
//			}
//			
//			if(account.getSmtpPort()!=null)
//			{
//				System.out.println("connect: "+account.getSmtpHost()+" "+account.getSmtpPort()+" "+account.getUser()+" "+account.getPassword());
//				tr.connect(account.getSmtpHost(), account.getSmtpPort().intValue(), account.getUser(), account.getPassword());
//			}
//			else
//			{
//				System.out.println("connect: "+account.getSmtpHost()+" "+account.getUser()+" "+account.getPassword());
//				tr.connect(account.getSmtpHost(), account.getUser(), account.getPassword());
//			}
//			
//			message.saveChanges();
//			tr.sendMessage(message, message.getAllRecipients());
//			tr.close();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
		
		return ret;
	}

	/**
	 *  Subscribe for email.
	 *  @param filter The filter.
	 *  @param account The email account.
	 */
	public ISubscriptionIntermediateFuture<Email> subscribeForEmail(IFilter<Email> filter, EmailAccount account)
	{
		final SubscriptionIntermediateFuture<Email> ret = new SubscriptionIntermediateFuture<Email>();
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
		
		ret.addIntermediateResult(new Email());
		
		addSubscription(ret, new SubscriptionInfo(filter, account));
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
		System.out.println("checking for new mails");
		
		if(subscriptions!=null)
		{
			for(SubscriptionIntermediateFuture<Email> fut: subscriptions.keySet())
			{
				try
				{
					SubscriptionInfo si = subscriptions.get(fut);
					List<Email> emails = si.getNewEmails();
					if(emails!=null && emails.size()>0)
					{
						for(Email email: emails)
						{
							fut.addIntermediateResult(email);
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
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
			});
		}

		// In any case invoke start to immediately check for new email
		receive.startBehavior().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("receive ended");
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("execep: "+exception);
				exception.printStackTrace();
			}
		});
		
		return receive;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtps.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
        
		Session sess = Session.getDefaultInstance(props, null);
		sess.setDebug(true);

		try
		{
			MimeMessage message = new MimeMessage(sess);
			message.setFrom(new InternetAddress("jadexagent"));
			message.setContent("test", "text/ plain");
			message.setSubject("test");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("braubach@informatik.uni-hamburg.de"));
			
			Transport tr;
			tr	= sess.getTransport("smtp");
			tr.connect(EmailAccount.TEST_ACCOUNT.getSmtpHost(), EmailAccount.TEST_ACCOUNT.getSmtpPort().intValue(), 
				EmailAccount.TEST_ACCOUNT.getUser(), EmailAccount.TEST_ACCOUNT.getPassword());
//			tr.connect(account.getSmtpHost(), account.getUser(), account.getPassword());
			
			message.saveChanges();
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
//		Properties props = System.getProperties();
//		props.setProperty("mail.store.protocol", "imaps");
//		try
//		{
//			Session session = Session.getDefaultInstance(props, null);
//			final Store store = session.getStore("imaps");
//			store.connect("imap.gmail.com", "jadexagent@gmail.com", "***REMOVED***");
//			
//			Folder f = store.getFolder("inbox");
//			f.open(Folder.READ_ONLY);
//
//			f.addMessageCountListener(new MessageCountListener()
//			{
//				public void messagesRemoved(MessageCountEvent e)
//				{
//					System.out.println("msg removed: "+e);
//				}
//				
//				public void messagesAdded(MessageCountEvent e)
//				{
//					System.out.println("msg added: "+e);
//				}
//			});
//			
//			if(f instanceof IMAPFolder)
//			{
//				IMAPFolder imf = (IMAPFolder)f;
//				imf.idle();
//			}
////				else if(f instanceof POP3Folder)
////				{
////					POP3Folder ptf = (POP3Folder)f;
////					ptf.s
////				}
//			
//			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
//			Message messages[] = f.search(ft);
//
//			System.out.println("new msg: "+SUtil.arrayToString(messages));
//
//	        f.close(false);        
//	    } 
//		catch(Exception e) 
//		{
//			e.printStackTrace();
//	    }
	}
}
