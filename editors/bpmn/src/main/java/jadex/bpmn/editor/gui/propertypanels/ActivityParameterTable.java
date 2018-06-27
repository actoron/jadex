package jadex.bpmn.editor.gui.propertypanels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.gui.autocombo.AutoComboTableCellEditor;
import jadex.commons.gui.autocombo.AutoComboTableCellRenderer;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.ClassInfoComboBoxRenderer;
import jadex.commons.gui.autocombo.ComboBoxEditor;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;

/**
 * 
 */
public class ActivityParameterTable extends JTable
{
	/** The column names. */
	protected static final String[] COLUMN_NAMES = { "Direction", "Name", "Class", "Initial Value", "Internal" };
	
	/** The activity. */
	protected VActivity activity;
	
	/**
	 *  Create a new ActivityParameterTable.
	 */
	public ActivityParameterTable(final ModelContainer modelcontainer, VActivity activity)
	{
		this.activity = activity;
		setModel(new ParameterTableModel());
		
		getColumnModel().getColumn(0).setPreferredWidth(500);
		getColumnModel().getColumn(1).setPreferredWidth(1000);
		getColumnModel().getColumn(2).setPreferredWidth(3000);
		getColumnModel().getColumn(3).setPreferredWidth(5000);
		getColumnModel().getColumn(4).setPreferredWidth(500);
		
		JComboBox dircombo = new JComboBox(new String[] { MParameter.DIRECTION_IN, MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT });
		dircombo.setEditable(false);
		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(dircombo));
		
//		System.out.println("size: "+modelcontainer.getAllClasses().size());
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getAllClasses());
		acc.setModel(accm);
		acc.setEditor(new ComboBoxEditor(accm));
		acc.setRenderer(new ClassInfoComboBoxRenderer());
		
		TableColumn col = getColumnModel().getColumn(2);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc)
		{
//			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
//			{
//				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//				
//				ClassInfo ci = (ClassInfo)value;
//				try
//				{
//					ClassLoader cl = modelcontainer.getProjectClassLoader();
//					Class<?> clazz = ci.getType(cl);
//					
//				}
//				catch(Exception e)
//				{
//				}
//				setText(value==null? "": box.getAutoModel().convertToString(value));
//				
//				return this;
//			}
		});
	}
	
	/**
	 *  Gets the BPMN model activity.
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
		addParameter(param);
	}
	
	/**
	 *  Adds a parameter.
	 */
	public void addParameter(MParameter param)
	{
		if (isEditing())
		{
			getCellEditor().stopCellEditing();
		}
		
		int row = getRowCount();
		getBpmnActivity().addParameter(param);
		((ParameterTableModel) getModel()).fireTableRowsInserted(row, row);
		
//		getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox(new Object[] { MParameter.DIRECTION_IN, MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT })));
		activity.addedParameter(param);
	}
	
	/**
	 *  Removes parameters.
	 *  @param ind Indices of parameters.
	 */
	public void removeParameters(int[] ind)
	{
		if (isEditing())
		{
			getCellEditor().stopCellEditing();
		}
		
		Arrays.sort(ind);
		Set<MParameter> params = new HashSet<MParameter>();
		for(int i = ind.length - 1; i >= 0; --i)
		{
			MParameter param = getBpmnActivity().getParameters().remove(ind[i]);
			((ParameterTableModel) getModel()).fireTableRowsDeleted(ind[i], ind[i]);
			params.add(param);
		}
		activity.removedParameter(params);
	}
	
	/**
	 *  Removes parameters.
	 *  @param ind Indices of parameters.
	 */
	public void removeAllParameters()
	{
		if(getBpmnActivity()!=null && getBpmnActivity().getParameters()!=null)
		{
			int length = getBpmnActivity().getParameters().size();
			Set<MParameter> params = new HashSet<MParameter>();
			for(int i=0; i<length; i++)
			{
				MParameter param = getBpmnActivity().getParameters().remove(0);
				params.add(param);
			}
			activity.removedParameter(params);
			((ParameterTableModel)getModel()).fireTableRowsDeleted(0, length-1);
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
			boolean ret = true;
			if (columnIndex == 4)
			{
				MParameter param = (MParameter)getBpmnActivity().getParameters().get(rowIndex);
				String inival = (String) getValueAt(rowIndex, 3);
				if (MParameter.DIRECTION_OUT.equals(param.getDirection()) || inival == null || inival.length() == 0)
				{
					ret = false;
				}
			}
			return ret;
		}
		
		/**
		 *  Returns the column class.
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 4)
			{
				return Boolean.class;
			}
			
			return super.getColumnClass(columnIndex);
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
			MParameter param = (MParameter)getBpmnActivity().getParameters().get(rowIndex);
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
					return param.getInitialValue() != null? param.getInitialValue().getValue() : "";
				case 4:
					return activity.isInternalParameters(param.getName());
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
					if (MParameter.DIRECTION_OUT.equals(value))
					{
						activity.removeInternalParameter(param.getName());
						fireTableCellUpdated(rowIndex, 4);
					}
					activity.refreshParameter(param);
					break;
				case 1:
				default:
					if (!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						boolean internal = activity.isInternalParameters(param.getName());
						activity.removeInternalParameter(param.getName());
						getBpmnActivity().getParameters().remove(rowIndex);
						param.setName(BasePropertyPanel.createFreeName((String) value, new BasePropertyPanel.IndexMapContains(getBpmnActivity().getParameters())));
						getBpmnActivity().getParameters().add(rowIndex, param.getName(), param);
						if (internal)
						{
							activity.addInternalParameter(param.getName());
						}
					}
					break;
				case 2:
					param.setClazz((ClassInfo)value);
					break;
				case 3:
					if (param.getInitialValue() != null)
					{
						param.getInitialValue().setValue((String) value);
					}
					else
					{
						param.setInitialValue(new UnparsedExpression(param.getName(), param.getClazz() != null? param.getClazz().getTypeName() : null, (String) value, null));
					}
					break;
				case 4:
					if (Boolean.TRUE.equals(value))
					{
						activity.addInternalParameter(param.getName());
					}
					else
					{
						activity.removeInternalParameter(param.getName());
					}
					break;
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}
