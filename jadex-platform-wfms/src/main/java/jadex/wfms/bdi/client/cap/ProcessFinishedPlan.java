package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

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
