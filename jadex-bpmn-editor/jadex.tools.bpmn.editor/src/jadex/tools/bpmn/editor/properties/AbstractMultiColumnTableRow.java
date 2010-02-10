/**
 * 
 */
package jadex.tools.bpmn.editor.properties;


/**
 * @author Claas
 *
 */
/**
 * Representation of a MultiColumnTableRow
 * 
 * @author Claas Altschaffel
 */
public class AbstractMultiColumnTableRow {
	
	// ---- attributes ----

	private String[] columnValues;
	private int uniqueColumnIndex;
	
	// ---- constructors ----
	
	/** default constructor */
	public AbstractMultiColumnTableRow(String[] columnValues, int uniqueColumnIndex)
	{
		super();
		
		this.uniqueColumnIndex = uniqueColumnIndex;
		
		this.columnValues = new String[columnValues.length];
		for (int i = 0; i < columnValues.length; i++)
		{
			assert columnValues[i] != null : "Value for column index '"+i+"' is null";
			this.columnValues[i] = new String(columnValues[i]);
		}
		
		//this.columnValues = columnValues;
		
	}
	
	// ---- methods ----
	
	/** check if the unique column index is valid and can be used */
	private boolean useUniqueColumn()
	{
		return uniqueColumnIndex >= 0 && uniqueColumnIndex < columnValues.length;
	}

	// ---- overrides ----
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AbstractMultiColumnTableRow))
		{
			return false;
		}
		
		boolean returnValue = true;
		if (useUniqueColumn())
		{
			returnValue = this.columnValues[uniqueColumnIndex].equals(((AbstractMultiColumnTableRow) obj).columnValues[uniqueColumnIndex]);
		}
		else
		{
			for (int i = 0; returnValue && i < this.columnValues.length; i++)
			{
				returnValue =  returnValue &&  this.columnValues[i].equals(((AbstractMultiColumnTableRow) obj).columnValues[i]);
			}
		}

		return returnValue;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int returnHash = 31;
		
		if (useUniqueColumn())
		{
			returnHash = this.columnValues[uniqueColumnIndex].hashCode();
		}
		else
		{
			for (int i = 0; i < this.columnValues.length; i++)
			{
				returnHash = returnHash + this.columnValues[i].hashCode() * 31;
			}
		}
		return returnHash;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("AbstractMultiColumnTableRow(");
		for (int i = 0; i < this.columnValues.length; i++)
		{
			buffer.append("`");
			buffer.append(columnValues[i]);
			buffer.append("´" + ", ");
		}
		// remove last delimiter
		buffer.delete(buffer.length()-", ".length(), buffer.length());
		buffer.append(")");
		System.out.println(buffer.toString());
		return buffer.toString();
	}
	
//	/**
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString()
//	{
//		StringBuffer buffer = new StringBuffer();
//		for (int i = 0; i < this.columnValues.length; i++)
//		{
//			buffer.append(columnValues[i]);
//			buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
//		}
//		// remove last delimiter
//		buffer.delete(buffer.length()-LIST_ELEMENT_ATTRIBUTE_DELIMITER.length(), buffer.length());
//		System.out.println(buffer.toString());
//		return buffer.toString();
//	}
	
	// ---- getter / setter ----
	
	/**
	 * @return the columnValues
	 */
	public String[] getColumnValues()
	{
		return columnValues;
	}

	/**
	 * @param columnValues the columnValues to set
	 */
	public void setColumnValues(String[] values)
	{
		this.columnValues = values;
	}

	/**
	 * @param columnIndex to get value
	 * @return the value at index
	 */
	public String getColumnValueAt(int columnIndex)
	{
		return columnValues[columnIndex];
	}

	/**
	 * @param columnIndex to set the value
	 * @param value the value to set
	 * 
	 */
	public void setColumnValueAt(int columnIndex, String value)
	{
		this.columnValues[columnIndex] = value;
	}

}
