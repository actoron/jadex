package jadex.commons.gui.jtable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bridge.ClassInfo;


/**
 * A renderer for classinfos. 
 */
public class ClassInfoRenderer extends DefaultTableCellRenderer
{
	/**
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 * Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
		ClassInfo ci = (ClassInfo)value;
		if(ci!=null)
		{
			setText(ci.getClassNameOnly());
			setToolTipText(getTooltipText(ci));
		}
		return this;
	}

	public static String getTooltipText(ClassInfo ci)
	{
		String tooltip = "<b>" + ci.getPrefixNotation() + "</b>";
		tooltip	= "<html>" + tooltip + "</html>";
		return tooltip;
	}
}
