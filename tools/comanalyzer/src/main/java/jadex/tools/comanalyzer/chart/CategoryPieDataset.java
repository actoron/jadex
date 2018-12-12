package jadex.tools.comanalyzer.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.PieDataset;


/**
 * A dataset that implements both PieDataset and CategoryDataset and can be used
 * for both types of charts. The dataset contains Lists rather than values. For
 * the interfaces getValue() retrieves the size of the list for a specific row
 * and column index
 */
public class CategoryPieDataset extends AbstractDataset implements PieDataset, CategoryDataset, Serializable
{

	// ------ constants ------

	/** Extact pie data by row */
	public static final int BY_ROW = 0;

	/** Extact pie data by column */
	public static final int BY_COLUMN = 1;

	// -------- attributes --------

	/** The extract mode for pie data */
	private int extract;

	/** A storage structure for the data. */
	private KeyedListsTable data;

	/** The string used to represent a null value. */
	private String nullValueString = "null";

	// -------- constructors --------

	/**
	 * Creates a new (empty) dataset.
	 */
	public CategoryPieDataset()
	{
		this.data = new KeyedListsTable();
		this.extract = BY_COLUMN;
	}

	// -------- CategoryDataset methods --------

	/**
	 * Adds or updates a list in the table and sends a DatasetChangeEvent to all
	 * registered listeners.
	 * 
	 * @param list the list (<code>null</code> permitted).
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 */
	public void setList(List list, Comparable rowKey, Comparable columnKey)
	{
		this.data.setList(list, rowKey, columnKey);
		fireDatasetChanged();
	}

	/**
	 * Adds an element to an existing list in the dataset and sends a
	 * DatasetChangeEvent to all registered listeners. If the existing list is
	 * <code>null</code>, a new list is created and the element is added.
	 * 
	 * @param element The element.
	 * @param rowKey The row key (<code>null</code> is converted to the
	 * nullstring).
	 * @param columnKey The column key (<code>null</code> is converted to the
	 * nullstring)..
	 */
	public void addElement(Object element, Comparable rowKey, Comparable columnKey)
	{
		this.data.addToList(element, convertNull(rowKey), convertNull(columnKey));
		fireDatasetChanged();
	}

	/**
	 * Removes an element from an existing list in the dataset and sends a
	 * DatasetChangeEvent to all registered listeners. If the existing list is
	 * <code>null</code>, a new list is created and the element is added.
	 * 
	 * @param element The element.
	 * @param rowKey The row key (<code>null</code> is converted to the
	 * nullstring).
	 * @param columnKey The column key (<code>null</code> is converted to the
	 * nullstring)..
	 */
	public void removeElement(Object element, Comparable rowKey, Comparable columnKey)
	{
		this.data.removeFromList(element, convertNull(rowKey), convertNull(columnKey));
		fireDatasetChanged();
	}

	/**
	 * Retrun the list for a given row and column key.
	 * 
	 * @param rowKey The row key (<code>null</code> is converted to the
	 * nullstring).
	 * @param columnKey The column key (<code>null</code> is converted to the
	 * nullstring)..
	 * @return The list.
	 */
	public List getList(Comparable rowKey, Comparable columnKey)
	{
		return this.data.getList(convertNull(rowKey), convertNull(columnKey));

	}

	/**
	 * Removes a row from the dataset and sends a DatasetChangeEvent to all
	 * registered listeners.
	 * 
	 * @param rowIndex the row index.
	 */
	public void removeRow(int rowIndex)
	{
		this.data.removeRow(rowIndex);
		fireDatasetChanged();
	}

	/**
	 * Removes a row from the dataset and sends a DatasetChangeEvent to all
	 * registered listeners.
	 * 
	 * @param rowKey the row key.
	 */
	public void removeRow(Comparable rowKey)
	{
		this.data.removeRow(rowKey);
		fireDatasetChanged();
	}

	/**
	 * Removes a column from the dataset and sends a DatasetChangeEvent to all
	 * registered listeners.
	 * 
	 * @param columnIndex the column index.
	 */
	public void removeColumn(int columnIndex)
	{
		this.data.removeColumn(columnIndex);
		fireDatasetChanged();
	}

	/**
	 * Removes a column from the dataset and sends a DatasetChangeEvent to all
	 * registered listeners.
	 * 
	 * @param columnKey the column key.
	 */
	public void removeColumn(Comparable columnKey)
	{
		this.data.removeColumn(columnKey);
		fireDatasetChanged();
	}

	/**
	 * Clears all data from the dataset and sends a DatasetChangeEvent} to all
	 * registered listeners.
	 */
	public void clear()
	{
		this.data.clear();
		fireDatasetChanged();
	}

	// -------- CategoryDataset interface --------

	/**
	 * Returns the number of rows in the table.
	 * 
	 * @return The row count.
	 */
	public int getRowCount()
	{
		return this.data.getRowCount();
	}

	/**
	 * Returns the number of columns in the table.
	 * 
	 * @return The column count.
	 */
	public int getColumnCount()
	{
		return this.data.getColumnCount();
	}

