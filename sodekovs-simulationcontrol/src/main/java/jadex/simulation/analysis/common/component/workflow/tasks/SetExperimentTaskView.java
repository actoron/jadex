package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.simulation.analysis.common.component.workflow.tasks.general.ATaskView;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class SetExperimentTaskView extends ATaskView
{
	@Override
	public void taskEventOccur(ATaskEvent event)
	{
		super.taskEventOccur(event);
		SetExperimentTask task = (SetExperimentTask) event.getSource();
		if (event.getCommand().equals(AConstants.TASK_LÄUFT))
		{
			component = ADataViewFactory.createView(task.getExperiment()).getComponent();
		}
		component.revalidate();
		component.repaint();
	}
}
