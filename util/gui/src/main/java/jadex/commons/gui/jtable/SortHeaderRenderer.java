package jadex.commons.gui.jtable;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class SortHeaderRenderer extends DefaultTableCellRenderer
{
//    public static Icon NONE = new SortArrowIcon(SortArrowIcon.NONE);
    public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);
    public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

    public SortHeaderRenderer()
    {
        setHorizontalTextPosition(LEFT);
        setHorizontalAlignment(CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
    {
        int index = -1;
        int direction = 0;

        if (table != null)
        {         
            if (table.getModel() instanceof ISorterFilterTableModel) {
                ISorterFilterTableModel model = (ISorterFilterTableModel) table.getModel();
                index = table.convertColumnIndexToView(model.getSortColumn());
                direction = model.getSortDirection();
            }

            JTableHeader header = table.getTableHeader();
            if (header != null)
            {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }
        setIcon(col==index && direction!=ISorterFilterTableModel.NONE ? new SortArrowIcon(direction): null);
        setText((value == null) ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }
}

