package jadex.wfms.simulation.stateholder.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import jadex.wfms.simulation.stateholder.BooleanStateSet;

public class BooleanStatePanel extends JPanel implements IStatePanel
{
	private JCheckBox falseBox;
	private JCheckBox trueBox;
	
	private BooleanStateSet stateSet;
	
	public BooleanStatePanel(final BooleanStateSet stateSet)
	{
		this.stateSet = stateSet;
		falseBox = new JCheckBox();
		falseBox.setSelected(stateSet.hasState(Boolean.FALSE));
		falseBox.setAction(new AbstractAction("Include \"False\" state")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (((JCheckBox) e.getSource()).isSelected())
					stateSet.addState(Boolean.FALSE);
				else
					stateSet.removeState(Boolean.FALSE);
			}
		});
		
		add(falseBox);
		
		trueBox = new JCheckBox();
		trueBox.setSelected(stateSet.hasState(Boolean.TRUE));
		trueBox.setAction(new AbstractAction("Include \"True\" state")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (((JCheckBox) e.getSource()).isSelected())
					stateSet.addState(Boolean.TRUE);
				else
					stateSet.removeState(Boolean.TRUE);
			}
		});
		add(trueBox);
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		falseBox.setSelected(stateSet.hasState(Boolean.FALSE));
		trueBox.setSelected(stateSet.hasState(Boolean.TRUE));
	}
}
