package sodekovs.investigation.controlcenter;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MyTableCellRenderer extends JLabel implements TableCellRenderer {
	// This method is called each time a cell in a column // using this renderer needs to be rendered.
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		// 'value' is value contained in the cell located at // (rowIndex, vColIndex)
		if (isSelected) {

		}
		if (hasFocus) {
		}
//		setText(value.toString()); // Set tool tip if desired
//		setToolTipText((String) value); // Since the renderer is a component, return itself
		return this;
	}

	public void validate() {
	}

	public void revalidate() {
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

}
