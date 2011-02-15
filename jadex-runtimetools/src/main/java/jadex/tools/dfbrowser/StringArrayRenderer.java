package jadex.tools.dfbrowser;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class StringArrayRenderer extends DefaultTableCellRenderer
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
		String[] sa = (String[])value;
		String content;
		String tooltip;
		if(sa == null || sa.length == 0)
		{
			content = "";
			setToolTipText(null);
		}
		else
		{
			content = sa[0];
			tooltip = sa[0];
			for(int i = 1; i < sa.length; i++)
			{
				content += ", " + sa[i];
				tooltip += "<br>" + sa[i];
			}
			setToolTipText("<html>" + tooltip + "</html>");
		}
		setText(content);
		return this;
	}
}

/* 
 * $Log$
 * Revision 1.2  2006/03/29 11:55:08  braubach
 * no message
 *
 * Revision 1.1  2006/03/13 18:19:26  walczak
 * Alpha version of df browser (Mock Up)
 *
 */