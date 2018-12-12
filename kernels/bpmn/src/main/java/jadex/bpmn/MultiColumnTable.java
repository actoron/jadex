/**
 * 
 */
package jadex.bpmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * SIMPLIFIED and ADAPTED Copy of editor common table!
 * @author Claas
 *
 */
public class MultiColumnTable
{

	// ---- attributes ----
	
	/** The list of rows in this table */
	private List<MultiColumnTableRow> rows;
	
	// ---- constructors ----
	
	/**
	 * 
	 * @param rowCount
	 * @param uniqueColumn
	 */
	public MultiColumnTable(int rowCount)
	{
		super();
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
	 * @see java.util.List#add(java.lang.Object)
	 */
	public void add(MultiColumnTableRow row)
	{
		add(this.rows.size(), row);
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
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o)
	{
		return rows.contains(o);
	}

	/**
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		return (o instanceof MultiColumnTable) 
			&& rows.equals(((MultiColumnTable) o).rows);
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public MultiColumnTableRow get(int index)
	{
		return rows.get(index);
	}

	/**
	 * @see java.util.List#hashCode()
	 */
	public int hashCode()
	{
		return rows.hashCode();
	}


	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		return rows.indexOf(o);
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return rows.isEmpty();
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator<MultiColumnTableRow> iterator()
	{
		return Collections.unmodifiableList(rows).iterator();
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o)
	{
		return rows.lastIndexOf(o);
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public MultiColumnTableRow remove(int index)
	{
		MultiColumnTableRow row = rows.remove(index);
		row.setTable(null);
		
		return  row;
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		if (o instanceof MultiColumnTableRow)
		{
			int index = rows.indexOf(o);
			if (index >= 0)
			{
				this.remove(index);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public MultiColumnTableRow set(int index, MultiColumnTableRow row)
	{
		this.remove(index);
		this.add(index, row);
		
		return row;
	}

	/**
	 * @see java.util.List#size()
	 */
	public int size()
	{
		return rows.size();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List<MultiColumnTableRow> subList(int fromIndex, int toIndex)
	{
		return Collections.unmodifiableList(rows.subList(fromIndex, toIndex)); 
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray()
	{
		return rows.toArray();
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] a)
	{
		return rows.toArray(a);
	}
	

	/**
	 * ADAPTED!!! Copy of common MultiColumnTableRow
	 * 
	 * @author Claas Altschaffel
	 */
	public class MultiColumnTableRow {
		
		// ---- attributes ----

		private Object[] columnValues;
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
				this.columnValues[i] = columnValues[i] != null ? columnValues[i] : "";
			}
			
			//this.columnValues = columnValues;
			
		}


		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof MultiColumnTableRow))
			{
				return false;
			}
			
			boolean returnValue = true;
			
			for (int i = 0; returnValue && i < this.columnValues.length; i++)
			{
				returnValue =  returnValue &&  this.columnValues[i].equals(((MultiColumnTableRow) obj).columnValues[i]);
			}
			

			return returnValue;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			int returnHash = 31;

			for (int i = 0; i < this.columnValues.length; i++)
			{
				returnHash = returnHash + this.columnValues[i].hashCode() * 31;
			}
			
			return returnHash;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("MultiColumnTableRow(");
			for (int i = 0; i < this.columnValues.length; i++)
			{
				buffer.append("\"");
				buffer.append(columnValues[i]);
				buffer.append("\"" + ", ");
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
		public MultiColumnTable getTable()
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
		public Object[] getColumnValues()
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
		public Object getColumnValueAt(int columnIndex)
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

}
