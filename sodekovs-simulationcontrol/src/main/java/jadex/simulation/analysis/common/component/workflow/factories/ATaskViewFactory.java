package jadex.simulation.analysis.common.component.workflow.factories;

import jadex.simulation.analysis.common.component.workflow.tasks.ExecuteExperimentTask;
import jadex.simulation.analysis.common.component.workflow.tasks.ExecuteExperimentTaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.PresentExperimentTask;
import jadex.simulation.analysis.common.component.workflow.tasks.PresentExperimentTaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.SetExperimentTask;
import jadex.simulation.analysis.common.component.workflow.tasks.SetExperimentTaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.SetModelTask;
import jadex.simulation.analysis.common.component.workflow.tasks.SetModelTaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATaskView;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATaskView;

public class ATaskViewFactory
{
	public static IATaskView createView(IATask task)
	{
		IATaskView view;
		if (task instanceof SetModelTask)
		{
			view = createSetModelView((SetModelTask)task);
		} else if (task instanceof SetExperimentTask)
		{
			view = createSetExperimentView((SetExperimentTask)task);
		} else if (task instanceof PresentExperimentTask)
		{
			view = createPresentExperimentView((PresentExperimentTask)task);
		} else if (task instanceof ExecuteExperimentTask)
		{
			view = createExecuteExperimentView((ExecuteExperimentTask)task);
		}  else
		{
			view = createATaskView(task);
		}
		return view;
	}

	private static IATaskView createATaskView(IATask task)
	{
		ATaskView view = new ATaskView();
		view.setDisplayedObject(task);
		return view;
	}

	private static IATaskView createExecuteExperimentView(ExecuteExperimentTask task)
	{
		ExecuteExperimentTaskView view = new ExecuteExperimentTaskView();
		view.setDisplayedObject(task);
		return view;
	}

	private static IATaskView createPresentExperimentView(PresentExperimentTask task)
	{
		PresentExperimentTaskView view = new PresentExperimentTaskView();
		view.setDisplayedObject(task);
		return view;
	}

	private static IATaskView createSetExperimentView(SetExperimentTask task)
	{
		SetExperimentTaskView view = new SetExperimentTaskView();
		view.setDisplayedObject(task);
		return view;
	}

	private static SetModelTaskView createSetModelView(SetModelTask task)
	{
		SetModelTaskView view = new SetModelTaskView();
		view.setDisplayedObject(task);
		return view;
	}
	
	public static Class getViewerClass(IATask task)
	{
		Class view;
		if (task instanceof SetModelTask)
		{
			view = SetModelTaskView.class;
		} else if (task instanceof SetExperimentTask)
		{
			view =  SetExperimentTaskView.class;
		} else if (task instanceof PresentExperimentTask)
		{
			view =  PresentExperimentTaskView.class;
		} else if (task instanceof ExecuteExperimentTask)
		{
			view =  ExecuteExperimentTaskView.class;
		}  else
		{
			view = ATaskView.class;
		}
		return view;
	}
}
