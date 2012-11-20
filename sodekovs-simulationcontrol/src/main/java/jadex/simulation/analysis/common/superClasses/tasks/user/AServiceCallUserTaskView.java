package jadex.simulation.analysis.common.superClasses.tasks.user;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.tasks.ATaskView;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jadex.commons.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * A tasks with a user interaction. OK Option
 * @author 5Haubeck
 *
 */
public class AServiceCallUserTaskView extends ATaskView implements IATaskView
{
	public AServiceCallUserTaskView(IATask taskObject)
	{
		super(taskObject);
	}
	
	public IFuture startGUI()
	{
		final Future ret = new Future();
		final JButton button = new JButton("Ausf�hrung fortsetzten");
		button.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ret.setResult(displayedTask);
				button.setEnabled(false);
//				try
//				{
//					parent.setClosed(true);
//				}
//				catch (PropertyVetoException e1)
//				{
//
//				}
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
