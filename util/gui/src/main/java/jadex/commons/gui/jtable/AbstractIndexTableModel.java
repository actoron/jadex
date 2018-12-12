package jadex.commons.gui.jtable;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;


public abstract class AbstractIndexTableModel extends DelegateTableModel
{
    // Collection of indices that pass the filtering criteria.
    protected ArrayList indexList = new ArrayList();


    public AbstractIndexTableModel(TableModel delegate)
    {
        super(delegate);
    }

    /**
     * Filter the model.
     * Subclasses should implement this methods to provide the filter logic.
     */
    public abstract void tableRowsInserted(int column,int firstRow,int lastRow);
    public abstract void tableRowsDeleted(int column,int firstRow, int lastRow);
    public abstract void tableRowsUpdated(int column,int firstRow,int lastRow);


    /**
     * Get the number of rows in the table.
     * @return The row count
     */
    public int getRowCount()
    {
        return indexList.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex)
    {
        // return the filtered data element
        int newRowIndex = ((Integer) indexList.get(rowIndex)).intValue();
        return delegate.getValueAt(newRowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        // set the filtered data element
        int newRowIndex = ((Integer) indexList.get(rowIndex)).intValue();
        delegate.setValueAt(aValue, newRowIndex, columnIndex);
    }


    /**
     * Signal that the table has changed in some way.
     * @param evt The TableModelEvent
     */
    public void tableChanged(TableModelEvent evt)
    {
        if (evt.getType()==TableModelEvent.DELETE) {
            tableRowsDeleted(evt.getColumn(),evt.getFirstRow(),evt.getLastRow());
        }

        if (evt.getType()==TableModelEvent.INSERT) {
            tableRowsInserted(evt.getColumn(),evt.getFirstRow(),evt.getLastRow());
        }

        if (evt.getType()==TableModelEvent.UPDATE) {
            tableRowsUpdated(evt.getColumn(),evt.getFirstRow(),evt.getLastRow());
        }

//        super.tableChanged(new TableModelEvent(this));
    }

    public int mapRow(int rowIndex)
    {
        int newRowIndex = ((Integer) indexList.get(rowIndex)).intValue();
        return newRowIndex;
    }

}
