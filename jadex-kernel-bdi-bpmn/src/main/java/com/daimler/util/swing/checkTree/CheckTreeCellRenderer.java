package com.daimler.util.swing.checkTree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.daimler.util.swing.TristateCheckBox;

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer{ 
    private CheckTreeSelectionModel selectionModel; 
    private TreeCellRenderer delegate; 
    private TristateCheckBox checkBox = new TristateCheckBox(); 
    private int initialCheckBoxDepth = 0;
    
    public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel){ 
        this(delegate, selectionModel, 0);
    } 
    
    public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel, int initialCheckBoxDepth){ 
        this.delegate = delegate; 
        this.selectionModel = selectionModel; 
        this.initialCheckBoxDepth = initialCheckBoxDepth;
        setLayout(new BorderLayout()); 
        setOpaque(false); 
        checkBox.setOpaque(false); 
    } 
 
 
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){ 
        Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus); 
        TreePath path = tree.getPathForRow(row); 
        if(path!=null){ 
            if(selectionModel.isPathSelected(path, true)) 
                checkBox.setState(Boolean.TRUE); 
            else 
                checkBox.setState(selectionModel.isPartiallySelected(path) ? null : Boolean.FALSE); 
        } 
        removeAll(); 
        add(renderer, BorderLayout.CENTER); 
        
        int hotspot = new JCheckBox().getPreferredSize().width; 
        setMinimumSize(new Dimension(renderer.getPreferredSize().width + hotspot, renderer.getPreferredSize().height));
        setMaximumSize(new Dimension(renderer.getPreferredSize().width + hotspot, renderer.getPreferredSize().height));
        setPreferredSize(new Dimension(renderer.getPreferredSize().width + hotspot, renderer.getPreferredSize().height));
        if (path != null && path.getPathCount() > initialCheckBoxDepth) add(checkBox, BorderLayout.WEST); 
        
        return this; 
    } 
} 