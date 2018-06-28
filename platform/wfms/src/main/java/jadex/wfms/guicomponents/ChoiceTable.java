package jadex.wfms.guicomponents;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ChoiceTable extends JPanel
{
	private static final String[] COLUMN_NAMES = { "Choice", "Include" };
	
	private JTable selectionTable;
	
	private Set selections;
	
	public ChoiceTable(final Object[] choices)
	{
		this.selections = new HashSet();
		this.selectionTable = new JTable(new AbstractTableModel()
		{
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0)
					return choices[rowIndex];
				else
					return new Boolean(selections.contains(choices[rowIndex]));
			}
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{
				if (columnIndex == 1)
					if (((Boolean) aValue).booleanValue())
						selections.add(choices[rowIndex]);
					else
						selections.remove(choices[rowIndex]);
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return columnIndex == 1;
			}
			
			public int getRowCount()
			{
				return choices.length;
			}
			
			public int getColumnCount()
			{
				return 2;
			}
			
			public String getColumnName(int column)
			{
				return COLUMN_NAMES[column];
			}
			
			public Class getColumnClass(int columnIndex)
			{
				if (columnIndex == 1)
					return Boolean.class;
				return super.getColumnClass(columnIndex);
			}
		});
		JScrollPane scrollPane = new JScrollPane(selectionTable);
		add(scrollPane);
	}
	
	public void doLayout()
	{
		int width = selectionTable.getTableHeader().getHeaderRect(1).getSize().width;
		selectionTable.getColumnModel().getColumn(1).setMinWidth(width);
		selectionTable.getColumnModel().getColumn(1).setMaxWidth(width);
		selectionTable.getColumnModel().getColumn(1).setResizable(false);
		super.doLayout();
	}
	
	public Object[] getSelections()
	{
		return selections.toArray();
	}
}
