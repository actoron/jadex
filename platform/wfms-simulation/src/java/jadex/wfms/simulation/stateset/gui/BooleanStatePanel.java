package jadex.wfms.simulation.stateset.gui;

import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.BooleanStateSet;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class BooleanStatePanel extends JPanel implements IStatePanel
{
	private JCheckBox falseBox;
	private JCheckBox trueBox;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public BooleanStatePanel(String tskName, String paramtrName, SimulationWindow simWdw)
	{
		this.taskName = tskName;
		this.parameterName = paramtrName;
		this.simWindow = simWdw;
		falseBox = new JCheckBox();
		
		BooleanStateSet stateSet =  null;
		if (simWindow.getSelectedScenario() != null)
			stateSet = (BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName);
		
		if (stateSet != null)
			falseBox.setSelected(stateSet.hasState(Boolean.FALSE));
		falseBox.setAction(new AbstractAction("Include \"False\" state")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					if (((JCheckBox) e.getSource()).isSelected())
						((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addState(Boolean.FALSE);
					else
						((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeState(Boolean.FALSE);
				}
			}
		});
		
		add(falseBox);
		
		trueBox = new JCheckBox();
		if (stateSet != null)
			falseBox.setSelected(stateSet.hasState(Boolean.TRUE));
		trueBox.setAction(new AbstractAction("Include \"True\" state")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					if (((JCheckBox) e.getSource()).isSelected())
						((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addState(Boolean.TRUE);
					else
						((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeState(Boolean.TRUE);
				}
			}
		});
		add(trueBox);
		
		refreshPanel();
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		if (simWindow.getSelectedScenario() != null)
		{
			falseBox.setEnabled(true);
			trueBox.setEnabled(true);
			falseBox.setSelected(((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).hasState(Boolean.FALSE));
			trueBox.setSelected(((BooleanStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).hasState(Boolean.TRUE));
		}
		else
		{
			falseBox.setSelected(false);
			trueBox.setSelected(false);
			falseBox.setEnabled(false);
			trueBox.setEnabled(false);
		}
	}
}
