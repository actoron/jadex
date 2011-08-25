package jadex.simulation.analysis.process.basicTasks.user;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.process.basicTasks.ATaskView;
import jadex.simulation.analysis.process.basicTasks.IATask;
import jadex.simulation.analysis.process.basicTasks.IATaskView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JComponent;

public class AServiceCallUserTaskView extends ATaskView implements IATaskView
{
	public AServiceCallUserTaskView(IATask taskObject)
	{
		super(taskObject);
	}
	
	public IFuture startGUI()
	{
		final Future ret = new Future();
		final JButton button = new JButton("Ausführung fortsetzten");
		button.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ret.setResult(displayedTask);
				button.setEnabled(false);
				try
				{
					parent.setClosed(true);
				}
				catch (PropertyVetoException e1)
				{

				}
			}
		});
		button.setPreferredSize(new Dimension(100, 20));
		
		this.component.add(button, new GridBagConstraints(0, 5, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		return ret;
	}
	
	public void addServiceGUI(JComponent component, GridBagConstraints constrain)
	{
		this.component.add(component, constrain);
	}
}
