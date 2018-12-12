package jadex.extension.envsupport.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Basic data structure for all collected data.
 */
public class DataTable
{
	//-------- attributes --------
	
	/** The table name. */
	protected String name;
	
	/** The table column names. */
	protected String[] columnnames;
	
	/** The data rows. */
	protected List rows;
	
	/** The map of column indices. */
	protected Map columns;

	//-------- constructors --------
	
	/**
	 *  Create a new data table. 
	 */
	public DataTable(String name, String[] columnnames)
	{
		this.name = name;
		this.columnnames = columnnames;
		this.columns = new HashMap();
		for(int i=0; i<columnnames.length; i++)
			columns.put(columnnames[i], Integer.valueOf(i));
	}
	
	//-------- methods --------
	
	/**
	 *  Add a data row.
	 *  @param row The data row.
	 */
	public void addRow(Object[] row)
	{
		if(row.length!=columnnames.length)
			throw new IllegalArgumentException("Data row must have same length as table: "+row.length+" "+columnnames.length);
		if(rows==null)
			rows = new ArrayList();
		rows.add(row);
	}
	
	/**
	 *  Get the data rows.
	 *  @return The data rows.
	 */
	public List getRows()
	{
		return rows==null? Collections.EMPTY_LIST: rows;
	}

	/**
	 *  Get the columnnames.
	 *  @return The columnnames.
	 */
	public String[] getColumnNames()
	{
		return this.columnnames;
	}
	
	/**
	 *  Get the column index for a column name.
	 *  @param columnname The column name.
	 *  @return The column index.
	 */
	public int getColumnIndex(String columnname)
	{
		Integer ret = (Integer)columns.get(columnname);
		return ret==null? -1: ret.intValue();
	}
	
	/**
	 *  Get the data element from a specific row and column.
	 */
	public Object getData(int row, int column)
	{
		return ((Object[])rows.get(row))[column];
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}	
	
}
