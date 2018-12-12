package jadex.bdi.planlib.messaging;

import jadex.bdiv3x.runtime.Plan;

//import org.jcq2k.MessagingNetwork;
//import org.jcq2k.MessagingNetworkException;
//import org.jcq2k.icq2k.ICQ2KMessagingNetwork;

/**
 *  Send an instant message.
 */
public class SendICQPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// No maven ICQ library available. Runs ok with jcq. 
		fail();
		
		// Account settings.
		IMAccount account = (IMAccount)getParameter("account").getValue();
		if(account==null)
			fail();
		
		// Message settings.
//		String	content	= (String)getParameter("content").getValue();
//		String[] receivers	= (String[])getParameterSet("receivers").getValues();
		
//		AIM aim = new AIM(account.getID(), account.getPassword());
//		
//		if(!aim.isLoggedin()) fail(aim.getError(), null);
//		
//		for(int i=receivers.length; i>0;) {
//			aim.send(receivers[--i], content);
//		}
//		aim.logout();
		
//		OscarConnection con = new OscarConnection("toc.oscar.aol.com", 9898, account.getId(), account.getPassword()); 
//		for(int i=0; i<receivers.length; i++) 
//		{
//			OscarInterface.sendBasicMessage(con, receivers[i], content);
//		}
		
//		try
//		{
//			ICQ2KMessagingNetwork nw = new ICQ2KMessagingNetwork();
//			nw.login(account.getId(), account.getPassword(), null, MessagingNetwork.STATUS_ONLINE);
//			for(int i=0; i<receivers.length; i++) 
//			{
//				nw.sendMessage(account.getId(), receivers[i], content);
//			}
//			nw.logout(account.getId());
//		}
//		catch(MessagingNetworkException e)
//		{
//			e.printStackTrace();
//			fail(e);
//		}
	}
}
