package com.daimler.client.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ComboChoiceField extends JComboBox implements IInputComponent
{

    private int width = 250;

    private int height = 24;

    public ComboChoiceField(List choices)
    {
        super();
        setBackground(Color.WHITE);
        setEditable(false);

        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        for (int j = 0; j < choices.size(); ++j)
        {
            Object oItem = choices.get(j);
            addItem(oItem);
            int itemWidth = (int) dlcr.getFontMetrics(dlcr.getFont())
                    .getStringBounds(oItem.toString(), dlcr.getGraphics())
                    .getWidth();
            if (itemWidth > width)
            {
                width = itemWidth;
            }
        }
        width = width + 30;
        setPreferredSize(new Dimension(width, height));
        if (getItemCount() > 0)
        {
            setSelectedIndex(0);
        }
    }

    public Object getValue()
    {
        return getSelectedItem();
    }

    public void setValue(Object v)
    {
        setSelectedItem(v);
    }

    public JComponent getComponent() {
        return this;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
