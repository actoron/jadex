package jadex.simulation.analysis.common.component.workflow.defaultView;

import javax.swing.JComponent;

public interface IBpmnComponentView
{

//	public abstract void init();

	// public abstract void viewModel(final IAModel model);
	//
	// public abstract void viewExperimentalFrame(final IAExperiment frame);
	//
	// public abstract void experimentieren(final JComponent comp);
	//
	// public abstract void present(final IAExperimentJob job);

	public void viewTask(String taskName);

}