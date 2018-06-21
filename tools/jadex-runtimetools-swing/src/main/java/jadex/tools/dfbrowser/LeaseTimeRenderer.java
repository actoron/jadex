package jadex.tools.dfbrowser;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class LeaseTimeRenderer extends DefaultTableCellRenderer
{
	final DateFormat date_format = new SimpleDateFormat("HH:mm:ss, dd-MM-yyyy");

	/**
	 * @param table
	 * @param value
	 * @param isSelected
	 * @param hasFocus
	 * @param row
	 * @param column
	 * @return this
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		Date date = (Date)value;
		String content = "n/a";
		try
		{
			content = date_format.format(date);
		}
		catch(Exception e)
		{/*NOP*/}
		setText(content);
		setToolTipText(content);
		return this;
	}
}