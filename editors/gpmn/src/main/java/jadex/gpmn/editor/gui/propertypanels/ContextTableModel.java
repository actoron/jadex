package jadex.gpmn.editor.gui.propertypanels;

import javax.swing.table.AbstractTableModel;

import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.model.gpmn.IParameter;

public class ContextTableModel extends AbstractTableModel
{
	/** The model container */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates the table model.
	 */
	public ContextTableModel(ModelContainer modelcontainer)
	{
		this.modelcontainer = modelcontainer;
	}
	
	/**
	 *  Adds a new parameter.
	 */
	public void addParameter()
	{
		int newindex = getRowCount();
		modelcontainer.getGpmnModel().getContext().addParameter();
		modelcontainer.setDirty(true);
		fireTableRowsInserted(newindex, newindex);
	}
	
	/**
	 *  Removes a parameter.
	 *  
	 *  @param index The parameter index.
	 */
	public void removeParameter(int index)
	{
		modelcontainer.getGpmnModel().getContext().removeParameter(index);
		modelcontainer.setDirty(true);
		fireTableRowsDeleted(index, index);
	}
	
	/**
	 *  Removes parameters.
	 *  
	 *  @param indexes The parameter indexes.
	 */
	public void removeParameters(int[] indexes)
	{
		modelcontainer.getGpmnModel().getContext().removeParameters(indexes);
		modelcontainer.setDirty(true);
		fireTableStructureChanged();
	}
	
	/**
	 *  Get the row count.
	 */
	public int getRowCount()
	{
		return modelcontainer.getGpmnModel().getContext().getParameters().size();
	}
	
	/**
	 *  Get the column count.
	 */
	public int getColumnCount()
	{
		return IParameter.PROPERTYNAMES.length;
	}
	
	/**
	 *  Get the value.
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		IParameter param = modelcontainer.getGpmnModel().getContext().getParameters().get(rowIndex);
		Object ret = null;
		if (columnIndex == 0)
		{
			ret = param.getName();
		}
		else if (columnIndex == 1)
		{
			ret = param.getType();
		}
		else if (columnIndex == 2)
		{
			ret = param.getValue();
		}
		else if (columnIndex == 3)
		{
			ret = param.isSet();
		}
		return ret;
	}
	
	/**
	 *  Set a value.
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		IParameter param = modelcontainer.getGpmnModel().getContext().getParameters().get(rowIndex);
		if (columnIndex == 0)
		{
			modelcontainer.getGpmnModel().getContext().renameParameter(param.getName(), (String) aValue);
		}
		else if (columnIndex == 1)
		{
			param.setType((String) aValue);
		}
		else if (columnIndex == 2)
		{
			param.setValue((String) aValue);
		}
		else if (columnIndex == 3)
		{
			param.setSet((Boolean) aValue);
		}
		modelcontainer.setDirty(true);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	/**
	 *  Gets the column name.
	 */
	public String getColumnName(int column)
	{
		return IParameter.PROPERTYNAMES[column];
	}
	
	/**
	 *  Gets the column class.
	 */
	public Class<?> getColumnClass(int columnIndex)
	{
		return IParameter.PROPERTYTYPES[columnIndex];
	}
	
	/**
	 *  Test if cell is editable.
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}
}
