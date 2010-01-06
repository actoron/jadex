package jadex.wfms.bdi.client.generic;

import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.Workitem;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;

public class WorkitemListComponent extends JPanel
{
	private static final String WORKITEM_LIST_COLUMN_NAME = "Workitems";
	
	private static final String BEGIN_ACTIVITY_BUTTON_LABEL = "Begin Activity";
	
	/** Table listing available workitems */
	private JTable workitemTable;
	
	/** Model of table listing available workitems */
	private DefaultTableModel workitemTableModel;
	
	/** Begin activity button */
	private JButton beginActivityButton;
	
	public WorkitemListComponent()
	{
		super(new GridBagLayout());
		
		workitemTableModel = new DefaultTableModel();
		workitemTableModel.setColumnIdentifiers(new Object[] {WORKITEM_LIST_COLUMN_NAME});
		
		workitemTable = new JTable(workitemTableModel)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		workitemTable.setDefaultRenderer(Object.class, new DefaultTableRenderer()
		{
			
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{
				if (value instanceof IWorkitem)
					return super.getTableCellRendererComponent(table, ((IWorkitem) value).getName() + " [" + ((IWorkitem) value).getRole() + "]", isSelected, hasFocus, row, column);
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});
		
		JScrollPane workitemScrollPane = new JScrollPane(workitemTable);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(workitemScrollPane, gbc);
		
		beginActivityButton = new JButton(BEGIN_ACTIVITY_BUTTON_LABEL);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		add(beginActivityButton, gbc);
	}
	
	public void setBeginActivityAction(Action action)
	{
		beginActivityButton.setAction(action);
		beginActivityButton.setText(BEGIN_ACTIVITY_BUTTON_LABEL);
	}
	
	public IWorkitem getSelectedWorkitem()
	{
		int row = workitemTable.getSelectedRow();
		int column = workitemTable.getSelectedColumn();
		if ((row >= 0) && (column >= 0))
			return (IWorkitem) workitemTableModel.getValueAt(row, column);
		return null;
	}
	
	/**
	 * Adds a workitem to the list.
	 * @param wi the workitem
	 */
	public void addWorkitem(IWorkitem wi)
	{
		for (int i = 0; i < workitemTableModel.getRowCount(); ++i)
		{
			if (wi.equals(workitemTableModel.getValueAt(i, 0)))
				return;
			
		}
		workitemTableModel.addRow(new Object[] {wi});
	}
	
	/**
	 * Removess a workitem from the list.
	 * @param wi the workitem
	 */
	public void removeWorkitem(IWorkitem wi)
	{
		for (int i = 0; i < workitemTableModel.getRowCount(); ++i)
		{
			if (wi.equals(workitemTableModel.getValueAt(i, 0)))
			{
				workitemTableModel.removeRow(i);
			}
		}
	}
	
	/**
	 * Sets the listed workitems, deleting the previous list.
	 * @param workitems new set of workitems
	 */
	public void setWorkitems(Set workitems)
	{
		while (workitemTableModel.getRowCount() != 0)
			workitemTableModel.removeRow(0);
		
		for (Iterator it = workitems.iterator(); it.hasNext(); )
			workitemTableModel.addRow(new Object[] {it.next()});
	}
}
