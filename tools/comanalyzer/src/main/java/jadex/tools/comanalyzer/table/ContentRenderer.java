package jadex.tools.comanalyzer.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A Renderer that can display different types of content. It distinguish
 * between null and String values and represents other types with their
 * classnames.
 */
class ContentRenderer extends DefaultTableCellRenderer
{

	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		String content;
		String tooltip;
		if(value == null)
		{
			content = "";
		}
		else if(value instanceof String)
		{
			content = (String)value;
		}
		else
		{
			content = value.getClass().getCanonicalName();
			tooltip = value.toString();
			setToolTipText("<html>" + tooltip + "</html>");
		}

		setText(content);
		return this;
	}
}
