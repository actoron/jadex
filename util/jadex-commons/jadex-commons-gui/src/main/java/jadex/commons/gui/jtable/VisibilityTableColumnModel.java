package jadex.commons.gui.jtable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * <code>VisibilityTableColumnModel</code> extends the DefaultTableColumnModel .
 * It provides a comfortable way to hide/show columns.
 * Columns keep their positions when hidden and shown again.
 *
 * In order to work with JTable it cannot add any events to <code>TableColumnModelListener</code>.
 * Therefore hiding a column will result in <code>columnRemoved</code> event and showing it
 * again will notify listeners of a <code>columnAdded</code>, and possibly a <code>columnMoved</code> event.
 * For the same reason the following methods still deal with visible columns only:
 * getColumnCount(), getColumns(), getColumnIndex(), getColumn()
 * If you want to take invisible column into account use:
 * getAllColumnCount(), getAllColumns(), getAllColumnIndex(), getAllColumn()
 *
 * @see javax.swing.table.DefaultTableColumnModel
 */

public class VisibilityTableColumnModel extends DefaultTableColumnModel implements IVisibilityTableColumnModel
{
    /**
     * Array of TableColumn objects in this model.
     *  Holds all column objects, regardless of their visibility
     */
    protected Vector allTableColumns;

    /**
     * Array of TableColumn objects that
     * cannot change the visibility.
     * By default this array is empty
     */
    protected Vector tableColumnsFixed;

    /**
     * Creates an extended table column model.
     */
    public VisibilityTableColumnModel()
    {
        allTableColumns = new Vector();
        tableColumnsFixed = new Vector();
    }

    /**
     *  Appends <code>aColumn</code> to the end of the
     *  <code>tableColumns</code> array.
     *  This method posts a <code>columnAdded</code>
     *  event to its listeners.
     *
     * @param   column         the <code>TableColumn</code> to be added
     * @see     javax.swing.table.DefaultTableColumnModel#addColumn
     */
    public void addColumn(TableColumn column)
    {
        allTableColumns.addElement(column);
        super.addColumn(column);

    }

    /**
     *  Deletes the <code>TableColumn</code> <code>column</code> from the
     *  <code>tableColumns</code> array.  This method will do nothing if
     *  <code>column</code> is not in the table's column list.
     *  This method posts a <code>columnRemoved</code>
     *  event to its listeners.
     *
     * @param   column          the <code>TableColumn</code> to be removed
     * @see     javax.swing.table.DefaultTableColumnModel#removeColumn
     */
    public void removeColumn(TableColumn column)
    {
        allTableColumns.removeElement(column);
        super.removeColumn(column);
    }

    /**
     * Moves the column and its header at <code>columnIndex</code> to
     * <code>newIndex</code>.  The old column at <code>columnIndex</code>
     * will now be found at <code>newIndex</code>.  The column that used
     * to be at <code>newIndex</code> is shifted left or right
     * to make room.  This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>.  This method
     * posts a <code>columnMoved</code> event to its listeners.
     *
     * @param   columnIndex                     the index of column to be moved
     * @param   newIndex                        index of the column's new location
     * @exception java.lang.IllegalArgumentException      if <code>columnIndex</code> or
     *                                          <code>newIndex</code>
     *                                          are not in the valid range
     *
     * @see javax.swing.table.DefaultTableColumnModel#moveColumn
     */
    public void moveColumn(int columnIndex, int newIndex)
    {
        if ((columnIndex < 0) || (columnIndex >= getColumnCount()) || (newIndex < 0) || (newIndex >= getColumnCount()))
        {
            throw new IllegalArgumentException("moveColumn() - Index out of range");
        }

        TableColumn fromColumn = (TableColumn) tableColumns.get(columnIndex);
        TableColumn toColumn = (TableColumn) tableColumns.get(newIndex);

        int allColumnsOldIndex = allTableColumns.indexOf(fromColumn);
        int allColumnsNewIndex = allTableColumns.indexOf(toColumn);

        if (columnIndex != newIndex)
        {
            allTableColumns.removeElementAt(allColumnsOldIndex);
            allTableColumns.insertElementAt(fromColumn, allColumnsNewIndex);
        }

        super.moveColumn(columnIndex, newIndex);
    }

    //
    // Querying the model
    //

    /**
     * Returns the number of columns in the model.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @return the number of columns in the model
     */
    public int getAllColumnCount()
    {
        return allTableColumns.size();
    }

    /**
     * Returns an <code>Enumeration</code> of all the columns in the model.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @return an <code>Enumeration</code> of all the columns in the model
     */
    public Enumeration getAllColumns()
    {
        return allTableColumns.elements();
    }

