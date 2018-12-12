package jadex.commons.gui.jtable;

import java.util.Enumeration;

import javax.swing.table.TableColumn;

/**
 * User: Ruediger Leppin
 * Date: 26.11.2003
 * Time: 01:52:25
 */
public interface IVisibilityTableColumnModel
{
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
    void setColumnVisible(TableColumn column, boolean visible);

    /**
     * Makes all columns in this model visible
     *
     * @see #isColumnVisible
     * @see #setColumnVisible
     */
    void setAllColumnsVisible();

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
    boolean isColumnVisible(TableColumn aColumn);

    /**
     * Returns an <code>Enumeration</code> of all the columns in the model.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @return an <code>Enumeration</code> of all the columns in the model
     */
    Enumeration getAllColumns();

    /**
     * Returns the number of columns in the model.
     * <br>
     * <i> All columns whether visible or not are taken into account !!! </i>
     *
     * @return the number of columns in the model
     */
    int getAllColumnCount();

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
    int getAllColumnIndex(Object identifier);

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
    TableColumn getAllColumn(int columnIndex);

    /**
     * Sets whether this column can change visibility.
     *
     * @param column        the <code>TableColumn</code>
     * @param changeable    if true, changing visibility is allowed;
     *                      otherwise false
     */
    void setColumnChangeable(TableColumn column, boolean changeable);

    /**
     * Checks whether the specified column can change visibility.
     *
     * @param column   column to check
     * @return          true if the column can change visibility;
     *                  otherwise false
     */
    boolean isColumnChangeable(TableColumn column);
}
