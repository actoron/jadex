package jadex.commons.gui.jtable;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;


public class SorterFilterTableModel extends AbstractIndexTableModel implements ISorterFilterTableModel
{

    public static final int PAUSE_DISPLAYING_OFF = -1;

    // Current filter
    protected Vector filter = new Vector();
    // if filter is enabled
    protected boolean doFilter = false;

    // Vector of sorting columns
    protected Vector sortColumns = new Vector();
    // Current sort column
    protected int sortColumn = -1;
    // Current sort direction
    protected int sortDirection = NONE;

    // stop displaying data
    protected boolean pauseDisplaying = false;
    // index of last row when paused displaying
    // if pauseRow is PAUSE_DISPLAYING_OFF diplaying isnt paused
    protected int pauseRow = PAUSE_DISPLAYING_OFF;


    public SorterFilterTableModel(TableModel delegate)
    {
        super(delegate);
        renewIndex();
    }

    public void renewIndex()
    {
        indexList.clear();
        //TODO: adjust pauseRow on delegate.tableChanged event
        if (pauseRow > delegate.getRowCount())
        {
            pauseRow = delegate.getRowCount();
        }
        int rows = pauseRow != PAUSE_DISPLAYING_OFF ? pauseRow : delegate.getRowCount();
        for (int row = 0; row < rows; row++)
        {
            if (isFiltered(getRowData(row)))
            {
                indexList.add(Integer.valueOf(row));
            }
        }

        if (isDoSort())
        {
            RowComparator rowComparator = new RowComparator(sortColumn, sortDirection == ASCENDING);
            Collections.sort(indexList, rowComparator);
        }

        fireTableDataChanged();
    }

    public void tableRowsDeleted(int column, int firstRow, int lastRow)
    {
        for (int row = firstRow; row <= lastRow; row++)
        {
            int index = indexList.indexOf(Integer.valueOf(row));
            if (index != -1)
            {
                indexList.remove(index);
                fireTableRowsDeleted(index, index);
            }

        }
    }

    public void tableRowsInserted(int column, int firstRow, int lastRow)
    {
        if (isPausedDisplaying())
        {
            return;
        }

        for (int row = firstRow; row <= lastRow; row++)
        {
            // insert index if filter matches
            if (isFiltered(getRowData(row)))
            {
                int index = getRowCount();
                if (isDoSort())
                {
                    // if sort then find the position to insert element
                    RowComparator rowComparator = new RowComparator(sortColumn, sortDirection == ASCENDING);
                    // the return value of binarySerach is (-(insertion point) - 1)
                    // insertion point is the index in the list
                    // at which the new element would be inserted
                    // if result >=0 then this is the position of an element found in the list
                    // note: If the list contains multiple elements equal to the specified object,
                    // there is no guarantee which one will be found
                    int result = Collections.binarySearch(indexList, Integer.valueOf(row),  rowComparator);
                    index = result < 0 ? -1 - result : result;
                }
                indexList.add(index, Integer.valueOf(row));
                fireTableRowsInserted(index, index);


            }
        }

    }

    public void tableRowsUpdated(int column, int firstRow, int lastRow)
    {
        // All data has changed
        if (lastRow == Integer.MAX_VALUE)
        {
            renewIndex();
        }
        else
        {
            for (int row = firstRow; row <= lastRow; row++)
            {
                int index = indexList.indexOf(Integer.valueOf(row));
                if (index != -1)
                {
                    fireTableRowsUpdated(index, index);
                }
            }
        }
    }

    private Vector getRowData(int row)
    {
        Vector rowData = new Vector();
        int columns = delegate.getColumnCount();
        for (int column = 0; column < columns; column++)
        {
            rowData.add(delegate.getValueAt(row, column));
        }
        return rowData;
    }

