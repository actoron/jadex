package jadex.commons.gui.jtable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;


public class VisibilityPopupMenu extends JPopupMenu
{
    private IVisibilityTableColumnModel model;

    public VisibilityPopupMenu(JTable table)
    {
        if (table.getColumnModel() instanceof IVisibilityTableColumnModel)
        {
            this.model = (IVisibilityTableColumnModel) table.getColumnModel();
        }
        else
        {
            throw new IllegalArgumentException("Jtable has no IVisibilityTableColumnModel");
        }

    }

    public void show(Component invoker, int x, int y)
    {
        removeAll();
        addMenuItems();
        super.show(invoker, x, y);
    }

    private void addMenuItems() {
        Enumeration eac = model.getAllColumns();
        while (eac.hasMoreElements())
        {
            TableColumn column = (TableColumn) eac.nextElement();
            JCheckBoxMenuItem item = new JCheckBoxMenuItem((String) column.getIdentifier());
            item.setSelected(model.isColumnVisible(column));
            item.setEnabled(model.isColumnChangeable(column));
            item.addActionListener(new MenuItemActionListener());
            add(item);
        }

        addSeparator();
        JMenuItem showAll = new JMenuItem("Show all columns");
        showAll.addActionListener(new ShowAllActionListener());
        add(showAll);
    }

    private class MenuItemActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
                TableColumn column = model.getAllColumn(getComponentIndex(item));
                model.setColumnVisible(column, item.isSelected());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private class ShowAllActionListener implements ActionListener
    {
        /**
         * Make all columns visible
         */
        public void actionPerformed(ActionEvent e)
        {
            Enumeration eac= model.getAllColumns();
            while (eac.hasMoreElements())
            {
                TableColumn column = (TableColumn) eac.nextElement();
                if (model.isColumnChangeable(column)) {
                    model.setColumnVisible(column,true);
                }
            }
        }
    }


}
