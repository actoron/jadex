package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jadex.bpmn.editor.gui.ImageProvider;

/**
 * 
 */
public class AddRemoveTablePanel extends JPanel
{
	/** The table. */
	protected JTable table;
	
	/**
	 * 
	 */
	public AddRemoveTablePanel(String name, ImageProvider imgprovider, final String defaultval, String[] vals)
	{
		final SimpleTableModel tm = new SimpleTableModel(name, vals);
		table = new JTable(tm);
		
		Action addaction = new AbstractAction("Add Entry")
		{
			public void actionPerformed(ActionEvent e)
			{
				String newentry = BasePropertyPanel.createFreeName(defaultval, new BasePropertyPanel.CollectionContains(tm.entries));
				tm.addValue(newentry);
			}
		};
		Action removeaction = new AbstractAction("Remove Entry")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = table.getSelectedRows();
				Arrays.sort(ind);
				
				for(int i = ind.length - 1; i >= 0; --i)
				{
					tm.removeValue(ind[i]);
				}
			}
		};
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(table);
		add(tablescrollpane, gc);
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(imgprovider, addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		add(buttonpanel, gc);
	}
	
	/**
	 * 
	 */
	public AddRemoveTablePanel(String name, Action addaction, Action removeaction, ImageProvider imgprovider, String[] vals)
	{
		table = new JTable(new SimpleTableModel(name, vals));
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(table);
		add(tablescrollpane, gc);
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(imgprovider, addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		add(buttonpanel, gc);
	}
	
	/**
	 * 
	 */
	public AddRemoveTablePanel(JTable table, Action addaction, Action removeaction, ImageProvider imgprovider)
	{
		this.table = table;
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(table);
		add(tablescrollpane, gc);
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(imgprovider, addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		add(buttonpanel, gc);
	}
	
	/**
	 *  Get the table.
	 *  return The table.
	 */
	public JTable getTable()
	{
		return table;
	}

	/**
	 *  Table model for imports.
	 */
	protected class SimpleTableModel extends AbstractTableModel
	{
		protected List<String> entries = new ArrayList<String>();
		
		protected String name;
		
		protected SimpleTableModel(String name, String[] vals)
		{
			this.name = name==null? "Entries": name;
			if(vals!=null)
			{
				for(String val: vals)
				{
					addValue(val);
				}
			}
		}
		
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return name;
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return entries.size();
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return 1;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return entries.get(rowIndex);
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			entries.set(rowIndex, (String)value);
			fireTableCellUpdated(rowIndex, columnIndex);
//			modelcontainer.setDirty(true);
		}
		
		/**
		 * 
		 */
		public void addValue(String val)
		{
			entries.add(val);
			fireTableRowsInserted(entries.size()-1, entries.size()-1);
		}
		
		/**
		 * 
		 */
		public void removeValue(int row)
		{
			String oldval = entries.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}
}
