/**
 * 
 */
package sodekovs.swing.jcc.plugins.coordination;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author thomas
 * 
 */
public class MechanismTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7095729062453218114L;

	private String[] columnNames = new String[] { "Realisation Name", "Implementation", "Active?" };

	private List<MechanismTableEntry> data = new ArrayList<MechanismTableEntry>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
	 * @return the data
	 */
	public List<MechanismTableEntry> getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2)
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		MechanismTableEntry mte = data.get(rowIndex);
		if (columnIndex == 2) {
			mte.setActive((Boolean) aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MechanismTableEntry entry = data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return entry.getMechanism().getRealisationName();
		case 1:
			return entry.getMechanism().getClass().getName();
		case 2:
			return entry.getActive();
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
}
