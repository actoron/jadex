package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class RemoveProcessModelPlan extends Plan
{
	public void body()
	{
		final Action pmRemoved = (Action) getBeliefbase().getBelief("remove_process_model_controller").getFact();
		final String modelName = (String) getParameter("model_name").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				pmRemoved.actionPerformed(new ActionEvent(modelName, 0, null));
			}
		});
	}
}
