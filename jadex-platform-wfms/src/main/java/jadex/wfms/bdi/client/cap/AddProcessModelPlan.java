package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.Action;

public class AddProcessModelPlan extends Plan
{
	public void body()
	{
		System.out.println("Add Process Model");
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
