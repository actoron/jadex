package jadex.wfms.simulation.gui;

import jadex.wfms.simulation.ActivationConstraint;
import jadex.wfms.simulation.stateset.gui.IStatePanel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TaskActivationPanel extends JPanel implements IStatePanel
{
	private static final int MAX_COUNT = 99999;
	
	private JCheckBox activationCheck;
	private JSpinner activationCount;
	
	private SimulationWindow simWindow;
	
	private String taskName;
	
	public TaskActivationPanel(String taskName, SimulationWindow simWdw)
	{
		this.simWindow = simWdw;
		this.taskName = taskName;
		activationCheck = new JCheckBox();
		activationCount = new JSpinner(new SpinnerNumberModel(1, 1, MAX_COUNT, 1));
		
		//BooleanStateSet stateSet =  null;
		
		JPanel activationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		activationCheck.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					if (((JCheckBox) e.getSource()).isSelected())
						simWindow.getSelectedScenario().setTaskValidationInfo(TaskActivationPanel.this.taskName,
								new ActivationConstraint(ActivationConstraint.MODE_EQUALS, ((Integer) activationCount.getValue()).intValue()));
					else
						simWindow.getSelectedScenario().removeTaskValidationInfo(TaskActivationPanel.this.taskName);
					refreshPanel();
				}
			}
		});
		
		activationCount.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
					simWindow.getSelectedScenario().setTaskValidationInfo(TaskActivationPanel.this.taskName,
							new ActivationConstraint(ActivationConstraint.MODE_EQUALS, ((Integer) activationCount.getValue()).intValue()));
			}
		});
		
		activationPanel.add(activationCheck);
		JLabel label = new JLabel("Task must execute ");
		activationPanel.add(label);
		activationPanel.add(activationCount);
		label = new JLabel(" time(s).");
		activationPanel.add(label);
		
		add(activationPanel);
		
		refreshPanel();
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		if (simWindow.getSelectedScenario() != null)
		{
			activationCheck.setEnabled(true);
			ActivationConstraint c = simWindow.getSelectedScenario().getTaskValidationInfo(taskName);
			if (c == null)
			{
				activationCheck.setSelected(false);
				activationCount.setValue(Integer.valueOf(1));
			}
			else
			{
				activationCheck.setSelected(true);
				activationCount.setValue(Integer.valueOf(c.getActivationCount()));
			}
			activationCount.setEnabled(activationCheck.isSelected());
		}
		else
		{
			activationCheck.setSelected(false);
			activationCheck.setEnabled(false);
			activationCount.setValue(Integer.valueOf(1));
			activationCount.setEnabled(false);
		}
	}
}
