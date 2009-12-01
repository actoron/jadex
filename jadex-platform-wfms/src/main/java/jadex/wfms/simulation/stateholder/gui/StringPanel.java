package jadex.wfms.simulation.stateholder.gui;

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
	private StringStateSet stateHolder;
	
	private JList stringList;
	
	public StringPanel(StringStateSet stateHolder)
	{
		this.stateHolder = stateHolder;
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
				String inputString = (String) JOptionPane.showInputDialog(StringPanel.this,
						  "Please enter a new string:",
						  "New String",
						  JOptionPane.PLAIN_MESSAGE,
						  null,
						  null,
						  null);
				if (inputString == null)
					return;
				
				StringStateSet stateHolder = StringPanel.this.stateHolder;
				stateHolder.addString(inputString);
				
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
				StringPanel.this.stateHolder.removeString((String) stringList.getSelectedValue());
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
		List strings = stateHolder.getStrings();
		for (Iterator it = strings.iterator(); it.hasNext(); )
			((DefaultListModel) stringList.getModel()).addElement(it.next());
	}
}
