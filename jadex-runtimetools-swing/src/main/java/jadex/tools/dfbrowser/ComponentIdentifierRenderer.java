package jadex.tools.dfbrowser;

import jadex.bridge.IComponentIdentifier;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ComponentIdentifierRenderer extends DefaultTableCellRenderer
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
		IComponentIdentifier aid = (IComponentIdentifier)value;
		setText(aid.getName());
		String[] addresses = aid.getAddresses();
		String tooltip = aid.getName();
		for(int i = 0; i < addresses.length; i++)
		{
			tooltip += "<br>" + addresses[i];
		}
		setToolTipText("<html>" + tooltip + "</html>");
		return this;
	}
}
