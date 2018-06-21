package jadex.commons.gui.autocombo;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class AutoComboTableCellRenderer extends DefaultTableCellRenderer
{
	/** The combo box. */
	protected AutoCompleteCombo box;
	
	/**
	 * 
	 */
	public AutoComboTableCellRenderer(AutoCompleteCombo box)
	{
		this.box = box;
	}
	
	/**
	 * 
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
//		ClassInfo ci = (ClassInfo)value;
		setText(value==null? "": box.getAutoModel().convertToString(value));
		
		return this;
	}
}