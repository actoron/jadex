package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.listeners.LogEvent;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class LogEventPlan extends Plan
{
	public void body()
	{
		final Action procFin = (Action) getBeliefbase().getBelief("log_controller").getFact();
		if (procFin == null)
			return;
		final LogEvent event = (LogEvent) getParameter("event").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				procFin.actionPerformed(new ActionEvent(event, 0, null));
			}
		});
	}
}