    private boolean isFiltered(Vector rowData)
    {
        if (filter == null || !doFilter)
        {
            return true;
        }

        boolean rowLogic[] = new boolean[filter.size() - 1];

        for (int i = 0; i < filter.size() - 1; i++)
        {
            Vector rowFilter = (Vector) filter.elementAt(i);
            rowLogic[i] = true;

            for (int j = 0; j < rowFilter.size() - 1; j++)
            {
                if (rowFilter.elementAt(j) != null && rowData.elementAt(j) != null)
                {
                    String cellFilter = rowFilter.elementAt(j).toString().trim();
                    String cellData = rowData.elementAt(j).toString().trim();

                    if (cellFilter.length() != 0)
                    {
                        //                    if (!cellData.equalsIgnoreCase(cellFilter))

                        try
                        {
                            if (!cellData.matches(cellFilter))
                            {
                                rowLogic[i] = false;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        boolean result = rowLogic.length == 0;
        for (int i = 0; i < rowLogic.length; i++)
        {
            result = result || rowLogic[i];
        }
        return result;
    }


    public void addMouseListener(final JTable table)
    {
        table.getTableHeader().setDefaultRenderer(new SortHeaderRenderer());
        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent event)
            {
                // check for left button click
                if (event.getButton() == MouseEvent.BUTTON1)
                {
                    // find column of click and
                    int tableColumn = table.columnAtPoint(event.getPoint());

                    Rectangle r = table.getTableHeader().getHeaderRect(tableColumn);
                    r.grow(-3, 0);
                    if (r.contains(event.getPoint()))
                    {
                        // translate to table model index
                        int modelColumn = table.convertColumnIndexToModel(tableColumn);

                        sortDirection = (modelColumn == sortColumn) ? (++sortDirection) % 3 : DESCENDING;
                        sortColumn = modelColumn;

                        RowComparator rowComparator = new RowComparator(sortColumn, sortDirection == ASCENDING);
                        Collections.sort(indexList, isDoSort() ? rowComparator : null);
                        fireTableDataChanged();

                        // need to call resizeAndRepaint here to
                        // update the header properly
                        table.getTableHeader().resizeAndRepaint();
                    }
                }

            }
        });
    }


    private class RowComparator implements Comparator
    {
        private boolean reverse = false;
        private int column;

        public RowComparator(int column, boolean reverse)
        {
            this.column = column;
            this.reverse = reverse;
        }

        public int compare(Object o1, Object o2)
        {
            int result = 0;
            int rowA = ((Integer) o1).intValue();
            int rowB = ((Integer) o2).intValue();
            Object a = delegate.getValueAt(rowA, column);
            Object b = delegate.getValueAt(rowB, column);

			// todo: a.getClass() == b.getClass() does not work for subclasses, use isAssignableFrom
            boolean areTheyCompareable = (a instanceof Comparable && b instanceof Comparable && a.getClass() == b.getClass());

            if (areTheyCompareable)
            {
                result = ((Comparable) a).compareTo((Comparable) b);
            }
            else
            {
                result = a.toString().compareTo(b.toString());
            }

            if (result == 0)
            {
                // if objects equal, take natural ordering (rowIndex) into account
                result = ((Integer) o1).compareTo((Integer) o2);
            }

            return reverse ? result * (-1) : result;
        }

    }

    public int getSortColumn()
    {
        return sortColumn;
    }

    public void setSortColumn(int sortColumn)
    {
        this.sortColumn = sortColumn;
    }

    public int getSortDirection()
    {
        return sortDirection;
    }

    public void setSortDirection(int sortDirection)
    {
        this.sortDirection = sortDirection;
    }

    public boolean isDoSort()
    {
        return sortColumn != -1 && sortDirection != NONE;
    }

    /**
     * Filter the table using the values in the given vector.
     * @param filter A Vector of filter expressions
     */
    public void setFilter(Vector filter)
    {
        this.filter = filter;
    }

    public Vector getFilter()
    {
        return filter;
    }

    public boolean isDoFilter()
    {
        return doFilter;
    }

    public void setDoFilter(boolean doFilter)
    {
        this.doFilter = doFilter;
    }

    public boolean isPausedDisplaying()
    {
        return pauseRow != PAUSE_DISPLAYING_OFF;
    }

    public void setPauseDisplaying(boolean pauseDisplaying)
    {
        this.pauseRow = pauseDisplaying ? delegate.getRowCount() : PAUSE_DISPLAYING_OFF;
    }
	
}
