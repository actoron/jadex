package jadex.wfms.bdi.client.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class RemoveActivityPlan extends Plan
{
	public void body()
	{
		final Action wiAdded = (Action) getBeliefbase().getBelief("remove_activity_controller").getFact();
		final IClientActivity act = (IClientActivity) getParameter("activity").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				wiAdded.actionPerformed(new ActionEvent(act, 0, null));
			}
		});
	}
}
