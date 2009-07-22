package com.daimler.client.gui.components;

import java.awt.Dimension;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.daimler.client.gui.components.parts.RegexInputVerifier;

public class RegExprTextField extends JTextField implements IInputComponent
{
    private int width = 300;
    
    private int height = 20;
    
    public RegExprTextField(Pattern pattern)
    {
        super();
        RegexInputVerifier verifier = new RegexInputVerifier(pattern);
        setInputVerifier(verifier);
        setPreferredSize(new Dimension(width, height));
    }
    
    public Object getValue()
    {
        return getText();
    }

    public void setValue(Object v)
    {
        setText((String) v);
    }

    public JComponent getComponent()
    {
        return this;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }
    
}
