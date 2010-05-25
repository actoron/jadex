package jadex.wfms.bdi.client.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;

public class ProcessFinishedPlan extends Plan
{
	public void body()
	{
		final Action procFin = (Action) getBeliefbase().getBelief("process_finished_controller").getFact();
		if (procFin == null)
			return;
		final String id = (String) getParameter("instance_id").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				procFin.actionPerformed(new ActionEvent(id, 0, null));
			}
		});
	}
}
