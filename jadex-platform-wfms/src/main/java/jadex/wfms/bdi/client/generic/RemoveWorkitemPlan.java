package jadex.wfms.bdi.client.generic;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IWorkitem;

public class RemoveWorkitemPlan extends Plan
{
	public void body()
	{
		final Action wiRemoved = (Action) getBeliefbase().getBelief("remove_workitem_controller").getFact();
		final IWorkitem wi = (IWorkitem) getParameter("workitem").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				wiRemoved.actionPerformed(new ActionEvent(wi, 0, null));
			}
		});
	}
}
