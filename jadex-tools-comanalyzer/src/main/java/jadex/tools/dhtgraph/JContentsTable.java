package jadex.tools.dhtgraph;

import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class JContentsTable extends JTable {

	private List<Tuple2<String, String>> content;
	private ContentsModel model;
	
	private Method hasher;

	public JContentsTable() {
		Class<?> clazz = SReflect.classForName0("jadex.platform.service.dht.ID", getClass().getClassLoader());
		if (clazz != null) {
			try {
				this.hasher = clazz.getMethod("get", String.class);
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		}
		
		this.content = new ArrayList<Tuple2<String, String>>();

		model = new ContentsModel(content);
		setModel(model);
	}
	
	public void setSortedContent(List<Tuple2<String, String>> content) {
		if (content == null) {
			content = new ArrayList<Tuple2<String, String>>();
		}
		this.content = content;
		model.content = content;
		model.fireTableStructureChanged();
	}

	class ContentsModel extends AbstractTableModel {

		private List<Tuple2<String, String>> content;

		public ContentsModel(List<Tuple2<String, String>> content) {
			super();
			this.content = content;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Key";
			case 1:
				return "Hash";
			case 2:
				return "Value";
			default:
				break;
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return content.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Tuple2<String, String> tuple = content.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return tuple.getFirstEntity();
			case 1:
				return hash(tuple.getFirstEntity());
			case 2:
				return tuple.getSecondEntity();
			default:
				break;
			}
			return null;
		}
		
		
		
		
	}
	
	private IID hash(String key) {
		if (hasher != null) {
			try {
				Object res = hasher.invoke(null, key);
				return (IID) res;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
