package jadex.tools.dfbrowser;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.service.types.df.IProperty;

class PropertyArrayRenderer extends DefaultTableCellRenderer
{

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
		IProperty[] sa = (IProperty[])value;
		String content;
		String tooltip;
		if(sa == null || sa.length == 0)
		{
			content = "";
			setToolTipText(null);
		}
		else
		{
			content = sa[0].getName() + "=" + sa[0].getValue();
			tooltip = content;
			for(int i = 1; i < sa.length; i++)
			{
				content += ", " + sa[i].getName() + "=" + sa[i].getValue();
				tooltip += "<br>" + sa[i].getName() + "=" + sa[i].getValue();
			}
			setToolTipText("<html>" + tooltip + "</html>");
		}
		setText(content);
		return this;
	}
}