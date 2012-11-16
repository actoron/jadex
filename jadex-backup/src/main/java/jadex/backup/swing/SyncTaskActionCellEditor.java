package jadex.backup.swing;

import jadex.backup.job.SyncProfile;

import java.awt.Component;
import java.util.List;

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
		return getComponent(table, value, selected, row, true);
	}
	
	//-------- TableCellEditor interface --------
	
	/**
	 *  Component for editing the table cell.
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column)
	{
		return getComponent(table, value, selected, row, false);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get a renderer or editor component.
	 */
	public Component	getComponent(JTable table, Object value, boolean selected, int row, boolean renderer)
	{
		String	type	= (String)table.getValueAt(row, 1);

		List<String>	actions	= SyncProfile.ALLOWED_ACTIONS.get(type);
		
		JComboBox	box	= (JComboBox)getComponent();
		box.removeAllItems();
		for(String action: actions)
		{
			box.addItem(action);
		}
		
		box.setSelectedItem(value);
		box.setForeground(renderer && selected ? table.getSelectionForeground() : table.getForeground());
		box.setBackground(renderer && selected ? table.getSelectionBackground() : table.getBackground());		
		return box;
	}
}
