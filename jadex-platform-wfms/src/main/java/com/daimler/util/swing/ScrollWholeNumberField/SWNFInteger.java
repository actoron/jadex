/*
 * scwnfInteger.java
 *
 * Created on November 13, 2002, 9:29 AM
 */

package com.daimler.util.swing.ScrollWholeNumberField;

/**
 * 
 * @author krischan
 */
public class SWNFInteger {

    int value, theMinValue, theMaxValue;

    boolean Underflow, Overflow;

    /** Creates a new instance of scwnfInteger */
    public SWNFInteger() {
        this(0, 100);
    }

    public SWNFInteger(int min, int max) {
        setTheMinValue(min);
        setTheMaxValue(max);
    }

    /**
     * Getter for property theMaxValue.
     * 
     * @return Value of property theMaxValue.
     *  
     */
    public int getTheMaxValue() {
        return theMaxValue;
    }

    /**
     * Setter for property theMaxValue.
     * 
     * @param theMaxValue
     *            New value of property theMaxValue.
     *  
     */
    public void setTheMaxValue(int theMaxValue) {
        if (theMaxValue <= theMinValue)
            this.theMaxValue = theMinValue + 1;
        else
            this.theMaxValue = theMaxValue;
    }

    /**
     * Getter for property theMinValue.
     * 
     * @return Value of property theMinValue.
     *  
     */
    public int getTheMinValue() {
        return theMinValue;
    }

    /**
     * Setter for property theMinValue.
     * 
     * @param theMinValue
     *            New value of property theMinValue.
     *  
     */
    public void setTheMinValue(int theMinValue) {
        if (theMinValue >= theMaxValue)
            this.theMinValue = theMaxValue - 1;
        else
            this.theMinValue = theMinValue;
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
    public boolean setValue(int value) {
        if (value < theMinValue) {
            Underflow = true;
            Overflow = false;
            value = theMinValue;
        } else if (value > theMaxValue) {
            Underflow = false;
            Overflow = true;
            value = theMaxValue;
        } else {
            Underflow = false;
            Overflow = false;
        }
        this.value = value;
        return !(Underflow || Overflow);
    }

    /**
     * Getter for property Overflow.
     * 
     * @return Value of property Overflow.
     *  
     */
    public boolean isOverflow() {
        return Overflow;
    }

    /**
     * Getter for property Underflow.
     * 
     * @return Value of property Underflow.
     *  
     */
    public boolean isUnderflow() {
        return Underflow;
    }

}