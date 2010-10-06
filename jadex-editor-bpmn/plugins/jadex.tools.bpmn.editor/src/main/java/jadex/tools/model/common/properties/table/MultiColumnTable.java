/**
 * 
 */
package jadex.tools.model.common.properties.table;

import jadex.tools.bpmn.editor.properties.template.AbstractBpmnPropertySection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * @author Claas
 *
 */
public class MultiColumnTable
{
	/*
	public static final void main(String[] args) 
	{
		try
		{
			int i = 0;
			for (byte b = 0; b < 32; b++)
			{
				System.out.println(i+": " + new String(new byte[] { b }, "US-ASCII"));
				i++;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	*/
	
	// ---- constants ----
	
	/** 
	 * String delimiter for list elements <p>
	 * <p><code>\u001E</code> RS 	Record Separator</p>
	 */
	public static final String LIST_ELEMENT_DELIMITER = "\u241F"; // "<*>";
	public static final String DEPRECATED_LIST_ELEMENT_DELIMITER = "\u241F"; // "<*>";
	
	/** 
	 * String delimiter for element attributes  <p>
	 * <p><code>\u001F</code> US 	Unit Separator </p>
	 */
	public static final String LIST_ELEMENT_ATTRIBUTE_DELIMITER = "\u240B";
	public static final String DEPRECATED_LIST_ELEMENT_ATTRIBUTE_DELIMITER = "\u240B"; //"#|#";

	// ---- attributes ----
	
	/** The list of rows in this table */
	private List<MultiColumnTableRow> rows;
	
	private int uniqueColumn = 0;
	
	// ---- constructor ----
	
	/**
	 * 
	 */
	public MultiColumnTable(int rowCount, int uniqueColumn)
	{
		super();
		this.uniqueColumn = uniqueColumn;
		this.rows = new ArrayList<MultiColumnTableRow>(rowCount);
	}

	// ---- methods ----
	
	/**
	 * Access the back-end row list
	 * @return the internal list of rows as unmodifiable list
	 */
	public List<MultiColumnTableRow> getRowList()
	{
		return Collections.unmodifiableList(rows);
	}
	
	/**
	 * Get the size of a row element
	 * @return the number of elements for a row in this table
	 */
	public int getRowSize()
	{
		if (!rows.isEmpty())
		{
			// ensure an element is found (don't use index 0 statically)
			for (MultiColumnTableRow row : rows)
			{
				return row.size();
			}
		}
		
		return 0;
	}
	
	/**
	 * @return the uniqueColumnIndex
	 */
	public int getUniqueColumn()
	{
		return uniqueColumn;
	}
	
	// ---- List delegations ----
	
	
	/**
	 * @param index
	 * @param row
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, MultiColumnTableRow row)
	{
		row.setTable(this);
		rows.add(index, row);
	}

	/**
	 * @param row
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(MultiColumnTableRow row)
	{
		row.setTable(this);
		return rows.add(row);
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear()
	{
		rows.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o)
	{
		return rows.contains(o);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		return rows.equals(o);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public MultiColumnTableRow get(int index)
	{
		return rows.get(index);
	}

	/**
	 * @return
	 * @see java.util.List#hashCode()
	 */
	public int hashCode()
	{
		return rows.hashCode();
	}


	/**
	 * @param o
	 * @return
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		return rows.indexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return rows.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<MultiColumnTableRow> iterator()
	{
		return Collections.unmodifiableList(rows).iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o)
	{
		return rows.lastIndexOf(o);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#remove(int)
	 */
	public MultiColumnTableRow remove(int index)
	{
		MultiColumnTableRow row = rows.remove(index);
		row.setTable(null);
		return  row;
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		return rows.remove(o);
	}

	/**
	 * @param index
	 * @param element
	 * @return
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public MultiColumnTableRow set(int index, MultiColumnTableRow element)
	{
		return rows.set(index, element);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size()
	{
		return rows.size();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see java.util.List#subList(int, int)
	 */
	public List<MultiColumnTableRow> subList(int fromIndex, int toIndex)
	{
		return Collections.unmodifiableList(rows.subList(fromIndex, toIndex)); 
	}

