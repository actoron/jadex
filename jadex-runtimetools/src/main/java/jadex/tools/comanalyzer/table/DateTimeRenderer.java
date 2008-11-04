package jadex.tools.comanalyzer.table;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A renderer for date values. The format is defined by the static field.
 */
class DateTimeRenderer extends DefaultTableCellRenderer
{

	final static DateFormat date_format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
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
		{
			/* NOP */
		}
		setText(content);
		// setToolTipText(content);
		return this;
	}
}