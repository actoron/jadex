package jadex.base.gui.jtable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.service.IServiceIdentifier;


/**
 * A renderer for classinfos. 
 */
public class ServiceIdentifierRenderer extends DefaultTableCellRenderer
{
	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		IServiceIdentifier si = (IServiceIdentifier)value;
		if(si!=null)
		{
			setText(si.getServiceName());
			setToolTipText(getTooltipText(si));
		}
		return this;
	}

	public static String getTooltipText(IServiceIdentifier si)
	{
		String tooltip = "<b>" + si.toString() + "</b>";
		tooltip	= "<html>" + tooltip + "</html>";
		return tooltip;
	}
}
