package sodekovs.swing.jcc.plugins.coordination;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ConfigurationTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7395870299271525575L;

	private String[] columnNames = new String[] { "Key", "Value" };

	private List<SimpleEntry<String, String>> data = new ArrayList<SimpleEntry<String, String>>();

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 1)
			return true;
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SimpleEntry<String, String> entry = data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return entry.getKey();
		case 1:
			return entry.getValue();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SimpleEntry<String, String> entry = data.get(rowIndex);
		if (columnIndex == 1 && aValue instanceof String) {
			String value = (String) aValue;
			entry.setValue(value);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public List<SimpleEntry<String, String>> getData() {
		return this.data;
	}
}
