package com.daimler.util.swing.autohidepanel;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXCollapsiblePane.Orientation;

public class HideablePanelGlassPane extends JXPanel {

    public static final String WEST = BorderLayout.WEST;
    public static final String EAST = BorderLayout.EAST;
    
    private AutoHidePanel thePanel;
    
    private String thePosition = WEST;
    
    private int theTopOffset = 0;
    private int theBottomOffset = 0;
    private JXPanel thePanelGround;
    
    public HideablePanelGlassPane(String position, AutoHidePanel panel) {
        super();
        if (!thePosition.equals(WEST) && !thePosition.equals(EAST)) {
            throw new IllegalArgumentException("Illegal panel position. Must be either HideablePanelGlassPane.WEST or HideablePanelGlassPane.EAST!");
        }
        thePosition = position;
        init();
        setThePanel(panel);
    }
    
    public HideablePanelGlassPane(String position) {
        this(position, new AutoHidePanel());
    }
    
    private void init() {
        setBorder(null);
        setLayout(new BorderLayout());
        thePanelGround = new JXPanel();
        thePanelGround.setBorder(null);
        thePanelGround.setAlpha(getAlpha());
        thePanelGround.setInheritAlpha(false);
        createEmptyOffsetBorder();
        thePanelGround.setPaintBorderInsets(false);
        layoutThePanel();
        setAlpha(0.7f);
    }
    
    public void setVisible(boolean b) {
        super.setVisible(b);
        getThePanel().setVisible(b);
    }
    
    private void createEmptyOffsetBorder() {
        thePanelGround.setBorder(BorderFactory.createEmptyBorder(theTopOffset, 0, theBottomOffset, 0));
    }

    private void layoutThePanel() {
        remove(thePanelGround);
        add(thePanelGround, getThePosition());
    }
    
    public AutoHidePanel getThePanel() {
        return thePanel;
    }
    
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        thePanelGround.setAlpha(alpha);
    }
    
    public void setThePanel(AutoHidePanel panel) {
        LayoutManager oldLayoutMgr = panel.getContentPane().getLayout();
        if (thePanel != null) {
            thePanelGround.remove(thePanel);
        }
        thePanel = panel;
        thePanel.setOrientation(Orientation.HORIZONTAL);
        if (getThePosition().equals(WEST)) {
            thePanel.setOpenDirection(AutoHidePanel.OPEN_EAST);
        } else if (getThePosition().equals(EAST)) {
            thePanel.setOpenDirection(AutoHidePanel.OPEN_WEST);
        }
        thePanel.getContentPane().setLayout(oldLayoutMgr);
        thePanelGround.add(thePanel);
    }
    
    public void setTopOffset(int offset) {
        theTopOffset = offset;
        createEmptyOffsetBorder();
        repaint();
    }
    
    public void setBottomOffset(int offset) {
        theBottomOffset = offset;
        createEmptyOffsetBorder();
        repaint();
    }
    
    public int getTopOffset() {
        return theTopOffset;
    }
    
    public int getBottomOffset() {
        return theBottomOffset;
    }
    
    public String getThePosition() {
        return thePosition;
    }
    
    public void setThePosition(String position) {
        if (!thePosition.equals(WEST) && !thePosition.equals(EAST)) {
            throw new IllegalArgumentException("Illegal panel position. Must be either HideablePanelGlassPane.WEST or HideablePanelGlassPane.EAST!");
        }
        thePosition = position;
        layoutThePanel();
    }

    
    
}
