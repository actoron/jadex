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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class BooleanInputPanel extends AbstractInputPanel
{

	private String labelText;

	private String toolTip;

	private JPanel mainPanel;

	private JCheckBox checkBox;

	public BooleanInputPanel(String name, String labelText, String toolTip,
			String helpText, Color bgColor, boolean isRequired)
	{
		super(name, helpText, bgColor, isRequired);
		this.labelText = labelText;
		this.toolTip = toolTip;
		initComponents();
		super.init();
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBackground(getBackgroundColor());
		mainPanel.setLayout(new BorderLayout());
		// theTextField = new JTextField();
		checkBox = new JCheckBox(labelText);
		checkBox.setBackground(getBackgroundColor());
		if (toolTip != null && toolTip.trim().length() > 0)
		{
			checkBox.setToolTipText(toolTip);
		}
		mainPanel.add(SwingUtils.getPanelWithComponent(checkBox,
				getBackgroundColor(), Component.LEFT_ALIGNMENT,
				Component.CENTER_ALIGNMENT), BorderLayout.WEST);
		// theMainPanel = SwingUtils.getPanelWithComponentAndSize(theTextField,
		// getBackground(), new Dimension(theTextField.getTheWidth(),
		// theTextField.getTheHeight()), getTheElementBorder());
		/*
		 * if (getTheAccessible().getTheValue() != null) {
		 * setTheCurrentValue(getTheAccessible().getTheValue()); } else {
		 * setTheCurrentValue(new Boolean(false)); }
		 */
		// theCheckBox.addActionListener(this);
	}

	public String getLabel()
	{
		return labelText;
	}

	public int getWeight()
	{
		return 100;
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
			return true;
		}
		return false;
	}

	public void markError()
	{
		checkBox.setForeground(Color.RED);
	}

	public void markOK()
	{
		checkBox.setForeground(Color.BLACK);
	}

	public void setMainPanelEnabled(boolean b)
	{
		checkBox.setEnabled(b);
		mainPanel.setEnabled(b);

	}

	public int getFieldCategory()
	{
		return AbstractInputPanel.CAT_BOOLEAN;
	}

	public JPanel getLabelPanel()
	{
		return null;
	}

	public Object getCurrentValue()
	{
		return new Boolean(checkBox.isSelected());
	}

	public void setCurrentValue(Object currentValue)
	{
		checkBox.setSelected(((Boolean) currentValue).booleanValue());

	}

}
