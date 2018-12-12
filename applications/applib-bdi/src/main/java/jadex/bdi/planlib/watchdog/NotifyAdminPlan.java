package jadex.bdi.planlib.watchdog;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Notify the human admin of some agent in case of problems.
 */
public class NotifyAdminPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		ObservationDescription desc = (ObservationDescription)getParameter("description").getValue();
		boolean success = false;

		ContactData[] contacts = desc.getContacts();

		for(int i=0; i<contacts.length; i++)
		{
			if(contacts[i].getEmail()!=null && getBeliefbase().getBelief("emailaccount").getFact()!=null)
			{
				try
				{
					IGoal sendemail = createGoal("send_email");
					sendemail.getParameter("subject").setValue("Watchdog warning message.");
					sendemail.getParameter("content").setValue("Application problem with: "+desc.getComponentIdentifier());
					sendemail.getParameterSet("receivers").addValue(contacts[i].getEmail());
					dispatchSubgoalAndWait(sendemail);
					success = true;
				}
				catch(GoalFailureException e)
				{
					getLogger().info("Could not notify via email: "+contacts[i].getEmail());
				}
			}

			if(contacts[i].getIcq()!=null && getBeliefbase().getBelief("icqaccount").getFact()!=null)
			{
				try
				{
					IGoal sendim = createGoal("send_im");
					sendim.getParameter("content").setValue("Application problem with: " + desc.getComponentIdentifier());
					sendim.getParameterSet("receivers").addValue(contacts[i].getIcq());
					dispatchSubgoalAndWait(sendim);
					success = true;
				}
				catch(GoalFailureException e)
				{
					getLogger().info("Could not notify via im: "+contacts[i].getIcq());
				}
			}
			
			if(contacts[i].getPhone()!=null)
			{
				try
				{
					IGoal sendsms = createGoal("send_sms");
					sendsms.getParameter("content").setValue("Application problem with: "+desc.getComponentIdentifier());
					sendsms.getParameterSet("receivers").addValue(contacts[i].getPhone());
					dispatchSubgoalAndWait(sendsms);
					success = true;
				}
				catch(GoalFailureException e)
				{
					getLogger().info("Could not notify via sms: "+contacts[i].getEmail());
				}
			}
		}

		// Fail if no notifications succeeded.
		if(!success)
			fail();
	}
}