	/**
	 * @return
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray()
	{
		return rows.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] a)
	{
		return rows.toArray(a);
	}
	
	
	
	// ---- @deprecated static conversion methods ----

	/**
	 * Convert a string representation of a MultiColumnTableRow list into a
	 * MultiColumnTableRow list
	 * 
	 * @param stringToConvert
	 * @param columnCount number of columns to expect in the table (e.g. attribute count)
	 * @param uniqueColumn, set to -1 for no unique column (beware, someone could get confused!)
	 * @return List of column rows from String, each column rows has set the unique column attribute
	 * @deprecated We use a annotation as table now
	 */
	public static MultiColumnTable convertMultiColumnTableString(
			String stringToConvert, int uniqueColumn)
	{

		StringTokenizer listTokens = new StringTokenizer(stringToConvert, LIST_ELEMENT_DELIMITER, false);
		
		long countRowTokens = listTokens.countTokens();
		MultiColumnTable table = new MultiColumnTable((int)countRowTokens/2, uniqueColumn);
		
		while (listTokens.hasMoreElements())
		{
			String parameterElement = listTokens.nextToken();
			StringTokenizer parameterTokens = new StringTokenizer(
					parameterElement,
					LIST_ELEMENT_ATTRIBUTE_DELIMITER, true);
	
			// number of columns (tokens / 2 +1) is the index that will be used.
			int countColumnTokens = ((int)(parameterTokens.countTokens() / 2))+1;
			// initialize array with empty strings because we 
			// don't check the values
			String[] attributes = new String[countColumnTokens];
			for (int index = 0; index < attributes.length; index++)
			{
				attributes[index] = "";
			}
			
			int attributeIndexCounter = 0;	
			String lastToken = null;
	
			while (parameterTokens.hasMoreElements())
			{
				String attributeToken = parameterTokens.nextToken();
	
				if (!attributeToken.equals(LIST_ELEMENT_ATTRIBUTE_DELIMITER))
				{
					attributes[attributeIndexCounter] = attributeToken;
					attributeIndexCounter++;
				}
				// we found a delimiter
				else
				{
					if (	// we found a delimiter at the first position
							lastToken == null 
							// we found a delimiter at the last position, 
							|| !parameterTokens.hasMoreElements()
							// we found two delimiter without any content between
							|| attributeToken.equals(lastToken))
					{
						attributes[attributeIndexCounter] = "";
						attributeIndexCounter++;
					}
					
				}
	
				// remember last token
				lastToken = attributeToken;
	
			} // end while paramTokens
	
			MultiColumnTableRow newRow = table.new MultiColumnTableRow(attributes, table);
			//addUniqueRowValue(newRow.getColumnValueAt(uniqueColumnIndex));
			table.add(newRow);
	
		} // end while listTokens
		
		return table;
	}
	
	
	/**
	 * Representation of a MultiColumnTableRow
	 * 
	 * @author Claas Altschaffel
	 */
	public class MultiColumnTableRow {
		
		// ---- attributes ----

		private String[] columnValues;
		//private int uniqueColumnIndex;
		private MultiColumnTable table;
		
		// ---- constructors ----

		/** default constructor */
		public MultiColumnTableRow(String[] columnValues, MultiColumnTable parent)
		{
			super();
			
			table = parent;
			
			this.columnValues = new String[columnValues.length];
			for (int i = 0; i < columnValues.length; i++)
			{
				//assert columnValues[i] != null : "Value for column index '"+i+"' is null";
				this.columnValues[i] = new String(columnValues[i] != null ? columnValues[i] : "");
			}
			
			//this.columnValues = columnValues;
			
		}

		// ---- methods ----
		
		/** check if the unique column index is valid and can be used */
		private boolean useUniqueColumn()
		{
			int uniqueColumnIndex = getUniqueColumnIndex();
			return uniqueColumnIndex >= 0 && uniqueColumnIndex < columnValues.length;
		}
		
		

		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof MultiColumnTableRow))
			{
				return false;
			}
			
			boolean returnValue = true;
			if (useUniqueColumn())
			{
				returnValue = this.columnValues[getUniqueColumnIndex()].equals(((MultiColumnTableRow) obj).columnValues[getUniqueColumnIndex()]);
			}
			else
			{
				for (int i = 0; returnValue && i < this.columnValues.length; i++)
				{
					returnValue =  returnValue &&  this.columnValues[i].equals(((MultiColumnTableRow) obj).columnValues[i]);
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
				returnHash = this.columnValues[getUniqueColumnIndex()].hashCode();
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
			buffer.append("MultiColumnTableRow(");
			for (int i = 0; i < this.columnValues.length; i++)
			{
				buffer.append("`");
				buffer.append(columnValues[i]);
				buffer.append("�" + ", ");
			}
			// remove last delimiter
			buffer.delete(buffer.length()-", ".length(), buffer.length());
			buffer.append(")");
			System.out.println(buffer.toString());
			return buffer.toString();
		}
		
		/**
		 * Convenience method to access the length of this {@link MultiColumnTableRow}
		 * @return the number of values in a row
		 */
		public int size()
		{
			return columnValues.length;
		}
		
		// ---- getter / setter ----
		
		/**
		 * Get the parent for this row
		 * @return The parent table for this table row
		 */
		private MultiColumnTable getTable()
		{
			return table;
		}

		/**
		 * Set the parent table for this table row
		 * @param table the parent
		 */
		private void setTable(MultiColumnTable table)
		{
			this.table = table;
		}
		
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

		/**
		 * @return the uniqueColumnIndex
		 */
		public int getUniqueColumnIndex()
		{
			return getTable().getUniqueColumn();
		}
	}

}
