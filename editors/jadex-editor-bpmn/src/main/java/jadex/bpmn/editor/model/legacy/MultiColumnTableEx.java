/**
 * 
 */
package jadex.bpmn.editor.model.legacy;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bpmn.model.MAnnotation;
import jadex.bpmn.model.MAnnotationDetail;

/**
 * ADAPTED !!! Copy of editor table version
 * @author Claas
 *
 */
public class MultiColumnTableEx extends MultiColumnTable
{

	// ---- attributes ----
	
	boolean[] isComplexColumn;
	
	Map<String, Map<String, String>> complexValues;
	
	// ---- constructors ----
	
	/**
	 * @param rowCount
	 * @param uniqueColumn
	 */
	public MultiColumnTableEx(int rowCount, boolean[] complexColumnMarker)
	{
		super(rowCount);
		this.isComplexColumn = complexColumnMarker;
		this.complexValues = new HashMap<String, Map<String,String>>();
	}
	
	/**
	 * @param rowCount
	 * @param uniqueColumn
	 */
	public MultiColumnTableEx(MultiColumnTable table)
	{
		this(table.getRowList().size(), null);
		
		// add rows from table
		for (MultiColumnTableRow row : table.getRowList())
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
		return this.complexValues.get(identifier);
	}
	

	// ---- methods ----
	
	/**
	 *  Get the cell value.
	 *  @param row The row.
	 *  @param i The column.
	 */
	public String getCellValue(int row, int i)
	{
//		MultiColumnTableRow multiColumnTableRow = get(row);
//		if (isComplexColumn(i))
//		{
//			return getComplexValue((String)multiColumnTableRow.getColumnValueAt(i));
//		}
		return (String)get(row).getColumnValueAt(i);
	}
	
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
		if (isComplexColumn != null && coulumnIndex < isComplexColumn.length)
			return isComplexColumn[coulumnIndex];
		
		return false;
	}

	// ---- overrides ----

//	/**
//	 * @see jadex.editor.common.model.properties.table.MultiColumnTable#add(int, jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow)
//	 */
//	public void add(int index, MultiColumnTableRow row)
//	{
//		// no complex type in table
//		if (isComplexColumn == null)
//		{
//			super.add(index, row);
//			return;
//		}
//		
//		// ensure unique reference identifier for complex types
//		for (int column = 0; column < isComplexColumn.length; column++)
//		{
//			if (isComplexColumn[column])
//			{
//				row.setColumnValueAt(column, createUniqueValue(row.getColumnValueAt(column)));
//			}
//		}
//		
//		super.add(index, row);
//		
//	}

	/**
	 * 
	 * @param string
	 * @return string as boolean[] marker
	 */
	public static boolean[] decodeComplexColumnMarker(String markerString)
	{
		boolean[] marker = null;
		
		if (markerString != null && !(markerString.trim().length() == 0))
		{
			String[] split = markerString
					.split(":");
			marker = new boolean[split.length];
			for (int i = 0; i < split.length; i++)
			{
				marker[i] = Boolean.parseBoolean(split[i]);
			}
		}
		return marker;
	}
	

	/**
	 * 
	 * @param marker
	 * @return boolean[] marker as String
	 */
	public static String encodeComplexColumnMarker(boolean[] marker)
	{
		
		if (marker != null)
		{
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < marker.length; i++)
			{
				b.append(marker[i]);
				if (i + 1 < marker.length)
				{
					b.append(":");
				}
			}
			return b.toString();
		}
		
		return "";
	}
	
	/**
	 * Convert the List of MAnnotationDetail to a Map.
	 * @param details
	 * @return Map with key value pairs of annptation Detail
	 */
	public static Map convertDetailsToMap(List details)
	{
		if (details == null)
			return null;

		Map returnMap = new HashMap();
		for(int j=0; j<details.size(); j++)
		{
			MAnnotationDetail detail = (MAnnotationDetail)details.get(j);
			returnMap.put(detail.getKey().toLowerCase(), detail.getValue());
		}
		
		return returnMap;
	}
	
	/**
	 * Get annotation detail
	 */
	public static MultiColumnTableEx parseEAnnotationTable(List details, List annos)
	{
		MultiColumnTableEx newTable;
		Map detailsMap = convertDetailsToMap(details);
		
		String dimension = (String)detailsMap.get("dimension");
		boolean[] complexColumnMarker = decodeComplexColumnMarker((String)detailsMap.get("complexcolumns"));

		TableCellIndex tableDimension = new TableCellIndex(dimension);
		newTable = new MultiColumnTableEx(tableDimension.getRowCount(), complexColumnMarker);
		for(int rowIndex = 0; rowIndex < tableDimension.rowCount; rowIndex++)
		{
			String[] newRow = new String[tableDimension.columnCount];
			for(int columnIndex = 0; columnIndex < tableDimension.columnCount; columnIndex++)
			{
				newRow[columnIndex] = (String)detailsMap.get((new TableCellIndex(rowIndex, columnIndex)).toString());

				// initialize complex values
				if(newTable.isComplexColumn(columnIndex))
				{
					Map valmap = new HashMap();
					
					MAnnotation ref = null;
					for(int i=0; i<annos.size(); i++)
					{
						MAnnotation anno = (MAnnotation)annos.get(i);
						if(anno.getSource().equals(newRow[columnIndex]))
						{
							ref = anno;
							break;
						}
					}
					if(ref==null)
						throw new RuntimeException("Complex values not found: "+detailsMap);
					
					List det = ref.getDetails();
					if(det!=null)
					{
						for(int i=0; i<det.size(); i++)
						{
							MAnnotationDetail ad = (MAnnotationDetail)det.get(i);
							valmap.put(ad.getKey(), ad.getValue());
						}
					}
					newTable.setComplexValue(newRow[columnIndex], valmap);
				}
			}
			newTable.add(newTable.new MultiColumnTableRow(newRow, newTable));
		}

		return newTable;

	}

	

}

/**
 * A cell index data type
 * @author Claas
 */
class TableCellIndex
{
	/** row dimension */
	int rowCount;
	
	/** column dimension */
	int columnCount;
	
	/**
	 * @param rowCount
	 * @param columnCount
	 */
	protected TableCellIndex(int rowCount, int columnCount)
	{
		this(rowCount, columnCount, null);
	}
	
	/**
	 * @param rowCount
	 * @param columnCount
	 */
	protected TableCellIndex(int rowCount, int columnCount, String cellDimension)
	{
		super();
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}

	/**
	 * @param dimension as string generated by toString() method
	 */
	protected TableCellIndex(String dimensionString)
	{
		String[] dimension = dimensionString.split(":");
		this.rowCount = Integer.valueOf(dimension[0]);
		this.columnCount = Integer.valueOf(dimension[1]);
	}
	
	// ---- methods ----
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return rowCount + ":" + columnCount;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount)
	{
		this.rowCount = rowCount;
	}

	/**
	 * @return the columnCount
	 */
	public int getColumnCount()
	{
		return columnCount;
	}

	/**
	 * @param columnCount the columnCount to set
	 */
	public void setColumnCount(int columnCount)
	{
		this.columnCount = columnCount;
	}

}
