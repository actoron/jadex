package com.daimler.client.gui.components;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.daimler.client.gui.components.parts.CurrentLineHighlighter;

public class TextTextField extends JScrollPane implements IInputComponent,
		DocumentListener, FocusListener
{

	private Object value;

	private int width = 400;

	private int height = 250;

	private JTextArea area;

	public TextTextField()
	{
		super();
		this.area = new JTextArea();
		this.area.setLineWrap(true);
		this.area.setWrapStyleWord(true);
		this.area.getDocument().addDocumentListener(this);
		getViewport().setView(area);
		this.area.addFocusListener(this);
		setPreferredSize(new Dimension(width, height));
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object v)
	{
		value = v;
		area.setText((String) value);
	}

	public JComponent getComponent()
	{
		return this;
	}

	public void changedUpdate(DocumentEvent arg0)
	{
	}

	public void insertUpdate(DocumentEvent e)
	{
		String sText = (String) value;
		try
		{
			sText = e.getDocument().getText(0, e.getDocument().getLength());
		} catch (BadLocationException err)
		{
		}
	}

	public void removeUpdate(DocumentEvent e)
	{
		String sText = (String) value;
		try
		{
			sText = e.getDocument().getText(0, e.getDocument().getLength());
		} catch (BadLocationException err)
		{
		}
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public void focusGained(FocusEvent e)
	{
		if (e.getComponent() instanceof JTextArea)
		{
			CurrentLineHighlighter.install((JTextArea) e.getComponent());
		}

	}

	public void focusLost(FocusEvent e)
	{
		if (e.getComponent() instanceof JTextArea)
		{
			CurrentLineHighlighter.uninstall((JTextArea) e.getComponent());
		}
	}

}