	/**
	 * Returns a value from the table.
	 * 
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * 
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(int row, int column)
	{
		return this.data.getValue(row, column);
	}

	/**
	 * Returns a row key.
	 * 
	 * @param row the row index (zero-based).
	 * 
	 * @return The row key.
	 */
	public Comparable getRowKey(int row)
	{
		return this.data.getRowKey(row);
	}

	/**
	 * Returns the row index for a given key.
	 * 
	 * @param key the row key.
	 * @return The row index.
	 */
	public int getRowIndex(Comparable key)
	{
		return this.data.getRowIndex(key);
	}

	/**
	 * Returns the row keys.
	 * 
	 * @return The keys.
	 */
	public List getRowKeys()
	{
		return this.data.getRowKeys();
	}

	/**
	 * Returns a column key.
	 * 
	 * @param column the column index (zero-based).
	 * 
	 * @return The column key.
	 */
	public Comparable getColumnKey(int column)
	{
		return this.data.getColumnKey(column);
	}

	/**
	 * Returns the column index for a given key.
	 * 
	 * @param key the column key.
	 * 
	 * @return The column index.
	 */
	public int getColumnIndex(Comparable key)
	{
		return this.data.getColumnIndex(key);
	}

	/**
	 * Returns the column keys.
	 * 
	 * @return The keys.
	 */
	public List getColumnKeys()
	{
		return this.data.getColumnKeys();
	}

	/**
	 * Returns the value for a pair of keys.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 * 
	 * @return The value (possibly <code>null</code>).

	 */
	public Number getValue(Comparable rowKey, Comparable columnKey)
	{
		return this.data.getValue(rowKey, columnKey);
	}

	// -------- PieDataset methods --------

	/**
	 * @return the extract mode for pie data
	 */
	public int getExtractMode()
	{
		return extract;
	}

	/**
	 * @param extractMode the extract mode to set
	 */
	public void setExtractMode(int extractMode)
	{
		this.extract = extractMode;
	}

	/**
	 * Returns the list of all pie elements.
	 * 
	 * @param key The key (<code>null</code> not permitted).
	 * 
	 * @return The list (possibly <code>null</code>).
	 */
	public List getList(Comparable key)
	{
		List elements = new ArrayList();
		for(int i = 0; i < (extract == BY_ROW ? getColumnCount() : getRowCount()); i++)
		{
			List n = extract == BY_ROW ? getList(key, getColumnKey(i)) : getList(getRowKey(i), key);
			if(n != null)
			{
				elements.addAll(n);
			}
		}
		return elements;
	}

	/**
	 * Returns the list of elements from a specific pieindex.
	 * 
	 * @param key The key (<code>null</code> not permitted).
	 * @param pieindex The pieindex.
	 * 
	 * @return The list (possibly <code>null</code>).
	 */
	public List getList(Comparable key, int pieindex)
	{
		List elements;
		if(extract == BY_ROW)
		{
			elements = getList(key, getColumnKey(pieindex));
		}
		else
		{
			elements = getList(getRowKey(pieindex), key);
		}
		return elements;
	}

	// -------- PieDataset interface --------

	/**
	 * Returns the number of items in the pie dataset.
	 * 
	 * @return The item count.
	 */
	public int getItemCount()
	{
		return extract == BY_ROW ? getRowCount() : getColumnCount();
	}

	/**
	 * Returns a value for a specific item in the pie dataset.
	 * 
	 * @param item The item index.
	 * 
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(int item)
	{
		double val = 0;
		boolean isnull = true;
		for(int i = 0; i < (extract == BY_ROW ? getColumnCount() : getRowCount()); i++)
		{
			Number n = extract == BY_ROW ? getValue(item, i) : getValue(i, item);
			if(n != null)
			{
				val += n.doubleValue();
				isnull = false;
			}
		}
		return isnull ? null : Double.valueOf(val);
	}

	/**
	 * Returns the index for a key in the pie dataset, or -1 if the key is not
	 * recognised.
	 * 
	 * @param key the key (<code>null</code> not permitted).
	 * 
	 * @return The index, or <code>-1</code> if the key is unrecognised.
	 */
	public int getIndex(Comparable key)
	{
		return extract == BY_ROW ? getRowIndex(key) : getColumnIndex(key);
	}

	/**
	 * Returns the key for the specified item in the pie dataset, or
	 * <code>null</code>.
	 * 
	 * @param item the item index (in the range <code>0</code> to
	 * <code>getItemCount() - 1</code>).
	 * 
	 * @return The key, or <code>null</code>.
	 */
	public Comparable getKey(int item)
	{
		return extract == BY_ROW ? getRowKey(item) : getColumnKey(item);
	}

	/**
	 * Returns the categories in the pie dataset. The returned list is
	 * unmodifiable.
	 * 
	 * @return The categories in the dataset.
	 */
	public List getKeys()
	{
		return Collections.unmodifiableList(extract == BY_ROW ? getRowKeys() : getColumnKeys());
	}

	/**
	 * Returns the data value associated with a key.
	 * 
	 * @param key the key (<code>null</code> not permitted).
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(Comparable key)
	{
		return getValue(extract == BY_ROW ? getRowIndex(key) : getColumnIndex(key));
	}

	// -------- helper methods --------


	private Comparable convertNull(Comparable text)
	{
		return text == null ? nullValueString : text;
	}

}
