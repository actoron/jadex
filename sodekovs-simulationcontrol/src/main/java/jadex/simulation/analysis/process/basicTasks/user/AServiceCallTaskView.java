package jadex.simulation.analysis.process.basicTasks.user;

import jadex.simulation.analysis.process.basicTasks.ATaskView;
import jadex.simulation.analysis.process.basicTasks.IATask;
import jadex.simulation.analysis.process.basicTasks.IATaskView;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;

public class AServiceCallTaskView extends ATaskView implements IATaskView
{
	public AServiceCallTaskView(IATask taskObject)
	{
		super(taskObject);
	}
	
	public void addServiceGUI(JComponent component, GridBagConstraints constrain)
	{
		this.component.add(component, constrain);
	}
}
