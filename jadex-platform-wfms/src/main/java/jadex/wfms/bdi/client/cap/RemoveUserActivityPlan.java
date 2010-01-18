package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class RemoveUserActivityPlan extends Plan
{
	public void body()
	{
		final Action wiRemoved = (Action) getBeliefbase().getBelief("remove_user_activity_controller").getFact();
		final IClientActivity act = (IClientActivity) getParameter("activity").getValue();
		final String userName = (String) getParameter("user_name").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				wiRemoved.actionPerformed(new ActionEvent(act, 0, userName));
			}
		});
	}
}
