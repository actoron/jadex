package com.daimler.client.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.text.Format;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class StringTextField extends JFormattedTextField implements
		IInputComponent, DocumentListener
{
	private Object value;

	private int width = 300;

	private int height = 20;

	private Format format;

	public StringTextField()
	{
		super();
		getDocument().addDocumentListener(this);
	}

	public StringTextField(Format format)
	{
		super(format);
		this.format = format;
		if (format == StringInputPanel.INTEGER_FORMAT
				|| format == StringInputPanel.DOUBLE_FORMAT)
		{
			setHorizontalAlignment(JTextField.TRAILING);
			width = 100;
			height = 20;
		} else if (format == StringInputPanel.DATE_FORMAT
				|| format == StringInputPanel.DATETIME_FORMAT
				|| format == StringInputPanel.TIME_FORMAT)
		{
			setHorizontalAlignment(JTextField.LEADING);
			width = 200;
			height = 20;
		} else
		{
			setHorizontalAlignment(JTextField.LEADING);
			width = 300;
			height = 20;
		}
		setPreferredSize(new Dimension(width, height));
		getDocument().addDocumentListener(this);
	}

	public Object getValue()
	{
		Object obj = super.getValue();
		return obj;
	}

	public void setValue(Object v)
	{
		if (v != null)
		{
			super.setValue(v);
			value = v;
		} else
		{
			setText("");
		}
	}

	public JComponent getComponent()
	{
		return this;
	}

	public void changedUpdate(DocumentEvent arg0)
	{
	}

	public void insertUpdate(DocumentEvent evt)
	{
		Object obj = super.getValue();
		if (getFormatter() == null && obj == null)
		{
			obj = getText();
		}
		try
		{
			value = format.parseObject((String) obj.toString());
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
	}

	public void removeUpdate(DocumentEvent evt)
	{
		Object obj = super.getValue();
		if (getFormatter() == null && obj == null)
		{
			obj = getText();
		}
		try
		{
			value = format.parseObject((String) obj.toString());
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
	}

	public int getTheHeight()
	{
		return height;
	}

	public int getTheWidth()
	{
		return width;
	}

}
