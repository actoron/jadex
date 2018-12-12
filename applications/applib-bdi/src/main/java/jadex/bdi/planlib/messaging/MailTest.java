package jadex.bdi.planlib.messaging;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 *  Test class for java mail API.
 */
public class MailTest
{
	/**
	 *  Main method for testing.
	 * @throws IOException
	 */
	public static void	main(String[] args) throws MessagingException, IOException
	{
		Session	session	= Session.getDefaultInstance(System.getProperties());

		URLName	url	= new URLName("pop3", "localhost", 110, "", "test@mydomain.com", "test");
		Store	store	= session.getStore(url);
		store.connect();

		Folder	inbox	= store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);

		if(inbox.getMessageCount()>0)
		{
			for(int i=0; i<inbox.getMessageCount(); i++)
			{
				Message	message	= inbox.getMessage(i+1);
				System.out.println(message.getContent());
			}
		}
		else
		{
			System.out.println("No new messages.");
		}
	}
}
