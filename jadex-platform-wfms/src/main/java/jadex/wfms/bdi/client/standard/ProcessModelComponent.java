package jadex.wfms.bdi.client.standard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ProcessModelComponent extends JPanel
{
	private static final String PROCESS_MODEL_COLUMN_NAME = "Process Models";
	
	private static final String START_BUTTON_LABEL = "Start Process";
	
	private static final String ADD_PROCESS_BUTTON_LABEL = "Add Process...";
	
	private static final String REMOVE_PROCESS_BUTTON_LABEL = "Remove Process";
	
	/** Table listing the process model names */
	private JTable processTable;
	
	/** Current process table mouse listener */
	private MouseListener processMouseListener;
	
	/** Model for the table listing the process model names */
	private DefaultTableModel processTableModel;
	
	/** Start process button */
	private JButton startButton;
	
	/** Add process button */
	private JButton addProcessButton;
	
	/** Remove process button */
	private JButton removeProcessButton;
	
	public ProcessModelComponent()
	{
		super(new GridBagLayout());
		processTableModel = new DefaultTableModel();
		processTableModel.setColumnIdentifiers(new Object[] {PROCESS_MODEL_COLUMN_NAME});
		
		processTable = new JTable(processTableModel)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane processScrollPane = new JScrollPane(processTable);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(processScrollPane, gbc);
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		add(buttonPanel, gbc);
		
		addProcessButton = new JButton(ADD_PROCESS_BUTTON_LABEL);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;
		buttonPanel.add(addProcessButton, gbc);
		
		removeProcessButton = new JButton(REMOVE_PROCESS_BUTTON_LABEL);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;
		buttonPanel.add(removeProcessButton, gbc);
		
		startButton = new JButton(START_BUTTON_LABEL);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;
		buttonPanel.add(startButton, gbc);
	}
	
	/**
	 * Sets the listed process model names, deleting the previous list.
	 * @param processModelNames new set of process model names
	 */
	public void setProcessModelNames(Set processModelNames)
	{
		while (processTableModel.getRowCount() != 0)
			processTableModel.removeRow(0);
		
		for (Iterator it = processModelNames.iterator(); it.hasNext(); )
			processTableModel.addRow(new Object[] {it.next()});
	}
	
	public void addProcessModelName(String name)
	{
		processTableModel.addRow(new Object[] {name});
	}
	
	public void removeProcessModelName(String name)
	{
		int row = 0;
		while (row < processTableModel.getRowCount())
		{
			if (name.equals(processTableModel.getValueAt(row, 0)))
			{
				processTableModel.removeRow(row);
				return;
			}
			++row;
		}
	}
	
	/**
	 * Returns the current selected process model name.
	 * @return currently selected process model name
	 */
	public String getSelectedModelName()
	{
		int row = processTable.getSelectedRow();
		int column = processTable.getSelectedColumn();
		if ((row >= 0) && (column >= 0))
			return (String) processTableModel.getValueAt(row, column);
		return null;
	}
	
	/**
	 * Sets the action for the start button.
	 * @param action action for the start button
	 */
	public void setStartAction(final Action action)
	{
		startButton.setAction(action);
		startButton.setText(START_BUTTON_LABEL);
		
		if (processMouseListener != null)
			processTable.removeMouseListener(processMouseListener);
		
		processMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					action.actionPerformed(new ActionEvent(e, e.getID(), null));
				}
			}
		};
		
		processTable.addMouseListener(processMouseListener);
	}
	
	/**
	 * Sets the action for the add process button.
	 * @param action action for the add process button
	 */
	public void setAddProcessAction(final Action action)
	{
		addProcessButton.setAction(action);
		addProcessButton.setText(ADD_PROCESS_BUTTON_LABEL);
	}
	
	/**
	 * Sets the action for the remove process button.
	 * @param action action for the remove process button
	 */
	public void setRemoveProcessAction(final Action action)
	{
		removeProcessButton.setAction(action);
		removeProcessButton.setText(REMOVE_PROCESS_BUTTON_LABEL);
	}
	
	/**
	 * Clears the model list
	 */
	public void clear()
	{
		while (processTableModel.getRowCount() > 0)
			processTableModel.removeRow(0);
	}
}
