// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class StringInputPanel extends AbstractInputPanel
{

	public static final NumberFormat DOUBLE_FORMAT = NumberFormat
			.getNumberInstance(Locale.GERMANY);

	public static final NumberFormat INTEGER_FORMAT = NumberFormat
			.getIntegerInstance(Locale.GERMANY);

	public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(
			DateFormat.MEDIUM, Locale.GERMANY);

	public static final DateFormat DATETIME_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
					Locale.GERMANY);

	public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(
			DateFormat.MEDIUM, Locale.GERMANY);

	private String labelText;

	private String toolTip;

	private JPanel mainPanel;

	private JPanel labelPanel;

	private StringTextField textField;

	private JLabel label;

	private Format format;

	public StringInputPanel(String name, String labelText, String toolTip,
			String helpText, Color bgColor, Object initialValue, boolean isRequired, Format format)
	{
		super(name, helpText, bgColor, initialValue, isRequired);
		this.labelText = labelText;
		this.toolTip = toolTip;
		this.format = format;
		initComponents();
		super.init();
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBackground(getBackgroundColor());
		mainPanel.setLayout(new BorderLayout());
		textField = new StringTextField(format);
		String sLabText = labelText;
		if (isInputRequired())
		{
			sLabText += "*";
		}
		label = new JLabel(sLabText);
		label.setBackground(getBackgroundColor());
		label.setLabelFor(textField);
		if (toolTip != null && toolTip.trim().length() > 0)
		{
			textField.setToolTipText(toolTip);
			label.setToolTipText(toolTip);
		}
		labelPanel = SwingUtils.getPanelWithComponent(label,
				getBackgroundColor(), Component.LEFT_ALIGNMENT,
				Component.CENTER_ALIGNMENT, getElementBorder());
		mainPanel.add(SwingUtils.getPanelWithComponentAndSize(textField,
				getBackgroundColor(), new Dimension(textField.getTheWidth(),
						textField.getTheHeight()), getElementBorder()),
				BorderLayout.WEST);
		// theMainPanel = SwingUtils.getPanelWithComponentAndSize(theTextField,
		// getBackground(), new Dimension(theTextField.getTheWidth(),
		// theTextField.getTheHeight()), getTheElementBorder());
	}

	/*
	 * public static Format getFormat(IContextVariableDomain dom) { if
	 * (dom.isInstanceOf(DomainFactory.DOM_DATE)) { return THE_DATE_FORMAT; } if
	 * (dom.isInstanceOf(DomainFactory.DOM_DATETIME)) { return
	 * THE_DATETIME_FORMAT; } if (dom.isInstanceOf(DomainFactory.DOM_TIME)) {
	 * return THE_TIME_FORMAT; } if (dom.isInstanceOf(DomainFactory.DOM_DOUBLE))
	 * { return THE_DOUBLE_FORMAT; } if
	 * (dom.isInstanceOf(DomainFactory.DOM_INTEGER)) { return
	 * THE_INTEGER_FORMAT; } if (dom.isInstanceOf(DomainFactory.DOM_LONG)) {
	 * return THE_INTEGER_FORMAT; } return null; }
	 */

	public String getTheLabel()
	{
		return labelText;
	}
	
	public void setEditable(boolean editable)
    {
    	textField.setEditable(editable);
    }
	
	public int getWeight()
	{
		return -100;
	}

	public Border getBorder()
	{
		return null;
	}

	JPanel getMainPanel()
	{
		return mainPanel;
	}

	public boolean isValueFilled()
	{
		if (getCurrentValue() != null)
		{
			if (getCurrentValue() instanceof String
					&& ((String) getCurrentValue()).trim().length() == 0)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public void markError()
	{
		label.setForeground(Color.RED);
	}

	public void markOK()
	{
		label.setForeground(Color.BLACK);
	}

	public void setMainPanelEnabled(boolean b)
	{
		textField.setEnabled(b);
		label.setEnabled(b);
		mainPanel.setEnabled(b);
	}

	public int getFieldCategory()
	{
		return AbstractInputPanel.CAT_TEXTFIELD;
	}

	public JPanel getLabelPanel()
	{
		return labelPanel;
	}

	public Object getCurrentValue()
	{
		return textField.getValue();
	}

	public void setCurrentValue(Object currentValue)
	{
		textField.setValue(currentValue);

	}

}
