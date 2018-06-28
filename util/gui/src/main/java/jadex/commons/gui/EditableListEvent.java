package jadex.commons.gui;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *  Event that is able to capture data of rows.
 *  Important for deletes. 
 */
public class EditableListEvent extends TableModelEvent
{
	/** The data. */
	protected Object[] data;
	
	/**
	 *  Create a new editable list event.
	 */
	public EditableListEvent(TableModel source, int firstRow, int lastRow, int column, int type, Object[] data)
	{
		super(source, firstRow, lastRow, column, type);
		this.data = data;
	}
	
	/**
	 *  Get the data for a specific row.
	 *  @param idx The index.
	 *  @return The data. 
	 */
	public Object getData(int idx)
	{
		return data[idx];
	}
	
	/**
	 *  Get all data.
	 *  @return The data.
	 */
	public Object[] getData()
	{
		return data;
	}
}