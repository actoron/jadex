package com.daimler.util.swing.autohidepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painter.effects.AreaEffect;
import org.jdesktop.swingx.painter.effects.ShadowPathEffect;

public class AutoHidePanel extends JXCollapsiblePane implements ChangeListener,
        ComponentListener {

    // e.g. the panel is on the left side of the glasspane
    public static final int OPEN_EAST = 0;

    // e.g. the panel sits at the right rim
    public static final int OPEN_WEST = 1;

    private static final String DEFAULT_TITLE = "New Panel";

    private String theTitle;

    private boolean autoHide = true;

    private int openDirection = OPEN_EAST;

    private HideTimer theHideTimer = null;

    private int theOpenWidth = -1;

    private JXPanel theItemPane;

    private JViewport theViewPort;
    
    private Color theTitleColor;

    private JXPanel theScrollUpPanel;

    private JXPanel theScrollDownPanel;
    
    public AutoHidePanel(String title) {
        super(Orientation.VERTICAL);
        theTitle = title;
        theHideTimer = new HideTimer(this, 0);
        init();
    }

    public AutoHidePanel() {
        this(DEFAULT_TITLE);
    }

    public void setOpenWidth(int openWidth) {
        theOpenWidth = openWidth;
    }

    private void init() {
        theItemPane = new JXPanel();
        theItemPane.setLayout(new VerticalLayout(10) {
            public Dimension preferredLayoutSize(Container parent) {
                Dimension dim = super.preferredLayoutSize(parent);
                Dimension dim2 = new Dimension(dim.width, dim.height - getGap());
                return dim2;
            }
        });
        theViewPort = new JViewport();
        theViewPort.addChangeListener(this);
        theViewPort.setView(theItemPane);
        JXPanel theRootPane = new JXPanel();
        theRootPane.setBorder(null);
        theRootPane.setLayout(new BorderLayout());

        JXButton theScrollDownButton = new JXButton("Down");
        theScrollDownButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Rectangle r = theItemPane.getVisibleRect();
                r.y = r.y + 10;
                if (r.y + r.height > theItemPane.getPreferredSize().height) {
                    r.y = theItemPane.getPreferredSize().height - r.height;
                }
                theItemPane.scrollRectToVisible(r);
            }

        });
        theScrollDownButton.setPreferredSize(new Dimension(40, 20));
        theScrollDownButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,
                0));
        theScrollDownPanel = new JXPanel();
        theScrollDownPanel.add(theScrollDownButton);
        showScrollDownArrow(false);

        JXButton theScrollUpButton = new JXButton("Up");
        theScrollUpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Rectangle r = theItemPane.getVisibleRect();
                r.y = r.y - 10;
                if (r.y <= 0) {
                    r.y = 0;
                }
                theItemPane.scrollRectToVisible(r);
            }

        });
        theScrollUpButton.setPreferredSize(new Dimension(40, 20));
        theScrollUpButton
                .setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        theScrollUpPanel = new JXPanel();
        theScrollUpPanel.add(theScrollUpButton);
        theScrollUpPanel.setVisible(false);
        showScrollUpArrow(false);

        // theRootPane.add(theItemPane, BorderLayout.CENTER);
        theRootPane.add(theViewPort, BorderLayout.CENTER);
        theRootPane.add(theScrollDownPanel, BorderLayout.SOUTH);
        theRootPane.add(theScrollUpPanel, BorderLayout.NORTH);
        setContentPane(theRootPane);
        //create default background painter (taken from Romains tutorial)
        GlossPainter gloss = new GlossPainter(new Color(1.0f, 1.0f, 1.0f,
                0.2f), GlossPainter.GlossPosition.TOP);

        PinstripePainter stripes = new PinstripePainter();
        stripes.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.17f));
        stripes.setSpacing(5.0);

        MattePainter matte = new MattePainter(new Color(51, 51, 51));
        
        CompoundPainter borderPainter = new CompoundPainter(new Painter[] {matte, stripes, gloss});
        setBackgroundPainter(borderPainter);
        setTitleColor(Color.BLACK);
        setBorder(new AutoHidePaneBorder(this));
        addMouseListener(new ToggleListener(this));
        addComponentListener(this);
        setPaintBorderInsets(false);
        setCollapsed(false);
    }

    private void showScrollUpArrow(boolean b) {
        theScrollUpPanel.setVisible(b);
    }

    private void showScrollDownArrow(boolean b) {
        theScrollDownPanel.setVisible(b);
    }

    public Component addItem(Component comp) {
        return theItemPane.add(comp);
    }
    
    public void removeItem(Component comp) {
        theItemPane.remove(comp);
    }
    
    public Component getItem(int index) {
        return theItemPane.getComponent(index);
    }

    public int getItemCount() {
        return theItemPane.getComponentCount();
    }

    public void setItemGap(int gap) {
        ((VerticalLayout) theItemPane.getLayout()).setGap(gap);
    }

    public int getItemGap() {
        return ((VerticalLayout) theItemPane.getLayout()).getGap();
    }

    public void setTitleFont(Font font) {
        ((AutoHidePaneBorder) getBorder()).setFont(font);
    }

    public Font getTitleFont() {
        return ((AutoHidePaneBorder) getBorder()).getFont();
    }

    public Dimension getPreferredSize() {
        AutoHidePaneBorder ahpb = (AutoHidePaneBorder) getBorder();
        Dimension dim = super.getPreferredSize();
        // int w = dim.width + ahpb.getTitleHeight();
        int w = dim.width;
        int h = dim.height + getInsets().top + getInsets().bottom;
        if (w > theOpenWidth && theOpenWidth > -1) {
            w = theOpenWidth;
        }
        int iMaxHeight = getParent().getHeight() - getParent().getInsets().top
                - getParent().getInsets().bottom;
        // System.out.println("iMaxHeight: " + iMaxHeight);
        if (h > iMaxHeight) {
            h = iMaxHeight;
        }
        // setMinimum
        if (theItemPane.getComponentCount() == 0) {
            if (!isCollapsed()) {
                if (theOpenWidth > -1) {
                    w = theOpenWidth;
                } else {
                    w = 50;
                }
            } else {
                w = ahpb.getTitleHeight();
            }
        }
        if (h < ahpb.getTitleWidth() + 6) {
            h = ahpb.getTitleWidth() + 6;
        }

        if (isCollapsed()) {
            // return dim;
            return new Dimension(w, h);
        }
        return new Dimension(w + ahpb.getTitleHeight(), h);
        // return new Dimension(w, h);
    }

    public Component add(Component comp) {
        // return getContentPane().add(comp);
        return theItemPane.add(comp);
    }

    public void setTitle(String title) {
        theTitle = title;
    }

    public String getTitle() {
        return theTitle;
    }

    public void setAutoHideEnabled(boolean b) {
        autoHide = b;
        if (!isAutoHideenabled() && isCollapsed()) {
            setCollapsed(false);
        }
    }

    public boolean isAutoHideenabled() {
        return autoHide;
    }

    public void setAutoHideDelay(int seconds) {
        theHideTimer.setInitialDelay(seconds * 1000);
    }

    public int getAutoHideDelay() {
        return (int) theHideTimer.getInitialDelay() / 1000;
    }

    public void setOpenDirection(int direction) {
        if (direction != OPEN_WEST && direction != OPEN_EAST) {
            throw new IllegalArgumentException(
                    "Illegal openening direction. Must be either AutoHidePanel.OPEN_EAST or AutoHidePanel.OPEN_WEST!");
        }
        openDirection = direction;
        repaint();
    }

    public int getOpenDirection() {
        return openDirection;
    }

    public void setBackgroundPainter(Painter bgPainter) {
        theItemPane.setBackgroundPainter(bgPainter);
        theScrollDownPanel.setBackgroundPainter(bgPainter);
        theScrollUpPanel.setBackgroundPainter(bgPainter);
    }
    
    public Painter getBackgroundPainter() {
        return theItemPane.getBackgroundPainter();
    }
    
    public void setTitleColor(Color color) {
        theTitleColor = color;
    }
    
    public Color getTitleColor() {
        return theTitleColor;
    }
    
    // ///////////// AutoHideTimer ////////////////
    private class HideTimer extends Timer {

        public HideTimer(final AutoHidePanel panel, int delay) {
            super(delay * 1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    panel.setCollapsed(true);
                }
            });
            setRepeats(false);
        }
    }

    // ///////////// LISTENER ////////////////////////
    private class ToggleListener extends MouseInputAdapter {
        private JXCollapsiblePane thePane;

        public ToggleListener(JXCollapsiblePane pane) {
            thePane = pane;
        }

        public void mouseEntered(MouseEvent e) {
            if (thePane.contains(e.getX(), e.getY()) && isAutoHideenabled()) {
                if (theHideTimer.isRunning()) {
                    theHideTimer.stop();
                }
                thePane.setCollapsed(false);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (!thePane.contains(e.getX(), e.getY()) && isAutoHideenabled()) {
                if (getAutoHideDelay() > 0) {
                    theHideTimer.restart();
                } else {
                    thePane.setCollapsed(true);
                }
            }
        }

    }

    // ///////////// BORDER IMPLEMENTATION ////////////////////

    private class AutoHidePaneBorder implements Border {

        private AutoHidePanel thePane;

        private Font theFont = UIManager.getFont("Label.font");
        private FontMetrics fm;

        

        public AutoHidePaneBorder(AutoHidePanel pane) {
            thePane = pane;
            setFont(theFont);
        }

        public int getTitleHeight() {
            return 4 + fm.getMaxDescent() + fm.getMaxAscent();
        }

        public int getTitleWidth() {

            return fm.stringWidth(thePane.getTitle());
        }

        public void setFont(Font font) {
            theFont = font;
            fm = Toolkit.getDefaultToolkit().getFontMetrics(theFont);
        }

        public Font getFont() {
            return theFont;
        }

        public Insets getBorderInsets(Component c) {
            AutoHidePanel ahp = (AutoHidePanel) c;
            if (ahp.getOpenDirection() == OPEN_EAST) {
                return new Insets(10, 0, 10, getTitleHeight());
            } else if (ahp.getOpenDirection() == OPEN_WEST) {
                return new Insets(10, getTitleHeight(), 10, 0);
            }
            return new Insets(10, 0, 10, 0);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        private void paintTitle(AutoHidePanel ahp, Graphics g, Color textColor,
                int x, int y, int width, int height) {
            g.translate(x, y);
            Graphics2D g2 = (Graphics2D) g;
            Font oldFont = g.getFont();
            Color oldColor = g.getColor();
            AffineTransform oldTransform = g2.getTransform();
            g2.rotate(Math.PI / 2);
            
//            ShadowFilter shadow = new ShadowFilter();
//            shadow.setAngle(-90.0f);
//            shadow.setDistance(2.0f);
//            shadow.setRadius(2.0f);
//            shadow.setOpacity(0.75f);
            
            TextPainter text = new TextPainter();
            text.setFont(theFont);
            text.setText(thePane.getTitle());
            text.setFillPaint(thePane.getTitleColor());
            //text.setLocatio(new Point2D.Double(0.0, 0.0));
            text.setAntialiasing(true);
            text.setAreaEffects(new AreaEffect[] {new ShadowPathEffect()});
//            text.setEffect(new ImageEffec (shadow));
            text.paint(g2, this, getTitleWidth(), getTitleHeight());
            
//            g.setFont(theFont);
//            g.setColor(Color.black);
//            g.drawString(thePane.getTitle(), 0, fm.getLeading()
//                    + fm.getAscent());
            g2.setTransform(oldTransform);
            g.setFont(oldFont);
            g.setColor(oldColor);
            g.translate(-x, -y);
        }

        private void paintBackground(AutoHidePanel ahp, Graphics g,
                Color bgColor, int x, int y) {
            g.translate(x, y);
            thePane.getBackgroundPainter().paint((Graphics2D) g, this, ahp.getWidth(), ahp.getHeight());
            g.translate(-x, -y);
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                int width, int height) {
            AutoHidePanel ahp = (AutoHidePanel) c;

            int titleX = ahp.getWidth();
            if (getOpenDirection() == OPEN_WEST) {
                titleX = getTitleHeight();
            }
            int titleY = 0;
            int titleWidth = getTitleHeight();
            int titleHeight = ahp.getHeight() - 3;

            paintBackground(ahp, g, Color.RED, x, y);
            paintTitle(ahp, g, Color.BLACK, titleX, titleY, titleWidth,
                    titleHeight);
        }

    }

    public void stateChanged(ChangeEvent e) {
        int h = getPreferredSize().height;
        Rectangle rectVisible = theItemPane.getVisibleRect();
        if (rectVisible.height > 0
                && h - getInsets().top - getInsets().bottom < theItemPane
                        .getPreferredSize().height) {
            // System.out.println("We have to show the scroll buttons");
            // Rectangle rectVisible = theItemPane.getVisibleRect();
            if (rectVisible.y > 0) {
                showScrollUpArrow(true);
            } else {
                showScrollUpArrow(false);
            }
            if (rectVisible.y + rectVisible.height < theItemPane
                    .getPreferredSize().height) {
                // System.out.println("Showing scroll down because: " +
                // rectVisible.y + " + " + rectVisible.height + " < " +
                // theItemPane.getPreferredSize().height);
                showScrollDownArrow(true);
            } else {
                showScrollDownArrow(false);
            }
        } else {
            showScrollDownArrow(false);
            showScrollUpArrow(false);
        }
    }

    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    public void componentResized(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    public void componentShown(ComponentEvent e) {
        (new HideTimer(AutoHidePanel.this, 2)).start();
    }

}
