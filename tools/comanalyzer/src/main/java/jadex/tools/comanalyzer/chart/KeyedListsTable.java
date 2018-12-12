package jadex.tools.comanalyzer.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.data.KeyedValues2D;
import org.jfree.data.UnknownKeyException;


/**
 * A data structure that stores lists, where each list is associated with two
 * keys (a 'row' key and a 'column' key).
 */
public class KeyedListsTable implements KeyedValues2D, Cloneable, Serializable//, PublicCloneable
{

	// -------- attributes --------

	/** The row keys. */
	private List rowKeys;

	/** The column keys. */
	private List columnKeys;

	/** The row data. comparable (rowkey) -> comparable (columnkey), list */
	private Map rows;

	/** If the row keys should be sorted by their comparable order. */
	private boolean sortRowKeys;

	// -------- constructors --------

	/**
	 * Creates a new table.
	 */
	public KeyedListsTable()
	{
		this(false);
	}

	/**
	 * Creates a new table.
	 * 
	 * @param sortRowKeys if the row keys should be sorted.
	 */
	public KeyedListsTable(boolean sortRowKeys)
	{
		this.rowKeys = new ArrayList();
		this.columnKeys = new ArrayList();
		this.rows = new HashMap();
		this.sortRowKeys = sortRowKeys;

	}

	// -------- KeyedListsTable methods --------

	/**
	 * Returns the List for the given row and column keys.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 * 
	 * @return The List (possibly <code>null</code>).
	 */
	public List getList(Comparable rowKey, Comparable columnKey)
	{
		if(rowKey == null)
		{
			throw new IllegalArgumentException("Null 'rowKey' argument.");
		}
		if(columnKey == null)
		{
			throw new IllegalArgumentException("Null 'columnKey' argument.");
		}

		// check that the column key is defined in the 2D structure
		if(!(this.columnKeys.contains(columnKey)))
		{
			throw new UnknownKeyException("Unrecognised columnKey: " + columnKey);
		}

		// check that the column key is defined in the 2D structure
		if(!(this.rowKeys.contains(rowKey)))
		{
			throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
		}

		List element = null;

		// the row may not have an entry for this key, in which case the
		// return value is null
		if(rows.containsKey(rowKey) && ((Map)rows.get(rowKey)).containsKey(columnKey))
		{
			element = (List)((Map)rows.get(rowKey)).get(columnKey);
		}
		return element;
	}

	/**
	 * Adds or updates a list.
	 * 
	 * @param list the list (<code>null</code> permitted).
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void setList(List list, Comparable rowKey, Comparable columnKey)
	{

		if(!rowKeys.contains(rowKey))
		{
			rowKeys.add(rowKey);
		}
		if(!columnKeys.contains(columnKey))
		{
			columnKeys.add(columnKey);
		}
		if(!rows.containsKey(rowKey))
		{
			rows.put(rowKey, new HashMap());
		}

		((Map)rows.get(rowKey)).put(columnKey, list);

	}

	/**
	 * Adds a element to a list.
	 * 
	 * @param element the element to add (<code>null</code> permitted).
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void addToList(Object element, Comparable rowKey, Comparable columnKey)
	{

		if(!rowKeys.contains(rowKey))
		{
			rowKeys.add(rowKey);
		}
		if(!columnKeys.contains(columnKey))
		{
			columnKeys.add(columnKey);
		}
		if(!rows.containsKey(rowKey))
		{
			rows.put(rowKey, new HashMap());
		}
		if(!((Map)rows.get(rowKey)).containsKey(columnKey))
		{
			List list = new ArrayList();
			list.add(element);
			((Map)rows.get(rowKey)).put(columnKey, list);
		}
		else
		{
			List list = (List)((Map)rows.get(rowKey)).get(columnKey);
			list.add(element);
		}
	}

	/**
	 * Removes a value from the table by setting it to <code>null</code>. If
	 * all the values in the specified row and/or column are now
	 * <code>null</code>, the row and/or column is removed from the table.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void removeList(Comparable rowKey, Comparable columnKey)
	{
		if(rows.containsKey(rowKey))
		{
			((Map)rows.get(rowKey)).remove(columnKey);
		}

		// check whether the row is now empty.
		if(((Map)rows.get(rowKey)).isEmpty())
		{
			rowKeys.remove(rowKey);
			rows.remove(rowKey);
		}

		// check whether the column is now empty.
		boolean allNull = true;
		for(Iterator iter = rows.keySet().iterator(); iter.hasNext();)
		{
			Comparable key = (Comparable)iter.next();
			if(((Map)rows.get(key)).containsKey(columnKey))
			{
				allNull = false;
				break;
			}
		}
		if(allNull)
		{
			columnKeys.remove(columnKey);
		}

		// check if there are no rows at all
		if(rows.isEmpty())
		{
			columnKeys.clear();
		}
	}

	/**
	 * Removes an element from the table. If the list is empty, it is set to
	 * <code>null</code>. If all the values in the specified row and/or
	 * column are now <code>null</code>, the row and/or column is removed
	 * from the table.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void removeFromList(Object element, Comparable rowKey, Comparable columnKey)
	{
		if(rows.containsKey(rowKey))
		{
			List list = (List)((Map)rows.get(rowKey)).get(columnKey);
			if(list != null)
			{
				list.remove(element);
				if(list.isEmpty())
				{
					removeList(rowKey, columnKey);
				}
			}
		}

	}

	/**
	 * Removes a row.
	 * 
	 * @param rowIndex the row index.
	 */
	public void removeRow(int rowIndex)
	{
		Comparable rowKey = getRowKey(rowIndex);
		removeRow(rowKey);
	}

