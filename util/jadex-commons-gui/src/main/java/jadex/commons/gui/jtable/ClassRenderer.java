package jadex.commons.gui.jtable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.commons.SReflect;

/**
 *  Table renderer for classes that displays full name in tooltip.
 */
public class ClassRenderer extends DefaultTableCellRenderer
{
	/**
	 *  Get the renderer component. 
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		Class val = (Class)value;
		String content = val!=null ? SReflect.getInnerClassName(val) : "";
		setText(content);
		if(val!=null)
			setToolTipText(val.getName());
		return this;
	}
}
