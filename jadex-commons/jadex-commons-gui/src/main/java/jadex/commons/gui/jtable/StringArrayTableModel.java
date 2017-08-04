package jadex.commons.gui.jtable;

import javax.swing.table.AbstractTableModel;

/** Table model based on a 2D string array. */
public class StringArrayTableModel extends AbstractTableModel
{
	/** The string array */
	protected String[][] stringarray;
	
	protected String[] columnnames;
	
	/** Flag if editable. */
	protected boolean editable;
	
	public StringArrayTableModel(String[][] stringarray)
	{
		this.stringarray = stringarray;
	}
	
	/**
	 *  Sets the column names.
	 * 
	 *  @param names The column names.
	 */
	public void setColumnNames(String[] names)
	{
		this.columnnames = names;
	}
	
	/**
	 *  Sets if editable.
	 *  @param editable True if editable.
	 */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
	
	/**
	 *  Override
	 */
	public String getColumnName(int column)
	{
		String ret = null;
		if (columnnames != null && column < columnnames.length)
			ret = columnnames[column];
		return ret;
	}
	
	/**
	 *  Override
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return editable;
	}
	
	/**
	 *  Override
	 */
	public int getRowCount()
	{
		return stringarray.length;
	}

	/**
	 *  Override
	 */
	public int getColumnCount()
	{
		int ret = 0;
		
		if (stringarray.length > 0)
			ret = stringarray[0].length;
		
		return ret;
	}
	
	/**
	 *  Override
	 */
	public Object getValueAt(int rowindex, int columnindex)
	{
		return stringarray[rowindex][columnindex];
	}
}
