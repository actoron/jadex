package jadex.simulation.analysis.common.workflow.tasks.general;

import jadex.simulation.analysis.common.events.ATaskEvent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ABasicTaskView implements IATaskView
{
	protected JComponent component;
	protected IATask displayedTask;
	protected Object mutex = new Object();
	
	public ABasicTaskView(IATask task)
	{
		displayedTask = task;
		task.addTaskListener(this);
		component = new JPanel(new GridBagLayout());
		JComponent freePanel = new JPanel();
		component.add(freePanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
	}	
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public void taskEventOccur(ATaskEvent event)
	{
		//omit
	}

}
