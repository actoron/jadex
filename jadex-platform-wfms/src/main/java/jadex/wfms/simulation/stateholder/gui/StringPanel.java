package jadex.wfms.simulation.stateholder.gui;

import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateholder.StringStateSet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class StringPanel extends JPanel implements IStatePanel
{
	private JList stringList;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public StringPanel(String tskName, String paramtrName, SimulationWindow simWdw)
	{
		this.taskName = tskName;
		this.parameterName = paramtrName;
		this.simWindow = simWdw;
		setLayout(new GridBagLayout());
		
		stringList = new JList(new DefaultListModel());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(stringList, c);
		
		refreshPanel();
		
		JButton addButton = new JButton(new AbstractAction("Add String...")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					String inputString = (String) JOptionPane.showInputDialog(StringPanel.this,
							  "Please enter a new string:",
							  "New String",
							  JOptionPane.PLAIN_MESSAGE,
							  null,
							  null,
							  null);
					if (inputString == null)
						return;
					
					((StringStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).addString(inputString);
				}
				
				refreshPanel();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(addButton, c);
		
		JButton removeButton = new JButton(new AbstractAction("Remove String")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
					((StringStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).removeString((String) stringList.getSelectedValue());
				refreshPanel();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(removeButton, c);
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		((DefaultListModel) stringList.getModel()).clear();
		if (simWindow.getSelectedScenario() != null)
		{
			List strings = ((StringStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).getStrings();
			for (Iterator it = strings.iterator(); it.hasNext(); )
				((DefaultListModel) stringList.getModel()).addElement(it.next());
		}
	}
}