    /**
     * Returns the index of the first column in the table
     * whose identifier is equal to <code>identifier</code>,
     * when compared using <code>equals</code>.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @param           identifier        the identifier object
     * @return          the index of the first table column
     *                  whose identifier is equal to <code>identifier</code>
     * @exception java.lang.IllegalArgumentException      if <code>identifier</code>
     *				is <code>null</code>, or no
     *				<code>TableColumn</code> has this
     *				<code>identifier</code>
     * @see             #getAllColumn
     */
    public int getAllColumnIndex(Object identifier)
    {
        if (identifier == null)
        {
            throw new IllegalArgumentException("Identifier is null");
        }

        Enumeration atc = allTableColumns.elements();
        TableColumn aColumn;
        int index = 0;

        while (atc.hasMoreElements())
        {
            aColumn = (TableColumn)atc.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (identifier.equals(aColumn.getIdentifier()))
                return index;
            index++;
        }
        throw new IllegalArgumentException("Identifier not found");
    }

    /**
     * Returns the <code>TableColumn</code> object for the column at
     * <code>columnIndex</code>.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @param   columnIndex     the index of the desired column
     * @return  the <code>TableColumn</code> object for
     *				the column at <code>columnIndex</code>
     */
    public TableColumn getAllColumn(int columnIndex)
    {
        return (TableColumn) allTableColumns.elementAt(columnIndex);
    }


    /**
     *  Sets the visibility of the specified TableColumn.
     *  The call is ignored if the TableColumn is not found in this column model
     *  or its visibility status did not change.
     *  This method posts a <code>columnAdded</code> or <code>columnRemoved</code>
     *  event to its listeners.
     *
     * @param column    the <code>TableColumn</code>
     * @param visible   its new visibility status
     */
    public void setColumnVisible(TableColumn column, boolean visible)
    {
        if (!visible)
        {
            super.removeColumn(column);
        }
        else
        {
            // find the visible index of the column:
            // iterate through both collections of visible and all columns, counting
            // visible columns up to the one that's about to be shown again
            int noVisibleColumns = tableColumns.size();
            int noInvisibleColumns = allTableColumns.size();
            int visibleIndex = 0;

            for (int invisibleIndex = 0; invisibleIndex < noInvisibleColumns; ++invisibleIndex)
            {
                TableColumn visibleColumn = (visibleIndex < noVisibleColumns ? (TableColumn) tableColumns.get(visibleIndex) : null);
                TableColumn testColumn = (TableColumn) allTableColumns.get(invisibleIndex);

                if (testColumn == column)
                {
                    if (visibleColumn != column)
                    {
                        super.addColumn(column);
                        super.moveColumn(tableColumns.size() - 1, visibleIndex);
                    }
                    return; // ####################
                }
                if (testColumn == visibleColumn)
                {
                    ++visibleIndex;
                }
            }

        }
    }


    /**
     * Makes all columns in this model visible
     *
     * @see #isColumnVisible
     * @see #setColumnVisible
     */
    public void setAllColumnsVisible()
    {
        int noColumns = allTableColumns.size();

        for (int columnIndex = 0; columnIndex < noColumns; ++columnIndex)
        {
            TableColumn visibleColumn = (columnIndex < tableColumns.size() ? (TableColumn) tableColumns.get(columnIndex) : null);
            TableColumn invisibleColumn = (TableColumn) allTableColumns.get(columnIndex);

            if (visibleColumn != invisibleColumn)
            {
                super.addColumn(invisibleColumn);
                super.moveColumn(tableColumns.size() - 1, columnIndex);
            }
        }
    }

    /**
     * Checks whether the specified column is currently visible.
     *
     * @param aColumn   column to check
     * @return          visibility  of specified column.
     *                  false if there is no such column at all.
     *
     * @see #setAllColumnsVisible
     * @see #setColumnVisible
     */
    public boolean isColumnVisible(TableColumn aColumn)
    {
        return tableColumns.contains(aColumn);
    }

    /**
     * Sets whether this column can change visibility.
     *
     * @param column        the <code>TableColumn</code>
     * @param changeable    if true, changing visibility is allowed;
     *                      otherwise false
     */
    public void setColumnChangeable(TableColumn column, boolean changeable)
    {
        tableColumnsFixed.remove(column);
        if (!changeable)
        {
            tableColumnsFixed.addElement(column);
        }
    }

    /**
     * Checks whether the specified column can change visibility.
     *
     * @param column   column to check
     * @return          true if the column can change visibility;
     *                  otherwise false
     */
    public boolean isColumnChangeable(TableColumn column)
    {
        return !tableColumnsFixed.contains(column);
    }

    /**
     * Helper method that adds a mouselistener to the header of a given <code>JTable</code>.
     * On right click a popup menu will appear to change the visibility of
     * the columns in the jtable
     * @param table the <code>JTable</code> to add the mouselistener to its header
     */
    public void addMouseListener(final JTable table)
    {

        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent event)
            {
                // check for right button click
                if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON3)
                {
                    VisibilityPopupMenu popupMenu = new VisibilityPopupMenu(table);
                    popupMenu.show(table, event.getX(), event.getY());
                }
            }
        });
    }


}

