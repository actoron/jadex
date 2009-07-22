package com.daimler.client.gui.components;

import java.awt.Color;

import javax.swing.JComponent;

public interface IInputComponent
{
    public void setValue(Object v);
    
    public Object getValue();
    
    public JComponent getComponent();
    
    public void setEnabled(boolean b);
    
    public boolean isEnabled();
    
    public void setBackground(Color c);
    
    public int getWidth();
    
    public int getHeight();
}
