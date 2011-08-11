package jadex.simulation.analysis.process.basicTasks;

import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ASubProcessTaskView extends ATaskView implements IATaskView
{
	public ASubProcessTaskView(IATask taskObject)
	{
		super(taskObject);
	}
	
	public void init(ASubProcessView component)
	{
		this.component.add(component, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
	}
}
