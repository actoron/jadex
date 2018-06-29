package jadex.wfms.client.standard;

import jadex.wfms.service.ProcessResourceInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ProcessModelTableModel extends AbstractTableModel
{
	/** Column Titles */
	protected static final String[] COLUMN_NAMES = { "Repository", "Resource", "Model" };
	
	/** List of process models. */
	protected List<ProcessResourceInfo> processmodels;
	
	/**
	 *  Creates the table model.
	 */
	public ProcessModelTableModel()
	{
		this.processmodels = new LinkedList<ProcessResourceInfo>();
	}
	
	/**
	 *  Adds process model information.
	 *  
	 *  @param info The information.
	 */
	public void addProcessModel(ProcessResourceInfo info)
	{
		int row = processmodels.size();
		processmodels.add(info);
		fireTableStructureChanged();
		//fireTableRowsInserted(row, row);
	}
	
	/**
	 *  Removes process model information.
	 *  
	 *  @param info The information.
	 */
	public void removeProcessModel(ProcessResourceInfo info)
	{
		int row = processmodels.indexOf(info);
		processmodels.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
	/**
	 *  Clears the model.
	 */
	public void clear()
	{
		processmodels.clear();
		fireTableStructureChanged();
	}
	
	/**
	 *  Returns the model information with the given index.
	 *  
	 *  @return Model information.
	 */
	public ProcessResourceInfo getModelAt(int index)
	{
		return processmodels.get(index);
	}
	
	/**
	 *  Gets a column name.
	 */
	public String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
	}
	
	/**
	 *  Gets number of rows.
	 */
	public int getRowCount()
	{
		return processmodels.size();
	}
	
	/**
	 *  Gets number of columns.
	 */
	public int getColumnCount()
	{
		return 3;
	}
	
	/**
	 *  Gets the value at the given position.
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object ret = null;
		switch(columnIndex)
		{
			case 0:
				ret = processmodels.get(rowIndex).getRepositoryId();
				break;
			case 1:
				ret = processmodels.get(rowIndex).getResourceId();
				break;
			case 2:
			default:
				ret = processmodels.get(rowIndex).getPath();
		}
		return ret;
	}
	
}
