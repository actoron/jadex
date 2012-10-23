package jadex.backup.swing;

import jadex.backup.job.SyncTaskEntry;
import jadex.backup.resource.BackupResource;
import jadex.commons.SUtil;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *  Combo box for available actions for a sync task.
 */
public class SyncTaskActionCellEditor	extends DefaultCellEditor	implements TableCellRenderer
{
	//-------- constructors --------
	
	/**
	 *  Create a sync task action editor.
	 */
	public SyncTaskActionCellEditor()
	{
		super(new JComboBox());
	}
	
	//-------- TableCellRenderer interface --------
	
	/**
	 *  Component for rendering the table cell.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
	{
		return getTableCellEditorComponent(table, value, selected, row, column);
	}
	
	//-------- TableCellEditor interface --------
	
	/**
	 *  Component for editing the table cell.
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column)
	{
		Object	type	= table.getValueAt(row, 1);
		String[]	actions;
		if(BackupResource.FILE_REMOTE_MODIFIED.equals(type))
		{
			actions	= new String[]{SyncTaskEntry.ACTION_UPDATE, SyncTaskEntry.ACTION_SKIP, SyncTaskEntry.ACTION_COPY, SyncTaskEntry.ACTION_OVERRIDE};
		}
		else if(BackupResource.FILE_REMOTE_ADDED.equals(type))
		{
			actions	= new String[]{SyncTaskEntry.ACTION_UPDATE, SyncTaskEntry.ACTION_SKIP};
		}
		else if(BackupResource.FILE_LOCAL_MODIFIED.equals(type))
		{
			actions	= new String[]{SyncTaskEntry.ACTION_SKIP, SyncTaskEntry.ACTION_REVERT, SyncTaskEntry.ACTION_COPY};
		}
		else if(BackupResource.FILE_CONFLICT.equals(type))
		{
			actions	= new String[]{SyncTaskEntry.ACTION_COPY, SyncTaskEntry.ACTION_SKIP, SyncTaskEntry.ACTION_UPDATE, SyncTaskEntry.ACTION_OVERRIDE};
		}
		else
		{
			actions	= SUtil.EMPTY_STRING_ARRAY;
		}
		
		JComboBox	box	= (JComboBox)getComponent();
		box.removeAllItems();
		for(String action: actions)
		{
			box.addItem(action);
		}
		
		box.setSelectedItem(value);
		box.setForeground(selected ? table.getSelectionForeground() : table.getForeground());
		box.setBackground(selected ? table.getSelectionBackground() : table.getBackground());		
		return box;
	}
}
