/*
 * scwnfChangedListener.java
 *
 * Created on November 13, 2002, 9:21 AM
 */

package com.daimler.util.swing.ScrollWholeNumberField;

import java.util.EventListener;

/**
 * 
 * @author krischan
 */
public interface SWNFChangedListener extends EventListener {
    public void scwnfValueChanged(SWNFEvent e);
}