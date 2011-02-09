package jadex.simulation.analysis.common.workflow.tasks.general;

import jadex.simulation.analysis.common.events.ATaskListener;

import javax.swing.JComponent;

public interface IATaskView extends ATaskListener
{
	public abstract JComponent getComponent();

}