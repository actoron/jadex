package jadex.bdi.planlib.messaging;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Send an instant message.
 */
public class SendXMPPPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Account settings.
		IMAccount account = (IMAccount)getParameter("account").getValue();
		if(account==null)
			fail();
		
		// Message settings.
		String	content	= (String)getParameter("content").getValue();
		String[] receivers	= (String[])getParameterSet("receivers").getValues();
		
		try
		{
			XMPPConnection connection = new XMPPConnection("jabber.ccc.de");
			connection.connect();
			connection.login(account.getId(), account.getPassword());
			
			for(int i=0; i<receivers.length; i++) 
			{
				Chat chat = connection.getChatManager().createChat(receivers[i], new MessageListener()
				{
				    public void processMessage(Chat chat, Message message) 
				    {
				        System.out.println("Received message: " + message);
				    }
				});
				chat.sendMessage(content);
			}
			
			connection.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
	
	/**
	 * Main for testing. 
	 * /
	public static void main(String[] args)
	{
		
		try
		{
			XMPPConnection connection = new XMPPConnection("jabber.ccc.de");
			connection.connect();
			connection.login("jadexagent", "jadexagent");
			
			Chat chat = connection.getChatManager().createChat("larslars@jabber.ccc.de", new MessageListener()
			{
			    public void processMessage(Chat chat, Message message) 
			    {
			        System.out.println("Received message: " + message);
			    }
			});
			chat.sendMessage("hello");
			
			connection.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
}
