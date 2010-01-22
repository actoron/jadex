package jadex.wfms.bdi.client.cap;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.IWorkitem;

public class AddProcessModelPlan extends Plan
{
	public void body()
	{
		final Action pmAdded = (Action) getBeliefbase().getBelief("add_process_model_controller").getFact();
		final String modelName = (String) getParameter("model_name").getValue();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				pmAdded.actionPerformed(new ActionEvent(modelName, 0, null));
			}
		});
	}
}
