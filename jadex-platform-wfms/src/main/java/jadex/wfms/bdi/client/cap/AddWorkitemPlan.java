package jadex.wfms.bdi.client.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IWorkitem;

public class AddWorkitemPlan extends Plan
{
	public void body()
	{
		final Action wiAdded = (Action) getBeliefbase().getBelief("add_workitem_controller").getFact();
		if (wiAdded == null)
			return;
		final IWorkitem wi = (IWorkitem) getParameter("workitem").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				wiAdded.actionPerformed(new ActionEvent(wi, 0, null));
			}
		});
	}
}
