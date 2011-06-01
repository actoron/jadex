package jadex.wfms.client.standard;

import jadex.wfms.client.IWorkitem;
import jadex.wfms.guicomponents.SGuiHelper;

import java.awt.Component;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WorkitemListComponent extends JPanel
{
	private static final Object[] WORKITEM_LIST_COLUMN_NAMES = {"Workitem", "Role"};
	
	private static final String BEGIN_ACTIVITY_BUTTON_LABEL = "Begin Activity";
	
	/** Table listing available workitems */
	private JTable workitemTable;
	
	/** Current workitem table mouse listener */
	private MouseListener workitemMouseListener;
	
	/** Model of table listing available workitems */
	private DefaultTableModel workitemTableModel;
	
	/** Begin activity button */
	private JButton beginActivityButton;
	
	public WorkitemListComponent()
	{
		super(new GridBagLayout());
		
		workitemTableModel = new DefaultTableModel();
		workitemTableModel.setColumnIdentifiers(WORKITEM_LIST_COLUMN_NAMES);
		
		workitemTable = new JTable(workitemTableModel)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		workitemTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{
				if (value instanceof IWorkitem)
					return super.getTableCellRendererComponent(table, SGuiHelper.beautifyName(((IWorkitem) value).getName()), isSelected, hasFocus, row, column);
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
		beginActivityButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		add(beginActivityButton, gbc);
	}
	
	public void setBeginActivityAction(final Action action)
	{
		beginActivityButton.setAction(action);
		beginActivityButton.setText(BEGIN_ACTIVITY_BUTTON_LABEL);
		
		if (workitemMouseListener != null)
			workitemTable.removeMouseListener(workitemMouseListener);
		
		workitemMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					action.actionPerformed(new ActionEvent(e, e.getID(), null));
				}
			}
		};
		
		workitemTable.addMouseListener(workitemMouseListener);
	}
	
	public IWorkitem getSelectedWorkitem()
	{
		int row = workitemTable.getSelectedRow();
		if (row >= 0)
			return (IWorkitem) workitemTableModel.getValueAt(row, 0);
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
		workitemTableModel.addRow(new Object[] {wi, wi.getRole()});
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
		{
			IWorkitem wi = (IWorkitem) it.next();
			workitemTableModel.addRow(new Object[] {wi, wi.getRole()});
		}
	}
	
	/**
	 * Clears the workitem list
	 */
	public void clear()
	{
		while (workitemTableModel.getRowCount() > 0)
			workitemTableModel.removeRow(0);
	}
}
