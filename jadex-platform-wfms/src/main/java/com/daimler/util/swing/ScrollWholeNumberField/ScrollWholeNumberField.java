/*
 * ScrollWholeNumberField.java
 *
 * Created on November 12, 2002, 7:57 PM
 */

package com.daimler.util.swing.ScrollWholeNumberField;

import java.awt.AWTEvent;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * 
 * @author krischan
 */
public class ScrollWholeNumberField extends JPanel {
    JKeyTextField wnf;

    JScrollBar sb;
    
    int iMaxValue = 99999;

    int iMinValue = -99999;

    int value = 0;

    int pageIncrement;

    boolean adjusting = false;

    final static int ADJUST_TEXT = 0x00000001;

    final static int ADJUST_SCROLL = 0x00000002;

    final static int ADJUST_FIRE = 0x00000004;

    final static int SET_TEXT = 0x00000001;

    final static int SET_SCROLL = 0x00000002;

    final static int SET_BEEP = 0x00000004;

    final static int ADJUST_ALL = ADJUST_TEXT | ADJUST_SCROLL | ADJUST_FIRE;

    final static int SET_ALL = SET_TEXT | SET_SCROLL | SET_BEEP;

    final SWNFInteger range = new SWNFInteger();

    private class JKeyTextField extends JTextField {
        boolean rangeError = false;

        private class IntegerDocument extends PlainDocument {
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (!ScrollWholeNumberField.this.adjusting) {
                    boolean intError = false;
                    boolean ok = true;
                    StringBuffer result = new StringBuffer();
                    String resultStr;
                    int val = 0;
                    char ch;

                    for (int i = 0; i < str.length(); i++) {
                        ch = str.charAt(i);
                        if (ch == '-') {
                            if (offs != 0)
                                ok = false;
                        } else if (Character.isDigit(ch)) {
                            if (getLength() > 0
                                    && getText(0, 1).charAt(0) == '-'
                                    && offs < 1)
                                ok = false;
                        } else
                            ok = false;
                        if (ok)
                            result.append(ch);
                    }

                    resultStr = result.toString();

                    try {
                        val = Integer.parseInt(new StringBuffer(getText(0,
                                getLength())).insert(offs, resultStr)
                                .toString());
                    } catch (NumberFormatException ex) {
                        intError = true;
                    }
                    if (!intError) {
                        if (!range.setValue(val)) {
                            val = range.getValue();
                            JKeyTextField.this.rangeError = true;
                            Toolkit.getDefaultToolkit().beep();
                        }
                        ScrollWholeNumberField.this.value = val;
                        ScrollWholeNumberField.this.doAdjust(ADJUST_SCROLL
                                | ADJUST_FIRE);
                    }
                    super.insertString(offs, resultStr, a);
                } else
                    super.insertString(offs, str, a);
            }

            public void remove(int offs, int len) throws BadLocationException {
                if (!ScrollWholeNumberField.this.adjusting) {
                    boolean intError = false;
                    int val = 0;

                    try {
                        String text = getText(0, getLength());
                        val = Integer.parseInt(text.substring(0, offs)
                                + text.substring(offs + len));
                    } catch (NumberFormatException ex) {
                        intError = true;
                    }
                    if (!intError) {
                        if (!range.setValue(val)) {
                            val = range.getValue();
                            JKeyTextField.this.rangeError = true;
                        }
                        ScrollWholeNumberField.this.value = val;
                        ScrollWholeNumberField.this.doAdjust(ADJUST_SCROLL
                                | ADJUST_FIRE);
                    }
                }
                super.remove(offs, len);
            }
        }

        public JKeyTextField() {
            super();
            enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
        }

        public JKeyTextField(int cols) {
            super(cols);
            enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
        }

        protected Document createDefaultModel() {
            return new IntegerDocument();
        }

