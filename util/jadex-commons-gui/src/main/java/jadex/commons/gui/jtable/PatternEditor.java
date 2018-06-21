package jadex.commons.gui.jtable;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 */
public class PatternEditor extends DefaultCellEditor
{
    Class[] argTypes = new Class[]{String.class};
    java.lang.reflect.Constructor constructor;
    Object value;

    public PatternEditor()
    {
        super(new JTextField());
    }

    public boolean stopCellEditing()
    {
        String s = (String) super.getCellEditorValue();
        // Here we are dealing with the case where a user
        // has deleted the string value in a cell, possibly
        // after a failed validation. Return null, so that
        // they have the option to replace the value with
        // null or use escape to restore the original.
        // For Strings, return "" for backward compatibility.
        if ("".equals(s))
        {
            if (constructor.getDeclaringClass() == String.class)
            {
                value = s;
            }
            return super.stopCellEditing();
        }

        // Try to compile a given Sttring into a pattern.
        // Catch the exception and show error message if
        // String has invalid syntax.
        try
        {
            Pattern.compile(s);
        }
        catch (Exception e)
        {
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
            JOptionPane.showMessageDialog(null, e.getMessage(), "Pattern Syntax Exception", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try
        {
            value = constructor.newInstance(new Object[]{s});
        }
        catch (Exception e)
        {
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        return super.stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        this.value = null;
        ((JComponent) getComponent()).setBorder(new LineBorder(Color.black));
        try
        {
            Class type = table.getColumnClass(column);
            // Since our obligation is to produce a value which is
            // assignable for the required type it is OK to use the
            // String constructor for columns which are declared
            // to contain Objects. A String is an Object.
            if (type == Object.class)
            {
                type = String.class;
            }
            constructor = type.getConstructor(argTypes);
        }
        catch (Exception e)
        {
            return null;
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public boolean shouldSelectCell(EventObject anEvent)
    {
        return false;
    }

    public Object getCellEditorValue()
    {
        return value;
    }

}
