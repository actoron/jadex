package jadex.commons.gui.jtable;

import java.util.Vector;

import javax.swing.table.TableModel;

public interface ISorterFilterTableModel extends TableModel
{
    public static final int NONE = 0;
    public static final int DESCENDING = 1;
    public static final int ASCENDING = 2;

    public int getSortColumn();

    public int getSortDirection();

    public void setSortColumn(int sortColumn);

    public void setSortDirection(int sortDirection);

    public void setFilter(Vector filter);

    public Vector getFilter();

    public boolean isDoFilter();

    public void setDoFilter(boolean doFilter);

}

