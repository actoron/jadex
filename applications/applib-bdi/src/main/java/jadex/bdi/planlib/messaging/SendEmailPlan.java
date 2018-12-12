package jadex.bdi.planlib.messaging;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jadex.bdiv3x.runtime.Plan;


/**
 *  Send an email.
 */
public class SendEmailPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Account settings.
		EmailAccount account = (EmailAccount)getParameter("account").getValue();
		if(account==null)
			fail();
		
		// Message settings.
		String	content	= (String)getParameter("content").getValue();
		String	subject	= (String)getParameter("subject").getValue();
		String[]	receivers	= (String[])getParameterSet("receivers").getValues();
		String[]	ccs	= (String[])getParameterSet("ccs").getValues();
		String[]	bccs	= (String[])getParameterSet("bccs").getValues();
		
		// Todo: attachments?
//		Object[]	attachments	= getParameterSet("attachments").getValues(); // todo:
		
		Properties props = new Properties();
		props.setProperty("mail.smtp.auth","true");
		props.setProperty("mail.smtps.auth","true");
		Session sess= Session.getDefaultInstance(props, null);
		sess.setDebug(true);

		try
		{
			MimeMessage message = new MimeMessage(sess);
			message.setFrom(new InternetAddress(account.getSender()));
			message.setContent(content, "text/ plain");
			message.setSubject(subject);
			for(int i=0; i<receivers.length; i++)
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(receivers[i]));
			for(int i=0; i<ccs.length; i++)
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccs[i]));
			for(int i=0; i<bccs.length; i++)
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccs[i]));

			Transport tr;
			if(account.isSsl())
				tr	= sess.getTransport("smtps");
			else
				tr	= sess.getTransport("smtp");

			if(account.getPort()!=null)
				tr.connect(account.getHost(), account.getPort().intValue(), account.getUser(), account.getPassword());
			else
				tr.connect(account.getHost(), account.getUser(), account.getPassword());
			
			message.saveChanges(); // don't forget this
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
		}
		catch(MessagingException e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