	/**
	 * Removes a row. If all values of any column are <code>null</code>, the
	 * column(s) is/are removed.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 */
	public void removeRow(Comparable rowKey)
	{
		rowKeys.remove(rowKey);
		rows.remove(rowKey);

		// check whether any column is now empty.
		boolean allNull;
		Comparable cKey = null;
		for(Iterator iterRow = rows.keySet().iterator(); iterRow.hasNext();)
		{
			Comparable rKey = (Comparable)iterRow.next();
			allNull = true;
			for(Iterator iterCol = columnKeys.iterator(); iterCol.hasNext();)
			{
				cKey = (Comparable)iterCol.next();
				if(((Map)rows.get(rKey)).containsKey(cKey))
				{
					allNull = false;
					break;
				}
			}
			if(allNull)
			{
				columnKeys.remove(cKey);
			}
		}

		// clear columns if there is no row left
		if(rowKeys.isEmpty())
		{
			columnKeys.clear();
		}
	}

	/**
	 * Removes a column.
	 * 
	 * @param columnIndex the column index.
	 */
	public void removeColumn(int columnIndex)
	{
		Comparable columnKey = getColumnKey(columnIndex);
		removeColumn(columnKey);
	}

	/**
	 * Removes a column.
	 * 
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void removeColumn(Comparable columnKey)
	{
		for(Iterator iter = rows.keySet().iterator(); iter.hasNext();)
		{
			Comparable rowKey = (Comparable)iter.next();
			((Map)rows.get(rowKey)).remove(columnKey);
			// check if row is empty
			if(((Map)rows.get(rowKey)).isEmpty())
			{
				rows.remove(rowKey);
				rowKeys.remove(rowKey);
			}

		}
		this.columnKeys.remove(columnKey);

	}

	/**
	 * Clears all the data and associated keys.
	 */
	public void clear()
	{
		this.rowKeys.clear();
		this.columnKeys.clear();
		this.rows.clear();
	}

	// -------- KeyedValues2D interface --------

	/**
	 * Returns the row count.
	 * 
	 * @return The row count.
	 */
	public int getRowCount()
	{
		return this.rowKeys.size();
	}

	/**
	 * Returns the column count.
	 * 
	 * @return The column count.
	 */
	public int getColumnCount()
	{
		return this.columnKeys.size();
	}

