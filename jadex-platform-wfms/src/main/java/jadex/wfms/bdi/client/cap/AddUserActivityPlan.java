package jadex.wfms.bdi.client.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class AddUserActivityPlan extends Plan
{
	public void body()
	{
		final Action wiAdded = (Action) getBeliefbase().getBelief("add_user_activity_controller").getFact();
		final IClientActivity act = (IClientActivity) getParameter("activity").getValue();
		final String userName = (String) getParameter("user_name").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				wiAdded.actionPerformed(new ActionEvent(act, 0, userName));
			}
		});
	}
}
