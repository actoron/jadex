package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class AddActivityPlan extends Plan
{
	public void body()
	{
		final Action acAdded = (Action) getBeliefbase().getBelief("add_activity_controller").getFact();
		if (acAdded == null)
			return;
		final IClientActivity act = (IClientActivity) getParameter("activity").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				acAdded.actionPerformed(new ActionEvent(act, 0, null));
			}
		});
	}
}
