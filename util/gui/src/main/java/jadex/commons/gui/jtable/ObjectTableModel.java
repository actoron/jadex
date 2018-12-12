package jadex.commons.gui.jtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import jadex.commons.SUtil;
import jadex.commons.Tuple;

/**
 *  The object table model has associated an object with each row.
 */
public class ObjectTableModel extends AbstractTableModel
{
	//-------- attributes --------

	/** The data. */
	protected Vector columns;

	/** The data. */
	protected Vector data;

	/** The classes of the columns. */
	protected HashMap columnclasses;

	/** The editable colums. */
	protected HashSet columseditable;

	//-------- constructors --------

	/**
	 *  Create a new object table model.
	 */
	public ObjectTableModel()
	{
		this.data = new Vector();
	}

	/**
	 *  Create a new object table model.
	 */
	public ObjectTableModel(String[] colnames)
	{
		this.data = new Vector();
		this.columns = new Vector();
		this.columnclasses = new HashMap();
		this.columseditable = new HashSet();
		for(int i=0; i<colnames.length; i++)
			this.columns.add(colnames[i]);
	}

	//-------- methods --------

	/**
	 *  Add a row to the model.
	 *  @param row The row data.
	 *  @param object The associated object.
	 */
	public void addRow(Object row, Object object)
	{
		ArrayList r = new ArrayList();
		r.add(row);
        insertRow(getRowCount(), r, object);
    }

	/**
	 *  Add a row to the model.
	 *  @param row The row data.
	 *  @param object The associated object.
	 */
	public void addRow(ArrayList row, Object object)
	{
        insertRow(getRowCount(), row, object);
    }

	/**
	 *  Add a row to the model.
	 *  @param row The row data.
	 *  @param object The associated object.
	 */
	public void addRow(Object[] row, Object object)
	{
        insertRow(getRowCount(), SUtil.arrayToList(row), object);
    }

	/**
	 *  Insert a row at a position.
	 *  @param rowcnt The row cnt.
	 *  @param row The row data.
	 *  @param object The object.
	 */
	public void insertRow(int rowcnt, Object[] row, Object object)
	{
		insertRow(rowcnt, SUtil.arrayToList(row), object);
    }

	/**
	 *  Insert a row at a position.
	 *  @param rowcnt The row cnt.
	 *  @param row The row data.
	 *  @param object The object.
	 */
	public synchronized void insertRow(int rowcnt, List row, Object object)
	{
		data.add(rowcnt, new Tuple(row, object));
     	//System.out.println("DATA: "+data);
        fireTableRowsInserted(rowcnt, rowcnt);
    }

	/**
	 *  Remove a row from the model.
	 *  @param object The associated object.
	 */
	public synchronized void removeRow(Object object)
	{
        for(int i=0; i<getRowCount() && object!=null; i++)
		{
			if(object.equals(((Tuple)data.get(i)).get(1)))
			{
				data.remove(i);
		        fireTableRowsDeleted(i, i);
			}
		}
    }

	/**
	 *  Remove a row from the model.
	 *  @param cnt The row number.
	 */
	public synchronized void removeRow(int cnt)
	{
		data.remove(cnt);
		fireTableRowsDeleted(cnt, cnt);
    }

	/**
	 *  Remove all rows from the model.
	 */
	public synchronized void removeAllRows()
	{
		int size = data.size();
		if(size>0)
		{
			data.clear();
			fireTableRowsDeleted(0, size-1);
		}
	}

	/**
	 *  Modify a row at a position.
	 *  @param val The value.
	 *  @param rowcnt The row.
	 *  @param columncnt The column.
	 */
	public synchronized void modifyData(Object val, int rowcnt, int columncnt)
	{
		Tuple tuple = (Tuple)data.get(rowcnt);
		List da = (List)tuple.getEntity(0);
		da.remove(columncnt);
		da.add(columncnt, val);
		 //System.out.println("DATA: "+data);
        fireTableRowsInserted(rowcnt, rowcnt);
    }

	/**
	 *  Get the associated object for a row.
	 *  @param rowcnt The row ccount.
	 *  @return The object.
	 */
	public synchronized Object getObjectForRow(int rowcnt)
	{
		//System.out.println("All: "+rowcnt+data+" --- "+data.get(rowcnt));
		return ((Tuple)data.get(rowcnt)).get(1);
	}

	/**
	 *  Get a column name
	 *  @param column The number of the column.
	 *  @return The column name.
	 */
	public String getColumnName(int column)
	{
		return (String)columns.get(column);
    }

	/**
     *  Get the class of the column.
     *  @param idx  the column being queried
     *  @return the Object.class
     */
    public Class getColumnClass(int idx)
	{
		Class clazz = (Class)columnclasses.get(Integer.valueOf(idx));
    	if(clazz==null)
			clazz = Object.class;
		return clazz;
	}

	/**
     *  Set the class of the column.
	 *  @param clazz The class.
     *  @param idx  the column being queried
     */
    public void setColumnClass(Class clazz, int idx)
	{
		columnclasses.put(Integer.valueOf(idx), clazz);
    }

	/**
	 *  Set if a column is editable.
	 *  @param editable True, for editable.
	 *  @param col The column.
	 */
	public void setColumnEditable(boolean editable, int col)
	{
		if(editable)
			columseditable.add(Integer.valueOf(col));
		else
			columseditable.remove(Integer.valueOf(col));
	}

	/**
     *  Returns false.  This is the default implementation for all cells.
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columseditable.contains(Integer.valueOf(columnIndex));
    }

	//-------- AbstractTableModel interface --------

	/**
	 *
	 *  @return The row count.
	 */
	public int getRowCount()
	{
		return data.size();
	}

	/**
	 *
	 *  @return The column count.
	 */
	public int getColumnCount()
	{
		return columns.size();
	}

	/**
	 *  Get the value from a field.
	 *  @param row The row index.
	 *  @param column The column index.
	 *  @return The value.
	 */
	public synchronized Object getValueAt(int row, int column)
	{
		return ((ArrayList)((Tuple)data.get(row)).get(0)).get(column);
	}
}
