package jadex.simulation.analysis.common.superClasses.tasks.user;

import jadex.simulation.analysis.common.superClasses.tasks.ATaskView;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;

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
