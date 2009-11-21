package jadex.wfms.simulation.stateholder.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import jadex.wfms.simulation.stateholder.BooleanStateSet;

public class BooleanStatePanel extends JPanel
{
	public BooleanStatePanel(final BooleanStateSet stateHolder)
	{
		JCheckBox falseBox = new JCheckBox();
		falseBox.setSelected(stateHolder.hasState(Boolean.FALSE));
		falseBox.setAction(new AbstractAction("Include \"False\" state")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (((JCheckBox) e.getSource()).isSelected())
					stateHolder.addState(Boolean.FALSE);
				else
					stateHolder.removeState(Boolean.FALSE);
			}
		});
		
		add(falseBox);
		
		JCheckBox trueBox = new JCheckBox();
		trueBox.setSelected(stateHolder.hasState(Boolean.TRUE));
		trueBox.setAction(new AbstractAction("Include \"True\" state")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (((JCheckBox) e.getSource()).isSelected())
					stateHolder.addState(Boolean.TRUE);
				else
					stateHolder.removeState(Boolean.TRUE);
			}
		});
		add(trueBox);
	}
	
}
