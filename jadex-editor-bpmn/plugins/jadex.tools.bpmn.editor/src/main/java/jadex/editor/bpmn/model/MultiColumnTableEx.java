/**
 * 
 */
package jadex.editor.bpmn.model;

import jadex.editor.common.model.properties.table.MultiColumnTable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MultiColumnTableEx extends MultiColumnTable
{
	// ---- attributes ----
	
	protected boolean[] isComplexColumn;
	
	protected Map<String, Map<String, String>>complexValues;
	
	// ---- constructors ----
	
	/**
	 * @param rowCount
	 * @param uniqueColumn
	 */
	public MultiColumnTableEx(int rowCount, int uniqueColumn, boolean[] complexColumnMarker)
	{
		super(rowCount, uniqueColumn);
		this.isComplexColumn = complexColumnMarker;
		this.complexValues = new HashMap<String, Map<String,String>>();
	}
	
	/**
	 * @param rowCount
	 * @param uniqueColumn
	 */
	public MultiColumnTableEx(MultiColumnTable table)
	{
		this(table.getRowList().size(), table.getUniqueColumn(), null);
		
		// add rows from table
		for(MultiColumnTableRow row : table.getRowList())
		{
			super.add(row);
		}
	}

	// ---- getter / setter ----
	
	/**
	 * @return the complexColumnMarker[]
	 */
	public boolean[] getComplexColumnsMarker()
	{
		return isComplexColumn;
	}
	
	/**
	 * Add a complex value to this table
	 * @param identifier
	 * @param value map
	 */
	public void setComplexValue(String identifier, Map<String, String> value)
	{
		this.complexValues.put(identifier, value);
	}
	
	/**
	 * Get a complex value from this table
	 * @param identifier
	 * @return value map
	 */
	public Map<String, String> getComplexValue(String identifier)
	{
		return complexValues.get(identifier)==null? Collections.EMPTY_MAP: complexValues.get(identifier);
	}

	// ---- methods ----
	
	/**
	 * If this method returns true for a column index, the value
	 * of this column is only a indirection key for a other value
	 * annotation.
	 * 
	 * @param coulumnIndex
	 * @return true, if the column contains a complex value reference
	 */
	public boolean isComplexColumn(int coulumnIndex)
	{
		if(isComplexColumn != null && coulumnIndex < isComplexColumn.length)
			return isComplexColumn[coulumnIndex];
		
		return false;
	}
	
	// ---- overrides ----
	
	/**
	 * @see jadex.editor.common.model.properties.table.MultiColumnTable#add(int, jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow)
	 */
	public void add(int index, MultiColumnTableRow row)
	{
		// no complex type in table
		if(isComplexColumn == null)
		{
			super.add(index, row);
			return;
		}
		
		// ensure unique reference identifier for complex types
		for(int column = 0; column < isComplexColumn.length; column++)
		{
			if(isComplexColumn[column])
			{
//				try
//				{
					row.setColumnValueAt(column, createUniqueValue(row.getColumnValueAt(column)));
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
			}
		}
		
		super.add(index, row);
	}
}