	/**
	 * Returns the value for a given row and column.
	 * 
	 * @param row the row index.
	 * @param column the column index.
	 * 
	 * @return The value.
	 */
	public Number getValue(int row, int column)
	{
		if(columnKeys.isEmpty() || rowKeys.isEmpty())
		{
			System.err.println(columnKeys + " " + rowKeys + " " + row + " " + column);
			return null;
		}

		Number result = null;
		Comparable columnKey = (Comparable)this.columnKeys.get(column);
		Comparable rowKey = (Comparable)this.rowKeys.get(row);

		// if (rowKey != null && columnKey != null ) {
		// the row may not have an entry for this key, in which case the
		// return value is null
		if(rows.containsKey(rowKey) && ((Map)rows.get(rowKey)).containsKey(columnKey))
		{
			result = Integer.valueOf(((Collection)((Map)rows.get(rowKey)).get(columnKey)).size());
		}
		// }
		return result;
	}

	/**
	 * Returns the value for the given row and column keys.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 * 
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(Comparable rowKey, Comparable columnKey)
	{
		if(rowKey == null)
		{
			throw new IllegalArgumentException("Null 'rowKey' argument.");
		}
		if(columnKey == null)
		{
			throw new IllegalArgumentException("Null 'columnKey' argument.");
		}

		// check that the column key is defined in the 2D structure
		if(!(this.columnKeys.contains(columnKey)))
		{
			throw new UnknownKeyException("Unrecognised columnKey: " + columnKey);
		}

		// check that the column key is defined in the 2D structure
		if(!(this.rowKeys.contains(rowKey)))
		{
			throw new UnknownKeyException("Unrecognised rowKey: " + rowKey);
		}

		Number result = null;
		// the row may not have an entry for this key, in which case the
		// return value is null
		if(rows.containsKey(rowKey) && ((Map)rows.get(rowKey)).containsKey(columnKey))
		{
			result = Integer.valueOf(((Collection)((Map)rows.get(rowKey)).get(columnKey)).size());
		}
		return result;
	}

	/**
	 * Returns the key for a given row.
	 * 
	 * @param row the row index (in the range 0 to {@link #getRowCount()} - 1).
	 * 
	 * @return The row key.
	 */
	public Comparable getRowKey(int row)
	{
		return (Comparable)this.rowKeys.get(row);
	}

	/**
	 * Returns the row index for a given key.
	 * 
	 * @param key the key (<code>null</code> not permitted).
	 * 
	 * @return The row index.
	 */
	public int getRowIndex(Comparable key)
	{
		if(key == null)
		{
			throw new IllegalArgumentException("Null 'key' argument.");
		}
		if(this.sortRowKeys)
		{
			return Collections.binarySearch(this.rowKeys, key);
		}
		else
		{
			return this.rowKeys.indexOf(key);
		}
	}

	/**
	 * Returns the row keys in an unmodifiable list.
	 * 
	 * @return The row keys.
	 */
	public List getRowKeys()
	{
		return Collections.unmodifiableList(this.rowKeys);
	}

	/**
	 * Returns the key for a given column.
	 * 
	 * @param column the column (in the range 0 to {@link #getColumnCount()} -
	 * 1).
	 * 
	 * @return The key.
	 */
	public Comparable getColumnKey(int column)
	{
		return (Comparable)this.columnKeys.get(column);
	}

	/**
	 * Returns the column index for a given key.
	 * 
	 * @param key the key (<code>null</code> not permitted).
	 * 
	 * @return The column index.
	 */
	public int getColumnIndex(Comparable key)
	{
		if(key == null)
		{
			throw new IllegalArgumentException("Null 'key' argument.");
		}
		return this.columnKeys.indexOf(key);
	}

	/**
	 * Returns the column keys in an unmodifiable list.
	 * 
	 * @return The column keys.
	 */
	public List getColumnKeys()
	{
		return Collections.unmodifiableList(this.columnKeys);
	}

	// -------- PublicCloneable interface --------

	/**
	 * Returns a clone.
	 * 
	 * @return A clone.
	 * /
	public Object clone() throws CloneNotSupportedException
	{
		KeyedListsTable clone = (KeyedListsTable) super.clone();
		// for the keys, a shallow copy should be fine because keys
		// should be immutable...
		clone.columnKeys = new java.util.ArrayList(this.columnKeys);
		clone.rowKeys = new java.util.ArrayList(this.rowKeys);

		// but the row data requires a deep copy
		clone.rows = (Map)ObjectUtilities.deepClone((Collection)this.rows);
		return clone;
	}*/
}
