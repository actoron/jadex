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
import javax.swing.table.DefaultTableModel;

public class ProcessModelComponent extends JPanel
{
	private static final String PROCESS_MODEL_COLUMN_NAME = "Process Models";
	
	private static final String START_BUTTON_LABEL = "Start Process";
	
	/** Table listing the process model names */
	private JTable processTable;
	
	/** Current process table mouse listener */
	private MouseListener processMouseListener;
	
	/** Model for the table listing the process model names */
	private DefaultTableModel processTableModel;
	
	/** Start process button */
	private JButton startButton;
	
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
		JScrollPane processScrollPane = new JScrollPane(processTable);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(processScrollPane, gbc);
		
		startButton = new JButton(START_BUTTON_LABEL);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		add(startButton, gbc);
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
}
