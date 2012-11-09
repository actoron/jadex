package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ActivityParameterTable extends JTable
{
	/** The column names. */
	protected static final String[] COLUMN_NAMES = { "Direction", "Name", "Class", "Initial Value" };
	
	/** The activity. */
	protected VActivity activity;
	
	public ActivityParameterTable(ModelContainer modelcontainer, VActivity activity)
	{
		super();
		this.activity = activity;
		setModel(new ParameterTableModel());
		
		getColumnModel().getColumn(0).setPreferredWidth(1000);
		for (int i = 1; i < getColumnCount(); ++i)
		{
			getColumnModel().getColumn(i).setPreferredWidth(3000);
		}
	}
	
	/**
	 *  Gets the BPMN model activity.
	 *	
	 *	@return The BPMN model activity.
	 */
	protected MActivity getBpmnActivity()
	{
		return (MActivity) activity.getBpmnElement();
	}
	
	/**
	 *  Adds a parameter.
	 */
	public void addParameter()
	{
		String name = BasePropertyPanel.createFreeName("name", new BasePropertyPanel.IndexMapContains(getBpmnActivity().getParameters()));
		MParameter param = new MParameter(MParameter.DIRECTION_INOUT, new ClassInfo(""), name, new UnparsedExpression(name, "", "", null));
		int row = getRowCount();
		getBpmnActivity().addParameter(param);
		((ParameterTableModel) getModel()).fireTableRowsInserted(row, row);
		
		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox(new Object[] { MParameter.DIRECTION_IN, MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT })));
	}
	
	/**
	 *  Removes parameters.
	 *  
	 *  @param ind Indices of parameters.
	 */
	public void removeParameters(int[] ind)
	{
		Arrays.sort(ind);
		
		for (int i = ind.length - 1; i >= 0; --i)
		{
			getBpmnActivity().getParameters().remove(ind[i]);
			((ParameterTableModel) getModel()).fireTableRowsDeleted(ind[i], ind[i]);
		}
	}
	
	/**
	 *  Table model for parameters.
	 */
	protected class ParameterTableModel extends AbstractTableModel
	{
		
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return COLUMN_NAMES[column];
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			int ret = getBpmnActivity().getParameters() != null? getBpmnActivity().getParameters().size() : 0;
			return ret;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			MParameter param = (MParameter) getBpmnActivity().getParameters().get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return param.getDirection();
				case 1:
				default:
					return param.getName();
				case 2:
					return param.getClazz();
				case 3:
					return param.getInitialValue().getValue();
			}
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			MParameter param = (MParameter) getBpmnActivity().getParameters().get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					param.setDirection((String) value);
					break;
				case 1:
				default:
					param.setName(BasePropertyPanel.createFreeName((String) value, new BasePropertyPanel.IndexMapContains(getBpmnActivity().getParameters())));
					break;
				case 2:
					param.setClazz(new ClassInfo((String) value));
					break;
				case 3:
					param.getInitialValue().setValue((String) value);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}
