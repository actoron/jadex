package jadex.bdi.testcases.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import jadex.base.test.TestReport;
import jadex.bdi.planlib.messaging.EmailAccount;
import jadex.bdi.planlib.messaging.IMAccount;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test the messaging capability.
 */
public class MessagingTestPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void	body()
	{
		testXMPP();
		
//		testICQ();
		
		testEMail();
	}

	/**
	 *  Test sending of email.
	 */
	protected void testEMail()
	{
		String	mailhost	= null;
		String	mailport	= null;
		String	mailuser	= null;
		String	mailpass	= null;
		String	mailsender	= null;
		String	mailreceivers	= null;
		
		File	file	= new File("./messagingtest.email.properties");
		boolean	haveprops;
		Properties	props	= new Properties();
		try
		{
			InputStream	is	= new FileInputStream(file);
			props.load(is);
			is.close();
			mailhost	= props.getProperty("mailhost");
			mailport	= props.getProperty("mailport");
			mailuser	= props.getProperty("mailuser");
			mailpass	= props.getProperty("mailpass");
			mailsender	= props.getProperty("mailsender");
			mailreceivers	= props.getProperty("mailreceivers");
			haveprops	= true;
		}
		catch(Exception e)
		{
			haveprops	= false;
		}
		
		TestReport tr1 = new TestReport("#1", "Test sending email.");
		if(haveprops)
		{
			try
			{
				EmailAccount eacc = new EmailAccount(mailhost, Integer.valueOf(mailport),
					mailuser, mailpass, mailsender, false);
				
				IGoal sendem = createGoal("send_email");
				sendem.getParameter("account").setValue(eacc);
				sendem.getParameter("subject").setValue("winning notification");
				sendem.getParameter("content").setValue("you have won, because a jadex agent has sent you its greetings ;-)");
				StringTokenizer	stok	= new StringTokenizer(mailreceivers, ",");
				while(stok.hasMoreTokens())
					sendem.getParameterSet("receivers").addValue(stok.nextToken().trim());
				dispatchSubgoalAndWait(sendem);
				tr1.setSucceeded(true);
			}
			catch(Exception e)
			{
				tr1.setReason("Exception occurred: "+e);
			}
		}
		else
		{
			try
			{
				props.setProperty("mailhost", "<address of mail host>");
				props.setProperty("mailport", "<port of mail host, e.g. 25>");
				props.setProperty("mailuser", "<user name of mail account>");
				props.setProperty("mailpass", "<password for user>");
				props.setProperty("mailsender", "<email address of mail account>");
				props.setProperty("mailreceivers", "<comma-separated list of email addresses to send to>");
				OutputStream	os	= new FileOutputStream(file);
				props.store(os, "Account settings used by jadex.bdi.testcases.MessagingTestPlan.\n#Please edit if you want to make the test case work.");
				os.close();
				tr1.setReason("No accountsettings found. Please edit "+file.getAbsolutePath());
			}
			catch(Exception e)
			{
				tr1.setReason("Error accessing settings: "+e+". Please create "+file.getAbsolutePath());
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr1);
	}
	
	/**
	 *  Test sending an ICQ message.
	 */
	protected void testICQ()
	{
		String	icqnumber	= null;
		String	icqpass	= null;
		String	icqreceivers	= null;
		
		File	file	= new File("./messagingtest.icq.properties");
		boolean	haveprops;
		Properties	props	= new Properties();
		try
		{
			InputStream	is	= new FileInputStream(file);
			props.load(is);
			is.close();
			icqnumber	= props.getProperty("icqnumber");
			icqpass	= props.getProperty("icqpass");
			icqreceivers	= props.getProperty("icqreceivers");
			haveprops	= true;
		}
		catch(Exception e)
		{
			haveprops	= false;
		}

		TestReport tr2 = new TestReport("#2", "Test sending instant message.");
		if(haveprops)
		{
			try
			{
				IMAccount iacc = new IMAccount(icqnumber, icqpass);
		
				IGoal sendim = createGoal("send_icq");
				sendim.getParameter("account").setValue(iacc);
				sendim.getParameter("content").setValue("hi from a jadex agent");
				StringTokenizer	stok	= new StringTokenizer(icqreceivers, ",");
				while(stok.hasMoreTokens())
					sendim.getParameterSet("receivers").addValue(stok.nextToken().trim());

				dispatchSubgoalAndWait(sendim);
				tr2.setSucceeded(true);
			}
			catch(Exception e)
			{
				tr2.setReason("Exception occurred: "+e);
			}
		}
		else
		{
			try
			{
				props.setProperty("icqnumber", "<icq account number>");
				props.setProperty("icqpass", "<password for icq account>");
				props.setProperty("icqreceivers", "<comma-separated list of icq numbers to send to>");
				OutputStream	os	= new FileOutputStream(file);
				props.store(os, "Account settings used by jadex.bdi.testcases.MessagingTestPlan.\n#Please edit if you want to make the test case work.");
				os.close();
				tr2.setReason("No accountsettings found. Please edit "+file.getAbsolutePath());
			}
			catch(Exception e)
			{
				tr2.setReason("Error accessing settings: "+e+". Please create "+file.getAbsolutePath());
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr2);
	}
	
	/**
	 *  Test sending an jabber message.
	 */
	protected void testXMPP()
	{
		String	xmppnumber	= null;
		String	xmpppass	= null;
		String	xmppreceivers	= null;
		
		File	file	= new File("./messagingtest.xmpp.properties");
		boolean	haveprops;
		Properties	props	= new Properties();
		try
		{
			InputStream	is	= new FileInputStream(file);
			props.load(is);
			is.close();
			xmppnumber	= props.getProperty("xmppnumber");
			xmpppass	= props.getProperty("xmpppass");
			xmppreceivers	= props.getProperty("xmppreceivers");
			haveprops	= true;
		}
		catch(Exception e)
		{
			haveprops	= false;
		}

		TestReport tr2 = new TestReport("#2", "Test sending instant message.");
		if(haveprops)
		{
			try
			{
				IMAccount iacc = new IMAccount(xmppnumber, xmpppass);
		
				IGoal sendim = createGoal("send_xmpp");
				sendim.getParameter("account").setValue(iacc);
				sendim.getParameter("content").setValue("hi from a jadex agent");
				StringTokenizer	stok	= new StringTokenizer(xmppreceivers, ",");
				while(stok.hasMoreTokens())
					sendim.getParameterSet("receivers").addValue(stok.nextToken().trim());

				dispatchSubgoalAndWait(sendim);
				tr2.setSucceeded(true);
			}
			catch(Exception e)
			{
				tr2.setReason("Exception occurred: "+e);
			}
		}
		else
		{
			try
			{
				props.setProperty("xmppnumber", "<xmpp account number>");
				props.setProperty("xmpppass", "<password for xmpp account>");
				props.setProperty("xmppreceivers", "<comma-separated list of xmpp numbers to send to>");
				OutputStream	os	= new FileOutputStream(file);
				props.store(os, "Account settings used by jadex.bdi.testcases.MessagingTestPlan.\n#Please edit if you want to make the test case work.");
				os.close();
				tr2.setReason("No accountsettings found. Please edit "+file.getAbsolutePath());
			}
			catch(Exception e)
			{
				tr2.setReason("Error accessing settings: "+e+". Please create "+file.getAbsolutePath());
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr2);
	}
}
