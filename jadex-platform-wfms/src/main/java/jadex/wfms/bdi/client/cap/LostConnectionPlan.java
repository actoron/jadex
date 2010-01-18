package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class LostConnectionPlan extends Plan
{
	public void body()
	{
		final Action lcAction = (Action) getBeliefbase().getBelief("lost_connection_controller").getFact();
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				lcAction.actionPerformed(new ActionEvent(LostConnectionPlan.this, 0, null));
			}
		});
	}
}
