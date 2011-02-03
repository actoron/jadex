package jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view;

import javax.swing.JComponent;

public interface IBpmnServiceView
{

	public abstract void init();

	// public abstract void viewModel(final IAModel model);
	//
	// public abstract void viewExperimentalFrame(final IAExperimentalFrame frame);
	//
	// public abstract void experimentieren(final JComponent comp);
	//
	// public abstract void present(final IAExperimentJob job);

	public abstract void viewTask(final JComponent comp);

}