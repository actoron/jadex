package jadex.tools.dhtgraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jadex.bridge.service.types.dht.IFinger;

public class JFingerTable extends JTable {

	private List<IFinger> fingers;
	private FingerModel model;

	public JFingerTable() {
		this.fingers = new ArrayList<IFinger>();

		model = new FingerModel(fingers);
		setModel(model);
//		DefaultTableColumnModel cModel = new DefaultTableColumnModel();
//		cModel.addColumn(new TableColumn(0, 30));
//		cModel.addColumn(new TableColumn(1));
//		cModel.addColumn(new TableColumn(2));
//		setColumnModel(cModel);
	}
	
	public void setSortedFingers(List<IFinger> fingers) {
		if (fingers == null) {
			fingers = new ArrayList<IFinger>();
		}
		this.fingers = fingers;
		model.fingers = fingers;
		model.fireTableStructureChanged();
	}

	static class FingerModel extends AbstractTableModel {

		private List<IFinger> fingers;

		public FingerModel(List<IFinger> fingers) {
			super();
			this.fingers = fingers;
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Index";
			case 1:
				return "Start";
			case 2:
				return "Node ID";
			default:
				break;
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return fingers.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			IFinger iFinger = fingers.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return rowIndex;
			case 1:
				return iFinger.getStart();
			case 2:
				return iFinger.getNodeId();
			default:
				break;
			}
			return null;
		}
		
		
	}
}
