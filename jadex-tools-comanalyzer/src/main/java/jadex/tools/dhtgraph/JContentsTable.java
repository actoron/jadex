package jadex.tools.dhtgraph;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import jadex.bridge.service.types.dht.IID;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;

public class JContentsTable extends JTable {

	private List<Tuple2<String, Object>> content;
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
		
		this.content = new ArrayList<Tuple2<String, Object>>();

		model = new ContentsModel(content);
		setModel(model);
		
		addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if (e.getClickCount() == 2) {
					 java.awt.Point p = e.getPoint();
			        int rowIndex = rowAtPoint(p);
//			        int colIndex = columnAtPoint(p);
			        
			        try {
			        	final Object key = getValueAt(rowIndex, 0);
			            Object value = getValueAt(rowIndex, 2);
			            final String display = DhtViewerPanel.toListString(value);
			            
			            new JDialog() {{
			            	setTitle("Value for Key: " + key.toString());
			            	setLocation(e.getXOnScreen()-200, e.getYOnScreen() -200);
			            	add(new JTextArea() {{
			            		setText(display);
			            	}});
			            	setSize(400, 400);
			            	setPreferredSize(new Dimension(400,400));
			            	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			            }}.setVisible(true);
			        } catch (RuntimeException e1) {
			            //catch null pointer exception if mouse is over an empty line
			        }
				        
				}
			}

			
			
		});
	}
	
	@Override
	public String getToolTipText(MouseEvent e)
	{
		String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            tip = getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        return tip;
	}
	
	

	public void setSortedContent(List<Tuple2<String, Object>> content) {
		if (content == null) {
			content = new ArrayList<Tuple2<String, Object>>();
		}
		this.content = content;
		model.content = content;
		model.fireTableStructureChanged();
	}

	class ContentsModel extends AbstractTableModel {

		private List<Tuple2<String, Object>> content;

		public ContentsModel(List<Tuple2<String, Object>> content) {
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
			Tuple2<String, Object> tuple = content.get(rowIndex);
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
