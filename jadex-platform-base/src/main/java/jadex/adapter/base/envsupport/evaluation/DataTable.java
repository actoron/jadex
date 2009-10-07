package jadex.adapter.base.envsupport.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class DataTable
{
	/** The table name. */
	protected String name;
	
	/** The table column names. */
	protected String[] columnnames;
	
	/** The data rows. */
	protected List rows;

	/**
	 *  Create a new data table. 
	 */
	public DataTable(String name, String[] columnnames)
	{
		this.name = name;
		this.columnnames = columnnames;
	}
	
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
	
}