        protected void processKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    ScrollWholeNumberField.this
                            .setValueImpl(value + 1, SET_ALL);
                    break;
                case KeyEvent.VK_DOWN:
                    ScrollWholeNumberField.this
                            .setValueImpl(value - 1, SET_ALL);
                    break;
                case KeyEvent.VK_PAGE_UP:
                    ScrollWholeNumberField.this.setValueImpl(value
                            + pageIncrement, value == iMaxValue ? SET_TEXT
                            | SET_SCROLL : SET_ALL);
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    ScrollWholeNumberField.this.setValueImpl(value
                            - pageIncrement, value == iMaxValue ? SET_TEXT
                            | SET_SCROLL : SET_ALL);
                    break;
                default:
                    super.processKeyEvent(e);
                }
            } else
                super.processKeyEvent(e);
        }

        protected void processFocusEvent(FocusEvent e) {
            if (e.getID() == FocusEvent.FOCUS_LOST && !e.isTemporary()
                    && rangeError) {
                ScrollWholeNumberField.this.doAdjust(ADJUST_TEXT);
                rangeError = false;
            }
            super.processFocusEvent(e);
        }
    }

    public ScrollWholeNumberField() {
        this(1, 1, 100, 10);
    }

    public ScrollWholeNumberField(int value, int min, int max) {
        this(value, min, max, ((max - min) / 10) + 1);
    }

    /** Creates a new instance of ScrollWholeNumberField */
    public ScrollWholeNumberField(int value, int min, int max, int pageIncrement) {
        Dimension diText, diScb;
        setLayout(new BorderLayout());
        wnf = new JKeyTextField(Integer.toString(max).length());
        add(wnf, BorderLayout.CENTER);
        sb = new JScrollBar(Adjustable.VERTICAL, iMinValue, 0, iMinValue,
                iMaxValue);
        sb.setBlockIncrement(pageIncrement);
        sb.addAdjustmentListener(new sbListener());
        add(sb, BorderLayout.EAST);
        diText = wnf.getPreferredSize();
        diScb = sb.getPreferredSize();
        sb.setPreferredSize(new Dimension(diScb.width, diText.height));
        setValues(value, min, max, pageIncrement);
    }

    void doAdjust(int operation) {
        adjusting = true;
        if ((operation & ADJUST_TEXT) != 0)
            wnf.setText(Integer.toString(value));
        if ((operation & ADJUST_SCROLL) != 0)
            sb.setValue(iMaxValue - value + iMinValue);
        adjusting = false;
        if ((operation & ADJUST_FIRE) != 0)
            fireScwnfEvent(value);
    }

    /**
     * Getter for property iMinValue.
     * 
     * @return Value of property iMinValue.
     *  
     */
    public int getIMinValue() {
        return iMinValue;
    }

    /**
     * Setter for property iMinValue.
     * 
     * @param iMinValue
     *            New value of property iMinValue.
     *  
     */
    public void setIMinValue(int iMinValue) {
        setValues(value, iMinValue, iMaxValue, pageIncrement);
    }

    /**
     * Getter for property iMaxValue.
     * 
     * @return Value of property iMaxValue.
     *  
     */
    public int getIMaxValue() {
        return iMaxValue;
    }

    /**
     * Setter for property iMaxValue.
     * 
     * @param iMaxValue
     *            New value of property iMaxValue.
     *  
     */
    public void setIMaxValue(int iMaxValue) {
        setValues(value, iMinValue, iMaxValue, pageIncrement);
    }

    /**
     * Getter for property value.
     * 
     * @return Value of property value.
     *  
     */
    public int getValue() {
        return value;
    }

    /**
     * Setter for property value.
     * 
     * @param value
     *            New value of property value.
     *  
     */
    public void setValues(int value, int min, int max, int pageIncrement) {
        if (max <= min)
            max = min + 1;
        if (pageIncrement > max - min)
            pageIncrement = max - min;
        if (pageIncrement < 1)
            pageIncrement = 1;
        if (value < min)
            value = min;
        if (value > max)
            value = max;

        this.pageIncrement = pageIncrement;
        iMinValue = min;
        iMaxValue = max;
        range.setTheMinValue(min);
        range.setTheMaxValue(max);
        adjusting = true;
        //if (this.value != value)
            wnf.setText(Integer.toString(value));
        adjusting = false;
        this.value = value;
        adjusting = true;
        sb.setValues(max - value + min, 0, min, max);
        adjusting = false;
        sb.setBlockIncrement(pageIncrement);
        fireScwnfEvent(value);
    }

    void setValueImpl(int value, int operation) {
        if (!range.setValue(value)) {
            value = range.getValue();
            if ((operation & SET_BEEP) != 0)
                Toolkit.getDefaultToolkit().beep();
        }

        if (this.value == value)
            return;
        this.value = value;
        doAdjust(operation & SET_TEXT | operation & SET_SCROLL | ADJUST_FIRE);
    }

    public void setValue(int value) {
        setValues(value, iMinValue, iMaxValue, pageIncrement);
    }

    private class sbListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent ae) {
            if (ScrollWholeNumberField.this.adjusting)
                return;
            ScrollWholeNumberField.this.setValueImpl(iMaxValue - ae.getValue()
                    + iMinValue, SET_TEXT);
        }
    }

    public int getPageIncrement() {
        return pageIncrement;
    }

    public void setPageIncrement(int pageIncrement) {
        setValues(value, iMinValue, iMaxValue, pageIncrement);
    }

    public void addScwnfChangedListener(SWNFChangedListener aListener) {
        listenerList.add(SWNFChangedListener.class, aListener);
    }

    public void removeScwnfChangedListener(SWNFChangedListener aListener) {
        listenerList.remove(SWNFChangedListener.class, aListener);
    }

    protected void fireScwnfEvent(int val) {
        Object[] listeners = listenerList.getListenerList();
        SWNFEvent event = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == SWNFChangedListener.class) {
                if (event == null)
                    event = new SWNFEvent(this, val);
                ((SWNFChangedListener) listeners[i + 1])
                        .scwnfValueChanged(event);
            }
    }

    public void setToolTipText(String tooltip)
    {
    	wnf.setToolTipText(tooltip);
    }
    
    public void setEnabled(boolean b) {
        sb.setEnabled(b);
        wnf.setEnabled(b);
    }

    public void setVisible(boolean b) {
        sb.setVisible(b);
        wnf.setVisible(b);
    }
}

