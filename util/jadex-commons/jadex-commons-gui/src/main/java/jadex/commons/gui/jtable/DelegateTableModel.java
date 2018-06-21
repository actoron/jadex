package jadex.commons.gui.jtable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * DelegateTableModel implements TableModel by routing all requests to its delegate model, and
 * TableModelListener by routing all events to its listeners. Inserting
 * a DelegateTableModel which has not been subclassed into a chain of table filters
 * should have no effect.
 */
public class DelegateTableModel extends AbstractTableModel implements TableModelListener
{
    // Storage of reference to model being filtered
    protected TableModel delegate;

    // Constructor - takes a single parameter containing
    // a reference to the model being filtered
    public DelegateTableModel(TableModel delegate)
    {
        this.delegate = delegate;
        this.delegate.addTableModelListener(this);
    }

    public TableModel getDelegate()
    {
        return delegate;
    }

    // By default, implement TableModel by forwarding all messages
    // to the model.

    public int getRowCount()
    {
        return delegate.getRowCount();
    }

    public int getColumnCount()
    {
        return delegate.getColumnCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return delegate.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        delegate.setValueAt(aValue, rowIndex, columnIndex);
    }

    public String getColumnName(int columnIndex)
    {
        return delegate.getColumnName(columnIndex);
    }

    public Class getColumnClass(int columnIndex)
    {
        return delegate.getColumnClass(columnIndex);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return delegate.isCellEditable(rowIndex, columnIndex);
    }

    // By default forward all events to all the listeners.
    public void tableChanged(TableModelEvent e)
    {
        fireTableChanged(e);
    }

}
