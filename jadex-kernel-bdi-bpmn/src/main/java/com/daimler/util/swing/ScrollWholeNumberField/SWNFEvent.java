/*
 * scwnfEvent.java
 *
 * Created on November 13, 2002, 9:23 AM
 */

package com.daimler.util.swing.ScrollWholeNumberField;

import java.util.EventObject;

/**
 * 
 * @author krischan
 */
public class SWNFEvent extends EventObject {

    int value;

    /** Creates a new instance of scwnfEvent */
    public SWNFEvent(ScrollWholeNumberField source, int value) {
        super(source);
        this.value = value;
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
    public void setValue(int value) {
        this.value = value;
    }

}